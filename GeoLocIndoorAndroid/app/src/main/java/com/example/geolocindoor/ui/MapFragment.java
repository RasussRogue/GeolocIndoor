package com.example.geolocindoor.ui;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arxit.geolocindoor.common.entities.Building;
import com.arxit.geolocindoor.common.entities.Event;
import com.arxit.geolocindoor.common.entities.Floor;
import com.arxit.geolocindoor.common.entities.VisiblePOI;
import com.example.geolocindoor.R;
import com.example.geolocindoor.exception.NoItineraryFoundException;
import com.example.geolocindoor.itinerary.Itinerary;
import com.example.geolocindoor.itinerary.ItineraryManager;
import com.example.geolocindoor.location.LocationManager;
import com.example.geolocindoor.managers.BuildingManager;
import com.example.geolocindoor.managers.EventManager;
import com.example.geolocindoor.managers.RetrieveBuildingTask;
import com.example.geolocindoor.managers.RetrieveEventsTask;
import com.example.geolocindoor.utils.DistanceCalculator;
import com.example.geolocindoor.utils.PreferenceUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.stream.Collectors;

import timber.log.Timber;

public class MapFragment extends Fragment implements OnMapReadyCallback, MapboxMap.OnMapClickListener, MapboxMap.OnCameraMoveStartedListener, MapboxMap.OnMoveListener, MapboxMap.OnCameraMoveListener, MapboxMap.OnCameraIdleListener {

    private static Logger LOGGER = Logger.getLogger("logger");
    private MapView mapView;
    private MapboxMap mapBoxMap;
    private MapDrawHandler mapDrawHandler;

    private LatLng currentPosition;
    private Location lastLocation;
    private int currentFloorLevel;
    private long currentTargetPOI;
    private Itinerary currentItinerary;

    private ProgressBar progressBar;
    private BottomSheetBehavior bottomSheetBehavior;
    private NumberPicker floorPicker;

    private LocationManager lManager;
    private BuildingManager buildingManager;
    private EventManager eventManager;
    private ValueAnimator animator;
    private Building building;
    private List<Event> events;
    private ItineraryManager iManager;
    private FloatingActionButton floatingActionButton;

    private VisiblePOI lastFocus = null;

    private CustomSearchView cSearchView;
    private TextView bottomSheetTitle;
    private Button bottomSheetItineraryButton;
    private Button bottomSheetCentrateButton;
    private ImageView bottomSheetFloorDiffImageView;
    private TextView bottomSheetDistanceTextView;
    private TextView bottomSheetUpcomingEventsTextView;
    private RecyclerView bottomSheetEventRecyclerView;
    private BottomSheetEventsAdapter bottomSheetEventsAdapter;
    private List<Event> bottomSheetEvents;
    private int formerBottomSheetState;

    private MapViewMode mapViewMode;

    private enum MapViewMode {
        TRACKING,
        EXPLORATION
    }

    private ImageView ivIndicatorGPS;
    private ImageView ivIndicatorNetwork;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.setHasOptionsMenu(true);
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        //Number picker
        this.floorPicker = root.findViewById(R.id.floorPicker);
        root.findViewById(R.id.floorPickerContainer).bringToFront();
        this.floorPicker.setEnabled(false);

        this.floatingActionButton = root.findViewById(R.id.center_button);
        this.floatingActionButton.setOnClickListener(view -> {
            this.mapViewMode = MapViewMode.TRACKING;
            this.updateFloor(this.currentFloorLevel);
            this.floorPicker.setValue(this.currentFloorLevel);
            this.moveCameraToCurrentPositionWithZoom(20);
        });
        this.floatingActionButton.bringToFront();

