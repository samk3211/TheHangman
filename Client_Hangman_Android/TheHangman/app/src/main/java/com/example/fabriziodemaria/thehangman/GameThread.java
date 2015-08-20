package com.example.fabriziodemaria.thehangover;

import android.app.Activity;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.lang.Thread;

/**
 * KTH - ID2212, Project 6
 * GameThread.java
 * Purpose: Handle TCP communication and game flow in multiplayer match
 *
 * @author Fabrizio Demaria & Samia Khalid
 * @version 1.0 05/01/2015
 **/
public class GameThread implements Runnable {

    private static final String LOST_COMMAND = "LOST";
    public static final String THEEND_INPUT = "**THEEND**";
    public static final String NEWWORD_INPUT = "**NEWWORD**";
    private static final String YOUR_TURN = "UTURN";
    private static final String TSCORE_COMMAND = "TSCORE";
    private static final String TLEFT_COMMAND = "TLEFT";
    private static final String START_COMMAND = "START";
    private static final String FIRST_COMMAND = "FIRST";
    private static final String NEXT_COMMAND = "NEXT";
    private static final String WIN_COMMAND = "WIN";

    GameOn myView;
    String inpString;
    private BlockingQueue<String> bridge = null;
    private boolean gameOn;

    public GameThread(GameOn v, BlockingQueue<String> bridge) {
        myView = v;
        this.bridge = bridge;
    }

