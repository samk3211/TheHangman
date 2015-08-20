package ServerPackage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * KTH - ID2212, Final Project
 * MainServer.java
 * Purpose: Accept new client connections.
 *
 * @author Fabrizio Demaria & Samia Khalid
 * @version 1.0 05/01/2015
 **/
public class MainServer {
    
    private  static final String USAGE = "java BasicRCatServer [port]";
    private ServerSocket serverSocket;
    private Room myRooms;

    
    
    public MainServer() throws IOException {
        this(8080);
    }
    
    public MainServer(int port) throws IOException {
        serverSocket = new ServerSocket(port);
    }
    
    /**
     * Loop for continuosly listening to the serverSocket and starting a new 
     * PreGame thread when a client is requesting a game.
     */
    public void serve() {
        myRooms = new Room();
        
        while(true) {
            try {
                Socket clientSocket = serverSocket.accept();
                
                //System.out.println("Conn");
                PreGame tmp = new PreGame(clientSocket, myRooms);
                Thread nt = new Thread(tmp);
                nt.start();
            } catch (IOException e) {
                System.err.println("Problem in starting a new client connection");
            }
        }
    }
    
    

    
    
    /** The main entry to server application.
     *  USAGE: java MainServer <port>
     *  The main method takes 0 or 1 argument. If no arguments it assigned
     * a default port to the server, otherwise <port> is set. After this it calls
     * the method for accepting connections from clients
     *
     * @param args
     */
    public static void main(String[] args) throws IOException {
        if (args.length > 0 &&
                (args[0].equalsIgnoreCase("-h") || args[0].equalsIgnoreCase("-help"))) {
            
            System.out.println(USAGE);
            System.exit(1);
        }
        
        MainServer server;
        String portNoString = null;
        
        
        try {
            if (args.length > 0) {
                portNoString = args[0];
                int port = Integer.parseInt(portNoString);
                server = new MainServer(port);
            } else {
                server = new MainServer();
            }
            server.serve();
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number: " + portNoString + ".");
            System.exit(1);
        }
    }
}