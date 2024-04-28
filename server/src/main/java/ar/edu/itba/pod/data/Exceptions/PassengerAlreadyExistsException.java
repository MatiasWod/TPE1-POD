package ar.edu.itba.pod.data.Exceptions;

public class PassengerAlreadyExistsException extends RuntimeException{

    public PassengerAlreadyExistsException(String booking) {
        super(String.format("Passenger with booking %s already exists", booking));
    }
}
