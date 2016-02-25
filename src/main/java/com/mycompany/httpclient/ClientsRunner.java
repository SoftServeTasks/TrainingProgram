/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.httpclient;

import com.mycompany.forwardproxyserver.ProxyServer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;

/**
 *
 * @author osyniaev
 */
public class ClientsRunner {
    
    public static void main(String[] args) throws InterruptedException {
        
        ProxyServer proxy = new ProxyServer();
        proxy.start();
        Thread.sleep(3000);
        
        CountDownLatch latch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            new HttpClient(i,latch).start();
        }
  
        
       /*ExecutorService service = Executors.newCachedThreadPool();
         Thread thread;
           for (int i = 0; i < 10; i++) {
                thread = new Thread(new HttpClient(1));
                service.submit(thread);
                thread.start();
            }*/
    }
}
