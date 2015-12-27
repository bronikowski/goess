package com.example.goess;


import java.util.ArrayList;

public class GameInfo {

    ArrayList<Integer> score = new ArrayList<>(100);
    ArrayList<Move> moves = new ArrayList<>();
    String md5;
    String blackPlayerName = "Black";
    String whitePlayerName = "White";

    GameInfo() {

    }

}
