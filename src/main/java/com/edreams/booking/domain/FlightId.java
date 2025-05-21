package com.edreams.booking.domain;

import java.util.Objects;
import java.util.regex.Pattern;

public final class FlightId {
    private static final Pattern VALID_FORMAT = Pattern.compile("EDR\\d{5}");
    
    private final String value;
    
    public FlightId(String value) {
        if (value == null || !VALID_FORMAT.matcher(value).matches()) {
            throw new IllegalArgumentException("Flight ID must follow format EDR followed by 5 digits");
        }
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FlightId flightId = (FlightId) o;
        return value.equals(flightId.value);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}