package com.example.lucian.reversi;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.util.Date;

import static com.example.lucian.reversi.GameActivity.G_GRID;
import static com.example.lucian.reversi.GameActivity.G_NAME;
import static com.example.lucian.reversi.GameActivity.G_NPC_LIST;
import static com.example.lucian.reversi.GameActivity.G_PLAYER_LIST;
import static com.example.lucian.reversi.GameActivity.G_REMAINING;
import static com.example.lucian.reversi.GameActivity.G_TIME;

/**
 * Created by lucian on 24/03/17.
 */

public class ResultsActivity extends AppCompatActivity {

    private static final String RES_DATE = "001";
    private static final String RES_LOG = "010";
    private static final String RES_MAIL = "100";
    protected String alias;
    protected int grid;
    protected int remaining;
    protected int player;
    protected int npc;
    protected int clock;
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
        if (player > npc)
            log = log + "Has guanyat !!";
        else if (npc > player)
            log = log + "Has perdut !!";
        else
            log = log + "Heu empatat ";
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
        Intent i = new Intent(this, ConfigActivity.class);
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
