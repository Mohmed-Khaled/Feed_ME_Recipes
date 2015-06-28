package com.example.android.feedmerecipes;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.feedmerecipes.data.RecipesContract;
import com.example.android.feedmerecipes.extra.RecipesAdapter;
import com.example.android.feedmerecipes.sync.RecipesSyncAdapter;

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    protected RecipesAdapter mAdapter;
    private static final int MAIN_LOADER = 0;
    private static final String[] RECIPE_COLUMNS = {
            RecipesContract.Recipes.TABLE_NAME + "." + RecipesContract.Recipes._ID,
            RecipesContract.Recipes.COLUMN_TITLE,
            RecipesContract.Recipes.COLUMN_URL,
            RecipesContract.Recipes.COLUMN_RID,
            RecipesContract.Recipes.COLUMN_TEXT,
    };
    // These indices are tied to RECIPE_COLUMNS.If RECIPE_COLUMNS changes, these must change.
    public static final int COL_RECIPE_ID = 0;
    public static final int COL_RECIPE_TITLE = 1;
    public static final int COL_RECIPE_URL = 2;
    public static final int COL_RECIPE_RID = 3;
    public static final int COL_RECIPE_TEXT = 4;


    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAdapter = new RecipesAdapter(getActivity(),null,0);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // Get a reference to the ListView, and attach this adapter to it.
        ListView listView = (ListView) rootView.findViewById(R.id.main_list);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    String title = cursor.getString(COL_RECIPE_TITLE);
                    String rId = cursor.getString(COL_RECIPE_RID);
                    updateRecipe(title,rId);
                    ((Callback) getActivity()).onItemSelected(RecipesContract.Recipes.buildRecipeUri(rId));
                }
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateRecipes();
    }

    private void updateRecipes() {
        RecipesSyncAdapter.syncImmediately(getActivity(),null,null);
    }
    private void updateRecipe(String title,String rId) {
        RecipesSyncAdapter.syncImmediately(getActivity(),title,rId);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MAIN_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                getActivity(),
                RecipesContract.Recipes.CONTENT_URI,
                RECIPE_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    /**
     * A callback interface that all activities containing this fragment must
     * implement. This mechanism allows activities to be notified of item
     * selections.
     */
    public interface Callback {

        void onItemSelected(Uri recipeUri);
    }

}
