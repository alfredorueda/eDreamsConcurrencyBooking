# Flight Booking Concurrency Training

This project is a senior-level concurrency training exercise inspired by flight booking systems 
of digital airline companies. 
It demonstrates domain-driven design principles and concurrency control in a rich domain model.

## Domain Model

### Entities

#### Flight (Aggregate Root)
- Core entity for flight bookings
- Maintains seats and their booking status
- Ensures thread safety with ReentrantLock
- Enforces invariants:
  - Cannot book more than capacity
  - A seat can only be booked once

#### Booking
- Represents a passenger's booking for a specific seat
- Immutable after creation
- Links passenger, flight, and seat number

#### Passenger
- Represents a customer who can book flights
- Contains identification and contact information

### Value Objects

#### FlightId
- Uniquely identifies a flight with format "EDR" followed by 5 digits
- Immutable value object

#### SeatNumber
- Identifies a specific seat on a flight
- Format: row number (1-99) followed by seat letter (A-F)

#### BookingRequest
- DTO for carrying booking request data
- Contains passenger information and desired flight

### Services

#### BookingEngine
- Orchestrates flight bookings across multiple flights
- Provides sequential and parallel booking strategies:
  - Sequential processing
  - Parallel processing with fixed thread pool
  - Parallel processing with virtual threads (Java 21 feature)
- Tracks booking results and performance metrics

## Concurrency Implementation

The project demonstrates several concurrency techniques:

1. Thread-safe entity design using ReentrantLock in the Flight entity
2. Use of ConcurrentHashMap for thread-safe collections
3. Java 21 Virtual Threads implementation
4. Traditional ExecutorService with fixed thread pool
5. Atomic operations and concurrent collections

## Running the Project

### Requirements
- Java 21
- Maven

### Build the Project
```
mvn clean package
```

### Run Tests
```
mvn test
```

### Performance Comparison

The project includes performance tests comparing:
- Sequential booking
- Fixed thread pool booking
- Virtual thread booking

The ConcurrencyStressTest class demonstrates massive concurrent bookings with 10,000+ virtual threads.

## Key Learning Points

- Encapsulation of concurrency inside domain objects
- Ensuring domain invariants under high concurrency
- Virtual Threads vs. platform threads performance comparison
- Thread-safe collections and their proper usage
- Designing immutable value objects

## Project Structure

- `src/main/java/com/edreams/booking/domain` - Domain model classes
- `src/main/java/com/edreams/booking/service` - Service layer
- `src/test/java/com/edreams/booking/domain` - Unit tests for domain model
- `src/test/java/com/edreams/booking/service` - Service tests including stress tests
