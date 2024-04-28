package ar.edu.itba.pod.data;

import ar.edu.itba.pod.counterReservation.AssignCountersResponse;
import ar.edu.itba.pod.data.Exceptions.AirlineNotMatchesCounterRangeException;
import ar.edu.itba.pod.data.Exceptions.NotRangeAssignedException;
import ar.edu.itba.pod.data.Utils.AirlineCounterRequest;
import ar.edu.itba.pod.data.Utils.CheckInCountersDTO;
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
//        freeCounters.add(new Counter(counterId, this)); // TODO: No se porque pusieron esto pero explota
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
            //TODO CHANGE AIRLINE COMPARISION TO COUNTERRANGE COMPARISION (CHECK IF CONTIANS THE SAME FLIGHT ALSO)
            if(counter.getCounterId() >= min){
                if(lastCounterAirline.equals(counter.getAirline())){
                    current.setCounterId(counter.getCounterId());
                } else {
                    current = new CounterRange(counter, counter.getCounterId());
                    countersInRange.add(current);
                    lastCounterAirline = counter.getAirline();
                }
            }else if(counter.getCounterId() > max){
                break;
            }
        }
        return countersInRange;
    }

    public AssignCountersResponse assignCounters(int counterCount, Airline airline, List<String> flights){
        int startPosition = getPositionForCountersAssignment(counterCount);
        if(startPosition == -1){
            System.out.println("Adding wachito to queue");
            airlineBlockingQueue.add(new AirlineCounterRequest(airline,counterCount,flights));
            return AssignCountersResponse.newBuilder().setFirstPosition(-1).setPendingAhead(airlineBlockingQueue.size()).build();
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
        //TODO revisar el método
        for(Counter counter : counters){
            if(counter.getCounterId() >= counterFrom && counter.getAirline().equals(airline.getAirlineName())){
                counter.freeCounter();
            }
        }
        if (!airlineBlockingQueue.isEmpty()) {
            AirlineCounterRequest aux = airlineBlockingQueue.peek();
            int startPosition = getPositionForCountersAssignment(aux.getCountersAmount());
            if (startPosition > 0) {
                addFlightsToCounters(startPosition, counterFrom,aux.getFlights(),aux.getAirline());
                airlineBlockingQueue.remove();
            }
        }
    }

    private void addFlightsToCounters(int startPosition, int counterCount, List<String> flights, Airline airline){
        for(Counter counter : counters){
            if(counter.getCounterId() >= startPosition && counter.getCounterId() < startPosition + counterCount){
                for(String flight : flights){
                    counter.addFlight(airline.getFlight(flight));
                }
                if (counter.getCounterId() == startPosition) {
                    counter.assignStartOfRange(counterCount);
                }
            }
        }
    }


    public List<CheckInCountersDTO> checkInCounters(int counterFrom, Airline airline) {
        // Aca quiero fijarme si en counterFrom hay un rango asignado y en ese caso quiero sacar cosas de la cola
        for (Counter counter : counters) {
            if (counter.getCounterId() == counterFrom) {
                if (!counter.isStartOfRange()) {
                    throw new NotRangeAssignedException();
                }
                if (!counter.getAirline().equals(airline.getAirlineName())) {
                    throw new AirlineNotMatchesCounterRangeException();
                }
                return counter.consumePassengerQueue();
            }
        }
        return Collections.emptyList();
    }
}
