package com.example.lucian.reversi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


public class MainActivity extends AppCompatActivity {

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first);

    }

    //help button
    public void clickHelp(View src) {
        Intent i = new Intent(this, HelpActivity.class);
        startActivity(i);
        finish();
    }

    //start button
    public void clickStart(View src) {
        Intent i = new Intent(this, ConfigActivity.class);
        startActivity(i);
        finish();
    }

    //exit button
    public void clickExit(View src) {
        finish();
    }


}

