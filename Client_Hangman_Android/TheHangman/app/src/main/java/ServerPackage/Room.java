/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ServerPackage;

import java.io.Serializable;
import java.net.Socket;


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
            if((socket1[i]!=null && socket1[i].isClosed()) && (socket2[i]!=null && socket2[i].isClosed())){
                connected[i]=0;
                players1[i]="";
                players2[i]="";

            }
            if((socket1[i]!=null && socket1[i].isClosed()) && (socket2[i]!=null && !socket2[i].isClosed())){
                connected[i]=1;
                players1[i]="";

            }
            if((socket1[i]!=null && !socket1[i].isClosed()) && (socket2[i]!=null && socket2[i].isClosed())){
                connected[i]=1;
                players2[i]="";
            }
        }
    }

}
