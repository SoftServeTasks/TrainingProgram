/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.forwardproxyserver;

import com.mycompany.forwardproxyserver.ntlm.AuthNtlnType3Handler;
import com.mycompany.forwardproxyserver.ntlm.NtlmManager;
import com.mycompany.forwardproxyserver.telemetry.ClientsBrowserAnalyzer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import jcifs.util.Base64;

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
    private AuthNtlnType3Handler type3Handler;
    private ClientsBrowserAnalyzer browserAnalyzer;

    public HttpRequestHandler(Socket socket, int count) throws IOException {
        this.connectionWithClient = socket;
        this.clientsNumber = count;
        initialize();
    }

    protected void setConnectionWithClient(Socket connectionWithClient) {
        this.connectionWithClient = connectionWithClient;
    }

    /**
     *
     * Get the input stream, which brings messages from the client Get the
     * output stream for sending messages to the client
     *
     * @throws IOException
     */
    private void initialize() throws IOException {
        fromClientChannel = connectionWithClient.getInputStream();
        toClientChannel = connectionWithClient.getOutputStream();
        responseHandler = new ResponseHandler(fromClientChannel, toClientChannel);
        requestParser = HttpRequestParser.INSTANCE;
        ntlmManager = new NtlmManager(responseHandler);
        browserAnalyzer = ClientsBrowserAnalyzer.INSTANSE;
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
        System.err.println(" * PROXY: Подключение к " + host + ":" + port);
        sc = new Socket(host, port);
        sc.getOutputStream().write(header.getBytes());
        System.err.println(" * PROXY: Header " + header + "Oтправлен " + host + ":" + port + "\n");
        Thread.sleep(2000);
        InputStream is = sc.getInputStream();

        System.err.println(" * PROXY: Читаю ответ от portscan.ru:80");

        Thread.sleep(3000);

//        String readClientsRequest = readClientsRequest(is);
        //System.err.println(" * PROXY: Ответ от portscan.ru:80 :" + readClientsRequest);
        byte buf[] = new byte[64 * 1024];
        int r = 1;
        while (r > 0) {
            r = is.read(buf);
            if (r > 0) {
                System.err.println(new String(buf, 0, r));
                if (r > 0) {
                    toClientChannel.write(buf, 0, r);
                }
            }
        }

        //responseHandler.printAnyMessage(readClientsRequest);
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
            requestParser.setRequest(clientsRequest);
            browserAnalyzer.setParser(requestParser);
            browserAnalyzer.analyzeBrowserType();
            System.out.println("+ PROXY: CURRENT TIME IS: " + getCurrentTime() + "\nCLIENTS " + clientsNumber + " REQUEST: \n" + clientsRequest);
            String proxyAuthenticatHeaderValue = null;
            while (proxyAuthenticatHeaderValue == null) {
                System.err.println("* PROXY: Необходима NTLM аутентификация, возвращаю ошибку 407 клиенту!\n");
                ntlmManager.return407();
                clientsRequest = readClientsRequest(fromClientChannel);
                requestParser.setRequest(clientsRequest);
                proxyAuthenticatHeaderValue = requestParser.extract(clientsRequest, "Proxy-Authorization: NTLM ", "\n");
            }

            System.err.println("+ PROXY: Client sent after 407: " + clientsRequest);
            String testGetType1 = ntlmManager.testGetType1(clientsRequest);
            System.err.println("+ PROXY: type1: " + testGetType1);
            ntlmManager.resolveNegotiate(clientsRequest);
            ntlmManager.sendChallenge();
            System.err.println("+ PROXY: Chellenge was sent to client");
            Thread.sleep(2000);
            clientsRequest = readClientsRequest(fromClientChannel);
            System.err.println("Clients Response (with Type3): " + clientsRequest);
            type3Handler = new AuthNtlnType3Handler(clientsRequest);
            String headerValue = type3Handler.getProxyAuthorizationHeaderValue();
            if (type3Handler.checkUserData()) {
                requestParser.setRequest(clientsRequest);
                String cleanClientsRequest = requestParser.cleanClientsRequest();
                dawnloadFromInet(cleanClientsRequest, requestParser.getHost(), requestParser.getPort());
            } else {
                System.err.println("Unrecognized client");
                responseHandler.print401Error();
            }

        } catch (Exception e) {
            try {
                e.printStackTrace();
                responseHandler.printError("exception:\n" + e);
            } catch (Exception ex) {
            }
        } finally {
            browserAnalyzer.getInfoAboutBrpowsers();
            try {
                connectionWithClient.close();
            } catch (IOException ex) {
                // do nothing
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
