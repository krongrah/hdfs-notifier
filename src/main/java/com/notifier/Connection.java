package com.notifier;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class Connection{

    private Socket sock;
    private PrintStream ps;

    public Connection(Socket sock) {
        this.sock = sock;
        try {
            ps = new PrintStream(sock.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //return false upon timeout
    public boolean update(){
        ps.println("update");
        return true;
    }

}
