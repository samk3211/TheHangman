package com.example.fabriziodemaria.thehangover;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.view.View;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * KTH - ID2212, Project 6
 * GameOn.java
 * Purpose: Thread handling game UI
 *
 * @author Fabrizio Demaria & Samia Khalid
 * @version 1.0 05/01/2015
 **/
public class GameOn extends ActionBarActivity {

    TextView resultText;
    TextView trialsdisplayed;
    TextView oscoredisplayed;
    TextView uscoredisplayed;

    Button trybutton;
    Button newwordbutton;
    EditText userInput;

    GameOn myref;

    private BlockingQueue<String> bridge = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_on);
        Intent intent = getIntent();
        myref = this;
        resultText = (TextView) findViewById(R.id.debmessage);
        bridge = new LinkedBlockingQueue<String>(1);

        trybutton = (Button) findViewById(R.id.trybutton);
        newwordbutton = (Button) findViewById(R.id.newwordbutton);

        trialsdisplayed = (TextView) findViewById(R.id.trialsdisplayed);
        uscoredisplayed = (TextView) findViewById(R.id.uscoredisplayed);
        oscoredisplayed = (TextView) findViewById(R.id.oscoredisplayed);
        userInput = (EditText) findViewById(R.id.userInput);
        bridge = new LinkedBlockingQueue<>(1);

        //Connection
        GameThread lg = new GameThread(myref, bridge);
        Thread tmptr = new Thread(lg);
        tmptr.start();
    }

    public void setTry(boolean b){
        trybutton.setEnabled(b);
    }
    public void setNewWord(boolean b){
        newwordbutton.setEnabled(b);
    }
    public void newwordrequest(View v){
        userInput.setText("");
        try {
            bridge.put(GameThread.NEWWORD_INPUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void updateText(String s){
        resultText.setText(s);
        resultText.setTextColor(Color.BLACK);
    }
    public void updateTextRed(String s){
        resultText.setText(s);
        resultText.setTextColor(Color.RED);
    }
    public void updateTextGreen(String s){
        resultText.setText(s);
        resultText.setTextColor(Color.GREEN);
    }
    public void updateScore1(String s){
        uscoredisplayed.setText(s);
    }
    public void updateScore2(String s){
        oscoredisplayed.setText(s);
    }
    public void updateTrials(String s){
        trialsdisplayed.setText(s);
    }
    public void trypressed(View v) {

        try {
            bridge.put(userInput.getText().toString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        userInput.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_game, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Singleton.getInstance("",0).destroySocket();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

























