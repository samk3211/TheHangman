package ServerPackage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * KTH - ID2212, Final Project
 * Room.java
 * Purpose: Maintain information about rooms status.
 *
 * @author Fabrizio Demaria & Samia Khalid
 * @version 1.0 05/01/2015
 */
public class Room implements Serializable {
    public int connected[];
    public String players1[];
    public String players2[];
    public transient Socket socket1[];
    public transient Socket socket2[];

    public Room(){
        connected = new int[10];
        players1 = new String[10];
        players2 = new String[10];
        socket1 = new Socket[10];
        socket2 = new Socket[10];

        for(int i = 0; i < 10; i++){
            connected[i] = 0;
            players1[i] = "";
            players2[i] = "";
        }
    }

    /**
     * Update the rooms informations when a new user connects
     *
     * @param i room index 
     * @param user client's username
     * @param s client socket
     */
    synchronized void updateRooms(int i, String user, Socket s) {
        connected[i]++;
        if(connected[i]==1){
            players1[i] = user;
            socket1[i] = s;
        } else if (connected[i]==2){
            players2[i] = user;
            socket2[i] = s;
        }


    }

    boolean canistart(int i) {
        return connected[i]==2;
    }

    
    /**
     * Update the rooms informations when a user needs to refresh the rooms list
     **/
    synchronized void refresh() {
        for(int i = 0; i < 10; i++){
            if(!isConn(socket1[i]) && !isConn(socket2[i])){
                connected[i]=0;
                players1[i]="";
                players2[i]="";
                
            }
            
            if((!isConn(socket1[i])) && (isConn(socket2[i]))){
                connected[i]=1;
                players1[i]="";
                
            }
            if((isConn(socket1[i])) && (!isConn(socket2[i]))){
                connected[i]=1;
                players2[i]="";
            }
        }
    }
    
    /**
     * Check if the socket has been closed by the client
     *
     * @param s socket to be tested
     * @return boolean true if the socket is still active
     */
    private boolean isConn(Socket s){
        if(s==null) return false;
        if(s.isClosed()) return false;
        
        try {
            BufferedOutputStream out = new BufferedOutputStream(s.getOutputStream());
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            //PrintWriter out2 = new PrintWriter(s.getOutputStream(), true);
            //out2.println("test\n");
            //if(out2.checkError()) return false;
            s.setSoTimeout(100);
            try{
            String tmp = in.readLine();
            if(tmp==null)return false;
            System.out.println(tmp);
            } catch (Exception e){
                s.setSoTimeout(0);
                return true;
            }
            
            
            //out.close();
            //out = new BufferedOutputStream(s.getOutputStream());
            //out.write(("test\n").getBytes());
            //out.flush();
            //out.close();
        } catch (IOException ex) {
            return false;
        }
        
        return true;
        
        
    }

}
