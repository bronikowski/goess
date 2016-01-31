package com.example.goess;

import java.util.ArrayList;

public class GameInfo {

    ArrayList<Integer> score = new ArrayList<>(1000);
    ArrayList<Move> moves = new ArrayList<>();
    String md5;
    String blackPlayerName = "Black";
    String whitePlayerName = "White";
    String result = "";

    GameInfo() {}

    public String getGameTitle() {
        return blackPlayerName + " vs " + whitePlayerName;
    }

    public GameInfo(GameInfo game) {
        this.score.clear();
        this.score.addAll(game.score);
        this.moves = game.moves;
        this.md5 = game.md5;
        this.blackPlayerName = game.blackPlayerName;
        this.whitePlayerName = game.whitePlayerName;
        this.result = game.result;
    }

}
