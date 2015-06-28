package com.example.android.feedmerecipes.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RecipesAuthenticatorService extends Service {
    // Instance field that stores the authenticator object
    private RecipesAuthenticator mAuthenticator;
    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new RecipesAuthenticator(this);
    }
    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
