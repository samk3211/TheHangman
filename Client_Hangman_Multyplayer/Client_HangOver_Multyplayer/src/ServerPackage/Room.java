/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ServerPackage;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


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
    
    private boolean isConn(Socket s){
        if(s==null) return false;
        if(s.isClosed()) return false;
        
        //try {
            //BufferedOutputStream out = new BufferedOutputStream(s.getOutputStream());
            //PrintWriter out2 = new PrintWriter(s.getOutputStream(), true);
            //out2.println("test\n");
            //if(out2.checkError()) return false;
            
            //out.close();
            //out = new BufferedOutputStream(s.getOutputStream());
            //out.write(("test\n").getBytes());
            //out.flush();
            //out.close();
        //} catch (IOException ex) {
        //    return false;
        //}
        
        return true;
        
        
    }

}
