package com.edreams.booking.domain;

import java.util.Objects;
import java.util.regex.Pattern;

public final class SeatNumber {
    private static final Pattern VALID_FORMAT = Pattern.compile("[0-9]{1,2}[A-F]");
    
    private final String value;
    
    public SeatNumber(String value) {
        if (value == null || !VALID_FORMAT.matcher(value).matches()) {
            throw new IllegalArgumentException("Seat number must be in format: row number (1-99) followed by seat letter (A-F)");
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
        SeatNumber that = (SeatNumber) o;
        return value.equals(that.value);
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