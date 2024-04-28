package ar.edu.itba.pod.data;


import ar.edu.itba.pod.events.EventStatus;
import ar.edu.itba.pod.events.EventsResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class Airline {
    private final String airlineName;
    private final Map<String, Flight> flights = new HashMap<>();
    private List<Integer> countersId= new ArrayList<>();
    private BlockingQueue<EventsResponse> eventsQueue = null;
    private Object eventsLock = "eventsLock";

    public Airline(String airlineName) {
        this.airlineName = airlineName;
    }

    public void loadFlight(Passenger passenger){
        if(!flights.containsKey(passenger.getFlightCode())){
            flights.put(passenger.getFlightCode(), new Flight(new ArrayList<>(), passenger.getFlightCode(), passenger.getAirlineCode()));
        }
        flights.get(passenger.getFlightCode()).addPassenger(passenger);
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

    public BlockingQueue<EventsResponse> registerForEvents(){
        eventsQueue = new LinkedBlockingQueue<>();
        return eventsQueue;
    }

    public void unregisterForEvents(){
        EventsResponse.Builder eventsResponse = EventsResponse.newBuilder().setStatus(EventStatus.DESTROYED);
        eventsQueue.add(eventsResponse.build());
        eventsQueue = null;
    }
}
