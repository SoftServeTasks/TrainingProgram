/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.sample;

/**
 *
 * @author osyniaev
 */
import java.io.*;
import java.net.*;

class SampleClient extends Thread {
 private static int counter = 1;

    public SampleClient() {
        counter++;
    }
 
 String request = null;
    public static void main(String args[]) {
        
        try {
            // открываем сокет и коннектимся к localhost:3128
            // получаем сокет сервера
            Socket s = new Socket("localhost", 3128);

            // берём поток вывода и выводим туда первый аргумент
            // заданный при вызове, адрес открытого сокета и его порт
            
            s.getOutputStream().write((counter + "\n" + s.getInetAddress().getHostAddress()
                    + ":" + s.getLocalPort()).getBytes());

            // читаем ответ
            byte buf[] = new byte[64 * 1024];
            int r = s.getInputStream().read(buf);
            String data = new String(buf, 0, r);

            // выводим ответ в консоль
            System.out.println(data);
        } catch (Exception e) {
            
            e.printStackTrace();
        } // вывод исключений
    }
}
