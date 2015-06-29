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
import com.example.android.feedmerecipes.extra.Utilities;
import com.example.android.feedmerecipes.service.RecipesService;

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    protected RecipesAdapter mAdapter;
    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    private static final int MAIN_LOADER = 0;
    private static final String[] RECIPE_COLUMNS = {
            RecipesContract.Recipes.TABLE_NAME + "." + RecipesContract.Recipes._ID,
            RecipesContract.Recipes.COLUMN_TITLE,
            RecipesContract.Recipes.COLUMN_URL,
            RecipesContract.Recipes.COLUMN_RID,
            RecipesContract.Recipes.COLUMN_TEXT
    };
    // These indices are tied to RECIPE_COLUMNS.If RECIPE_COLUMNS changes, these must change.
    //public static final int COL_RECIPE_ID = 0;
    //public static final int COL_RECIPE_TITLE = 1;
    //public static final int COL_RECIPE_URL = 2;
    public static final int COL_RECIPE_RID = 3;
    //public static final int COL_RECIPE_TEXT = 4;


    public MainFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAdapter = new RecipesAdapter(getActivity(),null,0);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (ListView) rootView.findViewById(R.id.main_list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (cursor != null) {
                    String rId = cursor.getString(COL_RECIPE_RID);
                    Utilities.updateRecipes(getActivity(),RecipesService.CALLER_RECIPE,null, rId);
                    ((Callback) getActivity()).onItemSelected(RecipesContract.Recipes.buildRecipeUri(rId));
                }
                mPosition = position;
            }
        });
        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Utilities.updateRecipes(getActivity(),RecipesService.CALLER_RECIPE, null,null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MAIN_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String sortOrder = RecipesContract.Recipes._ID + " DESC";
        return new CursorLoader(
                getActivity(),
                RecipesContract.Recipes.CONTENT_URI,
                RECIPE_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            mListView.smoothScrollToPosition(mPosition);
        }
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
