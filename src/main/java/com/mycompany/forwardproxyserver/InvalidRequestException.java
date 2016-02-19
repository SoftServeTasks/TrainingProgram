/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.forwardproxyserver;

/**
 * exception is thrown  when the request from the client is incorrect
 * @author osyniaev
 */
public class InvalidRequestException extends RuntimeException{
    
    private final String request;

    public InvalidRequestException(String request) {
        this.request = request;
    }
    
    
    @Override
    public String getLocalizedMessage(){
        return String.format("invalid request from client: %s", request);
    }
    
    @Override
    public String getMessage() {
        return String.format("invalid request from client: %s", request);
    }
    
    @Override
    public String toString() {
        return String.format("InvalidRequestException means invalid request from client: %s", request);
    }
}
