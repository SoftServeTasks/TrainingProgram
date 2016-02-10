/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.httpserverexample;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author osyniaev
 */
public class HttpClient extends Thread {

    static private int threadIdentificator = 1;
    
   
    public static void main(String[] args) {

        ExecutorService service = Executors.newCachedThreadPool();
        for (; threadIdentificator < 11; threadIdentificator++) {
            service.submit(new Runnable() {
                public void run() {

                    try {

                        Socket s = new Socket("localhost", 7777);
                        System.out.println("Client number " + threadIdentificator + " started");
                        String toServer = "Hello from client " + threadIdentificator;
                        s.getOutputStream().write(toServer.getBytes());

                        byte buf[] = new byte[64 * 1024];
                        int r = s.getInputStream().read(buf);
                        String data = new String(buf, 0, r);

                        System.out.println("Server sent: " + data);
                    } catch (Exception e) {
                        System.out.println("init error: " + e);
                    }
                }
            });
        }
    }
}
