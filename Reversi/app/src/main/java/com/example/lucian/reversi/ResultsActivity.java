package com.example.lucian.reversi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.Date;

import static com.example.lucian.reversi.ConfigActivity.CFG_PLAYER_NAME;
import static com.example.lucian.reversi.ConfigActivity.CFG_SELECTED_SIZE;
import static com.example.lucian.reversi.ConfigActivity.CFG_TIMER;
import static com.example.lucian.reversi.GridFragment.G_GRID;
import static com.example.lucian.reversi.GridFragment.G_NAME;
import static com.example.lucian.reversi.GridFragment.G_NPC_LIST;
import static com.example.lucian.reversi.GridFragment.G_PLAYER_LIST;
import static com.example.lucian.reversi.GridFragment.G_REASON;
import static com.example.lucian.reversi.GridFragment.G_REMAINING;
import static com.example.lucian.reversi.GridFragment.G_TIME;

/**
 * Created by lucian on 24/03/17.
 */

public class ResultsActivity extends AppCompatActivity {

    private static final String RES_DATE = "001";
    private static final String RES_LOG = "010";
    private static final String RES_MAIL = "100";
    private static final int CODE_OK = 0;
    protected String alias;
    protected int grid;
    protected int remaining;
    protected int player;
    protected int npc;
    protected int clock;
    protected String reason;
    private EditText dateAndTime;
    private EditText gameLog;
    private EditText mail;

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.results);

        gameLog = (EditText) findViewById(R.id.results_log_edit);
        dateAndTime = (EditText) findViewById(R.id.results_time_edit);
        mail = (EditText) findViewById(R.id.results_email_edit);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container2, new MainActivity.PlaceholderFragment()).commit();
        }

        //load data
        if (savedInstanceState != null) {
            //load date
            dateAndTime.setText(savedInstanceState.getString(RES_DATE));
            //load log
            gameLog.setText(savedInstanceState.getString(RES_LOG));
            //load mail
            mail.setText(savedInstanceState.getString(RES_MAIL));
        }

        Intent i = getIntent();
        alias = i.getStringExtra(G_NAME);
        grid = i.getIntExtra(G_GRID, 4);
        remaining = i.getIntExtra(G_REMAINING, 4);
        player = i.getIntExtra(G_PLAYER_LIST, 2);
        npc = i.getIntExtra(G_NPC_LIST, 2);
        clock = i.getIntExtra(G_TIME, 0);
        reason = i.getStringExtra(G_REASON);

        this.createLog();

        Date d = new Date();
        if (dateAndTime.getText() != null)
            dateAndTime.setText(d.toString());
    }

    private void createLog() {
        String log = "";
        //afegir nom
        log = log + "Alias: " + alias + ". ";
        //afegir mida
        log = log + "Mida graella: " + grid + ". ";
        //affegir temps
        log = log + "Temps Total: " + clock + ". ";
        //afegir dades partida
        if (reason.equals(getString(R.string.game_over_blocked)))
            log = log + "Heu quedat sense poder completar la graella !!";
        else if (reason.equals(getString(R.string.game_over_loss)))
            log = log + "Has perdut !!";
        else if (reason.equals(getString(R.string.game_over_tie)))
            log = log + "Heu empatat !!";
        else if (reason.equals(getString(R.string.game_over_time)))
            log = log + "Has esgotat el temps !!";
        else
            log = log + "Has guanyat";
        //dades dels jugadors
        log = log + "Tu: " + player + ". ";
        log = log + "Oponent: " + npc + ". ";
        int difference = Math.abs(player - npc);
        log = log + difference + " caselles de diferencia !";
        if (remaining > 0)
            log = log + "Han quedat " + remaining + " caselles per cobrir.";

        gameLog.setText(log);
    }

    //save state
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //save data
        savedInstanceState.putString(RES_DATE, this.getDate());
        savedInstanceState.putString(RES_LOG, this.getLog());
        savedInstanceState.putString(RES_MAIL, this.getMail());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * react to the user tapping/selecting an options menu item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.hamburger:
                Intent i = new Intent(this, OpcionesActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //send button
    public void clickSend(View src) {
        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setType("text/html");
        i.setData(Uri.parse("mailto:" + this.getMail()));
        i.putExtra(Intent.EXTRA_SUBJECT, "PracticaAndroid1");
        i.putExtra(Intent.EXTRA_TEXT, this.getLog());
        startActivity(i);

        startActivity(i);
        finish();
    }

    //new button
    public void clickNew(View src) {
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String strAlias = SP.getString("option1", "peabody");
        boolean timerOn = SP.getBoolean("option2", false);
        String gridSize = SP.getString("option3", "4");
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra(CFG_SELECTED_SIZE, Integer.valueOf(gridSize));
        i.putExtra(CFG_TIMER, timerOn);
        i.putExtra(CFG_PLAYER_NAME, strAlias);
        startActivity(i);
        finish();
    }

    //exit button
    public void clickExit(View src) {
        finish();
    }

    public String getMail() {
        return mail.getText().toString();
    }

    public String getLog() {
        return gameLog.getText().toString();
    }

    public String getDate() {
        return dateAndTime.getText().toString();
    }
}
