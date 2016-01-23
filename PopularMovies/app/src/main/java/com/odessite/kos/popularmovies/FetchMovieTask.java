package com.odessite.kos.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

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

public class FetchMovieTask extends AsyncTask<String, Void, String[]> {
    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private final List<String> urls = new ArrayList<String>();
    static final String BASE = "http://image.tmdb.org/t/p/w185/";
    static final String EXT = ".jpg";

    public List<String> getUrls(){
        return urls;
    }

    private String[] getMoviesFromJson(String moviesDbDiscover) throws JSONException{
        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "results";
        final String OWM_POSTER = "poster_path";
        final String OWM_OVERVIEW = "overview";
        final String OWM_DATE = "release_date";
        final String OWM_TITLE = "title";
        final String OWM_AVERAGE = "vote_average";

        JSONObject theMovieDbData = new JSONObject(moviesDbDiscover);
        JSONArray jsonArray = theMovieDbData.getJSONArray(OWM_LIST);
        String[] resultStr = new String[jsonArray.length()];

        for (int i = 0; i < jsonArray.length(); i++) {
            String poster;
            String overview;
            String date;
            String title;
            String average;

            JSONObject singleMovie = jsonArray.getJSONObject(i);
            poster = singleMovie.getString(OWM_POSTER);
            overview = singleMovie.getString(OWM_OVERVIEW);
            date = singleMovie.getString(OWM_DATE);
            title = singleMovie.getString(OWM_TITLE);
            average = singleMovie.getString(OWM_AVERAGE);

            resultStr[i] = poster;
        }
        return resultStr;
    }

    @Override
    protected String[] doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String moviesDbDiscover = null;

        try {
            // Construct the URL for the themoviedb query
            final String BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
            final String SORT_PARAM = "sort_by";
            final String APPID_PARAM = "api_key";

            Uri buildUrl = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(SORT_PARAM, params[0])
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
            return getMoviesFromJson(moviesDbDiscover);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String[] results) {
        if (null == results){
            for (String result: results
                 ) {
                urls.add(BASE + result + EXT);
            }
        }
    }
}
