package ar.edu.itba.pod.data.Utils;

import ar.edu.itba.pod.data.Passenger;

public class CheckInCountersDTO {
    private Passenger passenger;
    private int counterId;

    public CheckInCountersDTO(Passenger passenger, int counterId) {
        this.passenger = passenger;
        this.counterId = counterId;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public int getCounterId() {
        return counterId;
    }
}
