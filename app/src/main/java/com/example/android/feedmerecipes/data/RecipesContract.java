package com.example.android.feedmerecipes.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;


public class RecipesContract {

    public RecipesContract(){}

    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String UNIQUE = " UNIQUE";
    private static final String AUTO_INCREMENT = " AUTOINCREMENT";
    private static final String NOT_NULL = " NOT NULL";
    private static final String COMMA_SEP = ",";

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.example.android.feedmerecipes";
    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_RECIPES = "recipes";

    public static abstract class Recipes implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_RECIPES).build();
        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_RECIPES;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_RECIPES;

        public static final String TABLE_NAME = "recipes";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_URL = "url";
        public static final String COLUMN_RID = "rId";
        public static final String COLUMN_TEXT = "text";

        public static final String SQL_CREATE_RECIPES =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        _ID + INT_TYPE + PRIMARY_KEY + AUTO_INCREMENT + COMMA_SEP +
                        COLUMN_TITLE + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                        COLUMN_URL + TEXT_TYPE + NOT_NULL + COMMA_SEP +
                        COLUMN_RID + TEXT_TYPE + UNIQUE +NOT_NULL + COMMA_SEP +
                        COLUMN_TEXT + TEXT_TYPE +
                        " )";

        public static final String SQL_DELETE_RECIPES =
            "DROP TABLE IF EXISTS " + TABLE_NAME;


        public static Uri buildRecipeUri(String rId) {
            return CONTENT_URI.buildUpon().appendPath(rId).build();
        }

        public static Uri buildRecipeUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

        public static String getUriParam(Uri uri) {
            return uri.getLastPathSegment();
        }

    }
}
