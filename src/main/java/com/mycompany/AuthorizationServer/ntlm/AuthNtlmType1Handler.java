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
import jcifs.ntlmssp.Type1Message;
import jcifs.util.Base64;
import org.apache.log4j.Logger;

/**
 *
 * @author osyniaev
 */
public class AuthNtlmType1Handler {
    private final HttpRequestParser parser;
    private Type1Message type1Message;
    private final AuthorizedCustomerDao dao;
    private AuthNtlmType2Maker challengeMaker;
    private static final Logger LOGGER = Logger.getLogger(AuthNtlmType1Handler.class);

    public AuthNtlmType1Handler() {
        parser = HttpRequestParser.INSTANCE;
        dao=AuthorizedCustomerDao.INSTANSE;
        
    }

    void execute(String negotiate) throws IOException {
        type1Message = new Type1Message(Base64.decode(getProxyAuthorizationHeaderValue(negotiate)));
        //type1Message=new Type1Message(getType1Message(negotiate).getBytes());
        challengeMaker = new AuthNtlmType2Maker(type1Message);
        //int flags = type1Message.getFlags();
        LOGGER.info("Domain: " + type1Message.getSuppliedDomain() + "\nWorkstation: " 
                + type1Message.getSuppliedWorkstation());
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
        LOGGER.debug("Check: " + dao.isExist(new AuthorizedClientDto(domain, workstation)));
        return (dao.isExist(new AuthorizedClientDto(domain, workstation)));
    }
    
    
}
