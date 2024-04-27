package ar.edu.itba.pod.data;

import java.util.*;

public class Airport {
    private static Airport airport = null;
    private final Map<String, Sector> sectors = new HashMap<>();
    private final Object sectorLock = "sectorLock";
    private final Map<String, Airline> airlines = new HashMap<>();
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
            //TODO revisar
            for (Airline m : airlines.values()) {
                if (m.getFlights().containsKey(flightCode) && (!m.getFlights().get(flightCode).getAirlineName().equals(airlineName))) {
                    throw new IllegalArgumentException();
                }
            }
            for (Airline a : airlines.values()){
                for(Flight f : a.getFlights().values()){
                    if(f.getPassengerList().contains(new Passenger(bookingCode,flightCode,airlineName))){
                        throw new IllegalArgumentException();
                    }
                }
            }
            airlines.putIfAbsent(airlineName, new Airline(airlineName));
            airlines.get(airlineName).loadFlight(airlineName, flightCode, bookingCode);
        }
    }


    //Query Service
    public List<CounterState> getCountersState(String sectorName){
        synchronized (sectorLock){
            if (!sectors.containsKey(sectorName)){
                throw new IllegalArgumentException();
            }

            if(sectors.get(sectorName).getCounters().isEmpty()){
                //TODO implementar
                return null;
            }
            List<CounterState> counterStates = new ArrayList<>();
            List<Counter> counters = sectors.get(sectorName).getCounters();
            for (Counter counter: counters){
                //TODO ordenar clases
//                counterStates.add(new CounterState(sectorName,counters.getFirst().getCounterId(),counters.getLast().getCounterId(),counter.,counter.getFlightCode(),counter.getPeople()));
            }
            return counterStates;
        }
    }

    public List<CheckInHistoryInfo> getCheckInHistory(String sectorName,String airlineName ){
        synchronized (sectorLock){
            if (!sectors.containsKey(sectorName)){
                throw new IllegalArgumentException();
            }
            List<CheckInHistoryInfo> checkInHistoryInfos = new ArrayList<>();
            for (Counter counter: sectors.get(sectorName).getCounters()){
                //TODO ordenar clases
//                for (CheckIn checkIn: counter.getCheckIns()){
//                    checkInHistoryInfos.add(new CheckInHistoryInfo(checkIn.getBookingCode(),checkIn.getFlightCode(),checkIn.getAirlineCode(),sectorName,counter.getCounterId()));
//                }
            }
            return checkInHistoryInfos;
        }
    }


    public List<CounterRange> getCountersInRange(String sectorName, int min, int max){
        synchronized (sectorLock){
            if(!sectors.containsKey(sectorName)){
                throw new IllegalArgumentException();
            }
            if( (max-min) >= 1 ){
                return sectors.get(sectorName).getCountersInRange(min,max);
            }else{
                throw new IllegalArgumentException();
            }
        }
    }

    public void assignCounters(String sectorName, int counterCount, String airlineString, List<String> flights ){
        synchronized (sectorLock){
            if(!sectors.containsKey(sectorName)){
                throw new IllegalArgumentException();
            }
            if(!airlines.containsKey(airlineString)){
                throw new IllegalArgumentException();
            }
            //TODO No se agregaron pasajeros esperados con el código de vuelo, para al menos uno de los vuelos solicitados
            //TODO check that all flights EXISTS AND have passangers waiting (ARRIBA)
            sectors.get(sectorName).assignCounters(counterCount, airlines.get(airlineString), flights);
        }

    }

    public void freeCounters(String sectorName, int counterFrom, String airlineName){
        synchronized (sectorLock){
            //TODO falta ver si existen pasajeros esperando a ser atendidos en la cola del rango
            if(!airlines.containsKey(airlineName)){
                throw new IllegalArgumentException();
            }
            if (!sectors.containsKey(sectorName) || sectors.get(sectorName).getCounters().isEmpty() ||
                !sectors.get(sectorName).getCounters().get(0).getAirline().equals(airlineName)){
                throw new IllegalArgumentException();
            }
            sectors.get(sectorName).freeCounters(counterFrom,airlines.get(airlineName));
        }
    }
}
