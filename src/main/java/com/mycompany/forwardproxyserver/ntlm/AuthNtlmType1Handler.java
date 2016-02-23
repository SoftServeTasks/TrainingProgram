/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.forwardproxyserver.ntlm;

import com.mycompany.forwardproxyserver.HttpRequestParser;
import com.mycompany.forwardproxyserver.ntlm.auth.AuthorizedClientDto;
import com.mycompany.forwardproxyserver.ntlm.auth.AuthorizedCustomerDao;
import java.io.IOException;
import jcifs.ntlmssp.Type1Message;
import jcifs.util.Base64;

/**
 *
 * @author osyniaev
 */
public class AuthNtlmType1Handler {
    private final HttpRequestParser parser;
    private Type1Message type1Message;
    private final AuthorizedCustomerDao dao;
    private AuthNtlmType2Maker challengeMaker;

    public AuthNtlmType1Handler() {
        parser = HttpRequestParser.INSTANCE;
        dao=AuthorizedCustomerDao.INSTANSE;
        
    }

    void execute(String negotiate) throws IOException {
        type1Message = new Type1Message(Base64.decode(getProxyAuthorizationHeaderValue(negotiate)));
        //type1Message=new Type1Message(getType1Message(negotiate).getBytes());
        challengeMaker = new AuthNtlmType2Maker(type1Message);
        //int flags = type1Message.getFlags();
        System.err.println("Domain: " + type1Message.getSuppliedDomain() + "\nWorkstation: " + type1Message.getSuppliedWorkstation());
    }
    
    public String generateType2Message() throws NotEnoughUserCredentialsException {
        if (checkCredentials(type1Message.getSuppliedDomain(), type1Message.getSuppliedWorkstation())) {
            return challengeMaker.getCompleteChallenge();
        } else {
            throw new NotEnoughUserCredentialsException("Client with domain " + type1Message.getSuppliedDomain()
                    + " and workstation " + type1Message.getSuppliedWorkstation() + "doesn't have"
                    + "enough creditionals for access");
        } 
    }

    public String getProxyAuthorizationHeaderValue (String negotiate) {
        return parser.extract(negotiate, "Proxy-Authorization: NTLM ", "\n");
    }
    
    private boolean checkCredentials (String domain, String workstation) {
        return (dao.isExist(new AuthorizedClientDto(domain, workstation)));
    }
    
    
}
