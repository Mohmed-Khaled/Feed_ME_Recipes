package com.example.android.feedmerecipes;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.android.feedmerecipes.data.RecipesContract;
import com.example.android.feedmerecipes.data.RecipesDbHelper;


public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(RecipesDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new RecipesDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertDb(){
        //Test data
        String testTitle = "Burger";
        String testURL = "burger/burger.jpg";
        String testRId = "1";
        String testText = "meat";

        RecipesDbHelper dbHelper = new RecipesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(RecipesContract.Recipes.COLUMN_TITLE, testTitle);
        values.put(RecipesContract.Recipes.COLUMN_URL, testURL);
        values.put(RecipesContract.Recipes.COLUMN_RID, testRId);
        values.put(RecipesContract.Recipes.COLUMN_TEXT, testText);

        long recipeRowId = db.insert( RecipesContract.Recipes.TABLE_NAME, null, values);
        assertTrue(recipeRowId != -1);
        Log.d(LOG_TAG, "New row id: " + recipeRowId);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // Specify which columns you want.
        String[] columns = {
                RecipesContract.Recipes.COLUMN_TITLE,
                RecipesContract.Recipes.COLUMN_URL,
                RecipesContract.Recipes.COLUMN_RID,
                RecipesContract.Recipes.COLUMN_TEXT
        };

        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                RecipesContract.Recipes.TABLE_NAME,  // Table to Query
                columns,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // If possible, move to the first row of the query results.
        if (cursor.moveToFirst()) {
            // Get the value in each column by finding the appropriate column index.
            int titleIndex = cursor.getColumnIndex(RecipesContract.Recipes.COLUMN_TITLE);
            String title = cursor.getString(titleIndex);
            int urlIndex = cursor.getColumnIndex(RecipesContract.Recipes.COLUMN_URL);
            String url = cursor.getString(urlIndex);
            int rIdIndex = cursor.getColumnIndex(RecipesContract.Recipes.COLUMN_RID);
            String rId = cursor.getString(rIdIndex);
            int textIndex = cursor.getColumnIndex(RecipesContract.Recipes.COLUMN_TEXT);
            String text = cursor.getString(textIndex);

            // Hooray, data was returned!  Assert that it's the right data, and that the database
            // creation code is working as intended.
            assertEquals(testTitle,title);
            assertEquals(testURL,url);
            assertEquals(testRId,rId);
            assertEquals(testText, text);
        } else {
            fail("No values returned :(");
        }
        dbHelper.close();
        cursor.close();

    }
}
