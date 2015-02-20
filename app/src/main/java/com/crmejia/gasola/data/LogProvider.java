package com.crmejia.gasola.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by crismarmejia on 1/31/15.
 */
public class LogProvider extends ContentProvider {
    public static final String _ID_SELECTION = " _ID = ? ";
    private static final int LOG = 100;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private LogDbHelper mOpenHelper;

    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = LogContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, LogContract.PATH_LOG, LOG);

        return matcher;
    }
    @Override
    public boolean onCreate() {
        mOpenHelper = new LogDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        switch (sUriMatcher.match(uri)){
            case LOG:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        LogContract.LogEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case LOG:
                return LogContract.LogEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        if(match == LOG){
            long _id = db.insert(LogContract.LogEntry.TABLE_NAME,null, contentValues);
            if(_id > 0)
                returnUri = LogContract.LogEntry.buildLogUri(_id);
            else
                throw new android.database.SQLException("Failed to insert row into " + uri);
        } else
            throw new UnsupportedOperationException("Unknown uri: " + uri);

        getContext().getContentResolver().notifyChange(uri,null);
       return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        if(match == LOG) {
            rowsDeleted = db.delete(
                    LogContract.LogEntry.TABLE_NAME,selection,selectionArgs);

        } else
            throw new UnsupportedOperationException("Unknown uri: " + uri);

        // Because a null deletes all rows
        if (selection == null || rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        if(match == LOG) {
            rowsUpdated = db.update(
                    LogContract.LogEntry.TABLE_NAME, values, selection,selectionArgs);

        } else
            throw new UnsupportedOperationException("Unknown uri: " + uri);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
