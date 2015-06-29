package com.example.android.feedmerecipes.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;


public class RecipesProvider extends ContentProvider {
    public RecipesProvider() {
    }

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private RecipesDbHelper mOpenHelper;


    private static final int RECIPES = 100;
    private static final int RECIPE = 101;
    private static final int HISTORY = 102;
    private static final int SEARCH = 103;
    private static final int SEARCH_ITEM = 104;
    private static final int FAVORITES = 105;
    private static final int FAVORITE = 106;

    private static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = RecipesContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, RecipesContract.PATH_RECIPES, RECIPES);
        matcher.addURI(authority, RecipesContract.PATH_RECIPES + "/*", RECIPE);
        matcher.addURI(authority, RecipesContract.PATH_SEARCH, HISTORY);
        matcher.addURI(authority, RecipesContract.PATH_SEARCH + "/*", SEARCH);
        matcher.addURI(authority, RecipesContract.PATH_SEARCH + "/*/*", SEARCH_ITEM);
        matcher.addURI(authority, RecipesContract.PATH_FAVORITES, FAVORITES);
        matcher.addURI(authority, RecipesContract.PATH_FAVORITES + "/*", FAVORITE);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new RecipesDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (match) {
            case RECIPE:
                String recipeRId = RecipesContract.getUriParam(uri);
                selection = RecipesContract.Recipes.COLUMN_RID + " = ?";
                selectionArgs = new String[] {recipeRId};
                retCursor = mOpenHelper.getReadableDatabase().query(
                        RecipesContract.Recipes.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
                break;
            case RECIPES:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        RecipesContract.Recipes.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
                break;
            case SEARCH:
                String searchInput = RecipesContract.getUriParam(uri);
                selection = RecipesContract.Search.COLUMN_INPUT + " = ?";
                selectionArgs = new String[] {searchInput};
                retCursor = mOpenHelper.getReadableDatabase().query(
                        RecipesContract.Search.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
                break;
            case SEARCH_ITEM:
                String searchRId = RecipesContract.getUriParam(uri);
                selection = RecipesContract.Search.COLUMN_RID + " = ?";
                selectionArgs = new String[] {searchRId};
                retCursor = mOpenHelper.getReadableDatabase().query(
                        RecipesContract.Search.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
                break;
            case FAVORITES:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        RecipesContract.Favorites.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
                break;
            case FAVORITE:
                String favoriteRId = RecipesContract.getUriParam(uri);
                selection = RecipesContract.Favorites.COLUMN_RID + " = ?";
                selectionArgs = new String[] {favoriteRId};
                retCursor = mOpenHelper.getReadableDatabase().query(
                        RecipesContract.Favorites.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        null
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case RECIPES:
                return RecipesContract.Recipes.CONTENT_TYPE;
            case RECIPE:
                return RecipesContract.Recipes.CONTENT_ITEM_TYPE;
            case SEARCH:
                return RecipesContract.Search.CONTENT_TYPE;
            case SEARCH_ITEM:
                return RecipesContract.Search.CONTENT_ITEM_TYPE;
            case FAVORITES:
                return RecipesContract.Favorites.CONTENT_TYPE;
            case FAVORITE:
                return RecipesContract.Favorites.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Long insertID;
        Uri newInsert;
        switch (match) {
            case RECIPES:
                insertID = db.insert(
                        RecipesContract.Recipes.TABLE_NAME,
                        null,
                        contentValues
                );
                if (insertID > 0)
                    newInsert = RecipesContract.Recipes.buildRecipeUri(insertID);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case SEARCH:
                insertID = db.insert(
                        RecipesContract.Search.TABLE_NAME,
                        null,
                        contentValues
                );
                if (insertID > 0)
                    newInsert = RecipesContract.Search.buildSearchUri(insertID);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case FAVORITES:
                insertID = db.insert(
                        RecipesContract.Favorites.TABLE_NAME,
                        null,
                        contentValues
                );
                if (insertID > 0)
                    newInsert = RecipesContract.Favorites.buildFavoritesUri(insertID);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return newInsert;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case RECIPES:
                rowsDeleted = db.delete(RecipesContract.Recipes.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case HISTORY:
                rowsDeleted = db.delete(RecipesContract.Search.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case FAVORITES:
                rowsDeleted = db.delete(RecipesContract.Favorites.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            case FAVORITE:
                String favoriteRId = RecipesContract.getUriParam(uri);
                selection = RecipesContract.Favorites.COLUMN_RID + " = ?";
                selectionArgs = new String[] {favoriteRId};
                rowsDeleted = db.delete(RecipesContract.Favorites.TABLE_NAME,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match) {
            case RECIPE:
                String recipeRId = RecipesContract.getUriParam(uri);
                selection = RecipesContract.Recipes.COLUMN_RID + " = ?";
                selectionArgs = new String[] {recipeRId};
                rowsUpdated = db.update(RecipesContract.Recipes.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs
                );
                break;
            case SEARCH_ITEM:
                String searchRId = RecipesContract.getUriParam(uri);
                selection = RecipesContract.Search.COLUMN_RID + " = ?";
                selectionArgs = new String[] {searchRId};
                rowsUpdated = db.update(RecipesContract.Search.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, @NonNull ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case RECIPES:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(RecipesContract.Recipes.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case SEARCH:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(RecipesContract.Search.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
