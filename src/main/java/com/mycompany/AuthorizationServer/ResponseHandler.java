/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.AuthorizationServer;

import com.mycompany.AuthorizationServer.ntlm.ProxyMessages;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.log4j.Logger;

/**
 *
 * @author osyniaev
 */
public class ResponseHandler {
    
    private InputStream fromClientChannel = null;
    private OutputStream toClientChannel = null;
    private static final Logger LOGGER = Logger.getLogger(ResponseHandler.class);
    
    public ResponseHandler() {
    }
    
    public ResponseHandler(InputStream fromClientChannel, OutputStream toClientChannel) {
        this.fromClientChannel = fromClientChannel;
        this.toClientChannel = toClientChannel;
        
    }
    
    public InputStream getFromClientChannel() {
        return fromClientChannel;
    }
    
    public void setFromClientChannel(InputStream fromClientChannel) {
        this.fromClientChannel = fromClientChannel;
    }
    
    public OutputStream getToClientChannel() {
        return toClientChannel;
    }
    
    public void setToClientChannel(OutputStream toClientChannel) {
        this.toClientChannel = toClientChannel;
    }

    /**
     * returns proxy error to the client
     *
     * @param err
     * @throws Exception
     */
    public void printError(String err) throws Exception {
        toClientChannel.write((new String("HTTP/1.1 400 Bad Request\nServer: HomeProxy\n"
                + "Content-Type: text/plain; charset=windows-1251\n\n"
                + err)).getBytes());
    }

    /**
     * returns proxy message to the client
     *
     * @param message
     * @throws IOException
     */
    public void printMessage(String message) throws IOException {
        toClientChannel.write((new String("HTTP/1.1 200 OK\nServer: HomeProxy\n"
                + "Content-Type: text/plain; charset=windows-1251\n\n"
                + message)).getBytes());
    }

   public void printAnyMessage(String message) throws IOException {
       toClientChannel.write(message.getBytes());
   }
   
   public void print401Error() throws IOException {
       toClientChannel.write((ProxyMessages.CODE401.text()).getBytes());
   }
    
}