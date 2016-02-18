/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.forwardproxyserver;

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

    public HttpRequestHandler(Socket socket, int count) throws IOException {
        this.connectionWithClient = socket;
        this.clientsNumber = count;
        initialize();
    }

    protected void setConnectionWithClient(Socket connectionWithClient) {
        this.connectionWithClient = connectionWithClient;
    }

    
    /**
     * Получаем поток ввода, в который помещаются сообщения от клиента 
     * Получаем поток вывода, для отправки сообщений клиенту 
     * @throws IOException 
     */
    private void initialize() throws IOException {
       
        fromClientChannel = connectionWithClient.getInputStream();
       
        toClientChannel = connectionWithClient.getOutputStream();
        responseHandler = new ResponseHandler(fromClientChannel, toClientChannel);
        requestParser = HttpRequestParser.INSTANCE;
    }
    
    

    /**
     * 
     * @param header
     * @param host
     * @param port
     * @throws Exception 
     */
    protected void dawnloadFromInet(String header, String host, int port) throws Exception {
        System.err.println("Подключение к " + host + ":" + port);
        sc = new Socket(host, port);
        System.err.println("header: " + header);
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
            System.out.println("CURRENT TIME IS: " + getCurrentTime() + "\nCLIENTS " + clientsNumber + " REQUEST: \n" + header);
        }
        return header;
    }


    public void run() {
        try {

            String clientsRequest = readClientsRequest(fromClientChannel);
            requestParser.setRequest(clientsRequest);
            System.out.println("Clients request " + clientsNumber + " is: " + clientsRequest);
            dawnloadFromInet(clientsRequest, requestParser.getHost(), requestParser.getPort());
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
