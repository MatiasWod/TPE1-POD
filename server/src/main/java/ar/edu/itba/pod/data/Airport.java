package ar.edu.itba.pod.data;

import java.util.*;

public class Airport {
    private static Airport airport = null;
    private final Map<String, Sector> sectors = new HashMap<>();
    private final Object sectorLock = "sectorLock";
    private final Map<String,Map<String,Flight>> flights = new HashMap<>();
    private final Object flightsLock = "flightsLock";
    private int globalCounterNumber = 1;
    private static final int COUNTERS_INCORRECT_COUNT = 0; //Just not a magic number
    private static final int BOOKING_CODE_LENGTH = 6;

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
            sectors.put(sectorName,new Sector(sectorName));
        }
    }

    public Collection<Sector> getSectors(){
        synchronized (sectorLock){
            return sectors.values();
        }
    }

    public void addCounters(String sectorName, int counterCount){
        synchronized (sectorLock){
            if (!sectors.containsKey(sectorName) || counterCount <= COUNTERS_INCORRECT_COUNT){
                throw new IllegalArgumentException();
            }
            //TE CAMBIE EL CODIGO CANE NO SE SI TIENE SENTIDO
            while(counterCount > 0){
                Sector sector = sectors.get(sectorName);
                sector.addCounter(globalCounterNumber);
                counterCount--;
                globalCounterNumber++;
            }
            /*
            for(; counterCount > COUNTERS_INCORRECT_COUNT; globalCounterNumber++) {
                sectors.get(sectorName).add(new Counter(globalCounterNumber));
                counterCount--;
            }
            */
        }
    }

    public void loadPassengerSet(String bookingCode, String flightCode, String airlineName){
        //TODO dejarlo mas lindo
        synchronized (flightsLock){
            if (bookingCode.length() != BOOKING_CODE_LENGTH ||
                    flights.getOrDefault(airlineName,new HashMap<>())
                                    .getOrDefault(flightCode,new Flight(new ArrayList<>(),flightCode,airlineName))
                            .getPassengerList().contains(new Passenger(airlineName,flightCode,bookingCode))){
                throw new IllegalArgumentException();
            }
            for (Map<String, Flight> m:flights.values()){
                if(m.containsKey(flightCode) && !m.get(flightCode).getAirlineName().equals(airlineName)){
                    throw new IllegalArgumentException();
                }
            }
            flights.putIfAbsent(airlineName,new HashMap<>());
            flights.get(airlineName).putIfAbsent(flightCode,new Flight(new ArrayList<>(),flightCode,airlineName));
            flights.get(airlineName).get(flightCode).getPassengerList().add(new Passenger(bookingCode,flightCode,airlineName));
        }
    }
}
