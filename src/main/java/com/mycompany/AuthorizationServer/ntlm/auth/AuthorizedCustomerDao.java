/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.AuthorizationServer.ntlm.auth;

import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * @author osyniaev
 */
public enum AuthorizedCustomerDao implements IAuthorizedDao {
    INSTANSE;
    private volatile ConcurrentHashMap<String, AuthorizedClientDto> authorizedCustomers = new ConcurrentHashMap<>();

    private AuthorizedCustomerDao() {
        initForTest();
    }

    public void addClient(AuthorizedClientDto newClient) {
        authorizedCustomers.put(newClient.getWorkstation(), newClient);
    }

    private void initForTest() {
        addClient(new AuthorizedClientDto("SOFTSERVE", "DP852", "osyniaev","password"));
    }

    public boolean isExist(AuthorizedClientDto newClient) {
        if (authorizedCustomers.containsValue(newClient)) {
            return true;
        }
        AuthorizedClientDto client = getClient(newClient.getWorkstation());
        if (client.getDomain().equals(newClient.getDomain())) {
            return true;
        }
        return false;
    }
    
    public AuthorizedClientDto getClient (String workstation) {
        return authorizedCustomers.get(workstation);
    }

}
