package com.example.goess;


import java.util.ArrayList;

public class BoardLogic {

    private static String TAG = "BoardLogic";
    private static int BOARD_SIZE = 19;

    int currentIndex;
    Move.Player currentPlayer;

    Move.Player[][] board;
    public ArrayList<Move> movesList;

    public BoardLogic() {
        board = new Move.Player[BOARD_SIZE][BOARD_SIZE];
        initBoard();
        currentIndex = 0;
        currentPlayer = Move.Player.BLACK;
        movesList = new ArrayList<Move>();
    }

    public Move getNextMove() {
        Move move = null;
        if (currentIndex < movesList.size())
            move = movesList.get(currentIndex++);
        return move;
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

    public void clearBoardState() {
        currentIndex = 0;
        initBoard();
    }

    public void removeLastMoveFromBoardState() {
        if (currentIndex > 0) {
            Move lastDrawnMove = movesList.get(currentIndex - 1);
            currentIndex--;
            board[lastDrawnMove.x - 1][lastDrawnMove.y - 1] = Move.Player.EMPTY;
        }
    }

    public void addMoveToBoardState(Move move) {
        board[move.x - 1][move.y - 1] = move.player;
        currentPlayer = (move.player == Move.Player.BLACK) ? Move.Player.WHITE : Move.Player.BLACK;
    }

    public boolean isValid(Move move) {
        return (board[move.x - 1][move.y - 1] == Move.Player.EMPTY);
    }
}
