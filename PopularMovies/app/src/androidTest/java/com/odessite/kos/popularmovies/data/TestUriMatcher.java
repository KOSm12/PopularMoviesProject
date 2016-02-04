package com.odessite.kos.popularmovies.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

public class TestUriMatcher extends AndroidTestCase {
    private static final String PAGE_QUERY = "1";
    private static final int TEST_MOVIE_ID = 281957;

    // content://com.odessite.kos.popularmovies.app/movies
    private static final Uri TEST_MOVIE_DIR = MovieContract.MovieEntry.CONTENT_URI;
    private static final Uri TEST_MOVIE_WITH_PAGE_DIR = MovieContract.MovieEntry.buildMoviesPage(PAGE_QUERY);
    private static final Uri TEST_MOVIE_WITH_PAGE_AND_ID_ITEM = MovieContract.MovieEntry.buildMoviePageWithId(PAGE_QUERY, TEST_MOVIE_ID);

    public void testUriMatcher() {
        UriMatcher testMatcher = MoviesProvider.buildUriMatcher();

        assertEquals("Error: The MOVIES URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_DIR), MoviesProvider.MOVIES);
        assertEquals("Error: The MOVIES WITH PAGE URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_WITH_PAGE_DIR), MoviesProvider.MOVIES_WITH_PAGE);
        assertEquals("Error: The MOVIES WITH PAGE AND ID URI was matched incorrectly.",
                testMatcher.match(TEST_MOVIE_WITH_PAGE_AND_ID_ITEM), MoviesProvider.MOVIE_WITH_PAGE_AND_ID);
    }

}
