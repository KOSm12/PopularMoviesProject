package com.odessite.kos.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.odessite.kos.popularmovies.data.MovieContract.MovieEntry;

public class MovieDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 2;
    static final String DATABASE_NAME = "themovie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME +
                " (" + MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieEntry.COLUMN_POSTER + " TEXT, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_DATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POPULARITY + " REAL, " +
                MovieEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_VIDEO + " TEXT, " +
                MovieEntry.COLUMN_AVERAGE + " REAL NOT NULL);";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
     }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
