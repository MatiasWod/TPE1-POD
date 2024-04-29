package ar.edu.itba.pod.data.Exceptions;

public class StillPassengersInLineException extends RuntimeException{
    public StillPassengersInLineException(int queueSize) {
        super(String.format("Cammot free, there are still %d passenger in line", queueSize));
    }
}
