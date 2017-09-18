package com.mashjulal.android.forecastwidget;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mashjulal.android.forecastwidget.googleplaces.GooglePlacesAPIClient;
import com.mashjulal.android.forecastwidget.googleplaces.Place;
import com.mashjulal.android.forecastwidget.googleplaces.PlaceModel;

import java.util.List;

import static com.mashjulal.android.forecastwidget.ForecastApplication.bus;


/**
 * Class for {@link ConfigurationActivity}'s {@link RecyclerView} items
 *
 * Class-adapter define the way the items of {@link RecyclerView} display
 */
class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.ViewHolder> {

    private List<Place> mPlaces;

    PlaceAdapter(List<Place> places){
        mPlaces = places;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_place, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Place place = mPlaces.get(position);

        String city;
        String region = "";
        // If there is region in description then get it from value
        if(place.getTerms().size() > 1) {
            city = place.getTerms().get(0).getValue();
            int offset = place.getTerms().get(1).getOffset();
            region = place.getDescription().substring(offset);
        } else {
            city = place.getDescription();
        }

        holder.tvCity.setText(city);
        holder.tvRegion.setText(region);
        // If user selects item then get item's geographic coordinates
        // and save them in Shared Preferences
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().post(new Runnable() {
                    @Override
                    public void run() {
                        GooglePlacesAPIClient gpaa = GooglePlacesAPIClient.newInstance();
                        PlaceModel pal = gpaa.getInformationAboutPlace(place.getPlaceId());
                        PlaceModel.Result.Geometry.Location location =
                                pal.getResult().getGeometry().getLocation();
                        String city = place.getTerms().get(0).getValue();

                        bus.post(new ConfigurationActivity.PlaceSetEvent(location, city));
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPlaces.size();
    }

    /**
     * Class-container for item in {@link ConfigurationActivity}'s {@link RecyclerView}
     */
    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tvCity;
        TextView tvRegion;

        ViewHolder(View itemView) {
            super(itemView);

            tvCity = (TextView) itemView.findViewById(R.id.textview_itemplace_city);
            tvRegion = (TextView) itemView.findViewById(R.id.textview_itemplace_region);
        }
    }


}
