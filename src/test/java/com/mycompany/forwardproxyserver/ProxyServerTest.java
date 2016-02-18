/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.forwardproxyserver;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;


/**
 *
 * @author osyniaev
 */

public class ProxyServerTest {

    private ProxyServer instanse;
    private ServerSocket serverSocket;

    @Before
    public void setUp() {
        instanse = new ProxyServer(); 
    }

    /**
     * Test of startServer method, of class ProxyServer.
     */
    @Test
    public void verifyStartServer() throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, Exception {
        
        ProxyServer.startServer();
        Field declaredField = instanse.getClass().getDeclaredField("httpListener");
        declaredField.setAccessible(true);
        ServerSocket ss = (ServerSocket) declaredField.get(this);
        int actual = ss.getLocalPort();
        int expected = 1002;
        Assert.assertEquals(expected, actual);
        
    }

    /**
     * Test of listenPort method, of class ProxyServer.
     */
    @Test
    public void verifyListenPort() throws IOException, IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
        ServerSocket httpListener = mock(ServerSocket.class);
        Field declaredField = instanse.getClass().getDeclaredField("httpListener");
        declaredField.setAccessible(true);
        declaredField.set(this, httpListener);
        ProxyServer.listenPort();
        spy(httpListener).accept();
    }
   

    /**
     * Test of listenPort method, of class ProxyServer.
     */
    @Test(expected = IOException.class)
    public void verifyListenPortThrowsException() throws IOException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        ServerSocket mock = mock(ServerSocket.class);
        Field declaredField = instanse.getClass().getDeclaredField("httpListener");
        declaredField.setAccessible(true);
        declaredField.set(this, mock);
        doThrow(new IOException()).when(mock).accept();
        ProxyServer.listenPort();

    }
}
