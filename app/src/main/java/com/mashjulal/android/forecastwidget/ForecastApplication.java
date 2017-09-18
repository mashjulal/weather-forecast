package com.mashjulal.android.forecastwidget;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;

import com.mashjulal.android.forecastwidget.googleplaces.PlaceModel;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Application class
 */
public class ForecastApplication extends Application {

    // SharedPreferences parameter tags
    public static final String PREFIX_WIDGET_ID = "widget_id_";
    public static final String PREF_LAT = "_lat";
    public static final String PREF_LNG = "_lng";
    public static final String PREF_IS_INSTALLED = "_is_installed";
    public static final String PREF_CITY = "_city";

    static Bus bus = new Bus(ThreadEnforcer.MAIN);

    /**
     * Method returns latitude and longitude for specified app widget ID
     * @param context Application context
     * @param widgetId Unique app widget ID
     * @return array of geographic coordinates
     */
    static float[] getCoordsByWidgetId(Context context, int widgetId){
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(context);
        float lat = prefs.getFloat(PREFIX_WIDGET_ID + widgetId + PREF_LAT, -10000000);
        float lng = prefs.getFloat(PREFIX_WIDGET_ID + widgetId + PREF_LNG, -10000000);
        return new float[]{lat, lng};
    }

    /**
     * Method returns city name for specified app widget ID
     * @param context Application context
     * @param widgetId Unique app widget ID
     * @return city name
     */
    static String getCityNameByWidgetId(Context context, int widgetId){
        SharedPreferences prefs =
                PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(PREFIX_WIDGET_ID + widgetId + PREF_CITY, "");
    }

    /**
     * Method removes information associated with app widget ID
     * @param context Application context
     * @param widgetId Unique app widget ID
     */
    static void removeWidgetInfoById(Context context, int widgetId){
        SharedPreferences.Editor editor =
                PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.remove(PREFIX_WIDGET_ID + widgetId + PREF_LAT);
        editor.remove(PREFIX_WIDGET_ID + widgetId + PREF_LNG);
        editor.remove(PREFIX_WIDGET_ID + widgetId + PREF_IS_INSTALLED);
        editor.remove(PREFIX_WIDGET_ID + widgetId + PREF_CITY);
        editor.apply();
    }

    /**
     * Method sets information associated with specified app widget ID
     * @param location latitude and longitude of city
     * @param city City name
     */
    public static void setLocationToPref(Context context, int appWidgetId,
                                         PlaceModel.Result.Geometry.Location location, String city){
        SharedPreferences.Editor editor =
                PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putFloat(PREFIX_WIDGET_ID + appWidgetId + PREF_LAT, (float) location.getLat());
        editor.putFloat(PREFIX_WIDGET_ID + appWidgetId + PREF_LNG, (float) location.getLng());
        editor.putBoolean(PREFIX_WIDGET_ID + appWidgetId + PREF_IS_INSTALLED, true);
        editor.putString(PREFIX_WIDGET_ID + appWidgetId + PREF_CITY, city);
        editor.apply();
    }

    /**
     * Method checks connection to the Internet and returns
     * if is connected
     * @param context Application context
     * @return If device is connected to the Internet
     */
    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();

        return info != null &&
                (info.getType() == ConnectivityManager.TYPE_MOBILE ||
                        info.getType() == ConnectivityManager.TYPE_WIFI) &&
                info.getState() == NetworkInfo.State.CONNECTED;
    }
}
