package com.example.android.feedmerecipes.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class RecipesSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static RecipesSyncAdapter sRecipesSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("SunshineSyncService", "onCreate - SunshineSyncService");
        synchronized (sSyncAdapterLock) {
            if (sRecipesSyncAdapter == null) {
                sRecipesSyncAdapter = new RecipesSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sRecipesSyncAdapter.getSyncAdapterBinder();
    }
}
