package com.example.geolocindoor.exception;

public class NoItineraryFoundException extends RuntimeException {

    public NoItineraryFoundException(String message) {
        super(message);
    }
}
