package com.example.lucian.reversi;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by lucian on 30/03/17.
 */

public class GameController {

    protected static int TURN_PLAYER = 99;
    protected static int TURN_ATLAS = 66;
    protected String player_name = "Peabody";
    protected int grid_size = 4;
    protected int timer_on = 0;
    protected int time_left = 30;
    protected GameModel model;
    protected int TIME_ALLOWED = 30;
    protected Timer turn_start = new Timer();

    public GameController(String player, int size, int timer) {
        player_name = player;
        grid_size = size;
        timer_on = timer;

        //create grid
        //set up model
        //set initial pieces

        turn_start.schedule(new updateTime(), 0, 5000);
    }

    public void updateModel(ArrayList<Integer> white, ArrayList<Integer> black, ArrayList<Integer> possible) {
        //do shit
        //old_model + white_l + black_l + possible_l
    }

    //function that check if the player still has time
    public boolean checkTimeLeft() {
        return (time_left > 0);
    }

    //function to start a new turn
    public void new_turn(GameModel model, int turn) {
        //turn % 2 = 0   > ATLASs turn
        //turn % 2 = 1   > player turn
    }

    //funtion to fill the board initially
    public void fillInitialGrid(GameModel model) {
        //get the table model
        /*place first white on  x' = grid_size / 2
        *                       y' = grid_size / 2
        * place next white on   x = x'-1
        *                       x = y'-1
        * place first black on  x` = x'         or  pos = pos(first white)-1
        *                       y` = y'-1
        * place next black on   x = x' -1       or  pos = pos(first white)-grid_size
        *                       y = y'
        */

    }

    public ArrayList<Integer> getPossibleList(int turn) {

        /*if the turn is the players:
        *   get the list of all black pieces and for each do:
        *       check their neighbours  -- | / \
        *           if any is a white piece then check in that same direction for a empty spot
        *               and mark it as possible
        *               save the white piece position and the possible position together (e , w)
        *
        * if the turn is of the ATLAS:
        *   get the list of all white pieces and for each do:
        *       check their neighbours  -- | / \
        *           if any is a white piece then check in that same direction for a empty spot
        *               and mark it as possible
        *               save the white piece position and the possible position together (e , w)
        *           call a function to count the number of pieces stolen
        *           pick the best
        *
        *
        */

        return new ArrayList<>(4);
    }

    public void updateWhiteList(ArrayList<Integer> newList) {

    }

    //start

    //turn Atlas

    //turn Player


    //end

    public void updateBlackList(ArrayList<Integer> newList) {

    }

    public void handleTurnAtlas(ArrayList<Integer> newList) {

    }

    //function to keep track of elapsed time and print it for the player
    public class updateTime extends TimerTask {
        @Override
        public void run() {
            //text value for time = 5 10 15 20 25 30
            time_left = -5;
        }
    }


}
