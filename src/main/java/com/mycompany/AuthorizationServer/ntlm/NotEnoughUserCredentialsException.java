/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.AuthorizationServer.ntlm;

/**
 *
 * @author osyniaev
 */
class NotEnoughUserCredentialsException extends Exception {

    private String message;
    
    public NotEnoughUserCredentialsException() {
    }

    public NotEnoughUserCredentialsException(String message) {
        super(message);
        this.message = message;
    }

    public NotEnoughUserCredentialsException(String message, Throwable cause) {
        super(message, cause);
        this.message = message;
    } 
    
}
