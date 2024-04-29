package ar.edu.itba.pod.data;

import ar.edu.itba.pod.checkIn.PassengerStatus;
import ar.edu.itba.pod.data.Exceptions.StillPassengersInLineException;
import ar.edu.itba.pod.data.Utils.CheckInCountersDTO;
import ar.edu.itba.pod.data.Utils.CheckInHistoryInfo;
import ar.edu.itba.pod.events.EventStatus;
import ar.edu.itba.pod.events.EventsResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class Counter {
    private final int counterId;
    private final Sector sector;
    private final List<Flight> flights= new ArrayList<>();
    private boolean startOfRange = false;
    private int rangeLength = 0;
    private BlockingQueue<Passenger> passengerQueue;
    private final Object passangerLock = "passangerLock";
    
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
        if (startOfRange) {
            for (Passenger passenger : flight.getPassengerList()) {
                passenger.setStatus(PassengerStatus.COUNTER_ASSIGNED);
                passenger.setSector(sector.getName());
                passenger.setCounterFrom(this);
            }
        }
    }

    public void freeCounter(){
        if (startOfRange) {
            // Aca se puede chequear si quedaban pasajeros en la fila
            for (Flight flight : flights) {
                if (!flight.getPassengerList().isEmpty()) {
                    throw new StillPassengersInLineException(passengerQueue.size());
                }
            }
            startOfRange = false;
            passengerQueue = null;
            rangeLength = 0;
        }

        flights.clear();
    }

    public String getAirline(){
        if(flights.isEmpty()){
            return "-";
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

    public List<CheckInCountersDTO> consumePassengerQueue(List<CheckInHistoryInfo> checkInHistoryInfoList) {
        List<CheckInCountersDTO> toRet = new ArrayList<>();
        for (int cId = getCounterId(); cId < getCounterId() + rangeLength; cId++) {
            Passenger passenger = passengerQueue.poll();
            CheckInCountersDTO passengerData = new CheckInCountersDTO(cId);

            if (passenger != null) {
                checkInHistoryInfoList.add(new CheckInHistoryInfo(sector.getName(), cId, passenger.getAirlineCode(), passenger.getFlightCode(), passenger.getBookingCode()));
                passenger.setStatus(PassengerStatus.CHECKED_IN);
                passenger.setCheckedInAtCounter(cId);
                passengerData.setPassenger(passenger);

                //Notify the airline
                Airport airport = Airport.getInstance();
                EventsResponse eventsResponse = EventsResponse.newBuilder().setStatus(EventStatus.CHECKIN_SUCCESS)
                        .setBookingCode(passenger.getBookingCode()).addFlights(passenger.getFlightCode()).setFirstCounter(counterId)
                        .setSector(passenger.getSector()).build();
                airport.notifyAirline(passenger.getAirlineCode(), eventsResponse);

            }else {
                passengerData.setEmpty(true);
            }

            toRet.add(passengerData);
            }
        return toRet;
    }

    public Integer getQueueSize() {
        synchronized (passangerLock){
            if (passengerQueue == null) {
                return 0;
            }
            return passengerQueue.size();
        }
    }

    public int getPeopleInFront(Passenger passenger) {
        int peopleInLine = 0;
        for (Passenger pass : passengerQueue) {
            if (pass.equals(passenger)) {
                break;
            }
            peopleInLine++;
        }
        return peopleInLine;
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

    public void addPassengerToQueue(Passenger passenger) {
        passengerQueue.add(passenger);
        passenger.setStatus(PassengerStatus.ON_QUEUE);

        //Notify the airline
        Airport airport = Airport.getInstance();
        EventsResponse.Builder eventsResponseBuilder = EventsResponse.newBuilder().setStatus(EventStatus.PASSENGER_QUEUE_SUCCESS)
                .setBookingCode(passenger.getBookingCode()).addFlights(passenger.getFlightCode()).setPendingAhead(passengerQueue.size() - 1)
                .setSector(passenger.getSector()).setFirstCounter(passenger.getCounterFrom().getCounterId())
                .setLastCounter(passenger.getCounterFrom().getRangeLength()+passenger.getCounterFrom().getCounterId()-1);
        airport.notifyAirline(passenger.getAirlineCode(), eventsResponseBuilder.build());

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
