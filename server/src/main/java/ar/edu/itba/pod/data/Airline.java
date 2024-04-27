package ar.edu.itba.pod.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Airline {
    private final Map<String, Flight> flights = new HashMap<>();
    private List<Integer> countersId= new ArrayList<>();

    public void loadFlight(String airlineName, String flightCode, String bookingCode){
        if(!flights.containsKey(flightCode)){
            flights.put(flightCode, new Flight(new ArrayList<>(), flightCode, airlineName));
        }
        flights.get(flightCode).addPassenger(new Passenger(bookingCode, flightCode, airlineName));
    }

    public Map<String, Flight> getFlights(){
        return flights;
    }

    public Flight getFlight(String flightCode){
        return flights.get(flightCode);
    }

}
