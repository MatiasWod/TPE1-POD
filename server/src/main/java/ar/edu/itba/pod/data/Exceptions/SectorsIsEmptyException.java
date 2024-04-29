package ar.edu.itba.pod.data.Exceptions;

public class SectorsIsEmptyException extends RuntimeException {
    public static final String MESSAGE = "There are no sectors in airport";

    public SectorsIsEmptyException() {
        super(MESSAGE);
    }
}
