/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.forwardproxyserver.telemetry;

import com.mycompany.forwardproxyserver.HttpRequestParser;
import java.util.HashMap;
import org.apache.http.HttpRequest;
import org.apache.log4j.Logger;

/**
 *
 * @author osyniaev
 */
public enum ClientsBrowserAnalyzer {
    INSTANSE;

    private HttpRequestParser parser;
    private String userAgentHeader;
    private volatile HashMap<String, Integer> browsers = new HashMap<>();
    private static final Logger LOGGER = Logger.getLogger(ClientsBrowserAnalyzer.class);

    private ClientsBrowserAnalyzer() {
        initBroserBase();
    }

    public HttpRequestParser getParser() {
        return parser;
    }

    public void setParser(HttpRequestParser parser) {
        this.parser = parser;
    }

    private void addBrowser(String browserName) {
        Integer numberRequestsFromBrowser = browsers.get(browserName);
        numberRequestsFromBrowser++;
        LOGGER.debug("Добавляю " + numberRequestsFromBrowser + "-й браузер " + browserName + " в базу\n");
        browsers.put(browserName, numberRequestsFromBrowser);
    }

    private boolean isOpera() {
        if (userAgentHeader.contains("Opera")) {
            return true;
        }
        return false;
    }

    private boolean isChrome() {
        if (userAgentHeader.contains("Chrome")) {
            return true;
        }
        return false;
    }

    private boolean isSafary() {
        if (userAgentHeader.contains("Safary")) {
            return true;
        }
        return false;
    }

    private boolean isInternetExplorer() {
        if (userAgentHeader.contains("rv:11") || userAgentHeader.contains("MSIE")) {
            return true;
        }
        return false;
    }

    private boolean isMozilla() {
        if (userAgentHeader.contains("Firefox")) {
            return true;
        }
        return false;
    }

    private void initBroserBase() {
        browsers.put("Opera", 0);
        browsers.put("Google Chrome", 0);
        browsers.put("Safary", 0);
        browsers.put("Mozilla Firefox", 0);
        browsers.put("Internet Explirer", 0);
        browsers.put("Other", 0);
    }

    public void analyzeBrowserType() {
        userAgentHeader = parser.extract(parser.getRequest(), "User-Agent: Mozilla/5.0", "\n");
        if (isChrome()) {
            addBrowser("Google Chrome");
        } else if (isOpera()) {
            addBrowser("Opera");
        } else if (isSafary()) {
            addBrowser("Safary");
        } else if (isInternetExplorer()) {
            addBrowser("Internet Explirer");
        } else if (isMozilla()) {
            addBrowser("Mozilla Firefox");
        } else {
            addBrowser("Other");
        }
    }

    public void getInfoAboutBrpowsers() {
        LOGGER.info("\n\n*****************************\nИнформация о браузерах: "
                + browsers.toString() + "\n*****************************\n");
    }
}
