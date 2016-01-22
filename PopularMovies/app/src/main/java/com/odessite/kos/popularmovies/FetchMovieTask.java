package com.odessite.kos.popularmovies;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class FetchMovieTask extends AsyncTask<Void, Void, Void> {
    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    @Override
    protected Void doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String moviesDbDiscover = null;

        try {
            // Construct the URL for the themoviedb query
            String baseUrl = "https://api.themoviedb.org/3/discover/movie?";
            String apiKey = "api_key=" + BuildConfig.THEMOVIEDB_API_KEY;
            URL url = new URL(baseUrl.concat(apiKey));

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
            Log.d(LOG_TAG, moviesDbDiscover);

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
        return null;
    }
}
