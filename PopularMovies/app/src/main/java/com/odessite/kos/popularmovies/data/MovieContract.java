package com.odessite.kos.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import com.odessite.kos.popularmovies.R;

public class MovieContract {
    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.odessite.kos.popularmovies.app";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    // For instance, content://com.odessite.kos.popularmovies.app/movies/ is a valid path for
    // looking at movies data.
    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public static final String TABLE_NAME = "movies";

        public static final String COLUMN_POSTER = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_DATE = "release_date";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_VIDEO = "video";
        public static final String COLUMN_AVERAGE = "vote_average";
        public static final String COLUMN_PAGE = "page";

        public static Uri buildMoviesUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMoviesPage(String page) {
            return CONTENT_URI.buildUpon().appendPath(page).build();
        }

        public static Uri buildMoviesPageWithStartSort(String page, String sort){
            if (sort.equals(R.string.sort_by_popularity)){
                return CONTENT_URI.buildUpon().appendPath(page).appendQueryParameter(COLUMN_POPULARITY, sort).build();
            } else  if (sort.equals(R.string.sort_by_average)){
                return CONTENT_URI.buildUpon().appendPath(page).appendQueryParameter(COLUMN_AVERAGE, sort).build();
            }
            return null;
        }

        public static Uri buildMoviesPageWithStartAverage(String page, String sort){
            return CONTENT_URI.buildUpon().appendPath(page).appendQueryParameter(COLUMN_AVERAGE, sort).build();
        }

        public static Uri buildMoviePageWithId(String page, int id){
            return CONTENT_URI.buildUpon().appendPath(page).appendPath(Integer.toString(id)).build();
        }

        public static String getMovieIdFromUri(Uri uri){
            return uri.getPathSegments().get(4);
        }

        public static String getMoviesPageFromUri(Uri uri){
            int number = uri.getPathSegments().size();
            return uri.getPathSegments().get(number - 1);
        }

        public static String getStartPopularityFromUri(Uri uri){
            String popularityString = uri.getQueryParameter(COLUMN_POPULARITY);
            if (null != popularityString && popularityString.length() > 0){
                return popularityString;
            } else {
                return null;
            }
        }

        public static String getStartAverageFromUri(Uri uri){
            String averageString = uri.getQueryParameter(COLUMN_AVERAGE);
            if (null != averageString && averageString.length() > 0){
                return averageString;
            } else {
                return null;
            }
        }
    }
}
