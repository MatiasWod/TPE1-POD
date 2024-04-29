package ar.edu.itba.pod.data.Exceptions;

public class NotRangeAssignedException extends RuntimeException {
    public NotRangeAssignedException() {
        super(String.format("There is not a counter range assinged"));
    }
}
