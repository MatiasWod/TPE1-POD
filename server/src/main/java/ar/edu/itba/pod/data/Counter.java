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

    public void freeCounter(){
        flights.clear();
    }

    public String getAirline(){
        if(flights.isEmpty()){
            return "d6de3e17fe2d2a40de4f6a836f84f519c9c5db8261b5e284bcde5b8dd3d1ea69";
        }
        return flights.get(0).getAirlineName();
    }

    public boolean isFree(){
        return flights.isEmpty();
    }
}
