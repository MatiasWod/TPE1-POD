package ar.edu.itba.pod.servants;

import ar.edu.itba.pod.checkIn.*;
import ar.edu.itba.pod.data.Airport;
import ar.edu.itba.pod.data.Utils.PassengerCheckInInfoDTO;
import io.grpc.stub.StreamObserver;

public class CheckInService extends CheckInServiceGrpc.CheckInServiceImplBase {

    private final Airport airport = Airport.getInstance();
    @Override
    public void getCheckInCounters(Booking request, StreamObserver<CheckInCountersResponse> responseObserver) {
        // TODO: Fetch data using request information
        CheckInCountersResponse response = airport.getPassengerCheckinInfo(request.getCode());
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getInLine(GetInlineRequest request, StreamObserver<GetInlineResponse> responseObserver) {
        // TODO: Call fucking service dude
        System.out.println(request.getBooking());

        GetInlineResponse response = GetInlineResponse.newBuilder()
                .setFlightCode("AA123")
                .setAirline("American Airlines")
                .setCounters("[3-4]")
                .setSector("C")
                .setQueueSize(7)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getPassengerStatus(Booking request, StreamObserver<GetPassengerStatusResponse> responseObserver) {
        // TODO: Call fucking service dude
        System.out.println(request.getCode());

        GetPassengerStatusResponse response = GetPassengerStatusResponse.newBuilder()
                .setCounters("[3-4]")
                .setSector("C")
                .setQueueSize(7)
                .setStatus(PassengerStatus.onQueue)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
