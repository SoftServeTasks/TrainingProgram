/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.httpserverexample.firsthttpserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 *
 * @author osyniaev
 */
public class HttpServer extends Thread {

    private static int count = 1;

    public static void main(String[] args) throws Throwable {
        ServerSocket ss = new ServerSocket(7777);
        System.out.println("HTTP Server is started on port 7777");

        while (true) {
            System.out.println("Waiting for the clients\n"
                    + "*********************************\n");
            Socket s = ss.accept();

            System.err.println(" * Client number " + count + "  accepted\n");
            new Thread(new SocketProcessor(s, count)).start();
            System.err.println("         SocketProcessor number " + count + " is created\n");
            count++;
        }
    }

    private static class SocketProcessor implements Runnable {

        private final Socket s;
        private final InputStream is;
        private final OutputStream os;
        private final int number;

        private SocketProcessor(Socket s, int count) throws Throwable {
            this.s = s;
            this.is = s.getInputStream();
            this.os = s.getOutputStream();
            this.number = count;

        }

        public void run() {
            try {
                String readInputHeaders = readInputHeaders();
               
                writeResponse("<html>\n<body>\n<h1>" + getCurrentTime() + "</h1>\n"
                        + "<p>Current thread number is " + number + "</p>\n"
                        + "<p>Clients message was: " + readInputHeaders + "</p>\n</body>\n</html>");
                System.out.println("Current time is " + getCurrentTime() +
                        "\nMessage from client " + number + " :\n " + readInputHeaders
                        + "\n +++++++++++++++++++++++++++++++++++++++++++++++++++++\n");
            } catch (Throwable t) {
                /*do nothing*/
            } finally {
                try {
                    s.close();
                } catch (Throwable t) {
                    /*do nothing*/
                }
            }
            System.err.println(" Client number" + number + " processing finished");
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

            byte buf[] = new byte[128 * 1024];
            int r = is.read(buf);
            String data = new String(buf, 0, r);
            return data;
        }

        private String getCurrentTime() {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            return sdf.format(cal.getTime());
        }

    }
}
