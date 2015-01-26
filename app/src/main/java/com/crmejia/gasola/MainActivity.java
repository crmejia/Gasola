package com.crmejia.gasola;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
    public static class PlaceholderFragment extends Fragment {
        private Button mNewLogButton;
        private Button mCurrentLogButton;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            String[] dummyData = {
                    "20 litres - 200 km     from 01/01/2015 until 01/03/2015",
                    "10 litres - 200 km     from 01/01/2015 until 01/03/2015",
                    "24 litres - 200 km     from 01/01/2015 until 01/03/2015",
                    "18 litres - 200 km     from 01/01/2015 until 01/03/2015",
                    "8 litres - 200 km     from 01/01/2015 until 01/03/2015",
                    "11 litres - 200 km     from 01/01/2015 until 01/03/2015",
                    "34 litres - 200 km     from 01/01/2015 until 01/03/2015",
                    "2 litres - 200 km     from 01/05/2015 until 01/03/2015",
                    "11 litres - 200 km     from 01/03/2015 until 01/05/2015",
                    "50 litres - 200 km     from 01/01/2015 until 01/03/2015",
            };

            List<String> logs = new ArrayList<String>(Arrays.asList(dummyData));

            ArrayAdapter<String> logsAdapter = new ArrayAdapter<String>(
                    getActivity(),
                    R.layout.list_item_log,
                    R.id.list_item_log_distance_textView,
                    logs);

            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            ListView listView = (ListView) rootView.findViewById(R.id.listView_logs);
            listView.setAdapter(logsAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent logDetailIntent = new Intent(getActivity(), LogDetailActivity.class);
                    startActivity(logDetailIntent);
                }
            });

            mNewLogButton = (Button) rootView.findViewById(R.id.new_log_button);
            mCurrentLogButton = (Button) rootView.findViewById(R.id.current_log_button);

            mNewLogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent newLogIntent = new Intent(getActivity(),NewLogActivity.class);
                    startActivity(newLogIntent);
                }
            });

            mCurrentLogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent endLogIntent = new Intent(getActivity(),EndLogActivity.class);
                    startActivity(endLogIntent);
                }
            });

            return rootView;
        }
    }
}
