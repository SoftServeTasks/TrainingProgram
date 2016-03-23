/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.AuthorizationServer;

import com.mycompany.AuthorizationServer.HttpRequestParser;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author osyniaev
 */
public class HttpRequestParserTest {

    private HttpRequestParser instance;
    private String requestImmitation = "GET http://portscan.ru/js/plugins/bootstrap.min.js HTTP/1.1\n"
            + "Host: portscan.ru\n"
            + "User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; rv:44.0) Gecko/20100101 Firefox/44.0\n"
            + "Accept: */*\n"
            + "Accept-Language: ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3\n"
            + "Accept-Encoding: gzip, deflate\n"
            + "Referer: http://portscan.ru/\n"
            + "Cookie: _ym_uid=1455535634338552701; PHPSESSID=f154e155de9503ddabb99994fbba3f9e\n"
            + "Connection: keep-alive\n"
            + "If-Modified-Since: Wed, 16 Sep 2015 17:46:39 GMT\n"
            + "Cache-Control: max-age=0\n";

    @Before
    public void setUp() {
        instance = HttpRequestParser.INSTANCE;
        instance.setRequest(requestImmitation);

    }

    
    /**
     * Test of getHost method, of class HttpRequestParser.
     */
    @Test
    public void verifyGetHost() throws Exception {
        String expResult = "portscan.ru";
        String result = instance.getHost();
        assertEquals(expResult, result);
    }

    /**
     * Test of getPort method, of class HttpRequestParser.
     */
    @Test
    public void verifyGetPort() throws Exception {
        int expResult = 80;
        int result = instance.getPort();
        assertEquals(expResult, result);
    }

}
