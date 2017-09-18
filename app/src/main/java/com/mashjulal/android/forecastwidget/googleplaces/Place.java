package com.mashjulal.android.forecastwidget.googleplaces;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


/**
 * Class for requested place information
 *
 * Class is used to contain information about place:
 * - description
 * - id
 * - place_id
 * - terms
 */
public class Place {

    /**
     * Readable result name
     */
    @SerializedName("description")
    @Expose
    private String description;

    /**
     * Unique permanent place identifier
     */
    @SerializedName("id")
    @Expose
    private String id;

    /**
     * Unique place text identifier
     */
    @SerializedName("place_id")
    @Expose
    private String placeId;


    /**
     * An array of search words defining each section of the displayed description
     */
    @SerializedName("terms")
    @Expose
    private List<SearchWord> terms;

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public String getPlaceId() {
        return placeId;
    }

    public List<SearchWord> getTerms() {
        return terms;
    }

    /**
     * Class for description sections
     *
     * Class is used to display each section of description
     */
    public class SearchWord {

        /**
         * Section description
         */
        @SerializedName("value")
        @Expose
        private String value;

        /**
         * The initial position of section in description
         */
        @SerializedName("offset")
        @Expose
        private int offset;

        public String getValue() {
            return value;
        }

        public int getOffset() {
            return offset;
        }
    }
}
