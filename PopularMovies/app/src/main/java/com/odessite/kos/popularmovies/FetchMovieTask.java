package com.odessite.kos.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.odessite.kos.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class FetchMovieTask extends AsyncTask<String, Void, String[]> {
    static final String BASE = "http://image.tmdb.org/t/p/w185/";
    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private final Context mContext;
    private ImageAdapter imageAdapter;
    private List<String> urls = new ArrayList<String>();

    public FetchMovieTask(Context mContext, ImageAdapter imageAdapter) {
        this.mContext = mContext;
        this.imageAdapter = imageAdapter;
    }

    private String[] getMoviesFromJson(String moviesDbDiscover, int page) throws JSONException{
        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "results";
        final String OWM_POSTER = "poster_path";
        final String OWM_OVERVIEW = "overview";
        final String OWM_DATE = "release_date";
        final String OWM_ID = "id";
        final String OWM_TITLE = "title";
        final String OWM_POPULARITY = "popularity";
        final String OWM_VIDEO = "video";
        final String OWM_AVERAGE = "vote_average";
        final String OWM_PAGE = "page";

        try {
            JSONObject theMovieDbData = new JSONObject(moviesDbDiscover);
            JSONArray jsonArray = theMovieDbData.getJSONArray(OWM_LIST);
            Vector<ContentValues> cVector = new Vector<ContentValues>(jsonArray.length()+1);

            for (int i = 0; i < jsonArray.length(); i++) {
                String poster;
                String overview;
                String date;
                int id;
                String title;
                double popularity;
                String video;
                long average;

                JSONObject singleMovie = jsonArray.getJSONObject(i);
                poster = singleMovie.getString(OWM_POSTER);
                overview = singleMovie.getString(OWM_OVERVIEW);
                date = singleMovie.getString(OWM_DATE);
                id = singleMovie.getInt(OWM_ID);
                title = singleMovie.getString(OWM_TITLE);
                popularity = singleMovie.getDouble(OWM_POPULARITY);
                video = singleMovie.getString(OWM_VIDEO);
                average = singleMovie.getLong(OWM_AVERAGE);

                ContentValues movieValues = new ContentValues();
                movieValues.put(OWM_POSTER, poster);
                movieValues.put(OWM_OVERVIEW, overview);
                movieValues.put(OWM_DATE, date);
                movieValues.put(OWM_ID, id);
                movieValues.put(OWM_TITLE, title);
                movieValues.put(OWM_POPULARITY, popularity);
                movieValues.put(OWM_VIDEO, video);
                movieValues.put(OWM_AVERAGE, average);
                movieValues.put(OWM_PAGE, page);

                cVector.add(movieValues);
            }
            if (cVector.size() > 0){
                int deleteAll = mContext.getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI, null, null);
                // call bulkInsert to add movieEntries to database
                ContentValues[] movieValues = new ContentValues[cVector.size()];
                cVector.toArray(movieValues);
                mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, movieValues);
            }

            String sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
            Uri moviesForPageUri = MovieContract.MovieEntry.buildMoviesPage("1");
            Cursor cur = mContext.getContentResolver().query(moviesForPageUri, null,
            null, null, sortOrder);

            cVector = new Vector<ContentValues>(cur.getCount());
            if (cur.moveToFirst()){
                do {
                    ContentValues cv = new ContentValues();
                    DatabaseUtils.cursorRowToContentValues(cur, cv);
                    cVector.add(cv);
                }while (cur.moveToNext());
            }

            Log.d(LOG_TAG, "FetchWeatherTask Complete. " + cVector.size() + " Inserted");
            String[] resultStr = convertContentValuesToUXFormat(cVector);
            return resultStr;

        } catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private String[] convertContentValuesToUXFormat(Vector<ContentValues> cVector) {
        String[] resultStr = new String[cVector.size()];
        for (int i = 0; i < cVector.size(); i++) {
            ContentValues movieValues = cVector.elementAt(i);
            resultStr[i] = BASE + movieValues.getAsString(MovieContract.MovieEntry.COLUMN_POSTER);
        }
        return resultStr;
    }

    @Override
    protected String[] doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        int page = 1;

        String moviesDbDiscover = null;

        try {
            // Construct the URL for the themoviedb query
            final String BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
            final String PAGE_PARAM = "page";
            //final String SORT_PARAM = "sort_by";
            final String APPID_PARAM = "api_key";

            Uri buildUrl = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(PAGE_PARAM, Integer.toString(page))
                    .appendQueryParameter(APPID_PARAM, BuildConfig.THEMOVIEDB_API_KEY)
                    .build();

            URL url = new URL(buildUrl.toString());

            // create the request to themoviedb, and open connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder builder = new StringBuilder();
            if (inputStream == null){
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null){
                builder.append(line + "\n");
            }

            if (builder.length() == 0){
                return null;
            }

            moviesDbDiscover = builder.toString();

        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error URL ", e);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error Input ", e);
        } finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error closing stream ", e);
                }
            }
        }
        try {
            return getMoviesFromJson(moviesDbDiscover, page);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String[] results) {
        if (null != results){
            for (String moviePoster: results
                 ) {
                urls.add(moviePoster);
            }
            imageAdapter.setUrls(urls);
            imageAdapter.notifyDataSetInvalidated();
        }
    }
}
