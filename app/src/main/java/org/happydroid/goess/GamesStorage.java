package org.happydroid.goess;


import android.util.Log;
import android.content.Context;
import android.content.res.AssetManager;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class GamesStorage {

    private static String TAG = "GamesStorage";

    static final int RECENT_GAMES_LIST_SIZE = 5;

    HashMap<String, GameInfo> recentGamesByName = new HashMap<String, GameInfo>();
    LinkedList<String> recentGamesQueue = new LinkedList<String>();

    HashMap<String, String> defaultGamesByName = new HashMap<String, String>();
    ArrayList<String> repoGameNames = new ArrayList<String>();

    HashMap<String, GameInfo> playedGames = new HashMap<String, GameInfo>(); // in repogames?

    Context context;

    GamesStorage(Context context) {
        this.context = context;
        initDefaultGames(); // if no list from prefs
    }

    private void initDefaultGames() {
        String[] paths = new String[0];
        AssetManager am = context.getResources().getAssets();
        try {
            paths = am.list("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < paths.length; ++i) {

            if (paths[i].endsWith(".sgf")) {
                Log.i(TAG, "Reading game " + paths[i]);
                String content = null;
                try {
                    content = getFileContent(am.open(paths[i]));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "Content " + ((content != null) ? String.valueOf(content.length()) : "(null)"));

                defaultGamesByName.put(getNameFromContent(content), content);
                repoGameNames.add(getNameFromContent(content));
            }

        }
    }

    private String getNameFromContent(String content) {
        SGFParser parser = new SGFParser();
        return parser.getFullName(content);
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

    public void removeFromPlayedGames(GameInfo game) { //todo when repo update is done
        if (playedGames.containsKey(game.md5)) {
            playedGames.remove(game.md5);
        }
    }

    public void addScoreToPlayedGames(GameInfo game, float score) {
        game.score.add(score);
        if (!playedGames.containsKey(game.md5)) {
            playedGames.put(game.md5, game);
        }
        else
            playedGames.get(game.md5).score = game.score;
        for (Map.Entry<String, GameInfo> entry : playedGames.entrySet()) {
            Log.v(TAG, "Played game: " + entry.getKey());
            for (Float s : entry.getValue().score)
                Log.v(TAG, "score : " + String.valueOf((float)s));
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
