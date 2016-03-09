package org.happydroid.goess;


import android.content.SharedPreferences;
import android.util.Log;
import android.content.Context;
import android.content.res.AssetManager;

import com.google.android.gms.games.Game;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class GamesStorage {

    private static String TAG = "GamesStorage";

    static final int RECENT_GAMES_LIST_SIZE = 5;
    static final int REPO_GAMES_LIST_SIZE = 30;
    private static String APP_PREFERENCES_REPO_GAMES = "GoessRepoGames";
    private static String APP_PREFERENCES_PLAYED_GAMES = "GoessPlayedGames";
    private static String APP_PREFERENCES_RECENT_GAMES = "GoessRecentGames";
    private static String APP_PREFERENCES_TODAYS_GAME = "GoessTodaysGame";

    String todaysGameSgf = "";

    int recentMd5Index;


    LinkedList<String> recentGamesMd5Queue = new LinkedList<String>();
    HashMap<Integer, String> repoSgfsById = new HashMap<Integer, String>();
    HashMap<String, GameInfo> playedGamesByMd5 = new HashMap<String, GameInfo>();

    String[] repoGameNamesForDisplay;
    String[] recentGameNamesForDisplay;

    Context context;

    GamesStorage(Context context) {

        this.context = context;
        recentMd5Index = 0;

        SharedPreferences  prefs = context.getSharedPreferences(APP_PREFERENCES_TODAYS_GAME, Context.MODE_PRIVATE);
        String todaysGame = prefs.getString("todaysGame", "");

        loadRepoSgfsFromSharedPrefs();
        loadRecentMd5FromSharedPrefs();

        if (repoSgfsById.size() == 0) {
            Log.v(TAG, "Repo games from SP empty, reading defaults");
            loadDefaultGames();
            saveRepoSgfsToSharedPrefs();
        }

        prepareRepoGameNames();
        prepareRecentGameNames();

    }

    public void loadRecentMd5FromSharedPrefs() {

        HashMap<Integer, String> recentGamesMd5 = getRecentGamesMd5FromSharedPrefs();

        SortedSet<Integer> keys = new TreeSet<Integer>(recentGamesMd5.keySet());
        for (Integer key : keys) {
           String value = recentGamesMd5.get(key);
           recentGamesMd5Queue.add(value);
        }

    }

    public void loadPlayedGamesFromSharedPrefs() {
        SharedPreferences  prefs = context.getSharedPreferences(APP_PREFERENCES_PLAYED_GAMES, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        HashMap<String, String> map= (HashMap<String, String>) prefs.getAll();
        for (String s : map.keySet()) {
            String js = map.get(s);
            GameInfo g = gson.fromJson(js, GameInfo.class);
            playedGamesByMd5.put(g.md5, g);
            Log.v(TAG, " >>>>>>> load from SP game played " + g.md5);
        }

    }

    private void prepareRecentGameNames() {

        HashMap<Integer, String> recentGamesMd5 = getRecentGamesMd5FromSharedPrefs();

        recentGameNamesForDisplay = new String[recentGamesMd5.size()];
        for (Integer i : recentGamesMd5.keySet()) {
            String title = getGameTitleByMd5(recentGamesMd5.get(i));
            if (title.length() > 0) {
                recentGameNamesForDisplay[i] = getGameTitleByMd5(recentGamesMd5.get(i));
                Log.v(TAG, ">>> load recent name " + i + " " +title);
            }
        }

    }

    private void prepareRepoGameNames() {
        repoGameNamesForDisplay = new String[repoSgfsById.size()];
        for (Integer i : repoSgfsById.keySet()) {
            Log.v(TAG, ">>> add name " + i + " "  + getNameFromContent(repoSgfsById.get(i)));
            repoGameNamesForDisplay[i] = (getNameFromContent(repoSgfsById.get(i)));
        }
    }

    private void loadRepoSgfsFromSharedPrefs() {
        SharedPreferences prefs = context.getSharedPreferences(APP_PREFERENCES_REPO_GAMES, Context.MODE_PRIVATE);
        HashMap<String, String> map = (HashMap<String, String>) prefs.getAll();

        for (String s : map.keySet()) {
            String val = map.get(s);

            Log.v(TAG, "load repo games from SP, game id: " + s);
            repoSgfsById.put(Integer.parseInt(s), val);

        }

    }


    public HashMap<Integer, String> getRecentGamesMd5FromSharedPrefs() {
        SharedPreferences  prefs = context.getSharedPreferences(APP_PREFERENCES_RECENT_GAMES, Context.MODE_PRIVATE);
        HashMap<Integer, String> recentMd5ById = new HashMap<Integer, String>();
        HashMap<String, String> map = (HashMap<String, String>) prefs.getAll();
        for (String s : map.keySet()) {
            String val = map.get(s);
            recentMd5ById.put(Integer.parseInt(s), val);
        }

        return recentMd5ById;
    }

    public void saveToRecentGamesMd5SharedPrefs() {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_PREFERENCES_RECENT_GAMES, Context.MODE_PRIVATE).edit();

        ListIterator<String> listIterator = recentGamesMd5Queue.listIterator();
        int id = 0;
        while (listIterator.hasNext()) {
            editor.putString(String.valueOf(id), listIterator.next());
            editor.commit();
            id++;
	    }

    }

    public String getGameTitleByMd5(String md5) {
        Log.v(TAG, "request game played " + md5);
        GameInfo game = playedGamesByMd5.get(md5);
        String title = "";
        if (game != null)
            title = game.getGameTitleWithRanks();
        return title;
    }


    public void setTodaysGame(String sgf) {
        if (!todaysGameSgf.equals(sgf)) {
            Log.v(TAG, ">>>>>>> replace todays game" + todaysGameSgf);
            if (todaysGameSgf.length() > 0) {

                for (int i = repoSgfsById.size() - 2; i >= 0; --i) {
                    String tmp = repoSgfsById.get(i);
                    repoSgfsById.remove(i);
                    repoSgfsById.put(i + 1, tmp);
                }
                repoSgfsById.put(0, todaysGameSgf);
                //check if remove also from playedgames
                prepareRepoGameNames();

                Log.v(TAG, ">>>>>>> replace todays game with" + repoGameNamesForDisplay[0]);

                //updatelist
                saveRepoSgfsToSharedPrefs();
            }
            this.todaysGameSgf = sgf;

        }
    }

    private void loadDefaultGames() {
        String[] paths = new String[0];
        AssetManager am = context.getResources().getAssets();
        try {
            paths = am.list("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        int idx = 0;
        for (int i = 0; i < paths.length; ++i) {

            if (paths[i].endsWith(".sgf")) {
                Log.i(TAG, "Reading game " + paths[i]);
                String content = null;
                try {
                    content = getFileContent(am.open(paths[i]));
                } catch (IOException e) {
                    e.printStackTrace();
                }

           //     repoGameNamesForDisplay.add(getNameFromContent(content));
                if (content != null && (repoSgfsById.size() < REPO_GAMES_LIST_SIZE)) {
                    repoSgfsById.put(idx++, content);
                    Log.i(TAG, "add default game at  " + String.valueOf(idx) + "  " + getNameFromContent(content));
                }
            }
        }
    }

    private void saveRepoSgfsToSharedPrefs() {

        SharedPreferences.Editor editor = context.getSharedPreferences(APP_PREFERENCES_REPO_GAMES, Context.MODE_PRIVATE).edit();
        for (int i = 0; i < repoSgfsById.size(); ++i) {
            editor.putString(String.valueOf(i), repoSgfsById.get(i));
            editor.commit();
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


    public GameInfo getRecentGameById(int id) {
        Log.v(TAG, "get recent game by id " + id);
        return playedGamesByMd5.get(recentGamesMd5Queue.get(id));
    }


    public void removeFromPlayedGames(GameInfo game) { // remove when not in reposgfs and recents
        if (playedGamesByMd5.containsKey(game.md5)) {
            playedGamesByMd5.remove(game.md5);
        }
    }


    public void updateScoreInPlayedGames(GameInfo game, float score) {
        game.score.add(score);
        if (!playedGamesByMd5.containsKey(game.md5)) {
            playedGamesByMd5.put(game.md5, game);
        }
        else
            playedGamesByMd5.get(game.md5).score = game.score;
        for (Map.Entry<String, GameInfo> entry : playedGamesByMd5.entrySet()) {
            Log.v(TAG, "Played game: " + entry.getKey());
            for (Float s : entry.getValue().score)
                Log.v(TAG, "score : " + String.valueOf((float)s));
        }
    }

    public void addToPlayedGames(String md5, GameInfo game) {
        playedGamesByMd5.put(md5, game);
        Log.v(TAG, "put game played " + game.getGameTitleWithRanks());
    }

    public void addRecentGame(String md5) { //check if remove also from playedgames

        if (!recentGamesMd5Queue.contains(md5)) {
            if (recentGamesMd5Queue.size() < RECENT_GAMES_LIST_SIZE)
                recentGamesMd5Queue.addFirst(md5);
            else {
                recentGamesMd5Queue.removeLast();
                recentGamesMd5Queue.addFirst(md5);
            }
        } else {
            recentGamesMd5Queue.remove(md5);
            recentGamesMd5Queue.addFirst(md5);
        }

        saveToRecentGamesMd5SharedPrefs();
        prepareRecentGameNames();

    }

}
