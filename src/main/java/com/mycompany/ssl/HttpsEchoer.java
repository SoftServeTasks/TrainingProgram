/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.ssl;

/**
 *
 * @author osyniaev
 */
import java.io.*;
import java.security.*;
import javax.net.ssl.*;

public class HttpsEchoer extends Thread {

    @Override
    public void run() {
        String ksName = "C:/Users/osyniaev/herong.jks";
        char ksPass[] = "password".toCharArray();
        char ctPass[] = "password".toCharArray();
        try {
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new FileInputStream(ksName), ksPass);
            KeyManagerFactory kmf
                    = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, ctPass);
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(kmf.getKeyManagers(), null, null);
            SSLServerSocketFactory ssf = sc.getServerSocketFactory();
            SSLServerSocket s
                    = (SSLServerSocket) ssf.createServerSocket(8888);
            System.out.println("Server started:");
            printServerSocketInfo(s);
            // Listening to the port
            int count = 0;
            while (true) {
                SSLSocket c = (SSLSocket) s.accept();
                // Someone is calling this server
                count++;
                System.out.println("Connection #: " + count);
                printSocketInfo(c);
                BufferedWriter w = new BufferedWriter(
                        new OutputStreamWriter(c.getOutputStream()));
                BufferedReader r = new BufferedReader(
                        new InputStreamReader(c.getInputStream()));
                String m = r.readLine();
                if (m != null) {
                    // We have a real data connection
                    w.write("HTTP/1.0 200 OK");
                    w.newLine();
                    w.write("Content-Type: text/html");
                    w.newLine();
                    w.newLine();
                    w.write("<html><body><pre>");
                    w.newLine();
                    w.write("Connection #: " + count);
                    w.newLine();
                    w.newLine();
                    w.write(m);
                    w.newLine();
                    while ((m = r.readLine()) != null) {
                        if (m.length() == 0) {
                            break; // End of a GET call
                        }
                        w.write(m);
                        w.newLine();
                    }
                    w.write("</pre></body></html>");
                    w.newLine();
                    w.flush();
                }
                w.close();
                r.close();
                c.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printSocketInfo(SSLSocket s) {
        System.out.println("Server socket class: " + s.getClass());
        System.out.println("   Remote address = "
                + s.getInetAddress().toString());
        System.out.println("   Remote port = "
                + s.getPort());
        System.out.println("   Local socket address = "
                + s.getLocalSocketAddress().toString());
        System.out.println("   Local address = "
                + s.getLocalAddress().toString());
        System.out.println("   Local port = "
                + s.getLocalPort());
    }

    private static void printServerSocketInfo(SSLServerSocket s) {
        System.out.println("Server socket class: " + s.getClass());
        System.out.println("   Socket address = "
                + s.getInetAddress().toString());
        System.out.println("   Socket port = "
                + s.getLocalPort());
        System.out.println("   Need client authentication = "
                + s.getNeedClientAuth());
        System.out.println("   Want client authentication = "
                + s.getWantClientAuth());
        System.out.println("   Use client mode = "
                + s.getUseClientMode());
    }
}
