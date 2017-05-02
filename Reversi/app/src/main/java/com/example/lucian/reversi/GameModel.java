package com.example.lucian.reversi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by lucian on 30/03/17.
 */

public class GameModel {

    protected final static String PC = "PLAYER";
    protected final static String NPC = "NPC";
    protected final static String PCp = "PLAYER_POSSIBLE";
    protected final static String NPCp = "NPC_POSSIBLE";
    private static final String R_BLOCK = "cant move";
    private static final String R_TIMEOUT = "out of time";


    private ArrayList<Coordinates> NPC_pieces;          //computer
    private ArrayList<Coordinates> PLAYER_pieces;       //player
    private ArrayList<Coordinates> PLAYER_possible_moves;      //possible blue 
    private ArrayList<Coordinates> NPC_possible_moves;      //possible orange
    private int size;                                   //grid length
    public int move = 0;
    private Coordinates last_move;
    private GameTimer clock = new GameTimer();
    private String reason;

    //create model
    protected GameModel(int size, ArrayList<Coordinates> player, ArrayList<Coordinates> npc) {
        this.size = size;
        this.NPC_pieces = (player == null) ? new ArrayList<Coordinates>() : player;
        this.PLAYER_pieces = (npc == null) ? new ArrayList<Coordinates>() : npc;
        this.PLAYER_possible_moves = new ArrayList<>();
        this.NPC_possible_moves = new ArrayList<>();
        this.setInitialModel(size);
    }

    //get N of atlas pieces
    protected int getNPCCount() {
        return this.NPC_pieces.size();
    }

    //get N of pbody pieces
    protected int getPlayerCount() {
        return this.PLAYER_pieces.size();
    }

    //get number of remaining pieces
    protected int getRemainingCount() {
        return this.getTotalCount() - this.getNPCCount() - this.getPlayerCount();
    }

    //get total N of pieces
    protected int getTotalCount() {

        return this.size * this.size;
    }

    //coordinate to position
    protected int parseCoordinates(Coordinates c) {
        int r = c.row * this.size;
        return r + c.col;
    }

    //position to coordinate
    protected Coordinates parsePosition(int r) {
        Coordinates c = new Coordinates(0, 0);
        c.row = r / this.size;
        c.col = r % this.size;

        return c;
    }

    protected boolean isPlayerTurn(String turn) {
        return (turn.equals(PC));
    }

    //set starting grid
    protected void setInitialModel(int size) {
        //first white pieces
        Coordinates first_npc = new Coordinates(size / 2, size / 2);
        Coordinates second_npc = new Coordinates(first_npc.row - 1, first_npc.col - 1);
        this.NPC_pieces.add(first_npc);
        this.NPC_pieces.add(second_npc);

        //first black pieces
        Coordinates first_player = new Coordinates(first_npc.row - 1, size / 2);
        Coordinates second_player = new Coordinates(size / 2, first_npc.col - 1);
        this.PLAYER_pieces.add(first_player);
        this.PLAYER_pieces.add(second_player);
    }

    //get the list of possible moves for the current player
    protected ArrayList<Coordinates> getPossibleMoves(ArrayList<Coordinates> player, String turn) {
        Coordinates neigh;
        for (Coordinates c : player) {
            neigh = checkVneighbors(c, turn);
            if (neigh != null) {
                getEmptyPositionV(neigh, turn);
            }
            neigh = checkHneighbors(c, turn);
            if (neigh != null) {
                getEmptyPositionH(neigh, turn);
            }
            neigh = checkDneighbors(c, turn);
            if (neigh != null) {
                getEmptyPositionD(neigh, turn);
            }
        }
        if (isPlayerTurn(turn))
            return this.PLAYER_possible_moves;
        else
            return this.NPC_possible_moves;
    }

