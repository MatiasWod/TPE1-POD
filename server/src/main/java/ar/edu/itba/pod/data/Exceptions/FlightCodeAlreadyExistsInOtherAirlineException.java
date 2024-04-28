package ar.edu.itba.pod.data.Exceptions;

public class FlightCodeAlreadyExistsInOtherAirlineException extends RuntimeException {

    public FlightCodeAlreadyExistsInOtherAirlineException(String flightCode, String airline) {
        super(String.format("Flight code %s already exists in airline %s", flightCode, airline));
    }
}
