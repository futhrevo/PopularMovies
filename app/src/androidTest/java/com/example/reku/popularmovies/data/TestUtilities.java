package com.example.reku.popularmovies.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/**
 * Created by rakeshkalyankar on 11/02/16.
 */
public class TestUtilities extends AndroidTestCase{

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    // values for testing movies table
    static ContentValues createTestMovieValues(){
        ContentValues testValues = new ContentValues();
        testValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Hello World");
        testValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "2016");
        testValues.put(MovieContract.MovieEntry.COLUMN_POSTER, "/test");
        testValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, "Hello world story");
        testValues.put(MovieContract.MovieEntry.COLUMN_USER_RATING, 9.1);
        testValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE,  true);
        return testValues;
    }

    // insert our test records to database
    static long insertTestMovieValues(Context context){
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createTestMovieValues();

        long movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Test Movie Values ", movieRowId != -1);

        return movieRowId;
    }
}
