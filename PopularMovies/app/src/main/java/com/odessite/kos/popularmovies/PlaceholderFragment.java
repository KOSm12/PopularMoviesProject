package com.odessite.kos.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

public class PlaceholderFragment extends Fragment {
    public static final String LOG_TAG = PlaceholderFragment.class.getSimpleName();
    GridView listMovies;
    boolean clickSortButton = false;
    private ImageAdapter imageAdapter;

    public PlaceholderFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        imageAdapter = new ImageAdapter(getActivity());
        updateMovies("");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        listMovies = (GridView) view.findViewById(R.id.gridView);
        listMovies.setAdapter(imageAdapter);
        listMovies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String posterDetails = (String) imageAdapter.getItem(position);
                Intent detailIntent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, posterDetails);
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
        FetchMovieTask movieTask = new FetchMovieTask(getActivity(), imageAdapter);
        movieTask.execute(getString(R.string.sort_by_popularity));  // vote_average.desc  popularity.desc
    }
}
