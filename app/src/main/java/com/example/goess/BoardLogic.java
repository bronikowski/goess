package com.example.goess;


import java.util.ArrayList;

public class BoardLogic {

    private static int BOARD_SIZE = 19;

    int currentIndex;

    Move.Player[][] board;
    public ArrayList<Move> movesList;

    public BoardLogic() {
        board = new Move.Player[BOARD_SIZE][BOARD_SIZE];
        initBoard();
        currentIndex = 0;
        movesList = new ArrayList<Move>();

    }

    public Move getNextMove() {
        return movesList.get(currentIndex++);
    }

    private void initBoard() {
        for (int i = 0; i < BOARD_SIZE; ++i) {
            for (int j = 0; j < BOARD_SIZE; ++j)
                board[i][j] = Move.Player.EMPTY;
        }
    }

    public boolean parse(String filePath) {
        SGFParser parser = new SGFParser();
        movesList = parser.getMovesList(filePath);

        return (movesList.size() != 0);
    }

}
