package com.example.geolocindoor.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.example.geolocindoor.R;

public class PreferenceUtils {

    public static boolean shouldDisplayBeacons(@NonNull Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.prefs_key_beacons), false);
    }

    public static boolean isTrilaterationMode(@NonNull Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.prefs_key_trilat), true);
    }

    public static long getSelectedBuildingId(@NonNull Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return Long.parseLong(prefs.getString(context.getString(R.string.prefs_key_selected_building), context.getString(R.string.prefs_defaultvalue_selected_building)));
    }

    public static void setSelectedBuildingId(@NonNull Context context, long id){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(context.getString(R.string.prefs_key_selected_building), String.valueOf(id)).apply();
    }

    public static int getDaycount(@NonNull Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return Integer.parseInt(prefs.getString(context.getString(R.string.prefs_key_daycount), context.getString(R.string.prefs_defaultvalue_daycount)));
    }

    public static boolean isAccessibility(@NonNull Context context){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.prefs_key_accessibility), false);
    }
}
