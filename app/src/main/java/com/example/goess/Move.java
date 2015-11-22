package com.example.goess;


public class Move {

    public enum Player {
        EMPTY,
        BLACK,
        WHITE
    }

    int x;
    int y;
    Player player;

    public Move(int x, int y, boolean black) {
        this.x = x;
        this.y = y;
        this.player = black ? Player.BLACK : Player.WHITE;
    }

}
