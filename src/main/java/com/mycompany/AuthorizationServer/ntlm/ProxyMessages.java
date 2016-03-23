/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.AuthorizationServer.ntlm;

/**
 *
 * @author osyniaev
 */
public enum ProxyMessages {
    CODE407("HTTP/1.1 407 Proxy Authentication Required\n"
            + "Server: squid/2.5.STABLE3\nMime-Version: 1.0\n"
            + "Content-Type: text/htmlContent-Length: 21\n"
            + "Proxy-Authenticate: NTLM\n"
            + "Connection: close\n\n"),
    CODE401("HTTP/1.1 401 Unauthorized\n"
            + "Server: squid/2.5.STABLE3\nMime-Version: 1.0\n"
            + "Content-Type: text/html\nContent-Length: 20\n"
            + "WWW-Authenticate: Basic realm=\" --== Protected web-Area ==--\"\n"
            + "Connection: close\n\n"),
    CODE200("HTTP/1.1 200 OK\n"
            + "Server: squid/2.5.STABLE3\n"
            + "Mime-Version: 1.0\n"
            + "Content-Type: text/html\n"
            + "Content-Length: 19\n"
            + "Connection: close"),
    CHALLENGE("HTTP/1.1 407 Proxy Authentication Required\n"
            + "Server: squid/2.5.STABLE3\nMime-Version: 1.0\n"
            + "Content-Type: text/htmlContent-Length: 21\n"
            + "Connection: close\n"
            + "Proxy-Authenticate: NTLM ");

    private String message;

    private ProxyMessages(String message) {
        this.message = message;
    }

    public String text() {
        return message;
    }
}
