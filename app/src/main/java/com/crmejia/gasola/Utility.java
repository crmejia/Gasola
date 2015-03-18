package com.crmejia.gasola;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.crmejia.gasola.data.LogContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by crismarmejia on 2/19/15.
 */
public class Utility {

    public static final float KM_TO_MILES_CONSTANT = 0.62137f;
    public static final float LITER_TO_US_GALLON_CONSTANT = 0.264172f;
    public static final float LITER_TO_IMPERIAL_GALLO_CONSTANT = 0.219969f;
    public static final double MILE_TO_KILOMETER_CONSTANT = 1.60934;
    public static final double IMPERIAL_GAL_TO_LITER = 4.54609;
    public static final double US_GAL_TO_LITER = 3.78541;

    public static void toastDistance(Context context){
        Toast.makeText(context, "What is your current distance on your dashboard?", Toast.LENGTH_SHORT).show();
    }

    public static void toastAmount(Context context) {
        Toast.makeText(context, "How much gas did you put in?", Toast.LENGTH_SHORT).show();
    }

    public static String formattedTotalDistance(int startDistance, int endDistance, Context context){
        int totalDistance =  properDistance((endDistance - startDistance), context);
        return String.format("%s %s",totalDistance,getDistanceUnit(context));
    }

    public static String formattedAmount(int amount, Context context){
       return String.format("%s %s", properAmount(amount,context),  getAmountUnit(context));

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
        return Integer.toString(Math.round(fuelEconomy));
    }

    public static String formattedfuelEconomyUnit(Context context){
        return getDistanceUnit(context) + " per " + getAmountUnit(context);
    }

    public static String formattedfuelConsumption(float fuelConsumption, Context context){
        return Integer.toString(Math.round(fuelConsumption));
    }

    public static String formattedfuelConsumptionUnit(Context context){
        return getAmountUnit(context) + " per 100 " + getDistanceUnit(context);
    }

    // Format used for storing dates in the database.  ALso used for converting those strings
    // back into date objects for comparison/processing.
    public static final String DATE_FORMAT = "yyyyMMdd";
    /**
     * Helper method to convert the database representation of the date into something to display
     * to users.  As classy and polished a user experience as "20140102" is, we can do better.
     *
     * @param context Context to use for resource localization
     * @param dateStr The db formatted date string, expected to be of the form specified
     *                in Utility.DATE_FORMAT
     * @return a user-friendly representation of the date.
     */
    public static String getFriendlyDayString(Context context, String dateStr) {
        // The day string for forecast uses the following logic:
        // For today: "Today, June 8"
        // For tomorrow:  "Tomorrow"
        // For the next 5 days: "Wednesday" (just the day name)
        // For all days after that: "Mon Jun 8"

        Date todayDate = new Date();
        String todayStr = LogContract.getDbDateString(todayDate);
        Date inputDate = LogContract.getDateFromDb(dateStr);

        // If the date we're building the String for is today's date, the format
        // is "Today, June 24"
        if (todayStr.equals(dateStr)) {
            String today = context.getString(R.string.today);
            int formatId = R.string.format_full_friendly_date;
            return String.format(context.getString(
                    formatId,
                    today,
                    getFormattedMonthDay(context, dateStr)));
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(todayDate);
            cal.add(Calendar.DATE, 7);
            String weekFutureString = LogContract.getDbDateString(cal.getTime());

//            if (dateStr.compareTo(weekFutureString) < 0) {
//                // If the input date is less than a week in the future, just return the day name.
//                return getDayName(context, dateStr);
//            } else {
                // Otherwise, use the form "Mon Jun 3"
                SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEEE, MMMM dd");
                return shortenedDateFormat.format(inputDate);
//            }
        }
    }

    /**
     * Given a day, returns just the name to use for that day.
     * E.g "today", "tomorrow", "wednesday".
     *
     * @param context Context to use for resource localization
     * @param dateStr The db formatted date string, expected to be of the form specified
     *                in Utility.DATE_FORMAT
     * @return
     */
    public static String getDayName(Context context, String dateStr) {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(Utility.DATE_FORMAT);
        try {
            Date inputDate = dbDateFormat.parse(dateStr);
            Date todayDate = new Date();
            // If the date is today, return the localized version of "Today" instead of the actual
            // day name.
            if (LogContract.getDbDateString(todayDate).equals(dateStr)) {
                return context.getString(R.string.today);
            } else {
                // If the date is set for yesterday, the format is "Yesterday".
                Calendar cal = Calendar.getInstance();
                cal.setTime(todayDate);
                cal.add(Calendar.DATE, -1);
                Date tomorrowDate = cal.getTime();
                if (LogContract.getDbDateString(tomorrowDate).equals(
                        dateStr)) {
                    return context.getString(R.string.yesterday);
                } else {
                    // Otherwise, the format is just the day of the week (e.g "Wednesday".
                    SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
                    return dayFormat.format(inputDate);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            // It couldn't process the date correctly.
            return "";
        }
    }

    /**
     * Converts db date format to the format "Month day", e.g "June 24".
     * @param context Context to use for resource localization
     * @param dateStr The db formatted date string, expected to be of the form specified
     *                in Utility.DATE_FORMAT
     * @return The day in the form of a string formatted "December 6"
     */
    public static String getFormattedMonthDay(Context context, String dateStr) {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(Utility.DATE_FORMAT);
        try {
            Date inputDate = dbDateFormat.parse(dateStr);
            SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMMM dd");
            String monthDayString = monthDayFormat.format(inputDate);
            return monthDayString;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    //conversions
    private static int kilometerToMile(int kilometers){
        return (int) (kilometers * KM_TO_MILES_CONSTANT);
    }

    private static int mileToKilometer(int miles){
        return (int) (miles * MILE_TO_KILOMETER_CONSTANT);
    }

    private static int literToUsGallon(int liter){
        return (int) (liter * LITER_TO_US_GALLON_CONSTANT);
    }

    private static int literToImperialGallon(int liter){
        return (int) (liter * LITER_TO_IMPERIAL_GALLO_CONSTANT);
    }

    private static int UsGallonToLiter(int amount) {
        return (int)(amount * US_GAL_TO_LITER);
    }

    private static int ImperialGallonToLiter(int amount) {
        return (int) (amount * IMPERIAL_GAL_TO_LITER);
    }

    public static int gallonToLiter(int amount, Context context){
        String amountUnit = getAmountUnit(context);

        if(context.getString(R.string.pref_amount_units_imperial_gallon).equals(amountUnit)){
            amount = ImperialGallonToLiter(amount);
        } else if(context.getString(R.string.pref_amount_units_us_gallon).equals(amountUnit)){
            amount = UsGallonToLiter(amount);
        }
        return  amount;
    }

    public static int mileToKilometer(int distance, Context context){
        String distanceUnit = getDistanceUnit(context);

        if(!context.getString(R.string.pref_distance_units_kilometers).equals(distanceUnit)){
            distance = mileToKilometer(distance);
        }

        return distance;
    }


    public static int properDistance(int distance, Context context){
        String distanceUnit = getDistanceUnit(context);

        if(!context.getString(R.string.pref_distance_units_kilometers).equals(distanceUnit)){
            distance = kilometerToMile(distance);
        }

        return distance;
    }

    public static int properAmount(int amount, Context context){
        String amountUnit = getAmountUnit(context);

        if(context.getString(R.string.pref_amount_units_imperial_gallon).equals(amountUnit)){
            amount = literToImperialGallon(amount);
        } else if(context.getString(R.string.pref_amount_units_us_gallon).equals(amountUnit)){
            amount = literToUsGallon(amount);
        }
        return amount;
    }

}
