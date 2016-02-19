/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.forwardproxyserver.integration;

import com.mycompany.forwardproxyserver.HttpRequestHandler;
import com.mycompany.forwardproxyserver.ProxyServer;
import com.mycompany.httpclient.HttpClient;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 *
 * @author osyniaev
 */
@Category(IntegrationTest.class)
public class IT_RequestHandlerTester {

    private static ProxyServer server;
    private static HttpRequestHandler requestHandler;
    private HttpClient client;
    private HttpClient spy;
    private Thread clientsThread;

    @BeforeClass
    public static void setUpClass() {
        server = new ProxyServer();
        server.start();

        System.err.println("Client is created");
    }

    @Test
    public void IntegrationTest() {
        client = new HttpClient(7);
        spy = spy(client);
        clientsThread = new Thread(spy);
        System.out.println("Client is ready");
        clientsThread.start();
        waitForCoupleSeconds();
        try {
            verify(spy).readMessageFromServer();
            verify(spy).sendMessageToServer();
        } catch (IOException ex) {
            Logger.getLogger(IT_RequestHandlerTester.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void waitForCoupleSeconds() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            // do nothing
        }
    }
}
