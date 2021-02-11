package com.example.geolocindoor.ui;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geolocindoor.MainActivity;
import com.example.geolocindoor.R;
import com.example.geolocindoor.itinerary.ItineraryManager;
import com.example.geolocindoor.location.LocationManager;
import com.example.geolocindoor.managers.BuildingManager;
import com.example.geolocindoor.managers.EventManager;
import com.example.geolocindoor.managers.RetrieveEventsTask;
import com.example.geolocindoor.utils.DistanceCalculator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class EventsFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private EventManager eventManager;
    private BuildingManager buildingManager;
    private LocationManager lManager;

    private ProgressBar progressBar;
    private Button buttonRetry;
    private RecyclerView resultsRecyclerView;
    private EventsAdapter eventAdapter;
    private List<EventDisplay> upToDateEventDisplays;
    private int spinnerIndex;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_events, container, false);

        this.eventManager = EventManager.create(root.getContext());
        this.buildingManager = BuildingManager.create(root.getContext());
        this.lManager = LocationManager.create(root.getContext());

        this.progressBar = root.findViewById(R.id.progressBar);
        this.buttonRetry = root.findViewById(R.id.buttonRetry);
        this.buttonRetry.setOnClickListener(view -> this.retrieveEvents());

        this.resultsRecyclerView = root.findViewById(R.id.searchEventsRecyclerView);
        this.resultsRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL, false));

        Spinner sortSpinner = root.findViewById(R.id.spinnerType);
        sortSpinner.setOnItemSelectedListener(this);

        this.upToDateEventDisplays = new ArrayList<>();
        this.eventAdapter = new EventsAdapter(this.upToDateEventDisplays, locationId -> {
            Intent intent = new Intent(this.getContext(), MainActivity.class);
            intent.putExtra(ItineraryManager.VISIBLEPOI_TARGET_KEY, locationId);
            startActivity(intent);
        });
        this.resultsRecyclerView.setAdapter(this.eventAdapter);
        this.retrieveEvents();

        return root;
    }

    private void retrieveEvents(){
        this.progressBar.setVisibility(View.VISIBLE);
        RetrieveEventsTask task = new RetrieveEventsTask(Objects.requireNonNull(this.getContext()), this.buildingManager, this.eventManager, events -> {
            this.upToDateEventDisplays = events.stream().map(EventDisplay::new).collect(Collectors.toList());
            this.eventAdapter.setEvents(this.upToDateEventDisplays);
            this.refresh();
            this.lManager.addObserver(this.onLocationReceived);
            this.progressBar.setVisibility(View.GONE);
            this.buttonRetry.setVisibility(View.GONE);
        }, () -> {
            Toast.makeText(this.getContext(), this.getString(R.string.nonetwork_toast_message), Toast.LENGTH_LONG).show();
            this.progressBar.setVisibility(View.GONE);
            this.buttonRetry.setVisibility(View.VISIBLE);
        });
        task.execute();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        this.spinnerIndex = position;
        ((TextView) parent.getChildAt(0)).setTextColor(Color.WHITE);
        this.refresh();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private final BiConsumer<Location, Integer> onLocationReceived = (location, floorLevel) -> {
        this.upToDateEventDisplays.forEach(eventDisplay -> eventDisplay.setDistance(Math.round(DistanceCalculator.distance(eventDisplay.getEvent().getLocation(), location))));
        this.refresh();
        Objects.requireNonNull(this.resultsRecyclerView.getAdapter()).notifyDataSetChanged();
    };

    private void refresh(){
        this.upToDateEventDisplays = this.eventAdapter.getEvents().stream()
                .sorted(comparator(this.spinnerIndex))
                .collect(Collectors.toList());
        this.eventAdapter.setEvents(this.upToDateEventDisplays);
        Objects.requireNonNull(this.resultsRecyclerView.getAdapter()).notifyDataSetChanged();
    }

    private static Comparator<? super EventDisplay> comparator(int spinnerIndex){
        switch(spinnerIndex){
            case 0: return Comparator.comparing(x -> x.getEvent().getTitle());
            case 1: return Comparator.comparing(x -> x.getEvent().getStart());
            case 2: return Comparator.comparing(EventDisplay::getDistance);
            default: throw new IllegalStateException("Wrong spinner index");
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
    public void onStop() {
        super.onStop();
        this.lManager.removeObserver(this.onLocationReceived);
        this.stopped = true;
    }
}