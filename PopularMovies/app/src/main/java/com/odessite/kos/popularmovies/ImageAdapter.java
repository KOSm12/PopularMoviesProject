package com.odessite.kos.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    static final String BASE = "http://image.tmdb.org/t/p/w185/";
    private List<String> urls = new ArrayList<String>();
    public static final String LOG_TAG = ImageAdapter.class.getSimpleName();
    private Cursor mCursor;

    public ImageAdapter(Context mContext, Cursor aCursor) {
        this.mContext = mContext;
        this.mCursor = aCursor;
        this.urls = convertCursorRowsToUXFormat(mCursor);
    }

    private List<String> convertCursorRowsToUXFormat(Cursor mCursor) {
        if (mCursor == null){
            urls.add(BASE);
        } else {
            if (mCursor.moveToFirst()) {
                do {
                    urls.add(BASE + mCursor.getString(PlaceholderFragment.COL_MOVIE_POSTER));
                } while (mCursor.moveToNext());
            }
        }
        return urls;
    }

    public void setUrls(Cursor data) {
        urls.clear();
        urls = convertCursorRowsToUXFormat(data);
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public Object getItem(int position) {
        return urls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null){
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(285, 364));
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            imageView.setPadding(1, 1, 1, 1);
        } else {
            imageView = (ImageView) convertView;
        }
        String url = (String) getItem(position);
        Picasso.with(mContext).load(url).placeholder(R.drawable.sample).into(imageView);
        return imageView;
    }
}
