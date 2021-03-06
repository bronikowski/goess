package org.happydroid.goess;


import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import org.happydroid.goess.sgfparser.SGF;
import org.happydroid.goess.sgfparser.SGFException;

public class SGFParser {

    private static String TAG = "SGFParser";

    public SGFParser() {}

    public String md5(String in) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.reset();
            digest.update(in.getBytes());
            byte[] a = digest.digest();
            int len = a.length;
            StringBuilder sb = new StringBuilder(len << 1);
            for (int i = 0; i < len; i++) {
                sb.append(Character.forDigit((a[i] & 0xf0) >> 4, 16));
                sb.append(Character.forDigit(a[i] & 0x0f, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getFullName(String content) {

        StringReader sr = new StringReader(content);
        BufferedReader reader = new BufferedReader(sr);
        String name = "";
        SGF sgf = new SGF(SGF.GAME_TYPE.GO, "Goess");

        try {
            sgf.load(reader);
            reader.close();
            String blackName = sgf.getBlackName();
            String blackRank = sgf.getBlackRank();
            blackName += " [" + (blackRank.length() > 0 ? blackRank : "?") + "]";
            String whiteName = sgf.getWhiteName();
            String whiteRank = sgf.getWhiteRank();
            whiteName += " [" + (whiteRank.length() > 0 ? whiteRank : "?")  + "]";
            name = blackName + " vs " + whiteName;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return name;
    }

    public GameInfo getGameFromString(String content) {

        StringReader sr = new StringReader(content);
        BufferedReader reader = new BufferedReader(sr);

        return parseSGF(reader);
    }

    public GameInfo getGameFromFile(String filePath) {

        File fl = new File(filePath);
        GameInfo gameInfo = null;

        try {
            FileInputStream fin = new FileInputStream(fl);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
            gameInfo = parseSGF(reader);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return gameInfo;
    }

    public String getMd5FromFile(String content) {
        SGF sgf = new SGF(SGF.GAME_TYPE.GO, "Goess");

        String md5Input = "";
        try {
            StringReader sr= new StringReader(content);
            BufferedReader reader= new BufferedReader(sr);
            sgf.load(reader);
            reader.close();
            ArrayList<SGF.Coord> coordsList = sgf.getMainLine();
            ArrayList<Move> list = new ArrayList<Move>();

            SGF.Player player = sgf.firstToMove();

            for (SGF.Coord coord : coordsList) {
                if (!coord.equals(SGF.PASS)) {
                    Move move = new Move(coord.col(), coord.row(), player == SGF.Player.BLACK ? Move.Player.BLACK : Move.Player.WHITE);
                    player = (player == SGF.Player.BLACK) ? SGF.Player.WHITE : SGF.Player.BLACK;
                    list.add(move);
                    md5Input += coord.data();
                }
            }
        } catch (SGFException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return md5(md5Input);
    }

    private GameInfo parseSGF(BufferedReader reader) {
        SGF sgf = new SGF(SGF.GAME_TYPE.GO, "Goess");
        GameInfo gameInfo = new GameInfo();
        try {
            sgf.load(reader);
            reader.close();
            ArrayList<SGF.Coord> coordsList = sgf.getMainLine();
            ArrayList<Move> list = new ArrayList<Move>();

            SGF.Player player = sgf.firstToMove();
            String md5Input = "";

            for (SGF.Coord coord : coordsList) {
                if (!coord.equals(SGF.PASS)) {
                    Move move = new Move(coord.col(), coord.row(), player == SGF.Player.BLACK ? Move.Player.BLACK : Move.Player.WHITE);
                    player = (player == SGF.Player.BLACK) ? SGF.Player.WHITE : SGF.Player.BLACK;
                    list.add(move);
                    md5Input += coord.data();
                }
            }
            gameInfo.moves.addAll(list);
            gameInfo.md5 = md5(md5Input);
            gameInfo.blackPlayerName = sgf.getBlackName();
            gameInfo.whitePlayerName = sgf.getWhiteName();
            gameInfo.blackPlayerRank = sgf.getBlackRank();
            gameInfo.whitePlayerRank = sgf.getWhiteRank();
            gameInfo.result = sgf.getVerbatimResult();
            gameInfo.date = sgf.getDate();
            gameInfo.event = sgf.getEvent();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return gameInfo;
    }
}
