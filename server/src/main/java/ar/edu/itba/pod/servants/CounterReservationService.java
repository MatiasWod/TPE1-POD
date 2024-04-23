package ar.edu.itba.pod.servants;

import ar.edu.itba.pod.checkIn.Booking;
import ar.edu.itba.pod.checkIn.CheckInCountersResponse;
import ar.edu.itba.pod.commons.Empty;
import ar.edu.itba.pod.counterReservation.*;
import ar.edu.itba.pod.data.Airport;
import io.grpc.stub.StreamObserver;

import java.util.stream.Collectors;

public class CounterReservationService extends counterReservationServiceGrpc.counterReservationServiceImplBase  {

    private final Airport airport = Airport.getInstance();

    @Override
    public void getSectors(Empty request, StreamObserver<SectorsInformationResponse> responseObserver) {
        /*
        SectorsInformationResponse response = SectorsInformationResponse.newBuilder().addAllSectors(
                airport.getSectors().stream().map(sector -> Sector
                        .newBuilder()
                        .setName(sector.getName())
                        .setCounters()
                        .build()).collect(Collectors.toList())
        ).build();
        */
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
}
