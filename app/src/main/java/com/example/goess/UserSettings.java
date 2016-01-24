package com.example.goess;


import android.content.SharedPreferences;

public class UserSettings {

    enum Metrics {
        EUCLID,
        STANDARD
    }
    enum LineSize {
        TINY ,
        NORMAL,
        FAT
    }


    boolean showBoardCoords;
    boolean showIndicator;
    Metrics metrics;
    LineSize lineSize;
    SharedPreferences preferences;

    UserSettings(SharedPreferences preferences) {
        this.preferences = preferences;
        if (preferences != null) {
            showBoardCoords = preferences.getBoolean("showBoardCoords", false);
            showIndicator = preferences.getBoolean("showIndicator", true);
            String metric = preferences.getString("metric", Metrics.STANDARD.toString());
            metrics = Metrics.valueOf(metric);
            String line = preferences.getString("lineSize", LineSize.NORMAL.toString());
            lineSize = LineSize.valueOf(line);
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

    public void setMetrics(Metrics metric) {
        this.metrics = metric;
        preferences.edit().putString("metric", metric.toString()).apply();
    }

    public void setLineSize(LineSize size) {
        this.lineSize = size;
        preferences.edit().putString("lineSize", size.toString()).apply();
    }

}
