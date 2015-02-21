package com.crmejia.gasola;


import android.app.Fragment;
import android.os.Bundle;
import android.preference.PreferenceFragment;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //load the preferences
        addPreferencesFromResource(R.xml.preferences);
    }
}
