package ar.edu.itba.pod.data.Exceptions;

public class AirlineAlreadyRegisteredForEventsException extends RuntimeException {
    public static final String MESSAGE = "Airline already registered for events";

    public AirlineAlreadyRegisteredForEventsException() {
        super(MESSAGE);
    }
}
