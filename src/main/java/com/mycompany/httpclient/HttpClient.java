/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.httpclient;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author osyniaev
 */
public class HttpClient implements Runnable {

    private int threadIdentificator;
    private int port;
    private final static int DAFAULT_PORT = 1002;
    private Socket socket;

    public HttpClient(int count) {
        this.threadIdentificator = count;
        this.port = DAFAULT_PORT;
    }

    public HttpClient(int threadIdentificator, int port) {
        this.threadIdentificator = threadIdentificator;
        this.port = port;
    }

    public Socket getSocket() {
        return socket;
    }

    @Override
    public void run() {
        try {
            socket = new Socket("localhost", port);
            System.out.println("Client number " + threadIdentificator + " started");
            sendMessageToServer();
            System.out.println("Message sent to server localhost:" + port + "!\n");
            
            Thread.sleep(2000);

            String messageFromServer = readMessageFromServer();

            System.out.println("///////////////////////////////////////////////////\n"
                    + "SERVER localhost:" + port + " RETURNED ANSWER: \n\n " + messageFromServer
                    + "\n ************************************************** \n");
        } catch (Exception e) {
            System.out.println("init error: " + e);
        }
    }

    /**
     * sends messages to server
     *
     * @throws IOException
     */
    public void sendMessageToServer() throws IOException {
        String toServer = "CURRENT TIME IS: 13:03:30\n"
                + "CLIENTS REQUEST: \n"
                + "GET http://portscan.ru/ HTTP/1.1\n"
                + "Host: portscan.ru\n"
                + "User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:44.0) Gecko/20100101 Firefox/44.0\n"
                + "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\n"
                + "Accept-Language: ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3\n"
                + "Accept-Encoding: gzip, deflate\n"
                + "Cookie: _ym_uid=1455535634338552701; PHPSESSID=b1ef2ba08fcbfaea0e159dd877ac417d\n"
                + "Connection: keep-alive\n"/* + threadIdentificator*/;
        socket.getOutputStream().write(toServer.getBytes());
    }

    /**
     * client read messages from server
     *
     * @return (String) server message
     * @throws IOException
     */
    public String readMessageFromServer() throws IOException {
        byte buf[] = new byte[64 * 1024];
        int r = socket.getInputStream().read(buf);
        String data = new String(buf, 0, r);
        return data;
    }
}
