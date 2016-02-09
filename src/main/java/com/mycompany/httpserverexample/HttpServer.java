/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.httpserverexample;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by yar 09.09.2009
 */
public class HttpServer {

    public static void main(String[] args) throws Throwable {
        ServerSocket ss = new ServerSocket(7777);
        System.out.println("server is started");
        while (true) {
            Socket s = ss.accept();
            System.err.println("Client accepted");
            new Thread(new SocketProcessor(s)).start();
            System.err.println("SocketProcessor is created");
        }
    }

    private static class SocketProcessor implements Runnable {

        private Socket s;
        private InputStream is;
        private OutputStream os;
        private static int count = 0;

        private SocketProcessor(Socket s) throws Throwable {
            this.s = s;
            this.is = s.getInputStream();
            this.os = s.getOutputStream();
            count++;
            System.err.println("1");
        }

        public void run() {
            try {
                String readInputHeaders = readInputHeaders();
                Calendar cal = Calendar.getInstance();
                System.err.println("2");
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                writeResponse("<html><body><h1>" + sdf.format(cal.getTime()) + "</h1>"
                        + "<p>Current thread number is " + count + "</p>"
                        + "<p>Current thread name is " + readInputHeaders + "</p></body></html>");
                System.err.println(readInputHeaders);
            } catch (Throwable t) {
                /*do nothing*/
            } finally {
                try {
                    s.close();
                } catch (Throwable t) {
                    /*do nothing*/
                }
            }
            System.err.println("Client processing finished");
        }

        private void writeResponse(String s) throws Throwable {
            System.err.println("4");
            String response = "HTTP/1.1 200 OK\r\n"
                    + "Server: YarServer/2009-09-09\r\n"
                    + "Content-Type: text/html\r\n"
                    + "Content-Length: " + s.length() + "\r\n"
                    + "Connection: close\r\n\r\n";
            String result = response + s;
            System.err.println("5");
            os.write(result.getBytes());
            System.err.println("6");
            os.flush();
        }

        private String readInputHeaders() throws Throwable {
            System.err.println("7");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            System.err.println("8");
            String result = null;
            while (true) {
                String s = br.readLine();
                System.out.println(s);
                result += s;
                if (s == null || s.trim().length() == 0) {
                    break;
                }
            }
            System.err.println(result);
            return result;
        }
    }
}
