package ar.edu.itba.pod.data.Exceptions;

public class AirlineNotMatchesCounterRangeException extends RuntimeException {
    public AirlineNotMatchesCounterRangeException() {
        super(String.format("Airline does not match counter"));
    }
}
