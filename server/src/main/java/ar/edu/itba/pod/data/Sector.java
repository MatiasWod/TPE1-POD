package ar.edu.itba.pod.data;

import ar.edu.itba.pod.counterReservation.AssignCountersResponse;
import ar.edu.itba.pod.counterReservation.ListPendingAssignmentsResponse;
import ar.edu.itba.pod.counterReservation.PendingAssignmentsInformation;
import ar.edu.itba.pod.data.Exceptions.AirlineNotMatchesCounterRangeException;
import ar.edu.itba.pod.data.Exceptions.NotRangeAssignedException;
import ar.edu.itba.pod.data.Utils.AirlineCounterRequest;
import ar.edu.itba.pod.data.Utils.CheckInCountersDTO;
import ar.edu.itba.pod.data.Utils.CheckInHistoryInfo;
import ar.edu.itba.pod.events.EventStatus;
import ar.edu.itba.pod.events.EventsResponse;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

public class Sector {
    private final String name;
    private final List<Counter> counters = new ArrayList<>();
    private final BlockingQueue<AirlineCounterRequest> airlineBlockingQueue = new LinkedBlockingQueue<>();

    public Sector(String name){
        this.name = name;
    }

    public void addCounter(int counterId){
        counters.add(new Counter(counterId, this));
    }

    public List<Counter> getCounters(){
        return counters;
    }

    public String getName(){
        return name;
    }

    public List<CounterRange> getCountersInRange(int min, int max){
        List<CounterRange> countersInRange = new ArrayList<>();
        String lastCounterAirline = "9f870a64c92bf73caa394828f91cde686da2131dd9064d2cf18f6387eb5273ff";
        CounterRange current = null;

        for(Counter counter : counters){
            if(counter.getCounterId() >= min){
                if(lastCounterAirline.equals(counter.getAirline())){
                    current.setCounterId(counter.getCounterId());
                } else {
                    current = new CounterRange(counter, counter.getCounterId());
                    countersInRange.add(current);
                    lastCounterAirline = counter.getAirline();
                }
            }if(counter.getCounterId() >= max){
                break;
            }
        }
        return countersInRange;
    }

    public AssignCountersResponse assignCounters(int counterCount, Airline airline, List<String> flights){
        int startPosition = getPositionForCountersAssignment(counterCount);
        if(startPosition == -1){
            airlineBlockingQueue.add(new AirlineCounterRequest(airline, counterCount, flights));

            //Notify the airline
            EventsResponse.Builder eventsResponseBuilder = EventsResponse.newBuilder().setStatus(EventStatus.COUNTER_ASSIGNMENT_PENDING)
                    .setCounterCount(counterCount).setSector(name).setPendingAhead(airlineBlockingQueue.size()-1);
            flights.forEach(flight -> {
                if (flight!=null){
                    eventsResponseBuilder.addFlights(flight);
                }});
            airline.notifyEvent(eventsResponseBuilder.build());

            return AssignCountersResponse.newBuilder().setFirstPosition(-1).setPendingAhead(airlineBlockingQueue.size()-1).build();
        }
        addFlightsToCounters(startPosition,counterCount,flights,airline);
        return AssignCountersResponse.newBuilder().setFirstPosition(startPosition).setPendingAhead(-1).build();
    }

    private int getPositionForCountersAssignment(int counterCount){
        int firstCounterId = -1;
        int counterContCount = 0;
        for(Counter counter : counters){
            if(counter.isFree()){
                if(counter.getCounterId() == firstCounterId+counterContCount){
                    counterContCount++;

                } else{
                    firstCounterId = counter.getCounterId();
                    counterContCount=1;
                }
            } else{
                counterContCount = 0;
                firstCounterId = -1;
            }
            if(counterContCount == counterCount){
                return firstCounterId;
            }
        }
        return -1;
    }

