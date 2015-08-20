package com.example.fabriziodemaria.thehangover;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import ServerPackage.Room;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;

/**
 * KTH - ID2212, Project 6
 * RoomSelectionNet.java
 * Purpose: Handle TCP communication in room selection.
 *
 * @author Fabrizio Demaria & Samia Khalid
 * @version 1.0 05/01/2015
 **/
public class RoomSelectionNet implements Runnable {

    String username;
    Room myrooms = null;
    RoomSelection myRoomSelectionRef;
    private BlockingQueue<String> bridge = null;

    public RoomSelectionNet(String username, RoomSelection myRoomSelectionRef, BlockingQueue<String> bridge) {
        this.username = username;
        this.myRoomSelectionRef = myRoomSelectionRef;
        this.bridge = bridge;
    }

    @Override
    public void run() {
        try {
            String position;
            final BufferedReader rd = new BufferedReader(new InputStreamReader(Singleton.getInstance("",0).getSocket().getInputStream()));
            BufferedOutputStream out = new BufferedOutputStream(Singleton.getInstance("",0).getSocket().getOutputStream());
            out.write((username + "\n").getBytes());
            out.flush();
            ObjectInputStream ois = new ObjectInputStream(Singleton.getInstance("",0).getSocket().getInputStream());
            try {
                myrooms = (Room)ois.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            final int totScore = Integer.parseInt(rd.readLine());

            final int totGames = Integer.parseInt(rd.readLine());
            //update table
            ((Activity)myRoomSelectionRef).runOnUiThread(new Runnable() {
                public void run() {
                    myRoomSelectionRef.updateTable(myrooms);
                    myRoomSelectionRef.updateWelcomeMessage(totScore,totGames);

                }
            });

            while(true) {
            position = bridge.take();
            out.write(new String(position + "\n").getBytes());
            out.flush();
                String answer = rd.readLine();
                if (answer.equals("START")) {
                    myRoomSelectionRef.startGame();
                    break;
                }
                if (answer.equals("FULL")) {

                }
                if (answer.equals("WAIT")) {
                    ((Activity)myRoomSelectionRef).runOnUiThread(new Runnable() {
                        public void run() {
                            myRoomSelectionRef.startSpinner();

                        }
                    });
                    if (rd.readLine().equals("START")){
                        myRoomSelectionRef.startGame();
                    }
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
