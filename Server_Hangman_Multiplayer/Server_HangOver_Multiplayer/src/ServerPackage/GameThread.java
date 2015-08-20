package ServerPackage;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;

/**
 * KTH - ID2212, Final Project
 * GameThread.java
 * Purpose: Handling a single game flow (two players).
 *
 * @author Fabrizio Demaria & Samia Khalid
 * @version 1.0 05/01/2015
 **/
public class GameThread implements Runnable {
    
    private static final String TOTAL_SCORE_MESSAGE = "TSCORE\n";
    private static final String TRIALS_LEFT_MESSAGE = "TLEFT\n";
    private static final String YOUR_TURN = "UTURN\n";
    private static final String WIN_MESSAGE = "WIN\n";
    private static final String NEXT_MESSAGE = "NEXT\n";
    private static final String LOST_MESSAGE = "LOST\n";
    private static final String FIRST_MESSAGE = "FIRST\n";
    private static final String START_MESSAGE = "START";
    private static final String MYDICTIONARY = "mydictionary";
    
   private EntityManagerFactory emFactory;
    
    private final Socket clientSocket1;
    private final Socket clientSocket2;
    private String myWord;
    private String myWordSoFar;
    private int trialsLeft;
    private int totScore1;
    private int totScore2;
    private String username1;
    private String username2;
    
    
    public GameThread(Socket s1, Socket s2, String username1, String username2){
        System.err.println("" + s1.toString() + "Client connected, starting new thread");
        System.err.println("" + s2.toString() + "Client connected, starting new thread");
        clientSocket1 =  s1;
        clientSocket2 =  s2;
        trialsLeft = 6;
        totScore1 = 0;
        totScore2 = 0;
        this.username1 = username1;
        this.username2 = username2;
        
                
        emFactory = Persistence.createEntityManagerFactory("hangman");
    }
    
    /**
     * Picks up a random word from a file containing a word per line
     *
     * @param f File to be used for choosing a word
     *
     * @return Random word is returned in String format
     * @throws java.io.FileNotFoundException
     */
    public static String choose(File f) throws FileNotFoundException
    {
        String result = null;
        Random rand = new Random();
        int n = 0;
        for(Scanner sc = new Scanner(f); sc.hasNext(); )
        {
            ++n;
            String line = sc.nextLine();
            if(rand.nextInt(n) == 0)
                result = line;
        }
        
        return result;
    }
    
