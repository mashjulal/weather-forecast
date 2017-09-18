package com.mashjulal.android.forecastwidget.googleplaces;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


/**
 * Class for place predictions of query from API Google Places
 *
 * Class is used to contain information about request:
 * - predictions
 * - status
 */
public class PredictionModel {


    /**
     * List of place predictions which
     * matches to requested query
     */
    @SerializedName("predictions")
    @Expose
    private List<Place> predictions;

    /**
     * Data about the status of the request and debugging information,
     * which allows to find out the cause of the failure
     */
    @SerializedName("status")
    @Expose
    private String status;

    public List<Place> getPredictions() {
        return predictions;
    }

    public String getStatus() {
        return status;
    }
}
