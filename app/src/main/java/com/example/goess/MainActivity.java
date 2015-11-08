package com.example.goess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private static String TAG = "MainActivity";
    private static int GRID_PADDING = 20;
    private static int STONE_SIZE = 22;

    ImageView boardImage;
    Context context;
    int boardWidth;
    int boardHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this.getApplicationContext();
        boardImage = (ImageView) findViewById(R.id.backgroundImg);


        ViewTreeObserver vto = boardImage.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {
                boardImage.getViewTreeObserver().removeOnPreDrawListener(this);
                boardWidth = boardImage.getMeasuredHeight();
                boardHeight = boardImage.getMeasuredWidth();

                drawGrid();

                return true;
            }
        });


        final FrameLayout frameLayout = (FrameLayout)findViewById(R.id.background);

        final ImageView stoneImg = (ImageView) findViewById(R.id.blackImg);
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
                ViewGroup owner = (ViewGroup) stoneImg.getParent();

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
                        frameLayout.addView(vertic, verticParam);

                        break;
                    case MotionEvent.ACTION_UP:

                        owner.removeView(horiz);
                        owner.removeView(vertic);

                        ImageView img = new ImageView(context);
                        img.setImageDrawable(v.getResources().getDrawable(R.drawable.black));

                        stoneParam.leftMargin = (int) event.getX();
                        stoneParam.topMargin = (int) event.getY();
                        frameLayout.addView(img, stoneParam);

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

    private void drawGrid() {
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
