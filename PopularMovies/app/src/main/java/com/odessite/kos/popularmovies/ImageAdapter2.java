package com.odessite.kos.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
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
        SquaredImageView imageView = new SquaredImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        bindView(imageView, context, cursor);
        return imageView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        SquaredImageView imagePoster = (SquaredImageView) view;
        url = BASE + cursor.getString(PlaceholderFragment.COL_MOVIE_POSTER);
        Picasso.with(context).load(url).error(R.drawable.sample).into(imagePoster);
    }
}
