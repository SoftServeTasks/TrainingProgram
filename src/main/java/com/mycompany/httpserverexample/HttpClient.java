/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.httpserverexample;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author osyniaev
 */
public class HttpClient extends Thread {

    public static void main(String[] args) {

        ExecutorService service = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            service.submit(new Runnable() {
                public void run() {
                    int threadIdentificator = 1;
                    try {
                        // открываем сокет и коннектимся к localhost:3128
                        // получаем сокет сервера
                        Socket s = new Socket("localhost", 7777);
                        System.out.println("Client is started");

                        // берём поток вывода и выводим туда первый аргумент
                        // заданный при вызове, адрес открытого сокета и его порт
                        System.out.println("Current client name is " + threadIdentificator++);
                        s.getOutputStream().write(threadIdentificator++);

                        // читаем ответ
                        byte buf[] = new byte[64 * 1024];
                        int r = s.getInputStream().read(buf);
                        String data = new String(buf, 0, r);

                        // выводим ответ в консоль
                        System.out.println(data);
                    } catch (Exception e) {
                        System.out.println("init error: " + e);
                    } // вывод исключений
                }
            });
        }
    }
}