    void gameFlow() throws InterruptedException, IOException {
        final StringBuilder test = new StringBuilder();
        try{
            final BufferedReader rd = new BufferedReader(new InputStreamReader(Singleton.getInstance("",0).getSocket().getInputStream()));
            BufferedOutputStream out = new BufferedOutputStream(Singleton.getInstance("",0).getSocket().getOutputStream());
            gameOn = true;
            boolean matchOn;
            matchOn = true;
            while(gameOn) {
                out.write((START_COMMAND + "\n").getBytes());
                out.flush();


                Thread tmptr = new Thread(new Runnable() {
                    public void run() {
                        try {
                            inpString = rd.readLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

                tmptr.start();
                tmptr.join();

                if (!inpString.equals(FIRST_COMMAND)) {
                    System.exit(1);
                }
                handleCommand(NEXT_COMMAND, rd);

                ((Activity)myView).runOnUiThread(new Runnable() {
                    public void run() {
                        myView.setNewWord(false);
                        myView.setTry(false);

                    }
                });

                matchOn = true;

                while (matchOn) {
                    inpString = rd.readLine();
                    if (!handleCommand(inpString, rd)) {
                        matchOn = false;
                        break;
                    }
                    //Handle keyboard input
                    String myin = bridge.take();

                    ((Activity)myView).runOnUiThread(new Runnable() {
                        public void run() {
                            myView.setTry(false);

                        }
                    });
                    myin = myin.concat("\n");
                    out.write(myin.getBytes());
                    out.flush();

                    //Handle server's answer
                    inpString = rd.readLine();
                    if (!handleCommand(inpString, rd)) {
                        matchOn = false;
                    }
                }
            }
            System.out.println("Closing connection");
            ((Activity)myView).runOnUiThread(new Runnable() {
                public void run() {
                    gameOverUI();
                }
            });

            Singleton.getInstance("",0).destroySocket();
        } catch(Exception e){
            ((Activity)myView).runOnUiThread(new Runnable() {
                public void run() {
                    gameOverUI();
                    myView.updateTextRed("Connection Lost");

                }
            });

        }
    }

    /**
     * Checks the server incoming messages and updates the GUI according to the
     * type.
     *
     * @param command Message received from the server
     * @param rd Buffer connected to TCP socket for Client-Server communication
     *
     * @return Boolean value indicating if the match is over
     */
    private boolean handleCommand(String command, final BufferedReader rd) throws IOException, InterruptedException {

        String inpString;
        boolean matchOn = true;
        String myin;

        switch (command) {
            case WIN_COMMAND:
                inpString= rd.readLine();

                final String finalInpString3 = inpString;
                ((Activity)myView).runOnUiThread(new Runnable() {
                    public void run() {
                        //myView.updateText("TESTWIN");
                        myView.setTry(false);
                        myView.updateTextGreen("You win-" + finalInpString3);
                        myView.setNewWord(true);
                    }
                });
                inpString = rd.readLine();

                if(inpString.equals(TLEFT_COMMAND)){
                    final String finalString4 = rd.readLine();

                    ((Activity)myView).runOnUiThread(new Runnable() {
                        public void run() {
                            myView.updateTrials(finalString4);

                        }
                    });

                }
                inpString = rd.readLine();
                if(inpString.equals(TSCORE_COMMAND)){
                    final String finalString5 = rd.readLine();
                    final String finalString6 = rd.readLine();

                    ((Activity)myView).runOnUiThread(new Runnable() {
                        public void run() {
                            myView.updateScore1(finalString5);
                            myView.updateScore2(finalString6);

                        }
                    });

                }
                myin = bridge.take();
                if(myin.equals(NEWWORD_INPUT)){
                    /*
                    ((Activity)myView).runOnUiThread(new Runnable() {
                        public void run() {

                            myView.setTry(true);
                        }
                    });
                    */
                    ((Activity)myView).runOnUiThread(new Runnable() {
                        public void run() {
                            myView.updateText("Waiting for opponent...");
                        }
                    });

                    matchOn=false;
                }
                if(myin.equals(THEEND_INPUT)){
                    gameOn=false;
                }
                break;
            case LOST_COMMAND:
                final String finalString6= rd.readLine();
                ((Activity)myView).runOnUiThread(new Runnable() {
                    public void run() {
                        myView.setTry(false);
                        myView.updateTextRed("You lose - " + finalString6);
                        myView.setNewWord(true);
                    }
                });

                inpString = rd.readLine();

                if(inpString.equals(TLEFT_COMMAND)){
                    final String finalString7= rd.readLine();
                    ((Activity)myView).runOnUiThread(new Runnable() {
                        public void run() {
                            myView.updateTrials(finalString7);
                        }
                    });

                }
                inpString = rd.readLine();
                if(inpString.equals(TSCORE_COMMAND)){
                    final String finalString8= rd.readLine();
                    final String finalString9= rd.readLine();
                    ((Activity)myView).runOnUiThread(new Runnable() {
                        public void run() {

                            myView.updateScore1(finalString8);
                            myView.updateScore2(finalString9);
                        }
                    });

                }
                myin = bridge.take();
                if(myin.equals(NEWWORD_INPUT)){
                    /*
                    ((Activity)myView).runOnUiThread(new Runnable() {
                        public void run() {
                            myView.setTry(true);
                        }
                    });
*/
                    ((Activity)myView).runOnUiThread(new Runnable() {
                        public void run() {
                            myView.updateText("Waiting for opponent...");
                        }
                    });
                    matchOn=false;
                }
                if(myin.equals(THEEND_INPUT)){
                    gameOn=false;
                }
                break;
            case NEXT_COMMAND:
                inpString = rd.readLine();
                final String finalInpString = inpString;


                ((Activity)myView).runOnUiThread(new Runnable() {
                    public void run() {
                       myView.updateText(finalInpString);
                    }
                });

                break;
            case YOUR_TURN:
                inpString = rd.readLine();

                final String finalInpString8 = inpString;


                ((Activity)myView).runOnUiThread(new Runnable() {
                    public void run() {
                        myView.updateText(finalInpString8);
                        myView.setTry(true);
                    }
                });

                break;
        }
        if(matchOn){
            inpString = rd.readLine();
            if(inpString.equals(TLEFT_COMMAND)){
                inpString = rd.readLine();
                //trialsdisplayed.setText(rd.readLine());
                final String finalInpString1 = inpString;
                ((Activity)myView).runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            myView.updateTrials(finalInpString1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            inpString = rd.readLine();
            if(inpString.equals(TSCORE_COMMAND)){
                inpString = rd.readLine();
                final String finalInpString2 = inpString;
                inpString = rd.readLine();
                final String finalInpString3 = inpString;

                ((Activity)myView).runOnUiThread(new Runnable() {
                    public void run() {
                        try {
                            myView.updateScore1(finalInpString2);
                            myView.updateScore2(finalInpString3);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        }
        return matchOn;
    }


    private void gameOverUI(){
        myView.setNewWord(false);
        myView.setTry(false);
    }

    @Override
    public void run() {
        try {
            gameFlow();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
