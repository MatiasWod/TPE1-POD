package ar.edu.itba.pod.data;

import ar.edu.itba.pod.checkIn.PassengerStatus;
import ar.edu.itba.pod.data.Exceptions.PassengerQueueNotEmptyException;
import ar.edu.itba.pod.data.Utils.CheckInCountersDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Counter {
    private final int counterId;
    private final Sector sector;
    private final List<Flight> flights= new ArrayList<>();
    private boolean startOfRange = false;
    private int rangeLength = 0;
    private BlockingQueue<Passenger> passengerQueue;
    
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
        if (!passengerQueue.isEmpty()) {
            throw new PassengerQueueNotEmptyException();
        }

        if (startOfRange) {
            startOfRange = false;
            passengerQueue = null;
            rangeLength = 0;
        }

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

    public boolean isStartOfRange() {
        return startOfRange;
    }
    public void assignStartOfRange(int counterCount) {
        startOfRange = true;
        rangeLength = counterCount;
        passengerQueue = new LinkedBlockingQueue<>() {
        };
    }

    public List<CheckInCountersDTO> consumePassengerQueue() {
        List<CheckInCountersDTO> toRet = new ArrayList<>();
        for (int cId = getCounterId(); cId < getCounterId() + rangeLength; cId++) {
            Passenger passenger = passengerQueue.poll();
            CheckInCountersDTO passengerData = new CheckInCountersDTO(cId);

            if (passenger != null) {
                passengerData.setPassenger(passenger);
                passenger.setStatus(PassengerStatus.checkedIn);
            }else {
                passengerData.setEmpty(true);
            }

            toRet.add(passengerData);
            }
        return toRet;
    }

    public Integer getQueueSize() {
        return passengerQueue.size();
    }

    public int getRangeLength() {
        return rangeLength;
    }

    public boolean containsFlightCode(String flightCode) {
        return flights.stream().anyMatch(f -> Objects.equals(f.getFlightCode(), flightCode));
    }

    public boolean checkIfPassengerInQueue(Passenger passenger) {
        return passengerQueue.contains(passenger);
    }

    public int addPassengerToQueue(Passenger passenger) {
        passengerQueue.add(passenger);
        return passengerQueue.size() - 1;
    }

    /*
    TODO: Maybe usar esto mas adelante
    public void checkInCounters(int counterFrom, Airline airline) {
        PriorityQueue<Counter> pq = new PriorityQueue<>(counters.subList(counterFrom, counters.size()));
        while (!pq.isEmpty()) {
            Counter counter = pq.poll();
            if (counter.getAirline().equals(airline.getAirlineName())) {
                counter.checkIn();
            }
            else {
                return;
            }
        }
    } */
}
