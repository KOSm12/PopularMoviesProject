package com.odessite.kos.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();

    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);

        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MovieDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the location entry and weather entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> movieColumnsHasSet = new HashSet<String>();
        movieColumnsHasSet.add(MovieContract.MovieEntry._ID);
        movieColumnsHasSet.add(MovieContract.MovieEntry.COLUMN_POSTER);
        movieColumnsHasSet.add(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        movieColumnsHasSet.add(MovieContract.MovieEntry.COLUMN_DATE);
        movieColumnsHasSet.add(MovieContract.MovieEntry.COLUMN_POPULARITY);
        movieColumnsHasSet.add(MovieContract.MovieEntry.COLUMN_TITLE);
        movieColumnsHasSet.add(MovieContract.MovieEntry.COLUMN_VIDEO);
        movieColumnsHasSet.add(MovieContract.MovieEntry.COLUMN_AVERAGE);

        int columnNameIndex = c.getColumnIndex("name");
        do{
            String columnName = c.getString(columnNameIndex);
            movieColumnsHasSet.remove(columnName);
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required movie entry columns",
                movieColumnsHasSet.isEmpty());
        c.close();
        db.close();
    }

    public void testMovieTable(){
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = TestUtilities.createBlackMassMovieValues();
        long movieRowId;
        movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME,
                null, testValues);

        // verify we got a row back
        assertTrue("Error: Failure to insert Black Mass Values", movieRowId != -1);

        Cursor c = db.query(MovieContract.MovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query

        assertTrue("Error: No Records returned from movie query", c.moveToFirst());

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)

        TestUtilities.validateCurrentRecord("Error: Movie Query Validation Failed",
                c, testValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse("Error: More than one record returned from location query",
                c.moveToNext());

        // close Cursor and Database
        c.close();
        db.close();
    }
}
