package ar.edu.itba.pod.data.Exceptions;

public class PassengerAlreadyInQueue extends RuntimeException {
    public PassengerAlreadyInQueue(String booking) {
        super(String.format("Passenger with booking %s already in check in queue", booking));
    }
}
