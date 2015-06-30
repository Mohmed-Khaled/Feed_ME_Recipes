package com.example.android.feedmerecipes;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

public class RecipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        if(getSupportActionBar() != null)
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionBar)));
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            Bundle arguments = new Bundle();
            arguments.putParcelable(RecipeFragment.RECIPE_URI, getIntent().getData());
            arguments.putInt(RecipeFragment.RECIPE_CALLER, getIntent().getIntExtra(RecipeFragment.RECIPE_CALLER,-1));

            RecipeFragment fragment = new RecipeFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipeContainer, fragment)
                    .commit();
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recipe, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                switch (getIntent().getIntExtra(RecipeFragment.RECIPE_CALLER, -1)) {
                    case 0:
                        startActivity(new Intent(this, MainActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(this, SearchActivity.class)
                                .putExtra(SearchFragment.SEARCH_QUERY, getIntent().getData().getPathSegments().get(1))
                                .putExtra(SearchFragment.LIST_GRID, getIntent().getBooleanExtra(SearchFragment.LIST_GRID,false)));
                        break;
                    case 2:
                        startActivity(new Intent(this, FavoritesActivity.class));
                        break;
                    default:
                        throw new UnsupportedOperationException("Unknown caller activity");
                }
                return true;
            case R.id.action_favorites:
                startActivity(new Intent(this, FavoritesActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
