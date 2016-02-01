package com.odessite.kos.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ImageView detailImage = (ImageView) findViewById(R.id.posterImageView);
        //TextView textView = (TextView) findViewById(R.id.detailsTextView);

        Intent detailIntent = getIntent();
        if (detailIntent != null && detailIntent.hasExtra(Intent.EXTRA_TEXT)){
            String posterUrlImage = detailIntent.getStringExtra(Intent.EXTRA_TEXT);
            Picasso.with(getApplicationContext()).load(posterUrlImage).into(detailImage);
        }
    }
}
