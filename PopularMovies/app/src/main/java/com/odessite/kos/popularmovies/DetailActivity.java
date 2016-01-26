package com.odessite.kos.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ImageView detailImage = (ImageView) findViewById(R.id.posterImageView);
        TextView textView = (TextView) findViewById(R.id.detailsTextView);

        Intent detailIntent = getIntent();
        if (detailIntent != null && detailIntent.hasExtra(Intent.EXTRA_TEXT)){
            String movieData = detailIntent.getStringExtra(Intent.EXTRA_TEXT);
            String posterUrlImage = detailIntent.getStringExtra("Poster");
            Picasso.with(getApplicationContext()).load(posterUrlImage).into(detailImage);
            textView.setText(movieData);
        }

        if (savedInstanceState != null && savedInstanceState.containsKey(PlaceholderFragment.STATE_SORT_BY)){
            Log.v("DA", "Current state " + savedInstanceState.getString(PlaceholderFragment.STATE_SORT_BY));
        }
    }
}