    protected void getEmptyPositionD(Coordinates neigh, String turn) {
        if (neigh.row < this.size) {
            Coordinates new_neigh;
            if (this.isPlayerTurn(turn)) {
                if (this.PLAYER_pieces.contains(neigh)) {
                    //direction down to the right
                    new_neigh = new Coordinates(neigh.row + 1, neigh.col + 1);
                    getEmptyPositionH(new_neigh, turn);
                    //direction down to left
                    new_neigh = new Coordinates(neigh.row + 1, neigh.col - 1);
                    getEmptyPositionH(new_neigh, turn);
                    //direction up to right
                    new_neigh = new Coordinates(neigh.row - 1, neigh.col + 1);
                    getEmptyPositionH(new_neigh, turn);
                    //direction up to left
                    new_neigh = new Coordinates(neigh.row - 1, neigh.col - 1);
                    getEmptyPositionH(new_neigh, turn);
                } else if (!this.NPC_pieces.contains(neigh))
                    this.PLAYER_possible_moves.add(neigh);
            } else {
                if (this.NPC_pieces.contains(neigh)) {
                    new_neigh = new Coordinates(neigh.row + 1, neigh.col);
                    getEmptyPositionH(new_neigh, turn);
                    new_neigh = new Coordinates(neigh.row - 1, neigh.col);
                    getEmptyPositionH(new_neigh, turn);
                } else if (!this.PLAYER_pieces.contains(neigh))
                    this.NPC_possible_moves.add(neigh);
            }
        }
    }

    //return the coordinate if the piece belongs to the opponent, diagonal check
    protected Coordinates checkDneighbors(Coordinates c, String turn) {
        Coordinates D_pos_pos = new Coordinates(c.row + 1, c.col + 1);
        Coordinates D_pos = new Coordinates(c.row + 1, c.col - 1);
        Coordinates D_neg_neg = new Coordinates(c.row - 1, c.col - 1);
        Coordinates D_neg = new Coordinates(c.row - 1, c.col + 1);
        if (this.isPlayerTurn(turn)) {
            if (this.NPC_pieces.contains(D_pos_pos))
                return D_pos_pos;
            else if (this.NPC_pieces.contains(D_pos))
                return D_pos;
            else if (this.NPC_pieces.contains(D_neg_neg))
                return D_neg_neg;
            else if (this.NPC_pieces.contains(D_neg))
                return D_neg;
            else
                return null;
        } else {
            if (this.NPC_pieces.contains(D_pos_pos))
                return D_pos_pos;
            else if (this.NPC_pieces.contains(D_pos))
                return D_pos;
            else if (this.NPC_pieces.contains(D_neg_neg))
                return D_neg_neg;
            else if (this.NPC_pieces.contains(D_neg))
                return D_neg;
            else
                return null;
        }
    }

    protected void getEmptyPositionH(Coordinates neigh, String turn) {
        if (neigh.row < this.size) {
            Coordinates new_neigh;
            if (this.isPlayerTurn(turn)) {
                if (this.PLAYER_pieces.contains(neigh)) {
                    new_neigh = new Coordinates(neigh.row + 1, neigh.col);
                    getEmptyPositionH(new_neigh, turn);
                    new_neigh = new Coordinates(neigh.row - 1, neigh.col);
                    getEmptyPositionH(new_neigh, turn);
                } else if (!this.NPC_pieces.contains(neigh))
                    this.PLAYER_possible_moves.add(neigh);
            } else {
                if (this.NPC_pieces.contains(neigh)) {
                    new_neigh = new Coordinates(neigh.row + 1, neigh.col);
                    getEmptyPositionH(new_neigh, turn);
                    new_neigh = new Coordinates(neigh.row - 1, neigh.col);
                    getEmptyPositionH(new_neigh, turn);
                } else if (!this.PLAYER_pieces.contains(neigh))
                    this.NPC_possible_moves.add(neigh);
            }
        }
    }

    //return the coordinate if the piece belongs to the opponent, horizontal check
    protected Coordinates checkHneighbors(Coordinates c, String turn) {
        Coordinates H_pos = new Coordinates(c.row + 1, c.col);
        Coordinates H_neg = new Coordinates(c.row - 1, c.col);
        if (this.isPlayerTurn(turn)) {
            if (this.NPC_pieces.contains(H_pos))
                return H_pos;
            else if (this.NPC_pieces.contains(H_neg))
                return H_neg;
            else
                return null;
        } else {
            if (this.PLAYER_pieces.contains(H_pos))
                return H_pos;
            else if (this.PLAYER_pieces.contains(H_neg))
                return H_neg;
            else
                return null;
        }
    }

