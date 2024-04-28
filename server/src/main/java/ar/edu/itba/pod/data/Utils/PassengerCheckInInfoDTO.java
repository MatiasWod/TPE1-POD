package ar.edu.itba.pod.data.Utils;

public class PassengerCheckInInfoDTO {
    private String flightCode;
    private String airline;
    private int fromCounter;
    private int toCounter;
    private String sector;
    private int queueSize;

    public PassengerCheckInInfoDTO(String flightCode, String airline, int fromCounter, int toCounter, String sector, int queueSize) {
        this.flightCode = flightCode;
        this.airline = airline;
        this.fromCounter = fromCounter;
        this.toCounter = toCounter;
        this.sector = sector;
        this.queueSize = queueSize;
    }

    public PassengerCheckInInfoDTO() {

    }

    public String getFlightCode() {
        return flightCode;
    }

    public String getAirline() {
        return airline;
    }

    public int getFromCounter() {
        return fromCounter;
    }

    public int getToCounter() {
        return toCounter;
    }

    public String getSector() {
        return sector;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setFlightCode(String flightCode) {
        this.flightCode = flightCode;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public void setFromCounter(int fromCounter) {
        this.fromCounter = fromCounter;
    }

    public void setToCounter(int toCounter) {
        this.toCounter = toCounter;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }
}
