package org.happydroid.goess.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.ResultReceiver;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GameDownloadService extends Service {

    private static final String TAG = "GameDownloadService";
    public static final int UPDATE_RESULT = 1234;
    private DownloadTask gameDownloadTask;
    ResultReceiver receiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        receiver = (ResultReceiver) intent.getParcelableExtra("receiver");
        Uri uri = intent.getData();
        gameDownloadTask = new DownloadTask();
        gameDownloadTask.execute(uri);

        return Service.START_NOT_STICKY; //START_FLAG_REDELIVERY?

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class DownloadTask extends AsyncTask<Uri, Integer, String> {

        private static final String TAG = "GameDownloadAsyncTask";

        public DownloadTask() {
        }

        @Override
        protected String doInBackground(Uri... uri) {
            Log.d(TAG, "connecting....");
            FileOutputStream output = null;
            InputStream input = null;
            HttpURLConnection connection = null;

            try {
                URL url = new URL(uri[0].toString());
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.v(TAG, "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage());
                }

                input = connection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                StringBuilder str = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
                input.close();

                Bundle resultData = new Bundle();
                resultData.putString("sgf", str.toString());
                receiver.send(UPDATE_RESULT, resultData);

            } catch (Exception e) {
                Log.v(TAG, e.toString());
            } finally {
                try {
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                    Log.v(TAG, ignored.toString());
                }

                if (connection != null)
                    connection.disconnect();
            }

            return "";
        }

    }

}
