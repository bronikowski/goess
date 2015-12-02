package com.example.goess;

import android.util.Log;

import java.util.ArrayList;

public class BoardLogic {

    private static String TAG = "BoardLogic";
    private static int BOARD_SIZE = 19;

    int currentIndex;
    Move.Player currentPlayer;
    Move.Player[][] board;
    public ArrayList<Move> movesList;
    int score;

    public BoardLogic() {
        board = new Move.Player[BOARD_SIZE][BOARD_SIZE];
        initBoard();
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
        currentIndex = 0;
        currentPlayer = Move.Player.BLACK;
    }

    public boolean parseSGFFile(String filePath) {
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
            currentPlayer = (currentPlayer == Move.Player.BLACK) ? Move.Player.WHITE : Move.Player.BLACK;
        }
    }

    public void addMoveToBoardState(Move move) {
        board[move.x - 1][move.y - 1] = move.player;
        currentPlayer = (move.player == Move.Player.BLACK) ? Move.Player.WHITE : Move.Player.BLACK;

        if (isCapturing(move))
            Log.i(TAG, "Capturing");

    }

    private ArrayList<Move> getNeighbours(Move move, Move.Player color) {
        ArrayList<Move> neighbours = new ArrayList<>();

        int x = move.x;
        int y = move.y;

        if (x != 0  && board[x - 1][y] == color)
            neighbours.add(new Move(x - 1, y, color));
        if (y != 0  && board[x][y - 1] == color)
            neighbours.add(new Move(x, y - 1, color));
        if (x != BOARD_SIZE - 1 &&  board[x + 1][y] == color)
            neighbours.add(new Move(x + 1, y, color));
        if (y != BOARD_SIZE - 1 &&  board[x][y + 1] == color)
            neighbours.add(new Move(x, y + 1, color));


        return neighbours;

    }

    private ArrayList<Move> getNeighboursNotVisited(Move move, int[][] visited) {
        ArrayList<Move> result = new ArrayList<>();

        int x = move.x;
        int y = move.y;

        if (x != 0  && board[x - 1][y] == move.player && visited[x - 1][y] == 0)
            result.add(new Move(x - 1, y, move.player));
        if (y != 0  && board[x][y - 1] == move.player && visited[x][y - 1] == 0)
            result.add(new Move(x, y - 1, move.player));
        if (x != BOARD_SIZE - 1 &&  board[x + 1][y] == move.player && visited[x + 1][y] == 0)
            result.add(new Move(x + 1, y, move.player));
        if (y != BOARD_SIZE - 1 &&  board[x][y + 1] == move.player && visited[x][y + 1] == 0)
            result.add(new Move(x, y + 1, move.player));


        return result;

    }

    private ArrayList<Move> findGroupAt(Move move, int[][] visited) {
        if (visited == null) {
            visited = new int[BOARD_SIZE][BOARD_SIZE];
            for (int i = 0; i < BOARD_SIZE; ++i) {
                for (int j = 0; j < BOARD_SIZE; ++j) {
                    visited[i][j] = 0;
                }
            }
        }
        ArrayList<Move> group = getNeighboursNotVisited(move, visited);
        ArrayList<Move> result = new ArrayList<>();
        result.add(move);
        visited[move.x][move.y] = 1;

        for (int i = 0; i < group.size(); ++i) {
            if (visited[group.get(i).x][group.get(i).y] == 0) {
                ArrayList<Move> n = getNeighboursNotVisited(move, visited);
                result.addAll(n);
            }
        }
        return result;
    }

    private int countLiberties(ArrayList<Move> group) {

        int[][] visited = new int[BOARD_SIZE][BOARD_SIZE];
        for (int i = 0; i < BOARD_SIZE; ++i) {
            for (int j = 0; j < BOARD_SIZE; ++j) {
                visited[i][j] = 0;
            }
        }

        int liberties = 0;

        for (int j = 0; j < group.size(); ++j) {
            ArrayList<Move> n = getNeighbours(group.get(j), Move.Player.EMPTY);
            for (int i = 0; i < n.size(); ++i)
            {
                if (visited[n.get(i).x][n.get(i).y] == 0)
                {
                    visited[n.get(i).x][n.get(i).y] = 1;
                    ++liberties;
                }

            }
        }

        return liberties;
    }

    public boolean isCapturing(Move move) {
        Move.Player color = (move.player == Move.Player.BLACK) ? Move.Player.WHITE : Move.Player.BLACK;
        ArrayList<Move> neighbours = getNeighbours(move, color);

        for (int i = 0; i < neighbours.size(); ++i) {
            Move m = neighbours.get(i);
            ArrayList<Move> group = findGroupAt(m, null);
            if (countLiberties(group) == 0)
                return true;
        }
        return false;
    }

    public boolean isValid(Move move) {

        if (board[move.x - 1][move.y - 1] != Move.Player.EMPTY)
            return false;

        boolean res = false;

        if (currentIndex < movesList.size()) {
            Move nextMove = movesList.get(currentIndex);
            int diffX = Math.abs(nextMove.x - move.x);
            int diffY = Math.abs(nextMove.y - move.y);
            int maxDiff = Math.max(diffX, diffY);
            if (maxDiff <= 10)
                score = 10 - maxDiff;
            else
                score = 0;
            res = (score == 10);
        }

        if (res)
            currentIndex++;

        return res;
    }
}
