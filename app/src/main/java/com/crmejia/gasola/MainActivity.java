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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.crmejia.gasola.data.LogContract;


public class MainActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
        private Button mNewLogButton;
        private Button mCurrentLogButton;
        private SimpleCursorAdapter mLogAdapter;

        private static final int LOG_LOADER = 1;

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


        public PlaceholderFragment() {
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

//            TODO mLogAdapter.setViewBinder( );


            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            ListView listView = (ListView) rootView.findViewById(R.id.listView_logs);
            listView.setAdapter(mLogAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                    Cursor cursor = mLogAdapter.getCursor();
                    if(cursor != null && cursor.moveToPosition(position)) {
                        String gasAmount = cursor.getString(COL_LOG_GAS_AMOUNT);
                        String startDistance = cursor.getString(COL_LOG_START_DISTANCE);
                        String endDistance = cursor.getString(COL_LOG_END_DISTANCE);
                        String startDate = cursor.getString(COL_LOG_START_DATE);
                        String endDate = cursor.getString(COL_LOG_END_DATE);

                        String extraString = String.format("%s litres - %s km - %s km   %s   %s",
                                gasAmount, startDistance, endDistance, startDate, endDate);

                        Intent logDetailIntent = new Intent(getActivity(), LogDetailActivity.class)
                                .putExtra(Intent.EXTRA_TEXT, extraString);

                        startActivity(logDetailIntent);
                    }
                }
            });

            mNewLogButton = (Button) rootView.findViewById(R.id.new_log_button);
            mCurrentLogButton = (Button) rootView.findViewById(R.id.current_log_button);

            mCurrentLogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Cursor currentLogCursor = getActivity().getContentResolver().query(LogContract.LogEntry.CONTENT_URI,LOG_COLUMNS,null,null,null);
                    if (currentLogCursor.moveToLast() && currentLogCursor.getInt(COL_LOG_END_DISTANCE) == 0) {
                        String idString = currentLogCursor.getString(COL_LOG_ID);

                        Intent endLogIntent = new Intent(getActivity(), EndLogActivity.class)
                                .putExtra(Intent.EXTRA_TEXT, idString);
                        startActivity(endLogIntent);

                    }
                }
            });

            mNewLogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Cursor currentLogCursor = getActivity().getContentResolver().query(LogContract.LogEntry.CONTENT_URI,LOG_COLUMNS,null,null,null);
                    if (currentLogCursor.moveToLast() && currentLogCursor.getInt(COL_LOG_END_DISTANCE) != 0) {
                        Intent newLogIntent = new Intent(getActivity(), NewLogActivity.class);
                        startActivity(newLogIntent);
                    }
                }
            });


//            if(newLogCursor.moveToLast()) {
//                if(newLogCursor.getInt(COL_LOG_END_DISTANCE) == 0) {
////                a log is open
//                    mCurrentLogButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            String idString = newLogCursor.getString(COL_LOG_ID);
//
//                            Intent endLogIntent = new Intent(getActivity(), EndLogActivity.class)
//                                    .putExtra(Intent.EXTRA_TEXT, idString);
//                            startActivity(endLogIntent);
//                        }
//                    });
//                } else {
////                    no log is open, create a new one
//                    mNewLogButton.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            Intent newLogIntent = new Intent(getActivity(), NewLogActivity.class);
//                            startActivity(newLogIntent);
//                        }
//                    });
//                }
//            }

            return rootView;
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
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            mLogAdapter.swapCursor(null);

        }
    }
}
