/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.AuthorizationServer.ntlm;

import com.mycompany.AuthorizationServer.HttpRequestParser;
import com.mycompany.AuthorizationServer.ntlm.auth.AuthorizedClientDto;
import com.mycompany.AuthorizationServer.ntlm.auth.AuthorizedCustomerDao;
import java.io.IOException;
import jcifs.ntlmssp.Type3Message;
import jcifs.util.Base64;
import org.apache.log4j.Logger;

/**
 *
 * @author osyniaev
 */
public class AuthNtlmType3Handler {
    
     private final HttpRequestParser parser;
     private final AuthorizedCustomerDao dao;
     private String response;
     private Type3Message type3;
     private static final Logger LOGGER = Logger.getLogger(AuthNtlmType3Handler.class);

    public AuthNtlmType3Handler(String response) throws IOException {
        this.parser = HttpRequestParser.INSTANCE;
        this.dao = AuthorizedCustomerDao.INSTANSE;
        this.response = response;
        restoreType3Message();
    }
     
    public String getProxyAuthorizationHeaderValue () {
        return parser.extract(response, "Proxy-Authorization: NTLM ", "\n");
    }
    
    public Type3Message restoreType3Message() throws IOException {
        type3 = new Type3Message (Base64.decode(getProxyAuthorizationHeaderValue()));
        return type3;
    }

    public boolean checkUserData() {
        AuthorizedClientDto client = dao.getClient(type3.getWorkstation());
        if(client.getDomain().equals(type3.getDomain())&&client.getUser().equals(type3.getUser())) {
            return true;
        }
        return false;
    }
    
    
    
}
