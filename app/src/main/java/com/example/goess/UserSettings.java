package com.example.goess;


import android.content.SharedPreferences;

public class UserSettings {

    enum Metrics {
        EUCLID,
        TAXICAB
    }
    enum LineSize {
        TINY ,
        NORMAL,
        THICK
    }

    boolean showBoardCoords;
    boolean showIndicator;
    boolean doubleclick;
    boolean zoom;
    Metrics metrics;
    LineSize lineSize;
    SharedPreferences preferences;

    UserSettings(SharedPreferences preferences) {
        this.preferences = preferences;
        if (preferences != null) {
            showBoardCoords = preferences.getBoolean("showBoardCoords", false);
            showIndicator = preferences.getBoolean("showIndicator", true);
            doubleclick = preferences.getBoolean("doubleclick", false);
            zoom = preferences.getBoolean("zoom", true);
            String metric = preferences.getString("metric", Metrics.EUCLID.toString());
            metrics = Metrics.valueOf(metric);
            String line = preferences.getString("lineSize", LineSize.NORMAL.toString());
            lineSize = LineSize.valueOf(line);
        } else {
            setIndicator(true);
            setDoubleClick(false);
            setZoom(true);
            setShowBoardCoords(false);
            setLineSize(LineSize.NORMAL);
            setMetrics(Metrics.EUCLID);
        }
    }

    public void setShowBoardCoords(boolean show) {
        showBoardCoords = show;
        preferences.edit().putBoolean("showBoardCoords", show).apply();
    }

    public void setIndicator(boolean show) {
        showIndicator = show;
        preferences.edit().putBoolean("showIndicator", show).apply();
    }

    public void setZoom(boolean onoff) {
        zoom = onoff;
        preferences.edit().putBoolean("zoom", onoff).apply();
    }

    public void setDoubleClick(boolean click) {
        doubleclick = click;
        preferences.edit().putBoolean("doubleclick", click).apply();
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
