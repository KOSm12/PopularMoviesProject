package com.odessite.kos.popularmovies.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

public class TestUriMatcher extends AndroidTestCase {
    private static final String PAGE_QUERY = "1";
    private static final double TEST_POPULARITY = 13.263924;

    // content://com.odessite.kos.popularmovies.app/movies
    private static final Uri TEST_MOVIE_DIR = MovieContract.MovieEntry.CONTENT_URI;
    private static final Uri TEST_MOVIE_WITH_PAGE_DIR = MovieContract.MovieEntry.buildMoviesPage(PAGE_QUERY);

    public void testUriMatcher() {
        UriMatcher testMatcher = MoviesProvider.buildUriMatcher();

        assertEquals("Error: The MOVIES URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_DIR), MoviesProvider.MOVIES);
        assertEquals("Error: The MOVIES WITH PAGE URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_WITH_PAGE_DIR), MoviesProvider.MOVIES_WITH_PAGE);
    }

}
