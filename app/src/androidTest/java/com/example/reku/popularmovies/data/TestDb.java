package com.example.reku.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

/**
 * Created by rakeshkalyankar on 11/02/16.
 */
public class TestDb extends AndroidTestCase {
    public static final String TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    @Override
    protected void setUp() throws Exception {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable{
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);

        deleteTheDatabase();
        SQLiteDatabase db = new MovieDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());
        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain all tables
        assertTrue("Error: Your database was created without all tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> movieColoumnHashSet = new HashSet<>();
        movieColoumnHashSet.add(MovieContract.MovieEntry._ID);
        movieColoumnHashSet.add(MovieContract.MovieEntry.COLUMN_TITLE);
        movieColoumnHashSet.add(MovieContract.MovieEntry.COLUMN_POSTER);
        movieColoumnHashSet.add(MovieContract.MovieEntry.COLUMN_USER_RATING);
        movieColoumnHashSet.add(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        movieColoumnHashSet.add(MovieContract.MovieEntry.COLUMN_SYNOPSIS);
        movieColoumnHashSet.add(MovieContract.MovieEntry.COLUMN_FAVORITE);

        int columnNameIndex = c.getColumnIndex("name");
        do{
            String columnName = c.getString(columnNameIndex);
            movieColoumnHashSet.remove(columnName);
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required movie
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required movie entry columns",
                movieColoumnHashSet.isEmpty());
        db.close();
    }

    //test to check movie values insert into db
    public void testMovieTable(){
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create ContentValues  to insert
        ContentValues testValues = TestUtilities.createTestMovieValues();

        // Insert ContentValues into database and get a row ID back
        long movieRowId = TestUtilities.insertTestMovieValues(mContext);

        // Query the database and receive a Cursor back
        Cursor c = db.query(MovieContract.MovieEntry.TABLE_NAME,null,null,null,null,null,null,null);

        // Move the cursor to a valid database row
        assertTrue("Error no records returned from Movie query", c.moveToFirst());

        // Validate data in resulting Cursor with the original ContentValues
        TestUtilities.validateCurrentRecord("Error Movie query validation failed", c, testValues);

        assertFalse("Error: more than one record returned from Movie query", c.moveToNext());
        // Finally, close the cursor and database
        c.close();
        db.close();
    }


    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
