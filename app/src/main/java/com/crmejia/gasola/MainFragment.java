package com.crmejia.gasola;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.crmejia.gasola.data.LogContract;

public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private Button mCurrentNewLogButton;
    private SimpleCursorAdapter mLogAdapter;
    private TextView mFuelEconomyTextView;
    private TextView mFuelConsumptionTextView;
    private float mAverageFuelConsumption = -1;
    private float mAverageFuelEconomy = -1;
    private String mDistanceUnit;
    private String mAmountUnit;
    private View mRootView;

    private static final int LOG_LOADER = 1;
    static final int NEW_LOG_REQUEST =1;
    static final int END_LOG_REQUEST =2;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //The current/new button changes text depending on a new log was created, or log was ended or canceled
        if(requestCode == NEW_LOG_REQUEST){
            //a new log was started we should change the button text to current log
            if(resultCode == Activity.RESULT_OK){
                mCurrentNewLogButton.setText(getString(R.string.current_log_button_string));
            }
        } else if(requestCode == END_LOG_REQUEST){
            //log was finalized or deleted we should change the button text to new log
            if(resultCode == Activity.RESULT_OK){
                mCurrentNewLogButton.setText(getString(R.string.new_log_button_string));
            }
        }
    }

    //projection
    private static final String[] LOG_COLUMNS={
            LogContract.LogEntry.TABLE_NAME + "." + LogContract.LogEntry._ID,
            LogContract.LogEntry.COLUMN_START_DATE,
            LogContract.LogEntry.COLUMN_END_DATE,
            LogContract.LogEntry.COLUMN_START_DISTANCE,
            LogContract.LogEntry.COLUMN_END_DISTANCE,
            LogContract.LogEntry.COLUMN_GAS_AMOUNT,
    };

    //Indices tied to the Log columns projection
    public static final int COL_LOG_ID = 0;
    public static final int COL_LOG_START_DATE = 1;
    public static final int COL_LOG_END_DATE = 2;
    public static final int COL_LOG_START_DISTANCE = 3;
    public static final int COL_LOG_END_DISTANCE = 4;
    public static final int COL_LOG_GAS_AMOUNT = 5;


    public MainFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(LOG_LOADER,null,this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mLogAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.list_item_log,
                null,
                new String[]{LogContract.LogEntry.COLUMN_START_DATE,
                        LogContract.LogEntry.COLUMN_END_DATE,
                        LogContract.LogEntry.COLUMN_START_DISTANCE,
                        LogContract.LogEntry.COLUMN_GAS_AMOUNT
                },
                new int[]{
                        R.id.list_item_log_start_date_textView,
                        R.id.list_item_log_end_date_textView,
                        R.id.list_item_log_distance_textView,
                        R.id.list_item_log_quantity_textView
                },
                0
        );

        mLogAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                int endDistance = cursor.getInt(COL_LOG_END_DISTANCE);
                switch (columnIndex){
                    case COL_LOG_START_DISTANCE:
                        if(endDistance > 0) {
                            ((TextView) view).setText(Utility.formattedTotalDistance(cursor.getInt(columnIndex), endDistance, getActivity()));
                        }
                        else
                            ((TextView)view).setText("Logging...");
                        return true;
                    case COL_LOG_GAS_AMOUNT:
                        ((TextView)view).setText(Utility.formattedAmount(cursor.getInt(columnIndex),getActivity()));
                        return true;
                    case COL_LOG_START_DATE:
                        ((TextView)view).setText(String.format("From %s",Utility.getFriendlyDayString(getActivity(),cursor.getString(columnIndex))));
                        return true;
                    case COL_LOG_END_DATE:
                        String text = "";
                        if(endDistance > 0) {
                            text = String.format("Until %s", Utility.getFriendlyDayString(getActivity(), cursor.getString(columnIndex)));
                        }
                        ((TextView) view).setText(text);
                        return true;
                }
                return false;
            }
        });

        mRootView = inflater.inflate(R.layout.fragment_main, container, false);

        //set fuel economy and consumption units
        setFuelUnits(mRootView);

        mFuelEconomyTextView = (TextView) mRootView.findViewById(R.id.fuel_economy_textView);
        mFuelConsumptionTextView = (TextView) mRootView.findViewById(R.id.fuel_consumption_textView);

        ListView mLogListView = (ListView) mRootView.findViewById(R.id.listView_logs);
        mLogListView.setAdapter(mLogAdapter);

        mCurrentNewLogButton = (Button) mRootView.findViewById(R.id.current_new_log_button);
        Cursor currentLogCursor = getActivity().getContentResolver().query(LogContract.LogEntry.CONTENT_URI, LOG_COLUMNS, null, null, null);

        if (currentLogCursor.moveToLast() && currentLogCursor.getInt(COL_LOG_END_DISTANCE) == 0)
            mCurrentNewLogButton.setText(getString(R.string.current_log_button_string));
        else
            mCurrentNewLogButton.setText(getString(R.string.new_log_button_string));


        mCurrentNewLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor currentLogCursor = getActivity().getContentResolver().query(LogContract.LogEntry.CONTENT_URI, LOG_COLUMNS, null, null, null);
                //move cursor to last entry, if there is a cursor(not empty) and its distance is 0 then we are currently logging
                //otherwise we need to create a new log
                boolean lastLog = currentLogCursor.moveToLast();
                if (lastLog && currentLogCursor.getInt(COL_LOG_END_DISTANCE) == 0) {
                    mCurrentNewLogButton.setText(getString(R.string.current_log_button_string));
                    String idString = currentLogCursor.getString(COL_LOG_ID);
                    Intent endLogIntent = new Intent(getActivity(), EndLogActivity.class)
                            .putExtra(Intent.EXTRA_TEXT, idString);
                    startActivityForResult(endLogIntent, END_LOG_REQUEST);
                } else {// if (lastLog && currentLogCursor.getInt(COL_LOG_END_DISTANCE) != 0) {
                    Intent newLogIntent = new Intent(getActivity(), NewLogActivity.class);
                    startActivityForResult(newLogIntent, NEW_LOG_REQUEST);
                }
                currentLogCursor.close();
            }
        });

        currentLogCursor.close();
        return mRootView;
    }

    private void setFuelUnits(View rootView) {
        ((TextView) rootView.findViewById(R.id.fuel_economy_unit_textView)).setText(Utility.formattedfuelEconomyUnit(getActivity()));
        ((TextView) rootView.findViewById(R.id.fuel_consumption_unit_textView)).setText(Utility.formattedfuelConsumptionUnit(getActivity()));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Sort order:  descending, by date.
        String sortOrder = LogContract.LogEntry.COLUMN_START_DATE + " DESC";

        return new CursorLoader(
                getActivity(),
                LogContract.LogEntry.CONTENT_URI,
                LOG_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mLogAdapter.swapCursor(data);
        if(mAverageFuelConsumption < 0 && mAverageFuelEconomy < 0 ) {
            calculateAverageFuel(data);
            mFuelConsumptionTextView.setText(Utility.formattedfuelConsumption(mAverageFuelConsumption, getActivity()));
            mFuelEconomyTextView.setText(Utility.formattedfuelEconomy(mAverageFuelEconomy, getActivity()));
        }

        //these are set to check for preference changes
        mDistanceUnit = Utility.getDistanceUnit(getActivity());
        mAmountUnit = Utility.getAmountUnit(getActivity());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mLogAdapter.swapCursor(null);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        //if any of the unit preference change update the views we check to member variable for a change in the preference
        String distanceUnit = Utility.getDistanceUnit(getActivity());
        String amountUnit = Utility.getAmountUnit(getActivity());
        if(mDistanceUnit != null && mAmountUnit != null && mRootView != null && (!mDistanceUnit.equals(distanceUnit) || !mAmountUnit.equals(amountUnit))) {
            mDistanceUnit = distanceUnit;
            mAmountUnit = amountUnit;
            setFuelUnits(mRootView);
            getLoaderManager().restartLoader(LOG_LOADER, null, this);
        }
        super.onResume();
    }

    private void calculateAverageFuel(Cursor cursor){
        float averageFuelConsumption = 0;
        float averageFuelEconomy = 0;

        if(cursor != null){
            int distance, amount, count =0 ;
            while(cursor.moveToNext()){
                distance = Utility.properDistance(cursor.getInt(COL_LOG_END_DISTANCE) - cursor.getInt(COL_LOG_START_DISTANCE),getActivity());
                if(distance > 0) {
                    count++;
                    amount = Utility.properAmount(cursor.getInt(COL_LOG_GAS_AMOUNT),getActivity());
                    averageFuelConsumption += Utility.fuelConsumption(distance, amount);
                    averageFuelEconomy += Utility.fuelEconomy(distance, amount);
                }
            }
            //use count and not cursor.getCount() because we skip current log
            mAverageFuelConsumption = averageFuelConsumption / count;
            mAverageFuelEconomy = averageFuelEconomy / count;
        }
    }
}