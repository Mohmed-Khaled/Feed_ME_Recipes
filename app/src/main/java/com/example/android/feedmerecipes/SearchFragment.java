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
import android.widget.GridView;
import android.widget.ListView;

import com.example.android.feedmerecipes.data.RecipesContract;
import com.example.android.feedmerecipes.extra.RecipesAdapter;
import com.example.android.feedmerecipes.extra.Utilities;
import com.example.android.feedmerecipes.service.RecipesService;

/**
 * A placeholder fragment containing a simple view.
 */
public class SearchFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static boolean pendingRefresh = false;
    public static String pendingInput = null;
    static final String SEARCH_QUERY = "query";
    static final String LIST_GRID = "two pane";
    private String mInput;
    protected RecipesAdapter mAdapter;
    private ListView mListView;
    private GridView mGridView;
    private int mPosition;
    private static final String SELECTED_KEY = "selected_position";
    private boolean mTwoPane;
    private static final int SEARCH_LOADER = 0;
    private static final String[] SEARCH_COLUMNS = {
            RecipesContract.Search.TABLE_NAME + "." + RecipesContract.Search._ID,
            RecipesContract.Search.COLUMN_TITLE,
            RecipesContract.Search.COLUMN_URL,
            RecipesContract.Search.COLUMN_RID,
            RecipesContract.Search.COLUMN_TEXT
    };
    // These indices are tied to RECIPE_COLUMNS.If RECIPE_COLUMNS changes, these must change.
    //public static final int COL_SEARCH_ID = 0;
    //public static final int COL_SEARCH_TITLE = 1;
    //public static final int COL_SEARCH_URL = 2;
    public static final int COL_SEARCH_RID = 3;
    //public static final int COL_SEARCH_TEXT = 4;

    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAdapter = new RecipesAdapter(getActivity(),null,0);
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mInput = arguments.getString(SEARCH_QUERY);
            mTwoPane = arguments.getBoolean(LIST_GRID);
        }
        if (!mTwoPane){
            mListView = (ListView) rootView.findViewById(R.id.search_list);
            mListView.setAdapter(mAdapter);
            mPosition = ListView.INVALID_POSITION;
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                    if (cursor != null) {
                        String rId = cursor.getString(COL_SEARCH_RID);
                        Utilities.updateRecipes(getActivity(), RecipesService.CALLER_SEARCH, mInput, rId);
                        ((Callback) getActivity()).onItemSelected(RecipesContract.Search.buildSearchUri(mInput,rId));
                    }
                    mPosition = position;
                }
            });
        } else {
            mGridView = (GridView) rootView.findViewById(R.id.search_grid);
            mGridView.setAdapter(mAdapter);
            mPosition = GridView.INVALID_POSITION;
            mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                    if (cursor != null) {
                        String rId = cursor.getString(COL_SEARCH_RID);
                        Utilities.updateRecipes(getActivity(), RecipesService.CALLER_SEARCH, mInput, rId);
                        ((Callback) getActivity()).onItemSelected(RecipesContract.Search.buildSearchUri(mInput, rId));
                    }
                    mPosition = position;
                }
            });
        }
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
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(SEARCH_LOADER, null, this);
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
        return new CursorLoader(
                getActivity(),
                RecipesContract.Search.buildSearchUri(mInput),
                SEARCH_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader,final Cursor data) {
        mAdapter.swapCursor(data);
        if(!Utilities.isNetworkStatusAvailable(getActivity())){
            pendingRefresh = true;
            pendingInput = mInput;
        }
        if (mPosition != ListView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            if (mTwoPane)
                mGridView.smoothScrollToPosition(mPosition);
            else
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
