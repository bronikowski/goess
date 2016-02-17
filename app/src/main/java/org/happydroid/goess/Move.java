package org.happydroid.goess;


public class Move {

    public enum Player {
        EMPTY,
        BLACK,
        WHITE
    }

    int x;
    int y;
    Player player;

    public Move(int x, int y, Player player) {
        this.x = x;
        this.y = y;
        this.player = (player == Player.BLACK) ? Player.BLACK : Player.WHITE;
    }

}
