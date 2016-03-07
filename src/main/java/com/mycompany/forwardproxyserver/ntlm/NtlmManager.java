/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.forwardproxyserver.ntlm;

import com.mycompany.forwardproxyserver.ResponseHandler;
import java.io.IOException;
import jcifs.smb.NtlmAuthenticator;
import org.apache.log4j.Logger;

;

/**
 *
 * @author osyniaev
 */
public class NtlmManager extends NtlmAuthenticator {

    private final AuthNtlmType1Handler type1Handler;
    private AuthNtlmType3Handler type3Handler;
    private final ResponseHandler responseHandler;
    private static final Logger LOGGER = Logger.getLogger(NtlmManager.class);

    public NtlmManager(ResponseHandler responseHandler) {
        this.responseHandler = responseHandler;
        type1Handler = new AuthNtlmType1Handler();

    }

    /**
     *
     * @throws IOException
     */
    public void return407() throws IOException {
        responseHandler.printAnyMessage(ProxyMessages.CODE407.text());
    }

    public void return401() throws IOException {
        responseHandler.printAnyMessage(ProxyMessages.CODE401.text());
    }

    public void return200Ok() throws IOException {
        responseHandler.printAnyMessage(ProxyMessages.CODE200.text());
    }

    public void returnChallenge() throws IOException {
        responseHandler.printAnyMessage(ProxyMessages.CHALLENGE.text());
    }

    public void resolveNegotiate(String negotiate) throws IOException {
        type1Handler.execute(negotiate);

    }

    public String testGetType1(String negotiate) {
        return type1Handler.getProxyAuthorizationHeaderValue(negotiate);
    }

    public void sendChallenge() {
        try {
            responseHandler.printAnyMessage(type1Handler.generateType2Message());
        } catch (NotEnoughUserCredentialsException ex) {
            LOGGER.error("Challenge have not been sent, becouse client hasnt enough creditionals",ex);

        } catch (IOException ex) {
            LOGGER.error("Error:",ex);
        }
    }


}
