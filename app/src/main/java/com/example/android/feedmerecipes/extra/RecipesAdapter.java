package com.example.android.feedmerecipes.extra;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.feedmerecipes.R;
import com.example.android.feedmerecipes.data.RecipesContract;

public class RecipesAdapter extends CursorAdapter {

    private Context mContext;

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView imageView;
        public final TextView titleView;

        public ViewHolder(View view) {
            imageView = (ImageView) view.findViewById(R.id.imageView);
            titleView = (TextView) view.findViewById(R.id.textView);
        }
    }


    public RecipesAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
        int layoutId = R.layout.recipe_item;
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        int titleIndex = cursor.getColumnIndex(RecipesContract.Recipes.COLUMN_TITLE);
        String title = cursor.getString(titleIndex);
        int urlIndex = cursor.getColumnIndex(RecipesContract.Recipes.COLUMN_URL);
        String url = cursor.getString(urlIndex);
        viewHolder.titleView.setText(title);
        Utilities.loadImage(mContext,url,viewHolder.imageView);
    }

}
