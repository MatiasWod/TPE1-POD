package ar.edu.itba.pod.data.Exceptions;

public class NoCountersException extends RuntimeException{
    public NoCountersException(String sector) {
        super(String.format("There are no counters in sector %", sector));
    }
}
