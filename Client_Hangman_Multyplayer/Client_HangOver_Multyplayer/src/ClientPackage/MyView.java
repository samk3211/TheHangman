package ClientPackage;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * KTH - ID2212, Homework1
 * MyView.java
 * Purpose: GUI for the client.
 *
 * @author Fabrizio Demaria & Samia Khalid
 * @version 1.0 17/11/2014
 */
public class MyView extends java.applet.Applet {
    
    private static final String USAGE = "java MyView [host] [port] ";
    private static final String FIRST_PNG = "6.png";
    
    private int port = 8080;
    private String host = "localhost";
    JTextField hostField;
    JTextField portField;
    private boolean applet = true;
    private JLabel wordLabel;
    private Image image;
    JTextField insertWord = null;
    JButton connectButton = new JButton("Connect");
    JButton tryButton = new JButton("Try");
    JButton newWord = new JButton("New Word");
    JButton endButton = new JButton("End");
    private String myWord;
    private JLabel trialsLeft;
    private JLabel totScoreYou;
    private JLabel totScoreOpp;
    private JPanel counterPanel;
    
    /**
     * An applet must have a no-argument constructor.
     */
    public MyView(){};
    
    public MyView(String host, int port, boolean applet) {
        this.host = host;
        this.port = port;
        this.applet = applet;
    }
    
    public String getMyWord(){
        return myWord;
    }
    
