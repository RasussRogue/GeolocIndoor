package com.example.geolocindoor.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;

import com.example.geolocindoor.R;

public class DrawableUtils {

    public static Drawable getDrawableForPoiType(@NonNull Context context, @NonNull String poiType){
        try {
            int drawableId = context.getResources().getIdentifier("ico_" + poiType, "drawable", context.getPackageName());
            return context.getDrawable(drawableId);
        } catch (Resources.NotFoundException e){
            return context.getDrawable(R.drawable.ico_info_default);
        }
    }
}
