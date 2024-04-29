package ar.edu.itba.pod.data.Exceptions;

public class AirlineNotFoundException extends RuntimeException {
    public AirlineNotFoundException(String airline) {
        super(String.format("Airline %s not found", airline));
    }
}
