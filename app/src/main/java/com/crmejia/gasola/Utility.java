package com.crmejia.gasola;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

/**
 * Created by crismarmejia on 2/19/15.
 */
public class Utility {
    public static void toastDistance(Context context){
        Toast.makeText(context, "distance should be a valid number", Toast.LENGTH_SHORT).show();
    }

    public static String formattedTotalDistance(int startDistance, int endDistance, Context context){
        int totalDistance =  endDistance - startDistance;
        return String.format("%s %s",totalDistance, getDistanceUnit(context));
    }

    public static String getDistanceUnit(Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_distance_units_key),
                context.getString(R.string.pref_distance_units_kilometers));
    }

    public static String getAmountUnit(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_amount_units_key),
                context.getString(R.string.pref_amount_units_litres));
    }
}
