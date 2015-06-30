package com.example.android.feedmerecipes.service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;

import com.example.android.feedmerecipes.data.RecipesContract;
import com.example.android.feedmerecipes.extra.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class RecipesService extends IntentService {

    public static final int CALLER_RECIPE = 0;
    public static final int CALLER_SEARCH = 1;
    public static final String CALLER_EXTRA = "caller";
    public static final String INPUT_EXTRA = "input";
    public static final String RID_EXTRA = "tId";
    public RecipesService() {
        super("Recipes");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        int caller = intent.getIntExtra(CALLER_EXTRA,-1);
        String input = intent.getStringExtra(INPUT_EXTRA);
        String rId = intent.getStringExtra(RID_EXTRA);
        ContentResolver resolver = this.getContentResolver();
        HttpURLConnection urlConnection = null;
        InputStream inputStream;
        Uri builtUri;
        final String BASE_URL = "http://food2fork.com/api/";
        final String API_KEY = "5db24048ca45d2dd705854d3930917d9";
        final String SEARCH_PATH = "search";
        final String GET_PATH = "get";
        final String KEY_PARAM = "key";
        final String INPUT_PARAM = "q";
        final String RID_PARAM = "rId";
        if (input != null && rId == null){
            builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(SEARCH_PATH)
                    .appendQueryParameter(KEY_PARAM, API_KEY)
                    .appendQueryParameter(INPUT_PARAM, input)
                    .build();
        } else if (rId != null) {
            builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(GET_PATH)
                    .appendQueryParameter(KEY_PARAM, API_KEY)
                    .appendQueryParameter(RID_PARAM, rId)
                    .build();
        } else {
            builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendPath(SEARCH_PATH)
                    .appendQueryParameter(KEY_PARAM, API_KEY)
                    .build();
        }
        try {
            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            inputStream = urlConnection.getInputStream();
            String dataJSONStr = Utilities.convertInputStreamToString(inputStream);
            JSONObject jsonObject = new JSONObject(dataJSONStr);
            if (rId != null && caller == 0) {
                resolver.update(
                        RecipesContract.Recipes.buildRecipeUri(rId),  // Table to Query
                        Utilities.getRecipeData(caller,jsonObject),
                        null,
                        null
                );
            } else if (input != null && rId != null && caller == 1) {
                resolver.update(
                        RecipesContract.Search.buildSearchUri(input, rId),  // Table to Query
                        Utilities.getRecipeData(caller, jsonObject),
                        null,
                        null
                );
            } else if (input != null && caller == 1){
                resolver.bulkInsert(RecipesContract.Search.buildSearchUri(input),
                        Utilities.getSearchData(input, jsonObject));
            } else{
                resolver.bulkInsert(RecipesContract.Recipes.CONTENT_URI,
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
}
