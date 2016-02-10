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

class SimultaneousClients extends Thread {

    public static void main(String args[]) {
        ExecutorService service = Executors.newCachedThreadPool();
        Thread thread;
        for (int i = 0; i < 10; i++) {
            thread = new Thread(new SampleClient(i));
            service.submit(thread);
            thread.start();
        }
    }

    private static class SampleClient implements Runnable {

        private int counter;

        public SampleClient(int counter) {
            this.counter = counter;
        }

        @Override
        public void run() {
            try {
                Socket s = new Socket("localhost", 3128);

                System.out.println("Client number " + counter + " started");

                String toServer = String.format("From %d to server message: Hello \n "
                        + "****************************", counter);
                System.out.println(toServer);

                s.getOutputStream().write(toServer.getBytes());

                byte buf[] = new byte[64 * 1024];
                int r = s.getInputStream().read(buf);
                String data = new String(buf, 0, r);

                System.out.println(data);

            } catch (Exception e) {

                e.printStackTrace();
            }
        }

    }

}
