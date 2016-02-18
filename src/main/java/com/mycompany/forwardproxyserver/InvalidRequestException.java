/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.forwardproxyserver;

/**
 * исключение, вібрасіваемое при некорректном запросе от клиента
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
