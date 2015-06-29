package com.example.android.feedmerecipes.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RecipesDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "feedmerecipes.db";

    public RecipesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(RecipesContract.Recipes.SQL_CREATE_RECIPES);
        db.execSQL(RecipesContract.History.SQL_CREATE_HISTORY);
        db.execSQL(RecipesContract.Search.SQL_CREATE_SEARCH);
        db.execSQL(RecipesContract.Favorites.SQL_CREATE_FAVORITES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(RecipesContract.Recipes.SQL_DELETE_RECIPES);
        db.execSQL(RecipesContract.History.SQL_DELETE_HISTORY);
        db.execSQL(RecipesContract.Search.SQL_DELETE_SEARCH);
        db.execSQL(RecipesContract.Favorites.SQL_DELETE_FAVORITES);
        onCreate(db);
    }

}
