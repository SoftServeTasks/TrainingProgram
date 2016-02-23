/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.forwardproxyserver.ntlm.auth;

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
        return authorizedCustomers.containsValue(newClient);
    }
    
    public AuthorizedClientDto getClient (String workstation) {
        return authorizedCustomers.get(workstation);
    }

}
