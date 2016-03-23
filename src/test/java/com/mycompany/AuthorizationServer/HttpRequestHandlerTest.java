/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.AuthorizationServer;

import com.mycompany.AuthorizationServer.HttpRequestHandler;
import com.mycompany.AuthorizationServer.socket.SocketMock;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.Socket;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.internal.matchers.Any;

/**
 *
 * @author osyniaev
 */
public class HttpRequestHandlerTest {
    
   private HttpRequestHandler instance;
   private Socket socket;
   private int counter;
   private InputStream fromClient;
   private OutputStream toClient;
    
    
    @Before
    public void setUp() throws IOException  {
        socket = Mockito.mock(Socket.class);
        counter =1;
        instance = new HttpRequestHandler(socket, 1);
        fromClient = socket.getInputStream();
        toClient = socket.getOutputStream();
    }

    /**
     * Test of dawnloadFromInet method, of class HttpRequestHandler.
     */
    @Test
    public void testDawnloadFromInet() throws Exception {
        String header = "Hello";
        String host = "localhost";
        int port = 8080;
        instance.dawnloadFromInet(header, host, port);
        Field clientSocket = instance.getClass().getDeclaredField("sc");
        clientSocket.setAccessible(true);
        Socket actualResult = (Socket) clientSocket.get(instance);
        Socket expResult = new Socket(host, port);
        Assert.assertEquals(expResult.getInetAddress(), actualResult.getInetAddress());
        
    }

    /**
     * Test of run method, of class HttpRequestHandler.
     */
    @Test
    public void testRun() throws IOException, Exception {
       HttpRequestHandler instanc1=Mockito.mock(HttpRequestHandler.class);
       instanc1.setConnectionWithClient(socket);
       instanc1.run();
    }


    /**
     * Test of readClientsRequest method, of class HttpRequestHandler.
     */
    @Test(expected = NullPointerException.class)
    public void testReadClientsRequest() throws Exception {
        String result = instance.readClientsRequest(fromClient);
    }
    
}
