package com.crmejia.gasola.data;

import android.provider.BaseColumns;

/**
 * Created by crismarmejia on 1/27/15.
 */
public final class LogContract {
    //as seen in :http://developer.android.com/training/basics/data-storage/databases.html
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public LogContract(){}

    public static final class LogEntry implements BaseColumns{

        public static final String TABLE_NAME = "log";
        public static final String COLUMN_START_DATE = "startdate";
        public static final String COLUMN_END_DATE = "enddate";
        public static final String COLUMN_START_DISTANCE = "startdistance";
        public static final String COLUMN_END_DISTANCE = "enddistance";
        public static final String COLUMN_GAS_AMOUNT = "gasamount";

    }
}