    protected void getEmptyPositionV(Coordinates neigh, String turn) {
        if (neigh.col < this.size) {
            Coordinates new_neigh;
            if (this.isPlayerTurn(turn)) {
                if (this.PLAYER_pieces.contains(neigh)) {
                    new_neigh = new Coordinates(neigh.row, neigh.col + 1);
                    getEmptyPositionH(new_neigh, turn);
                    new_neigh = new Coordinates(neigh.row, neigh.col - 1);
                    getEmptyPositionH(new_neigh, turn);
                } else if (!this.NPC_pieces.contains(neigh))

                    this.PLAYER_possible_moves.add(neigh);
            } else {
                if (this.NPC_pieces.contains(neigh)) {
                    new_neigh = new Coordinates(neigh.row, neigh.col + 1);
                    getEmptyPositionH(new_neigh, turn);
                    new_neigh = new Coordinates(neigh.row, neigh.col - 1);
                    getEmptyPositionH(new_neigh, turn);
                } else if (!this.PLAYER_pieces.contains(neigh))
                    //add the rest to flipp_pieces
                    this.NPC_possible_moves.add(neigh);
            }
        }
    }

    //return the coordinate if the piece belongs to the opponent, vertical check
    protected Coordinates checkVneighbors(Coordinates c, String turn) {
        Coordinates V_pos = new Coordinates(c.row, c.col + 1);
        Coordinates V_neg = new Coordinates(c.row, c.col - 1);
        if (this.isPlayerTurn(turn)) {
            if (this.NPC_pieces.contains(V_pos))
                return V_pos;
            else if (this.NPC_pieces.contains(V_neg))
                return V_neg;
            else
                return null;
        } else {
            if (this.PLAYER_pieces.contains(V_pos))
                return V_pos;
            else if (this.PLAYER_pieces.contains(V_neg))
                return V_neg;
            else
                return null;
        }
    }

    protected ArrayList<Coordinates> getFlippPiecesD(Coordinates selected, String turn) {
        ArrayList<Coordinates> flip_pieces_D = new ArrayList<>();
        Coordinates piece_u = new Coordinates(selected.row + 1, selected.col + 1);
        if (piece_u.col == this.size) {
            piece_u = new Coordinates(selected.row + 1, selected.col - 1);
        }
        if (this.isPlayerTurn(turn)) {
            while (!this.PLAYER_pieces.contains(piece_u)) {
                if (this.NPC_pieces.contains(piece_u)) {
                    flip_pieces_D.add(piece_u);
                }
                piece_u = new Coordinates(piece_u.row + 1, piece_u.col + 1);
                if (piece_u.col == this.size || piece_u.row == this.size) {
                    piece_u = new Coordinates(piece_u.row + 1, piece_u.col - 1);
                }
            }
            Coordinates piece_d = new Coordinates(selected.row - 1, selected.col - 1);
            if (piece_d.col == 0) {
                piece_d = new Coordinates(selected.row - 1, selected.col + 1);
            }
            while (!this.PLAYER_pieces.contains(piece_d)) {
                if (this.NPC_pieces.contains(piece_d))
                    flip_pieces_D.add(piece_u);
                piece_u = new Coordinates(piece_u.row - 1, piece_u.col - 1);
                if (piece_u.col == 0 || piece_u.row == this.size)
                    piece_u = new Coordinates(piece_u.row - 1, piece_u.col + 1);
            }
        } else {
            while (!this.PLAYER_pieces.contains(piece_u)) {
                if (this.NPC_pieces.contains(piece_u)) {
                    flip_pieces_D.add(piece_u);
                }
                piece_u = new Coordinates(piece_u.row + 1, piece_u.col + 1);
                if (piece_u.col == this.size || piece_u.row == this.size) {
                    piece_u = new Coordinates(piece_u.row + 1, piece_u.col - 1);
                }
            }
            Coordinates piece_d = new Coordinates(selected.row - 1, selected.col - 1);
            if (piece_d.col == 0) {
                piece_d = new Coordinates(selected.row - 1, selected.col + 1);
            }
            while (!this.PLAYER_pieces.contains(piece_d)) {
                if (this.NPC_pieces.contains(piece_d))
                    flip_pieces_D.add(piece_u);
                piece_u = new Coordinates(piece_u.row - 1, piece_u.col - 1);
                if (piece_u.col == 0 || piece_u.row == this.size)
                    piece_u = new Coordinates(piece_u.row - 1, piece_u.col + 1);
            }
        }
        return flip_pieces_D;
    }

