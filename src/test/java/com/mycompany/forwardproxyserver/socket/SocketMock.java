/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.forwardproxyserver.socket;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 * @author osyniaev
 */
public class SocketMock extends Socket {
    private String DEFAULT_HOST="localhost";
    private int DEFAULT_PORT = 8080;
    private boolean created = false;
    

    public SocketMock(String host, int port) throws UnknownHostException, IOException {
        super();
        makeCreated();
    }
    
    
    
    public boolean isCreated(){
        return created;
    }
    
    public void makeCreated () {
        this.created=true;
    }
    
    
}
