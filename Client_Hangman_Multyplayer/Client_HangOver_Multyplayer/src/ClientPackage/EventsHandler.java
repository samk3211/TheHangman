package ClientPackage;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;

/**
 * KTH - ID2212, Homework1
 * EventHandler.java
 * Purpose: Listener for GUI events.
 *
 * @author Fabrizio Demaria & Samia Khalid
 * @version 1.0 17/11/2014
 */
public class EventsHandler implements ActionListener {
    
    private static final String NEW_WORD_CLICK = "New Word";
    private static final String TRY_CLICK = "Try";
    private static final String END_CLICK = "End";
    private static final String CONNECT_CLICK = "Connect";
    private static final String CONNECT_WARNING = "Connect to a server...";
    
    private final MyView myView;
    private BlockingQueue<String> bridge = null;
    
    public EventsHandler(MyView myview){
        this.myView = myview;
        bridge = new LinkedBlockingQueue<>(1);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = ((JButton) e.getSource()).getActionCommand();
        
        
        switch (command) {
            case CONNECT_CLICK:
                bridge.clear();
                myView.connectButton.setEnabled(false);
                int port = Integer.parseInt(myView.portField.getText());
                String host = myView.hostField.getText();
                
                GameLogic tmp;
                try {
                    tmp = new GameLogic(host,port,bridge,myView);
                    Thread tt = new Thread(tmp);
                    tt.start();
                    break;
                } catch (Exception ex) {
                    Logger.getLogger(EventsHandler.class.getName()).log(Level.SEVERE, null, ex);
                }
            
            case END_CLICK:
                bridge.offer(GameLogic.THEEND_INPUT);
                myView.printme(CONNECT_WARNING);
                myView.cancelMyWord();
                myView.connectButton.setEnabled(true);
                break;
            case TRY_CLICK:
                String tmpString = myView.insertWord.getText();
                myView.insertWord.setText("");
                bridge.offer(tmpString);
                break;
            case NEW_WORD_CLICK:
                bridge.offer(GameLogic.NEWWORD_INPUT);
                myView.newWord.setEnabled(false);
                break;
        }
    }
}
