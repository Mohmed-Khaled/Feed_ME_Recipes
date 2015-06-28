package com.example.android.feedmerecipes.extra;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.android.feedmerecipes.data.RecipesContract;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utilities {

/*    public static void updateHistory (Context context,String text) {

        ContentResolver resolver = context.getContentResolver();
        Uri checkEntry = InputHistoryContract.InputEntry.buildEntryUri(text);
        String[] columns = {
                InputHistoryContract.InputEntry.COLUMN_TEXT,
                InputHistoryContract.InputEntry.COLUMN_FREQUENCY
        };
        Cursor cursor = resolver.query(
                checkEntry,  // Table to Query
                columns,
                "", // Columns for the "where" clause
                new String[]{""}, // Values for the "where" clause
                null // sort order
        );
        if (cursor.moveToFirst()) {
            int frequencyIndex = cursor.getColumnIndex((InputHistoryContract.InputEntry.COLUMN_FREQUENCY));
            int frequency = cursor.getInt(frequencyIndex);
            frequency++;
            ContentValues values = new ContentValues();
            values.put(InputHistoryContract.InputEntry.COLUMN_FREQUENCY, frequency);
            int rowUpdate = resolver.update(
                    checkEntry,
                    values,
                    "",
                    new String[]{""}
            );
        } else {
            ContentValues values = new ContentValues();
            values.put(InputHistoryContract.InputEntry.COLUMN_TEXT, text);
            values.put(InputHistoryContract.InputEntry.COLUMN_FREQUENCY, 1);
            Uri newRow = resolver.insert(InputHistoryContract.InputEntry.CONTENT_URI, values);
        }
        cursor.close();
    }*/

    public static boolean isNetworkStatusAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null)
        {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if(netInfo != null)
                if(netInfo.isConnected())
                    return true;
        }
        return false;
    }

    public static String convertInputStreamToString(InputStream inputStream) throws IOException {

        StringBuilder buffer = new StringBuilder();

        if (inputStream == null) {
            // Nothing to do.
            return null;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
            // But it does make debugging a *lot* easier if you print out the completed
            // buffer for debugging.
            buffer.append(line).append("\n");
        }

        if (buffer.length() == 0) {
            // Stream was empty.  No point in parsing.
            return null;
        }
        return buffer.toString();
    }

    public static ContentValues[] getMainData(JSONObject jsonObject)
    {
        JSONArray jsonArray = jsonObject.optJSONArray("recipes");
        ContentValues[] returnValues = new ContentValues[jsonArray.length()];
        for (int i = 0;i < jsonArray.length();i++){
            JSONObject jo = jsonArray.optJSONObject(i);
            String title = jo.optString("title");
            String url = jo.optString("image_url");
            String rId  = jo.optString("recipe_id");
            ContentValues values = new ContentValues();
            values.put(RecipesContract.Recipes.COLUMN_TITLE, title);
            values.put(RecipesContract.Recipes.COLUMN_URL, url);
            values.put(RecipesContract.Recipes.COLUMN_RID, rId);
            returnValues[i] = values;
        }
        return returnValues;
    }
    public static ContentValues getRecipeData(JSONObject jsonObject)
    {
        ContentValues returnValues = new ContentValues() ;
        String text = "";
        JSONObject recipeObject = jsonObject.optJSONObject("recipe");
        JSONArray ingredients = recipeObject.optJSONArray("ingredients");
        for (int i = 0;i < ingredients.length();i++){
            text += String.valueOf(i+1)+"] ";
            text += ingredients.optString(i);
            text += ",\n";
        }
        returnValues.put(RecipesContract.Recipes.COLUMN_TEXT, text);
        return returnValues;
    }

    public static void loadImage(final Context context,final String imageUrl, final ImageView image){

        ImageLoader imageLoader = ImageLoader.getInstance();
        ImageLoaderConfiguration mImageLoaderConfig =
                new ImageLoaderConfiguration.Builder(context)
                        .denyCacheImageMultipleSizesInMemory()
                        .build();
        imageLoader.init(mImageLoaderConfig);
        DisplayImageOptions defaultOptions =
                new DisplayImageOptions.Builder()
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .build();

        imageLoader.displayImage(imageUrl, image, defaultOptions,new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                String message = "";
                switch (failReason.getType()) {
                    case IO_ERROR:
                        message = "Input/Output error";
                        break;
                    case DECODING_ERROR:
                        message = "Image can't be decoded";
                        break;
                    case NETWORK_DENIED:
                        message = "Downloads are denied";
                        break;
                    case OUT_OF_MEMORY:
                        message = "Out Of Memory error";
                        break;
                    case UNKNOWN:
                        message = "Unknown error";
                        break;
                }
                Log.e("ImageLoadingFailed", message);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }
}
