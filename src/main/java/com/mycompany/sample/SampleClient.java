/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.sample;

/**
 *
 * @author osyniaev
 */
import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class SampleClient extends Thread {

    private static int counter = 1;

    public static void main(String args[]) {
        ExecutorService service = Executors.newCachedThreadPool();
        for (; counter < 10; ) {
            service.submit(new Runnable() {
                public void run() {

                    try {
                        Socket s = new Socket("localhost", 3128);
                        
                        String toServer =  String.format("From %d to server message: Hello \n "
                                + "****************************", counter);

                        s.getOutputStream().write(toServer.getBytes());

                        byte buf[] = new byte[64 * 1024];
                        int r = s.getInputStream().read(buf);
                        String data = new String(buf, 0, r);

                        System.out.println(data);
                        
                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                }
            });
            counter++;
        }
    }

}
