package com.example.goess;


import android.graphics.Point;
import java.util.ArrayList;

public class BoardLogic {

    private static int BOARD_SIZE = 19;

    public enum BoardState {
        EMPTY,
        BLACK,
        WHITE
    }

    BoardState[][] board;
    public ArrayList<Point> movesList;

    public BoardLogic() {
        board = new BoardState[BOARD_SIZE][BOARD_SIZE];
        initBoard();
        movesList = new ArrayList<Point>();

    }


    private void initBoard() {
        for (int i = 0; i < BOARD_SIZE; ++i) {
            for (int j = 0; j < BOARD_SIZE; ++j)
                board[i][j] = BoardState.EMPTY;
        }
    }

}
