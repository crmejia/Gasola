package com.crmejia.gasola.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by crismarmejia on 1/27/15.
 */
public class LogDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Log.db";

    private static final String TEXT_NOT_NULL = " TEXT NOT NULL ";
    private static final String INTEGER_NOT_NULL = " INTEGER NOT NULL ";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + LogContract.LogEntry.TABLE_NAME + " (" +
                    LogContract.LogEntry._ID + " INTEGER PRIMARY KEY," +
                    LogContract.LogEntry.COLUMN_START_DATE + TEXT_NOT_NULL + COMMA_SEP +
                    LogContract.LogEntry.COLUMN_END_DATE + TEXT_NOT_NULL + COMMA_SEP +
                    LogContract.LogEntry.COLUMN_START_DISTANCE + INTEGER_NOT_NULL + COMMA_SEP +
                    LogContract.LogEntry.COLUMN_END_DISTANCE + INTEGER_NOT_NULL + COMMA_SEP +
                    LogContract.LogEntry.COLUMN_GAS_AMOUNT + INTEGER_NOT_NULL +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + LogContract.LogEntry.TABLE_NAME;

    public LogDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);

    }
}
