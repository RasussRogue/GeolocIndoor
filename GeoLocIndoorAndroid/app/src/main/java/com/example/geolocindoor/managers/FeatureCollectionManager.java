package com.example.geolocindoor.managers;

import android.location.Location;

import androidx.annotation.NonNull;

import com.arxit.geolocindoor.common.entities.Building;
import com.arxit.geolocindoor.common.entities.Floor;
import com.arxit.geolocindoor.common.entities.VisiblePOI;
import com.example.geolocindoor.itinerary.Itinerary;
import com.google.gson.JsonObject;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class FeatureCollectionManager {

    private FeatureCollectionManager(){}

    public static FeatureCollectionManager create(){
        return new FeatureCollectionManager();
    }

    private Floor checkFloorPresent(Building building, int floorLevel){
        Optional<Floor> floorOpt = building.getFloorByLevel(floorLevel);
        if (!floorOpt.isPresent()){
            throw new IllegalArgumentException("Can not find floor with level " + floorLevel);
        }
        return floorOpt.get();
    }

    public FeatureCollection beacons(@NonNull Building building, int floorLevel){

        Floor floor = this.checkFloorPresent(building, floorLevel);
        List<Feature> features = new ArrayList<>();
        floor.getBeacons().forEach(beacon -> features.add(Feature.fromGeometry(Point.fromLngLat(beacon.getLongitude(), beacon.getLatitude()))));
        return FeatureCollection.fromFeatures(features);
    }

    public FeatureCollection visiblePois(@NonNull Building building, int floorLevel, boolean excludeEventlessPois) {
        Floor floor = this.checkFloorPresent(building, floorLevel);
        List<Feature> features  = floor.getPois().stream()
                .filter(VisiblePOI.class::isInstance)
                .map(VisiblePOI.class::cast)
                .filter(poi -> excludeEventlessPois && Objects.requireNonNull(poi).getEvents().isEmpty())
                .map(p -> {
                    JsonObject props = new JsonObject();
                    props.addProperty("title", Objects.requireNonNull(p).getTitle());
                    props.addProperty("id", p.getId());
                    return Feature.fromGeometry(Point.fromLngLat(p.getLongitude(), p.getLatitude()), props);
                })
                .collect(Collectors.toList());
        return FeatureCollection.fromFeatures(features);
    }

    public FeatureCollection visiblePoisWithEvents(@NonNull Building building, int floorLevel) {
        Floor floor = this.checkFloorPresent(building, floorLevel);
        List<Feature> features  = floor.getPois().stream()
                .filter(VisiblePOI.class::isInstance)
                .map(VisiblePOI.class::cast)
                .filter(poi -> !Objects.requireNonNull(poi).getEvents().isEmpty())
                .map(p -> {
                    JsonObject props = new JsonObject();
                    props.addProperty("title", Objects.requireNonNull(p).getTitle());
                    props.addProperty("id", p.getId());
                    return Feature.fromGeometry(Point.fromLngLat(p.getLongitude(), p.getLatitude()), props);
                })
                .collect(Collectors.toList());
        return FeatureCollection.fromFeatures(features);
    }

    public FeatureCollection visiblePoisFromType(@NonNull Building building, int floorLevel, @NonNull String poiType){
        Floor floor = this.checkFloorPresent(building, floorLevel);
        List<Feature> features  = floor.getPois().stream()
                .filter(VisiblePOI.class::isInstance)
                .map(VisiblePOI.class::cast)
                .filter(poi -> Objects.requireNonNull(poi).getType().equals(poiType))
                .map(p -> {
                    JsonObject props = new JsonObject();
                    props.addProperty("title", Objects.requireNonNull(p).getTitle());
                    props.addProperty("id", p.getId());
                    return Feature.fromGeometry(Point.fromLngLat(p.getLongitude(), p.getLatitude()), props);
                })
                .collect(Collectors.toList());
        return FeatureCollection.fromFeatures(features);
    }

    public FeatureCollection bounds(@NonNull Building building, int floorLevel){

        Floor floor = this.checkFloorPresent(building, floorLevel);
        List<Feature> features = new ArrayList<>();
        floor.getBounds().forEach(bound -> {
            List<List<Point>> lists = new ArrayList<>();
            lists.add(Arrays.stream(bound.getGeometry().getCoordinates()).map(c -> Point.fromLngLat(c.x, c.y)).collect(Collectors.toList()));
            features.add(Feature.fromGeometry(Polygon.fromLngLats(lists)));
        });
        return FeatureCollection.fromFeatures(features);
    }

    public FeatureCollection walls(@NonNull Building building, int floorLevel){

        Floor floor = this.checkFloorPresent(building, floorLevel);
        List<Feature> features = new ArrayList<>();
        floor.getWalls().forEach(walls -> {
            List<Point> points = Arrays.stream(walls.getGeometry().getCoordinates()).map(c -> Point.fromLngLat(c.x, c.y)).collect(Collectors.toList());
            features.add(Feature.fromGeometry(LineString.fromLngLats(points)));
        });
        return FeatureCollection.fromFeatures(features);
    }

    public FeatureCollection itinerary(@NonNull Itinerary itinerary, int floorLevel, @NonNull Location position, boolean joinPosition){

        //Stream<Point> positionStream = Stream.of(position).map(p -> Point.fromLngLat(position.getLongitude(), position.getLatitude()));
        Stream<Point> pointsStream = itinerary.getPoints().stream()
                .filter(poi -> poi.getFloor().getLevel() == floorLevel)
                .map(poi -> Point.fromLngLat(poi.getLongitude(), poi.getLatitude()));

        List<Point> points = joinPosition ?
                Stream.concat(Stream.of(position).map(p -> Point.fromLngLat(position.getLongitude(), position.getLatitude())), pointsStream).collect(Collectors.toList()) :
                pointsStream.collect(Collectors.toList());

        List<Feature> features = new ArrayList<>();
        features.add(Feature.fromGeometry(LineString.fromLngLats(points)));
        return FeatureCollection.fromFeatures(features);
    }
}
