package com.example.lucian.reversi;


import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.lucian.reversi.ConfigActivity.CFG_PLAYER_NAME;
import static com.example.lucian.reversi.ConfigActivity.CFG_SELECTED_SIZE;
import static com.example.lucian.reversi.ConfigActivity.CFG_TIMER;

/**
 * Created by lucian on 30/03/17.
 */

public class GameActivity extends AppCompatActivity {

    protected static final String G_PLAYER_LIST = "00";
    protected static final String G_NPC_LIST = "11";
    protected static final String G_REMAINING = "111";
    protected static final String G_TIME = "000";
    protected static final String G_NAME = "001";
    protected static final String G_GRID = "010";

    protected int grid_size;
    protected boolean game_timer;

    protected String game_player;
    protected GameModel game_model;
    protected String current_turn;
    protected GameTimer game_clock;
    private MediaPlayer player;

    @Override

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        String templateMesageTurn = getString(R.string.game_turn);
        String templateScorePlayer = getString(R.string.game_score_player);
        String templateScoreNPC = getString(R.string.game_score_npc);
        String templateRemaining = getString(R.string.game_s_left);
        String templateInitialTime = getString(R.string.game_time);

        if (savedInstanceState != null) {
            //load stuff
            grid_size = savedInstanceState.getInt(CFG_SELECTED_SIZE);
            game_timer = savedInstanceState.getBoolean(CFG_TIMER);
            game_player = savedInstanceState.getString(CFG_PLAYER_NAME);

            ArrayList<Integer> p_list = savedInstanceState.getIntegerArrayList(G_PLAYER_LIST);
            ArrayList<Integer> npc_list = savedInstanceState.getIntegerArrayList(G_NPC_LIST);
            //set up initial grid
            game_model = new GameModel(grid_size, null, null);
            game_model.setNPCPieces(npc_list);
            game_model.setPlayerPieces(p_list);
        } else {
            //load config
            Intent i = getIntent();
            grid_size = i.getIntExtra(CFG_SELECTED_SIZE, 4);
            game_timer = i.getBooleanExtra(CFG_TIMER, false);
            game_player = i.getStringExtra(CFG_PLAYER_NAME);

            //set up initial grid
            game_model = new GameModel(grid_size, null, null);
        }

        //set up stats

        //time text
        TextView time_t = (TextView) findViewById(R.id.game_time);
        //start the clock
        game_clock = new GameTimer();
        game_clock.start();
        if (game_timer) {
            String t = "∞ ";
            String m_t =
                    String.format(templateInitialTime, t);
            time_t.setText(m_t);
        } else {
            int t = grid_size * 15;

            String m_t =
                    String.format(templateInitialTime, String.valueOf(t));
            time_t.setText(m_t);
        }
        //turn text
        TextView turn = (TextView) findViewById(R.id.game_turn);
        String m_t =
                String.format(templateMesageTurn, game_player);
        turn.setText(m_t);
        //player score text
        TextView score_p = (TextView) findViewById(R.id.game_score_player);

        String m_p =
                String.format(templateScorePlayer, String.valueOf(game_model.getPlayerCount()));
        score_p.setText(m_p);

        //npc score text
        TextView score_npc = (TextView) findViewById(R.id.game_score_npc);
        String m_n =
                String.format(templateScoreNPC, String.valueOf(game_model.getNPCCount()));
        score_npc.setText(m_n);

        //remaining text
        TextView remaining = (TextView) findViewById(R.id.game_s_left);
        String m_r =
                String.format(templateRemaining, String.valueOf(game_model.getRemainingCount()));
        remaining.setText(m_r);


