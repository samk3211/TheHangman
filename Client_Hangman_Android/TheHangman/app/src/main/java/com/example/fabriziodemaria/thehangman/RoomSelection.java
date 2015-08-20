    package com.example.fabriziodemaria.thehangover;

    import android.app.Activity;
    import android.content.Context;
    import android.content.Intent;
    import android.os.AsyncTask;
    import android.support.v7.app.ActionBar;
    import android.support.v7.app.ActionBarActivity;
    import android.os.Bundle;
    import android.view.LayoutInflater;
    import android.view.Menu;
    import android.view.MenuItem;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.AdapterView;
    import android.widget.ArrayAdapter;
    import android.widget.BaseExpandableListAdapter;
    import android.widget.ExpandableListAdapter;
    import android.widget.ExpandableListView;
    import android.widget.ListView;
    import android.widget.ProgressBar;
    import android.widget.TableLayout;
    import android.widget.TextView;
    import android.widget.Toast;

    import java.io.BufferedOutputStream;
    import java.io.BufferedReader;
    import java.io.IOException;
    import java.io.InputStreamReader;
    import java.io.ObjectInputStream;
    import java.io.OptionalDataException;
    import java.net.Socket;
    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.List;
    import java.util.concurrent.BlockingQueue;
    import java.util.concurrent.LinkedBlockingDeque;

    import ServerPackage.Room;


    /**
     * KTH - ID2212, Project 6
     * RoomSelection.java
     * Purpose: Display the list of rooms, provide player's stats and allow room selection.
     *
     * @author Fabrizio Demaria & Samia Khalid
     * @version 1.0 05/01/2015
     **/
    public class RoomSelection extends ActionBarActivity {

        public static final String EXTRA_MESSAGE = "ipinput";
        public static final String EXTRA_MESSAGE2 = "portinput";
        public static final String EXTRA_MESSAGE3 = "username";

        private String ip;
        private String port;
        private String username;

        Room myrooms;

        ExpandableListAdapter listAdapter;
        ExpandableListView expListView;
        List<String> listDataHeader;
        HashMap<String, List<String>> listDataChild;

        ListView myTable;
        ProgressBar spinner;
        TextView roomtitle;
        TextView statstest;

        RoomSelection myRoomSelectionRef;

        //Socket clientSocket;
        private BlockingQueue<String> bridge = null;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            bridge = new LinkedBlockingDeque<>(1);

            setContentView(R.layout.activity_room_selection);
            Intent intent = getIntent();
            //ActionBar actionBar = getSupportActionBar();
            //actionBar.hide();
            myRoomSelectionRef = this;
            ip = intent.getStringExtra(MainGame.EXTRA_MESSAGE);
            port = intent.getStringExtra(MainGame.EXTRA_MESSAGE2);
            username = intent.getStringExtra(MainGame.EXTRA_MESSAGE3);
            roomtitle = (TextView) findViewById(R.id.roomTitle);
            myTable = (ListView) findViewById(R.id.roomList);
            spinner = (ProgressBar) findViewById(R.id.waitingSpinner);
            statstest = (TextView) findViewById(R.id.statstext);

            GameLogic myLogic = new GameLogic();
            myLogic.execute(ip, port);

            myTable.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    try {
                        bridge.put(new String("" + position));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_room_selection, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle action bar item clicks here. The action bar will
            // automatically handle clicks on the Home/Up button, so long
            // as you specify a parent activity in AndroidManifest.xml.
            int id = item.getItemId();

            Singleton.getInstance("",0).destroySocket();
/*
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            */
            //noinspection SimplifiableIfStatement
            if (id == R.id.action_settings) {

                return true;
            }

            return super.onOptionsItemSelected(item);
        }

        public void updateWelcomeMessage(int score, int games){
            statstest.setText("Welcome " + username + "\nScore = " + score + "\nGames = " + games);
        }

        /**
         * Update the view with the rooms current status
         *
         * @param myrooms contains all the information about the current state of the rooms
         */
        public void updateTable(Room myrooms) {
            this.myrooms = myrooms;
            // Defined Array values to show in ListView
            String[] values = new String[10];

            for(int i=0; i<10; i++){
                values[i] = "Room" + i + ": " + myrooms.connected[i] + "/2 - " + myrooms.players1[i] + ", " + myrooms.players2[i];
            }

            // Define a new Adapter
            // First parameter - Context
            // Second parameter - Layout for the row
            // Third parameter - ID of the TextView to which the data is written
            // Forth - the Array of data
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, values);


            // Assign adapter to ListView
            myTable.setAdapter(adapter);
        }

        /**
         * Start a game activity (GameOn)
         **/
        public void startGame() {
            Intent intent = new Intent(this, GameOn.class);
            startActivity(intent);
        }

        public void startSpinner() {
            spinner.setVisibility(View.VISIBLE);
            myTable.setVisibility(View.INVISIBLE);
            roomtitle.setText("Waiting for opponent...");

        }

        /**
         * Create the socket in the Singleton and start a RoomSelectionNet thread for TCP comm
         *
         * This task takes as input the ip and the port of the server (provided by the user in MainGame
         */
        private class GameLogic extends AsyncTask<String,Void,String> {
            @Override
            protected String doInBackground(String... params) {
                // perform Long time consuming operation
                Singleton.getInstance(ip,Integer.parseInt(port));
                //clientSocket = Singleton.getInstance("",0).getSocket();
                return "Connected!";
            }

            /* (non-Javadoc)
             * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
             */
            @Override
            protected void onPostExecute(String result) {
                // TODO Auto-generated method stub

                if(result.equals("Connected!")) {

                    RoomSelectionNet lg = new RoomSelectionNet(username, myRoomSelectionRef, bridge);
                    Thread tmptr = new Thread(lg);
                    tmptr.start();

                }
            }

            /* (non-Javadoc)
             * @see android.os.AsyncTask#onPreExecute()
             */
            @Override
            protected void onPreExecute() {
                // TODO Auto-generated method stub
                super.onPreExecute();
                //spinner.setVisibility(View.VISIBLE);
            }

            /* (non-Javadoc)
             * @see android.os.AsyncTask#onProgressUpdate(Progress[])
             */
            @Override
            protected void onProgressUpdate(Void... values) {
                // TODO Auto-generated method stub
                super.onProgressUpdate(values);
            }
        }

    }
