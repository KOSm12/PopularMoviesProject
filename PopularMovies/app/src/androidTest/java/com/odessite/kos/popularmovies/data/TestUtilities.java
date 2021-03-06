package com.odessite.kos.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.odessite.kos.popularmovies.utils.PollingCheck;
import com.odessite.kos.popularmovies.data.MovieContract.MovieEntry;

import java.util.Map;
import java.util.Set;

public class TestUtilities extends AndroidTestCase {
    static final String TEST_STRING = "1";
    static final double TEST_POPULARITY = 88.551849;

    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        String strValueCurs;
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            // This if using for get double data from the DB
            if (columnName.equals(MovieEntry.COLUMN_POPULARITY)){
                double douValueCurs = valueCursor.getDouble(idx);
                strValueCurs = String.valueOf(douValueCurs);
            } else {strValueCurs = valueCursor.getString(idx);}
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, strValueCurs);
        }
    }

    static ContentValues createBlackMassMovieValues() {
        ContentValues movieValues = new ContentValues();

        movieValues.put(MovieEntry.COLUMN_POSTER, "/yIVnNriiyl522hk3LFLJrrMovhP.jpg");
        movieValues.put(MovieEntry.COLUMN_OVERVIEW, "The true story of Whitey Bulger");
        movieValues.put(MovieEntry.COLUMN_DATE, "2015-09-18");
        movieValues.put(MovieEntry.COLUMN_ID, 281957);
        movieValues.put(MovieEntry.COLUMN_POPULARITY, TEST_POPULARITY);
        movieValues.put(MovieEntry.COLUMN_TITLE, "Black Mass");
        movieValues.put(MovieEntry.COLUMN_VIDEO, "");
        movieValues.put(MovieEntry.COLUMN_AVERAGE, 5.98);
        movieValues.put(MovieEntry.COLUMN_PAGE, TEST_STRING);
        return movieValues;
    }

    static long insertBlackMassMovieValues(Context context){
        // insert test records into the database
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createBlackMassMovieValues();

        long movieRowId;
        movieRowId = db.insert(MovieEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back
        assertTrue("Error: Failure to insert North Pole Location Values", movieRowId != -1);

        return movieRowId;
    }

    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}
