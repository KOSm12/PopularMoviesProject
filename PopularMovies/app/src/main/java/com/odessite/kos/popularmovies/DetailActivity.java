package com.odessite.kos.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.odessite.kos.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {
    public static final int MOVIE_DETAIL_LOADER_ID = 1;
    public static final String LOG_TAG = DetailActivity.class.getSimpleName();
    ImageView detailImage;
    TextView title;
    TextView releaseDate;
    TextView average;
    TextView desc;

    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." +
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_POSTER,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_DATE,
            MovieContract.MovieEntry.COLUMN_ID,
            MovieContract.MovieEntry.COLUMN_POPULARITY,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_VIDEO,
            MovieContract.MovieEntry.COLUMN_AVERAGE,
            MovieContract.MovieEntry.COLUMN_PAGE
    };

    static final int COL_MOVIE_ID_DATA = 0;
    static final int COL_MOVIE_POSTER = 1;
    static final int COL_MOVIE_OVERVIEW = 2;
    static final int COL_MOVIE_DATE = 3;
    static final int COL_MOVIE_ID = 4;
    static final int COL_MOVIE_POPULARITY = 5;
    static final int COL_MOVIE_TITLE = 6;
    static final int COL_MOVIE_VIDEO = 7;
    static final int COL_MOVIE_AVERAGE = 8;
    static final int COL_MOVIE_PAGE = 9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getSupportLoaderManager().initLoader(MOVIE_DETAIL_LOADER_ID, null, this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        detailImage = (ImageView) findViewById(R.id.posterImageView);
        title = (TextView) findViewById(R.id.detailsTextView);
        releaseDate = (TextView) findViewById(R.id.release_date_tv);
        average = (TextView) findViewById(R.id.average_tv);
        desc =(TextView) findViewById(R.id.description_tv);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getIntent();
        if (null == intent){return null;}

        return new CursorLoader(
                getApplicationContext(),
                intent.getData(),
                MOVIE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()){
            String poster = ImageAdapter.BASE + data.getString(COL_MOVIE_POSTER);
            Picasso.with(getApplication()).load(poster).placeholder(R.drawable.sample).into(detailImage);
            title.setText(data.getString(COL_MOVIE_TITLE));
            releaseDate.setText(data.getString(COL_MOVIE_DATE));
            String averageFormat = String.format("%s/10", data.getString(COL_MOVIE_AVERAGE));
            average.setText(averageFormat);
            desc.setText(data.getString(COL_MOVIE_OVERVIEW));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
