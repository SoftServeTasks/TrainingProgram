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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;

class SampleServer extends Thread {

    private final Socket s;
    private final int num;
    private InputStream is;
    private OutputStream os;

    public static void main(String args[]) {
        try {
            int i = 0;
            ServerSocket server = new ServerSocket(3128, 0,
                    InetAddress.getByName("localhost"));

            System.out.println("server is started");

            while (true) {
                new SampleServer(i, server.accept());
                i++;
            }
        } catch (Exception e) {
            System.out.println("init error: " + e);
        }
    }

    public SampleServer(int num, Socket s) {
        this.num = num;
        this.s = s;

        setDaemon(true);
        setPriority(NORM_PRIORITY);
        start();
    }

    public void run() {
        try {
            getStreams();
            String clientRequest = readRequestFromClient();
            writeResponseToClient(clientRequest);
            System.out.println(clientRequest);
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        } catch (Throwable ex) {
            Logger.getLogger(SampleServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void getStreams() throws IOException {
        is = s.getInputStream();
        os = s.getOutputStream();
    }

    private String readRequestFromClient() throws IOException {
        byte buf[] = new byte[128 * 1024];
        int r = is.read(buf);
        String data = new String(buf, 0, r);
        data = "Current time is: " + getCurrentTime() + " \n Client " + num + " sent : " + "\n" + data;
        return data;
    }

    private void writeResponseToClient(String s) throws Throwable {

        String response = String.format("HTTP/1.1 200 OK\r\n"
                + "Server: YarServer/2009-09-09\r\n"
                + "Content-Type: text/html\r\n"
                + "Content-Length: %d \r\n"
                + "Connection: close\r\n\r\n"
                + "<html><body><h1> %s </h1>"
                + "<p>Current thread number is %d </p>",
                s.length(), getCurrentTime(), num);
        String result = response + s;
        os.write(result.getBytes());
        os.flush();
    }
    
    private String getCurrentTime(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(cal.getTime());
    }
}
