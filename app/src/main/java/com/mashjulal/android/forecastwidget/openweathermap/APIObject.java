package com.mashjulal.android.forecastwidget.openweathermap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;


/**
 * Class-container for API OpenWeatherMap objects
 */
public class APIObject {
    /**
     * Class for forecast information
     *
     * Class is used to contain information about
     * forecast for requested city at specific time
     * - main (temperature, pressure, humidity etc.)
     * - weather
     * - wind
     */
    public class Forecast {

        /**
         * Main forecast information for specific time
         */
        @SerializedName("main")
        @Expose
        private Main main;

        /**
         * Weather information for specific time
         */
        @SerializedName("weather")
        @Expose
        private List<Weather> weather;

        /**
         * Wind information for specific time
         */
        @SerializedName("wind")
        @Expose
        private Wind wind;

        public Main getMain() {
            return main;
        }

        public List<Weather> getWeather() {
            return weather;
        }

        public Wind getWind() {
            return wind;
        }
    }

    /**
     * Class for main forecast information
     *
     * Class is used to contain main information
     * about weather at specific time:
     * - temperature
     * - pressure
     * - humidity
     */
    public class Main {
        /**
         * Temperature at specific time
         */
        @SerializedName("temp")
        @Expose
        private float temp;

        /**
         * Atmospheric pressure at specific time
         */
        @SerializedName("pressure")
        @Expose
        private float pressure;

        /**
         * Humidity at specific time
         */
        @SerializedName("humidity")
        @Expose
        private float humidity;


        public float getTemp() {
            return temp;
        }

        public float getPressure() {
            return pressure;
        }

        public float getHumidity() {
            return humidity;
        }
    }

    /**
     * Class for weather information
     *
     * Class is used to contain information about current weather situation
     */
    public class Weather {

        /**
         * Weather icon id
         */
        @SerializedName("icon")
        @Expose
        private String icon;

        public String getIcon() {
            return icon;
        }
    }

    /**
     * Class for information about wind
     *
     * Class is used to contain information about wind:
     * - speed
     * - degree
     */
    public class Wind {

        /**
         * Wind speed
         */
        @SerializedName("speed")
        @Expose
        private float speed;

        /**
         * Wind degree (0-360)
         */
        @SerializedName("deg")
        @Expose
        private float deg;

        public float getSpeed() {
            return speed;
        }

        public float getDeg() {
            return deg;
        }
    }
}
