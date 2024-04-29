package ar.edu.itba.pod.data.Exceptions;

import ar.edu.itba.pod.data.Passenger;

public class PassengerAlreadyCheckedIn extends RuntimeException {
    public PassengerAlreadyCheckedIn(String booking) {
        super(String.format("The passenger with %s already checked in", booking));
    }
}
