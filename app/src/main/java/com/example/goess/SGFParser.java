package com.example.goess;


import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class SGFParser {

    private static String TAG = "SGFParser";
    private static char BLACK_MOVE = 'B';
    private static char WHITE_MOVE = 'W';
    private static String BLACK_PLAYER_NAME = "PB";
    private static String WHITE_PLAYER_NAME = "PW";

    public SGFParser() {

    }

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


    private GameInfo getGame(String content) {
        String blackPlayerName = "Black";
        String whitePlayerName = "White";
        String md5Input = "";

        ArrayList<Move> list = new ArrayList<>();
     //   String content = getFileContent(filePath);
        Log.i(TAG, "Content " + content);
        int start = content.indexOf(BLACK_PLAYER_NAME);
        int end = content.substring(start).indexOf("]");
        if (start > 0 && end > 0) {
            blackPlayerName = content.substring(start + 3, start + end);
        }
        start = content.indexOf(WHITE_PLAYER_NAME);
        end = content.substring(start).indexOf("]");
        if (start > 0 && end > 0) {
            whitePlayerName = content.substring(start + 3, start + end);
        }

        start = content.indexOf(";B[");
        if (start < 0) {
            Log.e(TAG, "Could not find first move!");
            return null;
        }
        int startOffset = 1;
        if (start == 0)
            startOffset = 0; //should not happen
        Log.i(TAG, "start at " + start + "  " + content);

        char next, a, b;
        int endOffset = content.length() - 4;
        boolean checked = false;

        try {
            for (int i = start - startOffset; i < endOffset; ++i) {
                next = content.charAt(i);
                if (!checked && next == '(') {
                    checked = true;
                    endOffset = content.indexOf(")");
                }
                if (next == BLACK_MOVE || next == WHITE_MOVE) {
                    a = content.charAt(i + 2);
                    b = content.charAt(i + 2 + 1);
                    Move move = new Move(a - 'a', b - 'a', content.charAt(i) == BLACK_MOVE ? Move.Player.BLACK : Move.Player.WHITE);
                //    Log.i(TAG, "add   " + String.valueOf(move.x) + ":" + String.valueOf(move.y));
                    list.add(move);
                    md5Input += a;
                    md5Input += b;
                }
            }
        } catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "IndexOutOfBoundsException: " + e.getMessage());
            list.clear();
        }

        GameInfo gameInfo = new GameInfo();
        gameInfo.moves.addAll(list);
        gameInfo.md5 = md5(md5Input);
        gameInfo.blackPlayerName = blackPlayerName;
        gameInfo.whitePlayerName = whitePlayerName;

        return gameInfo;
    }

    public GameInfo getGameFromString(String content) {
        return getGame(content);
    }

    public GameInfo getGameFromFile(String filePath) {

        String content = getFileContent(filePath);
        return getGame(content);
    }


    private String getFileContent(String filePath) {

        Log.i(TAG, "Reading " + filePath);

        File fl = new File(filePath);
        StringBuilder sb = new StringBuilder();

        try {
            FileInputStream fin = new FileInputStream(fl);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fin));

            String line = null;
            while ((line = reader.readLine()) != null)
                sb.append(line);

            reader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

}
