package com.crmejia.gasola;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.crmejia.gasola.data.LogContract;

/**
 * Created by crismarmejia on 1/31/15.
 */
public class TestProvider extends AndroidTestCase {
    public static String LOG_TAG = TestProvider.class.getSimpleName();

    public void deleteAllRecords() {
        mContext.getContentResolver().delete(
                LogContract.LogEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                LogContract.LogEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();

    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    public void setUp() {
        deleteAllRecords();
    }

    public void testGetType() {
        // content://com.crmejia.gasola/log
        String type = mContext.getContentResolver().getType(LogContract.LogEntry.CONTENT_URI);
        assertEquals(LogContract.LogEntry.CONTENT_TYPE, type);

    }

//    @Test(expected=UnsupportedOperationException.class)
//    public void testWrongURI() throws UnsupportedOperationException{
//
//    }

    public void testInsertReadProvider() {

        ContentValues testValues = TestDb.createFakeLogValues();

        Uri locationUri = mContext.getContentResolver().insert(LogContract.LogEntry.CONTENT_URI, testValues);
        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);

        Cursor cursor = mContext.getContentResolver().query(
                LogContract.LogEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestDb.validateCursor(cursor, testValues);
    }

    public void testDeleteRecordsAtEnd() {
        deleteAllRecords();
    }

    public void insertFakeLogData() {
        ContentValues fakeLogValues = TestDb.createFakeLogValues();
        Uri logInsertUri = mContext.getContentResolver()
                .insert(LogContract.LogEntry.CONTENT_URI, fakeLogValues);
        assertTrue(logInsertUri != null);

//        locationRowId = ContentUris.parseId(logInsertUri);

    }

    public void testUpdateAndReadLog() {
        insertFakeLogData();
        int newEndDistance = 50000;

        // Make an update to one value.
        ContentValues fakeLogUpdate = new ContentValues();
        fakeLogUpdate.put(LogContract.LogEntry.COLUMN_END_DISTANCE, newEndDistance);

        mContext.getContentResolver().update(
                LogContract.LogEntry.CONTENT_URI, fakeLogUpdate, null, null);

        // A cursor is your primary interface to the query results.
        Cursor logCursor = mContext.getContentResolver().query(
                LogContract.LogEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make the same update to the full ContentValues for comparison.
        ContentValues logAltered = TestDb.createFakeLogValues();
        logAltered.put(LogContract.LogEntry.COLUMN_END_DISTANCE, newEndDistance);

        TestDb.validateCursor(logCursor, logAltered);
    }



}
