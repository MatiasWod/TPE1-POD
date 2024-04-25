package ar.edu.itba.pod.servants;

import ar.edu.itba.pod.data.Airport;
import ar.edu.itba.pod.query.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class QueryServant extends QueryServiceGrpc.QueryServiceImplBase {
    private final Airport airport = Airport.getInstance();

    @Override
    public void getCountersState(CountersStateRequest request, StreamObserver<CountersStateResponse> responseObserver) {
        // Fetch data using request information
        System.out.println("Sector Name: " + request.getSectorName());

        try {
            CountersStateResponse.Builder response = CountersStateResponse.newBuilder();
            response.addAllCountersState(
                    airport.getCountersState(request.getSectorName()).stream().
                            map(counterState -> CounterState.newBuilder()
                            .setSectorName(counterState.getSectorName())
                            .setCounterStart(counterState.getCounterStart())
                            .setCounterEnd(counterState.getCounterEnd())
                            .setAirlineName(counterState.getAirlineName())
                            .setFlightCode(counterState.getFlightCode())
                            .setPeople(counterState.getPeople())
                            .build()
                    ).toList()
            );
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        }catch (IllegalArgumentException exception){
            responseObserver.onError(Status.INVALID_ARGUMENT.asRuntimeException());
        }
    }

    @Override
    public void getCheckInHistory(CheckInHistoryRequest request, StreamObserver<CheckInHistoryResponse> responseObserver) {
        // Fetch data using request information
        System.out.println("Sector Name: " + request.getSectorName() + " " + "Airline Name: " + request.getAirlineName());

        try {
            CheckInHistoryResponse.Builder response = CheckInHistoryResponse.newBuilder();
            response.addAllCheckInsHistory(
                    airport.getCheckInHistory(request.getSectorName(), request.getAirlineName()).stream().
                            map(checkInHistoryInfo -> CheckInHistory.newBuilder()
                            .setSectorName(checkInHistoryInfo.getSectorName())
                            .setCounterCode(checkInHistoryInfo.getCounterId())
                            .setAirlineName(checkInHistoryInfo.getAirlineName())
                            .setFlightCode(checkInHistoryInfo.getFlightCode())
                            .setBookingCode(checkInHistoryInfo.getBookingCode())
                            .build()
                    ).toList()
            );
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        }catch (IllegalArgumentException exception){
            responseObserver.onError(Status.INVALID_ARGUMENT.asRuntimeException());
        }
    }

}
