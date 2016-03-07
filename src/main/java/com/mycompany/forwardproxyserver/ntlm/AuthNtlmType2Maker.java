/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.forwardproxyserver.ntlm;

import jcifs.ntlmssp.Type1Message;
import jcifs.ntlmssp.Type2Message;
import jcifs.util.Base64;
import org.apache.log4j.Logger;

/**
 *
 * @author osyniaev
 */
public class AuthNtlmType2Maker {
    
    private Type1Message type1;
    private ProxyMessages messageMacker;
    private Type2Message type2;
    private static final Logger LOGGER = Logger.getLogger(AuthNtlmType2Maker.class);


    public AuthNtlmType2Maker(Type1Message negotiate) {
        this.type1 = negotiate;
    }
    
    private Type2Message makeType2Message(){
        return new Type2Message(type1);
    }
    
    private void initType2 () {
        type2 = makeType2Message();
        type2.setChallenge("testChallenge".getBytes());
        type2.setContext("testContext".getBytes());
        type2.setTarget("testTarget");
        type2.setTargetInformation("targetInformation".getBytes());
        type2.setFlags(type1.getFlags());
        LOGGER.debug("\ntype2 decoded value: " +  type2.toString() + "\n");
    }
     
    public String getCompleteChallenge () {
        initType2();
        return messageMacker.CHALLENGE.text() + Base64.encode(type2.toByteArray());
    }
}
