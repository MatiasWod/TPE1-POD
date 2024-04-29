package ar.edu.itba.pod.data;

import ar.edu.itba.pod.checkIn.CheckInCountersResponse;
import ar.edu.itba.pod.checkIn.GetInlineResponse;
import ar.edu.itba.pod.checkIn.GetPassengerStatusResponse;
import ar.edu.itba.pod.checkIn.PassengerStatus;
import ar.edu.itba.pod.counterReservation.AssignCountersResponse;
import ar.edu.itba.pod.counterReservation.ListPendingAssignmentsResponse;
import ar.edu.itba.pod.data.Exceptions.*;
import ar.edu.itba.pod.data.Utils.AirlineCounterRequest;
import ar.edu.itba.pod.data.Utils.CheckInCountersDTO;
import ar.edu.itba.pod.data.Utils.CheckInHistoryInfo;
import ar.edu.itba.pod.data.Utils.CounterState;
import ar.edu.itba.pod.events.EventsResponse;

import java.util.*;
import java.util.concurrent.BlockingQueue;

public class Airport {
    private static Airport airport = null;
    private final Map<String, Sector> sectors = new HashMap<>();
    private final Object sectorLock = "sectorLock";
    private final Map<String, Airline> airlines = new HashMap<>();
    private final Map<String, Passenger> passengers = new HashMap<>();
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
                throw new SectorAlreadyExistsException(sectorName);
            }
            sectors.put(sectorName,new Sector(sectorName));
        }
    }

    public Collection<Sector> getSectors(){
        synchronized (sectorLock){
            if (sectors.isEmpty()) throw new SectorsIsEmptyException();
            return sectors.values();
        }
    }

    public int addCounters(String sectorName, int counterCount){
        synchronized (sectorLock){
            if (!sectors.containsKey(sectorName)){
                throw new SectorNotFoundException(sectorName);
            }
            if (counterCount <= COUNTERS_INCORRECT_COUNT) {
                throw new CounterCountShouldBePositiveException();
            }
            //TE CAMBIE EL CODIGO CANE NO SE SI TIENE SENTIDO
            int firstCounter = globalCounterNumber;
            while(counterCount > 0){
                Sector sector = sectors.get(sectorName);
                sector.addCounter(globalCounterNumber);
                counterCount--;
                globalCounterNumber++;
            }
            return firstCounter;
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
                    throw new FlightCodeAlreadyExistsInOtherAirlineException(flightCode, m.getFlights().get(flightCode).getAirlineName());
                }
            }

            if(passengers.get(bookingCode) != null) {
                throw new PassengerAlreadyExistsException(bookingCode);
            }
            Passenger passenger = new Passenger(bookingCode, flightCode, airlineName, PassengerStatus.NEW_BORN);
            passengers.put(bookingCode, passenger);

            airlines.putIfAbsent(airlineName, new Airline(airlineName));
            airlines.get(airlineName).loadFlight(passenger);
        }
    }


    //Query Service
    public List<CounterState> getAllCountersState(){
        synchronized (sectorLock){
            List<CounterState> counterStates = new ArrayList<>();
            for(Sector sector : sectors.values()){
                counterStates.addAll(getCountersState(sector.getName()));
            }
            return counterStates;
        }
    }

    public List<CounterState> getCountersState(String sectorName) {
        synchronized (sectorLock) {
            if (!sectors.containsKey(sectorName)) {
                throw new IllegalArgumentException();
            }

            if (sectors.get(sectorName).getCounters().isEmpty()) {
                //TODO implementar
                return null;
            }
            List<CounterState> counterStates = new ArrayList<>();
            Sector sector = sectors.get(sectorName);
            int startCounterId = -1;
            int lastCounterId= -1;
            for (Counter counter : sector.getCounters()) {
                if (counter.isStartOfRange()) {
                    if (startCounterId != -1) {
                        counterStates.add(new CounterState(sectorName, startCounterId, lastCounterId, counter.getAirline(), counter.getFlights(), counter.getQueueSize()));
                        startCounterId = -1;
                    }
                    counterStates.add(new CounterState(sectorName, counter.getCounterId(), counter.getCounterId() + counter.getRangeLength() - 1, counter.getAirline(), counter.getFlights(), counter.getQueueSize()));
                }else if (!counter.isFree()){
                    continue;
                } else if (lastCounterId!=-1 && (counter.getCounterId() != (lastCounterId + 1))){
                    counterStates.add(new CounterState(sectorName, startCounterId, lastCounterId, counter.getAirline(), counter.getFlights(), counter.getQueueSize()));
                    startCounterId = counter.getCounterId();
                    lastCounterId = counter.getCounterId();
                } else {
                    if (startCounterId == -1) {
                        startCounterId = counter.getCounterId();
                    }
                    lastCounterId = counter.getCounterId();
                }

            }
            if (startCounterId != -1) {
                int lastIndex=sector.getCounters().size()-1;
                counterStates.add(new CounterState(sectorName, startCounterId, lastCounterId, sector.getCounters().get(lastIndex).getAirline(), sector.getCounters().get(lastIndex).getFlights(), sector.getCounters().get(lastIndex).getQueueSize()));
            }
            return counterStates;
        }
    }

    public List<CheckInHistoryInfo> getCheckInHistory(String sectorName, String airlineName ){
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
                throw new SectorNotFoundException(sectorName);
            }
            if( (max-min) >= 1 ){
                List<CounterRange> toRet = sectors.get(sectorName).getCountersInRange(min,max);
                if (toRet.isEmpty()) throw new BadCounterRangeException(min, max);
                return toRet;
            }else{
                throw new BadCounterRangeException(min, max);
            }
        }
    }

    public AssignCountersResponse assignCounters(String sectorName, int counterCount, String airlineString, List<String> flights ){
        synchronized (sectorLock){
            if(!sectors.containsKey(sectorName)){
                throw new SectorNotFoundException(sectorName);
            }
            if(!airlines.containsKey(airlineString)){
                throw new AirlineNotFoundException(airlineString);
            }
            for (String flight : flights) {
                for (Airline airline : airlines.values()) {
                    if(airline.getFlights().containsKey(flight)) {
                        if (!Objects.equals(airline.getAirlineName(), airlineString)) {
                            throw new AirlineNotMatchesFlight(flight, airline.getAirlineName());
                        }
                        if (airline.getFlight(flight).getPassengerList().isEmpty()){
                            throw new NoPassengersInFlightException(flight);
                        }
                    }
                }
            }
            Sector sector = sectors.get(sectorName);
            for (Counter counter : sector.getCounters()) {
                Optional<Flight> flight = counter.getFlights().stream().filter(f -> flights.contains(f.getFlightCode())).findFirst();
                if (flight.isPresent() && flight.get().getAirlineName().equals(airlineString)) {
                    throw new CounterRangeAlreadyAssignedToflight(flight.get().getFlightCode());
                }
            }
            for (AirlineCounterRequest air : sector.getAirlineBlockingQueue()) {
                for (String f : air.getFlights()) {
                    if (flights.contains(f)) {
                        throw new FlightCodeAlreadyInCounterRangeQueue(f);
                    }
                }
            }
            return sectors.get(sectorName).assignCounters(counterCount, airlines.get(airlineString), flights);
        }

    }

    public List<CheckInCountersDTO> checkInCounters(String sectorName, int counterFrom, String airlineName){
        synchronized (sectorLock){
            if(!sectors.containsKey(sectorName)){
                throw new IllegalArgumentException();
            }
            for (Counter counter: sectors.get(sectorName).getCounters()) {
                if (counter.getCounterId() == counterFrom && !counter.getAirline().equals(airlineName)) {
                    throw new IllegalArgumentException();
                }
            }
            // Esto se mete en el sector y consume la cola adecuada
            return sectors.get(sectorName).checkInCounters(counterFrom, airlines.get(airlineName));
        }
    }

    public void freeCounters(String sectorName, int counterFrom, String airlineName){
        synchronized (sectorLock){
            if(!sectors.containsKey(sectorName)){
                throw new SectorNotFoundException(sectorName);
            }
            if(!airlines.containsKey(airlineName)){
                throw new AirlineNotFoundException(airlineName);
            }

            for (Counter counter: sectors.get(sectorName).getCounters()) {
                if (counter.getCounterId() == counterFrom) {
                    if(!counter.getAirline().equals(airlineName)) {
                        throw new AirlineNotMatchesCounterRangeException();
                    }
                }
            }
            sectors.get(sectorName).freeCounters(counterFrom, airlines.get(airlineName));
        }
    }

    public CheckInCountersResponse getPassengerCheckinInfo(String booking) {
        // A partir de una booking conseguir el vuelo, aerolinea, counters y gente en la cola
        synchronized (sectorLock) {
            CheckInCountersResponse.Builder pDTO = CheckInCountersResponse.newBuilder();
            Passenger passenger = passengers.get(booking);

            if (passenger == null) {
                throw new BookingNotFoundException();
            }

            Sector sector = sectors.get(passenger.getSector());
            Counter counter = passenger.getCounterFrom();

            if (!counter.isStartOfRange()) {
                throw new NotRangeAssignedException();
            }
            if (!counter.containsFlightCode(passenger.getFlightCode())) {
                throw new FlightNotMatchesCounterException();
            }

            pDTO.setAirline(counter.getAirline());
            pDTO.setSector(sector.getName());
            pDTO.setQueueSize(counter.getQueueSize());
            pDTO.setFromCounter(counter.getCounterId());
            pDTO.setToCounter(counter.getCounterId() + counter.getRangeLength() - 1);

            pDTO.setFlightCode(passenger.getFlightCode());
            return pDTO.build();
        }
    }

    public GetInlineResponse getPassengerInLine(String booking, String sectorName, int counterNumber) {
        synchronized (sectorLock) {
            Passenger passenger = passengers.get(booking);
            GetInlineResponse.Builder builder = GetInlineResponse.newBuilder();

            if (passenger == null) {
                throw new BookingNotFoundException();
            }
            if (passenger.getStatus() == PassengerStatus.CHECKED_IN) {
                throw new PassengerAlreadyCheckedIn();
            }
            if (!sectors.containsKey(sectorName)) {
                throw new SectorNotFoundException(sectorName);
            }

            Sector sector = sectors.get(sectorName);
            for (Counter counter : sector.getCounters()) {
                if (counter.getCounterId() == counterNumber) {
                    if (!counter.isStartOfRange()) {
                        throw new BadCounterIdException();
                    }
                    if (!Objects.equals(counter.getAirline(), passenger.getAirlineCode())) {
                        throw new AirlineNotMatchesCounterRangeException();
                    }
                    if (counter.checkIfPassengerInQueue(passenger)) {
                        throw new PassengerAlreadyInQueue();
                    }
                    counter.addPassengerToQueue(passenger);
                    builder.setQueueSize(counter.getPeopleInFront(passenger))
                            .setCounterFrom(counter.getCounterId())
                            .setCounterTo(counter.getCounterId() + counter.getRangeLength() - 1);
                }
            }
            return builder.setFlightCode(passenger.getFlightCode())
                    .setBooking(booking)
                    .setAirline(passenger.getAirlineCode())
                    .setSector(sectorName)
                    .build();
        }
    }

    public ListPendingAssignmentsResponse listPendingAssignments(String sectorName){
        synchronized (sectorLock){
            if (!sectors.containsKey(sectorName)){
                throw new IllegalArgumentException();
            }
            return sectors.get(sectorName).getPendingAssignments();
        }
    }

    public GetPassengerStatusResponse getPassengerStatus(String booking) {
        synchronized (sectorLock) {
            Passenger passenger = passengers.get(booking);
            GetPassengerStatusResponse.Builder builder = GetPassengerStatusResponse.newBuilder();

            if (passenger == null) {
                throw new BookingNotFoundException();
            }

            builder.setFlightCode(passenger.getFlightCode());
            if (passenger.getCounterFrom() == null) {
                throw new NotRangeAssignedException();
            }

            Sector sector = sectors.get(passenger.getSector());
            Counter counter = passenger.getCounterFrom();

            builder.setAirline(counter.getAirline()).
            setSector(sector.getName()).
            setQueueSize(counter.getPeopleInFront(passenger)).
            setCounter(counter.getCounterId()).
            setCounterRange(counter.getRangeLength()).
            setStatus(passenger.getStatus());

            if (passenger.getStatus() == PassengerStatus.CHECKED_IN) {
                builder.setCounter(passenger.getCheckedInAtCounter());
            }
            return builder.build();
        }
    }

    public BlockingQueue<EventsResponse> RegisterAirlineForEvents(String airline){
        if(!airlines.containsKey(airline)){
            throw new AirlineNotFoundException(airline);
        }
        return airlines.get(airline).registerForEvents();
    }

    public void UnregisterAirlineForEvents(String airline){
        if(!airlines.containsKey(airline)){
            throw new AirlineNotFoundException(airline);
        }
        airlines.get(airline).unregisterForEvents();
    }

    public void notifyAirline(String airline, EventsResponse event){
        if(!airlines.containsKey(airline)){
            throw new AirlineNotFoundException(airline);
        }
        airlines.get(airline).notifyEvent(event);
    }
}
