package ar.edu.itba.pod.data.Utils;

import ar.edu.itba.pod.data.Passenger;

public class CheckInCountersDTO {
    private Passenger passenger;
    private final int counterId;
    private boolean isEmpty = false;

    public CheckInCountersDTO(int counterId) {
        this.counterId = counterId;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public int getCounterId() {
        return counterId;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean isEmpty) {
        this.isEmpty = isEmpty;
    }

}
