package com.mashjulal.android.forecastwidget;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Created by Master on 24.07.2017.
 */

public class ForecastModelUnitTest {

    private int[] generateHoursArray(int startValue) {
        int[] array = new int[40];
        for (int i = 0; i < 40; i++)
            array[i] = startValue + 3 * i;
        return array;
    }

    @Test
    public void hours_isCorrect() {
        for (int i = 0; i < 24; i++) {
            getNextDay1Index_isCorrect(i);
            getNextDay2Index_isCorrect(i);
            getNextDay3Index_isCorrect(i);
        }
    }

    public void getNextDay1Index_isCorrect(int hour) {
        hour -= hour % 3;
        int[] hours = generateHoursArray(hour);
        int index = (24 - hour) / 3;
        assertEquals("hour - " + hour, 8 - (hour / 3), index);
        assertEquals("hour - " + hour,24, hours[index]);
        assertEquals("hour - " + hour,36, hours[index+4]);
        int tempMin = 1000, tempMax = -1000;
        for (int h : Arrays.copyOfRange(hours, index, index + 8)){
            if (h > tempMax)
                tempMax = h;
            if (h < tempMin)
                tempMin = h;
        }
        assertEquals("getNextDay1Index_isCorrect: hour - " + hour,24, tempMin);
        assertEquals("getNextDay1Index_isCorrect: hour - " + hour,45, tempMax);
    }

    public void getNextDay2Index_isCorrect(int hour) {
        hour -= hour % 3;
        int[] hours = generateHoursArray(hour);
        int index = (48 - hour) / 3;
        assertEquals("getNextDay2Index_isCorrect: hour - " + hour, 16 - (hour / 3), index);
        assertEquals("getNextDay2Index_isCorrect: hour - " + hour, 48, hours[index]);
        assertEquals("getNextDay2Index_isCorrect: hour - " + hour, 60, hours[index+4]);
        int tempMin = 1000, tempMax = -1000;
        for (int h : Arrays.copyOfRange(hours, index, index + 8)){
            if (h > tempMax)
                tempMax = h;
            if (h < tempMin)
                tempMin = h;
        }
        assertEquals("getNextDay2Index_isCorrect: hour - " + hour, 48, tempMin);
        assertEquals("getNextDay2Index_isCorrect: hour - " + hour, 69, tempMax);
    }

    public void getNextDay3Index_isCorrect(int hour) {
        hour -= hour % 3;
        int[] hours = generateHoursArray(hour);
        int index = (72 - hour) / 3;
        assertEquals("getNextDay3Index_isCorrect: hour - " + hour, 24 - (hour / 3), index);
        assertEquals("getNextDay3Index_isCorrect: hour - " + hour, 72, hours[index]);
        assertEquals("getNextDay3Index_isCorrect: hour - " + hour, 84, hours[index+4]);
        int tempMin = 1000, tempMax = -1000;
        for (int h : Arrays.copyOfRange(hours, index, index + 8)){
            if (h > tempMax)
                tempMax = h;
            if (h < tempMin)
                tempMin = h;
        }
        assertEquals("getNextDay3Index_isCorrect: hour - " + hour, 72, tempMin);
        assertEquals("getNextDay3Index_isCorrect: hour - " + hour, 93, tempMax);
    }
}
