/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.httpclient;

import com.mycompany.forwardproxyserver.ProxyServer;
import com.mycompany.forwardproxyserver.telemetry.ServerResponseTimeAnalizer;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;
import com.mycompany.httpclient.HttpClient;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
            //new SunHttpClient().start();
            new com.mycompany.httpclient.HttpClient(i,latch).start();
        }
        ServerResponseTimeAnalizer analyzer = ServerResponseTimeAnalizer.ANALIZER;
        Thread.sleep(15000);
        analyzer.growStatisticsGrafic();
        analyzer.getResponseTimeStatistic();
        System.err.println("Среднее время ответа сервера " + analyzer.getAvgResponseTime());
        
        
        analyzer.growStatisticsGrafic();
        analyzer.getResponseTimeStatistic();
        /*ExecutorService service = Executors.newCachedThreadPool();
        Thread thread;
        for (int i = 0; i < 10; i++) {
        thread = new Thread(new HttpClient(1));
        service.submit(thread);
        thread.start();
        }*/
    }
}
