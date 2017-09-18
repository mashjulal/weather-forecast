package com.mashjulal.android.forecastwidget.openweathermap;


import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;


/**
 * Client class for API OpenWeatherMap
 *
 * Static class is used to make requests to API OpenWeatherMap
 */
public class OpenWeatherMapApiClient {

    // codes for units in requests
    private static final String UNITS_STANDARD = "standard";
    private static final String UNITS_METRIC = "metric";
    private static final String UNITS_IMPERIAL = "imperial";

    // supported languages codes
    private static final String LANGUAGE_EN = "en";
    private static final String LANGUAGE_RU = "ru";

    /// TAG is for use logging output to LogCat
    private static final String TAG = OpenWeatherMapApiClient.class.getSimpleName();
    // API key for requests to OpenWeatherMap
    private static String API_KEY;

    private String language = LANGUAGE_EN;
    private String units = UNITS_STANDARD;
    private OpenWeatherMapApi openWeatherMapApi;
    private static OpenWeatherMapApiClient instance;

    /**
     * Fabric method for creating static instance of
     * {@link OpenWeatherMapApiClient}
     * @return OpenWeatherMapApiClient instance
     */
    public static OpenWeatherMapApiClient newInstance(){
        if (instance == null)
            instance = new OpenWeatherMapApiClient();
        return instance;
    }

    /**
     * Public method for getting {@link WeatherModel}
     * for requested geographic coordinates
     * @param lat latitude
     * @param lon longitude
     * @return request result which contains information about weather
     */
    public WeatherModel getCurrentWeather(double lat, double lon){
        AsyncTask<Double, Void, WeatherModel> asyncTaskGetWeatherByCoords =
                new AsyncTask<Double, Void, WeatherModel>() {
                    @Override
                    protected WeatherModel doInBackground(Double... params) {
                        WeatherModel result = null;
                        try {
                            double lat = params[0], lon = params[1];
                            result = openWeatherMapApi
                                    .getWeatherByGeographicCoordinates(API_KEY, lat, lon, units, language)
                                    .execute().body();
                        } catch (IOException e) {
                            Log.d(TAG, e.toString());
                        }
                        return result;
                    }
                };

        WeatherModel result = null;
        try {
            result = asyncTaskGetWeatherByCoords.execute(lat, lon).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.d(TAG, e.toString());
        }
        return result;
    }

    /**
     * Public method for getting {@link ForecastModel}
     * for requested geographic coordinates
     * @param lat latitude
     * @param lon longitude
     * @return request result which contains forecast information
     */
    public ForecastModel getForecast(double lat, double lon){
        AsyncTask<Double, Void, ForecastModel> asyncTaskGetForecastByCoords =
                new AsyncTask<Double, Void, ForecastModel>() {
                    @Override
                    protected ForecastModel doInBackground(Double... params) {
                        ForecastModel result = null;
                        try {
                            double lat = params[0], lon = params[1];
                            result = openWeatherMapApi
                                    .getForecastByGeographicCoordinates(API_KEY, lat, lon, units, language)
                                    .execute().body();
                        } catch (IOException e) {
                            Log.d(TAG, e.toString());
                        }
                        return result;
                    }
                };
        ForecastModel result = null;
        try {
            result = asyncTaskGetForecastByCoords.execute(lat, lon).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.d(TAG, e.toString());
        }
        return result;
    }

    private OpenWeatherMapApiClient() {
        createApi();
        setLanguage();
    }

    /**
     * Private method which set supported request result language
     */
    private void setLanguage() {
        String lang = Locale.getDefault().getLanguage();
        switch (lang){
            case LANGUAGE_RU:
                language = LANGUAGE_RU;
                units = UNITS_METRIC;
                break;
            case LANGUAGE_EN:
                language = LANGUAGE_EN;
                units = UNITS_IMPERIAL;
                break;
        }
    }

    /**
     * Private method which creates {@link OpenWeatherMapApi} instance
     * using {@link Retrofit}
     */
    private void createApi(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // TODO: add API key initialization
        openWeatherMapApi = retrofit.create(OpenWeatherMapApi.class);
    }

    /**
     * Interface for requests to API Google Places
     */
    interface OpenWeatherMapApi {

        /**
         * Method which gets weather information for
         * requested geographic coordinates
         * @param appID valid API key of API OpenWeatherMap
         * @param lat latitude
         * @param lon longitude
         * @param unitsType specified units type for requests
         * @param language specified language for requests
         * @return request result which contains weather information
         */
        @GET("/data/2.5/weather")
        Call<WeatherModel> getWeatherByGeographicCoordinates(
                @Query("APPID") String appID,
                @Query("lat") double lat,
                @Query("lon") double lon,
                @Query("units") String unitsType,
                @Query("lang") String language);

        /**
         * Method which gets forecast for requested
         * geographic coordinates
         8 valid API key of API OpenWeatherMap
         * @param lat latitude
         * @param lon longitude
         * @param unitsType specified units type for requests
         * @param language specified language for requests
         * @return request result which contains forecast
         */
        @GET("/data/2.5/forecast")
        Call<ForecastModel> getForecastByGeographicCoordinates(
                @Query("APPID") String appID,
                @Query("lat") double lat,
                @Query("lon") double lon,
                @Query("units") String unitsType,
                @Query("lang") String language);
    }
}
