package com.odessite.kos.popularmovies;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    //boolean clickSortButton = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, new PlaceholderFragment())
                    .commit();
        }
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sort_by_average){
            clickSortButton = true;
            invalidateOptionsMenu() ;
            updateMovies("vote_average.desc");
        } else if (id == R.id.sort_by_popularity){
            clickSortButton = true;
            invalidateOptionsMenu() ;
            updateMovies("popularity.desc");
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
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
        return super.onPrepareOptionsMenu(menu);
    }

    private void updateMovies(String sort){
        //FetchMovieTask movieTask = new FetchMovieTask();
        //movieTask.execute(sort);  // vote_average.desc  popularity.desc
    }*/
}
