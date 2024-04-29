package ar.edu.itba.pod.data.Exceptions;

public class FlightCodeAlreadyInCounterRangeQueue extends RuntimeException{
    public FlightCodeAlreadyInCounterRangeQueue(String flight) {
        super(String.format("Flight %s is already in queue for counter range assignment", flight));
    }
}
