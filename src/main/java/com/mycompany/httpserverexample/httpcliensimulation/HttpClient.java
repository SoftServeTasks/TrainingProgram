/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.httpserverexample.httpcliensimulation;

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
    private final static int DAFAULT_PORT = 5555;

    public HttpClient(int count) {
        this.threadIdentificator = count;
        this.port = DAFAULT_PORT;
    }

    public HttpClient(int threadIdentificator, int port) {
        this.threadIdentificator = threadIdentificator;
        this.port = port;
    }
    

    @Override
    public void run() {
        try {

            Socket s = new Socket("localhost", port);
            System.out.println("Client number " + threadIdentificator + " started");
            String toServer = "Hello from client " + threadIdentificator;
            s.getOutputStream().write(toServer.getBytes());
            System.out.println("Message: " + toServer + " sent to server localhost:"+ port + "!\n");

            byte buf[] = new byte[64 * 1024];
            int r = s.getInputStream().read(buf);
            String data = new String(buf, 0, r);

            System.out.println("///////////////////////////////////////////////////\n"
                    + "SERVER localhost:"+ port + " RETURNED ANSWER: \n\n " + data +
                    "\n ************************************************** \n");
        } catch (Exception e) {
            System.out.println("init error: " + e);
        }
    }
}
