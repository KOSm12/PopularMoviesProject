package com.odessite.kos.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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

public class PlaceholderFragment extends Fragment {
    public static final String LOG_TAG = PlaceholderFragment.class.getSimpleName();
    public List<String> urls = new ArrayList<String>();
    static final String STATE_SORT_BY = "sort_by";
    GridView listMovies;
    boolean clickSortButton = false;
    String startSortLoad;
    List<String> fullData = new ArrayList<String>();

    public PlaceholderFragment() {
        setHasOptionsMenu(true);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_SORT_BY)){
            Log.v(LOG_TAG, "Init star log " + savedInstanceState.getString(STATE_SORT_BY));
            startSortLoad = savedInstanceState.getString(STATE_SORT_BY);
        } else {
            startSortLoad = "popularity.desc";
        }
        updateMovies(startSortLoad);
        listMovies = (GridView) view.findViewById(R.id.gridView);
        final ImageAdapter imageAdapter = new ImageAdapter(getActivity(), urls);
        listMovies.setAdapter(imageAdapter);
        listMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String posterDetails = (String) imageAdapter.getItem(position);
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class)
                    .putExtra(Intent.EXTRA_TEXT, fullData.get(position))
                    .putExtra("Poster", posterDetails);
                startActivity(detailIntent);
            }
        });

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sort_by_average){
            clickSortButton = true;
            updateMovies("vote_average.desc");
        } else if (id == R.id.sort_by_popularity){
            clickSortButton = true;
            updateMovies("popularity.desc");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (clickSortButton) {
            if (menu.findItem(R.id.sort_by_average).isVisible()) {
                menu.findItem(R.id.sort_by_average).setVisible(false);
                menu.findItem(R.id.sort_by_popularity).setVisible(true);
                clickSortButton = false;
            } else if (menu.findItem(R.id.sort_by_popularity).isVisible()) {
                menu.findItem(R.id.sort_by_popularity).setVisible(false);
                menu.findItem(R.id.sort_by_average).setVisible(true);
                clickSortButton = false;
            }
        }
    }

    private void updateMovies(String sort){
        FetchMovieTask movieTask = new FetchMovieTask();
        movieTask.execute(sort);  // vote_average.desc  popularity.desc
        startSortLoad = sort;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.v(LOG_TAG, startSortLoad);
        outState.putString(STATE_SORT_BY, startSortLoad);
        super.onSaveInstanceState(outState);
    }

    public class FetchMovieTask extends AsyncTask<String, Void, String[][]> {
        static final String BASE = "http://image.tmdb.org/t/p/w185/";

        private String[][] getMoviesFromJson(String moviesDbDiscover) throws JSONException {
            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "results";
            final String OWM_POSTER = "poster_path";
            final String OWM_OVERVIEW = "overview";
            final String OWM_DATE = "release_date";
            final String OWM_TITLE = "title";
            final String OWM_AVERAGE = "vote_average";

            JSONObject theMovieDbData = new JSONObject(moviesDbDiscover);
            JSONArray jsonArray = theMovieDbData.getJSONArray(OWM_LIST);
            String[][] resultStr = new String[jsonArray.length()][5];

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
                String[] movieAllData = {BASE + poster, overview, date, title, average};
                resultStr[i] = movieAllData;
            }
            return resultStr;
        }

        @Override
        protected String[][] doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String moviesDbDiscover = null;

            try {
                // Construct the URL for the themoviedb query
                final String BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
                final String PAGE_PARAM = "page";
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
        protected void onPostExecute(String[][] results) {
            if (null != results){
                urls.clear();
                for (int i = 0; i < results.length; i++) {
                    urls.add(results[i][0]);
                    fullData.add("Title: " + results[i][3] + "\n" +
                                "Date: " + results[i][2] + "\n" +
                                "Overview: " + results[i][1] + "\n" +
                                "Average: " + results[i][4]);
                }
                listMovies.setAdapter(new ImageAdapter(getActivity(), urls));
            }
        }
    }

}
