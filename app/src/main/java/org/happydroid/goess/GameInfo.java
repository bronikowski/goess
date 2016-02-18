package org.happydroid.goess;

import java.util.ArrayList;

public class GameInfo {

    ArrayList<Integer> score = new ArrayList<Integer>(1000);
    ArrayList<Move> moves = new ArrayList<Move>();
    String md5;
    String blackPlayerName = "Black";
    String whitePlayerName = "White";
    String result = "";
    String blackPlayerRank = "";
    String whitePlayerRank = "";

    GameInfo() {}

    public String getGameTitle() {
        return blackPlayerName + (blackPlayerRank.length() > 0 ? ( " [" + blackPlayerRank + "]") : "") +
                " vs " + whitePlayerName + (whitePlayerRank.length() > 0 ? (" [" + whitePlayerRank + "]") : "");
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