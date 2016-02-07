package com.example.goess;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

public class BoardLogic {

    private static String TAG = "BoardLogic";
    private static int BOARD_SIZE = 19;

    int currentIndex;
    Move.Player currentPlayer;
    Move.Player[][] board;
    GameInfo currentGame;
    public ArrayList<Move> deadStones = new ArrayList<Move>();
    HashMap<Integer, ArrayList<Move>> captureCache = new HashMap<Integer, ArrayList<Move>>();
    double score;
    SGFParser parser = new SGFParser();
    int[][] visited = new int[BOARD_SIZE][BOARD_SIZE];

    public BoardLogic() {
        board = new Move.Player[BOARD_SIZE][BOARD_SIZE];
        initBoard();
    }

    public Move getNextMove() {
        Move move = null;
        if (currentIndex < currentGame.moves.size())
            move = currentGame.moves.get(currentIndex++);
        return move;
    }

    public Move getPreviousMove() {
        Move move = null;
        if ((currentIndex  > 0) && (currentIndex <= currentGame.moves.size()))
            move = currentGame.moves.get(currentIndex - 1);
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

    public GameInfo parseSGFFile(String filePath) {

        currentGame = parser.getGameFromFile(filePath);
        return currentGame;
    }

    public GameInfo parseSGFString(String content) {

        currentGame = parser.getGameFromString(content);
        return currentGame;
    }

    public String getMd5(String input) {
        return parser.md5(input);
    }

    public String getBlackPlayer() {
        return currentGame.blackPlayerName;
    }

    public String getWhitePlayer() {
        return currentGame.whitePlayerName;
    }

    public void clearBoardState() {
        currentIndex = 0;
        score = 0;
        initBoard();
        deadStones.clear();
        captureCache.clear();
    }

    public void removeLastMoveFromBoardState() {
        if (currentIndex > 0) {
            Move lastDrawnMove = currentGame.moves.get(currentIndex - 1);
            currentIndex--;
            board[lastDrawnMove.x][lastDrawnMove.y] = Move.Player.EMPTY;
            currentPlayer = (currentPlayer == Move.Player.BLACK) ? Move.Player.WHITE : Move.Player.BLACK;
        }
     //   Log.i(TAG, "remove, index at " + String.valueOf(currentIndex));
    }

    public ArrayList<Move> restoreBoardState() {

        ArrayList<Move> deadStones = captureCache.get(currentIndex + 1);
        if (deadStones != null)
        Log.i(TAG, "restore from cache  " + String.valueOf(currentIndex + 1) + " size " + String.valueOf(deadStones.size()));
        captureCache.remove(currentIndex + 1);
        return deadStones;
    }

    public void removeFromBoardState(Move move) {
        board[move.x][move.y] = Move.Player.EMPTY;
    }


    public void addMoveToBoardState(Move move) {
        board[move.x][move.y] = move.player;
        currentPlayer = (move.player == Move.Player.BLACK) ? Move.Player.WHITE : Move.Player.BLACK;
        Log.i(TAG, "added move, id    " + String.valueOf(currentGame.moves.size()));
    }

    private ArrayList<Move> getNeighbours(Move move, Move.Player color) {
        ArrayList<Move> neighbours = new ArrayList<Move>();

        int x = move.x;
        int y = move.y;

        if (x != 0 && board[x - 1][y] == color) {
            neighbours.add(new Move(x - 1, y, color));
        }
        if (y != 0 && board[x][y - 1] == color) {
            neighbours.add(new Move(x, y - 1, color));
        }
        if (x != BOARD_SIZE - 1 && board[x + 1][y] == color) {
            neighbours.add(new Move(x + 1, y, color));
        }
        if (y != BOARD_SIZE - 1 && board[x][y + 1] == color) {
            neighbours.add(new Move(x, y + 1, color));
        }

        return neighbours;

    }

    private ArrayList<Move> getNeighboursNotVisited(Move move, int[][] visited) {
        ArrayList<Move> result = new ArrayList<Move>();

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


    private ArrayList<Move> findGroupAt(Move move) {

        ArrayList<Move> group = getNeighboursNotVisited(move, visited);
        ArrayList<Move> result = new ArrayList<Move>();

        if (visited[move.x][move.y] == 0) {
            visited[move.x][move.y] = 1;
            result.add(move);
            for (int i = 0; i < group.size(); ++i) {
                if (visited[group.get(i).x][group.get(i).y] == 0) {
                    ArrayList<Move> n = findGroupAt(group.get(i));
                    result.addAll(n);
                }
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
            for (int i = 0; i < n.size(); ++i) {
                if (visited[n.get(i).x][n.get(i).y] == 0) {
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

        deadStones.clear();

        for (int i = 0; i < BOARD_SIZE; ++i) {
            for (int j = 0; j < BOARD_SIZE; ++j) {
                visited[i][j] = 0;
            }
        }

     //   Log.i(TAG, "check if capturing  nr " + String.valueOf(currentIndex));
        for (int i = 0; i < neighbours.size(); ++i) {
            Move m = neighbours.get(i);
            ArrayList<Move> group = findGroupAt(m);
            if (countLiberties(group) == 0) {
                deadStones.addAll(group);
                ArrayList<Move> tmp = new ArrayList<Move>();
                tmp.addAll(group);
                if (captureCache.containsKey(currentIndex))
                    captureCache.get(currentIndex).addAll(tmp);
                else
                    captureCache.put(currentIndex, tmp);
             //   Log.i(TAG, "add to cache  " + String.valueOf(currentIndex) + " of size " + String.valueOf(captureCache.get(currentIndex).size()));
            }
        }
        return deadStones.size() > 0;
    }

    public boolean isValid(Move move, UserSettings.Metrics metrics) {
        if (board[move.x][move.y] != Move.Player.EMPTY) {
            Log.i(TAG, "not valid move ");
            return false;
        }
        boolean res = false;

        if (currentIndex < currentGame.moves.size()) {
            Move nextMove = currentGame.moves.get(currentIndex);
            if (metrics == UserSettings.Metrics.TAXICAB)
                calculateStandard(nextMove, move);
            else if (metrics == UserSettings.Metrics.EUCLID)
                calculateEuclidean(nextMove, move);

            res = (score == 10);
        }

        if (res)
            currentIndex++;
        return res;
    }

    private void calculateEuclidean(Move nextMove, Move move) {
        double diffX = Math.abs(nextMove.x - move.x);
        double diffY = Math.abs(nextMove.y - move.y);
        if (diffX < 10 && diffY < 10) {
            double maxDiff = Math.sqrt((diffX * diffX) + (diffY * diffY));
            if (maxDiff <= 10)
                score = 10 - maxDiff;
        } else
            score = 0;
    }

    private void calculateStandard(Move nextMove, Move move) {
        double diffX = Math.abs(nextMove.x - move.x);
        double diffY = Math.abs(nextMove.y - move.y);
        double maxDiff = Math.max(diffX, diffY);
        if (maxDiff <= 10)
            score = 10 - maxDiff;
        else
            score = 0;
    }
}
