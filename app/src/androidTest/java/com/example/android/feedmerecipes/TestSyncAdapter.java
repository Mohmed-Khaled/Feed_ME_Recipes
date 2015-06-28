package com.example.android.feedmerecipes;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.example.android.feedmerecipes.data.RecipesContract;

public class TestSyncAdapter extends AndroidTestCase {

    public void clearDB(){
        ContentResolver resolver = mContext.getContentResolver();
        resolver.delete(RecipesContract.Recipes.CONTENT_URI, null, null);
        Cursor cursorToClear = resolver.query(
                RecipesContract.Recipes.CONTENT_URI,  // Table to Query
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // sort order
        );
        assertEquals(cursorToClear.moveToFirst(),false);
        cursorToClear.close();
    }
    public boolean validateSync(Uri uri){
        ContentResolver resolver = mContext.getContentResolver();
        Cursor cursor = resolver.query(
                uri,  // Table to Query
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // sort order
        );
        if (cursor.moveToFirst()){
            cursor.close();
            return true;
        }
        return false;
    }

/*    public void testMain(){
        clearDB();
        RecipesSyncAdapter.syncImmediately(mContext,null,null);
        Uri uri = RecipesContract.Recipes.CONTENT_URI;
        assertTrue(validateSync(uri));
    }
    public void testRecipe(){
        ContentResolver resolver = mContext.getContentResolver();
        String title = null;
        String rId = null;
        Cursor cursor = resolver.query(
                RecipesContract.Recipes.CONTENT_URI,  // Table to Query
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // sort order
        );
        if (cursor.moveToFirst()){
            int titleIndex = cursor.getColumnIndex(RecipesContract.Recipes.COLUMN_TITLE);
            title = cursor.getString(titleIndex);
            int rIdIndex = cursor.getColumnIndex(RecipesContract.Recipes.COLUMN_RID);
            rId = cursor.getString(rIdIndex);
        }
        RecipesSyncAdapter.syncImmediately(mContext,title, rId);
        Uri uri = RecipesContract.Recipes.buildRecipeUri(rId);
        assertTrue(validateSync(uri));
        cursor.close();
        clearDB();
    }*/
}
