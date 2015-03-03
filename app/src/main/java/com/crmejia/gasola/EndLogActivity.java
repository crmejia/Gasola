package com.crmejia.gasola;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.crmejia.gasola.data.LogContract;
import com.crmejia.gasola.data.LogProvider;


public class EndLogActivity extends Activity {
    public static String LOG_TAG = EndLogActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_log);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_end_log, menu);
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
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        private Button mEndLogButton;
        private Button mCancelLogButton;

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
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_end_log, container, false);
            mCancelLogButton = (Button) rootView.findViewById(R.id.cancel_log_button);
            mEndLogButton = (Button) rootView.findViewById(R.id.end_log_button);

            String startDistance = String.format(getString(R.string.new_log_distance), Utility.getDistanceUnit(getActivity()));
            ((TextView)rootView.findViewById(R.id.end_distance_textView)).setText(startDistance);
            Intent intent = getActivity().getIntent();
            final ContentResolver contentResolver = getActivity().getContentResolver();

            if(intent != null && intent.hasExtra(Intent.EXTRA_TEXT)){
                final String[] logIdString = {intent.getStringExtra(Intent.EXTRA_TEXT)};
                mCancelLogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        contentResolver.delete(LogContract.LogEntry.CONTENT_URI, LogProvider._ID_SELECTION, logIdString);
                        getActivity().setResult(RESULT_OK, null);
                        getActivity().finish();
                    }
                });

                mEndLogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //read & validate end distance
                        String endDistanceString = ((TextView)rootView.findViewById(R.id.end_distance_editText)).getText().toString();
                        try{
                            int endDistance = Integer.parseInt(endDistanceString);
                            Cursor currentLogCursor= contentResolver.query(
                                    LogContract.LogEntry.CONTENT_URI,
                                    LOG_COLUMNS,
                                    LogProvider._ID_SELECTION,
                                    logIdString,
                                    null);
                            if(currentLogCursor.moveToFirst()){
                                int startDistance = currentLogCursor.getInt(COL_LOG_START_DISTANCE);
                                //only allow end distance which is longer than start distance
                                if(startDistance < endDistance){
                                    ContentValues endDistanceValue = new ContentValues();
                                    endDistanceValue.put(LogContract.LogEntry.COLUMN_END_DISTANCE, endDistance);
                                    contentResolver.update(
                                            LogContract.LogEntry.CONTENT_URI,
                                            endDistanceValue,
                                            LogProvider._ID_SELECTION,
                                            logIdString
                                    );
                                    getActivity().setResult(RESULT_OK, null);
                                    getActivity().finish();
                                }
                            } else{
                                Utility.toastDistance(getActivity());
                            }
                        } catch (NumberFormatException e){
                            Log.w(LOG_TAG,"Not able to parse the end distance");
                            Utility.toastDistance(getActivity());
                        }

                    }
                });
            } else {

                View.OnClickListener finishActivity = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getActivity().finish();
                    }
                };

                mCancelLogButton.setOnClickListener(finishActivity);
                mEndLogButton.setOnClickListener(finishActivity);
            }
            return rootView;
        }
    }
}
