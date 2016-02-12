/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.httpserverexample.proxyserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author osyniaev
 */
public class ProxyServer extends Thread {

    private static final int DEFAULT_PORT = 5555;

    public static void main(String[] args) {

        int port = DEFAULT_PORT;

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Proxy server started on port: "
                    + serverSocket.getLocalPort() + "\n");
        } catch (IOException e) {
            System.out.println("Port " + port + " is blocked.");
            System.exit(-1);
        }

        while (true) {
            try {
                int count = 1;
                Socket clientSocket5555 = serverSocket.accept();
                ClientSession session = new ClientSession(clientSocket5555);
                System.err.println(" * Client number " + count + "  accepted\n");
                new Thread(session).start();
                count++;

            } catch (IOException e) {
                System.out.println("Failed to establish connection.");
                System.out.println(e.getMessage());
                System.exit(-1);
            }
        }
    }
}
