package com.mashjulal.android.forecastwidget.googleplaces;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


/**
 * Class for place information from API Google Places
 *
 * Class is used to contain information about request:
 * - result
 * - status
 */
public class PlaceModel {

    /**
     * Detailed information about the requested place
     */
    @SerializedName("result")
    @Expose
    private Result result;

    /**
     * Data about the status of the request and debugging information,
     * which allows to find out the cause of the failure
     */
    @SerializedName("status")
    @Expose
    private String status;

    public Result getResult() {
        return result;
    }

    public String getStatus() {
        return status;
    }

    /**
     * Class for information about requested place
     *
     * Class is used to contain detailed information about the requested place:
     * - geometry
     */
    public class Result {

        /**
         * Geographic information about requested place
         */
        @SerializedName("geometry")
        @Expose
        private Geometry geometry;

        public Geometry getGeometry() {
            return geometry;
        }

        /**
         * Class for geographic information of requested place
         *
         * Class is used to contain geographic information of requested place
         * - location
         */
        public class Geometry {

            /**
             * Information about location of requested place
             */
            @SerializedName("location")
            @Expose
            private Location location;

            public Location getLocation() {
                return location;
            }

            /**
             * Class for requested place geographic location
             *
             * Class is used to contain geographic information requested place
             * - latitude
             * - longitude
             */
            public class Location {

                /**
                 * Requested place latitude
                 */
                @SerializedName("lat")
                @Expose
                private double lat;

                /**
                 * Requested place longitude
                 */
                @SerializedName("lng")
                @Expose
                private double lng;

                public double getLat() {
                    return lat;
                }

                public double getLng() {
                    return lng;
                }
            }
        }
    }
}
