/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.forwardproxyserver;

import com.mycompany.forwardproxyserver.ntlm.AuthNtlnType3Handler;
import com.mycompany.forwardproxyserver.ntlm.NtlmManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
    }

    /**
     * Connects to the resource from the header "host", 
     * sends clients request, 
     * receives response from resource and returns it to client
     * @param header
     * @param host
     * @param port
     * @throws Exception
     */
    protected void dawnloadFromInet(String header, String host, int port) throws Exception {
        System.err.println("Подключение к " + host + ":" + port);
        sc = new Socket(host, port);
        sc.getOutputStream().write(header.getBytes());

        InputStream is = sc.getInputStream();

        byte buf[] = new byte[512 * 1024];
        int r = 1;
        while (r > 0) {
            r = is.read(buf);
            if (r > 0) {
                System.err.println(new String(buf, 0, r));
                toClientChannel.write(buf, 0, r);
            }
        }

        sc.close();
    }

    /**
     * reads message from client
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

    public void run() {
        try {
            String clientsRequest;
            clientsRequest=readClientsRequest(fromClientChannel);
            System.out.println("+ PROXY: CURRENT TIME IS: " + getCurrentTime() + "\nCLIENTS " + clientsNumber + " REQUEST: \n" + clientsRequest);
            ntlmManager.return407();
            clientsRequest = readClientsRequest(fromClientChannel);
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
            if(type3Handler.checkUserData()) {
                requestParser.setRequest(clientsRequest);
                dawnloadFromInet(clientsRequest, requestParser.getHost(), requestParser.getPort());
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
