package com.example.fabriziodemaria.thehangover;


import android.support.v7.app.ActionBar;
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
 * MainGame.java
 * Purpose: First activity with login phase.
 *
 * @author Fabrizio Demaria & Samia Khalid
 * @version 1.0 05/01/2015
 **/
public class MainGame extends ActionBarActivity {

    public static final String EXTRA_MESSAGE = "ipinput";
    public static final String EXTRA_MESSAGE2 = "portinput";
    public static final String EXTRA_MESSAGE3 = "username";

    EditText Ipaddress;
    EditText Portvalue;
    EditText username;
    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Ipaddress = (EditText)findViewById(R.id.iptext);
        username = (EditText)findViewById(R.id.userfield);
        Portvalue = (EditText)findViewById(R.id.inputport);
        mButton = (Button)findViewById(R.id.connect_button);
    }

    public void connectPressed(View v) {
        vai();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_game, menu);
        Ipaddress.setText("130.229.158.59");
        Portvalue.setText("4443");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Starts a RoomSelection activity passing info supplied by user
     **/
    public void vai(){


        Intent intent = new Intent(this, RoomSelection.class);

        String message = Ipaddress.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        String message2 = Portvalue.getText().toString();
        intent.putExtra(EXTRA_MESSAGE2, message2);
        String message3 = username.getText().toString();
        intent.putExtra(EXTRA_MESSAGE3, message3);
        startActivity(intent);

    }




}
