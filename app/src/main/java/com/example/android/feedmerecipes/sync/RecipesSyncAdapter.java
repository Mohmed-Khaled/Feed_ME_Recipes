package com.example.android.feedmerecipes.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.android.feedmerecipes.R;
import com.example.android.feedmerecipes.data.RecipesContract;
import com.example.android.feedmerecipes.extra.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RecipesSyncAdapter extends AbstractThreadedSyncAdapter {

    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;
    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 60 * 24;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;


    /**
     * Set up the sync adapter
     */
    public RecipesSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        /*
         * If your app uses a content resolver, get an instance of it
         * from the incoming Context
         */
        mContentResolver = context.getContentResolver();
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d("DEBUG","Performing Sync");
        HttpURLConnection urlConnection = null;
        InputStream inputStream;
        Uri builtUri;
        final String BASE_URL = "http://food2fork.com/api/";
        final String API_KEY = "5db24048ca45d2dd705854d3930917d9";
        final String SEARCH_PATH = "search";
        final String GET_PATH = "get";
        final String KEY_PARAM = "key";
        final String TITLE_PARAM = "key";
        final String RID_PARAM = "rId";
        String mTitle = extras.getString("mTitle",null);
        String mRId = extras.getString("mRId",null);
        Log.d("DEBUG","title = "+mTitle);
        Log.d("DEBUG","rId = "+mRId);
        if (mTitle != null && mRId == null){
            builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(SEARCH_PATH)
                    .appendQueryParameter(KEY_PARAM, API_KEY)
                    .appendQueryParameter(TITLE_PARAM, mTitle)
                    .build();
            Log.d("DEBUG","Search Sync");

        } else if (mTitle != null) {
            builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(GET_PATH)
                    .appendQueryParameter(KEY_PARAM, API_KEY)
                    .appendQueryParameter(RID_PARAM, mRId)
                    .build();
            Log.d("DEBUG","Recipe Sync");
        } else {
            builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(SEARCH_PATH)
                    .appendQueryParameter(KEY_PARAM, API_KEY)
                    .build();
            Log.d("DEBUG","Main Sync");

        }
        try {
            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            String dataJSONStr = Utilities.convertInputStreamToString(inputStream);
            JSONObject jsonObject = new JSONObject(dataJSONStr);
            if (mRId != null) {
                mContentResolver.update(
                        RecipesContract.Recipes.buildRecipeUri(mRId),  // Table to Query
                        Utilities.getRecipeData(jsonObject),
                        RecipesContract.Recipes.COLUMN_RID + " = ?",
                        new String[]{mRId}
                );

            } else{
                mContentResolver.bulkInsert(RecipesContract.Recipes.CONTENT_URI,
                        Utilities.getMainData(jsonObject));

            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context,String title,String rId) {
        Log.d("DEBUG","Hello Sync");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        bundle.putString("mTitle",title);
        bundle.putString("mRId",rId);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        RecipesSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context,null,null);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
