package com.crmejia.gasola.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by crismarmejia on 1/27/15.
 */
public final class LogContract {

    public static final String CONTENT_AUTHORITY = "com.crmejia.gasola";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_LOG = "log";

    //as seen in :http://developer.android.com/training/basics/data-storage/databases.html
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public LogContract(){}
    public static final String DATE_FORMAT = "yyyyMMdd";

    public static String getDbDateString(Date date){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    /**
     * Converts a dateText to a long Unix time representation
     * @param dateText the input date string
     * @return the Date object
     */
    public static Date getDateFromDb(String dateText) {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            return dbDateFormat.parse(dateText);
        } catch ( ParseException e ) {
            e.printStackTrace();
            return null;
        }
    }


    public static final class LogEntry implements BaseColumns{

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_LOG).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_LOG;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_LOG;

        public static final String TABLE_NAME = "log";
        public static final String COLUMN_START_DATE = "startdate";
        public static final String COLUMN_END_DATE = "enddate";
        public static final String COLUMN_START_DISTANCE = "startdistance";
        public static final String COLUMN_END_DISTANCE = "enddistance";
        public static final String COLUMN_GAS_AMOUNT = "gasamount";

        public static final String DATE_FORMAT = "yyyyMMdd";
        public static String getDbDateString(Date date){
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            return sdf.format(date);
        }

        public static Uri buildLogUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI,id);
        }

    }
}