        //Bottom sheet
        LinearLayout bottomSheet = Objects.requireNonNull(this.getActivity()).findViewById(R.id.bottomSheetLayout);
        this.bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        this.bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @SuppressLint("SwitchIntDef")
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState){
                    case BottomSheetBehavior.STATE_HIDDEN:
                        moveDownFloatingActionButton();
                        setFormerCurrentBottomState(newState);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        if (formerBottomSheetState != BottomSheetBehavior.STATE_EXPANDED){
                            moveUpFloatingActionButton();
                        }
                        setFormerCurrentBottomState(newState);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        setFormerCurrentBottomState(newState);
                        break;
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) { }
        });
        this.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        this.progressBar = root.findViewById(R.id.progressBar);
        this.ivIndicatorGPS = root.findViewById(R.id.ivIndicatorGPS);
        this.ivIndicatorGPS.bringToFront();
        this.ivIndicatorNetwork = root.findViewById(R.id.ivIndicatorNetwork);
        this.ivIndicatorNetwork.bringToFront();
        this.ivIndicatorNetwork.setOnClickListener(view -> {
            this.retrieveBuilding(root.getContext());
            this.retrieveEventsForSearchView();
        });

        this.mapViewMode = MapViewMode.TRACKING;

        //Mapbox
        this.mapView = root.findViewById(R.id.mapView);
        this.mapView.onCreate(savedInstanceState);
        this.mapView.getMapAsync(this);
        this.mapDrawHandler = new MapDrawHandler(root.getContext(), Arrays.asList(root.getContext().getResources().getStringArray(R.array.iconLayers)));

        //Managers
        this.lManager = LocationManager.create(root.getContext());
        this.buildingManager = BuildingManager.create(root.getContext());
        this.eventManager = EventManager.create(root.getContext());
        this.iManager = ItineraryManager.getInstance();

        this.retrieveBuilding(root.getContext());

        this.bottomSheetTitle = bottomSheet.findViewById(R.id.bottomSheetTitle);
        this.bottomSheetItineraryButton = bottomSheet.findViewById(R.id.bottomSheetItineraryButton);
        this.bottomSheetCentrateButton = bottomSheet.findViewById(R.id.bottomSheetCentrateButton);
        this.bottomSheetFloorDiffImageView = bottomSheet.findViewById(R.id.ivFloorDifference);
        this.bottomSheetDistanceTextView = bottomSheet.findViewById(R.id.bottomSheetDistanceTextView);
        this.bottomSheetUpcomingEventsTextView = bottomSheet.findViewById(R.id.tvUpcomingEvents);
        this.bottomSheetEventRecyclerView = bottomSheet.findViewById(R.id.bottomSheetEventsRecyclerView);
        this.bottomSheetEvents = new ArrayList<>();
        this.bottomSheetEventsAdapter = new BottomSheetEventsAdapter(this.bottomSheetEvents);
        this.bottomSheetEventRecyclerView.setAdapter(this.bottomSheetEventsAdapter);
        this.bottomSheetEventRecyclerView.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.VERTICAL, false));

        bottomSheetItineraryButton.setOnClickListener(v -> {
            if (lastFocus != null){
                Intent intent = this.getActivity().getIntent();
                intent.putExtra(ItineraryManager.VISIBLEPOI_TARGET_KEY, lastFocus.getId());
                this.currentTargetPOI = lastFocus.getId();
                this.mapViewMode = MapViewMode.EXPLORATION;
                this.moveCameraToItinerary(this.iManager.computeItinerary(this.lastLocation, this.currentFloorLevel, this.lastFocus));
            }
        });

        bottomSheetCentrateButton.setOnClickListener(v -> {
            if (lastFocus != null) {
                this.moveCameraToPosition(new LatLng(this.lastFocus.getLatitude(), this.lastFocus.getLongitude()));
            }
        });

        return root;
    }

    private void retrieveBuilding(@NonNull Context context){
        this.progressBar.setVisibility(View.VISIBLE);
        RetrieveBuildingTask task = new RetrieveBuildingTask(context, building -> {
            this.onBuildingRetrieved(building);
            this.ivIndicatorNetwork.setVisibility(View.GONE);
        }, this::onNoNetworkButBuildingInCache, this::onErrorRetrievingBuilding);
        task.execute();
    }

    private void onBuildingRetrieved(@NonNull Building building){

        this.building = building;
        this.lManager.setBuilding(building);
        this.iManager.setBuilding(building, PreferenceUtils.isAccessibility(Objects.requireNonNull(this.getContext())));

        String[] floorTitles = building.getFloors().stream().map(Floor::getName).toArray(String[]::new);
        this.floorPicker.setMaxValue(floorTitles.length - 1);
        this.floorPicker.setValue(this.currentFloorLevel);
        this.floorPicker.setDisplayedValues(floorTitles);
        this.floorPicker.setWrapSelectorWheel(true);

        this.formatNumberPickerText(this.floorPicker, ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorPrimary));

        this.floorPicker.setOnValueChangedListener((picker, oldVal, newVal) -> {
            if (!this.mapViewMode.equals(MapViewMode.EXPLORATION)){
                this.mapViewMode = MapViewMode.EXPLORATION;
            }
            this.formatNumberPickerText(this.floorPicker, ContextCompat.getColor(getContext(), R.color.colorPrimary));
            this.updateFloor(newVal);
        });
        this.floorPicker.setEnabled(true);
        this.floorPicker.bringToFront();

        this.fillAutoCompleteSuggestions();
        if (!this.lManager.isRunning()){
            this.lManager.start();
        }

    }

    private void onNoNetworkButBuildingInCache(@NonNull Building building){
        String message = String.format(this.getString(R.string.nonetwork_building_in_cache_toast_message), building.getName());
        Toast.makeText(this.getContext(), message, Toast.LENGTH_LONG).show();
        Timber.e("NO NETWORK BUT BUILDING " + building.getId() + " COULD BE FOUND IN CACHE");
        this.ivIndicatorNetwork.setVisibility(View.VISIBLE);
        this.onBuildingRetrieved(building);
    }

    private void onErrorRetrievingBuilding(){
        Toast.makeText(this.getContext(), this.getString(R.string.nonetwork_toast_message), Toast.LENGTH_LONG).show();
        Timber.e("NO NETWORK : ERROR RETRIEVING BUILDING");
        this.ivIndicatorNetwork.setVisibility(View.VISIBLE);

        this.ivIndicatorNetwork.bringToFront();
        if (!this.lManager.isRunning()){
            this.lManager.start();
        }
    }

    private void formatNumberPickerText(NumberPicker picker, int color) {
        int count = picker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = picker.getChildAt(i);
            if (child instanceof EditText) {
                try {
                    Class clazz = picker.getClass();
                    Field field = clazz.getDeclaredField("mSelectorWheelPaint");
                    field.setAccessible(true);

                    EditText editText = (EditText) child;
                    editText.setTextColor(color);

                    Paint paint = (Paint) field.get(picker);
                    Objects.requireNonNull(paint).setColor(color);

                    picker.invalidate();
                    return;

                } catch (IllegalAccessException | NoSuchFieldException e) {
                    LOGGER.log(Level.WARNING, e.getMessage());
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        SearchManager searchManager = (SearchManager) Objects.requireNonNull(this.getActivity()).getSystemService(Context.SEARCH_SERVICE);
        this.cSearchView = (CustomSearchView) menu.findItem(R.id.search).getActionView();
        this.cSearchView.setSearchableInfo(Objects.requireNonNull(searchManager).getSearchableInfo(this.getActivity().getComponentName()));
        this.retrieveEventsForSearchView();
    }

    private void retrieveEventsForSearchView(){
        RetrieveEventsTask eventsTask = new RetrieveEventsTask(Objects.requireNonNull(this.getContext()), this.buildingManager, this.eventManager,
                events -> {
                    this.events = events;
                    this.fillAutoCompleteSuggestions();
                },
                () -> Timber.e("ERROR RETRIEVING EVENTS (to fill suggestions in SearchView)"));
        eventsTask.execute();
    }

    private void fillAutoCompleteSuggestions(){
        if (this.building == null || this.events == null){
            return;
        }
        this.cSearchView.setData(this.building, this.events);
    }

    private boolean hasItineraryRequestInIntent(){
        if (this.getActivity() == null){
            return false;
        }
        return this.getActivity().getIntent().hasExtra(ItineraryManager.VISIBLEPOI_TARGET_KEY);
    }

    private void updateFloor(int displayedFloorLevel){
        if (this.building == null){
            return;
        }
        this.mapDrawHandler.update(building, displayedFloorLevel);
        this.updateItinerary(this.lastLocation, displayedFloorLevel);
    }

    private void updateItinerary(Location position, int floorLevel){

        if (this.building == null || this.lastLocation == null || !this.hasItineraryRequestInIntent()){
            return;
        }

        int levelToDisplay = floorLevel;
        boolean joinPositionToItinerary = true;
        if (this.mapViewMode.equals(MapViewMode.TRACKING)){
            levelToDisplay = this.currentFloorLevel;
        } else if (this.mapViewMode.equals(MapViewMode.EXPLORATION)){
            levelToDisplay = this.floorPicker.getValue();
            if (levelToDisplay != this.currentFloorLevel){
                joinPositionToItinerary = false;
            }
        }

        this.mapDrawHandler.updateItinerary(this.currentItinerary, levelToDisplay, position, joinPositionToItinerary);
    }

    private void updateBottomSheetDistance(){
        if (this.lastFocus != null){
            double distance2d = DistanceCalculator.distance(this.lastFocus, this.lastLocation);
            String floorDiffText;
            int floorDiff = this.currentFloorLevel - this.lastFocus.getFloor().getLevel();
            if (floorDiff < 0){
                floorDiffText = Math.abs(floorDiff) + " floor" + (Math.abs(floorDiff) > 1 ? "s" : "") + " up";
                this.bottomSheetFloorDiffImageView.setRotation(90);
                this.bottomSheetFloorDiffImageView.setVisibility(View.VISIBLE);
            } else if (floorDiff == 0){
                floorDiffText = "Same floor";
                this.bottomSheetFloorDiffImageView.setRotation(180);
                this.bottomSheetFloorDiffImageView.setVisibility(View.GONE);
            } else {
                floorDiffText = floorDiff + " floor" + (Math.abs(floorDiff) > 1 ? "s" : "") + " down";
                this.bottomSheetFloorDiffImageView.setRotation(270);
                this.bottomSheetFloorDiffImageView.setVisibility(View.VISIBLE);
            }
            String distanceText = String.format(this.getString(R.string.bottom_sheet_distance_format), floorDiffText, Math.round(distance2d));
            this.bottomSheetDistanceTextView.setText(distanceText);
        }
    }

    private void setFormerCurrentBottomState(int former){
        this.formerBottomSheetState = former;
    }

    private void populateBottomSheet(){
        this.updateBottomSheetDistance();
        this.bottomSheetEvents = this.lastFocus.getEvents().stream().sorted(Comparator.comparing(Event::getStart)).collect(Collectors.toList());
        this.bottomSheetEventsAdapter.setEvents(this.bottomSheetEvents);
        this.bottomSheetUpcomingEventsTextView.setVisibility(this.bottomSheetEvents.isEmpty() ? View.GONE : View.VISIBLE);
        this.bottomSheetEventsAdapter.notifyDataSetChanged();
    }

    private BiConsumer<Location, Integer> onLocationReceived = ((location, floorLevel) -> {

        // Got last known location. In some rare situations this can be null.
        if (this.lastLocation == null && this.hasItineraryRequestInIntent()){
            this.currentTargetPOI = Objects.requireNonNull(this.getActivity()).getIntent().getLongExtra(ItineraryManager.VISIBLEPOI_TARGET_KEY, 666);
        }

        if (this.building != null && this.progressBar.getVisibility() != View.GONE){
            this.progressBar.setVisibility(View.GONE);
        }

        this.lastLocation = location;
        this.currentFloorLevel = floorLevel;
        this.updateBottomSheetDistance();

        // Check if user is arrived at destination
        if (this.hasItineraryRequestInIntent()){
            VisiblePOI targetPoi = (VisiblePOI) this.building.getPoiById(this.currentTargetPOI).get();
            try {
                this.currentItinerary = this.iManager.computeItinerary(location, floorLevel, targetPoi);
            } catch (NoItineraryFoundException e){
                Toast.makeText(this.getContext(), this.getString(R.string.no_itinerary_found_message), Toast.LENGTH_LONG).show();
                Timber.e("ITINERARY EXCEPTION : %s", e.getMessage());
                this.removeItinerary();
            }

            if (this.iManager.isArrived(targetPoi, location, floorLevel)){
                this.removeItinerary();
                Toast.makeText(this.getContext(), this.getString(R.string.itinerary_destination_reached), Toast.LENGTH_LONG).show();
            }
        }

        // Logic to handle location object
        this.updateCurrentPosition(location, floorLevel);
        if (this.mapViewMode.equals(MapViewMode.TRACKING)){
            this.floorPicker.setValue(floorLevel);
            this.updateFloor(floorLevel);
        } else {
            this.updateItinerary(location, floorLevel);
        }
        this.ivIndicatorGPS.setVisibility(this.lManager.isIndoor() ? View.GONE : View.VISIBLE);

        //Check if app has been launched from outside
        Intent intent = Objects.requireNonNull(this.getActivity()).getIntent();
        Uri data = intent.getData();
        if (data != null) {
            String request =  Objects.requireNonNull(data.getQuery()).replace("q=","");
            if(!request.equals("")){
                this.onStartFromCalendar(request);
            }
            intent.setData(null);
        }
    });

    private void onStartFromCalendar(String name){
        Optional<VisiblePOI> optVPoi = this.building.getPoiByName(name);
        if(!optVPoi.isPresent()){
            Toast.makeText(this.getContext(), this.getString(R.string.place_not_found), Toast.LENGTH_LONG).show();
            return;
        }

        VisiblePOI vPoi = optVPoi.get();
        LatLng point = new LatLng(vPoi.getLatitude(),vPoi.getLongitude());
        this.mapViewMode = MapViewMode.EXPLORATION;
        this.floorPicker.setValue(vPoi.getFloor().getLevel());
        this.updateFloor(vPoi.getFloor().getLevel());
        this.setLastFocus(vPoi);
        this.populateBottomSheet();
        this.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        this.moveCameraToPositionWithZoom(point, 19);
    }

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {

        this.mapBoxMap = mapboxMap;
        mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {

            //Remove MapBox's default buildings layers
            style.removeLayer("building");
            style.removeLayer("building-outline");
            style.removeLayer("road-path");
            style.removeLayer("road-path-bg");

            this.mapDrawHandler.init(style);
            mapboxMap.setMaxZoomPreference(22);
            mapboxMap.getUiSettings().setLogoEnabled(false);
            mapboxMap.getUiSettings().setAttributionEnabled(false);
            mapboxMap.getUiSettings().setCompassMargins(0, convertPxToDp(60), convertPxToDp(20), 0);
            mapboxMap.getUiSettings().setCompassImage(Objects.requireNonNull(ContextCompat.getDrawable(Objects.requireNonNull(this.getContext()), R.drawable.ic_blue_compass)));
        });

        this.lManager.addObserver(this.onLocationReceived);

        mapboxMap.addOnMapClickListener(this);
        mapboxMap.addOnMoveListener(this);
        mapboxMap.addOnCameraMoveStartedListener(this);
        mapboxMap.addOnCameraMoveListener(this);
        mapboxMap.addOnCameraIdleListener(this);
    }

    private void removeItinerary(){
        if (this.hasItineraryRequestInIntent()){
            Objects.requireNonNull(this.getActivity()).getIntent().removeExtra(ItineraryManager.VISIBLEPOI_TARGET_KEY);
        }
        this.currentItinerary = null;
        this.currentTargetPOI = -1;
        this.mapDrawHandler.clearItinerary();
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {

        PointF pointf = this.mapBoxMap.getProjection().toScreenLocation(point);
        RectF rectF = new RectF(pointf.x - 10, pointf.y - 10, pointf.x + 10, pointf.y + 10);
        this.mapDrawHandler.handleClick(this.mapBoxMap, rectF,
                feature -> {
            this.building.getPoiById(feature.getNumberProperty("id").longValue())
                    .ifPresent(poi -> {
                        this.setLastFocus((VisiblePOI) poi);
                        this.populateBottomSheet();
                    });
            this.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }, () -> this.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN));
        return true;
    }

    @Override
    public void onMoveBegin(@NonNull MoveGestureDetector detector) {
        this.mapViewMode = MapViewMode.EXPLORATION;
    }

    @Override
    public void onMove(@NonNull MoveGestureDetector detector) {

    }

    @Override
    public void onMoveEnd(@NonNull MoveGestureDetector detector) {

    }

    private void setLastFocus(VisiblePOI newFocus) {
        lastFocus = newFocus;
        bottomSheetTitle.setText(lastFocus.getTitle());
    }

    //region control camera

    private boolean movingMapCamera = false;

    @Override
    public void onCameraMoveStarted(int reason) {
        this.movingMapCamera = true;
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            this.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    @Override
    public void onCameraMove() { }

    @Override
    public void onCameraIdle() {
        this.movingMapCamera = false;
    }

    private final static int CAMERA_TRANSITION_DURATION = 2000;

    private void moveCameraToCurrentPositionWithZoom(float zoomLevel) {
        CameraPosition newPosition = new CameraPosition.Builder()
                .target(currentPosition)
                .zoom(zoomLevel)
                .build();
        mapBoxMap.animateCamera(CameraUpdateFactory.newCameraPosition(newPosition), CAMERA_TRANSITION_DURATION);
    }

    private void moveCameraToCurrentPosition() {
        if (!this.movingMapCamera){
            this.moveCameraToPosition(this.currentPosition);
        }
    }

    private void moveCameraToPosition(@NonNull LatLng position){
        CameraPosition newPosition = new CameraPosition.Builder()
                .target(position)
                .build();
        this.mapBoxMap.animateCamera(CameraUpdateFactory.newCameraPosition(newPosition), CAMERA_TRANSITION_DURATION);
    }

    private void moveCameraToPositionWithZoom(@NonNull LatLng position, float zoomLevel){
        CameraPosition newPosition = new CameraPosition.Builder()
                .target(position)
                .zoom(zoomLevel)
                .build();
        this.mapBoxMap.animateCamera(CameraUpdateFactory.newCameraPosition(newPosition), CAMERA_TRANSITION_DURATION);
    }

    private void moveCameraToItinerary(@NonNull Itinerary i){
        this.mapBoxMap.animateCamera(CameraUpdateFactory.newLatLngBounds(i.computeBounds(this.lastLocation), 150), 1000);
    }

    //endregion

    private static final int FAB_ANIMATION_DURATION = 160;

    private void moveDownFloatingActionButton(){
        floatingActionButton.animate().y(floatingActionButton.getY() + convertPxToDp(105)).setDuration(FAB_ANIMATION_DURATION).start();
    }

    private void moveUpFloatingActionButton(){
        floatingActionButton.animate().y(floatingActionButton.getY() - convertPxToDp(105)).setDuration(FAB_ANIMATION_DURATION).start();
    }

    private int convertPxToDp(int px) {
        if (this.getView() == null) {
            return 0;
        }
        return (int) (px * this.getView().getResources().getDisplayMetrics().density);
    }

    private void updateCurrentPosition(Location location, int floorLevel) {
        LatLng newPosition = new LatLng(location.getLatitude(), location.getLongitude());
        if (this.currentPosition != null) {
            if (animator != null && animator.isStarted()) {
                currentPosition = (LatLng) animator.getAnimatedValue();
                animator.cancel();
            }
            animator = ObjectAnimator
                    .ofObject(latLngEvaluator, currentPosition, newPosition)
                    .setDuration(1500);
            animator.addUpdateListener(animatorUpdateListener);
            animator.start();
            currentPosition = newPosition;
            this.mapBoxMap.getStyle(style -> this.mapDrawHandler.updateUserAccuracyLayer(location));
            if (this.mapViewMode.equals(MapViewMode.TRACKING)){
                this.moveCameraToCurrentPosition();
            }
        }else {
            this.currentPosition = newPosition;
            this.mapBoxMap.getStyle(style -> {
                this.mapDrawHandler.updateUserPosition(location.getLongitude(), location.getLatitude());
                this.mapDrawHandler.initUserAccuracyLayer(style, location);
                moveCameraToCurrentPositionWithZoom(19);
            });

        }
    }

    private static final TypeEvaluator<LatLng> latLngEvaluator = new TypeEvaluator<LatLng>() {
        private final LatLng latLng = new LatLng();

        @Override
        public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
            latLng.setLatitude(startValue.getLatitude()
                    + ((endValue.getLatitude() - startValue.getLatitude()) * fraction));
            latLng.setLongitude(startValue.getLongitude()
                    + ((endValue.getLongitude() - startValue.getLongitude()) * fraction));
            return latLng;
        }
    };

    private final ValueAnimator.AnimatorUpdateListener animatorUpdateListener =
            valueAnimator -> {
                LatLng animatedPosition = (LatLng) valueAnimator.getAnimatedValue();
                this.mapDrawHandler.updateUserPosition(animatedPosition.getLongitude(), animatedPosition.getLatitude());
            };

    //region override map

    private boolean stopped = false;

    @Override
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (this.stopped){
            this.lManager.addObserver(this.onLocationReceived);
            this.stopped = false;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
        this.bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        this.lManager.removeObserver(this.onLocationReceived);
        this.stopped = true;
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (animator != null) {
            animator.cancel();
        }
        mapView.onDestroy();
        this.lManager.stop();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    //endregion
}