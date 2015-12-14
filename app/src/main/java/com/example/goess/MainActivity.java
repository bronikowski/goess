package com.example.goess;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.lang.reflect.Field;
import java.util.ArrayList;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;


public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";
    private static int GRID_PADDING = 20;
    private static int STONE_SIZE = 28;
    private static String FILE_EXT = "sgf";
    private static int REQUEST_CODE = 1;
    private static int BOARD_SIZE = 19;

    ImageView boardImage;
    ImageView stoneImage;
    TextView scoreLabel;
    Context context;
    int boardWidth;
    int boardHeight;
    int stoneWidth;
    int stoneHeight;
    float offsetW;
    float offsetH;
    int currentStoneViewId;

    Button nextBtn;
    Button prevBtn;
    Button rewindBtn;
    Button forwardBtn;

    FrameLayout frameLayout;
    Toolbar myToolbar;

    BoardLogic boardLogic;
    GamesStorage gamesStorage;
    boolean gameReady;
    int tries;
    float currentScore = 0;

    View[][] stonesImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Choose a game!");
        setSupportActionBar(myToolbar);
        TextView title = getActionBarTextView();
        title.setTextSize(16);

        stonesImg = new View[BOARD_SIZE][BOARD_SIZE];

        context = this.getApplicationContext();
        boardImage = (ImageView) findViewById(R.id.backgroundImg);
        stoneImage = (ImageView) findViewById(R.id.blackImg);
        scoreLabel = (TextView) findViewById(R.id.testLabel);
        nextBtn = (Button) findViewById(R.id.nextBtn);
        prevBtn = (Button) findViewById(R.id.prevBtn);
        rewindBtn = (Button) findViewById(R.id.rewindBtn);
        forwardBtn = (Button) findViewById(R.id.forwardBtn);

        boardLogic = new BoardLogic();
        gamesStorage = new GamesStorage(context);
        gameReady = false;

        frameLayout = (FrameLayout)findViewById(R.id.background);


        ViewTreeObserver vto = boardImage.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                boardImage.getViewTreeObserver().removeOnPreDrawListener(this);
                boardWidth = boardImage.getMeasuredHeight();
                boardHeight = boardImage.getMeasuredWidth();
                offsetW = (boardWidth - (GRID_PADDING * 2)) / 18;
                offsetH = (boardHeight - (GRID_PADDING * 2)) / 18;

                stoneWidth = stoneImage.getMeasuredHeight();
                stoneHeight = stoneImage.getMeasuredWidth();

                Log.i(TAG, "boardWidth: " + String.valueOf(stoneWidth + ", boardHeight: " +
                String.valueOf(stoneHeight)));

                drawBoardGrid();

                ViewGroup.LayoutParams params = frameLayout.getLayoutParams();
                params.height = boardHeight;
                params.width = boardWidth;

                return true;
            }
        });

        currentStoneViewId = 0;

        final View horiz = (View) findViewById(R.id.horiz);
        final View vertic = (View) findViewById(R.id.vertic);
        horiz.setVisibility(View.INVISIBLE);
        vertic.setVisibility(View.INVISIBLE);

        boardImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                FrameLayout.LayoutParams horizParam = new FrameLayout.LayoutParams(2, boardWidth);
                FrameLayout.LayoutParams verticParam = new FrameLayout.LayoutParams(boardHeight, 2);

                ViewGroup owner = (ViewGroup) stoneImage.getParent();

                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_MOVE:

                        owner.removeView(horiz);
                        owner.removeView(vertic);

                        horizParam.leftMargin = (int) event.getX();
                        horizParam.bottomMargin = (int) event.getY();
                        horiz.setVisibility(View.VISIBLE);
                        frameLayout.addView(horiz, horizParam);

                        verticParam.rightMargin = (int) event.getX();
                        verticParam.topMargin = (int) event.getY();
                        vertic.setVisibility(View.VISIBLE);
                        if ((int) event.getY() < boardHeight)
                            frameLayout.addView(vertic, verticParam);


                        break;
                    case MotionEvent.ACTION_UP:

                        owner.removeView(horiz);
                        owner.removeView(vertic);

                        if (gameReady) {
                            tries++;
                            ImageView img = new ImageView(context);
                            img.setImageDrawable(v.getResources().getDrawable(R.drawable.black));

                            int eventX = (int) event.getX();
                            int eventY = (int) event.getY();

                            if (eventX > 15 && eventX < (boardWidth - 10)
                                    && eventY > 15 && eventY < (boardHeight - 10)) {

                                int x = Math.round((float) eventX / offsetW);
                                int y = Math.round((float) eventY / offsetH);

                                if (x > 19)
                                    x = 19;
                                if (y > 19)
                                    y = 19;

                                Move move = new Move(x - 1, y - 1, boardLogic.currentPlayer);

                                if (boardLogic.isValid(move)) {
                                    drawStone(move, v, true);
                                    currentScore += (1.0f / tries);
                                    tries = 0;
                                    checkIfCapturing(move);
                                } else
                                    currentScore += 0;

                                float totalScore = (currentScore / boardLogic.currentIndex) * 100;
                                updateScoreLabel(totalScore);
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
                switch (v.getId()) {
                    case R.id.nextBtn:
                        if (gameReady) {
                            Move move = boardLogic.getNextMove();
                            if (move != null) {
                                drawStone(move, v, true);
                                checkIfCapturing(move);
                            }
                            else
                                Toast.makeText(getApplicationContext(), "No more moves!",
                                        Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(getApplicationContext(), "Please choose a game!",
                                    Toast.LENGTH_LONG).show();
                        break;
                    case R.id.prevBtn:
                        if (currentStoneViewId >= 3) {

                            Move lastDrawnMove = boardLogic.getPreviousMove();
                            if (lastDrawnMove != null)
                                frameLayout.removeView(stonesImg[lastDrawnMove.x][lastDrawnMove.y]);

                            boardLogic.removeLastMoveFromBoardState();
                            updateLastMoveMark();
                            currentStoneViewId--;

                            ArrayList<Move> deadStones = boardLogic.restoreBoardState();
                            if (deadStones != null && deadStones.size() > 0) {
                                for (int i = 0; i < deadStones.size(); ++i) {
                                    drawStone(deadStones.get(i), v, false);
                                }
                            }
                        }
                        break;
                    case R.id.rewindBtn:
                      //  Log.i(TAG, "stones size " + String.valueOf(stonesImg));
                        clearBoard();
                        break;
                    case R.id.forwardBtn:
                        if (gameReady) {
                            Move m;
                            while ((m = boardLogic.getNextMove()) != null) {
                                drawStone(m, v, true);
                                checkIfCapturing(m);
                            }
                        } else
                            Toast.makeText(getApplicationContext(), "Please choose a game!",
                                    Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };

        nextBtn.setOnClickListener(listener);
        prevBtn.setOnClickListener(listener);
        rewindBtn.setOnClickListener(listener);
        forwardBtn.setOnClickListener(listener);
    }



    private void checkIfCapturing(Move move) {
        if (boardLogic.isCapturing(move)) {
            for (int i = 0; i < boardLogic.deadStones.size(); ++i) {
                Move m = boardLogic.deadStones.get(i);
                frameLayout.removeView(stonesImg[m.x][m.y]);
                currentStoneViewId--;
                boardLogic.removeFromBoardState(m);
            }
        }
    }

    private void clearBoard() {
        boardLogic.clearBoardState();
        if (currentStoneViewId >= 3) {
            frameLayout.removeViews(3, currentStoneViewId - 2);
            currentStoneViewId = 0;
        }
    }

    private void updateScoreLabel(float percentage) {
        scoreLabel.setText("score: " + String.valueOf((int)percentage) + "%");
        LinearLayout fill = (LinearLayout) findViewById(R.id.scoreFill);
        LinearLayout bkg = (LinearLayout) findViewById(R.id.scoreBkg);
        ViewGroup.LayoutParams paramsBkg =  bkg.getLayoutParams();
        ViewGroup.LayoutParams params =  fill.getLayoutParams();
        params.width = boardLogic.score * ((paramsBkg.width - 10) / 10);
        View v = findViewById(R.id.scoreFillEnd);
        if (boardLogic.score == 10) {
            v.setVisibility(View.VISIBLE);
        } else
            v.setVisibility(View.INVISIBLE);

        fill.setLayoutParams(params);
    }

    private void drawStone(Move move, View view, boolean mark) {
        Log.i(TAG, "Putting stone at " + String.valueOf(move.x) + ":" + String.valueOf(move.y)
                + " nr " + String.valueOf(boardLogic.currentIndex));

        ImageView img = new ImageView(context);
        img.setImageDrawable(view.getResources().getDrawable(
                move.player == Move.Player.BLACK ? R.drawable.black : R.drawable.white));

        if (move.player == Move.Player.WHITE)
            STONE_SIZE = 30;
        else
            STONE_SIZE = 28;

        FrameLayout.LayoutParams stoneParam = new FrameLayout.LayoutParams(STONE_SIZE, STONE_SIZE);
        stoneParam.leftMargin = ((int) ((move.x + 1) * offsetW)) - (STONE_SIZE / 2) + 1/*imgpadding*/;
        stoneParam.topMargin = ((int) ((move.y + 1) * offsetH)) - (STONE_SIZE / 2);
        frameLayout.addView(img, stoneParam);
        currentStoneViewId = frameLayout.indexOfChild(img);

        boardLogic.addMoveToBoardState(move);
        stonesImg[move.x][move.y] = img;
        if (mark)
            updateLastMoveMark();
    }

    ImageView lastMarkedStone = null;

    private void updateLastMoveMark() {

        Move lastDrawnMove = boardLogic.getPreviousMove();
        if (lastDrawnMove != null) {
            Log.i(TAG, "last drawn stone at " + String.valueOf(lastDrawnMove.x) + ":" + String.valueOf(lastDrawnMove.y));

            ImageView iv = (ImageView) stonesImg[lastDrawnMove.x][lastDrawnMove.y];
            iv.setImageResource(boardLogic.currentPlayer == Move.Player.BLACK ?
                    R.drawable.white_marked : R.drawable.black_marked);
            if (lastMarkedStone == null)
                lastMarkedStone = iv;
            else {
                lastMarkedStone.setImageResource(boardLogic.currentPlayer == Move.Player.BLACK ?
                        R.drawable.black : R.drawable.white);
                lastMarkedStone = iv;
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);

            if (filePath.substring(filePath.length() - 3).equals(FILE_EXT)) {
                Log.i(TAG, "Opening file: " + filePath);
                gameReady = boardLogic.parseSGFFile(filePath);
                if (gameReady) {
                    loadGame(boardLogic.parser.getLastSgf());
                }

            } else {
                Toast.makeText(getApplicationContext(), "This is not SGF!",
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, FilePickerActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        }
    }

    private void updateGameInfo(String title) {
        getSupportActionBar().setTitle(title);
        setSupportActionBar(myToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private TextView getActionBarTextView() {
        TextView titleTextView = null;

        try {
            Field f = myToolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            titleTextView = (TextView) f.get(myToolbar);
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }
        return titleTextView;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_openGame:
                Intent intent = new Intent(this, FilePickerActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                return true;
            case R.id.action_recentlyUsed:
                showRecentGamesList();
                return true;
            case R.id.action_gamesList:
                showDefaultGamesList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void loadGame(String sgf) {
        clearBoard();
        String title = boardLogic.getBlackPlayer() + " vs " + boardLogic.getWhitePlayer();
        updateGameInfo(title);
        updateScoreLabel(0);
        gamesStorage.addRecentGame(title, sgf);
        tries = 0;
        currentScore = 0;
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
                String sgf = gamesStorage.getRecentGameAt(items[item].toString());
                if (sgf != null) {
                    gameReady = boardLogic.parseSGFString(sgf);
                    if (gameReady) {
                        loadGame(sgf);
                    }
                } else
                    Toast.makeText(getApplicationContext(), "Could not load game!", Toast.LENGTH_SHORT).show();
            }

        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showDefaultGamesList() {
        final CharSequence[] items = new CharSequence[gamesStorage.DEFAULT_GAMES_LIST_SIZE];
        int i = 0;
        for (String key : gamesStorage.defaultGamesByName.keySet()) {
            items[i++] = key;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Games repository");
        builder.setItems(items, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {
                String sgf = gamesStorage.getDefaultGameAt(items[item].toString());
                gameReady = boardLogic.parseSGFString(sgf);
                if (gameReady) {
                    loadGame(sgf);
                }
            }

        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void drawBoardGrid() {
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        Bitmap bitmap = ((BitmapDrawable) boardImage.getDrawable()).getBitmap();

        Bitmap tempBitmap = Bitmap.createBitmap(boardWidth, boardHeight, Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);
        tempCanvas.drawBitmap(bitmap, 0, 0, null);

        float offset = (boardWidth - (GRID_PADDING * 2)) / 18;
        for (float i = 25; i <= (offset * 20); i += offset) {
            tempCanvas.drawLine(i, 25, i, (offset * 19) + 2, p);
            tempCanvas.drawLine(25, i, (offset * 19) + 2, i, p);
        }
        boardImage.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));

        Log.i(TAG, "boardWidth: " + String.valueOf(boardWidth + ", boardHeight: " +
                String.valueOf(boardHeight)) + ", offset:" + offset);
    }
}
