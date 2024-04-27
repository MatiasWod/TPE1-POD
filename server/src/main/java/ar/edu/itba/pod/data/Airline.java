package ar.edu.itba.pod.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Airline {
    private final String airlineName;
    private final Map<String, Flight> flights = new HashMap<>();
    private List<Integer> countersId= new ArrayList<>();

    public Airline(String airlineName) {
        this.airlineName = airlineName;
    }

    public void loadFlight(String airlineName, String flightCode, String bookingCode){
        if(!flights.containsKey(flightCode)){
            flights.put(flightCode, new Flight(new ArrayList<>(), flightCode, airlineName));
        }
        flights.get(flightCode).addPassenger(new Passenger(bookingCode, flightCode, airlineName));
    }

    public Map<String, Flight> getFlights(){
        return flights;
    }

    public List<Flight> getFlightsList(List<String> flightCodes){
        List<Flight> aux = new ArrayList<>();
        for(String s : flightCodes){
            aux.add(flights.get(s));
        }
        return aux;
    }

    public Flight getFlight(String flightCode){
        return flights.get(flightCode);
    }

    public String getAirlineName() {
        return airlineName;
    }
}
