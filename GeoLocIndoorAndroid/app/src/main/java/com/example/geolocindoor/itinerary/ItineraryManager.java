package com.example.geolocindoor.itinerary;

import android.location.Location;

import androidx.annotation.NonNull;

import com.arxit.geolocindoor.common.entities.Building;
import com.arxit.geolocindoor.common.entities.Floor;
import com.arxit.geolocindoor.common.entities.POI;
import com.arxit.geolocindoor.common.entities.VisiblePOI;
import com.example.geolocindoor.exception.NoItineraryFoundException;
import com.example.geolocindoor.utils.DistanceCalculator;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import es.usc.citius.hipster.algorithm.Algorithm;
import es.usc.citius.hipster.algorithm.Hipster;
import es.usc.citius.hipster.graph.GraphSearchProblem;
import es.usc.citius.hipster.graph.HipsterGraph;
import es.usc.citius.hipster.model.problem.SearchProblem;

public final class ItineraryManager {

    private Building building;
    private HipsterGraph<POI, Double> graph;

    public static final String VISIBLEPOI_TARGET_KEY = "VisiblePOI_target";

    private ItineraryManager(){}

    private static ItineraryManager instance;

    public static ItineraryManager getInstance(){
        if (instance == null){
            instance = new ItineraryManager();
        }
        return instance;
    }

    public void setBuilding(@NonNull Building building, boolean accessibility){
        this.building = building;
        this.graph = accessibility ? this.building.getAccessibilityGraph() : this.building.getGraph();
    }

    private void checkInitializedBuilding(){
        if (this.building == null){
            throw new IllegalStateException("Building has not been set yet");
        }
    }

    public void setAccessibility(boolean accessibility){
        this.checkInitializedBuilding();
        this.graph = accessibility ? this.building.getAccessibilityGraph() : this.building.getGraph();
    }

    private Algorithm.SearchResult computeResult(@NonNull POI start, @NonNull POI target){
        this.checkInitializedBuilding();
        SearchProblem problem = GraphSearchProblem.startingFrom(start).in(this.graph).takeCostsFromEdges().build();
        return Hipster.createAStar(problem).search(target);
    }

    private Algorithm.SearchResult computeResult(@NonNull LatLng position, int floorLevel, @NonNull POI target){
        Optional<POI> closest = this.findClosestPOI(position, floorLevel);
        if (!closest.isPresent()){
            throw new NoItineraryFoundException("Could not find closest POI from location " + position + " in level " + floorLevel);
        }
        return this.computeResult(closest.get(), target);
    }

    private Itinerary computeItinerary(@NonNull POI start, @NonNull POI target){
        Algorithm.SearchResult result = this.computeResult(start, target);
        if (result.getOptimalPaths().isEmpty()){
            throw new NoItineraryFoundException("No itinerary found");
        }
        return new Itinerary((List<POI>) result.getOptimalPaths().get(0));
    }

    public Itinerary computeItinerary(@NonNull Location position, int floorLevel, @NonNull POI target){
        Optional<POI> closest = this.findClosestPOI(new LatLng(position.getLatitude(), position.getLongitude()), floorLevel);
        if (!closest.isPresent()){
            throw new NoItineraryFoundException("Could not find closest POI from location " + position + " in level " + floorLevel);
        }
        return this.computeItinerary(closest.get(), target);
    }

    public Itinerary computeItinerary(@NonNull LatLng position, int floorLevel, @NonNull POI target){
        Optional<POI> closest = this.findClosestPOI(position, floorLevel);
        if (!closest.isPresent()){
            throw new NoItineraryFoundException("Could not find closest POI from location " + position + " in level " + floorLevel);
        }
        return this.computeItinerary(closest.get(), target);
    }

    private Optional<POI> findClosestPOI(LatLng position, int floorLevel){
        this.checkInitializedBuilding();
        Optional<Floor> floorOpt = this.building.getFloorByLevel(floorLevel);
        if (!floorOpt.isPresent()){
            throw new IllegalArgumentException("Floor level does not exist : " + floorLevel);
        }
        Floor floor = floorOpt.get();
        return floor.getPois().stream().min(Comparator.comparing(poi -> new LatLng(poi.getLatitude(), poi.getLongitude()).distanceTo(position)));
    }

    private static final int ARRIVED_DISTANCE = 4;

    public boolean isArrived(@NonNull VisiblePOI target, @NonNull Location location, int floorLevel){
        return target.getFloor().getLevel() == floorLevel
                && DistanceCalculator.distance(target, location) < ARRIVED_DISTANCE;
    }

}
