package ar.edu.itba.pod.data.Utils;


import ar.edu.itba.pod.data.Flight;
import ar.edu.itba.pod.query.Flights;

import java.util.List;

public class CounterState {
    private final String sectorName;
    private final int counterStart;
    private final int counterEnd;
    private final String airlineName;
    private final List<Flight> flights;
    private final int people;

    public CounterState(String sectorName, int counterStart, int counterEnd, String airlineName, List<Flight> flights, int people) {
        this.sectorName = sectorName;
        this.counterStart = counterStart;
        this.counterEnd = counterEnd;
        this.airlineName = airlineName;
        this.flights = flights;
        this.people = people;
    }

    public String getSectorName() {
        return sectorName;
    }

    public int getCounterStart() {
        return counterStart;
    }

    public int getCounterEnd() {
        return counterEnd;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public List<String> getFlightCodes() {
        return flights.stream().map(Flight::getFlightCode).toList();
    }

    public int getPeople() {
        return people;
    }
}
