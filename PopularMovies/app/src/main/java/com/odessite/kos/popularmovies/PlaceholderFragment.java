package com.odessite.kos.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class PlaceholderFragment extends Fragment {

    public PlaceholderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        GridView listMovies = (GridView) view.findViewById(R.id.gridView);
        listMovies.setAdapter(new ImageAdapter(getActivity()));

        FetchMovieTask movieTask = new FetchMovieTask();
        movieTask.execute("popularity.desc");  //vote_average

        return view;
    }

}
