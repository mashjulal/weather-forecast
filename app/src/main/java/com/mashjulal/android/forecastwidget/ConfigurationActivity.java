package com.mashjulal.android.forecastwidget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mashjulal.android.forecastwidget.googleplaces.GooglePlacesAPIClient;
import com.mashjulal.android.forecastwidget.googleplaces.Place;
import com.mashjulal.android.forecastwidget.googleplaces.PlaceModel;
import com.mashjulal.android.forecastwidget.googleplaces.PredictionModel;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.mashjulal.android.forecastwidget.ForecastApplication.bus;
import static com.mashjulal.android.forecastwidget.ForecastApplication.isConnectedToInternet;
import static com.mashjulal.android.forecastwidget.ForecastApplication.setLocationToPref;
import static com.mashjulal.android.forecastwidget.googleplaces.GooglePlacesAPIClient.STATUS_INVALID_REQUEST;
import static com.mashjulal.android.forecastwidget.googleplaces.GooglePlacesAPIClient.STATUS_OK;
import static com.mashjulal.android.forecastwidget.googleplaces.GooglePlacesAPIClient.STATUS_OVER_QUERY_LIMIT;
import static com.mashjulal.android.forecastwidget.googleplaces.GooglePlacesAPIClient.STATUS_REQUEST_DENIED;
import static com.mashjulal.android.forecastwidget.googleplaces.GooglePlacesAPIClient.STATUS_ZERO_RESULTS;


/**
 * Class for setting city for widget
 *
 * Class is used to configure city for selected
 * app widget using API Google Places
 */
public class ConfigurationActivity extends AppCompatActivity {

//    Saved instance bundle parameters
    private static final String STATE_QUERY = "query";

    private SearchView mSearchViewPlaces;
    private ProgressBar mLoading;
    private List<Place> mPlaces = new ArrayList<>();
    private PlaceAdapter mPlaceAdapter;
    private GooglePlacesAPIClient mGooglePlacesAPIAdapter;
    private int mAppWidgetId;
    private String mSavedQuery;

    public ConfigurationActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        setContentView(R.layout.activity_configuration);

        // Subscribe activity to bus events
        bus.register(this);

        LinearLayoutManager lm = new LinearLayoutManager(this);
        RecyclerView recycleViewPlaces = (RecyclerView) findViewById(R.id.recyclerview_configuration_places);
        recycleViewPlaces.setLayoutManager(lm);
        recycleViewPlaces.addItemDecoration(new DividerItemDecoration(this, lm.getOrientation()));

        mLoading = (ProgressBar) findViewById(R.id.progressBar_configuration);

        mPlaceAdapter = new PlaceAdapter(mPlaces);
        recycleViewPlaces.setAdapter(mPlaceAdapter);
        mGooglePlacesAPIAdapter = GooglePlacesAPIClient.newInstance();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        // If this activity was started with an intent without an app widget ID, finish it.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore search query
        mSavedQuery = savedInstanceState.getString(STATE_QUERY);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_configuration, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchViewPlaces = (SearchView) searchItem.getActionView();
        // Set request and update RecyclerView after every character
        mSearchViewPlaces.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchPlaces(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchPlaces(newText);
                return false;
            }
        });
        if (!TextUtils.isEmpty(mSavedQuery)) {
            searchItem.expandActionView();
            mSearchViewPlaces.setQuery(mSavedQuery, true);
            mSearchViewPlaces.clearFocus();
        }
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save search query
        outState.putString(STATE_QUERY, mSearchViewPlaces.getQuery().toString());
        // Save app widget ID
        outState.putInt(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        super.onSaveInstanceState(outState);
    }

    @Subscribe
    public void getMessage(PlaceSetEvent event) {
        setLocationToPref(this, mAppWidgetId, event.getLocation(), event.getCity());
        updateWidget();
        finish();
    }

    @Subscribe
    public void getMessage(String string){
        switch (string){
            case "close":
                finish();
                break;
        }
    }

    /**
     * Method for request autocomplete for entered query
     * @param query Entered string
     */
    void searchPlaces(final String query) {
        if (TextUtils.isEmpty(query))
            return;

        final TextView error = (TextView) findViewById(R.id.textview_configuration_error);
        error.setVisibility(View.GONE);
        if (!isConnectedToInternet(this)) {
            error.setVisibility(View.VISIBLE);
            error.setText(R.string.not_connected_to_the_internet);
            return;
        }

        mLoading.setVisibility(View.VISIBLE);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mPlaces.clear();
                PredictionModel pm = mGooglePlacesAPIAdapter
                        .getAutocompleteForQuery(query);
                switch (pm.getStatus()){
                    case STATUS_OK:
                        mPlaces.addAll(pm.getPredictions());
                        break;
                    case STATUS_ZERO_RESULTS:
                        error.setText(getString(R.string.no_places));
                        error.setVisibility(View.VISIBLE);
                        break;
                    case STATUS_OVER_QUERY_LIMIT:
                        error.setText(getString(R.string.over_limit));
                        error.setVisibility(View.VISIBLE);
                        break;
                    case STATUS_REQUEST_DENIED:
                        error.setText(String.format(Locale.getDefault(),
                                getString(R.string.unknown_error), STATUS_REQUEST_DENIED));
                        error.setVisibility(View.VISIBLE);
                        break;
                    case STATUS_INVALID_REQUEST:
                        error.setText(String.format(Locale.getDefault(),
                                getString(R.string.invalid_query), query));
                        error.setVisibility(View.VISIBLE);
                        break;
                }
                mPlaceAdapter.notifyDataSetChanged();
            }
        });
        mLoading.setVisibility(View.GONE);
    }

    /**
     * Method updates app widget after successful initialization
     */
    private void updateWidget(){
        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        ForecastWidget.updateAppWidget(this, appWidgetManager, mAppWidgetId);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
    }

    /**
     * Class for place setting event
     *
     * Class contains information about specified place:
     * - location
     * - city name
     */
    static class PlaceSetEvent {

        private PlaceModel.Result.Geometry.Location location;

        private String city;

        PlaceSetEvent(PlaceModel.Result.Geometry.Location location, String city) {
            this.location = location;
            this.city = city;
        }

        String getCity() {
            return city;
        }

        PlaceModel.Result.Geometry.Location getLocation() {
            return location;
        }
    }
}
