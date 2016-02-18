/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.forwardproxyserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;


/**
 *
 * @author osyniaev
 */
public class ProxyServer extends Thread {

    private static final int DEFAULT_PORT = 1002;
    private static ServerSocket httpListener = null;
    private static  ExecutorService executorPool = Executors.newCachedThreadPool();;

    public ProxyServer(ExecutorService executorPool) {
        this.executorPool = executorPool;
    }
    
    public ProxyServer() {
        //this.executorPool = Executors.newCachedThreadPool();
    }

    public static ServerSocket getHttpListener() {
        return httpListener;
    }

    public static void setHttpListener(ServerSocket httpListener) {
        ProxyServer.httpListener = httpListener;
    }
    
    
    

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
            httpListener = new ServerSocket(port);
            System.out.println("Proxy server started on port: "
                    + httpListener.getLocalPort() + "\n");
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
                Socket clientSocket = httpListener.accept();
                HttpRequestHandler session = new HttpRequestHandler(clientSocket, count);
                System.err.println(" * Client number " + count + "  accepted\n");
                executorPool.execute(session);
                count++;

            } catch (IOException e) {
                System.out.println("Failed to establish connection.");
                System.out.println(e.getMessage());
                System.exit(-1);
            }
        }
    }
}

