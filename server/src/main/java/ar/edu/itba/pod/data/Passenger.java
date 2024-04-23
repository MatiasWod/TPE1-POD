package ar.edu.itba.pod.data;

import java.util.Objects;

public class Passenger {
    private String bookingCode;
    private String flightCode;
    private String airlineCode;

    public Passenger(String bookingCode, String flightCode, String airlineCode) {
        this.bookingCode = bookingCode;
        this.flightCode = flightCode;
        this.airlineCode = airlineCode;
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
}
