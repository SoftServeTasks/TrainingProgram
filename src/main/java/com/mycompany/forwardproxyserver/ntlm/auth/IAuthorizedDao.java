/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.forwardproxyserver.ntlm.auth;

/**
 *
 * @author osyniaev
 */
public interface IAuthorizedDao {
     void addClient(AuthorizedClientDto newClient);
     boolean isExist (AuthorizedClientDto newClient);
}
