package com.example.goess;


import android.graphics.Point;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class SGFParser {

    private static String TAG = "SGFParser";
    private static String BLACK_MOVE = "B";
    private static String WHITE_MOVE = "W";


    public SGFParser() {

    }

    public ArrayList<Point> getMovesList(String filePath) {

        ArrayList<Point> list = new ArrayList<Point>();
        String content = getFileContent(filePath);
        Log.i(TAG, "Content " + content);

        int start = content.indexOf(";B[");
        if (start < 0)
            Log.e(TAG, "Could not find first move!");
        content = content.substring(start + 1);
        Log.i(TAG, "start at " + start + "  " + content);

        char letter;
        for (int i = 0; i < content.length() - 1; i += 6) {
            letter = content.charAt(i + 2);
            Point p = new Point();
            p.x = letter - 'a' + 1;
            letter = content.charAt(i + 2 + 1);
            p.y = letter - 'a' + 1;
            list.add(p);
        }

        return list;
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
                sb.append(line).append("\n");

            reader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
