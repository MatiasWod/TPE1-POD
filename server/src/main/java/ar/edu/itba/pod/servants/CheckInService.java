package ar.edu.itba.pod.servants;

import ar.edu.itba.pod.checkIn.*;
import ar.edu.itba.pod.data.Airport;
import io.grpc.stub.StreamObserver;

public class CheckInService extends CheckInServiceGrpc.CheckInServiceImplBase {

    private final Airport airport = Airport.getInstance();
    @Override
    public void getCheckInCounters(Booking request, StreamObserver<CheckInCountersResponse> responseObserver) {
        CheckInCountersResponse response = airport.getPassengerCheckinInfo(request.getCode());
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getInLine(GetInlineRequest request, StreamObserver<GetInlineResponse> responseObserver) {
        GetInlineResponse response = airport.getPassengerInLine(request.getBooking(), request.getSectorName(), request.getCounterNumber());
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getPassengerStatus(Booking request, StreamObserver<GetPassengerStatusResponse> responseObserver) {
        GetPassengerStatusResponse response = airport.getPassengerStatus(request.getCode());
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
