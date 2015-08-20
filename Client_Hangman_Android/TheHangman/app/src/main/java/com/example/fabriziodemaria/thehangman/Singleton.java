package com.example.fabriziodemaria.thehangover;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class Singleton {
    private static Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private boolean logged;
    private static Singleton instance;
    private String information;
    static PrintWriter out;

    private Singleton()
    {
    }

    public static Singleton getInstance(String ip, int port)
    {
        if(instance == null || socket.isClosed())
            initSingleton(ip, port);

        return instance;
    }

    public static void destroySocket()
    {
        try {
            //socket.shutdownInput();
            //socket.shutdownOutput();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initSingleton(String ip, int port)
    {
        instance = new Singleton();

        try
        {
            socket = new Socket(ip, port);
        }
        catch (IOException e)
        {
            System.err.println("Failed creating new socket.");
        }
        /*
        try
        {
            out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())),
                    true);
        }
        catch (IOException e)
        {
            System.err.println("EPIC FAIL HERE");
        }
        */
    }

    public Socket getSocket()
    {
        return socket;
    }


    public PrintWriter getOutput()
    {
        return out;
    }

}
