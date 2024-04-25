package ar.edu.itba.pod.servants;

import ar.edu.itba.pod.query.*;
import io.grpc.stub.StreamObserver;

public class QueryServant extends QueryServiceGrpc.QueryServiceImplBase {

    @Override
    public void getCountersState(CountersStateRequest request, StreamObserver<CountersStateResponse> responseObserver) {
        // Fetch data using request information
        System.out.println("Sector Name: " + request.getSectorName());

        CountersStateResponse.Builder responseBuilder = CountersStateResponse.newBuilder();
        responseBuilder.addCountersState(
                CounterState.newBuilder()
                        .setSectorName(request.getSectorName())
                        .setCounterStart(1)
                        .setCounterEnd(10)
                        .setAirlineName("Sample Airline")
                        .setFlightCode("ABC123")
                        .setPeople(50)
                        .build()
        );

        CountersStateResponse response = responseBuilder.build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getCheckInHistory(CheckInHistoryRequest request, StreamObserver<CheckInHistoryResponse> responseObserver) {
        // Fetch data using request information
        System.out.println("Sector Name: " + request.getSectorName() + " " + "Airline Name: " + request.getAirlineName());

        CheckInHistoryResponse.Builder responseBuilder = CheckInHistoryResponse.newBuilder();
        responseBuilder.addCheckInsHistory(
                CheckInHistory.newBuilder()
                        .setSectorName(request.getSectorName())
                        .setCounter("C")
                        .setAirlineName(request.getAirlineName())
                        .setFlightCode("ABC123")
                        .setBookingCode("123456")
                        .build()
        );

        CheckInHistoryResponse response = responseBuilder.build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
