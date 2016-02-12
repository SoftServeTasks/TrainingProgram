/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.forwardproxyserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.log4j.Logger;


/**
 *
 * @author osyniaev
 */
public class ProxyServer extends Thread {

    private static final int DEFAULT_PORT = 1002;
    private static ServerSocket serverSocket = null;

    public static void main(String[] args) {

        startServer();
        listenPort();

    }
    
    /**
     * Starting the server
     */
    public static  void startServer() {
        int port = DEFAULT_PORT;
 
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Proxy server started on port: "
                    + serverSocket.getLocalPort() + "\n");
        } catch (IOException e) {
            System.out.println("Port " + port + " is blocked.");
            System.exit(-1);
        }
    }

    /**
     * The server listens on port and waits for clients to connect.
     * For each connected client creates a separate handler in a separate thread.
     */
    
    public static void listenPort() {
        while (true) {
            try {
                int count = 1;
                Socket clientSocket = serverSocket.accept();
                HttpRequestHandler session = new HttpRequestHandler(clientSocket);
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

