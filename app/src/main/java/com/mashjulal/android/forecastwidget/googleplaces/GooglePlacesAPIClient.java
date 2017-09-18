package com.mashjulal.android.forecastwidget.googleplaces;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;


/*
* Client class for API Google Places
*
* Static class is used to make requests to API Google Places
*/
public class GooglePlacesAPIClient {

    // TAG is for use logging output to LogCat
    private static final String TAG = GooglePlacesAPIClient.class.getSimpleName();

    // API key for requests to API Google Places
    private static String API_KEY;

    // Codes of request status
    public static final String STATUS_OK = "OK";
    public static final String STATUS_ZERO_RESULTS = "ZERO_RESULTS";
    public static final String STATUS_OVER_QUERY_LIMIT = "OVER_QUERY_LIMIT";
    public static final String STATUS_REQUEST_DENIED = "REQUEST_DENIED";
    public static final String STATUS_INVALID_REQUEST = "INVALID_REQUEST";

    // Supported languages codes
    private static final String LANGUAGE_EN = "en";
    private static final String LANGUAGE_RU = "ru";

    private GooglePlacesAPI googlePlacesAPI;
    private static GooglePlacesAPIClient instance;
    private String language = LANGUAGE_EN;

    /**
     * Fabric method for creating static instance of
     * {@link GooglePlacesAPIClient}
     * @return GogglePlacesAPIClient instance
     */
    public static GooglePlacesAPIClient newInstance(){
        if (instance == null)
            instance = new GooglePlacesAPIClient();
        return instance;
    }

    /**
     * Public method for getting {@link PredictionModel}
     * for requested query
     * @param query String for which autocomplete is requested
     * @return request result which contains predictions for query
     */
    public PredictionModel getAutocompleteForQuery(String query){
        AsyncTask<String, Void, PredictionModel> at = new AsyncTask<String, Void, PredictionModel>() {
            @Override
            protected PredictionModel doInBackground(String... params) {
                try {
                    Response<PredictionModel> g = googlePlacesAPI.getAutocomplete
                            (API_KEY, params[0], "(cities)", language).execute();
                    return g.body();
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        };
        try {
            return at.execute(query).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    /**
     * Public method for getting {@link PlaceModel}
     * of requested place id
     * @param placeId Unique place text identifier
     * @return request result which contains information about place
     */
    public PlaceModel getInformationAboutPlace(String placeId){
        AsyncTask<String, Void, PlaceModel> at = new AsyncTask<String, Void, PlaceModel>() {
            @Override
            protected PlaceModel doInBackground(String... params) {
                try {
                    Response<PlaceModel> g = googlePlacesAPI
                            .getPlaceInformation(API_KEY, params[0]).execute();
                    return g.body();
                } catch (IOException e) {
                    Log.e(TAG, e.toString());
                }
                return null;
            }
        };
        try {
            return at.execute(placeId).get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    private GooglePlacesAPIClient() {
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
                break;
            case LANGUAGE_EN:
                language = LANGUAGE_EN;
                break;
        }
    }

    /**
     * Private method which creates {@link GooglePlacesAPI} instance
     * using {@link Retrofit}
     */
    private void createApi(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // TODO: add API key initialization
        googlePlacesAPI = retrofit.create(GooglePlacesAPI.class);
    }

    /**
     * Interface for requests to API Google Places
     */
    interface GooglePlacesAPI {
        /**
         * Method which gets autocomplete for requested query
         * @param apiKey valid API key of API Google Places
         * @param query String for which autocomplete is requested
         * @param placeType Specified place type {@see <a href="https://developers.google.com/
         * places/web-service/autocomplete?hl=ru#place_types">
         * API Google Places Autocomplete Place types</a>}
         * @param language Supported language
         * @return request result which contains predictions for query
         */
        @GET("/maps/api/place/autocomplete/json")
        Call<PredictionModel> getAutocomplete(
                @Query("key") String apiKey,
                @Query("input") String query,
                @Query("type") String placeType,
                @Query("language") String language);


        /**
         * Method which gets autocomplete for requested query
         * @param apiKey valid API key of API Google Places
         * @param placeId Unique place text identifier
         * @return request result which contains information about place
         */
        @GET("/maps/api/place/details/json")
        Call<PlaceModel> getPlaceInformation(
                @Query("key") String apiKey,
                @Query("placeid") String placeId);
    }
}
