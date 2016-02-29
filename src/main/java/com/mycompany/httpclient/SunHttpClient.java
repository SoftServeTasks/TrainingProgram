/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.httpclient;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;

/**
 *
 * @author osyniaev
 */
public class SunHttpClient extends Thread {

    HttpClient client;
    HttpClientParams clientParams;
    GetMethod method;
    HttpMethodParams methodParams;
    HostConfiguration hostConfig;

    public SunHttpClient() {
        this.client = new HttpClient();
        client.getState().clear();
        clientParams = client.getParams();
        clientParams.setParameter("http.protocol.content-charset", "UTF8");
        prepareHttpRequest();
    }

    private void prepareHttpRequest() {
        this.method = new GetMethod();
        method.setRequestHeader("Accept-Encoding", "gzip, deflate, sdch");
        method.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36");
        method.setRequestHeader("Proxy-Connection", "keep-alive");
        method.setRequestHeader("Accept-Language", "uk-UA,uk;q=0.8,ru;q=0.6,en-US;q=0.4,en;q=0.2");
        method.setRequestHeader("Proxy-Authorization: NTLM", "uk-UA,uk;q=0.8,ru;q=0.6,en-US;q=0.4,en;q=0.2");
        methodParams = method.getParams();
        methodParams.setVersion(HttpVersion.HTTP_1_1);

    }

    @Override
    public void run() {
        client.getParams().setParameter(AuthPolicy.AUTH_SCHEME_PRIORITY,
                Arrays.asList(AuthPolicy.NTLM));
        NTCredentials credentials = new NTCredentials("osyniaev", "password", "DP852", "SOFTSERVE");
        client.getState().setCredentials(AuthScope.ANY, credentials);
        hostConfig = hostConfig = client.getHostConfiguration();
        hostConfig.setProxy("localhost", 1002);
        hostConfig.setHost("portscan.ru");
        try {
            client.executeMethod(method);
        } catch (IOException ex) {
            Logger.getLogger(SunHttpClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            System.err.println(method.getResponseBodyAsString());
        } catch (IOException ex) {
            Logger.getLogger(SunHttpClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
