package ServerPackage;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;

/**
 * KTH - ID2212, Final Project
 * PreGame.java
 * Purpose: Handle room information and pre-game phase.
 *
 * @author Fabrizio Demaria & Samia Khalid
 * @version 1.0 05/01/2015
 **/
public class PreGame implements Runnable {
    private EntityManagerFactory emFactory;
    private final Socket clientSocket;
    Room myRooms;
    String username;
    
    public PreGame(Socket s, Room myRooms){
        clientSocket = s;
        this.myRooms = myRooms;
        emFactory = Persistence.createEntityManagerFactory("hangman");
    }
    
    @Override
    public void run(){
        try {
            BufferedReader in;
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedOutputStream out = new BufferedOutputStream(clientSocket.getOutputStream());
            String username = in.readLine();
            
            //DB management
            newAccount(username);
            EntityManager em = null;
            em = beginTransaction();
            Account myAcc = getAccount(username, em);
            commitTransaction(em);
            
            int totScore = myAcc.getScore();
            int totGames = myAcc.getTotgames();
            
            
            
            
            ObjectOutputStream  oos;
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            
            //REFRESH ROOMS
            myRooms.refresh();
            oos.writeObject(myRooms);
            oos.flush();
            System.out.println("Rooms sent");
            
            out.write((new String("" + totScore) + "\n").getBytes());
            out.flush();
            out.write((new String("" + totGames) + "\n").getBytes());
            out.flush();
            while(true){
            String commandin = in.readLine();
            //riceve indice
            int i = Integer.parseInt(commandin);
            
            if(myRooms.connected[i]==2){
                out.write(("FULL" + "\n").getBytes());
                out.flush();
                continue;
            }
            
            myRooms.updateRooms(i, username, clientSocket);
            
            if(myRooms.canistart(i)){
                //player2 output
                out.write(("START" + "\n").getBytes());
                out.flush();
                //player1 output
                BufferedOutputStream outpl1 = new BufferedOutputStream(myRooms.socket1[i].getOutputStream());
                outpl1.write(("START" + "\n").getBytes());
                outpl1.flush();
                
                GameThread tmp = new GameThread(myRooms.socket1[i], myRooms.socket2[i], myRooms.players1[i], myRooms.players2[i]);
                Thread nt = new Thread(tmp);
                nt.start();
                break;
            } else {
                
                out.write(("WAIT" + "\n").getBytes());
                out.flush();
                break;
            }
         }
            
        } catch (IOException ex) {
            Logger.getLogger(PreGame.class.getName()).log(Level.SEVERE, null, ex);
        }
        
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
    
    
    
    public Account newAccount(String name)
    {
        EntityManager em = null;
        try
        {
            em = beginTransaction();
            List<Account> existingAccounts = em.createNamedQuery("findAccountWithName", Account.class).
                    setParameter("ownerName", name).getResultList();
            if (existingAccounts.size() != 0)
            {
                // account exists, can not be created.
                return existingAccounts.get(0);
            }

            // create account.
            Account account = new Account(name);
            em.persist(account);
            return account;
        } finally
        {
            commitTransaction(em);
        }
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
    
}
