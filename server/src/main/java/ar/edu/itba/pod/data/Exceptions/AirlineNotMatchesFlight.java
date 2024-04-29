package ar.edu.itba.pod.data.Exceptions;

public class AirlineNotMatchesFlight extends RuntimeException {
    public AirlineNotMatchesFlight(String flightCode, String airline) {
        super(String.format("Airline %s already has flight %s", airline, flightCode));
    }
}
