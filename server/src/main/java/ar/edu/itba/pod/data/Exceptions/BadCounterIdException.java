package ar.edu.itba.pod.data.Exceptions;

public class BadCounterIdException extends RuntimeException{
    public BadCounterIdException(int counter, String sector) {
        super(String.format("The counter %d from sector %s is not checking in", counter, sector));
    }
}
