package ar.edu.itba.pod.data.Exceptions;

public class AirlineNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Airline not found.";
    public AirlineNotFoundException() {
        super(MESSAGE);
    }
}