    protected ArrayList<Coordinates> getFlippPiecesH(Coordinates selected, String turn) {
        ArrayList<Coordinates> flip_pieces_H = new ArrayList<>();
        Coordinates piece = new Coordinates(selected.row + 1, selected.col);
        if (piece.col == this.size)
            piece = new Coordinates(selected.row - 1, selected.col);

        if (this.isPlayerTurn(turn)) {
            while (!this.PLAYER_pieces.contains(piece)) {
                if (this.NPC_pieces.contains(piece))
                    flip_pieces_H.add(piece);
                piece = new Coordinates(piece.row + 1, piece.col);
                if (piece.col == this.size)
                    piece = new Coordinates(piece.row - 1, piece.col);
            }
        } else {
            while (!this.PLAYER_pieces.contains(piece)) {
                if (this.NPC_pieces.contains(piece))
                    flip_pieces_H.add(piece);
                piece = new Coordinates(piece.row + 1, piece.col);
                if (piece.col == this.size)
                    piece = new Coordinates(piece.row - 1, piece.col);
            }
        }
        return flip_pieces_H;
    }

    protected ArrayList<Coordinates> getFlippPiecesV(Coordinates selected, String turn) {
        ArrayList<Coordinates> flip_pieces_V = new ArrayList<>();
        Coordinates piece = new Coordinates(selected.row, selected.col + 1);
        if (piece.col == this.size)
            piece = new Coordinates(selected.row, selected.col - 1);

        if (this.isPlayerTurn(turn)) {
            while (!this.PLAYER_pieces.contains(piece)) {
                if (this.NPC_pieces.contains(piece))
                    flip_pieces_V.add(piece);
                piece = new Coordinates(piece.row, piece.col + 1);
                if (piece.col == this.size)
                    piece = new Coordinates(piece.row, piece.col - 1);

            }
        } else {
            while (!this.PLAYER_pieces.contains(piece)) {
                if (this.NPC_pieces.contains(piece))
                    flip_pieces_V.add(piece);
                piece = new Coordinates(piece.row, piece.col + 1);
                if (piece.col == this.size)
                    piece = new Coordinates(piece.row, piece.col - 1);

            }
        }
        return flip_pieces_V;
    }

    protected ArrayList<Coordinates> getAllFlipPieces(Coordinates selected, String turn) {
        ArrayList<Coordinates> flip_pieces = new ArrayList<>();
        //calculate vertical
        for (Coordinates v : getFlippPiecesV(selected, turn)) {
            flip_pieces.add(v);
        }
        //calculate horizontal
        for (Coordinates h : getFlippPiecesH(selected, turn)) {
            flip_pieces.add(h);
        }
        for (Coordinates d : getFlippPiecesD(selected, turn)) {
            flip_pieces.add(d);
        }

        return flip_pieces;
    }

    protected void flipPieces(ArrayList<Coordinates> pieces) {
        for (Coordinates c : pieces) {
            flipPiece(c);
        }
    }

    protected void flipPiece(Coordinates c) {
        if (this.PLAYER_pieces.contains(c)) {
            this.PLAYER_pieces.remove(c);
            this.NPC_pieces.add(c);
        } else {
            this.NPC_pieces.remove(c);
            this.PLAYER_pieces.add(c);
        }
    }

    protected int getTimeElapsed(){
        clock.stop();
        return clock.getTimeElapsed();
    }

    //reversi logic
    public void playGame(int time_left){

        clock.start();

        Boolean GAME_OVER = false;
        while (!GAME_OVER) {
            if (!getRemaining())
                GAME_OVER = true;
            ArrayList<Coordinates>players_moves = playersTurn();
            if (players_moves.size()>0) {
                while (move == 0) {
                    try {
                        TimeUnit.SECONDS.sleep(10);
                        if (!clock.checkClock(time_left))
                            GAME_OVER = true;
                            setReason(R_TIMEOUT);
                    } catch (InterruptedException e) {
                        //exception
                    }
                    //wait
                }
                applyPlayerChoice(getPlayerMove() ,PC);
            }
            ArrayList<Coordinates> npc_moves = npcsTurn();
            if (npc_moves.size()>0){
                //pick the best move
                applyPlayerChoice(getBestMove(npc_moves) ,NPC);
            }else{
                if(checkForBlock(players_moves,npc_moves))
                    GAME_OVER = true;
                    setReason(R_BLOCK);
            }

            }

        }


    protected boolean checkForBlock(ArrayList<Coordinates> player_moves, ArrayList<Coordinates> npc_moves){
        if (player_moves.size() == 0 && npc_moves.size() == 0){
            return true;
        }
        return false;
    }

