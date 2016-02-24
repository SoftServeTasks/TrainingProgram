/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.forwardproxyserver.ntlm;

import jcifs.ntlmssp.Type1Message;

/**
 *
 * @author osyniaev
 */
public class AuthNtlmType1Maker {
    private final String domain;
    private final String workstation;
    private static final int DEFAULT_FLAGS= -1576488441;

    public AuthNtlmType1Maker(String domain, String workstation) {
        this.domain = domain;
        this.workstation = workstation;
    }
    
    public Type1Message makeType1Message() {
        return new Type1Message(DEFAULT_FLAGS, domain, workstation);
    }
    
}
