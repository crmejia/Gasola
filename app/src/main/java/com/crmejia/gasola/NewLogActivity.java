package com.crmejia.gasola;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.crmejia.gasola.data.LogContract;

import java.util.Date;


public class NewLogActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_log);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_log, menu);
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
        private final String LOG_TAG = MainActivity.class.getSimpleName();

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_new_log, container, false);
            TextView gasAmountTextView = (TextView)rootView.findViewById(R.id.gas_amount_textView);
            TextView startDistanceTextView = (TextView)rootView.findViewById(R.id.start_distance_textView);
            Button startLogButton = (Button)rootView.findViewById(R.id.start_log_button);
            Button cancelLogButton = (Button)rootView.findViewById(R.id.cancel_log_button);

            String gasAmount = String.format(getString(R.string.new_log_amount), Utility.getAmountUnit(getActivity()));
            String startDistance = String.format(getString(R.string.new_log_distance), Utility.getDistanceUnit(getActivity()));
            gasAmountTextView.setText(gasAmount);
            startDistanceTextView.setText(startDistance);

            startLogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(createNewLog()){
                        getActivity().setResult(RESULT_OK, null);
                        getActivity().finish();
                    }
                }
            });

            cancelLogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getActivity().finish();
                }
            });

            return rootView;
        }
        private boolean createNewLog(){
            //read input & validate
            String gasAmountString = ((EditText) getView().findViewById(R.id.gas_amount_editText)).getText().toString();
            String startDistanceString = ((EditText) getView().findViewById(R.id.start_distance_editText)).getText().toString();
            int gasAmount = 0, startDistance = 0;
            try {
                gasAmount = Integer.parseInt(gasAmountString);
                gasAmount = Utility.gallonToLiter(gasAmount,getActivity());
            } catch (NumberFormatException e) {
                Utility.toastAmount(getActivity());
                Log.i(LOG_TAG, e.getMessage());
                return false;
            }

            try {
                startDistance = Integer.parseInt(startDistanceString);
                startDistance = Utility.mileToKilometer(startDistance,getActivity());
            } catch (NumberFormatException e) {
                Utility.toastDistance(getActivity());
                Log.i(LOG_TAG, e.getMessage());
                return false;
            }
            
            if(gasAmount > 0 && startDistance > 0) {
                //get contentProvider from resolver and insertdata
                ContentValues newLogValues = new ContentValues();
                newLogValues.put(LogContract.LogEntry.COLUMN_START_DISTANCE, startDistance);
                newLogValues.put(LogContract.LogEntry.COLUMN_GAS_AMOUNT, gasAmount);
                newLogValues.put(LogContract.LogEntry.COLUMN_START_DATE, LogContract.LogEntry.getDbDateString(new Date()));

                // fake values, should be null?
                newLogValues.put(LogContract.LogEntry.COLUMN_END_DISTANCE, 0);
                newLogValues.put(LogContract.LogEntry.COLUMN_END_DATE, LogContract.LogEntry.getDbDateString(new Date()));


                Uri newLogUri = getActivity().getContentResolver().insert(LogContract.LogEntry.CONTENT_URI, newLogValues);

                if (ContentUris.parseId(newLogUri) != -1)
                    return true;
            }else{
                if(gasAmount <= 0)
                    Utility.toastAmount(getActivity());
                else if (startDistance <= 0)
                    Utility.toastDistance(getActivity());
            }

            return  false;
        }
    }
}
