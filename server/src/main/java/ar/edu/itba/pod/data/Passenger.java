package ar.edu.itba.pod.data;

import ar.edu.itba.pod.checkIn.PassengerStatus;

import java.util.Objects;

public class Passenger {
    private String bookingCode;
    private String flightCode;
    private String airlineCode;
    private String sector;
    private Counter counterFrom;
    private int checkedInAtCounter;
    private PassengerStatus status;

    public Passenger(String bookingCode, String flightCode, String airlineCode, PassengerStatus status) {
        this.bookingCode = bookingCode;
        this.flightCode = flightCode;
        this.airlineCode = airlineCode;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passenger passenger = (Passenger) o;
        return bookingCode.equals(passenger.bookingCode);
    }

    @Override
    public int hashCode() {
        return bookingCode.hashCode();
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public String getFlightCode() {
        return flightCode;
    }

    public String getAirlineCode() {
        return airlineCode;
    }


    public PassengerStatus getStatus() {
        return status;
    }

    public void setStatus(PassengerStatus status) {
        this.status = status;
    }

    public String getSector() {
        return sector;
    }

    public int getCheckedInAtCounter() {
        return checkedInAtCounter;
    }

    public void setCheckedInAtCounter(int checkedInAtCounter) {
        this.checkedInAtCounter = checkedInAtCounter;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public Counter getCounterFrom() {
        return counterFrom;
    }

    public void setCounterFrom(Counter counterFrom) {
        this.counterFrom = counterFrom;
    }
}
