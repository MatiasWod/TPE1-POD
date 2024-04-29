package ar.edu.itba.pod.data.Exceptions;

public class NoPassengersInFlightException extends RuntimeException {
    public NoPassengersInFlightException(String flightCode) {
        super(String.format("Flight %s has no passengers", flightCode));
    }
}