    //reason why game over
    protected String getReason(){
        return reason;
    }

    protected void setReason(String s){
        this.reason = s;
    }

    protected boolean getRemaining(){
        if(getRemainingCount()==0){
            setReason("Grid Full");
            return false;
        }
        return true;
    }

    protected ArrayList<Coordinates> playersTurn(){
        //set turn to player
        resetPlayerMoves();
        //find the possible moves for the player
        return getPossibleMoves(this.PLAYER_pieces, PC);
    }

    protected ArrayList<Coordinates> npcsTurn() {
        //set turn to npc
        resetNPCMoves();
        //find the possible moves for the npc
        return getPossibleMoves(this.PLAYER_pieces, PC);
    }

    protected void resetPlayerMoves() {
        this.PLAYER_possible_moves = new ArrayList<>();
    }

    protected void resetNPCMoves() {
        this.NPC_possible_moves = new ArrayList<>();
    }

    protected void applyPlayerChoice(Coordinates c,String turn){
        //update clock
        ArrayList<Coordinates> all_flip = getAllFlipPieces(c, turn);
        flipPieces(all_flip);
    }

    //obtain the player pressed buton
    protected Coordinates getPlayerMove(){
        return last_move;
    }
    //set the player pressed buton
    protected void setPlayerMove(Coordinates c){
        last_move = c;
    }

    //npc ai
    protected Coordinates getBestMove(ArrayList<Coordinates> pieces){
        ArrayList<Integer> move_value = new ArrayList<>();

        for (Coordinates c: pieces) {
            int value = 0;
            value+=getFlippPiecesV(c, NPC).size();
            value+=getFlippPiecesD(c, NPC).size();
            value+=getFlippPiecesH(c, NPC).size();
            move_value.add(value,parseCoordinates(c));
        }
        int max = Collections.max(move_value);
        int pos = move_value.indexOf(max);

        return parsePosition(pos);
    }

    public boolean checkIfPlayerPiece(Coordinates c) {
        return this.PLAYER_pieces.contains(c);
    }

    public boolean checkIfNPCPiece(Coordinates c) {
        return this.NPC_pieces.contains(c);
    }

    public boolean checkPlayerPossible(Coordinates c) {
        return this.PLAYER_possible_moves.contains(c);
    }
    public boolean checkIfNPCPossible(Coordinates c) {
        return this.NPC_possible_moves.contains(c);
    }

    public boolean checkNPCPossible(Coordinates c) {
        return this.NPC_possible_moves.contains(c);
    }

    public ArrayList<Integer> getPieces(String who) {
        ArrayList<Integer> result = new ArrayList<>();
        if (who == PC) {
            for (Coordinates c : this.PLAYER_pieces) {
                result.add(this.parseCoordinates(c));
            }
        } else if (who == NPC) {
            for (Coordinates c : this.NPC_pieces) {
                result.add(this.parseCoordinates(c));
            }
        } else if (who == PCp) {
            for (Coordinates c : this.PLAYER_possible_moves) {
                result.add(this.parseCoordinates(c));
            }
        } else {
            for (Coordinates c : this.NPC_possible_moves) {
                result.add(this.parseCoordinates(c));
            }
        }
        return result;
    }

    public void setPlayerPieces(ArrayList<Integer> positions) {
        this.PLAYER_pieces = parseList(positions);
    }

    public void setNPCPieces(ArrayList<Integer> positions) {
        this.NPC_pieces = parseList(positions);
    }

    public ArrayList<Coordinates> parseList(ArrayList<Integer> pieces) {
        ArrayList<Coordinates> result = new ArrayList<>();
        for (Integer position : pieces) {
            result.add(this.parsePosition(position));
        }
        return result;
    }


    //extra
    protected class Coordinates {

        private int row;                          //position x
        private int col;                          //position y

        protected Coordinates(int row, int col) {
            this.row = row;
            this.col = col;
        }


    }

    //extra
    protected class GameTimer {
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

        public int getTimeElapsed() {
            Double d = this.getSeconds();
            Long l = Math.round(d);
            int i = Integer.valueOf(l.intValue());
            return i;
        }

        public boolean checkClock(int time_max) {
            this.stop();
            if (time_max>0){
                if ((time_max-getTimeElapsed()) <= 0){
                    return false;
                }
            }
            return true;
        }
    }

}
