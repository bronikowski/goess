package com.example.goess;


import android.util.Log;
import android.content.Context;
import android.content.res.AssetManager;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class GamesStorage {

    private static String TAG = "GamesStorage";

    static final int DEFAULT_GAMES_LIST_SIZE = 6;
    static final int RECENT_GAMES_LIST_SIZE = 5;

    HashMap<String, GameInfo> recentGamesByName = new HashMap<String, GameInfo>();
    HashMap<String, String> defaultGamesByName = new HashMap<String, String>();
    LinkedList<String> recentGamesQueue = new LinkedList<String>();
    HashMap<String, GameInfo> gamesHistory = new HashMap<String, GameInfo>();

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

                defaultGamesByName.put(getNameFromContent(content), content);
            }
        }
    }

    private String getNameFromContent(String content) {
        SGFParser parser = new SGFParser();
        String blackPlayerName = parser.getFullName(content, SGFParser.BLACK_PLAYER_NAME);
        String whitePlayerName = parser.getFullName(content, SGFParser.WHITE_PLAYER_NAME);

        return blackPlayerName + " vs " + whitePlayerName;
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

    public GameInfo getRecentGameAt(String name) {
        Log.v(TAG, "get recent game " + name + " " + recentGamesByName.get(name).md5);
        return recentGamesByName.get(name);
    }

    public void removeFromGamesHistory(GameInfo game) {
        if (gamesHistory.containsKey(game.md5)) {
            gamesHistory.remove(game.md5);
        }
    }

    public void addToGamesHistory(GameInfo game, int score) {
        game.score.add(score);
        if (!gamesHistory.containsKey(game.md5)) {
            gamesHistory.put(game.md5, game);
        }
        else
            gamesHistory.get(game.md5).score = game.score;
        for (Map.Entry<String, GameInfo> entry : gamesHistory.entrySet()) {
            Log.v(TAG, "game in history: " + entry.getKey());
            for (Integer s : entry.getValue().score)
                Log.v(TAG, "score : " + String.valueOf(s));
        }
    }

    public void addRecentGame(String name, GameInfo game) {

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
        recentGamesByName.put(name, game);
        Log.v(TAG, "add recent game " + name + " " + game.md5 + " with score " + String.valueOf(game.score.size()));
    }

}
