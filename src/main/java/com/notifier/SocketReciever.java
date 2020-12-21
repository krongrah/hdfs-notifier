package com.notifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SocketReciever extends Thread {

    ServerSocket servsock = null;
    private List<Connection> connections = new ArrayList();

    SocketReciever() {
        try {
            servsock = new ServerSocket(9001);



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void notifyAllUsers(){
        System.out.println("notifying users");
        for (Connection connection:connections) {
            System.out.println("notifying user");
            if(!connection.update()){
                connections.remove(connection);
            }
        }
    }


    @Override
    public void run() {
        while (true) {
            try {
                Socket sock=servsock.accept();


                InputStream in = sock.getInputStream();
                OutputStream out = sock.getOutputStream();
                Scanner s = new Scanner(in, "UTF-8");

                String data = s.useDelimiter("\\r\\n\\r\\n").next();
                Matcher get = Pattern.compile("^GET").matcher(data);

                if (get.find()) {
                    Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
                    match.find();
                    byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                            + "Connection: Upgrade\r\n"
                            + "Upgrade: websocket\r\n"
                            + "Sec-WebSocket-Accept: "
                            + Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")))
                            + "\r\n\r\n").getBytes("UTF-8");
                    out.write(response, 0, response.length);

                    connections.add(new Connection(sock));
                    System.out.println("new connection");
                }
        } catch (NoSuchAlgorithmException | IOException e) {
                e.printStackTrace();
            }
        }
}
}
