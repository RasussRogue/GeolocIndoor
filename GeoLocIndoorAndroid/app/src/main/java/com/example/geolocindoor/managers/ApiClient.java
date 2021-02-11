package com.example.geolocindoor.managers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import com.arxit.geolocindoor.common.entities.Building;
import com.arxit.geolocindoor.common.entities.Event;
import com.arxit.geolocindoor.common.utils.BuildingSerializationHandler;
import com.example.geolocindoor.R;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

final class ApiClient {

    private final Context context;
    private final OkHttpClient client;
    private final BuildingSerializationHandler serializer;
    private final DateTimeFormatter dtf;

    private ApiClient(@NonNull Context context){
        this.context = context;
        this.client = new OkHttpClient();
        this.serializer = new BuildingSerializationHandler();
        this.dtf = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    }

    public static ApiClient create(Context context){
        return new ApiClient(context);
    }

    private Request createGetRequest(String route){
        return new Request.Builder()
                .url(PreferenceManager.getDefaultSharedPreferences(this.context).getString(context.getString(R.string.prefs_key_api_host), context.getString(R.string.geoloc_indoor_api_host)) + route)
                .get().build();
    }

    List<BuildingSimple> getBuildings(){
        Request request = this.createGetRequest("/api/buildings");
        try {
            Response response = client.newCall(request).execute();
            List<Building> buildings = this.serializer.deserializeBuildinds(Objects.requireNonNull(response.body()).string());
            return buildings.stream().map(BuildingSimple::fromBuilding).collect(Collectors.toList());
        } catch (IOException e){
            throw new UncheckedIOException(e);
        }
    }

    Building getBuilding(long buildingId){
        Request request = this.createGetRequest("/api/building/" + buildingId);
        try {
            Response response = client.newCall(request).execute();
            return this.serializer.deserializeBuilding(Objects.requireNonNull(response.body()).string());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    long getBuildingChecksum(long buildingId){
        Request request = this.createGetRequest("/api/checksum/" + buildingId);
        try {
            Response response = client.newCall(request).execute();
            return Long.parseLong(Objects.requireNonNull(response.body()).string());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    List<Event> getEventsByInterval(long buildingId, @NonNull ZonedDateTime start, @NonNull ZonedDateTime end){
        String route = "/api/events/" + buildingId + "/interval?minStart=" + this.dtf.format(start).replace("+", "%2B") + "&maxEnd=" + this.dtf.format(end).replace("+", "%2B");
        Request request = this.createGetRequest(route);
        try {
            Response response = client.newCall(request).execute();
            return this.serializer.deserializeEvents(Objects.requireNonNull(response.body()).string());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
