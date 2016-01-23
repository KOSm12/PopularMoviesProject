package com.odessite.kos.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends BaseAdapter{
    private Context mContext;
    private final List<String> urls;
    public static final String LOG_TAG = ImageAdapter.class.getSimpleName();



    public ImageAdapter(Context context, List<String> links) {
        mContext = context;
        urls = links;
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
            imageView.setLayoutParams(new GridView.LayoutParams(145, 145));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(2, 2, 2, 2);
        } else {
            imageView = (ImageView) convertView;
        }
        String url = (String) getItem(position);
        Log.v(LOG_TAG, "The image " + url);
        Picasso.with(mContext).load(url).into(imageView);
        return imageView;
    }

}
