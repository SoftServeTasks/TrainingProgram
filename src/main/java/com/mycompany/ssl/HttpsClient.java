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
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;

public class HttpsClient extends Thread {

    public static final TrustManager[] trustManager = new TrustManager[]{
        new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                for (X509Certificate cert : certs) {
                    System.out.println("============================================\n");
                    System.out.println(cert);
                }
            }
        }
    };

    @Override
    public void run() {
        try {
            PrintStream out = System.out;
            SSLContext sc = SSLContext.getInstance("TLSv1.2");
            sc.init(null, trustManager, new java.security.SecureRandom());
            // Getting the default SSL socket factory
            SSLSocketFactory f
                    = (SSLSocketFactory) sc.getSocketFactory();
            out.println("The default SSL socket factory class: "
                    + f.getClass());

            // Getting the default SSL socket factory
            SSLSocket c
                    = (SSLSocket) f.createSocket("localhost", 8888);
            printSocketInfo(c);
            c.startHandshake();
            BufferedWriter w = new BufferedWriter(new OutputStreamWriter(
                    c.getOutputStream()));
            BufferedReader r = new BufferedReader(new InputStreamReader(
                    c.getInputStream()));
            w.write("GET / HTTP/1.0");
            w.newLine();
            w.newLine(); // end of HTTP request
            w.flush();
            String m = null;
            while ((m = r.readLine()) != null) {
                out.println(m);
            }
            w.close();
            r.close();
            c.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e1) {

        } catch (KeyManagementException e2) {

        }
    }

    private static void printSocketInfo(SSLSocket s) {
        System.out.println("Socket class: " + s.getClass());
        System.out.println("   Remote address = "
                + s.getInetAddress().toString());
        System.out.println("   Remote port = " + s.getPort());
        System.out.println("   Local socket address = "
                + s.getLocalSocketAddress().toString());
        System.out.println("   Local address = "
                + s.getLocalAddress().toString());
        System.out.println("   Local port = " + s.getLocalPort());
        System.out.println("   Need client authentication = "
                + s.getNeedClientAuth());
        SSLSession ss = s.getSession();
        System.out.println("   Cipher suite = " + ss.getCipherSuite());
        System.out.println("   Protocol = " + ss.getProtocol());
    }
}
