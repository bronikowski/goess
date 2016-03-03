package org.happydroid.goess;

import java.util.ArrayList;

public class GameInfo {

    ArrayList<Float> score = new ArrayList<Float>(1000);
    ArrayList<Move> moves = new ArrayList<Move>();
    String md5;
    String blackPlayerName = "Black";
    String whitePlayerName = "White";
    String result = "";
    String blackPlayerRank = "";
    String whitePlayerRank = "";
    String date = "";
    String event = "";

    GameInfo() {}

    public String getGameTitleWithRanks() {
        return blackPlayerName + (blackPlayerRank.length() > 0 ? ( " [" + blackPlayerRank + "]") : "") +
                " vs " + whitePlayerName + (whitePlayerRank.length() > 0 ? (" [" + whitePlayerRank + "]") : "");
    }

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
        this.date = game.date;
        this.event = game.event;
    }

}
