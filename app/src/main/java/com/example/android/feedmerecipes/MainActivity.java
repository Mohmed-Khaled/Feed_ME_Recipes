package com.example.android.feedmerecipes;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.feedmerecipes.extra.Utilities;

public class MainActivity extends AppCompatActivity implements MainFragment.Callback {

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!Utilities.isNetworkStatusAvailable(this)){
            Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
        }
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionBar)));
        if (findViewById(R.id.recipeContainer) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the recipe view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.recipeContainer, new RecipeFragment())
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public void onItemSelected(Uri recipeUri) {
        if (mTwoPane){
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(RecipeFragment.RECIPE_URI, recipeUri);
            args.putInt(RecipeFragment.RECIPE_CALLER, 0);

            RecipeFragment fragment = new RecipeFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipeContainer, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this,RecipeActivity.class);
            intent.setData(recipeUri);
            intent.putExtra(RecipeFragment.RECIPE_CALLER,0);
            startActivity(intent);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        if (null != searchView) {
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
/*                    Utilities.updateRecipes(getApplicationContext(),RecipesService.CALLER_SEARCH,query,null,null);
                    Intent intent = new Intent(getApplicationContext(),SearchActivity.class);
                    intent.putExtra(SearchFragment.SEARCH_URI,"query");*/
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case R.id.action_settings:
                return true;
            case R.id.action_search:
                return true;
            case R.id.action_favorites:
                startActivity(new Intent(this,FavoritesActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
