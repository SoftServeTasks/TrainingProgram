/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.forwardproxyserver;

import java.io.InputStream;
import java.io.OutputStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 *
 * @author osyniaev
 */
public class ResponseHandlerTest {
    
    private ResponseHandler instance;
    private InputStream fromClientChannel;
    private OutputStream toClientChannel;
    
    @Before
    public void setUp() {
        fromClientChannel = mock(InputStream.class);
        toClientChannel = mock (OutputStream.class);
        instance = new ResponseHandler(fromClientChannel, toClientChannel);
    }
    
    @After
    public void tearDown() {
    }

    
    /**
     * Test of printError method, of class ResponseHandler.
     */
    @Test
    public void verifyPrintError() throws Exception {
        String err = "Error1";
        instance.printError(err);
        verify(toClientChannel).write(new String("HTTP/1.1 400 Bad Request\nServer: HomeProxy\n"
                + "Content-Type: text/plain; charset=windows-1251\n\n"
                + err).getBytes());
    }

    /**
     * Test of printMessage method, of class ResponseHandler.
     */
    @Test
    public void verifyPrintMessage() throws Exception {
        String message = "Message";
        instance.printMessage(message);
        verify(toClientChannel).write((new String("HTTP/1.1 200 OK\nServer: HomeProxy\n"
                + "Content-Type: text/plain; charset=windows-1251\n\n"
                + message)).getBytes());
    }
    
}
