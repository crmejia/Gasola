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

    public static float fuelEconomy(int totalDistance, int amount) {
        if (totalDistance > 0 && amount > 0) {
            return totalDistance / amount;
        }
        return 0.0f;
    }

    public static float fuelConsumption(int totalDistance, int amount){
        if (totalDistance > 0 && amount > 0) {
            return (amount * 100) / totalDistance;
        }
        return 0.0f;
    }

    public static String formattedfuelEconomy(float fuelEconomy, Context context){
        String unit = Integer.toString(Math.round(fuelEconomy))
                + " " + getDistanceUnit(context) + " per " + getAmountUnit(context);
        return String.format(context.getString(R.string.fuel_economy), unit);
    }

    public static String formattedfuelConsumption(float fuelConsumption, Context context){
        String unit = Integer.toString(Math.round(fuelConsumption))
                + " " + getAmountUnit(context) + " per 100 " + getDistanceUnit(context);
        return String.format(context.getString(R.string.fuel_consumption), unit);
    }
}
