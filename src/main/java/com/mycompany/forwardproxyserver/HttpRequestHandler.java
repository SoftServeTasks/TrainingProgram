/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.forwardproxyserver;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *
 * @author osyniaev
 */
public class HttpRequestHandler implements Runnable{
    
    private final Socket connectionWithClient;
    private InputStream fromClientChannel = null;
    private OutputStream toClientChannel = null;
    //private static final Logger LOGGER = LogManager.getLogger(HttpRequestHandler.class);
    
    public HttpRequestHandler(Socket socket) throws IOException {
        this.connectionWithClient = socket;
        initialize();
    }

    private void initialize() throws IOException {
        /* Получаем поток ввода, в который помещаются сообщения от клиента */
        fromClientChannel = connectionWithClient.getInputStream();
        /* Получаем поток вывода, для отправки сообщений клиенту */
        toClientChannel = connectionWithClient.getOutputStream();
    }

    // "вырезает" из строки str часть, находящуюся между строками start и end
    // если строки end нет, то берётся строка после start
    // если кусок не найден, возвращается null
    // для поиска берётся строка до "\n\n" или "\r\n\r\n", если таковые присутствуют
    protected String extract(String str, String start, String end)
    {
        int s = str.indexOf("\n\n", 0), e;
        if(s < 0) s = str.indexOf("\r\n\r\n", 0);
        if(s > 0) str = str.substring(0, s);
        s = str.indexOf(start, 0)+start.length();
        if(s < start.length()) return null;
        e = str.indexOf(end, s);
        if(e < 0) e = str.length();
        System.err.println("Обрезанная строка: \n" + str.substring(s, e));
        return (str.substring(s, e)).trim();
    }

    // "вырезает" из HTTP заголовка URI ресурса и конвертирует его в filepath для файла кэша
    // URI берётся только для GET и POST запросов, иначе возвращается null
    protected String getPath(String header)
    {
        String URI = extract(header, "GET ", " "), path;
        if(URI == null) URI = extract(header, "POST ", " ");
        if(URI == null) return null;

        path = URI.toLowerCase();
        if(path.indexOf("http://", 0) == 0)
            URI = URI.substring(7);
        else
        {
            path = extract(header, "Host:", "\n");
            if(path == null) return null;
            URI = path+URI;
        }

        // define cashe path
        path = "cache"+File.separator;

        // convert URI to filepath
        char a;
        boolean flag = false;
        for(int i = 0; i < URI.length(); i++)
        {
            a = URI.charAt(i);

            switch(a)
            {
            case '/' :
                if(flag)
                    path = path+"%"+Integer.toString((int)a, 16).toUpperCase();
                else
                    path = path+".!"+File.separatorChar;
                break;
            case '!' :
                path = path+"%"+Integer.toString((int)a, 16).toUpperCase();
                break;
            case '\\' :
                path = path+"%"+Integer.toString((int)a, 16).toUpperCase();
                break;
            case ':' :
                path = path+"%"+Integer.toString((int)a, 16).toUpperCase();
                break;
            case '*' :
                path = path+"%"+Integer.toString((int)a, 16).toUpperCase();
                break;
            case '?' :
                if(flag)
                    path = path+"%"+Integer.toString((int)a, 16).toUpperCase();
                else
                {
                    path = path+".!"+File.separatorChar;
                    flag = true;
                }
                break;
            case '"' :
                path = path+"%"+Integer.toString((int)a, 16).toUpperCase();
                break;
            case '<' :
                path = path+"%"+Integer.toString((int)a, 16).toUpperCase();
                break;
            case '>' :
                path = path+"%"+Integer.toString((int)a, 16).toUpperCase();
                break;
            case '|' :
                path = path+"%"+Integer.toString((int)a, 16).toUpperCase();
                break;
            default: path = path+a;
            }
        }
        if(path.charAt(path.length()-1) == File.separatorChar) path = path+".root";
        System.err.println("FilePath: " + path);
        return path;
        
    }

