package com.edreams.booking.domain;

import java.util.Objects;
import java.util.UUID;

public final class Passenger {
    private final UUID passengerId;
    private final String name;
    private final String email;

    public Passenger(UUID passengerId, String name, String email) {
        this.passengerId = Objects.requireNonNull(passengerId);
        this.name = Objects.requireNonNull(name);
        this.email = Objects.requireNonNull(email);
        
        if (name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be blank");
        }
        
        if (email.isBlank() || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
    
    public Passenger(String name, String email) {
        this(UUID.randomUUID(), name, email);
    }
    
    public UUID getPassengerId() {
        return passengerId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passenger passenger = (Passenger) o;
        return passengerId.equals(passenger.passengerId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(passengerId);
    }
    
    @Override
    public String toString() {
        return "Passenger{" +
                "passengerId=" + passengerId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}