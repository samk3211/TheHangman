package ClientPackage;

import ServerPackage.Room;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * KTH - ID2212, Homework1
 * GameLogic.java
 * Purpose: Communication with game server and game logic. 
 *
 * @author Fabrizio Demaria & Samia Khalid
 * @version 1.0 17/11/2014
 */
public class GameLogic implements Runnable {
    private static final String LOST_COMMAND = "LOST";
    private static final String YOUR_TURN = "UTURN";
    public static final String THEEND_INPUT = "**THEEND**";
    public static final String NEWWORD_INPUT = "**NEWWORD**";
    private static final String TSCORE_COMMAND = "TSCORE";
    private static final String TLEFT_COMMAND = "TLEFT";
    private static final String START_COMMAND = "START";
    private static final String FIRST_COMMAND = "FIRST";
    private static final String NEXT_COMMAND = "NEXT";
    private static final String WIN_COMMAND = "WIN";
    private static final String CONNECTION_ERROR_MESSAGE = "Connection error, try again";
    
    private Socket clientSocket;
    private final MyView myView;
    private BlockingQueue<String> bridge = null;
    private boolean gameOn;
    
     /**
     * Constructor of class GameLogic that set the necessary attributes for the 
     * game thread and it also handles the connection to the server
     *
     * @param host Host address of the server
     * @param port Server's port
     * @param bridge BlockingQueue for communication between GameLogic and MyView
     * @param myView Pointer to the GUI
     */
    public GameLogic(String host, int port, BlockingQueue bridge, MyView myView) throws Exception {
        this.myView = myView;
        this.bridge = bridge;
        this.gameOn = true;
        try {
            clientSocket = new Socket(host, port);
            
            myView.tryButton.setEnabled(true);
            myView.endButton.setEnabled(true);
        } catch (Exception e) {
            myView.printmered(CONNECTION_ERROR_MESSAGE);
            myView.tryAgain();
            
        }
    }
    
     /**
     * Once the connection is enstablished it initiates the game 
     */
    void gameFlow() throws InterruptedException {
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedOutputStream out = new BufferedOutputStream(clientSocket.getOutputStream());
            
            boolean matchOn;
            while(gameOn){
                
                out.write((START_COMMAND+"\n").getBytes());
                out.flush();
                String inpString;
                inpString = rd.readLine();
                
                if(!inpString.equals(FIRST_COMMAND)){
                    System.exit(1);
                }
                handleCommand(NEXT_COMMAND, rd);
                
                myView.tryButton.setEnabled(false);
                matchOn=true;
                
                while(matchOn){
                    inpString = rd.readLine();
                    if (!handleCommand(inpString, rd)) {
                        matchOn = false;
                        break;
                    }
                    //Handle keyboard input
                    String myin = bridge.take();
                    myView.tryButton.setEnabled(false);
                    myin = myin.concat("\n");
                    out.write(myin.getBytes());
                    out.flush();
                    
                    //Handle server's answer
                    inpString = rd.readLine();
                    if(!handleCommand(inpString, rd)){
                        matchOn = false;
                    }
                }
                
                
            }
            
            System.out.println("Closing connection");
            gameOverUI();
            clientSocket.close();
        } catch (IOException e) {
            gameOverUI();
            myView.printmered("Connection lost");
        }
    }
    
    
    @Override
    public void run() {
        BufferedReader rd = null;
            
            
            
        try {
            //Handle multiplayer
            
            String position;
            Room myrooms = null;
            rd = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedOutputStream out = new BufferedOutputStream(clientSocket.getOutputStream());
            out.write(("Mac" + "\n").getBytes());
            out.flush();
            ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
            try {
                myrooms = (Room)ois.readObject();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            final int totScore = Integer.parseInt(rd.readLine());
            final int totGames = Integer.parseInt(rd.readLine());
            out.write(new String("0" + "\n").getBytes());
            out.flush();
            try {
                String answer = rd.readLine();
                if (answer.equals("START")) {
                    
                }
                if (answer.equals("FULL")) {
                     myView.printmered("Room is full");
                }
                if (answer.equals("WAIT")) {
                    myView.printme("Waiting for opponent...");
                    if (rd.readLine().equals("START")){
                        
                    }
           
                }
                gameFlow();
            } catch (InterruptedException ex) {
                Logger.getLogger(GameLogic.class.getName()).log(Level.SEVERE, null, ex);
            }
        } catch (IOException ex) {
            Logger.getLogger(GameLogic.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                rd.close();
            } catch (IOException ex) {
                Logger.getLogger(GameLogic.class.getName()).log(Level.SEVERE, null, ex);
            }
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
    private boolean handleCommand(String command, BufferedReader rd) throws IOException, InterruptedException {
        
        String inpString;
        boolean matchOn = true;
        String myin;
        
        switch (command) {
            case WIN_COMMAND:
                inpString= rd.readLine();
                myView.tryButton.setEnabled(false);
                myView.printmegreen("You win - " + inpString);
                myView.newWord.setEnabled(true);
                inpString = rd.readLine();
                
                if(inpString.equals(TLEFT_COMMAND)){
                    myView.setTrials(rd.readLine());
                }
                inpString = rd.readLine();
                if(inpString.equals(TSCORE_COMMAND)){
                    myView.setScoreYou(rd.readLine());
                    myView.setScoreOpp(rd.readLine());
                    
                }
                myin = bridge.take();
                if(myin.equals(NEWWORD_INPUT)){
                    //myView.tryButton.setEnabled(true);
                    myView.printme("Waiting for opponent...");
                    matchOn=false;
                }
                if(myin.equals(THEEND_INPUT)){
                    gameOn=false;
                }
                break;
            case LOST_COMMAND:
                inpString= rd.readLine();
                myView.tryButton.setEnabled(false);
                myView.printmered("You lose - " + inpString);
                myView.newWord.setEnabled(true);
                inpString = rd.readLine();
                
                if(inpString.equals(TLEFT_COMMAND)){
                    myView.setTrials(rd.readLine());
                }
                inpString = rd.readLine();
                if(inpString.equals(TSCORE_COMMAND)){
                    myView.setScoreYou(rd.readLine());
                    myView.setScoreOpp(rd.readLine());
                }
                myin = bridge.take();
                if(myin.equals(NEWWORD_INPUT)){
                    //myView.tryButton.setEnabled(true);
                    myView.printme("Waiting for opponent...");
                    matchOn=false;
                }
                if(myin.equals(THEEND_INPUT)){
                    gameOn=false;
                }
                break;
            case NEXT_COMMAND:
                inpString = rd.readLine();
                myView.printme(inpString);
                break;
            case YOUR_TURN:
                inpString = rd.readLine();

                final String finalInpString8 = inpString;

                myView.tryButton.setEnabled(true);
                myView.printme(finalInpString8);
                break;
        }
        if(matchOn){
            inpString = rd.readLine();
            if(inpString.equals(TLEFT_COMMAND)){
                myView.setTrials(rd.readLine());
            }
            inpString = rd.readLine();
            if(inpString.equals(TSCORE_COMMAND)){
                myView.setScoreYou(rd.readLine());
                myView.setScoreOpp(rd.readLine());
            }
        }
        return matchOn;
    }
    
    private void gameOverUI(){
        myView.tryButton.setEnabled(false);
        myView.newWord.setEnabled(false);
        myView.endButton.setEnabled(false);
        myView.connectButton.setEnabled(true);
    }
}



