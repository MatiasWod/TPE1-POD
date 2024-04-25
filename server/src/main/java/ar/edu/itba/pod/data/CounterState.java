package ar.edu.itba.pod.data;


//    string sectorName = 1;
//    int32 counterStart = 2;
//    int32 counterEnd = 3;
//    string airlineName = 4;
//    string flightCode = 5;
//    int32 people = 6;
public class CounterState {
    private final String sectorName;
    private final int counterStart;
    private final int counterEnd;
    private final String airlineName;
    private final String flightCode;
    private final int people;

    public CounterState(String sectorName, int counterStart, int counterEnd, String airlineName, String flightCode, int people) {
        this.sectorName = sectorName;
        this.counterStart = counterStart;
        this.counterEnd = counterEnd;
        this.airlineName = airlineName;
        this.flightCode = flightCode;
        this.people = people;
    }

    public String getSectorName() {
        return sectorName;
    }

    public int getCounterStart() {
        return counterStart;
    }

    public int getCounterEnd() {
        return counterEnd;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public String getFlightCode() {
        return flightCode;
    }

    public int getPeople() {
        return people;
    }
}
