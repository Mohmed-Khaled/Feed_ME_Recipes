package com.example.android.feedmerecipes.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.android.feedmerecipes.MainFragment;
import com.example.android.feedmerecipes.SearchFragment;
import com.example.android.feedmerecipes.extra.Utilities;

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Utilities.isNetworkStatusAvailable(context)){
            if (MainFragment.pendingRefresh)
                Utilities.updateRecipes(context,RecipesService.CALLER_RECIPE, null,null);
            if (SearchFragment.pendingRefresh && SearchFragment.pendingInput != null)
                Utilities.updateRecipes(context, RecipesService.CALLER_SEARCH, SearchFragment.pendingInput, null);

        }
    }
}
