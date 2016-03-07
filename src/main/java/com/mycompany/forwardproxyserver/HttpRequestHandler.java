/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.forwardproxyserver;

import com.mycompany.forwardproxyserver.ntlm.AuthNtlmType3Handler;
import com.mycompany.forwardproxyserver.ntlm.NtlmManager;
import com.mycompany.forwardproxyserver.telemetry.ClientsBrowserAnalyzer;
import com.mycompany.forwardproxyserver.telemetry.ServerResponseTimeAnalizer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.log4j.Logger;

/**
 *
 * @author osyniaev
 */
public class HttpRequestHandler implements Runnable {

    private int clientsNumber;
    private Socket connectionWithClient;
    private InputStream fromClientChannel = null;
    private OutputStream toClientChannel = null;
    private ResponseHandler responseHandler;
    private HttpRequestParser requestParser;
    private Socket sc;
    private NtlmManager ntlmManager;
    private AuthNtlmType3Handler type3Handler;
    private ClientsBrowserAnalyzer browserAnalyzer;
    private static volatile int count = 0;
    private long timer;
    private ServerResponseTimeAnalizer serverResponseTimeAnalizer;
    private long start;
    private long finish;
    private static final Logger LOGGER = Logger.getLogger(HttpRequestHandler.class);

    public HttpRequestHandler(Socket socket, int count) throws IOException {
        this.connectionWithClient = socket;
        this.clientsNumber = count;
        initialize();
    }

    protected void setConnectionWithClient(Socket connectionWithClient) {
        this.connectionWithClient = connectionWithClient;
    }

    public static int getCount() {
        return count;
    }

    public static void setCount(int count) {
        HttpRequestHandler.count = count;
    }

    public long getTimer() {
        return timer;
    }

    public void setTimer(long timer) {
        this.timer = timer;
    }

    /**
     *
     * Get the input stream, which brings messages from the client Get the
     * output stream for sending messages to the client
     *
     * @throws IOException
     */
    private void initialize() throws IOException {
        count++;
        fromClientChannel = connectionWithClient.getInputStream();
        toClientChannel = connectionWithClient.getOutputStream();
        responseHandler = new ResponseHandler(fromClientChannel, toClientChannel);
        requestParser = HttpRequestParser.INSTANCE;
        ntlmManager = new NtlmManager(responseHandler);
        browserAnalyzer = ClientsBrowserAnalyzer.INSTANSE;
        serverResponseTimeAnalizer = ServerResponseTimeAnalizer.ANALIZER;
        
    }

    /**
     * Connects to the resource from the header "host", sends clients request,
     * receives response from resource and returns it to client
     *
     * @param header
     * @param host
     * @param port
     * @throws Exception
     */
    protected void dawnloadFromInet(String header, String host, int port) throws Exception {
        LOGGER.info(" * PROXY: Подключение к " + host + ":" + port);
        sc = new Socket(host, port);
        InputStream fromSite;
        try (OutputStream toSite = sc.getOutputStream()) {
            toSite.write(header.getBytes());
            toSite.flush();
            LOGGER.debug(" * PROXY: Header " + header + "\nOтправлен " + host + ":" + port + "\n");
            fromSite = sc.getInputStream();
            LOGGER.info(" * PROXY: Читаю ответ от portscan.ru:80");
            //        String readClientsRequest = readClientsRequest(is);
            //System.err.println(" * PROXY: Ответ от portscan.ru:80 :" + readClientsRequest);
            byte buf[] = new byte[64 * 1024];
            int r = 1;
            while (r > 0) {
                r = fromSite.read(buf);
                //System.err.println(new String(buf, 0, r));
                if ((r > 0)) {
                    toClientChannel.write(buf, 0, r);
                }
                finish = System.currentTimeMillis();
                serverResponseTimeAnalizer.addcurrentResponseTime(finish - start, count);
            }
            //responseHandler.printAnyMessage(readClientsRequest);
        }
        fromSite.close();
        sc.close();
    }

    /**
     * reads message from client
     *
     * @param fromClientChannel
     * @return
     * @throws IOException
     */
    protected String readClientsRequest(InputStream fromClientChannel) throws IOException {
        String header = null;
        byte buf[] = new byte[1024 * 1024];
        int r = fromClientChannel.read(buf);
        if (r >= 0) {
            header = new String(buf, 0, r);
        }
        return header;
    }

    @Override
    public void run() {
        try {
            String clientsRequest = null;
            clientsRequest = readClientsRequest(fromClientChannel);
            start = System.currentTimeMillis();
            requestParser.setRequest(clientsRequest);
            browserAnalyzer.setParser(requestParser);
            browserAnalyzer.analyzeBrowserType();
            LOGGER.debug("\n+ PROXY: CURRENT TIME IS: " + getCurrentTime() + "\nCLIENTS " + clientsNumber + " REQUEST: \n" + clientsRequest);
            String proxyAuthenticatHeaderValue = null;
            while (proxyAuthenticatHeaderValue == null) {
                LOGGER.info("\n* PROXY: Необходима NTLM аутентификация, возвращаю ошибку 407 клиенту!\n");
                finish = System.currentTimeMillis();
                ntlmManager.return407();
                serverResponseTimeAnalizer.addcurrentResponseTime(finish - start, count);
                clientsRequest = readClientsRequest(fromClientChannel);
                start = System.currentTimeMillis();
                requestParser.setRequest(clientsRequest);
                proxyAuthenticatHeaderValue = requestParser.extract(clientsRequest, "Proxy-Authorization: NTLM ", "\n");
            }

            LOGGER.debug("\n+ PROXY: Client sent after 407: " + clientsRequest);
            String testGetType1 = ntlmManager.testGetType1(clientsRequest);
            LOGGER.debug("\n+ PROXY: type1: " + testGetType1);
            ntlmManager.resolveNegotiate(clientsRequest);
            ntlmManager.sendChallenge();
            finish = System.currentTimeMillis();
            serverResponseTimeAnalizer.addcurrentResponseTime(finish - start, count);
            LOGGER.info("\n+ PROXY: Chellenge was sent to client");
            clientsRequest = readClientsRequest(fromClientChannel);
            start = System.currentTimeMillis();
            LOGGER.debug("\n+ PROXY: Clients Response (with Type3): " + clientsRequest);
            type3Handler = new AuthNtlmType3Handler(clientsRequest);
            String headerValue = type3Handler.getProxyAuthorizationHeaderValue();
            if (type3Handler.checkUserData()) {
                requestParser.setRequest(clientsRequest);
                String cleanClientsRequest = requestParser.cleanClientsRequest();
                dawnloadFromInet(cleanClientsRequest, requestParser.getHost(), requestParser.getPort());
            } else {
                LOGGER.error("Unrecognized client");
                responseHandler.print401Error();
                finish = System.currentTimeMillis();
                serverResponseTimeAnalizer.addcurrentResponseTime(finish - start, count);
            }

        } catch (Exception e) {
            try {
                LOGGER.error("Error: ", e);
                responseHandler.printError("exception:\n" + e);
            } catch (Exception ex) {
            }
        } finally {
            browserAnalyzer.getInfoAboutBrpowsers();
            count--;
            try {
                connectionWithClient.shutdownInput();
                connectionWithClient.shutdownOutput();
                connectionWithClient.close();
                serverResponseTimeAnalizer.getResponseTimeStatistic();
            } catch (IOException ex) {
                LOGGER.error("ERROR: ", ex);
            }
        }
    }

    /**
     *
     * @return current time
     */
    private String getCurrentTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(cal.getTime());
    }
}