    public void freeCounters(int counterFrom, Airline airline) {
        int freedAmount = 0;
        List<Flight> flights = new ArrayList<>();

        for(Counter counter : counters){
            if(counter.getCounterId() >= counterFrom && counter.getAirline().equals(airline.getAirlineName())){
                counter.freeCounter();
                freedAmount++;
                flights = counter.getFlights();
            }
        }

        if(freedAmount != 0){
            //Notify the airline
            EventsResponse.Builder eventsResponseBuilder = EventsResponse.newBuilder().setStatus(EventStatus.COUNTER_FREED).setSector(name)
                    .setFirstCounter(counterFrom).setLastCounter(counterFrom+freedAmount-1);
            flights.forEach(flight -> {
                if (flight!=null){
                    eventsResponseBuilder.addFlights(flight.getFlightCode());
                }});
            airline.notifyEvent(eventsResponseBuilder.build());


            // Checks if the next one from the queue can be assigned
            if (!airlineBlockingQueue.isEmpty()) {
                AirlineCounterRequest aux = airlineBlockingQueue.peek();
                int startPosition = getPositionForCountersAssignment(aux.getCountersAmount());
                if (startPosition > 0) {
                    addFlightsToCounters(startPosition, counterFrom,aux.getFlights(),aux.getAirline());
                    airlineBlockingQueue.remove();

                    int airlinesAhead = 0;
                    for(AirlineCounterRequest airlineRequest : airlineBlockingQueue){
                        //Notify the airlines
                        EventsResponse.Builder eventsResponseBuilder2 = EventsResponse.newBuilder().setStatus(EventStatus.COUNTER_ASSIGNMENT_PENDING_CHANGE)
                                .setCounterCount(airlineRequest.getCountersAmount()).setSector(name).setPendingAhead(airlinesAhead);
                        airlineRequest.getFlights().forEach(flight -> {
                            if (flight!=null){
                                eventsResponseBuilder2.addFlights(flight);
                            }});
                        airline.notifyEvent(eventsResponseBuilder2.build());
                        airlinesAhead++;
                    }
                }
            }
        }
    }

    private void addFlightsToCounters(int startPosition, int counterCount, List<String> flights, Airline airline){
        for(Counter counter : counters){
            if(counter.getCounterId() >= startPosition && counter.getCounterId() < startPosition + counterCount){
                if (counter.getCounterId() == startPosition) {
                    counter.assignStartOfRange(counterCount);
                }
                for(String flight : flights){
                    counter.addFlight(airline.getFlight(flight));
                }
            }
        }
        //Notify the airline
        EventsResponse.Builder eventsResponseBuilder = EventsResponse.newBuilder().setStatus(EventStatus.COUNTER_ASSIGNMENT_SUCCESS).setCounterCount(counterCount)
                .setFirstCounter(startPosition).setLastCounter(startPosition+counterCount-1).setSector(name);
        flights.forEach(flight -> {
            if (flight!=null){
                eventsResponseBuilder.addFlights(flight);
            }});
        airline.notifyEvent(eventsResponseBuilder.build());
    }


    public List<CheckInCountersDTO> checkInCounters(int counterFrom, Airline airline, List<CheckInHistoryInfo> checkInHistoryInfoList) {
        // Aca quiero fijarme si en counterFrom hay un rango asignado y en ese caso quiero sacar cosas de la cola
        for (Counter counter : counters) {
            if (counter.getCounterId() == counterFrom) {
                if (!counter.isStartOfRange()) {
                    throw new NotRangeAssignedException(counterFrom);
                }
                if (!counter.getAirline().equals(airline.getAirlineName())) {
                    throw new AirlineNotMatchesCounterRangeException();
                }
                return counter.consumePassengerQueue(checkInHistoryInfoList);
            }
        }
        return Collections.emptyList();
    }

    public ListPendingAssignmentsResponse getPendingAssignments(){
        List<AirlineCounterRequest> aux = airlineBlockingQueue.stream().sorted().toList();
        List<PendingAssignmentsInformation> pendingAssignmentsInformationList = new ArrayList<>();
        int i = 0;
        for (AirlineCounterRequest acr : aux){
            for (String f :acr.getFlights()) {
                pendingAssignmentsInformationList.add(PendingAssignmentsInformation.newBuilder()
                        .setAirlineName(acr.getAirline().getAirlineName())
                        .setCounter(acr.getCountersAmount())
                        .setFlightCode(i,f)
                        .build());
                i++;
            }
            i = 0;
        }
        return ListPendingAssignmentsResponse.newBuilder()
                .addAllPendingAssignmentsInformation(pendingAssignmentsInformationList)
                .build();
    }

    public BlockingQueue<AirlineCounterRequest> getAirlineBlockingQueue() {
        return airlineBlockingQueue;
    }
}
