package com.example.geolocindoor;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arxit.geolocindoor.common.entities.Building;
import com.arxit.geolocindoor.common.entities.VisiblePOI;
import com.arxit.geolocindoor.common.utils.StringUtils;
import com.example.geolocindoor.itinerary.ItineraryManager;
import com.example.geolocindoor.location.LocationManager;
import com.example.geolocindoor.managers.BuildingManager;
import com.example.geolocindoor.managers.EventManager;
import com.example.geolocindoor.managers.RetrieveEventsTask;
import com.example.geolocindoor.ui.CustomSearchView;
import com.example.geolocindoor.ui.EventDisplay;
import com.example.geolocindoor.ui.EventsAdapter;
import com.example.geolocindoor.ui.VisiblePOIAdapter;
import com.example.geolocindoor.ui.VisiblePOIDisplay;
import com.example.geolocindoor.utils.DistanceCalculator;
import com.example.geolocindoor.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import info.hoang8f.android.segmented.SegmentedGroup;

public class SearchResultsActivity extends AppCompatActivity {

    private String query;
    private BuildingManager buildingManager;
    private EventManager eventManager;
    private LocationManager lManager;

    private ProgressBar progressBar;
    private Button buttonRetry;
    private SegmentedGroup segmentedGroup;
    private RecyclerView resultsRecyclerView;

    private VisiblePOIAdapter poiAdapter;
    private EventsAdapter eventsAdapter;
    private List<EventDisplay> eventDisplays;
    private List<VisiblePOIDisplay> poiDisplays;

    private CustomSearchView cSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);

        this.buildingManager = BuildingManager.create(this);
        this.eventManager = EventManager.create(this);
        this.lManager = LocationManager.create(this);

        this.progressBar = this.findViewById(R.id.progressBar);
        this.buttonRetry = this.findViewById(R.id.buttonRetry);
        this.buttonRetry.setOnClickListener(view -> this.retrieveEvents());
        Toolbar toolbar = findViewById(R.id.app_bar_secondary);
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        toolbar.setNavigationOnClickListener(view -> finish());

        this.resultsRecyclerView = this.findViewById(R.id.searchResultsRecyclerView);
        this.resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        this.eventDisplays = new ArrayList<>();
        this.eventsAdapter = new EventsAdapter(this.eventDisplays, locationId -> {
            this.startActivity(new Intent(this, MainActivity.class).putExtra(ItineraryManager.VISIBLEPOI_TARGET_KEY, locationId));
            this.finish();
        });

        this.poiDisplays = new ArrayList<>();
        this.poiAdapter = new VisiblePOIAdapter(this.poiDisplays, locationId -> {
            this.startActivity(new Intent(this, MainActivity.class).putExtra(ItineraryManager.VISIBLEPOI_TARGET_KEY, locationId));
            this.finish();
        });

        this.segmentedGroup = this.findViewById(R.id.searchResultSegmentedGroup);
        this.handleIntent(getIntent());

        RadioButton placeRadioButton = findViewById(R.id.placeRadioButton);
        placeRadioButton.setChecked(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        this.cSearchView = (CustomSearchView) menu.findItem(R.id.search).getActionView();
        this.cSearchView.setSearchableInfo(Objects.requireNonNull(searchManager).getSearchableInfo(this.getComponentName()));

        // Set unlimited max width of the search view
        this.cSearchView.setMaxWidth(Integer.MAX_VALUE);

        // Set search input field as displayed by default
        this.cSearchView.setIconifiedByDefault(false);

        ImageView searchViewIcon = this.cSearchView.findViewById(androidx.appcompat.R.id.search_mag_icon);
        ViewGroup linearLayoutSearchView =(ViewGroup) searchViewIcon.getParent();
        linearLayoutSearchView.removeView(searchViewIcon);

        View searchViewPlate = this.cSearchView.findViewById(androidx.appcompat.R.id.search_plate);
        searchViewPlate.setBackgroundColor(Color.TRANSPARENT);

        View searchViewSubmit = this.cSearchView.findViewById(androidx.appcompat.R.id.submit_area);
        searchViewSubmit.setBackgroundColor(Color.TRANSPARENT);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            this.query = intent.getStringExtra(SearchManager.QUERY);
            this.cSearchView.setQuery(this.query, false);
        }

        this.retrieveEvents();
        return super.onCreateOptionsMenu(menu);
    }

    private void retrieveEvents(){

        this.progressBar.setVisibility(View.VISIBLE);
        Intent intent = getIntent();

        RetrieveEventsTask eventsTask = new RetrieveEventsTask(this, this.buildingManager, this.eventManager,
                events -> {

                    Building building = this.buildingManager.offlineManager().getCachedBuilding(PreferenceUtils.getSelectedBuildingId(this));
                    this.cSearchView.setData(building, events);

                    this.eventDisplays = this.eventManager.getEventsMatching(events, this.query).stream()
                            .map(EventDisplay::new).collect(Collectors.toList());
                    this.eventsAdapter.setEvents(this.eventDisplays);

                    this.poiDisplays = building.getPlacesMatching(this.query).stream()
                            .sorted(Comparator.comparing(VisiblePOI::getTitle))
                            .map(VisiblePOIDisplay::new).collect(Collectors.toList());
                    this.poiAdapter.setPlaces(this.poiDisplays);

                    this.lManager.addObserver(this.onLocationReceived);
                    this.handleIntent(intent);
                },
                () -> Toast.makeText(this, this.getString(R.string.nonetwork_toast_message), Toast.LENGTH_LONG).show());
        eventsTask.execute();
    }

    private final BiConsumer<Location, Integer> onLocationReceived = (location, floorLevel) -> {
        this.eventDisplays.forEach(eventDisplay -> eventDisplay.setDistance(Math.round(DistanceCalculator.distance(eventDisplay.getEvent().getLocation(), location))));
        this.eventDisplays.sort(Comparator.comparing(EventDisplay::getDistance));
        this.poiDisplays.forEach(poiDisplay -> poiDisplay.setDistance(Math.round(DistanceCalculator.distance(poiDisplay.getPoi(), location))));
        this.poiDisplays.sort(Comparator.comparing(VisiblePOIDisplay::getDistance));
        Objects.requireNonNull(this.resultsRecyclerView.getAdapter()).notifyDataSetChanged();
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            this.query = StringUtils.unaccent(intent.getStringExtra(SearchManager.QUERY));
            this.segmentedGroup.setOnCheckedChangeListener((group, checkedId) -> {
                if (checkedId == R.id.placeRadioButton){
                    this.resultsRecyclerView.setAdapter(this.poiAdapter);
                    this.poiAdapter.notifyDataSetChanged();
                } else if (checkedId == R.id.eventRadioButton){
                    this.resultsRecyclerView.setAdapter(this.eventsAdapter);
                    this.eventsAdapter.notifyDataSetChanged();
                }
            });
            this.progressBar.setVisibility(View.GONE);
            this.buttonRetry.setVisibility(View.GONE);
        }
    }

    private boolean stopped = false;

    @Override
    public void onResume() {
        super.onResume();
        if (this.stopped){
            this.lManager.addObserver(this.onLocationReceived);
            this.stopped = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.lManager.removeObserver(this.onLocationReceived);
        this.stopped = true;
    }
}
