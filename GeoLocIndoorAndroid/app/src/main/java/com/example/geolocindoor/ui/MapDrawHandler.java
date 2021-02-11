package com.example.geolocindoor.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.arxit.geolocindoor.common.entities.Building;
import com.arxit.geolocindoor.common.entities.VisiblePOI;
import com.example.geolocindoor.R;
import com.example.geolocindoor.itinerary.Itinerary;
import com.example.geolocindoor.managers.FeatureCollectionManager;
import com.example.geolocindoor.utils.DrawableUtils;
import com.example.geolocindoor.utils.PreferenceUtils;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.style.expressions.Expression;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.layers.TransitionOptions;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.mapbox.mapboxsdk.style.expressions.Expression.stop;
import static com.mapbox.mapboxsdk.style.expressions.Expression.zoom;

class MapDrawHandler {

    private final Context context;
    private final FeatureCollectionManager fcManager;

    private final GeoJsonSource userPositionSource;
    private final static String USER_POSITION_SOURCE_ID = "user-position-src";
    private final static String USER_POSITION_LAYER_ID = "user-position-layer";
    private final static String USER_ACCURACY_LAYER_ID = "user-accuracy-layer";

    private final GeoJsonSource beaconSource;
    private final static String BEACON_SOURCE_ID = "beacon-src";
    private final static String BEACON_LAYER_ID = "beacon-layer";

    private final GeoJsonSource boundsSource;
    private final static String BOUNDS_SOURCE_ID = "bounds-src";
    private final static String BOUNDS_LAYER_ID = "bounds-layer";

    private final GeoJsonSource wallsSource;
    private final static String WALLS_SOURCE_ID = "walls-src";
    private final static String WALLS_LAYER_ID = "walls-layer";

    private final GeoJsonSource itinerarySource;
    private final static String ITINERARY_SOURCE_ID = "itinerary-src";
    private final static String ITINERARY_IN_LAYER_ID = "itinerary-in-layer";
    private final static String ITINERARY_OUT_LAYER_ID = "itinerary-out-layer";

    private final GeoJsonSource visiblePoisSource;
    private final static String VISIBLEPOI_SOURCE_ID = "visiblepoi-src";
    private final static String VISIBLEPOI_LAYER_ID = "visiblepoi-layer";

    private final GeoJsonSource visiblePoisSourceWithEvents;
    private final static String VISIBLEPOI_WITH_EVENTS_SOURCE_ID = "visiblepoi-withevents-src";
    private final static String VISIBLEPOI_WITH_EVENTS_LAYER_ID = "visiblepoi-withevents-layer";
    private final static String VISIBLE_POI_WITH_EVENTS_POINTER = "visiblepoi-withevents-pointer";

    private final List<String> iconLayerTypes;
    private final Map<String, String> iconSourceIds;
    private final Map<String, String> iconLayerIds;
    private final Map<String, String> iconPointerIds;
    private final Map<String, GeoJsonSource> iconLayerSources;


    MapDrawHandler(@NonNull Context context, @NonNull List<String> iconLayerTypes){

        this.context = context;
        this.fcManager = FeatureCollectionManager.create();

        this.userPositionSource = new GeoJsonSource(USER_POSITION_SOURCE_ID, FeatureCollection.fromFeatures(new ArrayList<>()));
        this.beaconSource = new GeoJsonSource(BEACON_SOURCE_ID, FeatureCollection.fromFeatures(new ArrayList<>()));
        this.wallsSource = new GeoJsonSource(WALLS_SOURCE_ID, FeatureCollection.fromFeatures(new ArrayList<>()));
        this.boundsSource = new GeoJsonSource(BOUNDS_SOURCE_ID, FeatureCollection.fromFeatures(new ArrayList<>()));
        this.itinerarySource = new GeoJsonSource(ITINERARY_SOURCE_ID, FeatureCollection.fromFeatures(new ArrayList<>()));
        this.visiblePoisSource = new GeoJsonSource(VISIBLEPOI_SOURCE_ID, FeatureCollection.fromFeatures(new ArrayList<>()));
        this.visiblePoisSourceWithEvents = new GeoJsonSource(VISIBLEPOI_WITH_EVENTS_SOURCE_ID, FeatureCollection.fromFeatures(new ArrayList<>()));

        this.iconLayerTypes = iconLayerTypes;
        this.iconSourceIds = iconLayerTypes.stream().collect(Collectors.toMap(Function.identity(), type -> type + "-src"));
        this.iconLayerIds = iconLayerTypes.stream().collect(Collectors.toMap(Function.identity(), type -> type + "-layers"));
        this.iconPointerIds = iconLayerTypes.stream().collect(Collectors.toMap(Function.identity(), type -> type + "-pointer"));
        this.iconLayerSources = iconLayerTypes.stream().collect(Collectors.toMap(Function.identity(),
                type -> new GeoJsonSource(this.iconSourceIds.get(type), FeatureCollection.fromFeatures(new ArrayList<>()))));
    }