    @Override
    public void run() {
        try {
            
            BufferedReader in1 = new BufferedReader(new InputStreamReader(clientSocket1.getInputStream()));
            BufferedOutputStream out1 = new BufferedOutputStream(clientSocket1.getOutputStream());
            BufferedReader in2 = new BufferedReader(new InputStreamReader(clientSocket2.getInputStream()));
            BufferedOutputStream out2 = new BufferedOutputStream(clientSocket2.getOutputStream());
            // read the file name from the socket connection
            boolean gameon;
            
            while(true){
                String firstw = in1.readLine();
                String firstw2 = in2.readLine();
                File file = new File(MYDICTIONARY);
                
                if(!firstw.equals(START_MESSAGE) || !firstw2.equals(START_MESSAGE)) {
                    clientSocket1.close();
                    clientSocket2.close();
                    break;
                }
                
                myWord = choose(file);
                System.out.println(myWord);
                
                gameon=true;
                trialsLeft=6;
                
                int len = myWord.length();
                myWordSoFar = new String();
                for(int t = 0; t< len ; t++){
                    myWordSoFar = myWordSoFar.concat("-");
                }
                
                answerClient(FIRST_MESSAGE,myWordSoFar,out1,1);
                answerClient(FIRST_MESSAGE,myWordSoFar,out2,2);
                
                while(gameon){
                    //Reading word or letter from Client
                    
                    //PLAYER1
                    answerClient(YOUR_TURN,myWordSoFar,out1,1);
                    String recString = in1.readLine();
                    
                    if(recString.length()==1){
                        //Dealing with LETTER input
                        processLetter(recString);
                        gameon = doNextMove(myWordSoFar, out1, out2, false, 1, 2);
                        
                    } else {
                        //Dealing with WORD input
                        gameon = doNextMove(recString, out1, out2, true, 1,2);
                    }
                    
                    if(gameon==false)break;
                    //PLAYER2
                    answerClient(YOUR_TURN,myWordSoFar,out2,2);
                    recString = in2.readLine();
                    
                    if(recString.length()==1){
                        //Dealing with LETTER input
                        processLetter(recString);
                        gameon = doNextMove(myWordSoFar, out2, out1, false, 2,1);
                    } else {
                        //Dealing with WORD input
                        gameon = doNextMove(recString, out2, out1, true, 2,1);
                    }
                }
            }
            //Thread.currentThread().yield();
        } catch (Exception e) {
            System.err.println("" + this.clientSocket1.toString() + "Peer disconnected, closing thread");
            System.err.println("" + this.clientSocket2.toString() + "Peer disconnected, closing thread");
            try {
                clientSocket1.close();
            } catch (IOException ex) {
                Logger.getLogger(GameThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Gets the letter received from the client and performs appropriate actions
     * according to whether it has been correctly guessed or not
     *
     * @param recString The letter from the client in String format
     * @retrun Boolean value indicating if the letter has been found or not
     */
    private boolean processLetter(String recString) {
        int len = myWord.length();
        boolean thereis = false;
        String myWordSoFarTemp = "";
        
        for(int t = 0; t<len ; t++){
            if(myWord.toLowerCase().charAt(t)==recString.toLowerCase().charAt(0)){
                thereis = true;
                myWordSoFarTemp = myWordSoFarTemp.concat(("" + myWord.charAt(t)));
            } else if (myWordSoFar.charAt(t)!='-') {
                myWordSoFarTemp = myWordSoFarTemp.concat(("" + myWordSoFar.charAt(t)));
            } else {
                myWordSoFarTemp = myWordSoFarTemp.concat("-");
            }
        }
        if(!thereis) trialsLeft--;
        myWordSoFar = myWordSoFarTemp;
        return thereis;
    }
    
    /**
     * Send answer to the client according to the result of the previous move
     *
     * @param nextCommand command to be sent to the client according to adopted
     *                    communication protocol
     * @param displayWord string containing the updated word to be displayed on client's GUI
     * @param out buffer connected to the socket for Server-Client communication
     */
    private void answerClient(String nextCommand, String displayWord, BufferedOutputStream out, int playerNum) throws IOException {
        //Send message about the word
        out.write(nextCommand.getBytes());
        out.flush();
        out.write((displayWord + "\n").getBytes());
        out.flush();
        //Information about score and trials
        out.write(TRIALS_LEFT_MESSAGE.getBytes());
        out.flush();
        out.write(("" + trialsLeft + "\n").getBytes());
        out.flush();
        out.write(TOTAL_SCORE_MESSAGE.getBytes());
        out.flush();
        if(playerNum==1){
            
            out.write(("" + totScore1 + "\n").getBytes());
            out.flush();
            out.write(("" + totScore2 + "\n").getBytes());
            out.flush();
        } else if (playerNum==2) {
            out.write(("" + totScore2 + "\n").getBytes());
            out.flush();
            out.write(("" + totScore1 + "\n").getBytes());
            out.flush();
        }
    }
    
    /**
     * Updates the current score and trials left and
     * accordingly it passes the appropriate parameters to the method used for sending
     * messages to the client
     *
     * @param compareString the updated string to be compared with the one to be guessed
     * @param out buffer connected to the socket for Server-Client communication
     * @param isWord to indicate if the client has sent a single letter or a word
     *
     * @return boolean to indicate if the game is over after previous move
     */
    private boolean doNextMove(String compareString, BufferedOutputStream playing, BufferedOutputStream opponent, boolean isWord, int playerNum, int opponentNum) throws IOException{
        boolean gameon = true;
        if(trialsLeft==1 && !compareString.equals(myWord)){
            //giocatore perde
            
            totScore1--;
            totScore2--;
            trialsLeft--;
            decUpdateDB(username1);
            decUpdateDB(username2);
            answerClient(LOST_MESSAGE,myWord,playing,playerNum);
            answerClient(LOST_MESSAGE,myWord,opponent,opponentNum);
            gameon=false;
        } else if (trialsLeft!=0 && !compareString.equals(myWord)) {
            if(isWord) trialsLeft--;
            answerClient(NEXT_MESSAGE,myWordSoFar,playing,playerNum);
        } else {
            if(playerNum==1){
                incUpdateDB(username1);
                decUpdateDB(username2);
                totScore1++;
            } else if(playerNum==2){
                decUpdateDB(username1);
                incUpdateDB(username2);
                totScore2++;
            }
            answerClient(WIN_MESSAGE,myWord,playing,playerNum);
            answerClient(LOST_MESSAGE,myWord,opponent,opponentNum);
            gameon=false;
        }
        if(trialsLeft==0)gameon=false;
        return gameon;
    }
    
    private EntityManager beginTransaction()
    {
        EntityManager em = emFactory.createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        transaction.begin();
        return em;
    }

    private void commitTransaction(EntityManager em)
    {
        em.getTransaction().commit();
    }

    /**
     * Update the database with the result of the match
     *
     * @param username used to lookup the corresponding entry in the DB
     */
    private void decUpdateDB(String username) {
        EntityManager em = null;
            em = beginTransaction();
            getAccount(username, em).decScore();
            getAccount(username, em).incGames();
            commitTransaction(em);
    }
    
    
    /**
     * Update the database with the result of the match
     *
     * @param username used to lookup the corresponding entry in the DB
     */
    private void incUpdateDB(String username) {
        EntityManager em = null;
            em = beginTransaction();
            getAccount(username, em).incScore();
            getAccount(username, em).incGames();
            commitTransaction(em);
    }
    

    public Account getAccount(String ownerName, EntityManager em)
    {
        if (ownerName == null)
        {
            return null;
        }

        try
        {
            return (Account) em.createNamedQuery("findAccountWithName").
                    setParameter("ownerName", ownerName).getSingleResult();
        } catch (NoResultException noSuchAccount)
        {
            return null;
        }
    }
}
