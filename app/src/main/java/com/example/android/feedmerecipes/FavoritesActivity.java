package com.example.android.feedmerecipes;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.feedmerecipes.extra.Utilities;

public class FavoritesActivity extends AppCompatActivity implements FavoritesFragment.Callback {

    private boolean mTwoPane;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        if(getSupportActionBar() != null)
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
            args.putInt(RecipeFragment.RECIPE_CALLER, 2);

            RecipeFragment fragment = new RecipeFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.recipeContainer, fragment)
                    .commit();
        } else {
            Intent intent = new Intent(this,RecipeActivity.class);
            intent.setData(recipeUri);
            intent.putExtra(RecipeFragment.RECIPE_CALLER,2);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_favorites, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_remove_favorites) {
            Utilities.removeFavorites(this);
            if (mTwoPane){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.recipeContainer, new RecipeFragment())
                        .commit();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
