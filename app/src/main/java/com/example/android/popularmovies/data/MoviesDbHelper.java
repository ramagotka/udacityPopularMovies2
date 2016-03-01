package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by hania on 24.02.16.
 */
public class MoviesDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "movies.db";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + FavoriteMovieContract.MovieEntry.TABLE_NAME + " (" +
                FavoriteMovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY," +
                FavoriteMovieContract.MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT UNIQUE NOT NULL, " +
                FavoriteMovieContract.MovieEntry.COLUMN_OVERVIEW+ " TEXT NOT NULL, " +
                FavoriteMovieContract.MovieEntry.COLUMN_PICTURE_URL + " TEXT NOT NULL, " +
                FavoriteMovieContract.MovieEntry.COLUMN_POPULARITY + " TEXT NOT NULL " +
                FavoriteMovieContract.MovieEntry.COLUMN_RELEASE_DATE+ " TEXT NOT NULL " +
                FavoriteMovieContract.MovieEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL " +
                " );";

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoriteMovieContract.MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
