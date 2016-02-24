/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.forwardproxyserver;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 *
 * @author osyniaev
 */
public enum HttpRequestParser {
    INSTANCE;

    private String request;
    private String host;
    private int port;

    HttpRequestParser() {

    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    /**
     *
     * "Cuts" from the string str portion located between the lines start and
     * end If the string end doesn't exist, it takes string after the start line
     * if a piece is not found, Returns null for the search method takes the
     * string to "\ n \ n" or "\ r \ n \ r \ n", If it is present
     */
    public String extract(String str, String start, String end) {
        int s = str.indexOf("\n\n", 0), e;
        if (s < 0) {
            s = str.indexOf("\r\n\r\n", 0);
        }
        if (s > 0) {
            str = str.substring(0, s);
        }
        s = str.indexOf(start, 0) + start.length();
        if (s < start.length()) {
            return null;
        }
        e = str.indexOf(end, s);
        if (e < 0) {
            e = str.length();
        }
        return (str.substring(s, e)).trim();
    }

    /**
     *
     * @return header "host" value host:port
     */
    private String getHostLine() {
        String hostLine = extract(request, "Host:", "\n");
        if ((hostLine == null)) {
            throw new InvalidRequestException(request);
        }
        System.out.println("hostLine: " + hostLine);
        return hostLine;
    }

    /**
     *
     * @return HOST
     * @throws Exception
     */
    public String getHost() throws Exception {
        String hostLine = getHostLine();
        int colonIndex;
        if (hostLine.contains(":")) {
            colonIndex = hostLine.indexOf(":", 0);
        } else {
            colonIndex = hostLine.indexOf("/", 0);
        }
        if (colonIndex < 0) {
            setHost(hostLine);
            System.err.println("host: " + hostLine);
            return hostLine;

        } else {
            hostLine = hostLine.substring(0, colonIndex);
            setHost(hostLine);
            System.err.println("host: " + hostLine);
            return hostLine;
        }
    }

    /**
     *
     * @return PORT
     * @throws Exception
     */
    public int getPort() throws Exception {
        String hostLine = getHostLine();
        int portValue = hostLine.indexOf(":", 0);
        if (portValue < 0) {
            portValue = 80;
        } else {
            portValue = Integer.parseInt(hostLine.substring(portValue + 1));
        }
        setPort(portValue);
        System.err.println("port = " + portValue);
        return portValue;
    }
    
    public String cleanClientsRequest () {
        String [] strings = request.split("\n");
        String result="";
        for (String str: strings) {
            if(!str.startsWith("Proxy-Authorization: NTLM ")) {
                result = result.concat(str.concat("\n"));
            } 
        }
        return result;
    }
}
