package com.odessite.kos.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageAdapter2 extends CursorAdapter {
    static final String BASE = "http://image.tmdb.org/t/p/w185/";
    String url;

    public ImageAdapter2(Context context, Cursor c, int flags) {
        super(context, c, flags);


    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new GridView.LayoutParams(285, 364));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setPadding(1, 1, 1, 1);
        bindView(imageView, context, cursor);
        return imageView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imagePoster = (ImageView) view;
        url = BASE + cursor.getString(PlaceholderFragment.COL_MOVIE_POSTER);
        Picasso.with(context).load(url).placeholder(R.drawable.sample).into(imagePoster);
    }
}
