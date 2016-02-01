package com.odessite.kos.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

public class MoviesProvider extends ContentProvider {
    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int MOVIES = 100;
    static final int MOVIES_WITH_PAGE = 101;
    static final int MOVIES_WITH_PAGE_AND_SORT_BY_POPULARITY = 102;
    static final int MOVIES_WITH_PAGE_AND_SORT_BY_AVERAGE = 103;
    static final int MOVIE_WITH_PAGE_AND_ID = 104;

    private static final SQLiteQueryBuilder sMoviesByPageQueryBuilder;

    static {
        sMoviesByPageQueryBuilder = new SQLiteQueryBuilder();

        sMoviesByPageQueryBuilder.setTables(
                MovieContract.MovieEntry.TABLE_NAME
        );
    }

    //page = ?
    private static final String sMoviePageSelection =
            MovieContract.MovieEntry.COLUMN_PAGE + " = ? ";

    //page = ? and sort_by_popularity <= ?
    public static final String sMoviePageAndSortPopularitySelection =
            MovieContract.MovieEntry.COLUMN_PAGE + " = ? AND " +
            MovieContract.MovieEntry.COLUMN_POPULARITY + " <= ? ";

    // page = ? and sort_by_average <= ?
    public static final String sMoviePageAndSortByAverageSelection =
            MovieContract.MovieEntry.COLUMN_PAGE + " = ? " +
            MovieContract.MovieEntry.COLUMN_AVERAGE + " <= ? ";

    public static final String sMoviePageAndIdSelection =
            MovieContract.MovieEntry.COLUMN_PAGE + " = ? AND " +
            MovieContract.MovieEntry.COLUMN_ID + " = ? ";

    private Cursor getMoviesByPagePopularity(Uri uri, String[] projection, String sortOrder){
        String moviesPage = MovieContract.MovieEntry.getMoviesPageFromUri(uri);
        String sortType = null;
        String selectionColumns = null;
        if (sortOrder.equals(MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC")){
            sortType = MovieContract.MovieEntry.getStartPopularityFromUri(uri);
            selectionColumns = sMoviePageAndSortPopularitySelection;
        } else if (sortOrder.equals(MovieContract.MovieEntry.COLUMN_AVERAGE + " DESC")) {
            sortType = MovieContract.MovieEntry.getStartAverageFromUri(uri);
            selectionColumns = sMoviePageAndSortByAverageSelection;
        }

        String[] selectionArg;
        String selection;
        if (sortType != null){
            selection = selectionColumns;
            selectionArg = new String[]{moviesPage, sortType};
        } else {
            selection = sMoviePageSelection;
            selectionArg = new String[]{moviesPage};
        }

        return sMoviesByPageQueryBuilder.query(mOpenHelper.getReadableDatabase(),
                projection,
                selection,
                selectionArg,
                null,
                null,
                sortOrder);
    }

    private Cursor getMovieByPageAndId(Uri uri, String[] projection, String sortOrder){
        String page = MovieContract.MovieEntry.getMoviesPageFromUri(uri);
        String movieId = MovieContract.MovieEntry.getMovieIdFromUri(uri);

        return sMoviesByPageQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                sMoviePageAndIdSelection,
                new String[] {page, movieId},
                null,
                null,
                sortOrder
        );
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)){
            // "movies/*"
            case MOVIES_WITH_PAGE:
                retCursor = getMoviesByPagePopularity(uri, projection, sortOrder);
                break;
            // "movie/*/#"
            case MOVIE_WITH_PAGE_AND_ID:
                retCursor = getMovieByPageAndId(uri, projection, sortOrder);
                break;
            // "movies"
            case MOVIES:
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match){
            case MOVIES_WITH_PAGE:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_WITH_PAGE_AND_ID:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;
            case MOVIES:
                return MovieContract.MovieEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match){
            case MOVIES:
                long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.MovieEntry.buildMoviesUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final  int match = sUriMatcher.match(uri);
        int rowsDeleted;
        if (null == selection) selection = "1";

        switch (match){
            case MOVIES:
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final  int match = sUriMatcher.match(uri);
        int rowsUpdated;
        switch (match){
            case MOVIES:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match){
            case MOVIES:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value: values){
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount ++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority,
                MovieContract.PATH_MOVIES, MOVIES);
        matcher.addURI(authority,
                MovieContract.PATH_MOVIES + "/*", MOVIES_WITH_PAGE);
        matcher.addURI(authority,
                MovieContract.PATH_MOVIES + "/*/*", MOVIE_WITH_PAGE_AND_ID);

        return matcher;
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
