package com.example.goess;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;

public class GamesStorage {

    private static String TAG = "GamesStorage";

    static final int DEFAULT_GAMES_LIST_SIZE = 6;
    static final int RECENT_GAMES_LIST_SIZE = 5;

    HashMap<String, String> recentGamesByName = new HashMap<String, String>();
    HashMap<String, String> defaultGamesByName = new HashMap<String, String>();
    LinkedList<String> recentGamesQueue = new LinkedList<String>();

    Context context;

    GamesStorage(Context context) {
        this.context = context;
        initDefault();
    }

    private void initDefault() {
        String[] paths = new String[0];
        AssetManager am = context.getResources().getAssets();
        try {
            paths = am.list("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < paths.length; ++i) {
            if (paths[i].endsWith(".sgf")) {
                Log.i(TAG, "Reading " + paths[i]);
                String content = null;
                try {
                    content = getFileContent(am.open(paths[i]));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "Content " + ((content != null) ? String.valueOf(content.length()) : "(null)"));
                defaultGamesByName.put(paths[i], content);
            }
        }
    }

    private String getFileContent(InputStream inputStream) {
        StringBuilder sb = new StringBuilder();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

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

    public String getDefaultGameAt(String name) {
        return defaultGamesByName.get(name);
    }

    public String getRecentGameAt(String name) {
        return recentGamesByName.get(name);
    }

    public void addRecentGame(String name, String sgf) {

        if (!recentGamesByName.containsKey(name)) {
            if (recentGamesByName.size() < RECENT_GAMES_LIST_SIZE)
                recentGamesQueue.addFirst(name);
            else {
                String last = recentGamesQueue.removeLast();
                recentGamesByName.remove(last);
                recentGamesQueue.addFirst(name);
            }
        } else {
            recentGamesQueue.remove(name);
            recentGamesQueue.addFirst(name);
        }
        recentGamesByName.put(name, sgf);
    }

}
