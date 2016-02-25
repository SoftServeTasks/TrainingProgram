/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.httpclient;

import com.mycompany.forwardproxyserver.HttpRequestParser;
import com.mycompany.forwardproxyserver.ntlm.AuthNtlmType1Maker;
import com.mycompany.forwardproxyserver.ntlm.AuthNtlmType3Maker;
import com.mycompany.forwardproxyserver.ntlm.auth.AuthorizedClientDto;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import jcifs.ntlmssp.Type2Message;
import jcifs.ntlmssp.Type3Message;
import jcifs.util.Base64;

/**
 *
 * @author osyniaev
 */
public class HttpClient /*implements Runnable*/ extends Thread{

    private final CountDownLatch latch;
    private int threadIdentificator;
    private int port;
    private final static int DAFAULT_PORT = 1002;
    private Socket socket;
    private HttpRequestParser parser = HttpRequestParser.INSTANCE;
    private AuthNtlmType3Maker type3Maker;
    private AuthNtlmType1Maker type1Maker;
    private Type2Message type2;
    private Type3Message type3;
    private String domain;
    private String workstation;
    private String user;
    private String password;

    private String defaultMessageToServer = "GET http://portscan.ru/ HTTP/1.1\n"
            + "Host: portscan.ru\n"
            + "Proxy-Connection: keep-alive\n"
            + "Cache-Control: max-age=0\n"
            + "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\n"
            + "Upgrade-Insecure-Requests: 1\n"
            + "User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/48.0.2564.116 Safari/537.36\n"
            + "Accept-Encoding: gzip, deflate, sdch\n"
            + "Accept-Language: uk-UA,uk;q=0.8,ru;q=0.6,en-US;q=0.4,en;q=0.2\n"
            + "Cookie: _ym_uid=1455283662407492012; PHPSESSID=910986a453229a57d394f007caf5f811\n"/* + threadIdentificator*/;

    private String type1MessageToServer;

    private String type3MessageToServer;

    public HttpClient(int count, CountDownLatch latch) {
        this.threadIdentificator = count;
        this.port = DAFAULT_PORT;
        initClientsData();
        type1Maker = new AuthNtlmType1Maker(domain, workstation);
        type1MessageToServer = defaultMessageToServer + "Proxy-Authorization: NTLM " + Base64.encode(type1Maker.makeType1Message().toByteArray()) + "\n\n";
        this.latch = latch;
    }
    
    public HttpClient(int count) {
        this.threadIdentificator = count;
        this.port = DAFAULT_PORT;
        initClientsData();
        type1Maker = new AuthNtlmType1Maker(domain, workstation);
        type1MessageToServer = defaultMessageToServer + "Proxy-Authorization: NTLM " + Base64.encode(type1Maker.makeType1Message().toByteArray()) + "\n\n";
        this.latch = null;
    }

    public HttpClient(int threadIdentificator, int port, CountDownLatch latch) {
        this.threadIdentificator = threadIdentificator;
        this.port = port;
        initClientsData();
        type1Maker = new AuthNtlmType1Maker(domain, workstation);
        this.latch = latch;
    }

    public Socket getSocket() {
        return socket;
    }

    private void initClientsData() {
        domain = "SOFTSERVE";
        workstation = "DP852";
        user = "osyniaev";
        password = "password";
    }

    @Override
    public void run() {
        try {
            socket = new Socket("localhost", port);
            System.out.println("Client number " + threadIdentificator + " started");
            Thread.sleep(1000);
            sendMessageToServer();
            System.out.println("* CLIENT: Message sent to server localhost:" + port + "!\n");

            Thread.sleep(2000);

            String messageFromServer = readMessageFromServer();

            System.out.println("///////////////////////////////////////////////////\n"
                    + "* CLIENT: SERVER localhost:" + port + " RETURNED ANSWER: \n\n " + messageFromServer
                    + "\n ************************************************** \n");

            String proxyAuthenticatHeaderValue = parser.extract(messageFromServer, "Proxy-Authenticate: ", "\n");
            System.err.println("* CLIENT: Сервер вернул header Proxy-Authenticate: " + proxyAuthenticatHeaderValue + "\n");
            if (proxyAuthenticatHeaderValue.equals("NTLM")) {
                System.err.println("* CLIENT: Отправляю Type1Message \n" + type1MessageToServer);
                sendMessageToServer(type1MessageToServer);
            }
            Thread.sleep(2000);
            String challengeFromServer = readMessageFromServer();
            System.err.println("\n* CLIENT: I've got challenge fron proxy: \n" + challengeFromServer + "\n");
            String ntlmHeaderValue = parser.extract(challengeFromServer, "Proxy-Authenticate: NTLM ", "\n");
            type2 = new Type2Message(Base64.decode(ntlmHeaderValue));
            type3Maker = new AuthNtlmType3Maker(type2);
            type3 = type3Maker.makeType3Message(new AuthorizedClientDto(domain, workstation, user, password));
            type3MessageToServer = defaultMessageToServer + "Proxy-Authorization: NTLM " + Base64.encode(type3.toByteArray());
            System.err.println("\nType3: " + type3 + "\n");
            Thread.sleep(2000);
            sendMessageToServer(type3MessageToServer);
            Thread.sleep(2000);
            String completeMessageFromServer = readMessageFromServer();
            System.err.println("\n* CLIENT: Hooray! I've got necessary information\n Servers Response is :\n" + completeMessageFromServer);

        } catch (Exception e) {
            System.out.println("* CLIENT: init error: " + e);
            e.printStackTrace();
        }
    }

    /**
     * sends messages to server
     *
     * @throws IOException
     */
    public void sendMessageToServer() throws IOException {
        socket.getOutputStream().write((defaultMessageToServer + "\n").getBytes());
    }

    public void sendMessageToServer(String message) throws IOException {
        socket.getOutputStream().write(message.getBytes());
    }

    /**
     * client read messages from server
     *
     * @return (String) server message
     * @throws IOException
     */
    public String readMessageFromServer() throws IOException {
        byte buf[] = new byte[64 * 1024];
        int r = socket.getInputStream().read(buf);
        String data = new String(buf, 0, r);
        return data;
    }
}
