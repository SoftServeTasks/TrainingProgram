/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.httpserverexample.proxyserver;

import com.sun.net.httpserver.HttpServer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author osyniaev
 */
class ClientSession implements Runnable {

    private Socket socket;
    private InputStream in = null;
    private OutputStream out = null;


    @Override
    public void run() {
        try {
            /* Получаем заголовок сообщения от клиента */
            String header = readHeader();
            System.out.println(header + "\n");
            /* Получаем из заголовка указатель на интересующий ресурс */
            from_net(header, "localhost", 7777);
            System.err.println(readHeader());
            writeAnswerFromServer(readHeader());
            System.out.println("Работа proxy окончена успешно! ");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            Logger.getLogger(ClientSession.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Throwable ex) {
            Logger.getLogger(ClientSession.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ClientSession(Socket socket) throws IOException {
        this.socket = socket;
        initialize();
    }

    private void initialize() throws IOException {
        /* Получаем поток ввода, в который помещаются сообщения от клиента */
        in = socket.getInputStream();
        /* Получаем поток вывода, для отправки сообщений клиенту */
        out = socket.getOutputStream();
    }

    /**
     * * Считывает заголовок сообщения от клиента. * * @return строка с
     * заголовком сообщения от клиента. * @throws IOException
     */
    private String readHeader() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder builder = new StringBuilder();
        String ln = null;
        while (true) {
            ln = reader.readLine();
            if (ln == null || ln.isEmpty()) {
                break;
            }
            builder.append(ln + System.getProperty("line.separator"));
        }
        return builder.toString();
    }

    // "вырезает" из строки str часть, находящуюся между строками start и end
    // если строки end нет, то берётся строка после start
    // если кусок не найден, возвращается null
    // для поиска берётся строка до "\n\n" или "\r\n\r\n", если таковые присутствуют
    protected String extract(String str, String start, String end) {
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

    // печатает ошибку прокси
    protected void printError(String err) throws Exception {
        out.write((new String("HTTP/1.1 200 OK\nServer: HomeProxy\n"
                + "Content-Type: text/plain; charset=windows-1251\n\n"
                + err)).getBytes());
    }

    // вытаскивает из HTTP заголовка хост, порт соединения,
    // после чего вызывает другой сервер (для нескольких серверов, на которые
    // выполняется маршрутизация
    protected void from_net(String header) throws Exception, Throwable {
        String host = extract(header, "Host:", "\n");
        if (host == null) {
            printError("invalid request:\n" + header);
            return;
        }

        int port = host.indexOf(":", 0);
        if (port < 0) {
            port = 80;
        } else {
            port = Integer.parseInt(host.substring(port + 1));
            host = host.substring(0, port);
        }

        from_net(header, host, port);
    }

    // подключение к серверу
    protected void from_net(String header, String host, int port) throws Exception, Throwable {
        System.out.println("Перенаправляю запрос на http://localhost:7777/");
        Socket sc = new Socket(host, port);
        header = "ПОЛУЧЕНО ОТ PROXY! \n" + header;
        sc.getOutputStream().write(header.getBytes());
        byte buf[] = new byte[128 * 1024];
        int r = sc.getInputStream().read(buf);
        String data = new String(buf, 0, r);
        writeAnswerFromServer("ОТ 7777 ПОЛУЧЕН ОТВЕТ: \n" + data);
        r=in.read(buf);
        data = new String(buf, 0, r);
        writeAnswerFromServer("ОТ 7777 ПОЛУЧЕН ОТВЕТ: \n" + data);
        sc.close();
    }
    
    private void writeAnswerFromServer(String serversAnswer) throws Throwable {
            out.write(serversAnswer.getBytes());
            out.flush();
        }

}
