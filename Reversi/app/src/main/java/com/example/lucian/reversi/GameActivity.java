package com.example.lucian.reversi;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.lucian.reversi.ReversiLogic.Coord;

import static com.example.lucian.reversi.ConfigActivity.CFG_PLAYER_NAME;
import static com.example.lucian.reversi.ConfigActivity.CFG_SELECTED_SIZE;
import static com.example.lucian.reversi.ConfigActivity.CFG_TIMER;


/**
 * Created by lucian on 30/03/17.
 */

public class GameActivity extends AppCompatActivity implements GridFragment.PlayListener {

    private GridFragment frgGrid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_activity);


        frgGrid = (GridFragment) getFragmentManager()
                .findFragmentById(R.id.FrgGrid);

        frgGrid.setgPlayListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.game_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        /*
        if (id == R.id.game_back) {
            //turnBack(frgGrid);
        }*/
        if (id == R.id.game_restart) {
            restart();
        }
        if (id == R.id.game_giveup) {
            giveUp(frgGrid);
        }
        return super.onOptionsItemSelected(item);
    }


    private void restart() {
        Intent i = new Intent(this, GameActivity.class);
        i.putExtra(CFG_SELECTED_SIZE, frgGrid.grid_size);
        i.putExtra(CFG_TIMER, frgGrid.game_timer);
        i.putExtra(CFG_PLAYER_NAME, frgGrid.game_player);
        startActivity(i);
        finish();
    }

    private void giveUp(GridFragment frgGrid) {
        frgGrid.gameHasEnded();
    }

    @Override
    public void onPlay(Coord c) {
        LogFragment fgdet = (LogFragment) getFragmentManager().findFragmentById(R.id.FrgLog);
        boolean hayLog = (fgdet != null && fgdet.isInLayout());
        if (hayLog) {
            //
            frgGrid.setInitialLog(fgdet);
            fgdet.addToLog(frgGrid.game_model.logPlayMade(c));
        } else {
            //Toast.makeText(this, "error no fragment", Toast.LENGTH_SHORT).show();
        }

    }

}
