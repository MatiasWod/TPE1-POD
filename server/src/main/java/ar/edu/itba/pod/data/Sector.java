package ar.edu.itba.pod.data;

import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;

public class Sector {
    private final String name;
    private final List<Counter> counters = new ArrayList<>();

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

    public void assignCounters(int counterCount, Airline airline, List<String> flights){
        int startPosition = getPositionForCountersAssignment(counterCount);
        if(startPosition == -1){
            //TODO ASSIGN PENDIENTES
        }
        for(Counter counter : counters){
            if(counter.getCounterId() >= startPosition && counter.getCounterId() < startPosition+counterCount){
                for(String flight : flights){
                    counter.addFlight(airline.getFlight(flight));
                }
            }
        }
    }

    public int getPositionForCountersAssignment(int counterCount){
        int firstCounterId = -1;
        int counterContCount = 0;
        for(Counter counter : counters){
            if(counter.isFree()){
                if(counter.getCounterId() == firstCounterId+1){
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
}
