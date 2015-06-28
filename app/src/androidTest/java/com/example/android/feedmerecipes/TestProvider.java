package com.example.android.feedmerecipes;


import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.android.feedmerecipes.data.RecipesContract;
import com.example.android.feedmerecipes.data.RecipesProvider;

public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();

    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // WeatherProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                RecipesProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: WeatherProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + RecipesContract.CONTENT_AUTHORITY,
                    providerInfo.authority, RecipesContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: WeatherProvider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    public void testDeleteRecords() {
        clearDB();
    }

    public void testGetType() {
        String dirType = mContext.getContentResolver().getType(RecipesContract.Recipes.CONTENT_URI);
        assertEquals(RecipesContract.Recipes.CONTENT_TYPE,dirType);
        String testGet = "2";
        Uri testRecipeUri = RecipesContract.Recipes.buildRecipeUri(testGet);
        String itemType = mContext.getContentResolver().getType(testRecipeUri);
        assertEquals(RecipesContract.Recipes.CONTENT_ITEM_TYPE,itemType);

    }

    public void testWriteRead(){
        //Test data
        String testTitle = "Burger";
        String testURL = "burger/burger.jpg";
        String testRId = "1";
        String testText = "burger + ketchup";

        ContentResolver resolver = mContext.getContentResolver();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(RecipesContract.Recipes.COLUMN_TITLE, testTitle);
        values.put(RecipesContract.Recipes.COLUMN_URL, testURL);
        values.put(RecipesContract.Recipes.COLUMN_RID, testRId);

        Uri newRow = resolver.insert(RecipesContract.Recipes.CONTENT_URI, values);
        Log.d(LOG_TAG, "New row uri: " + newRow);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = resolver.query(
                RecipesContract.Recipes.CONTENT_URI,  // Table to Query
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
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
            assertEquals(testTitle,title);
            assertEquals(testURL,url);
            assertEquals(testRId,rId);
        } else {
            fail("No values returned :(");
        }
        cursor.close();
        ContentValues values1 = new ContentValues();
        values1.put(RecipesContract.Recipes.COLUMN_TEXT, testText);
        int rowsUpdated = resolver.update(
                RecipesContract.Recipes.buildRecipeUri(testRId),  // Table to Query
                values1,
                null,
                null
        );
        assertEquals(1,rowsUpdated);
        Cursor cursor1 = resolver.query(
                RecipesContract.Recipes.buildRecipeUri(testRId),  // Table to Query
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // sort order
        );
        if (cursor1.moveToFirst()) {
            // Get the value in each column by finding the appropriate column index.
            int titleIndex = cursor1.getColumnIndex(RecipesContract.Recipes.COLUMN_TITLE);
            String title = cursor1.getString(titleIndex);
            int urlIndex = cursor1.getColumnIndex(RecipesContract.Recipes.COLUMN_URL);
            String url = cursor1.getString(urlIndex);
            int rIdIndex = cursor1.getColumnIndex(RecipesContract.Recipes.COLUMN_RID);
            String rId = cursor1.getString(rIdIndex);
            int textIndex = cursor1.getColumnIndex(RecipesContract.Recipes.COLUMN_TEXT);
            String text = cursor1.getString(textIndex);
            assertEquals(testTitle,title);
            assertEquals(testURL,url);
            assertEquals(testRId,rId);
            assertEquals(testText,text);
        } else {
            fail("No values returned :(");
        }
        cursor1.close();
        clearDB();
    }

    public void testBulkInsert(){
        clearDB();
        ContentResolver resolver = mContext.getContentResolver();
        ContentValues[] bulkInsertContentValues = createBulkInsertValues();
        int insertCount = mContext.getContentResolver().bulkInsert(RecipesContract.Recipes.CONTENT_URI, bulkInsertContentValues);
        assertEquals(BULK_INSERT_RECORDS_TO_INSERT, insertCount);
        Cursor cursor = resolver.query(
                RecipesContract.Recipes.CONTENT_URI,  // Table to Query
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // sort order
        );
        assertEquals(BULK_INSERT_RECORDS_TO_INSERT, cursor.getCount());
        cursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext() ) {
            assertEquals(createBulkInsertValues()[i],bulkInsertContentValues[i]);
        }
        cursor.close();
        clearDB();
    }

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

    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;
    static ContentValues[] createBulkInsertValues() {
        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues recipesValues = new ContentValues();
            recipesValues.put(RecipesContract.Recipes.COLUMN_TITLE, "Element #"+String.valueOf(i));
            recipesValues.put(RecipesContract.Recipes.COLUMN_URL, "Element/"+String.valueOf(i));
            recipesValues.put(RecipesContract.Recipes.COLUMN_RID, String.valueOf(i));
            returnContentValues[i] = recipesValues;
        }
        return returnContentValues;
    }
}
