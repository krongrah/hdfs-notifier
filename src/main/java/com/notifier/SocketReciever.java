package com.notifier;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

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
        for (Connection connection:connections) {
            if(!connection.update()){
                connections.remove(connection);
            }
        }
    }


    @Override
    public void run() {
        while (true) {
            try {
                connections.add(new Connection(servsock.accept()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