        //set up grid
        GridView grid = (GridView) findViewById(R.id.grid);
        grid.setNumColumns(grid_size);
        grid.setVerticalSpacing(2);
        grid.setHorizontalSpacing(2);
        grid.setAdapter(new ReversiAdapter(this, game_model.getPieces(GameModel.PC),
                game_model.getPieces(GameModel.NPC), game_model.getPieces(GameModel.PCp),
                game_model.getPieces(GameModel.NPCp)));
        grid.setOnItemClickListener(new GridInfo());

    }

    private void playSound(int soundId) {
        player = MediaPlayer.create(this, soundId);
        player.start();
    }

    private void showMesage(String mesage) {
        Toast.makeText(this, mesage, Toast.LENGTH_SHORT).show();
    }

    private boolean checkCell(int position) {
        if (game_model.isPlayerTurn(current_turn)) {
            GameModel.Coordinates c = game_model.parsePosition(position);
            return game_model.checkPlayerPossible(c);
        } else {
            GameModel.Coordinates c = game_model.parsePosition(position);
            return game_model.checkNPCPossible(c);
        }

    }

    //save state
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        //save data
        savedInstanceState.putInt(CFG_SELECTED_SIZE, this.grid_size);
        savedInstanceState.putString(CFG_PLAYER_NAME, this.game_player);
        savedInstanceState.putBoolean(CFG_TIMER, this.game_timer);

        //save game model
        savedInstanceState.putIntegerArrayList(G_PLAYER_LIST, this.game_model.getPieces(GameModel.PC));
        savedInstanceState.putIntegerArrayList(G_NPC_LIST, this.game_model.getPieces(GameModel.NPC));

    }

    //results button
    // to skip straight to the results screen
    public void clickResults(View src) {
        //stop clock
        game_clock.stop();
        Intent i = new Intent(this, ResultsActivity.class);
        //load data
        i.putExtra(G_NAME, this.game_player);
        i.putExtra(G_GRID, this.grid_size);
        i.putExtra(G_REMAINING, this.getRemaining());
        i.putExtra(G_PLAYER_LIST, this.game_model.getPlayerCount());
        i.putExtra(G_NPC_LIST, this.game_model.getNPCCount());
        i.putExtra(G_TIME, this.getTimeElapser());
        startActivity(i);
        finish();
    }


    protected int getCellSize() {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 290 / grid_size, r.getDisplayMetrics());
        return Math.round(px);
    }

    public int getRemaining() {
        return this.game_model.getRemainingCount();
    }

    public int getTimeElapser() {
        Double d = game_clock.getSeconds();
        Long l = Math.round(d);
        int i = Integer.valueOf(l.intValue());
        return i;
    }

    protected class ReversiAdapter extends BaseAdapter {

        private GameActivity mContext;
        private ArrayList<Integer> player;
        private ArrayList<Integer> npc;
        private ArrayList<Integer> npc_pos;
        private ArrayList<Integer> player_pos;

        private ReversiAdapter(GameActivity c, ArrayList<Integer> pc, ArrayList<Integer> n_pc,
                               ArrayList<Integer> pc_p, ArrayList<Integer> npc_p) {
            mContext = c;
            player = pc;
            player_pos = pc_p;
            npc_pos = npc_p;
            npc = n_pc;
        }

        public int getCount() {
            return this.mContext.game_model.getTotalCount(); //size * size
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            ImageView cell;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                cell = new ImageView(mContext);
            } else {
                cell = (ImageView) convertView;
            }
            cell.setLayoutParams(new GridView.LayoutParams(getCellSize(), getCellSize()));

            //call the model to return the cell
            this.setCell(cell, position);

            return cell;
        }

        private void setCell(ImageView cell, int position) {
            if (player.contains(position))
                cell.setBackgroundResource(R.drawable.portal_blue_full);
            else if (player_pos.contains(position))
                cell.setBackgroundResource(R.drawable.portal_blue);
            else if (npc.contains(position))
                cell.setBackgroundResource(R.drawable.portal_orange_full);
            else if (npc_pos.contains(position))
                cell.setBackgroundResource(R.drawable.portal_orange);
            else
                cell.setBackgroundResource(R.drawable.cell);
        }

    }

    private class GridInfo implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View v,
                                int position, long id) {
            //play game and stuff
            if (checkCell(position)) {
                //play sound
                game_model.flipPiece(game_model.parsePosition(position));
                playSound(R.raw.good_click);
            } else {
                playSound(R.raw.wrong_click);
                String mes = "La casella " + position + " no es valida";
                showMesage(mes);
            }
        }
    }

    private class GameTimer {
        private long begin, end;

        public void start() {
            begin = System.currentTimeMillis();
        }

        public void stop() {
            end = System.currentTimeMillis();
        }

        public long getTime() {
            return end - begin;
        }

        public double getSeconds() {
            return (end - begin) / 1000.0;
        }
    }
}
