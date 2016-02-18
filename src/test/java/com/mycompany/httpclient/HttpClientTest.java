/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.httpclient;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author osyniaev
 */
public class HttpClientTest {
    
    private HttpClient instance;
    private Socket socket;
    private InputStream in;
    private OutputStream out;
    private String toServer;
    
    
    @Before
    public void setUp() throws IOException {
        ServerSocket httpListener = new ServerSocket(1002);
        httpListener.accept();
        instance = new HttpClient(1);
        instance.run();;
        socket = instance.getSocket();
        in = socket.getInputStream();
        out= socket.getOutputStream();
        toServer = "CURRENT TIME IS: 13:03:30\n"
                    + "CLIENTS REQUEST: \n"
                    + "GET http://portscan.ru/ HTTP/1.1\n"
                    + "Host: portscan.ru\n"
                    + "User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:44.0) Gecko/20100101 Firefox/44.0\n"
                    + "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8\n"
                    + "Accept-Language: ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3\n"
                    + "Accept-Encoding: gzip, deflate\n"
                    + "Cookie: _ym_uid=1455535634338552701; PHPSESSID=b1ef2ba08fcbfaea0e159dd877ac417d\n"
                    + "Connection: keep-alive\n";
    }


    /**
     * Test of run method, of class HttpClient.
     */
    @Test
    public void verifyRunClient() throws IOException {
        instance.run();
        verify(instance).sendMessageToServer();
        verify(instance).readMessageFromServer();
    }
    
    /**
     * Test of sendMessageToServer method, of class HttpClient.
     */
    @Test
    public void verifySendMessageToServer() throws IOException {
        instance.sendMessageToServer();
        verify(out).write(toServer.getBytes());
    }
    
    /**
     * Test of readMessageFromServer method, of class HttpClient.
     */
    @Test
    public void verifyreadMessageFromServer() throws IOException {
        instance.readMessageFromServer();
        verify(in).read();
    }
}
