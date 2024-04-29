package ar.edu.itba.pod.data.Exceptions;

public class NotRangeAssignedToFlightException extends RuntimeException {
    public NotRangeAssignedToFlightException(String flight) {
        super(String.format("The flight %s does not have a counter assigned yet", flight));
    }
}
