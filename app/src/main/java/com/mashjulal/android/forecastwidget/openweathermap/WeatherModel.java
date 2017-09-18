package com.mashjulal.android.forecastwidget.openweathermap;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


/**
 * Class for city weather information from API OpenWeatherMap
 *
 * Class is used to contain information about weather request:
 * - city ID
 * - weather
 * - main information
 * - wind
 * - status
 */
public class WeatherModel {

    /**
     * City ID
     */
    @SerializedName("id")
    @Expose
    private int id;

    /**
     * Weather information
     */
    @SerializedName("weather")
    @Expose
    private List<APIObject.Weather> weather;

    /**
     * Main information
     */
    @SerializedName("main")
    @Expose
    private APIObject.Main main;

    /**
     * Information about wind
     */
    @SerializedName("wind")
    @Expose
    private APIObject.Wind wind;

    /**
     * Request status
     */
    @SerializedName("cod")
    @Expose
    private float cod;

    public List<APIObject.Weather> getWeather() {
        return weather;
    }

    public APIObject.Main getMain() {
        return main;
    }

    public APIObject.Wind getWind() {
        return wind;
    }

    public float getCod() {
        return cod;
    }

    public int getId() {
        return id;
    }
}
