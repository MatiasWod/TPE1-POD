package ar.edu.itba.pod.servants;

import ar.edu.itba.pod.commons.Empty;
import ar.edu.itba.pod.counterReservation.*;
import ar.edu.itba.pod.data.Airport;
import ar.edu.itba.pod.data.Utils.CheckInCountersDTO;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CounterReservationService extends counterReservationServiceGrpc.counterReservationServiceImplBase  {

    private final Airport airport = Airport.getInstance();

    @Override
    public void listSectors(Empty request, StreamObserver<SectorsInformationResponse> responseObserver) {
        SectorsInformationResponse response = SectorsInformationResponse.newBuilder().addAllSectors(
                airport.getSectors().stream().map(sector -> {
                    Sector.Builder sectorBuilder = Sector.newBuilder().setName(sector.getName());
                    sector.getCounters().forEach(counter -> sectorBuilder.addCounters(Counter.newBuilder().setCounterId(counter.getCounterId())));
                    return sectorBuilder.build();
                }).collect(Collectors.toList())
        ).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }


    @Override
    public void listCounters(ListCounterRequest request, StreamObserver<ListCounterResponse> responseStreamObserver){
        ListCounterResponse response = ListCounterResponse.newBuilder().addAllCountersInformation(
                airport.getCountersInRange(request.getSectorName(), request.getCounterStart(), request.getCounterEnd()).stream().map(
                        counterRange -> {
                            CountersInformation.Builder countersInformationBuilder = CountersInformation.newBuilder()
                                    .setFirstCounter(counterRange.getFirstCounter().getCounterId())
                                    .setLastCounter(counterRange.getLastCounterId())
                                    .setAirline(counterRange.getAirline());
                            List<String> flightsList = new ArrayList<>();

                            counterRange.getFirstCounter().getFlights().forEach(flight -> {
                                if (flight != null) {
                                    flightsList.add(flight.getFlightCode());
                                }
                            });

                            countersInformationBuilder.addAllFlights(flightsList);
                            if(counterRange.getFirstCounter().isStartOfRange()){
                                countersInformationBuilder.setPeople(counterRange.getFirstCounter().getQueueSize());
                            }
                            return countersInformationBuilder.build();
                        }).collect(Collectors.toList())
        ).build();
        responseStreamObserver.onNext(response);
        responseStreamObserver.onCompleted();
    }

    @Override
    public void assignCounters(AssignCountersRequest request, StreamObserver<AssignCountersResponse> responseObserver){
        AssignCountersResponse response = airport.assignCounters(request.getSector(),request.getCounterCount(), request.getAirline(), request.getFlightsList());
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void freeCounters(FreeCountersRequest request, StreamObserver<Empty> responseObserver) {
        airport.freeCounters(request.getSectorName(),request.getCounterFrom(),request.getAirlineName());
        responseObserver.onNext(Empty.newBuilder().build());
        responseObserver.onCompleted();
    }

    @Override
    public void checkInCounters(CheckInCountersRequest request, StreamObserver<CheckInCountersReservationResponse> responseObserver) {
        List<CheckInCountersDTO> checkInCountersDTOS = airport.checkInCounters(request.getSectorName(), request.getFromVal(), request.getAirlineName());

        CheckInCountersReservationResponse response = CheckInCountersReservationResponse.newBuilder().addAllBookingCounterId(
                checkInCountersDTOS.stream().map(checkInCountersDTO -> {
                    BookingCounterId.Builder bookingCounterIdBuilder = BookingCounterId.newBuilder()
                            .setIsEmpty(checkInCountersDTO.isEmpty())
                            .setCounterId(checkInCountersDTO.getCounterId());
                    if (!checkInCountersDTO.isEmpty()) {
                        bookingCounterIdBuilder.setBooking(checkInCountersDTO.getPassenger().getBookingCode());
                        bookingCounterIdBuilder.setFlight(checkInCountersDTO.getPassenger().getFlightCode());
                    }
                    return bookingCounterIdBuilder.build();
                }).collect(Collectors.toList())
        ).build();

                    responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void listPendingAssignments(ListPendingAssignmentsRequest request, StreamObserver<ListPendingAssignmentsResponse> responseObserver) {
        ListPendingAssignmentsResponse response = airport.listPendingAssignments(request.getSectorName());
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
