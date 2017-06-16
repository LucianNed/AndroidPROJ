package com.example.lucian.reversi;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.lucian.reversi.ConfigActivity.CFG_PLAYER_NAME;
import static com.example.lucian.reversi.ConfigActivity.CFG_SELECTED_SIZE;
import static com.example.lucian.reversi.ConfigActivity.CFG_TIMER;
import static com.example.lucian.reversi.ReversiLogic.Re;


/**
 * Created by lucian on 14/06/17.
 */

public class GridFragment extends Fragment {

    protected static final String G_PLAYER_LIST = "00";
    protected static final String G_NPC_LIST = "11";
    protected static final String G_REMAINING = "111";
    protected static final String G_TIME = "000";
    protected static final String G_NAME = "001";
    protected static final String G_GRID = "010";
    protected static final String G_REASON = "110";
    protected static final String G_MODEL = "111";

    private static final String DB_SIZE = "mida";
    private static final String DB_DATE = "date";
    private static final String DB_TIMED = "timed";
    private static final String DB_BLACK = "black";
    private static final String DB_WHITE = "white";
    private static final String DB_TIMEL = "time_left";
    private static final String DB_RES = "result";
    private static final String DB_ALIAS = "alias";

    protected int grid_size;
    protected boolean game_timer;

    protected String game_player;
    protected ReversiLogic game_model;
    protected MediaPlayer player;
    protected TextView time_t;
    protected TextView score_p;
    protected TextView score_npc;
    protected TextView remaining;
    protected TextView turn;
    protected GridView grid;
    protected ReversiAdapter rev_adap;

    private PlayListener lstnr;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.grid_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        String templateMesageTurn = getString(R.string.game_turn);
        String templateScorePlayer = getString(R.string.game_score_player);
        String templateScoreNPC = getString(R.string.game_score_npc);
        String templateRemaining = getString(R.string.game_s_left);
        String templateInitialTime = getString(R.string.game_time);

        if (state != null) {

            //load stuff
            grid_size = state.getInt(CFG_SELECTED_SIZE);
            game_timer = state.getBoolean(CFG_TIMER);
            game_player = state.getString(CFG_PLAYER_NAME);
            game_model = state.getParcelable(G_MODEL);

        } else {

            //load config
            Intent i = getActivity().getIntent();
            grid_size = i.getIntExtra(CFG_SELECTED_SIZE, 4);
            game_timer = i.getBooleanExtra(CFG_TIMER, false);
            game_player = i.getStringExtra(CFG_PLAYER_NAME);

            //set up initial grid
            game_model = new ReversiLogic(grid_size, false, this);
        }

        //set up stats
        //time text
        time_t = (TextView) getActivity().findViewById(R.id.game_time);
        int t_limit;
        if (!game_timer) {
            String t = "∞";
            String m_t =
                    String.format(templateInitialTime, t);
            time_t.setText(m_t);
        } else {
            t_limit = grid_size * 15;
            String m_t =
                    String.format(templateInitialTime, String.valueOf(t_limit));
            time_t.setText(m_t);
            time_t.setTextColor(Color.RED);
        }

        //turn text
        turn = (TextView) getActivity().findViewById(R.id.game_turn);
        String m_t =
                String.format(templateMesageTurn, "1");
        turn.setText(m_t);

        //player score text
        score_p = (TextView) getActivity().findViewById(R.id.game_score_player);
        String m_p =
                String.format(templateScorePlayer, String.valueOf(game_model.getPieces(ReversiLogic.Piece.BLACK)));
        score_p.setText(m_p);

        //npc score text
        score_npc = (TextView) getActivity().findViewById(R.id.game_score_npc);
        String m_n =
                String.format(templateScoreNPC, String.valueOf(game_model.getPieces(ReversiLogic.Piece.WHITE)));
        score_npc.setText(m_n);

        //remaining text
        remaining = (TextView) getActivity().findViewById(R.id.game_s_left);
        String m_r =
                String.format(templateRemaining, String.valueOf(game_model.getRemainingPieces()));
        remaining.setText(m_r);

