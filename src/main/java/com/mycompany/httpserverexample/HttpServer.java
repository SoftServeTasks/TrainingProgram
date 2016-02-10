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
    private static int count = 1;

    public static void main(String[] args) throws Throwable {
        ServerSocket ss = new ServerSocket(7777);
        System.out.println("server is started");
        while (true) {
            Socket s = ss.accept();
            System.err.println("Client number " + count + "  accepted");
            new Thread(new SocketProcessor(s,count)).start();
            System.err.println("SocketProcessor number " + count + " is created");
            count++;
        }
    }

    private static class SocketProcessor implements Runnable {

        private Socket s;
        private InputStream is;
        private OutputStream os;
        private int number;


        private SocketProcessor(Socket s, int count) throws Throwable {
            this.s = s;
            this.is = s.getInputStream();
            this.os = s.getOutputStream();
            this.number = count;
            
        }

        public void run() {
            try {
                String readInputHeaders = readInputHeaders();
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                writeResponse("<html><body><h1>" + sdf.format(cal.getTime()) + "</h1>"
                        + "<p>Current thread number is " + number + "</p>"
                        + "<p>Current thread name is " + readInputHeaders + "</p></body></html>");
                System.out.println("Message from client " + number + " : " + readInputHeaders);
            } catch (Throwable t) {
                /*do nothing*/
            } finally {
                try {
                    s.close();
                } catch (Throwable t) {
                    /*do nothing*/
                }
            }
            System.err.println("Client number" + number + " processing finished");
        }

        private void writeResponse(String s) throws Throwable {

            String response = "HTTP/1.1 200 OK\r\n"
                    + "Server: YarServer/2009-09-09\r\n"
                    + "Content-Type: text/html\r\n"
                    + "Content-Length: " + s.length() + "\r\n"
                    + "Connection: close\r\n\r\n";
            String result = response + s;
            os.write(result.getBytes());
            os.flush();
        }

        private String readInputHeaders() throws Throwable {

            byte buf[] = new byte[128*1024];
            int r = is.read(buf);
            String data = new String(buf, 0, r);
            return data;
        }

           /* BufferedReader br = new BufferedReader(new InputStreamReader(is));

            String result = null;
            while (true) {
                String s = br.readLine();
                result += s;
                if (s == null || s.trim().length() == 0) {
                    break;
                }
            }
            return result;
        }*/
    }
}
