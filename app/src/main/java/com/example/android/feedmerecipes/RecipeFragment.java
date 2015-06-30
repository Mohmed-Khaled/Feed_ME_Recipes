package com.example.android.feedmerecipes;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.feedmerecipes.data.RecipesContract.Favorites;
import com.example.android.feedmerecipes.data.RecipesContract.Recipes;
import com.example.android.feedmerecipes.data.RecipesContract.Search;
import com.example.android.feedmerecipes.extra.Utilities;


/**
 * A placeholder fragment containing a simple view.
 */
public class RecipeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    static final String RECIPE_URI = "URI";
    static final String RECIPE_CALLER = "CALLER";
    private static final String RECIPE_SHARE_HASHTAG = " #FeedMERecipes";
    private ShareActionProvider mShareActionProvider;

    private Uri mUri;
    private int mCaller;
    private static final int RECIPE_LOADER = 0;
    private static final String[] RECIPE_COLUMNS = {
            Recipes.TABLE_NAME + "." + Recipes._ID,
            Recipes.COLUMN_TITLE,
            Recipes.COLUMN_URL,
            Recipes.COLUMN_RID,
            Recipes.COLUMN_TEXT
    };
    private static final String[] FAVORITES_COLUMNS = {
            Favorites.TABLE_NAME + "." + Favorites._ID,
            Favorites.COLUMN_TITLE,
            Favorites.COLUMN_URL,
            Favorites.COLUMN_RID,
            Favorites.COLUMN_TEXT
    };
    private static final String[] SEARCH_COLUMNS = {
            Search.TABLE_NAME + "." + Search._ID,
            Search.COLUMN_TITLE,
            Search.COLUMN_URL,
            Search.COLUMN_RID,
            Search.COLUMN_TEXT
    };
    // These indices are tied to RECIPE_COLUMNS.If RECIPE_COLUMNS changes, these must change.
    //public static final int COL_RECIPE_ID = 0;
    public static final int COL_RECIPE_TITLE = 1;
    public static final int COL_RECIPE_URL = 2;
    public static final int COL_RECIPE_RID = 3;
    public static final int COL_RECIPE_TEXT = 4;
    private TextView mTitleView;
    private ImageView mImageView;
    private TextView mTextView;
    private Button mFavButton;

    public RecipeFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (!Utilities.isNetworkStatusAvailable(getActivity())){
            Toast.makeText(getActivity()
                    , "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(RECIPE_URI);
            mCaller = arguments.getInt(RECIPE_CALLER);
        }
        View rootview = inflater.inflate(R.layout.fragment_recipe, container, false);
        mTitleView = (TextView) rootview.findViewById(R.id.recipeTitle);
        mImageView = (ImageView) rootview.findViewById(R.id.recipeImage);
        mTextView = (TextView) rootview.findViewById(R.id.recipeText);
        mFavButton = (Button) rootview.findViewById(R.id.recipeFavorite);
        mFavButton.setClickable(false);
        return rootview;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_recipe_fragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

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
            String[] projection;
            switch (mCaller){
                case 0:
                    projection = RECIPE_COLUMNS;
                    break;
                case 1:
                    projection = SEARCH_COLUMNS;
                    break;
                case 2:
                    projection = FAVORITES_COLUMNS;
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown caller: " + mCaller);
            }
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    projection,
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
            final String title = data.getString(COL_RECIPE_TITLE);
            final String url = data.getString(COL_RECIPE_URL);
            final String rId = data.getString(COL_RECIPE_RID);
            final String text = data.getString(COL_RECIPE_TEXT);
            mTitleView.setText(title);
            Utilities.loadImage(getActivity(), url, mImageView);
            mTextView.setText(text);
            if (mCaller != 2) {
                mFavButton.setText("Add To Favorites");
                mFavButton.setClickable(true);
                mFavButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utilities.addToFavorites(
                                getActivity(),
                                title,
                                url,
                                rId,
                                text
                        );
                        Toast.makeText(getActivity(), "Added To Favorites", Toast.LENGTH_LONG).show();
                    }
                });
            } else {
                mFavButton.setText("Remove From Favorites");
                mFavButton.setClickable(true);
                mFavButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utilities.removeFromFavorites(getActivity(),Favorites.buildFavoritesUri(rId));
                        Toast.makeText(getActivity(), "Removed From Favorites", Toast.LENGTH_LONG).show();
                    }
                });
            }
            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareRecipeIntent(title,text));
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private Intent createShareRecipeIntent(String title,String text) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareRecipe = "Recipe for " + title + ": \n";
        shareRecipe += text + "\n" + RECIPE_SHARE_HASHTAG + "\n";
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareRecipe);
        return shareIntent;
    }
}
