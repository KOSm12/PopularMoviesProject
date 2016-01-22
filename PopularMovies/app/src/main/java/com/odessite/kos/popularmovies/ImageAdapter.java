package com.odessite.kos.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.squareup.picasso.Picasso;

public class ImageAdapter extends BaseAdapter{
    private Context mContext;

    public ImageAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SquaredImageView picassoImage = (SquaredImageView) convertView;

        if (picassoImage == null){
            picassoImage = new SquaredImageView(mContext);
        }
        String url = (String) getItem(position);
        Picasso.with(mContext).load(url).into(picassoImage);
        return null;
    }
}
