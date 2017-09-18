package com.mashjulal.android.forecastwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.widget.RemoteViews;

import com.mashjulal.android.forecastwidget.openweathermap.APIObject;
import com.mashjulal.android.forecastwidget.openweathermap.ForecastModel;
import com.mashjulal.android.forecastwidget.openweathermap.OpenWeatherMapApiClient;
import com.mashjulal.android.forecastwidget.openweathermap.WeatherModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import static com.mashjulal.android.forecastwidget.ForecastApplication.getCityNameByWidgetId;
import static com.mashjulal.android.forecastwidget.ForecastApplication.getCoordsByWidgetId;
import static com.mashjulal.android.forecastwidget.ForecastApplication.isConnectedToInternet;
import static com.mashjulal.android.forecastwidget.ForecastApplication.removeWidgetInfoById;


/**
 * Class is used to display information
 * about current weather and 3-day forecast
 */
public class ForecastWidget extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // If not connected to the Internet then refuse update
        if (!isConnectedToInternet(context))
            return;

        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        for (int widgetId: appWidgetIds)
            removeWidgetInfoById(context, widgetId);
    }
    /**
     * Static method updates widget by it's ID
     * @param context Application context
     * @param appWidgetManager Application app widget manager
     * @param appWidgetId Unique app widget id
     */
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // API OpenWeatherMap Client
        OpenWeatherMapApiClient owmah = OpenWeatherMapApiClient.newInstance();

        // Load app widget information from Shared Preferences
        float[] coords = getCoordsByWidgetId(context, appWidgetId);
        String city = getCityNameByWidgetId(context, appWidgetId);

        // Get weather and forecast information from OpenWeatherMap
        ForecastModel forecastModel = owmah.getForecast(coords[0], coords[1]);
        WeatherModel weatherModel = owmah.getCurrentWeather(coords[0], coords[1]);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_forecast);

        // Update app widget if there are weather and forecast and requests were successful
        if (forecastModel != null && weatherModel != null &&
                forecastModel.getCod() == 200 && weatherModel.getCod() == 200) {
            setTodayWeatherInfo(context, views, city, weatherModel);
            setNextDayWeatherInfo(1, views, forecastModel);
            setNextDayWeatherInfo(2, views, forecastModel);
            setNextDayWeatherInfo(3, views, forecastModel);

            // Set pending intent for configure city name
            Intent intentSettings = new Intent(context, ConfigurationActivity.class);
            intentSettings.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            PendingIntent piSettings = PendingIntent.getActivity(context, appWidgetId, intentSettings, 0);
            views.setOnClickPendingIntent(R.id.tv_forecast_city, piSettings);

            // Set pending intent for opening OpenWeatherMap's site if user click app widget
            Intent intentSite = new Intent(Intent.ACTION_VIEW);
            intentSite.setData(Uri.parse("https://openweathermap.org/city/" + weatherModel.getId()));
            PendingIntent pendingIntent = PendingIntent.getActivity(context, appWidgetId, intentSite, 0);
            views.setOnClickPendingIntent(R.id.gl_forecast, pendingIntent);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }

    /**
     * Method updates next day forecast information for app widget:
     * - day name;
     * - weather type at middle of the day;
     * - max and min temperature
     * @param dayOffset day offset from today
     * @param rv app widget's RemoteVies
     * @param fm {@link ForecastModel} which was got from API OpenWeatherMap
     */
    private static void setNextDayWeatherInfo(int dayOffset, RemoteViews rv, ForecastModel fm) {
        // Get day weather (at 00:00) index in forecast
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        hour -= hour % 3;
        int index = (24 * dayOffset - hour) / 3;
        // Get forecast for middle of the day
        APIObject.Forecast lo = fm.getList().get(index+4);
        // Find daily min and max temperature
        float tempMin = 1000, tempMax = -1000;
        for (APIObject.Forecast l : fm.getList().subList(index, index + 8)) {
            float temp = l.getMain().getTemp();
            if (temp > tempMax)
                tempMax = temp;
            if (temp < tempMin)
                tempMin = temp;
        }

        int dayNameViewID = 0;
        int weatherTypeViewID = 0;
        int maxTempViewID = 0;
        int minTempViewID = 0;
        switch (dayOffset) {
            case 1:
                dayNameViewID = R.id.tv_forecast_nextDay1Name;
                weatherTypeViewID = R.id.iv_forecast_nextDay1WeatherType;
                maxTempViewID = R.id.tv_forecast_nextDay1TempMax;
                minTempViewID = R.id.tv_forecast_nextDay1TempMin;
                break;
            case 2:
                dayNameViewID = R.id.tv_forecast_nextDay2Name;
                weatherTypeViewID = R.id.iv_forecast_nextDay2WeatherType;
                maxTempViewID = R.id.tv_forecast_nextDay2TempMax;
                minTempViewID = R.id.tv_forecast_nextDay2TempMin;
                break;
            case 3:
                dayNameViewID = R.id.tv_forecast_nextDay3Name;
                weatherTypeViewID = R.id.iv_forecast_nextDay3WeatherType;
                maxTempViewID = R.id.tv_forecast_nextDay3TempMax;
                minTempViewID = R.id.tv_forecast_nextDay3TempMin;
                break;
        }

        rv.setTextViewText(
                dayNameViewID,
                getNameOfDayOfWeek(dayOffset));
        rv.setImageViewResource(
                weatherTypeViewID,
                getImageResourceById(lo.getWeather().get(0).getIcon()));
        rv.setTextViewText(
                maxTempViewID,
                String.valueOf(Math.round(tempMax)));
        rv.setTextViewText(
                minTempViewID,
                String.valueOf(Math.round(tempMin)));
    }

    /**
     * Method updates current weather information for app widget:
     * - day name;
     * - current temperature;
     * - current pressure;
     * - current weather type;
     * - current wind speed and direction;
     * - city name;
     * - last update time
     * @param context Application context
     * @param rv App widget's RemoteVies
     * @param city City name
     * @param wm {@link WeatherModel} which was got from API OpenWeatherMap
     */
    private static void setTodayWeatherInfo(Context context, RemoteViews rv, String city, WeatherModel wm) {
        Calendar calendar = Calendar.getInstance();
        Resources resources = context.getResources();

        rv.setTextViewText(R.id.tv_forecast_city, city);
        rv.setTextViewText(R.id.tv_forecast_todayName, getNameOfDayOfWeek(0));
        rv.setImageViewResource(R.id.iv_forecast_todayWeatherType,
                getImageResourceById(wm.getWeather().get(0).getIcon()));
        rv.setTextViewText(
                R.id.tv_forecast_currentTemperature,
                String.format(resources.getString(R.string.temperature_template), Math.round(wm.getMain().getTemp())));
        rv.setImageViewBitmap(R.id.iv_forecast_windDirection,
                getWindAngleImage(context, wm.getWind().getDeg() - 180));
        rv.setTextViewText(R.id.tv_forecast_windSpeed,
                String.valueOf(Math.round(wm.getWind().getSpeed())));
        rv.setTextViewText(R.id.tv_forecast_pressure,
                String.valueOf(getPressure(wm.getMain().getPressure())));
        String lastUpdateTime = new SimpleDateFormat("HH:mm", Locale.getDefault())
                .format(calendar.getTime());
        rv.setTextViewText(R.id.tv_forecast_lastUpdate, lastUpdateTime);
    }

    /**
     * Method returns right direction for bitmap wind arrow
     * @param context Application context
     * @param angle Required arrow angle
     * @return Bitmap picture of wind arrow with right direction
     */
    private static Bitmap getWindAngleImage(Context context, float angle){
        Bitmap source = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_arrow);
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                source.getHeight(), matrix, true);
    }

    /**
     * Method returns correct day name with offset from today
     * @param noOfDaysFromToday Day offset from today
     * @return Correct day name
     */
    private static String getNameOfDayOfWeek(int noOfDaysFromToday){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, noOfDaysFromToday);
        SimpleDateFormat sdf = new SimpleDateFormat("EEE", Locale.getDefault());
        return sdf.format(calendar.getTime()).toUpperCase();
    }

    /**
     * Method return mipmap resource with given weather icon ID
     * @param id Resource ID
     * @return Mipmap resource
     */
    private static int getImageResourceById(String id){
        switch (id){
            case "01d":
                return R.drawable.ic_01d;
            case "01n":
                return R.drawable.ic_01n;
            case "02d":
                return R.drawable.ic_02d;
            case "02n":
                return R.drawable.ic_02n;
            case "03d": case "03n":
                return R.drawable.ic_03d;
            case "04d": case "04n":
                return R.drawable.ic_04d;
            case "09d": case "09n":
                return R.drawable.ic_09d;
            case "10d":
                return R.drawable.ic_10d;
            case "10n":
                return R.drawable.ic_10n;
            case "11d": case "11n":
                return R.drawable.ic_11d;
            case "13d": case "13n":
                return R.drawable.ic_13d;
            case "50d": case "50n":
                return R.drawable.ic_50d;
        }
        return 0;
    }

    /**
     * Method rounds and converts atmosphere pressure (hPa) to the format
     * specified in the current country (hPa/mmHg)
     * @param pressure atmosphere pressure in hPa
     * @return converted integer pressure (hPa/mmHg)
     */
    private static int getPressure(float pressure){
        String locale = Locale.getDefault().getLanguage();
        if (locale.equals("ru"))
            return Math.round(pressure * 0.75006375541921f);
        return Math.round(pressure);
    }
}

