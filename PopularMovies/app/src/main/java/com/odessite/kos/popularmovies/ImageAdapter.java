package com.odessite.kos.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends BaseAdapter{
    private Context mContext;
    private List<String> urls = new ArrayList<String>();
    public static final String LOG_TAG = ImageAdapter.class.getSimpleName();

    public ImageAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
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
