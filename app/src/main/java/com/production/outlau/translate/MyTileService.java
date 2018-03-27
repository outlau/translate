package com.production.outlau.translate;

import android.content.Intent;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;


@SuppressLint("Override")
@TargetApi(Build.VERSION_CODES.N)
public class MyTileService extends TileService {

    public static final String TAG = Widget.class.getSimpleName();

    @Override
    public void onTileAdded() {
        Log.d(TAG, "onTileAdded: ");
    }

    @Override
    public void onStartListening() {

        Tile tile = getQsTile();
        Log.d(TAG, "onStartListening: "+tile.getLabel());
    }

    @Override
    public void onClick() {
        Intent intent = new Intent(getApplicationContext(),
                MainFragmentActivity.class);
        startActivityAndCollapse(intent);

    }
}