    /**
     * Takes the image file name and loads the image attribute of the class MyView
     *
     * @param myimage name of the image file present in the source directory
     *                containinf the desired image to be loaded on screen
     */
    private void loadImage(String myimage) {
        
        MediaTracker tracker = new MediaTracker(this);
        image = Toolkit.getDefaultToolkit().getImage(myimage);
        tracker.addImage(image, 0);
        try
        {
            tracker.waitForID(0);
        }
        catch(InterruptedException ie)
        {
            System.out.println(ie.getMessage());
        }
    }
    
    
    /** init() is invoked when the applet is initialised
     *  either by a browser's JVM or explicitely by the application
     *  from the main method */
    @Override
    public void init() {
        if (applet) {
            host = getParameter("Host");
            if (host == null) {
                host = getCodeBase().getHost();
            }
            if (host == null || host.equals("")) {
                host = "localhost";
            }
            try {
                port = Integer.parseInt(getParameter("Port"));
            }
            catch (NumberFormatException e) {
                port = getCodeBase().getPort();
                if (port <= 0) {
                    port = 8080;
                }
            }
        }
        
        EventsHandler myController = new EventsHandler(this);
        this.setPreferredSize(new Dimension(500, 650));
        
        JPanel commandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        commandPanel.add(new Label("Host: "));
        hostField = new JTextField(host, 15);
        //hostField.addActionListener(MakeTCPConnection.this);
        commandPanel.add(hostField);
        
        commandPanel.add(new JLabel("Port: "));
        portField = new JTextField(String.valueOf(port), 4);
        //portField.addActionListener(MakeTCPConnection.this);
        commandPanel.add(portField);
        
        
        commandPanel.add(connectButton);
        connectButton.addActionListener(myController);
        
        
        setLayout(new BorderLayout());
        add(BorderLayout.NORTH,commandPanel);
        
        //Playing panel
        wordLabel = new JLabel();
        wordLabel.setFont(new Font(wordLabel.getFont().getName(), Font.BOLD, 16));
        wordLabel.setText("Connect to server...");
        wordLabel.setHorizontalAlignment(JTextField.CENTER);
        
        trialsLeft = new JLabel();
        trialsLeft.setText("Trials left: 6");
        
        totScoreYou = new JLabel();
        totScoreYou.setText("Your score: 0");
        totScoreOpp = new JLabel();
        totScoreOpp.setText("Opponent score: 0");
        
        newWord.setEnabled(false);
        newWord.addActionListener(myController);
        
        tryButton.setEnabled(false);
        tryButton.addActionListener(myController);
        
        endButton.setEnabled(false);
        endButton.addActionListener(myController);
        
        insertWord = new JTextField();
        insertWord.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if(e.getKeyCode() == KeyEvent.VK_ENTER)
                    tryButton.doClick();
            }
        });
        
        JPanel playPanel = new JPanel();
        GroupLayout layout = new GroupLayout(playPanel);
        playPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(wordLabel)
                        .addComponent(insertWord)
                        //.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                        .addComponent(tryButton)
                        .addComponent(newWord)
                        .addComponent(endButton)
                        .addComponent(trialsLeft)
                        .addComponent(totScoreYou)
                        .addComponent(totScoreOpp))
        );
        layout.setVerticalGroup(layout.createSequentialGroup()
                //.addGroup(layout.createParallelGroup()
                .addComponent(wordLabel)
                //.addGroup(layout.createParallelGroup()
                .addComponent(insertWord)
                //.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
                .addComponent(tryButton)
                .addComponent(newWord)
                .addComponent(endButton)
                .addComponent(trialsLeft)
                .addComponent(totScoreYou)
                .addComponent(totScoreOpp)
        );
        
        add(BorderLayout.CENTER,playPanel);
        loadImage(FIRST_PNG);
        counterPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image,0,0, this);
            }
        };
        
        counterPanel.setPreferredSize(new Dimension(500, 375));
        counterPanel.setBackground(Color.LIGHT_GRAY);
        counterPanel.setForeground(Color.BLUE);
        add(BorderLayout.SOUTH, counterPanel);
        endButton.setVisible(false);
        
    } // end of init
    
    
    
    public void printme(String str){
        wordLabel.setText("");
        wordLabel.setText(str);
        wordLabel.setForeground(Color.black);
        wordLabel.update(wordLabel.getGraphics());
        
    }
    
    public void printmered(String str){
        wordLabel.setText("");
        wordLabel.setText(str);
        wordLabel.setForeground(Color.red);
        wordLabel.update(wordLabel.getGraphics());
        
    }
    
    public void printmegreen(String str){
        wordLabel.setText("");
        wordLabel.setText(str);
        wordLabel.setForeground(new Color(57,171,67));
        wordLabel.update(wordLabel.getGraphics());
        
    }
    
    public void tryAgain(){
        connectButton.setEnabled(true);
    }
    
    
    public void setImage(int left){
        String tmp = ""+ left + ".png";
        loadImage(tmp);
        repaint();
        
    }
    
    public void cancelMyWord(){
        myWord = null;
    }
    
    public void setTrials(String str){
        trialsLeft.setText("Trials left: " + str);
        int imnum = new Integer(str);
        setImage(imnum);
        
    }
    
    public void setScoreYou(String str){
        totScoreYou.setText("Your score: " + str);
        
    }
    
    public void setScoreOpp(String str){
     totScoreOpp.setText("Opponent score: " + str);
           
    }
    
    
    /** The main entry to client application.
     *  USAGE: java MyView <host> <port>
     *  The main method parses command line arguments as initial suggestions
     *  to port and host fields, creates a frame, instantiates the applet MyView,
     *  calls the applet's init method and adds the applet to the frame,
     *  then it packs the frame and makes it visible
     * @param args
     */
    public static void main(String[] args) {
        // make a frame
        JFrame frame = new JFrame();
        // exit on closing
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // instantiate the applet
        // parse command line arguments (host, port), if any
        String host = "localhost";
        if (args.length > 0) {
            host = args[0];
        }
        // print USAGE if asked for help
        if (host.equalsIgnoreCase("-h") || host.equalsIgnoreCase("-help")) {
            System.out.println(USAGE);
            System.exit(1);
        }
        
        int port = 8080;
        if (args.length > 1)
            try {
                port = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException e) {
                System.err.println(USAGE);
                System.exit(0);
            }
        MyView myv =  new MyView(host, port, false);
        
        myv.init();
        // add to the applet to the frame
        frame.setContentPane(myv);
        frame.setTitle("L'impiccato!");
        frame.pack();
        // make the frame visible
        frame.setResizable(false);
        frame.setVisible(true);
    } // end main
    
}