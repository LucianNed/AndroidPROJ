package com.example.lucian.reversi;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import static com.example.lucian.reversi.ConfigActivity.CFG_PLAYER_NAME;
import static com.example.lucian.reversi.ConfigActivity.CFG_SELECTED_SIZE;
import static com.example.lucian.reversi.ConfigActivity.CFG_TIMER;

public class MainActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    //help button
    public void clickHelp(View src) {
        Intent i = new Intent(this, HelpActivity.class);
        startActivity(i);
        finish();
    }

    //start button
    public void clickStart(View src) {
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

    //consult db button
    public void clickDB(View src) {
        Intent i = new Intent(this, AccessBDActivity.class);
        startActivity(i);
        finish();
    }

    //exit button
    public void clickExit(View src) {
        finish();
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
                //Toast.makeText(this, "ADD!", Toast.LENGTH_SHORT).show();
                Intent i = new Intent(this, OpcionesActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

    } //end fragment


}

