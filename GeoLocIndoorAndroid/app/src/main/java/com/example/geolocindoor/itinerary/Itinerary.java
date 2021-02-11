package com.example.geolocindoor.itinerary;

import android.location.Location;

import androidx.annotation.NonNull;

import com.arxit.geolocindoor.common.entities.POI;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;

import java.util.Collections;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

public final class Itinerary {

    private final List<POI> points;

    Itinerary(@NonNull List<POI> points) {
        this.points = points;
    }

    public List<POI> getPoints() {
        return Collections.unmodifiableList(this.points);
    }

    public LatLngBounds computeBounds(@NonNull Location location){
        DoubleSummaryStatistics latStats = DoubleStream.concat(
                        this.points.stream().mapToDouble(POI::getLatitude),
                        Stream.of(location).mapToDouble(Location::getLatitude))
                .summaryStatistics();
        //DoubleSummaryStatistics lonStats = this.points.stream().mapToDouble(POI::getLongitude).summaryStatistics();
        DoubleSummaryStatistics lonStats = DoubleStream.concat(
                this.points.stream().mapToDouble(POI::getLongitude),
                Stream.of(location).mapToDouble(Location::getLongitude))
                .summaryStatistics();

        return LatLngBounds.from(latStats.getMax(), lonStats.getMax(), latStats.getMin(), lonStats.getMin());
    }
}
