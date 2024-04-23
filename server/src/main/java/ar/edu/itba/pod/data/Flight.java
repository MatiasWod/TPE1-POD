package ar.edu.itba.pod.data;

import java.util.List;

public class Flight {
    private List<Passenger> passengerList;
    private String flightCode;
    private String airlineName;

    public Flight(List<Passenger> passengerList, String flightCode, String airlineName) {
        this.passengerList = passengerList;
        this.flightCode = flightCode;
        this.airlineName = airlineName;
    }

    public List<Passenger> getPassengerList() {
        return passengerList;
    }

    public String getFlightCode() {
        return flightCode;
    }

    public String getAirlineName() {
        return airlineName;
    }
}
