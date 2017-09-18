package com.mashjulal.android.forecastwidget;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Receiver listens for current network state
 * and updates all application appwidgets if device
 * is connected to the Internet
 */
public class InternetStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (isOnline(context)) {
            updateForecastWidgets(context);
        }
    }

    /**
     * Static method which checks current network state
     * @param context Application context
     * @return true if device is connected to the Internet; false otherwise
     */
    private static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    /**
     * Static method which updates application all appwidgets
     * @param context Application context
     */
    private static void updateForecastWidgets(Context context) {
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(
                new ComponentName(context, ForecastWidget.class));
        Intent updateIntent = new Intent(context, ForecastWidget.class);
        updateIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(updateIntent);
    }
}
