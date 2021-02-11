package com.example.geolocindoor.ui;

import android.content.Context;

import androidx.annotation.NonNull;

import com.arxit.geolocindoor.common.entities.Event;
import com.example.geolocindoor.utils.DurationCalculator;

public class EventDisplay {

    private final Event event;
    private long distance;

    public EventDisplay(Event event) {
        this.event = event;
    }

    public Event getEvent() {
        return event;
    }

    public long getDistance() {
        return distance;
    }

    public void setDistance(long distance) {
        this.distance = distance;
    }

    String computeDuration(@NonNull Context context){
        return DurationCalculator.computeDuration(context, this.event.getStart(), this.event.getEnd());
    }
}
