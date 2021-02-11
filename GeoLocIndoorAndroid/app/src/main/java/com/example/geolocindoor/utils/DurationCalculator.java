package com.example.geolocindoor.utils;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.geolocindoor.R;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class DurationCalculator {

    public static String computeDuration(@NonNull Context context, @NonNull ZonedDateTime start, @NonNull ZonedDateTime end){
        long diffInMinutes = start.until(end, ChronoUnit.MINUTES);
        if (diffInMinutes < 60){
            return String.format(context.getString(R.string.time_format_minute), diffInMinutes);
        }
        if (diffInMinutes < 24 * 60){
            return String.format(context.getString(R.string.time_format_hours), diffInMinutes/60);
        }
        return String.format(context.getString(R.string.time_format_days), diffInMinutes/(24*60));
    }
}