    void init(@NonNull Style style){

        style.addImage(VISIBLE_POI_WITH_EVENTS_POINTER, Objects.requireNonNull(ContextCompat.getDrawable(this.context, R.drawable.ic_logo_geoloc_logo)));
        this.iconPointerIds.forEach((type, pointerId) -> style.addImage(pointerId, DrawableUtils.getDrawableForPoiType(this.context, type)));

        style.addSource(boundsSource);
        style.addLayer(new FillLayer(BOUNDS_LAYER_ID, BOUNDS_SOURCE_ID).withProperties(
                PropertyFactory.fillColor(Color.rgb(240, 240, 240))));

        style.addSource(wallsSource);
        style.addLayer(new LineLayer(WALLS_LAYER_ID, WALLS_SOURCE_ID).withProperties(
                PropertyFactory.lineColor(Color.BLACK),
                PropertyFactory.lineWidth(2.5f)));

        style.addSource(itinerarySource);
        style.addLayer(new LineLayer(ITINERARY_OUT_LAYER_ID, ITINERARY_SOURCE_ID).withProperties(
                PropertyFactory.lineColor(Color.BLUE),
                PropertyFactory.lineWidth(4f)));

        style.addLayer(new LineLayer(ITINERARY_IN_LAYER_ID, ITINERARY_SOURCE_ID).withProperties(
                PropertyFactory.lineColor(Color.WHITE),
                PropertyFactory.lineWidth(0.5f)));

        if(PreferenceUtils.shouldDisplayBeacons(this.context)){
            style.addSource(beaconSource);
            style.addLayer(new CircleLayer(BEACON_LAYER_ID, BEACON_SOURCE_ID).withProperties(
                    PropertyFactory.circleColor(Color.RED),
                    PropertyFactory.circleStrokeColor(Color.BLACK),
                    PropertyFactory.circleRadius(5f)));
        }

        style.addSource(visiblePoisSource);
        style.addLayer(new SymbolLayer(VISIBLEPOI_LAYER_ID, VISIBLEPOI_SOURCE_ID).withProperties(
                PropertyFactory.textField(Expression.get("title")),
                PropertyFactory.textSize(Expression.interpolate(Expression.exponential(19), Expression.zoom(),
                        Expression.stop(15, 0),
                        Expression.stop(17, 12f),
                        Expression.stop(19, 16f)
                )),
                PropertyFactory.textColor(Color.BLACK),
                PropertyFactory.textJustify(Property.TEXT_JUSTIFY_AUTO),
                PropertyFactory.textRadialOffset(0.5f)));

        style.addSource(visiblePoisSourceWithEvents);
        style.addLayer(new SymbolLayer(VISIBLEPOI_WITH_EVENTS_LAYER_ID, VISIBLEPOI_WITH_EVENTS_SOURCE_ID).withProperties(
                PropertyFactory.iconImage(VISIBLE_POI_WITH_EVENTS_POINTER),
                PropertyFactory.iconAllowOverlap(true),
                PropertyFactory.iconIgnorePlacement(true)));

        this.iconLayerSources.forEach((type, source) -> {
            style.addSource(source);
            style.addLayer(new SymbolLayer(this.iconLayerIds.get(type), this.iconSourceIds.get(type)).withProperties(
                    PropertyFactory.iconImage(this.iconPointerIds.get(type)),
                    PropertyFactory.iconAllowOverlap(true),
                    PropertyFactory.iconIgnorePlacement(true)));
        });

        style.addSource(this.userPositionSource);
        style.addLayer(new CircleLayer(USER_POSITION_LAYER_ID, USER_POSITION_SOURCE_ID).withProperties(
                        PropertyFactory.circleColor(ContextCompat.getColor(this.context, R.color.colorPrimary)),
                        PropertyFactory.circleBlur(0.2f),
                        PropertyFactory.circleRadius(7f)));
    }