    // печатает ошибку прокси
    protected void printError(String err) throws Exception
    {
        toClientChannel.write((new String("HTTP/1.1 200 OK\nServer: HomeProxy\n"
                        +"Content-Type: text/plain; charset=windows-1251\n\n"
                        +err)).getBytes());
    }

    // загружает из сети страничку с одновременным кэшированием её на диск
    // странички в кэше храняться прямо с HTTP заголовком
    protected void from_net(String header, String host, int port, String path) throws Exception
    {
        Socket sc = new Socket(host, port);
        sc.getOutputStream().write(header.getBytes());

        InputStream is = sc.getInputStream();

        File f = new File((new File(path)).getParent());
        if(!f.exists()) f.mkdirs();

        FileOutputStream fos = new FileOutputStream(path);

        byte buf[] = new byte[64*1024];
        int r = 1;
        while(r > 0)
        {
            r = is.read(buf);
            if(r > 0)
            {
                fos.write(buf, 0, r);
                if(r > 0) toClientChannel.write(buf, 0, r);
            }
        }
        fos.close();
        sc.close();
    }

    // вытаскивает из HTTP заголовка хост, порт соединения и путь до файла кэша,
    // после чего вызывает ф-ию загрузки из сети
    protected void from_net(String header) throws Exception
    {
        String host = extract(header, "Host:", "\n"), path = getPath(header);
        if((host == null)||(path == null))
        {
            printError("invalid request:\n"+header);
            return;
        }

        int port = host.indexOf(":",0);
        if(port < 0) port = 80;
        else
        {
            port = Integer.parseInt(host.substring(port+1));
            host = host.substring(0, port);
        }
        System.err.println("port = " + port + "\n host = " + host);
        from_net(header, host, port, path);
    }

    // загружает из кэша файл и выдаёт его
    // если во входящем HTTP заголовке стоит "Pragma: no-cache"
    // или такого файла в кэше нет, то вызывается ф-ия загрузки из сети
    protected void from_cache(String header) throws Exception
    {
        String path = getPath(header);
        if(path == null)
        {
            printError("invalid request:\n"+header);
            return;
        }

        // except "Pragma: no-cache"
        String pragma = extract(header, "Pragma:", "\n");
        System.err.println("pragma: " +  pragma);
        if(pragma != null)
        if(pragma.toLowerCase().equals("no-cache"))
        {
            from_net(header);
            return;
        }

        if((new File(path)).exists())
        {
            FileInputStream fis = new FileInputStream(path);
            byte buf[] = new byte[64*1024];
            int r = 1;

            while(r > 0)
            {
                r = fis.read(buf);
                if(r > 0) toClientChannel.write(buf, 0, r);
            }

            fis.close();
        }
        else
            from_net(header);
    }

    // обработка подключения "в потоке"
    // получает HTTP запрос от браузера
    // если запрос начинается с GET пытается взять файл из кэша
    // иначе - грузит из сети
    public void run()
    {
        try
        {
            fromClientChannel = connectionWithClient.getInputStream();
            toClientChannel = connectionWithClient.getOutputStream();


            byte buf[] = new byte[64*1024];
            int r = fromClientChannel.read(buf);

            String header = new String(buf, 0, r);
            //LOGGER.debug("CLIENTS REQUEST:\n" + header);
            System.err.println("CURRENT TIME IS: " + getCurrentTime() +"\nCLIENTS REQUEST: \n"  + header);
            if(header.indexOf("GET ", 0) == 0){
                System.err.println("From cashe \n");
                from_cache(header);
            }
            else {
                from_net(header);
                System.err.println("From net \n");
            }

            connectionWithClient.close();
        }
        catch(Exception e)
        {
            try
            {
                e.printStackTrace();
                printError("exception:\n"+e);
                connectionWithClient.close();
            }
            catch(Exception ex){}
        }
    }
    private String getCurrentTime() {
            Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            return sdf.format(cal.getTime());
        }
}
    

