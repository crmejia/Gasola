package com.crmejia.gasola;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.crmejia.gasola.data.LogContract;
import com.crmejia.gasola.data.LogDbHelper;

import java.util.Map;
import java.util.Set;

/**
 * Created by crismarmejia on 1/31/15.
 */
public class TestDb extends AndroidTestCase {
    public static String LOG_TAG = TestDb.class.getSimpleName();

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(LogDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new LogDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {

        LogDbHelper dbHelper = new LogDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // map of test values with column names as the keys
        ContentValues values = createFakeLogValues();

        long logRowId;
        logRowId = db.insert(LogContract.LogEntry.TABLE_NAME, null, values);

        // Verify we got a row back
        assertTrue(logRowId != -1);
        Log.d(LOG_TAG, "New row id: " + logRowId);

        Cursor cursor = db.query(
                LogContract.LogEntry.TABLE_NAME,  // Table to Query
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        validateCursor(cursor, values);

        dbHelper.close();
    }

    static ContentValues createFakeLogValues(){
        ContentValues fakeValues = new ContentValues();

        fakeValues.put(LogContract.LogEntry.COLUMN_START_DATE,"20150115");
        fakeValues.put(LogContract.LogEntry.COLUMN_END_DATE, "20150130");
        fakeValues.put(LogContract.LogEntry.COLUMN_START_DISTANCE, 45000);
        fakeValues.put(LogContract.LogEntry.COLUMN_END_DISTANCE, 45100);
        fakeValues.put(LogContract.LogEntry.COLUMN_GAS_AMOUNT, 10);

        return fakeValues;
    }

    static void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }
        valueCursor.close();
    }


}