    void update(Building building, int displayedFloorLevel){

        this.beaconSource.setGeoJson(fcManager.beacons(building, displayedFloorLevel));
        this.boundsSource.setGeoJson(fcManager.bounds(building, displayedFloorLevel));
        this.wallsSource.setGeoJson(fcManager.walls(building, displayedFloorLevel));
        this.visiblePoisSource.setGeoJson(fcManager.visiblePois(building, displayedFloorLevel, true));
        this.visiblePoisSourceWithEvents.setGeoJson(fcManager.visiblePoisWithEvents(building, displayedFloorLevel));

        this.iconLayerSources.forEach((type, source) -> source.setGeoJson(this.fcManager.visiblePoisFromType(building, displayedFloorLevel, type)));
    }

    void updateUserPosition(double longitude, double latitude){
        this.userPositionSource.setGeoJson(Point.fromLngLat(longitude, latitude));
    }

    void updateItinerary(Itinerary it, int levelToDisplay, Location position, boolean joinPositionToItinerary){
        this.itinerarySource.setGeoJson(this.fcManager.itinerary(it, levelToDisplay, position, joinPositionToItinerary));
    }

    void clearItinerary(){
        this.itinerarySource.setGeoJson(FeatureCollection.fromFeatures(new ArrayList<>()));
    }

    private CircleLayer userAccuracyLayer;

    private final static double ZOOM_15_RATIO = 2.389;
    private final static double ZOOM_16_RATIO = 1.194;
    private final static double ZOOM_17_RATIO = 0.597;
    private final static double ZOOM_18_RATIO = 0.299;
    private final static double ZOOM_19_RATIO = 0.149;
    private final static double ZOOM_20_RATIO = 0.075;
    private final static double ZOOM_21_RATIO = 0.037;
    private final static double ZOOM_22_RATIO = 0.019;

    void initUserAccuracyLayer(@NonNull Style style, @NonNull Location location){
        this.userAccuracyLayer = new CircleLayer(MapDrawHandler.USER_ACCURACY_LAYER_ID, MapDrawHandler.USER_POSITION_SOURCE_ID).withProperties(
                PropertyFactory.circleColor(ContextCompat.getColor(this.context.getApplicationContext(), R.color.colorPrimary)),
                PropertyFactory.circleOpacity((float) 0.3),
                PropertyFactory.circleRadius(Expression.interpolate(Expression.exponential(15), zoom(),
                        stop(15, location.getAccuracy() / ZOOM_15_RATIO),
                        stop(16, location.getAccuracy() / ZOOM_16_RATIO),
                        stop(17, location.getAccuracy() / ZOOM_17_RATIO),
                        stop(18, location.getAccuracy() / ZOOM_18_RATIO),
                        stop(19, location.getAccuracy() / ZOOM_19_RATIO),
                        stop(20, location.getAccuracy() / ZOOM_20_RATIO),
                        stop(21, location.getAccuracy() / ZOOM_21_RATIO),
                        stop(22, location.getAccuracy() / ZOOM_22_RATIO)
                ))
        );
        this.userAccuracyLayer.setCircleRadiusTransition(new TransitionOptions(1000, 0));
        style.addLayer(this.userAccuracyLayer);
    }

    void updateUserAccuracyLayer(@NonNull Location location){
        this.userAccuracyLayer.setProperties(
                PropertyFactory.circleRadius(Expression.interpolate(Expression.exponential(15), zoom(),
                        stop(15, location.getAccuracy() / ZOOM_15_RATIO),
                        stop(16, location.getAccuracy() / ZOOM_16_RATIO),
                        stop(17, location.getAccuracy() / ZOOM_17_RATIO),
                        stop(18, location.getAccuracy() / ZOOM_18_RATIO),
                        stop(19, location.getAccuracy() / ZOOM_19_RATIO),
                        stop(20, location.getAccuracy() / ZOOM_20_RATIO),
                        stop(21, location.getAccuracy() / ZOOM_21_RATIO),
                        stop(22, location.getAccuracy() / ZOOM_22_RATIO))));
    }

    void handleClick(@NonNull MapboxMap mapboxMap, @NonNull RectF envelope, @NonNull Consumer<Feature> c, @NonNull Runnable whenNoFeatureFound){
        Optional<Feature> fOpt = Stream.concat(
                Stream.concat(mapboxMap.queryRenderedFeatures(envelope, VISIBLEPOI_LAYER_ID).stream(),
                        mapboxMap.queryRenderedFeatures(envelope, VISIBLEPOI_WITH_EVENTS_LAYER_ID).stream()),
                this.iconSourceIds.values().stream().flatMap(layerId -> mapboxMap.queryRenderedFeatures(envelope, layerId).stream())
        ).findFirst();
        fOpt.ifPresent(c);
        if (!fOpt.isPresent()){
            whenNoFeatureFound.run();
        }
    }
}
