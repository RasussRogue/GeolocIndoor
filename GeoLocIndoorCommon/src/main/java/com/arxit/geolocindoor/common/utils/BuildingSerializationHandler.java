package com.arxit.geolocindoor.common.utils;

import com.arxit.geolocindoor.common.entities.Building;
import com.arxit.geolocindoor.common.entities.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BuildingSerializationHandler {

    private final ObjectMapper mapper;

    public BuildingSerializationHandler(){
        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.mapper.registerModule(new JavaTimeModule());
    }

    public String serializeBuilding(Building building){
        try {
            return this.mapper.writeValueAsString(building);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Building deserializeBuilding(String json){
        try {
            return this.mapper.readValue(Objects.requireNonNull(json), Building.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Building> deserializeBuildinds(String json){
        try {
            return this.mapper.readValue(Objects.requireNonNull(json), new TypeReference<ArrayList<Building>>(){});
        } catch (JsonProcessingException e){
                throw new RuntimeException(e);
            }
    }

    public String serializeEvents(List<Event> events){
        try {
            return this.mapper.writeValueAsString(Objects.requireNonNull(events));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Event> deserializeEvents(String json){
        try {
            return this.mapper.readValue(Objects.requireNonNull(json), new TypeReference<ArrayList<Event>>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
