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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.feedmerecipes.data.RecipesContract.Recipes;
import com.example.android.feedmerecipes.extra.Utilities;


/**
 * A placeholder fragment containing a simple view.
 */
public class RecipeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String RECIPE_URI = "URI";
    private Uri mUri;
    private static final int RECIPE_LOADER = 0;
    private static final String[] RECIPE_COLUMNS = {
            Recipes.TABLE_NAME + "." + Recipes._ID,
            Recipes.COLUMN_TITLE,
            Recipes.COLUMN_URL,
            Recipes.COLUMN_RID,
            Recipes.COLUMN_TEXT,
    };
    // These indices are tied to RECIPE_COLUMNS.If RECIPE_COLUMNS changes, these must change.
    //public static final int COL_RECIPE_ID = 0;
    public static final int COL_RECIPE_TITLE = 1;
    public static final int COL_RECIPE_URL = 2;
    //public static final int COL_RECIPE_RID = 3;
    public static final int COL_RECIPE_TEXT = 4;
    private TextView mTitleView;
    private ImageView mImageView;
    private TextView mTextView;

    public RecipeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(RECIPE_URI);
        }
        View rootview = inflater.inflate(R.layout.fragment_recipe, container, false);
        mTitleView = (TextView) rootview.findViewById(R.id.recipeTitle);
        mImageView = (ImageView) rootview.findViewById(R.id.recipeImage);
        mTextView = (TextView) rootview.findViewById(R.id.recipeText);
        return rootview;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(RECIPE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( null != mUri ) {
            // Return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    RECIPE_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            String title = data.getString(COL_RECIPE_TITLE);
            mTitleView.setText(title);
            String url = data.getString(COL_RECIPE_URL);
            Utilities.loadImage(getActivity(), url, mImageView);
            String text = data.getString(COL_RECIPE_TEXT);
            mTextView.setText(text);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
