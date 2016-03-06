package org.happydroid.goess.service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import org.happydroid.goess.service.DownloadReceiver;
import org.happydroid.goess.service.GameDownloadService;


public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";
    private static final String UPDATE_URL = "http://dianull.magt.pl/sgf/new.sgf";

    DownloadReceiver receiver;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Requesting download service");
        Intent downloader = new Intent(context, GameDownloadService.class);
        downloader.setData(Uri.parse(UPDATE_URL));
        receiver = new DownloadReceiver(new Handler());
        receiver.setContext(context);
        downloader.putExtra("receiver", receiver);
        context.startService(downloader);
    }
}
