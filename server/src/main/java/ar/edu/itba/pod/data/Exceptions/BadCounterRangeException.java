package ar.edu.itba.pod.data.Exceptions;

public class BadCounterRangeException extends RuntimeException{

    public BadCounterRangeException(int min, int max) {
        super(String.format("(%d-%d) is not a valid counter range"));
    }
}
