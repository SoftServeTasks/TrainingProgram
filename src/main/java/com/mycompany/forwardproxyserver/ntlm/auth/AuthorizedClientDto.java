/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.forwardproxyserver.ntlm.auth;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author osyniaev
 */
public final class AuthorizedClientDto implements Serializable {
    private final String domain;
    private final String workstation;
    private String user;
    private String password;

    public AuthorizedClientDto(String domain, String workstation) {
        this.domain = domain;
        this.workstation = workstation;
    }

    public AuthorizedClientDto(String domain, String workstation, String user, String password) {
        this.domain = domain;
        this.workstation = workstation;
        this.user = user;
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
     public String getDomain() {
        return domain;
    }

    public String getWorkstation() {
        return workstation;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.domain);
        hash = 97 * hash + Objects.hashCode(this.workstation);
        hash = 97 * hash + Objects.hashCode(this.user);
        hash = 97 * hash + Objects.hashCode(this.password);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AuthorizedClientDto other = (AuthorizedClientDto) obj;
        if (!Objects.equals(this.domain, other.domain)) {
            return false;
        }
        if (!Objects.equals(this.workstation, other.workstation)) {
            return false;
        }
        if (!Objects.equals(this.user, other.user)) {
            return false;
        }
        if (!Objects.equals(this.password, other.password)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AuthorizedClientDto{" + "domain=" + domain + ", workstation=" + workstation + ", user=" + user + ", password=" + password + '}';
    }

    
    
}