        //set up grid
        grid = (GridView) getActivity().findViewById(R.id.grid);
        grid.setNumColumns(grid_size);
        grid.setVerticalSpacing(2);
        grid.setHorizontalSpacing(2);
        rev_adap = new ReversiAdapter((GameActivity) this.getActivity());
        grid.setAdapter(rev_adap);

    }

    public void setgPlayListener(PlayListener listener) {
        lstnr = listener;
    }

    public void setTurnBack() {
        boolean found = false;
        for (int i = game_model.turns.size() - 1; i >= 0; i--) {
            if (game_model.whos_turn.get(i) == ReversiLogic.Piece.WHITE) {
                if (found == true) {
                    game_model.pieces = game_model.turns.get(i);
                    game_model.current = ReversiLogic.Piece.BLACK;
                    UpdateStats();
                    rev_adap.notifyDataSetChanged();
                } else {
                    found = true;
                }
            } else {
                game_model.turns.remove(i);
            }
        }
    }

    protected int getCellSize() {
        Resources r = getResources();
        int max_size;
        if ((getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_NORMAL) {
            max_size = 290;
        } else
            max_size = 480;
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, max_size / grid_size, r.getDisplayMetrics());
        return Math.round(px);
    }

    private void UpdateStats() {
        String m_p =
                String.format(getString(R.string.game_score_player), String.valueOf(game_model.getPieces(ReversiLogic.Piece.BLACK)));
        score_p.setText(m_p);
        String m_n =
                String.format(getString(R.string.game_score_npc), String.valueOf(game_model.getPieces(ReversiLogic.Piece.WHITE)));
        score_npc.setText(m_n);
        String m_r =
                String.format(getString(R.string.game_s_left), String.valueOf(game_model.getRemainingPieces()));
        remaining.setText(m_r);
        String m_t =
                String.format(getString(R.string.game_turn), String.valueOf(game_model.turns.size() + 1));
        turn.setText(m_t);
        String m_tt =
                String.format(getString(R.string.game_time), String.valueOf(game_model.getTimeRemaining()));
        time_t.setText(m_tt);
    }

    protected void setInitialLog(LogFragment frgLog) {
        String s = frgLog.log.getText().toString();
        if (s == "LOG... \n") {
            //alias and size
            String alias_t =
                    String.format(getResources().getString(R.string.log_alias), game_player);
            String mida_t =
                    String.format(getResources().getString(R.string.log_alias), String.valueOf(grid_size));
            frgLog.addToLog("  " + alias_t + mida_t);
            //timed or not
            String timer_t;
            if (game_timer)
                timer_t = getResources().getString(R.string.log_with_timer);
            else
                timer_t = getResources().getString(R.string.log_without_timer);
            frgLog.addToLog("  " + timer_t);
        }
    }

    protected void playSound(int soundId) {
        player = MediaPlayer.create(getActivity(), soundId);
        player.start();
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
        savedInstanceState.putParcelable(G_MODEL, this.game_model);
    }

    public void gameHasEnded() {
        //log the game in the database
        saveToDB();
        //go to results
        Intent i = new Intent(getActivity(), ResultsActivity.class);
        //load data
        i.putExtra(G_NAME, this.game_player);
        i.putExtra(G_GRID, this.grid_size);
        i.putExtra(G_MODEL, this.game_model);
        i.putExtra(G_REMAINING, this.game_model.getRemainingPieces());
        i.putExtra(G_PLAYER_LIST, this.game_model.getPieces(ReversiLogic.Piece.BLACK));
        i.putExtra(G_NPC_LIST, this.game_model.getPieces(ReversiLogic.Piece.WHITE));
        i.putExtra(G_TIME, this.game_model.getTimeElapsed());
        String r = this.game_model.reason;
        if (r == null)
            r = Re;
        i.putExtra(G_REASON, r);
        startActivity(i);
        getActivity().finish();
    }

    private void saveToDB() {
        CustomSQLHelper usdbh =
                new CustomSQLHelper(getActivity(), "DBReversi", null, 2);
        SQLiteDatabase db = usdbh.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DB_ALIAS, game_player);
        values.put(DB_SIZE, grid_size);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        values.put(DB_DATE, sdf.format(new Date()));
        values.put(DB_TIMED, game_timer);
        values.put(DB_BLACK, game_model.getPieces(ReversiLogic.Piece.BLACK));
        values.put(DB_WHITE, game_model.getPieces(ReversiLogic.Piece.WHITE));
        values.put(DB_TIMEL, game_model.getTimeRemaining());
        values.put(DB_RES, game_model.result);
        //put em in
        db.insert("Log", null, values);

        db.close();
    }

    //results button
    // to skip straight to the results screen

    public interface PlayListener {
        void onPlay(ReversiLogic.Coord c);
    }

    protected class ReversiAdapter extends BaseAdapter {

        private GameActivity mContext;

        private ReversiAdapter(GameActivity c) {
            mContext = c;

        }

        public int getCount() {
            return game_model.getTotalPieces(); //size * size
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            final Button cell;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                cell = new Button(mContext);
            } else {
                cell = (Button) convertView;
            }
            cell.setLayoutParams(new GridView.LayoutParams(getCellSize() - grid_size, getCellSize()));
            cell.setTag(position);
            cell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //handle clicks
                    ReversiLogic.Coord c = game_model.parsePosition((Integer) v.getTag());
                    lstnr.onPlay(c);
                    game_model.playSelected(c);
                    UpdateStats();
                    notifyDataSetChanged();
                }
            });
            //call the model to return the cell
            this.setCell(cell, position);

            return cell;
        }

        private void setCell(Button cell, int position) {
            //player
            if (checkPosition(position, ReversiLogic.Piece.BLACK))
                cell.setBackgroundResource(R.drawable.portal_black);
                //npc
            else if (checkPosition(position, ReversiLogic.Piece.WHITE))
                cell.setBackgroundResource(R.drawable.portal_white);
                //plays
            else if (checkPlays(position))
                cell.setBackgroundResource(R.drawable.portal_pos);
                //rest
            else
                cell.setBackgroundResource(R.drawable.cell);
        }

        protected boolean checkPosition(int position, ReversiLogic.Piece color) {
            ReversiLogic.Coord parsed_p = game_model.parsePosition(position);
            if (game_model.pieces.get(parsed_p) == ReversiLogic.Piece.BLACK && color != null && color == ReversiLogic.Piece.BLACK)
                return true;
            if (game_model.pieces.get(parsed_p) == ReversiLogic.Piece.WHITE && color != null && color == ReversiLogic.Piece.WHITE)
                return true;
            return false;
        }

        private boolean checkPlays(int position) {
            ReversiLogic.Coord parsed_p = game_model.parsePosition(position);
            if (game_model.posplays.contains(parsed_p))
                return true;
            return false;
        }
    }


}
