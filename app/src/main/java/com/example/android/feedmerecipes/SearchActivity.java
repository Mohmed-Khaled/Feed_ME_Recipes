package com.example.android.feedmerecipes;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.android.feedmerecipes.extra.Utilities;

public class SearchActivity extends AppCompatActivity implements SearchFragment.Callback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        if(getSupportActionBar() != null)
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.actionBar)));
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.

            Bundle arguments = new Bundle();
            arguments.putString(SearchFragment.SEARCH_QUERY, getIntent().getStringExtra(SearchFragment.SEARCH_QUERY));
            arguments.putBoolean(SearchFragment.LIST_GRID,getIntent().getBooleanExtra(SearchFragment.LIST_GRID,false));

            SearchFragment fragment = new SearchFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.searchContainer, fragment)
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favorites) {
            startActivity(new Intent(this,FavoritesActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri recipeUri) {
        if (Utilities.isNetworkStatusAvailable(this) || Utilities.checkData(this,recipeUri)){
            Intent intent = new Intent(this,RecipeActivity.class);
            intent.setData(recipeUri);
            intent.putExtra(RecipeFragment.RECIPE_CALLER,1);
            intent.putExtra(SearchFragment.LIST_GRID,getIntent().getBooleanExtra(SearchFragment.LIST_GRID,false));
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }
}
