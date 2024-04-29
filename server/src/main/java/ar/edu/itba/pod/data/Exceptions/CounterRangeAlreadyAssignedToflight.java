package ar.edu.itba.pod.data.Exceptions;

public class CounterRangeAlreadyAssignedToflight extends RuntimeException {
    public CounterRangeAlreadyAssignedToflight(String flight) {
        super(String.format("Flight %s already has a counterRange assigned", flight));
    }
}
