package ar.edu.itba.pod.data;

import java.util.*;

public class Airport {
    private static Airport airport = null;
    private final Map<String, List<Counter>> sectors = new HashMap<>();
    private final Object sectorLock = "sectorLock";
    private int globalCounterNumber = 1;
    private static final int COUNTERS_INCORRECT_COUNT = 0;

    private Airport(){

    }

    public static synchronized Airport getInstance(){
        if(airport == null){
            airport = new Airport();
        }
        return airport;
    }

    public void addSector(String sectorName){
        synchronized (sectorLock){
            if(sectors.containsKey(sectorName)){
                throw new IllegalArgumentException();
            }
            sectors.put(sectorName,new ArrayList<>());
        }
    }

    public void addCounters(String sectorName, int counterCount){
        synchronized (sectorLock){
            if (!sectors.containsKey(sectorName) || counterCount <= COUNTERS_INCORRECT_COUNT){
                throw new IllegalArgumentException();
            }
            //TODO ver si esta parte se puede hacer más orientada a objetos.
            for(; counterCount > COUNTERS_INCORRECT_COUNT; globalCounterNumber++) {
                sectors.get(sectorName).add(new Counter(globalCounterNumber));
                counterCount--;
            }
        }
    }

    public void loadPassengerSet(String bookingCode, String flightCode, String airlineName){
        //TODO completar
    }
}
