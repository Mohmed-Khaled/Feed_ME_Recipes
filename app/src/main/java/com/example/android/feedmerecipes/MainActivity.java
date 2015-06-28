package com.example.android.feedmerecipes;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.android.feedmerecipes.extra.Utilities;
import com.example.android.feedmerecipes.sync.RecipesSyncAdapter;

public class MainActivity extends AppCompatActivity implements MainFragment.Callback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!Utilities.isNetworkStatusAvailable(this)){
            Toast.makeText(this,"No Internet Connection",Toast.LENGTH_SHORT).show();
            RecipesSyncAdapter.initializeSyncAdapter(this);
        }
    }

    @Override
    public void onItemSelected(Uri recipeUri) {
        Intent intent = new Intent(this,RecipeActivity.class);
        intent.setData(recipeUri);
        startActivity(intent);
    }


/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/
}
