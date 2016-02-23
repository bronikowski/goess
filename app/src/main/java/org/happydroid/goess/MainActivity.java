package org.happydroid.goess;

import android.text.Html;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.nononsenseapps.filepicker.FilePickerActivity;
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;


public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";
    private static String APP_PREFERENCES = "GoessSettings";
    private static String APP_PREFERENCES_HISTORY = "GoessGameHistory";
    private static String APP_PREFERENCES_RECENT_GAMES = "GoessRecentGames";
    private static String FILE_EXT = "sgf";
    private static int FILE_PICKER_REQUEST_CODE = 1;
    private static int GAME_REPO_REQUEST_CODE = 2;
    private static int BOARD_SIZE = 19;
    private static float BOARD_SCALE_FACTOR = 1.8f;

    GoImages goImages;
    Bitmap rawBoardBitmap;
    ImageView boardImage;
    ImageView stoneImage;
    ImageView infoImage;
    TextView scoreLabel;
    TextView moveLabel;
    Context context;
    int boardWidth;
    int boardHeight;
    float offsetW;
    float offsetH;
    int currentStoneViewId;

    Button nextBtn;
    Button prevBtn;
    Button rewindBtn;
    Button forwardBtn;

    FrameLayout frameLayout;
    RelativeLayout gameModeLayout;
    RelativeLayout editModeLayout;
    Toolbar myToolbar;

    BoardLogic boardLogic;
    GamesStorage gamesStorage;
    UserSettings userSettings;
    boolean gameReady;
    int tries;
    float currentScore = 0;
    int userMoves = 0;
    boolean putDummy = true;
    Move lastDummyMove = null;


    View[][] stonesImg;
    View[][] dummyStonesImg;
    View graphView;
    ImageView deleteImg;
    TextView deleteTxt;

    XYMultipleSeriesRenderer graphRenderer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Choose a game!");
        setSupportActionBar(myToolbar);

        goImages = new GoImages();
        stonesImg = new View[BOARD_SIZE][BOARD_SIZE];
        dummyStonesImg = new View[BOARD_SIZE][BOARD_SIZE];

        context = this.getApplicationContext();
        userSettings = new UserSettings(context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE));

        boardImage = (ImageView) findViewById(R.id.backgroundImg);
        rawBoardBitmap = ((BitmapDrawable) boardImage.getDrawable()).getBitmap();
        stoneImage = (ImageView) findViewById(R.id.blackImg);
        infoImage = (ImageView) findViewById(R.id.infoImg);
        scoreLabel = (TextView) findViewById(R.id.scoreTxtLabel);
        moveLabel = (TextView) findViewById(R.id.moveTxtLabel);

        nextBtn = (Button) findViewById(R.id.nextBtn);
        prevBtn = (Button) findViewById(R.id.prevBtn);
        rewindBtn = (Button) findViewById(R.id.rewindBtn);
        forwardBtn = (Button) findViewById(R.id.forwardBtn);

        boardLogic = new BoardLogic();
        gamesStorage = new GamesStorage(context);

        loadGameHistory();
        loadRecentGames();

        showIndicator(false);

        gameReady = false;
        userSettings.setState(UserSettings.State.GAME_NOT_LOADED);

        frameLayout = (FrameLayout)findViewById(R.id.background);
        gameModeLayout = (RelativeLayout)findViewById(R.id.gameModeLayout);
        editModeLayout = (RelativeLayout)findViewById(R.id.editModeLayout);
        editModeLayout.setVisibility(View.GONE);

        infoImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                showGameInfo();
                return false ;
            }
        });

        ViewTreeObserver vto = boardImage.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                boardImage.getViewTreeObserver().removeOnPreDrawListener(this);
                boardWidth = boardImage.getMeasuredWidth();
                boardHeight = boardImage.getMeasuredHeight();

                goImages.regenerate(boardWidth, boardWidth / 11);
                offsetW = (boardWidth ) / 20;
                offsetH = (boardHeight ) / 20;

                drawBoardGrid(userSettings.showBoardCoords);
                ViewGroup.LayoutParams params = frameLayout.getLayoutParams();

                params.height = boardWidth;
                params.width = boardWidth;

                return true;
            }
        });

        buildGraph();
        currentStoneViewId = 0;

        final View horiz = (View) findViewById(R.id.horiz);
        final View vertic = (View) findViewById(R.id.vertic);

        horiz.setVisibility(View.INVISIBLE);
        vertic.setVisibility(View.INVISIBLE);

        boardImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                int action = event.getAction();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        if (!gameReady) {
                            Toast.makeText(getApplicationContext(), "Please choose a game!",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                           if (userSettings.doubleclick) {

                               int x = getXCoord(event.getX());
                               int y = getYCoord(event.getY());

                               Move move = new Move(x - 1, y - 1, boardLogic.currentPlayer);
                               ImageView im = (ImageView) dummyStonesImg[move.x][move.y];
                               if (im == null)
                                   removeLastDummyStone(true);
                           }
                            if ((userSettings.zoom != UserSettings.Zoom.NONE) && !isZoomed()) {
                                float toolbarHeight = myToolbar.getHeight();
                                setZoom(event.getX(), event.getY() - toolbarHeight);
                            } else //red lines not showing on first touch if zoom is on
                                drawRedLines(event.getX(), event.getY(), horiz, vertic);
                        }

                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (gameReady) {
                            drawRedLines(event.getX(), event.getY(), horiz, vertic);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        if (gameReady) {
                            ViewGroup owner = (ViewGroup) stoneImage.getParent();
                            owner.removeView(horiz);
                            owner.removeView(vertic);

                            int eventX = (int) event.getX();
                            int eventY = (int) event.getY();

                            if (eventX > 15 && eventX < (boardWidth - 10)
                                    && eventY > 15 && eventY < (boardHeight - 10)) {

                                int x = getXCoord(eventX);
                                int y = getXCoord(eventY);

                                Move move = new Move(x - 1, y - 1, boardLogic.currentPlayer);
                                boolean valid = false;
                                if (stonesImg[move.x][move.y] == null) {
                                    if (!userSettings.doubleclick) {
                                        tries++;
                                        if (boardLogic.isValid(move, userSettings.hint)) {
                                            drawStone(move, true, false);
                                            currentScore += (1.0f / tries);
                                            tries = 0;
                                            userMoves++;
                                            checkIfCapturing(move);
                                            if (userSettings.state == UserSettings.State.GAME_LOADED)
                                                userSettings.setState(UserSettings.State.GAME_IN_PROGRESS);
                                            valid = true;
                                        } else {
                                            if (userSettings.hint == UserSettings.Hint.AREA) {
                                                //todo
                                            }
                                            checkIfShowMove();
                                        }
                                        float totalScore = (currentScore / userMoves) * 100;
                                        updateScoreLabel(totalScore);
                                        if (userSettings.zoom != UserSettings.Zoom.NONE)
                                            resetZoom();
                                    } else if (putDummy) {
                                        lastDummyMove = new Move(move.x, move.y, move.player);
                                        drawStone(move, true, true);
                                        putDummy = false;
                                    } else {
                                        ImageView im = (ImageView) dummyStonesImg[move.x][move.y];
                                        if (im != null) {
                                            tries++;
                                            if (boardLogic.isValid(move, userSettings.hint)) {
                                                putDummy = true;
                                                drawStone(move, true, false);
                                                currentScore += (1.0f / tries);
                                                tries = 0;
                                                userMoves++;
                                                checkIfCapturing(move);
                                                if (userSettings.state == UserSettings.State.GAME_LOADED)
                                                    userSettings.setState(UserSettings.State.GAME_IN_PROGRESS);
                                                valid = true;
                                            } else {
                                                removeLastDummyStone(true);
                                                checkIfShowMove();
                                            }
                                            float totalScore = (currentScore / userMoves) * 100;
                                            updateScoreLabel(totalScore);

                                            if (userSettings.zoom != UserSettings.Zoom.NONE)
                                                resetZoom();
                                        } else {
                                            removeLastDummyStone(true);
                                            lastDummyMove = new Move(move.x, move.y, move.player);
                                            drawStone(move, true, true);
                                            putDummy = false;

                                        }
                                    }
                                    if (valid)
                                        updateGameInfo(boardLogic.currentGame.getGameTitle());
                                }
                            }
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        View.OnClickListener listener = new View.OnClickListener() {
            public void onClick(View v) {
                if (!gameReady)
                    return;
                switch (v.getId()) {
                    case R.id.nextBtn:
                        makeMove();
                        boardLogic.score = 0;
                        updateScoreLabel(lastScore);
                        break;
                    case R.id.prevBtn:
                        removeLastDummyStone(true);
                        if (currentStoneViewId >= 3) {

                            Move lastDrawnMove = boardLogic.getPreviousMove();
                            if (lastDrawnMove != null) {
                                frameLayout.removeView(stonesImg[lastDrawnMove.x][lastDrawnMove.y]);
                                stonesImg[lastDrawnMove.x][lastDrawnMove.y] = null;
                            }
                            boardLogic.removeLastMoveFromBoardState();
                            updateLastMoveMark();
                            currentStoneViewId--;

                            ArrayList<Move> deadStones = boardLogic.restoreBoardState();
                            if (deadStones != null && deadStones.size() > 0) {
                                for (int i = 0; i < deadStones.size(); ++i) {
                                    drawStone(deadStones.get(i), false, false);
                                }
                            }
                        }
                        boardLogic.score = 0;
                        updateScoreLabel(lastScore);
                        break;
                    case R.id.rewindBtn:
                        removeLastDummyStone(true);
                        clearBoard();
                        break;
                    case R.id.forwardBtn:
                        if (gameReady) {
                            Move m;
                            while ((m = boardLogic.getNextMove()) != null) {
                                drawStone(m, true, false);
                                checkIfCapturing(m);
                            }
                        } else
                            Toast.makeText(getApplicationContext(), "Please choose a game!",
                                    Toast.LENGTH_LONG).show();
                        boardLogic.score = 0;
                        updateScoreLabel(lastScore);
                        break;
                }
                updateGameInfo(boardLogic.currentGame.getGameTitle());
            }
        };

        nextBtn.setOnClickListener(listener);
        prevBtn.setOnClickListener(listener);
        rewindBtn.setOnClickListener(listener);
        forwardBtn.setOnClickListener(listener);

        if (userSettings.showAbout) {
            showAbout();
            userSettings.setShowAbout(false);
        }
    }

    private void checkIfShowMove() {
        if (tries == 3 && userSettings.autoMove == UserSettings.AutoMove.THREE) {
            makeMove();
            updateGameInfo(boardLogic.currentGame.getGameTitle());
            tries = 0;
        } else if (tries == 5 && userSettings.autoMove == UserSettings.AutoMove.FIVE) {
            makeMove();
            updateGameInfo(boardLogic.currentGame.getGameTitle());
            tries = 0;
        }
    }

    private void makeMove() {
        Move move = boardLogic.getNextMove();
        if (move != null) {
            drawStone(move, true, false);
            checkIfCapturing(move);
        }
    }

    private int getLineSize() {
        int lineSize = 3;
        if (userSettings.lineSize == UserSettings.LineSize.THICK)
            if (boardWidth > 500)
                lineSize = 5;
            else
                lineSize = 4;
        else if (userSettings.lineSize == UserSettings.LineSize.NORMAL) {
            if (boardWidth > 500)
                lineSize = 4;
            else
                lineSize = 3;

        } else if (userSettings.lineSize == UserSettings.LineSize.TINY) {
            if (boardWidth > 500)
                lineSize = 3;
            else
                lineSize = 2;
        }
        return lineSize;
    }

    private int getXCoord(float eventX) {
        int x = Math.round(eventX / offsetW);
        if (x > 19)
            x = 19;

        return x;
    }

    private int getYCoord(float eventY) {
        int y = Math.round(eventY / offsetH);
        if (y > 19)
            y = 19;

        return y;
    }

    private void drawRedLines(float x, float y, View horiz, View vertic) {
        ViewGroup owner = (ViewGroup) stoneImage.getParent();
        owner.removeView(horiz);
        owner.removeView(vertic);

        int hx = getXCoord(x);
        int vy = getYCoord(y);

        FrameLayout.LayoutParams horizParam = new FrameLayout.LayoutParams(getLineSize(), boardWidth);
        FrameLayout.LayoutParams verticParam = new FrameLayout.LayoutParams(boardHeight, getLineSize());

        horizParam.leftMargin = ((int) (hx * offsetW));
        horizParam.bottomMargin = (int) y;
        horiz.setVisibility(View.VISIBLE);
        frameLayout.addView(horiz, horizParam);

        verticParam.rightMargin = (int) x;
        verticParam.topMargin = ((int) (vy * offsetH));
        vertic.setVisibility(View.VISIBLE);
        if (verticParam.topMargin < boardHeight && verticParam.topMargin >= offsetH)
            frameLayout.addView(vertic, verticParam);
    }

    @Override
    public void onBackPressed() {
        if (isZoomed()) {
            resetZoom();
        } else
            super.onBackPressed();
    }

    private void setZoom(float x, float y) {

        if (userSettings.zoom == UserSettings.Zoom.CENTER_TOUCH) {
            if (x >= (boardWidth / 2)) {
                int diff = ((int) x - (boardWidth / 2));
                x += (diff * 2);
                if (x > boardWidth)
                    x = boardWidth;
            } else {
                int diff = ((boardWidth / 2) - (int) x);
                x -= (diff * 2);
                if (x < 0)
                    x = 0;
            }
        }

        frameLayout.setPivotX(x);
        frameLayout.setPivotY(y);
        frameLayout.setScaleX(BOARD_SCALE_FACTOR);
        frameLayout.setScaleY(BOARD_SCALE_FACTOR);
        gameModeLayout.setVisibility(View.GONE);
        myToolbar.setVisibility(View.GONE);
    }

    private boolean isZoomed() {
        return (frameLayout.getScaleX() > 1.0f);
    }

    private void resetZoom() {
        frameLayout.setScaleX(1f);
        frameLayout.setScaleY(1f);
        gameModeLayout.setVisibility(View.VISIBLE);
        myToolbar.setVisibility(View.VISIBLE);
    }

    private void loadGameHistory() {
        SharedPreferences  prefs = context.getSharedPreferences(APP_PREFERENCES_HISTORY, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        HashMap<String, String> map= (HashMap<String, String>) prefs.getAll();
        for (String s : map.keySet()) {
            String js = map.get(s);
            GameInfo g = gson.fromJson(js, GameInfo.class);
            gamesStorage.gamesHistory.put(g.md5, g);
        }

        for (Map.Entry<String, GameInfo> entry : gamesStorage.gamesHistory.entrySet()) {
            for (Integer s : entry.getValue().score)
                Log.v(TAG, "score : " + String.valueOf(s));
        }
    }

    private void loadRecentGames() {
        SharedPreferences  prefs = context.getSharedPreferences(APP_PREFERENCES_RECENT_GAMES, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        HashMap<String, String> map= (HashMap<String, String>) prefs.getAll();
        for (String s : map.keySet()) {
            String js = map.get(s);
            GameInfo g = gson.fromJson(js, GameInfo.class);

            if (gamesStorage.gamesHistory.containsKey(g.md5)) {
                g.score.clear();
                g.score.addAll(gamesStorage.gamesHistory.get(g.md5).score);
            }
            Log.v(TAG, "read recent  games, game score:  " + String.valueOf(g.score.size()));
            gamesStorage.addRecentGame(g.getGameTitleWithRanks(), g);
        }
    }

    private void checkIfCapturing(Move move) {
        if (boardLogic.isCapturing(move)) {
            for (int i = 0; i < boardLogic.deadStones.size(); ++i) {
                Move m = boardLogic.deadStones.get(i);
                frameLayout.removeView(stonesImg[m.x][m.y]);
                stonesImg[m.x][m.y] = null;
                currentStoneViewId--;
                boardLogic.removeFromBoardState(m);
            }
        }
    }

    private void clearBoard() {
        boardLogic.clearBoardState();
        updateScoreLabel(0);
        lastScore = 0;
        currentScore = 0;
        tries = 0;
        userMoves = 0;
        if (currentStoneViewId >= 3) {
            frameLayout.removeViews(3, currentStoneViewId - 2);
            currentStoneViewId = 0;
        }
        for (int i = 0; i < BOARD_SIZE; ++i)
            for (int j = 0; j < BOARD_SIZE; ++j) {
                stonesImg[i][j] = null;
                removeLastDummyStone(false);
                dummyStonesImg[i][j] = null;
            }

    }

    private void showIndicator(boolean show) {
        RelativeLayout fill = (RelativeLayout) findViewById(R.id.scoreLayout);
        fill.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    float lastScore = 0;

    private void updateScoreLabel(float percentage) { //only from user moves
        lastScore = percentage;
        scoreLabel.setText(String.valueOf((int) percentage) + "%");
        LinearLayout fill = (LinearLayout) findViewById(R.id.scoreFill);
        LinearLayout bkg = (LinearLayout) findViewById(R.id.scoreBkg);
        ViewGroup.LayoutParams paramsBkg = bkg.getLayoutParams();
        ViewGroup.LayoutParams params = fill.getLayoutParams();
        params.width = (int)(boardLogic.score * ((double)(paramsBkg.width) / 10.0d));
        View v = (View)findViewById(R.id.scoreFillEnd);
        if (boardLogic.score == 10) {
            params.width -= 20;
            v.setVisibility(View.VISIBLE);
        } else
            v.setVisibility(View.GONE);

        fill.setLayoutParams(params);
    }

    private void removeLastDummyStone(boolean andSetToNull) {
        if (lastDummyMove != null) {
            ImageView im = (ImageView) dummyStonesImg[lastDummyMove.x][lastDummyMove.y];
            if (im != null) {
                frameLayout.removeView(dummyStonesImg[lastDummyMove.x][lastDummyMove.y]);
            }
            if (andSetToNull)
                dummyStonesImg[lastDummyMove.x][lastDummyMove.y] = null;
        }
    }

    private void drawStone(Move move, boolean mark, boolean dummy) {
        Log.i(TAG, "Putting stone at " + String.valueOf(move.x) + ":" + String.valueOf(move.y)
                + " nr " + String.valueOf(boardLogic.currentIndex) + " of player "
        + move.player);

        if (!dummy) {
            ImageView im = (ImageView)dummyStonesImg[move.x][move.y];
            if (im != null) {
                frameLayout.removeView(dummyStonesImg[move.x][move.y]);
            }
            removeLastDummyStone(false);
        }
        ImageView img = new ImageView(context);
        if (move.player == Move.Player.BLACK) {
            img.setImageBitmap(goImages._black);
        } else {
            img.setImageBitmap(goImages._whites[(move.x * BOARD_SIZE + move.y) % goImages._whites.length]);
        }

        if (dummy)
            img.setAlpha(0.5f);

        int stoneSize = (int)(boardWidth) / 20;

        FrameLayout.LayoutParams stoneParam = new FrameLayout.LayoutParams(stoneSize, stoneSize);
        stoneParam.leftMargin = ((int) ((move.x + 1) * offsetW)) - ((int)stoneSize / 2);
        stoneParam.topMargin = ((int) ((move.y + 1) * offsetH)) - ((int)stoneSize / 2);
        frameLayout.addView(img, stoneParam);

        if (!dummy) {
            stonesImg[move.x][move.y] = img;
            currentStoneViewId = frameLayout.indexOfChild(img);
            Log.i(TAG, "Putting stone at " + String.valueOf(move.x) + ":" + String.valueOf(move.y)
                    + " nr " + String.valueOf(boardLogic.currentIndex) + " of player "
                    + move.player);
            boardLogic.addMoveToBoardState(move);

            if (mark)
                updateLastMoveMark();
        } else {
            dummyStonesImg[lastDummyMove.x][lastDummyMove.y] = img;
        }
    }

    ImageView lastMarkedStone = null;

    private void updateLastMoveMark() {

        Move lastDrawnMove = boardLogic.getPreviousMove();
        if (lastDrawnMove != null) {
            Log.i(TAG, "last drawn stone at " + String.valueOf(lastDrawnMove.x) + ":" + String.valueOf(lastDrawnMove.y));

            Paint p = new Paint();
            p.setColor(Color.RED);
            p.setStrokeWidth(1);

           ImageView iv = (ImageView) stonesImg[lastDrawnMove.x][lastDrawnMove.y];
            if (iv != null) {
                if (boardLogic.currentPlayer == Move.Player.BLACK) {

                    Bitmap bm = goImages._whites[(lastDrawnMove.x * BOARD_SIZE + lastDrawnMove.y) % goImages._whites.length];
                    Bitmap tempBitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas tempCanvas = new Canvas(tempBitmap);
                    tempCanvas.drawBitmap(bm, 0, 0, null);
                    tempCanvas.drawCircle(bm.getWidth() / 2.0f, bm.getHeight() / 2.0f, bm.getHeight() / 5.5f, p);
                    iv.setImageBitmap(tempBitmap);
                } else {
                    Bitmap bm = goImages._black;
                    Bitmap tempBitmap = Bitmap.createBitmap(bm.getWidth(), bm.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas tempCanvas = new Canvas(tempBitmap);
                    tempCanvas.drawBitmap(bm, 0, 0, null);
                    tempCanvas.drawCircle(bm.getWidth() / 2.0f, bm.getHeight() / 2.0f, bm.getHeight() / 5.5f, p);
                    iv.setImageBitmap(tempBitmap);
                }
                if (lastMarkedStone == null)
                    lastMarkedStone = iv;
                else {
                    lastMarkedStone.setImageBitmap(boardLogic.currentPlayer == Move.Player.BLACK ?
                            goImages._black : goImages._whites[(lastDrawnMove.x * BOARD_SIZE + lastDrawnMove.y) % goImages._whites.length]);
                    lastMarkedStone = iv;
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String filePath = "";
        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {

            Uri uri = data.getData();
            filePath = uri.getPath();

            if (filePath.length() >= 0 && filePath.substring(filePath.length() - 3).equals(FILE_EXT)) {
                Log.i(TAG, "Opening file: " + filePath);
                GameInfo game = boardLogic.parseSGFFile(filePath);
                if (game != null) {
                    if (gamesStorage.gamesHistory.containsKey(game.md5)) {
                        game = gamesStorage.gamesHistory.get(game.md5);
                    }
                    gameReady = (game != null && game.moves.size() != 0);
                    if (gameReady) {
                        loadGame(game);
                    }
                }

            } else {
                Toast.makeText(getApplicationContext(), "This is not SGF!",
                            Toast.LENGTH_LONG).show();
                openFilePicker();
            }

        } else if (requestCode == GAME_REPO_REQUEST_CODE && resultCode == RESULT_OK) {
            String name = data.getStringExtra("gamename");
            String sgf = gamesStorage.getDefaultGameAt(name);
            GameInfo game = boardLogic.parseSGFString(sgf);
            if (game != null) {
                if (gamesStorage.gamesHistory.containsKey(game.md5)) {
                    game = gamesStorage.gamesHistory.get(game.md5);
                }
                gameReady = (game != null && game.moves.size() != 0);
                if (gameReady) {
                    loadGame(game);
                }
            }
        }
    }

    private void updateMoveLabel() {
        String moves = "(" + (String.valueOf(boardLogic.currentIndex)) + "/" + boardLogic.currentGame.moves.size() + ")";
        moveLabel.setText(moves);
    }

    private void updateGameInfo(String title) {
        scoreLabel.setText(scoreLabel.getText());
        updateMoveLabel();
        getSupportActionBar().setTitle(title);
        setSupportActionBar(myToolbar);
        myToolbar.refreshDrawableState();
        if (boardLogic.currentIndex == boardLogic.currentGame.moves.size()) {
            askIfAddToHistory();
            String result = boardLogic.currentGame.result;
            moveLabel.setText(result);
        }
    }

    private void saveCurrentGameToHistoryPrefs() {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_PREFERENCES_HISTORY, Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(boardLogic.currentGame);
        editor.putString(boardLogic.currentGame.md5, json);
        editor.commit();

    }

    private void deleteCurrentGameHistoryFromPrefs() {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_PREFERENCES_HISTORY, Context.MODE_PRIVATE).edit();
        editor.remove(boardLogic.currentGame.md5);
        editor.commit();
    }

    private void saveCurrentGameToRecentPrefs() {
        SharedPreferences.Editor editor = context.getSharedPreferences(APP_PREFERENCES_RECENT_GAMES, Context.MODE_PRIVATE).edit();
        Gson gson = new Gson();
        String json = gson.toJson(boardLogic.currentGame);
        editor.putString(boardLogic.currentGame.md5, json);
        editor.commit();
    }


    private void askIfAddToHistory() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Game finished with score " + String.valueOf((int)lastScore) + "%");
        alertDialog.setMessage("Add this score to game history?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        gamesStorage.addToGamesHistory(boardLogic.currentGame, (int) lastScore);
                        saveCurrentGameToHistoryPrefs();
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Score saved!",
                                Toast.LENGTH_SHORT).show();

                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_openGame:
                openFilePicker();
                return true;
            case R.id.action_recentlyUsed:
                showRecentGamesList();
                return true;
            case R.id.action_gamesList:
                showDefaultGamesList();
                return true;
            case R.id.action_settings:
                showSettings();
                return true;
            case R.id.action_about:
                showAbout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openFilePicker() {
        Intent i = new Intent(context, FilePickerActivity.class);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
        startActivityForResult(i, FILE_PICKER_REQUEST_CODE);
    }

    private void loadGame(GameInfo game) {
        clearBoard();
        boardLogic.currentGame = game;
        updateGameInfo(game.getGameTitle());
        updateScoreLabel(0);
        ImageView view = (ImageView) findViewById(R.id.gameInfoImg);
        view.setVisibility(View.VISIBLE);
        view = (ImageView) findViewById(R.id.infoImg);
        view.setVisibility(View.VISIBLE);
        if (userSettings.hint == UserSettings.Hint.DISTANCE)
            showIndicator(true);
        Log.v(TAG, "load  game " + game.md5 + "   score size " + String.valueOf(boardLogic.currentGame.score.size()));
        gamesStorage.addRecentGame(game.getGameTitleWithRanks(), game);
        saveCurrentGameToRecentPrefs();
        tries = userMoves = 0;
        currentScore = 0;
        userSettings.setState(UserSettings.State.GAME_LOADED);

        if (userSettings.showFirstMoves) {
            makeFirstMoves();
        }

    }

    private void makeFirstMoves() {
        int i = 4;
        while (i-- > 0) {
            makeMove();
        }
        String moves = "(" + (String.valueOf(boardLogic.currentIndex)) + "/" + boardLogic.currentGame.moves.size() + ")";
        moveLabel.setText(moves);
    }


    private void showAbout() {
        String quote = "“To follow the path, look to the master, follow the master, " +
                "walk with the master, see through the master, become the master.”";

        String msg = "<b>How to play</b><br><br>Load a game and try to guess the next move!<br><br>" +
                    "Skipped moves are not counted against the score.<br><br>" +
                    "You can save current score when the game is finished.<br>";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(Html.fromHtml("<i><font color=grey>" + quote + "</font></i><br><br><br>" + msg));
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

    private void showRecentGamesList() {
        int cnt = gamesStorage.recentGamesQueue.size() > 0 ? gamesStorage.recentGamesQueue.size() : 0;
        if (cnt == 0) {
            Toast.makeText(getApplicationContext(), "No recent games found!", Toast.LENGTH_SHORT).show();
            return;
        }
        final CharSequence[] items = new CharSequence[cnt];
        int i = 0;
        for (String key : gamesStorage.recentGamesQueue) {
            items[i++] = key;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Recent games");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                GameInfo game = gamesStorage.getRecentGameAt(items[item].toString());
                if (game != null) {
                    gameReady = game.moves.size() != 0;
                    if (gameReady) {
                        loadGame(game);
                    }
                } else
                    Toast.makeText(getApplicationContext(), "Could not load game!", Toast.LENGTH_SHORT).show();
            }

        });

        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        alert.setOnShowListener(new RecentGamesListener());
        alert.show();
    }

    public void onGameInfoImgClicked(View v) {
        if (gameReady) {
            String black = "Black: " + boardLogic.currentGame.blackPlayerName +
                    (boardLogic.currentGame.blackPlayerRank.length() > 0 ?
                            (" [" + boardLogic.currentGame.blackPlayerRank + "]") : "");
            String white = "White: " + boardLogic.currentGame.whitePlayerName +
                    (boardLogic.currentGame.whitePlayerRank.length() > 0 ?
                            (" [" + boardLogic.currentGame.whitePlayerRank + "]") : "");
            String result = boardLogic.currentGame.result;

            if (result.length() > 0) {
                result = "\nResult: " + result;
            }
            String date = boardLogic.currentGame.date;
            if (date.length() > 0) {
                date = "\nDate: " + date;
            }
            String event =  boardLogic.currentGame.event;
            if (event.length() > 0) {
                event = "\nEvent: " + event;
            }

            showAlertMessage(black + "\n" + white + result + date + event);
        }
    }

    private void buildGraph() {
        graphRenderer = new XYMultipleSeriesRenderer();

        XYSeriesRenderer renderer = new XYSeriesRenderer();
        renderer.setLineWidth(2);
        renderer.setColor(getResources().getColor(R.color.colorPrimaryDark));
        renderer.setDisplayBoundingPoints(true);
        renderer.setPointStyle(PointStyle.CIRCLE);
        renderer.setPointStrokeWidth(3);

        graphRenderer.addSeriesRenderer(renderer);
        graphRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00));

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        Log.v(TAG, "screen width/height: " + String.valueOf(width) + ":" + String.valueOf(height));
        if (height > 1000 && width > 1000) {
            graphRenderer.setLabelsColor(Color.GRAY);
            graphRenderer.setAxesColor(Color.GRAY);
            graphRenderer.setLabelsColor(Color.GRAY);
            graphRenderer.setAxisTitleTextSize(24);
            graphRenderer.setLabelsTextSize(26);
            graphRenderer.setLegendTextSize(26);
            int[] margins = {60, 35, 70, 35};
            graphRenderer.setMargins(margins);

        }

        graphRenderer.setPanEnabled(false, false);
        graphRenderer.setYAxisMax(100);
        graphRenderer.setYAxisMin(0);
        graphRenderer.setYTitle("Guess %");
        graphRenderer.setXTitle("Games played");
        graphRenderer.setShowGrid(true);

    }

    private void attemptDeleteHistory() {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Delete game history");
        alertDialog.setMessage("Are you sure? This cannot be undone.");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        boardLogic.currentGame.score.clear();
                        gamesStorage.removeFromGamesHistory(boardLogic.currentGame);
                        deleteCurrentGameHistoryFromPrefs();
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Game history deleted!",
                                Toast.LENGTH_SHORT).show();

                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void showAlertMessage(String msg) {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setMessage(msg);
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.show();
    }

    private void showGameInfo() {

        if (!gameReady) {
            showAlertMessage("No game loaded!");
            return;
        }

        if (boardLogic.currentGame.score.size() == 0) {
            showAlertMessage("No game history yet!");
            return;
        }

        XYSeries series = new XYSeries("Guess history of current game");
        int time = 1;

        Log.v(TAG, "scores for " + boardLogic.currentGame.md5);
        for (int i : gamesStorage.gamesHistory.get(boardLogic.currentGame.md5).score) {
            series.add(time++, i);
            Log.v(TAG, "score " + String.valueOf(i));
        }

        XYMultipleSeriesDataset mXYMultipleSeriesDataSet = new XYMultipleSeriesDataset();
        mXYMultipleSeriesDataSet.addSeries(series);

        GraphicalView chartView = ChartFactory.getLineChartView(context, mXYMultipleSeriesDataSet, graphRenderer);

        LayoutInflater inflater = getLayoutInflater();
        graphView = (View) inflater.inflate(R.layout.graph, null);

        deleteImg = (ImageView) graphView.findViewById(R.id.deleteImg);
        deleteTxt = (TextView) graphView.findViewById(R.id.deleteTxtLabel);

        View.OnTouchListener listener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                attemptDeleteHistory();
                return false;
            }
        };
        deleteImg.setOnTouchListener(listener);
        deleteTxt.setOnTouchListener(listener);

        LinearLayout l = (LinearLayout) graphView.findViewById(R.id.chart);
        l.addView(chartView, 0);

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setView(graphView);

        AlertDialog alert = dialog.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }

    private void showSettings() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Customize Goess");

        LayoutInflater inflater = getLayoutInflater();
        View settingsView = (View) inflater.inflate(R.layout.settings, null);
        dialog.setView(settingsView);

        CheckBox cb = (CheckBox) settingsView.findViewById(R.id.boardCoordsCb);
        cb.setChecked(userSettings.showBoardCoords);
        cb = (CheckBox) settingsView.findViewById(R.id.doubleClickCb);
        cb.setChecked(userSettings.doubleclick);
        cb = (CheckBox) settingsView.findViewById(R.id.firstMovesCb);
        cb.setChecked(userSettings.showFirstMoves);


        checkRadioGroups(settingsView);

        AlertDialog alert = dialog.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();

    }

    private void checkRadioGroups(View view) {
        RadioGroup lineRadioGroup = (RadioGroup)view.findViewById(R.id.linesizegroup);
        if (userSettings.lineSize == UserSettings.LineSize.THICK)
            lineRadioGroup.check(R.id.linethick);
        else if (userSettings.lineSize == UserSettings.LineSize.NORMAL)
            lineRadioGroup.check(R.id.linenormal);
        else
            lineRadioGroup.check(R.id.linetiny);

        RadioGroup hintRadioGroup = (RadioGroup)view.findViewById(R.id.hintgroup);
        if (userSettings.hint == UserSettings.Hint.DISTANCE)
            hintRadioGroup.check(R.id.showDistanceRb);
        else if (userSettings.hint == UserSettings.Hint.AREA)
            hintRadioGroup.check(R.id.showAreaRb);
        else
            hintRadioGroup.check(R.id.hintNoneRb);

        RadioGroup autoMoveRadioGroup = (RadioGroup)view.findViewById(R.id.autoMoveGroup);
        if (userSettings.autoMove == UserSettings.AutoMove.THREE)
            autoMoveRadioGroup.check(R.id.threefailuresRb);
        else if (userSettings.autoMove == UserSettings.AutoMove.FIVE)
            autoMoveRadioGroup.check(R.id.fivefailuresRb);
        else
            autoMoveRadioGroup.check(R.id.noneFailuresRb);

        RadioGroup zoomRadioGroup = (RadioGroup)view.findViewById(R.id.zoomgroup);
        if (userSettings.zoom == UserSettings.Zoom.CENTER_TOUCH)
            zoomRadioGroup.check(R.id.zoomcentertouch);
        else if (userSettings.zoom == UserSettings.Zoom.CENTER_CANVAS)
            zoomRadioGroup.check(R.id.zoomcentercanvas);
        else
            zoomRadioGroup.check(R.id.zoomnone);

        RadioGroup modeRadioGroup = (RadioGroup)view.findViewById(R.id.modegroup);
        if (userSettings.mode == UserSettings.Mode.GAME)
            modeRadioGroup.check(R.id.gameModeRb);
        else
            modeRadioGroup.check(R.id.editModeRb);
    }

    public void firstMovesHandler(View v) {
        CheckBox cb = (CheckBox) v;
        userSettings.setShowFirstMoves(cb.isChecked());
        if (!userSettings.showFirstMoves && userSettings.state == UserSettings.State.GAME_LOADED) {
            clearBoard();
            String moves = "(" + (String.valueOf(boardLogic.currentIndex)) + "/" + boardLogic.currentGame.moves.size() + ")";
            moveLabel.setText(moves);
        } else if (userSettings.showFirstMoves && userSettings.state == UserSettings.State.GAME_LOADED) {
            makeFirstMoves();
        }
    }

    public void boardCoordsHandler(View v) {
        CheckBox cb = (CheckBox) v;
        userSettings.setShowBoardCoords(cb.isChecked());
        drawBoardGrid(userSettings.showBoardCoords);
    }

    public void doubleClickHandler(View v) {
        CheckBox cb = (CheckBox) v;
        userSettings.setDoubleClick(cb.isChecked());
        removeLastDummyStone(true);
        putDummy = true;
    }

    public void lineTinyHandler(View v) {
        userSettings.setLineSize(UserSettings.LineSize.TINY);
        drawBoardGrid(userSettings.showBoardCoords);
    }

    public void lineNormalHandler(View v) {
        userSettings.setLineSize(UserSettings.LineSize.NORMAL);
        drawBoardGrid(userSettings.showBoardCoords);
    }

    public void lineThickHandler(View v) {
        userSettings.setLineSize(UserSettings.LineSize.THICK);
        drawBoardGrid(userSettings.showBoardCoords);
    }

    public void hintDistanceHandler(View v) {
        userSettings.setHint(UserSettings.Hint.DISTANCE);
        showIndicator(true);
    }

    public void hintAreaHandler(View v) {
        userSettings.setHint(UserSettings.Hint.AREA);
        showIndicator(false);
    }

    public void hintNoneHandler(View v) {
        userSettings.setHint(UserSettings.Hint.NONE);
        showIndicator(false);
    }

    public void zoomTouchHandler(View v) {
        userSettings.setZoom(UserSettings.Zoom.CENTER_TOUCH);
    }

    public void zoomCanvasHandler(View v) {
        userSettings.setZoom(UserSettings.Zoom.CENTER_CANVAS);
    }

    public void zoomNoneHandler(View v) {
        userSettings.setZoom(UserSettings.Zoom.NONE);
        tries = 0;
    }

    public void threeFailuresHandler(View v) {
        userSettings.setAutoMove(UserSettings.AutoMove.THREE);
        tries = 0;
    }

    public void fiveFailuresHandler(View v) {
        userSettings.setAutoMove(UserSettings.AutoMove.FIVE);
        tries = 0;
    }

    public void noneFailuresHandler(View v) {
        userSettings.setAutoMove(UserSettings.AutoMove.NONE);
    }

    public void gameModeHandler(View v) {
        userSettings.setMode(UserSettings.Mode.GAME);
    }

    public void editModeHandler(View v) {
        userSettings.setMode(UserSettings.Mode.VIEW_ONLY);
        
    }

    private void showDefaultGamesList() {

        Intent intent = new Intent(this, RepoActivity.class);
        intent.putStringArrayListExtra("classic", gamesStorage.classic);
        intent.putStringArrayListExtra("japanese", gamesStorage.japanese);
        intent.putStringArrayListExtra("korean", gamesStorage.korean);
        intent.putStringArrayListExtra("chinese", gamesStorage.chinese);
        intent.putStringArrayListExtra("european", gamesStorage.european);

        startActivityForResult(intent, GAME_REPO_REQUEST_CODE);
    }

    private void drawBoardGrid(boolean grid) {
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        float ratio = 1.0f/boardWidth;
        float radius = 0.0f;

        if (userSettings.lineSize == UserSettings.LineSize.THICK) {
            if (boardWidth > 500) {
                ratio = 4.0f / boardWidth;
                radius += 2.5f;
            } else {
                ratio = 2.7f / boardWidth;
            }

        } else if (userSettings.lineSize == UserSettings.LineSize.NORMAL) {
            if (boardWidth > 500) {
                ratio = 2.5f / boardWidth;
                radius += 1.0f;
            } else {
                ratio = 1.0f / boardWidth;
            }
        } else if (userSettings.lineSize == UserSettings.LineSize.TINY) {
            if (boardWidth > 500) {
                ratio = 1.5f / boardWidth;
                radius += 1.0f;
            } else {
                ratio = 1.0f / boardWidth;
            }
            p.setAlpha(140);
        }

        p.setStrokeWidth(ratio * boardWidth);
        radius += boardWidth * ratio + 1.3f;

        Bitmap tempBitmap = Bitmap.createBitmap(boardWidth, boardHeight, Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawBitmap(rawBoardBitmap, 0, 0, null);

        float offset = boardWidth / 20;

        for (float i = offset; i <= (offset * 19); i += offset) {
            tempCanvas.drawLine(i, offset, i, (offset * 19) + 0, p);
            tempCanvas.drawLine(offset, i, (offset * 19) + 0, i, p);
        }

        float off = (boardWidth > 500) ? 0.0f : ((userSettings.lineSize == UserSettings.LineSize.THICK) ? 0.0f : 0.5f);
        for (float i = offset * 4; i <= (offset * 19); i += (offset * 6)) {
            for (float j = offset * 4; j <= (offset * 19); j += (offset * 6))
                tempCanvas.drawCircle(i + off, j + off, radius, p);
        }

        if (grid) {
            p.setTextSize((offsetW / 2) - 3);
            p.setFakeBoldText(true);
            int c = 0;
            String alphabet = "ABCDEFGHJKLMNOPQRST";
            int padding = (int)(offsetW / 4) + 4;
            for (float i = offset; i <= (offset * 19); i += offset) {
                tempCanvas.drawText(String.valueOf(alphabet.charAt(c)), i - 3, (offsetW / 3) + 2, p);
                tempCanvas.drawText(String.valueOf(alphabet.charAt(c++)), i - 3, boardHeight - 4, p);
                tempCanvas.drawText(String.valueOf(20 - c), 1, i + 5, p);
                if ((19 - c) > 8)
                    padding = (int)(offsetW / 2);
                else
                    padding = (int)(offsetW / 4) + 2;
                tempCanvas.drawText(String.valueOf(20 - c), boardWidth - padding, i + 5, p);
            }

        }
        boardImage.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));

        Log.i(TAG, "boardWidth: " + String.valueOf(boardWidth + ", boardHeight: " +
                String.valueOf(boardHeight)) + ", offset:" + offset);

    }
}
