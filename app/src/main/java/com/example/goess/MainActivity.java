package com.example.goess;

import android.content.Context;
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

import com.nbsp.materialfilepicker.ui.FilePickerActivity;


public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";
    private static int GRID_PADDING = 20;
    private static int STONE_SIZE = 28;
    private static String FILE_EXT = "sgf";
    private static int REQUEST_CODE = 1;

    ImageView boardImage;
    ImageView stoneImage;
    TextView testLabel;
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

    BoardLogic boardLogic;
    boolean gameReady;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        context = this.getApplicationContext();
        boardImage = (ImageView) findViewById(R.id.backgroundImg);
        stoneImage = (ImageView) findViewById(R.id.blackImg);
        testLabel = (TextView) findViewById(R.id.testLabel);
        nextBtn = (Button) findViewById(R.id.nextBtn);
        prevBtn = (Button) findViewById(R.id.prevBtn);
        rewindBtn = (Button) findViewById(R.id.rewindBtn);
        forwardBtn = (Button) findViewById(R.id.forwardBtn);

        boardLogic = new BoardLogic();
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

                FrameLayout.LayoutParams horizParam = new FrameLayout.LayoutParams(1, boardWidth);
                FrameLayout.LayoutParams verticParam = new FrameLayout.LayoutParams(boardHeight, 1);

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

                        ImageView img = new ImageView(context);
                        img.setImageDrawable(v.getResources().getDrawable(R.drawable.black));

                        int eventX = (int) event.getX();
                        int eventY = (int) event.getY();

                        if (eventX > 15 && eventX < (boardWidth - 10)
                                && eventY > 15 && eventY < (boardHeight - 10)) {

                            int x = Math.round((float)eventX / offsetW);
                            int y = Math.round((float)eventY / offsetH);

                            if (x > 19)
                                x = 19;
                            if (y > 19)
                                y = 19;
                            Move move = new Move(x, y, boardLogic.currentPlayer == Move.Player.BLACK);
                            if (boardLogic.isValid(move))
                                drawStone(move, v);
                            updateScore();
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
                            if (move != null)
                                drawStone(move, v);
                            else
                                Log.w(TAG, "No more moves left!");
                        } else
                            Toast.makeText(getApplicationContext(), "Please choose a game!",
                                    Toast.LENGTH_LONG).show();
                        break;
                    case R.id.prevBtn:
                        if (currentStoneViewId >= 3) {
                            frameLayout.removeViewAt(currentStoneViewId--);
                            boardLogic.removeLastMoveFromBoardState();
                            if (boardLogic.currentIndex > 0) {
                                ImageView iv = (ImageView) frameLayout.getChildAt(currentStoneViewId);
                                iv.setImageResource(boardLogic.currentPlayer == Move.Player.BLACK ?
                                        R.drawable.white_marked : R.drawable.black_marked);
                            }
                        }
                        break;
                    case R.id.rewindBtn:
                        if (currentStoneViewId >= 3) {
                            frameLayout.removeViews(3, currentStoneViewId - 2);
                            currentStoneViewId = 0;
                            boardLogic.clearBoardState();
                        }
                        break;
                    case R.id.forwardBtn:
                        if (gameReady) {
                            Move m;
                            while ((m = boardLogic.getNextMove()) != null) {
                                drawStone(m, v);
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


    private void updateScore() {
        testLabel.setText("score: " + String.valueOf(boardLogic.score));
        LinearLayout fill =  (LinearLayout) findViewById(R.id.scoreFill);
        LinearLayout bkg =  (LinearLayout) findViewById(R.id.scoreBkg);
        ViewGroup.LayoutParams paramsBkg =  bkg.getLayoutParams();
        ViewGroup.LayoutParams params =  fill.getLayoutParams();
        params.width = boardLogic.score * (paramsBkg.width / 10);

        fill.setLayoutParams(params);
    }

    private void drawStone(Move move, View view) {
        Log.i(TAG, "Putting stone at " + String.valueOf(move.x) + ":" + String.valueOf(move.y));
        ImageView img = new ImageView(context);
        img.setImageDrawable(view.getResources().getDrawable(
                move.player == Move.Player.BLACK ? R.drawable.black_marked : R.drawable.white_marked));

        if (move.player == Move.Player.WHITE)
            STONE_SIZE = 30;
        else
            STONE_SIZE = 28;

        FrameLayout.LayoutParams stoneParam = new FrameLayout.LayoutParams(STONE_SIZE, STONE_SIZE);
        stoneParam.leftMargin = ((int) (move.x * offsetW)) - (STONE_SIZE / 2) + 1/*imgpadding*/;
        stoneParam.topMargin = ((int) (move.y * offsetH)) - (STONE_SIZE / 2);
        frameLayout.addView(img, stoneParam);
        currentStoneViewId = frameLayout.indexOfChild(img);

        if (boardLogic.currentIndex >= 2) {
            ImageView iv = (ImageView) frameLayout.getChildAt(currentStoneViewId - 1);
            iv.setImageResource(boardLogic.currentPlayer == Move.Player.BLACK ?
                    R.drawable.white : R.drawable.black);
        }

        boardLogic.addMoveToBoardState(move);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);

            if (filePath.substring(filePath.length() - 3).equals(FILE_EXT)) {
                Log.i(TAG, "Opening file: " + filePath);

                gameReady = boardLogic.parse(filePath);

            } else {
                Toast.makeText(getApplicationContext(), "This is not SGF!",
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, FilePickerActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_openGame:
                Intent intent = new Intent(this, FilePickerActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
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
