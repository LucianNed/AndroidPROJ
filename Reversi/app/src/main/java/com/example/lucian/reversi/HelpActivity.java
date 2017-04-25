package com.example.lucian.reversi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by lucian on 24/03/17.
 */

public class HelpActivity extends AppCompatActivity {

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
    }

    //back button
    public void clickBack(View src) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}