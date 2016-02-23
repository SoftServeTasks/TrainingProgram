/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.forwardproxyserver.ntlm;

import com.mycompany.forwardproxyserver.ntlm.auth.AuthorizedClientDto;
import jcifs.ntlmssp.Type2Message;
import jcifs.ntlmssp.Type3Message;

/**
 *
 * @author osyniaev
 */
public class AuthNtlmType3Maker {
    
    private final Type2Message type2;
    private Type3Message type3;

    public AuthNtlmType3Maker(Type2Message type2) {
        this.type2 = type2;
    }
    
    public Type3Message makeType3Message () {
        type3 = new Type3Message(type2);
        return type3;
    }
    
    public Type3Message makeType3Message (AuthorizedClientDto client) {
        type3 = new Type3Message(type2, client.getPassword(), client.getDomain(), client.getUser(), client.getWorkstation(), type2.getFlags());
        return type3;
    }
    
}
