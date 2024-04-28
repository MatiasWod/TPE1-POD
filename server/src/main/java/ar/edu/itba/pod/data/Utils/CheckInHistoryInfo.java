package ar.edu.itba.pod.data.Utils;

public class CheckInHistoryInfo {
    private final String sectorName;
    private final int counterId;
    private final String airlineName;
    private final String flightCode;
    private final String bookingCode;

    public CheckInHistoryInfo(String sectorName, int counterId, String airlineName, String flightCode, String bookingCode) {
        this.sectorName = sectorName;
        this.counterId = counterId;
        this.airlineName = airlineName;
        this.flightCode = flightCode;
        this.bookingCode = bookingCode;
    }

    public String getSectorName() {
        return sectorName;
    }

    public int getCounterId() {
        return counterId;
    }

    public String getAirlineName() {
        return airlineName;
    }

    public String getFlightCode() {
        return flightCode;
    }

    public String getBookingCode() {
        return bookingCode;
    }


}
