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
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;



public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";
    private static int GRID_PADDING = 20;
    private static int STONE_SIZE = 28;
    private static int REQUEST_CODE = 1;

    ImageView boardImage;
    ImageView stoneImage;
    Context context;
    int boardWidth;
    int boardHeight;
    int stoneWidth;
    int stoneHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        context = this.getApplicationContext();
        boardImage = (ImageView) findViewById(R.id.backgroundImg);
        stoneImage = (ImageView) findViewById(R.id.blackImg);

        BoardLogic boardLogic = new BoardLogic();


        ViewTreeObserver vto = boardImage.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                boardImage.getViewTreeObserver().removeOnPreDrawListener(this);
                boardWidth = boardImage.getMeasuredHeight();
                boardHeight = boardImage.getMeasuredWidth();

                stoneWidth = stoneImage.getMeasuredHeight();
                stoneHeight = stoneImage.getMeasuredWidth();

                Log.i(TAG, "boardWidth: " + String.valueOf(stoneWidth + ", boardHeight: " +
                String.valueOf(stoneHeight)));

                drawBoardGrid();

                return true;
            }
        });


        final FrameLayout frameLayout = (FrameLayout)findViewById(R.id.background);


        final View horiz = (View) findViewById(R.id.horiz);
        final View vertic = (View) findViewById(R.id.vertic);
        horiz.setVisibility(View.INVISIBLE);
        vertic.setVisibility(View.INVISIBLE);


        boardImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                FrameLayout.LayoutParams horizParam = new FrameLayout.LayoutParams(1, boardWidth);
                FrameLayout.LayoutParams verticParam = new FrameLayout.LayoutParams(boardHeight, 1);
                FrameLayout.LayoutParams stoneParam = new FrameLayout.LayoutParams(STONE_SIZE, STONE_SIZE);
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

                            float offsetW = (boardWidth - (GRID_PADDING * 2)) / 18;
                            float offsetH = (boardHeight - (GRID_PADDING * 2)) / 18;
                            int x = ((int) (eventX / offsetW)) + 1;
                            int y = ((int) (eventY / offsetH)) + 1;

                            if (x > 19)
                                x = 19;
                            if (y > 19)
                                y = 19;

                            stoneParam.leftMargin = ((int) (x * offsetW)) - (STONE_SIZE / 2) + 1/*imgpadding*/;
                            stoneParam.topMargin = ((int) (y * offsetH)) - (STONE_SIZE / 2);
                            frameLayout.addView(img, stoneParam);

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

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            Log.i(TAG, "Opening file: " + filePath);
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
