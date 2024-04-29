package ar.edu.itba.pod.data.Exceptions;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(String booking) {
        super(String.format("The booking %s was not found", booking));
    }
}
