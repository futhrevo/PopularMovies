package com.example.reku.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.reku.popularmovies.data.MovieContract.MovieEntry;

/**
 * Created by rakeshkalyankar on 11/02/16.
 */
public class MovieDbHelper extends SQLiteOpenHelper {
    private static final String TAG = MovieDbHelper.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    static final String DATABASE_NAME = "movies.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQl_CREATE_MOVIES_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieEntry.COLUMN_TITLE +  " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_USER_RATING + " REAL NOT NULL, " +
                MovieEntry.COLUMN_FAVORITE + "BOOLEAN DEFAULT FALSE, "+

                " UNIQUE (" + MovieEntry.COLUMN_TITLE + ", " + MovieEntry.COLUMN_RELEASE_DATE + ") ON CONFLICT REPLACE);" ;
        Log.i(TAG, SQl_CREATE_MOVIES_TABLE);
        db.execSQL(SQl_CREATE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //TODO: alter table to store user generated content
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
