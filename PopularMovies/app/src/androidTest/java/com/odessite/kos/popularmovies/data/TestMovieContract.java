package com.odessite.kos.popularmovies.data;

import android.net.Uri;
import android.test.AndroidTestCase;

public class TestMovieContract extends AndroidTestCase {
    public static final String TEST_MOVIE_PAGE = "6";
    public static final String TEST_MOVIE_POPULARITY = "popularity";

    public void testBuildMoviePage(){
        Uri movieUri = MovieContract.MovieEntry.buildMoviesPage(TEST_MOVIE_PAGE);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildMoviePage in " +
                        "MovieContract.", movieUri);
        assertEquals("Error: Movie Page not properly appended to the end of the Uri",
                TEST_MOVIE_PAGE, movieUri.getLastPathSegment());
        assertEquals("Error: Weather location Uri doesn't match our expected result",
                movieUri.toString(),
                "content://com.odessite.kos.popularmovies.app/movies/6");
    }
}
