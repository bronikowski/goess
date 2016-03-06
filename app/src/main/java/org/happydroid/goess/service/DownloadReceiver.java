package org.happydroid.goess.service;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

public class DownloadReceiver extends ResultReceiver {

    private static final String TAG = "DownloadReceiver";
    private static String APP_PREFERENCES_TODAYS_GAME = "GoessTodaysGame";
    Context context;

    public DownloadReceiver(Handler handler) {
        super(handler);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        super.onReceiveResult(resultCode, resultData);
        if (resultCode == GameDownloadService.UPDATE_RESULT) {
            String sgf = resultData.getString("sgf");
            Log.v(TAG, "received " + sgf);

            SharedPreferences.Editor editor = context.getSharedPreferences(APP_PREFERENCES_TODAYS_GAME, Context.MODE_PRIVATE).edit();
            editor.putString("todaysGame", sgf);
            editor.commit();
        }
    }
}
