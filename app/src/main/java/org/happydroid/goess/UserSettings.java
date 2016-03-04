package org.happydroid.goess;


import android.content.SharedPreferences;

public class UserSettings {

    enum Hint {
        DISTANCE,
        AREA,
        NONE
    }
    enum LineSize {
        TINY,
        NORMAL,
        THICK
    }

    enum Zoom {
        CENTER_TOUCH,
        CENTER_CANVAS,
        NONE
    }

    enum AutoMove {
        THREE,
        FIVE,
        NONE
    }

    enum State {
        GAME,
        EDIT
    }

    enum Mode {
        GAME,
        VIEW_ONLY
    }

    boolean showAbout;
    boolean showBoardCoords;
    boolean showIndicator;
    boolean doubleclick;
    boolean showFirstMoves;
    boolean markWrongGuess;
    Hint hint;
    LineSize lineSize;
    Zoom zoom;
    AutoMove autoMove;
    SharedPreferences preferences;
    State state;
    Mode mode;
    String lastActiveGameMd5;

    UserSettings(SharedPreferences preferences) {
        this.preferences = preferences;
        if (preferences != null) {
            showAbout = preferences.getBoolean("showAbout", true);
            showBoardCoords = preferences.getBoolean("showBoardCoords", true);
            showIndicator = preferences.getBoolean("showIndicator", true);
            doubleclick = preferences.getBoolean("doubleclick", true);
            showFirstMoves = preferences.getBoolean("showFirstMoves", true);
            markWrongGuess = preferences.getBoolean("markWrongGuess", true);
            String hint = preferences.getString("hint", Hint.DISTANCE.toString());
            this.hint = Hint.valueOf(hint);
            String line = preferences.getString("lineSize", LineSize.NORMAL.toString());
            lineSize = LineSize.valueOf(line);
            String zoomkind = preferences.getString("zoom", Zoom.NONE.toString());
            zoom = Zoom.valueOf(zoomkind);
            String automove = preferences.getString("autoMove", AutoMove.THREE.toString());
            autoMove = AutoMove.valueOf(automove);
            String sate = preferences.getString("state", State.GAME.toString());
            this.state = State.valueOf(sate);
            String mode = preferences.getString("mode", Mode.GAME.toString());
            this.mode = Mode.valueOf(mode);
            lastActiveGameMd5 = preferences.getString("lastGame", "");
        } else {
            setShowAbout(true);
            setIndicator(true);
            setDoubleClick(false);
            setShowBoardCoords(true);
            setMarkWrongGuess(true);
            setLineSize(LineSize.NORMAL);
            setHint(Hint.DISTANCE);
            setZoom(Zoom.NONE);
            setAutoMove(AutoMove.THREE);
            setState(State.GAME);
            setMode(Mode.GAME);
            setLastActiveGame("");
        }
    }


    public void setLastActiveGame(String md5) {
        lastActiveGameMd5 = md5;
        preferences.edit().putString("lastGame", md5).apply();
    }

    public void setShowAbout(boolean show) {
        showAbout = show;
        preferences.edit().putBoolean("showAbout", show).apply();
    }

    public void setShowBoardCoords(boolean show) {
        showBoardCoords = show;
        preferences.edit().putBoolean("showBoardCoords", show).apply();
    }

    public void setIndicator(boolean show) {
        showIndicator = show;
        preferences.edit().putBoolean("showIndicator", show).apply();
    }

    public void setZoom(Zoom zoom) {
        this.zoom = zoom;
        preferences.edit().putString("zoom", zoom.toString()).apply();
    }

    public void setDoubleClick(boolean click) {
        doubleclick = click;
        preferences.edit().putBoolean("doubleclick", click).apply();
    }

    public void setHint(Hint hint) {
        this.hint = hint;
        preferences.edit().putString("hint", hint.toString()).apply();
    }

    public void setLineSize(LineSize size) {
        this.lineSize = size;
        preferences.edit().putString("lineSize", size.toString()).apply();
    }

    public void setShowFirstMoves(boolean show) {
        showFirstMoves = show;
        preferences.edit().putBoolean("showFirstMoves", show).apply();
    }

    public void setMarkWrongGuess(boolean mark) {
        markWrongGuess = mark;
        preferences.edit().putBoolean("markWrongGuess", mark).apply();
    }

    public void setAutoMove(AutoMove automove) {
        this.autoMove = automove;
        preferences.edit().putString("autoMove", automove.toString()).apply();
    }

    public void setState(State state) {
        this.state = state;
        preferences.edit().putString("state", state.toString()).apply();
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        preferences.edit().putString("mode", mode.toString()).apply();
    }
}
