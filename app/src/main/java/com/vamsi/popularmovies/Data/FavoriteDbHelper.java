package com.vamsi.popularmovies.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.vamsi.popularmovies.Data.MoviesContract.FavoriteEntry;
/**
 * Created by Vamsi Smart on 12-09-2016.
 */
public class FavoriteDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movies.db";

    public FavoriteDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_FAVORITE_TABLE = "CREATE TABLE " + FavoriteEntry.TABLE_NAME + " (" +
                FavoriteEntry._ID + " INTEGER PRIMARY KEY," +
                FavoriteEntry.COLUMN_MOVIE_ID + " TEXT UNIQUE NOT NULL, " +
                FavoriteEntry.COLUMN_BACKDROP_PATH + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_ORIGINAL_TITLE + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                FavoriteEntry.COLUMN_VOTE_AVERAGE + " REAL NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVORITE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoriteEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
