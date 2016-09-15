package com.vamsi.popularmovies.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.widget.Toast;

import com.vamsi.popularmovies.Data.MoviesContract.FavoriteEntry;
import com.vamsi.popularmovies.R;

/**
 * Created by Vamsi Smart on 12-09-2016.
 */
public class FavoriteProvider extends ContentProvider {


    private SQLiteDatabase db;

    static final int FAVORITE = 1;
    static final int FAVORITE_ID = 2;

    static final UriMatcher uriMatcher;
    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, "favorite", FAVORITE);
        uriMatcher.addURI(MoviesContract.CONTENT_AUTHORITY, "favorite/*", FAVORITE_ID);
    }



    @Override
    public boolean onCreate() {
        Context context = getContext();
        FavoriteDbHelper dbHelper = new FavoriteDbHelper(context);

        db = dbHelper.getWritableDatabase();
        return (db == null)? false:true;
    }



    @Override
    public Uri insert(Uri uri, ContentValues values) {

        long rowID = db.insert(	MoviesContract.FavoriteEntry.TABLE_NAME, "", values);

        if (rowID > 0)
        {
            Uri _uri = ContentUris.withAppendedId(MoviesContract.FavoriteEntry.CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;

        } else {

            Toast.makeText(getContext(), getContext().getResources().getString(R.string.favorite_exists), Toast.LENGTH_SHORT).show();
            return null;

        }
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(MoviesContract.FavoriteEntry.TABLE_NAME);

        switch (uriMatcher.match(uri)) {

            case FAVORITE:
                break;

            case FAVORITE_ID:
                selection = FavoriteEntry.COLUMN_MOVIE_ID + "=" + uri.getPathSegments().get(1);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        //get favorite movies order by movie_id
        sortOrder = MoviesContract.FavoriteEntry.COLUMN_MOVIE_ID;

        Cursor c = qb.query(db,	projection,	selection, selectionArgs,null, null, sortOrder);

        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }



    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case FAVORITE:
                break;

            case FAVORITE_ID:

                selection = FavoriteEntry.COLUMN_MOVIE_ID +  " = " + uri.getPathSegments().get(1);

                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        count = db.delete(FavoriteEntry.TABLE_NAME, selection, selectionArgs);

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }



    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }



    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            /**
             * Get all list of favorite
             */
            case FAVORITE:
                return FavoriteEntry.CONTENT_TYPE;

            /**
             * Get a particular movie
             */
            case FAVORITE_ID:
                return FavoriteEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }


}
