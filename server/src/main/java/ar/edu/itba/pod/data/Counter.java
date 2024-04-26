package ar.edu.itba.pod.data;

import java.util.ArrayList;
import java.util.List;

public class Counter {
    private final int counterId;
    private final Sector sector;
    private final List<Flight> flights= new ArrayList<>();
    
    public Counter(int counterId, Sector sector){
        this.counterId = counterId;
        this.sector = sector;
    }

    public int getCounterId() {
        return counterId;
    }

    public Sector getSector() {
        return sector;
    }

    public List<Flight> getFlights() {
        return flights;
    }

    public void addFlight(Flight flight){
        flights.add(flight);
    }

    public boolean isFree(){
        return flights.isEmpty();
    }
}
