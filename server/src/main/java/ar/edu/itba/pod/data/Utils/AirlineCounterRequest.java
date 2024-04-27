package ar.edu.itba.pod.data.Utils;

import ar.edu.itba.pod.data.Airline;
import ar.edu.itba.pod.data.Flight;

import java.util.List;

public class AirlineCounterRequest {
    private final Airline airline;
    private final int countersAmount;
    private final List<String> flights;

    public AirlineCounterRequest(Airline airline, int countersAmount, List<String> flights) {
        this.airline = airline;
        this.countersAmount = countersAmount;
        this.flights = flights;
    }

    public Airline getAirline() {
        return airline;
    }

    public int getCountersAmount() {
        return countersAmount;
    }

    public List<String> getFlights() {
        return flights;
    }
}
