package com.mashjulal.android.forecastwidget.openweathermap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


/**
 * Class for city forecast from API OpenWeatherMap
 *
 * Class is used to contain information about forecast request:
 * - request code
 * - forecast list
 */
public class ForecastModel {

    /**
     * Request status code
     */
    @SerializedName("cod")
    @Expose
    private int cod;

    /**
     * Forecast list for requested city
     */
    @SerializedName("list")
    @Expose
    private List<APIObject.Forecast> list;

    public int getCod() {
        return cod;
    }

    public List<APIObject.Forecast> getList() {
        return list;
    }
}
