package com.example.android.feedmerecipes.extra;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.android.feedmerecipes.data.RecipesContract;
import com.example.android.feedmerecipes.service.RecipesService;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utilities {

    public static void updateRecipes(Context context,int caller,String input,String rId) {
        Intent intent = new Intent(context, RecipesService.class);
        intent.putExtra(RecipesService.CALLER_EXTRA,caller);
        intent.putExtra(RecipesService.INPUT_EXTRA,input);
        intent.putExtra(RecipesService.RID_EXTRA,rId);
        context.startService(intent);
    }

    public static void addToFavorites(Context context,String title,String url,String rId,String text){
        ContentValues values = new ContentValues();
        values.put(RecipesContract.Favorites.COLUMN_TITLE,title);
        values.put(RecipesContract.Favorites.COLUMN_URL,url);
        values.put(RecipesContract.Favorites.COLUMN_RID,rId);
        values.put(RecipesContract.Favorites.COLUMN_TEXT,text);
        context.getContentResolver().insert(
                RecipesContract.Favorites.CONTENT_URI,
                values
        );
    }
    public static void removeFromFavorites(Context context,Uri uri){
        context.getContentResolver().delete(uri,null,null);
    }

    public static void removeFavorites(Context context){
        context.getContentResolver().delete(RecipesContract.Favorites.CONTENT_URI,null,null);
    }

    public static void clearData(Context context){
        context.getContentResolver().delete(RecipesContract.Recipes.CONTENT_URI,null,null);
        context.getContentResolver().delete(RecipesContract.Search.CONTENT_URI,null,null);
        deleteCache(context);

    }

    public static boolean checkData(Context context,Uri uri){
        Cursor cursor = context.getContentResolver().query(
                uri,  // Table to Query
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // sort order
        );
        if (cursor.moveToFirst()){
            if  (cursor.getString(cursor.getColumnIndex(RecipesContract.Recipes.COLUMN_TEXT)) != null){
                return true;
            }
        }
        cursor.close();
        return false;
    }

    public static boolean checkSearchData(Context context,Uri uri){
        Cursor cursor = context.getContentResolver().query(
                uri,  // Table to Query
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // sort order
        );
        if (cursor.moveToFirst()){
                return true;
        }
        cursor.close();
        return false;
    }

    public static boolean checkIfFavorite(Context context,Uri uri){
        Cursor cursor = context.getContentResolver().query(
                uri,  // Table to Query
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // sort order
        );
        if (cursor.moveToFirst()){
            return true;
        }
        cursor.close();
        return false;
    }

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
    public static ContentValues[] getSearchData(String input,JSONObject jsonObject)
    {
        JSONArray jsonArray = jsonObject.optJSONArray("recipes");
        ContentValues[] returnValues = new ContentValues[jsonArray.length()];
        for (int i = 0;i < jsonArray.length();i++){
            JSONObject jo = jsonArray.optJSONObject(i);
            String title = jo.optString("title");
            String url = jo.optString("image_url");
            String rId  = jo.optString("recipe_id");
            ContentValues values = new ContentValues();
            values.put(RecipesContract.Search.COLUMN_INPUT,input);
            values.put(RecipesContract.Search.COLUMN_TITLE, title);
            values.put(RecipesContract.Search.COLUMN_URL, url);
            values.put(RecipesContract.Search.COLUMN_RID, rId);
            returnValues[i] = values;
        }
        return returnValues;
    }
    public static ContentValues getRecipeData(int caller,JSONObject jsonObject)
    {
        ContentValues returnValues = new ContentValues() ;
        String text = "";
        JSONObject recipeObject = jsonObject.optJSONObject("recipe");
        JSONArray ingredients = recipeObject.optJSONArray("ingredients");
        for (int i = 0;i < ingredients.length();i++){
            text += String.valueOf(i+1)+"] ";
            text += ingredients.optString(i);
            if (i != ingredients.length() - 1)
                text += ",\n";
        }
        switch(caller){
            case 0:
                returnValues.put(RecipesContract.Recipes.COLUMN_TEXT, text);
                return returnValues;
            case 1:
                returnValues.put(RecipesContract.Search.COLUMN_TEXT, text);
                return returnValues;
            default:
                throw new UnsupportedOperationException("Unknown caller: " + caller);
        }
    }

    public static void loadImage(final Context context,final String imageUrl, final ImageView image){

        ImageLoader imageLoader = ImageLoader.getInstance();
        ImageLoaderConfiguration mImageLoaderConfig =
                new ImageLoaderConfiguration.Builder(context)
                        .denyCacheImageMultipleSizesInMemory()
                        .build();
        DisplayImageOptions defaultOptions =
                new DisplayImageOptions.Builder()
                        .showImageOnLoading(android.R.drawable.stat_sys_download)
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .build();
        imageLoader.init(mImageLoaderConfig);
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

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        }
        return false;
    }
}
