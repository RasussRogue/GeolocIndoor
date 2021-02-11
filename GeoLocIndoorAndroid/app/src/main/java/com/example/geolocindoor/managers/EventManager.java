package com.example.geolocindoor.managers;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;

import com.arxit.geolocindoor.common.entities.Building;
import com.arxit.geolocindoor.common.entities.Event;
import com.arxit.geolocindoor.common.entities.VisiblePOI;
import com.arxit.geolocindoor.common.utils.StringUtils;
import com.example.geolocindoor.utils.PreferenceUtils;

import java.io.UncheckedIOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import timber.log.Timber;

public final class EventManager {

    private final Context context;
    private final ApiClient apiClient;
    private final OfflineManager offlineManager;

    private EventManager(@NonNull Context context){
        this.context = context;
        this.apiClient = ApiClient.create(context);
        this.offlineManager = OfflineManager.create(context);
    }

    @SuppressLint("StaticFieldLeak")
    private static EventManager instance;

    public static EventManager create(Context context){
        if (instance == null){
            instance = new EventManager(context.getApplicationContext());
        }
        return instance;
    }

    List<Event> getEvents(@NonNull Building building){

        int days = PreferenceUtils.getDaycount(this.context);
        if (days <= 0){
            throw new IllegalArgumentException("Day count to get events must be > 0");
        }

        List<Event> events;
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime intervalEnd = now.plusDays(days);

        try {
            events = this.apiClient.getEventsByInterval(building.getId(), now, intervalEnd);
            this.offlineManager.saveEventsToCache(building.getId(), events);
        } catch (UncheckedIOException e){
            if (this.offlineManager.hasCachedEvents(building.getId())){
                events = this.offlineManager.getCachedEvents(building.getId()).stream()
                        .filter(event -> now.isBefore(event.getEnd()))
                        .collect(Collectors.toList());
                Timber.e("Network error while getting events, BUT events for building '%s'(#%d) are stored in cache", building.getName(), building.getId());
            } else {
                events = new ArrayList<>();
                Timber.e("Network error while getting events, events not found in cache for building '%s'(#%d)", building.getName(), building.getId());
            }
        }

        events.forEach(event -> event.getLocation().setFloor(building.getPoiById(event.getLocation().getId()).get().getFloor()));
        return events;
    }

    void bindEventsToPois(@NonNull Building building, @NonNull List<Event> events){
        events.forEach(event ->
                building.getPoiById(event.getLocation().getId())
                        .filter(VisiblePOI.class::isInstance)
                        .map(VisiblePOI.class::cast)
                        .ifPresent(visiblePOI -> visiblePOI.addEvent(event)));
    }

    public List<Event> getEventsMatching(@NonNull List<Event> events, @NonNull String request){
        String matching = StringUtils.unaccent(request);
        return events.stream().filter(e -> e.matches(matching)).collect(Collectors.toList());
    }
}
