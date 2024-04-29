package ar.edu.itba.pod.data.Exceptions;

public class FlightNotMatchesCounterException extends RuntimeException{
    public FlightNotMatchesCounterException(String flight, int counter, String sector) {
        super(String.format("The flight %s is not checkin in at counter %d sector %s", flight, counter, sector));
    }
}
