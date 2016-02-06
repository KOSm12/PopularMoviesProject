package com.odessite.kos.popularmovies;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.odessite.kos.popularmovies.data.MovieContract;
import com.odessite.kos.popularmovies.data.MovieContract.MovieEntry;
import com.odessite.kos.popularmovies.services.MoviesService;

public class PlaceholderFragment extends Fragment implements LoaderCallbacks<Cursor> {
    public static final int MOVIE_LOADER_ID = 1;
    public static final String LOG_TAG = PlaceholderFragment.class.getSimpleName();
    private boolean clickSortButton = false;
    private GridView listMovies;
    private ImageAdapter2 imageAdapter;
    private String sortOrder;
    private String startLangSetting;

    private static final String[] MOVIE_COLUMNS = {
        MovieEntry.TABLE_NAME + "." +
        MovieEntry._ID,
        MovieEntry.COLUMN_POSTER,
        MovieEntry.COLUMN_OVERVIEW,
        MovieEntry.COLUMN_DATE,
        MovieEntry.COLUMN_ID,
        MovieEntry.COLUMN_POPULARITY,
        MovieEntry.COLUMN_TITLE,
        MovieEntry.COLUMN_VIDEO,
        MovieEntry.COLUMN_AVERAGE,
        MovieEntry.COLUMN_PAGE
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

    public PlaceholderFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        startLangSetting = Utility.getPreferredLang(getActivity());

        // Get movies from themoviedb.com
        updateMovies(startLangSetting);
        // Sort order: Desc, by average default
        sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
        //imageAdapter = new ImageAdapter(getActivity(), null);
        imageAdapter = new ImageAdapter2(getActivity(), null, 0);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        listMovies = (GridView) view.findViewById(R.id.gridView);
        listMovies.setAdapter(imageAdapter);
        listMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                if (null != cursor){
                    String page = cursor.getString(COL_MOVIE_PAGE);
                    int movieId = cursor.getInt(COL_MOVIE_ID);
                    Intent intent = new Intent(getActivity(), DetailActivity.class)
                            .setData(MovieContract.MovieEntry.buildMoviePageWithId(page, movieId));
                    startActivity(intent);
                }
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
            sortOrder = MovieContract.MovieEntry.COLUMN_AVERAGE + " DESC";
            getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
            return true;
        } else if (id == R.id.sort_by_popularity){
            clickSortButton = true;
            sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC";
            getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
            return true;
        } else if (id == R.id.setting_menu){
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
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

    private void updateMovies(String lang){
        /*FetchMovieTask movieTask = new FetchMovieTask(getActivity());
        String page = "1";
        movieTask.execute(page, lang);  // vote_average.desc  popularity.desc*/
        Intent serviceIntent = new Intent(getActivity(), MoviesService.class);
        serviceIntent.putExtra(MoviesService.LANGUAGE_QUERY_EXTRA, Utility.getPreferredLang(getActivity()));
        getActivity().startService(serviceIntent);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // Prepare the loader.  Either re-connect with an existing one,
        // or start a new one.
        getLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        String currentLang = Utility.getPreferredLang(getActivity());
        if (!currentLang.equals(startLangSetting)){
            updateMovies(currentLang);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = MovieContract.MovieEntry.buildMoviesPage("1");

        return new CursorLoader(getActivity(),
                uri,
                MOVIE_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        /*imageAdapter.setUrls(data);
        imageAdapter.notifyDataSetInvalidated();*/
        imageAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        imageAdapter.swapCursor(null);
    }
}
