package ar.edu.itba.pod.servants;

import ar.edu.itba.pod.checkIn.Booking;
import ar.edu.itba.pod.checkIn.CheckInCountersResponse;
import ar.edu.itba.pod.checkIn.CheckInServiceGrpc;
import io.grpc.stub.StreamObserver;

public class CheckInService extends CheckInServiceGrpc.CheckInServiceImplBase {
    @Override
    public void getCheckInCounters(Booking request, StreamObserver<CheckInCountersResponse> responseObserver) {
        // Fetch data using request information
        System.out.println(request.getCode());

        CheckInCountersResponse response = CheckInCountersResponse.newBuilder()
                .setFlightCode("AA123")
                .setAirline("American Airlines")
                .setCounters("[3-4]")
                .setSector("C")
                .setQueueSize(7)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
