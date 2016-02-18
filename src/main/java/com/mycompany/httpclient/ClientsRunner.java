/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.httpclient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author osyniaev
 */
public class ClientsRunner {
    
    public static void main(String[] args) throws InterruptedException {
        
        ExecutorService service = Executors.newCachedThreadPool();
         Thread thread;
           // for (int i = 0; i < 10; i++) {
                thread = new Thread(new HttpClient(1));
                service.submit(thread);
                thread.start();
          //  }
    }
           

    
}
