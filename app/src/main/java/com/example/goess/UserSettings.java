package com.example.goess;


import android.content.SharedPreferences;

public class UserSettings {

    enum Metrics {
        EUCLID,
        STANDARD
    }

    boolean showBoardCoords;
    boolean showIndicator;
    Metrics metrics;
    SharedPreferences preferences;

    UserSettings(SharedPreferences preferences) {
        this.preferences = preferences;
        if (preferences != null) {
            showBoardCoords = preferences.getBoolean("showBoardCoords", false);
            showIndicator = preferences.getBoolean("showIndicator", true);
            metrics = preferences.getBoolean("euclidean", false) ? Metrics.EUCLID : Metrics.STANDARD;
        } else
            setIndicator(true);
    }

    public void setShowBoardCoords(boolean show) {
        showBoardCoords = show;
        preferences.edit().putBoolean("showBoardCoords", show).apply();
    }

    public void setIndicator(boolean show) {
        showIndicator = show;
        preferences.edit().putBoolean("showIndicator", show).apply();
    }

    public void setMetrics(boolean euclid) {
        this.metrics = euclid ? Metrics.EUCLID : Metrics.STANDARD;
        preferences.edit().putBoolean("euclidean", euclid).apply();
    }

}
