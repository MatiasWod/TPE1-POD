package ar.edu.itba.pod.data.Exceptions;

public class AirlineNotRegisteredException extends RuntimeException{
    public static final String MESSAGE = "Airline isnt registered for events";

    public AirlineNotRegisteredException() {
        super(MESSAGE);
    }
}
