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
 * It's a sample of elementary forward proxy server
 * It expects a socket connection, then it creates a new socket  
 * to communicate with each individual client in separate thread
 * @author osyniaev
 */
public class ProxyServer extends Thread {

    private static final int DEFAULT_PORT = 1002;
    private static ServerSocket httpListener = null;
    private static  ExecutorService executorPool = Executors.newCachedThreadPool();
    private HttpRequestHandler session;
    private static final Logger LOGGER = Logger.getLogger(ProxyServer.class);

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
    
    @Override
    public void run() {
        startServer();
        listenPort();
    }
    

   /* public static void main(String[] args) {

        startServer();
        listenPort();

    }*/
    
    /**
     * Starting the server
     */
    public /*static*/  void startServer() {
        int port = DEFAULT_PORT;
 
        try {
            httpListener = new ServerSocket(port);
            LOGGER.info("Proxy server started on port: "
                    + httpListener.getLocalPort() + "\n");
        } catch (IOException e) {
            LOGGER.error("Port " + port + " is blocked.",e);
            System.exit(-1);
        }
    }

    /**
     * The server listens on port and waits for clients to connect.
     * when client is accepted, server creates a request handler instance in a separate thread
     * For each connected client creates a separate handler in a separate thread.
     */
    
    public /*static*/ void listenPort() {
        while (true) {
            try {
                int count = 1;
                Socket clientSocket = httpListener.accept();
                session = new HttpRequestHandler(clientSocket, count);
                LOGGER.info(" + PROXY: * Client number " + count + "  accepted\n");
                executorPool.execute(session);
                count++;

            } catch (IOException e) {
                LOGGER.error("Failed to establish connection.",e);
                System.exit(-1);
            }
        }
    }
}

