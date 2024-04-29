package ar.edu.itba.pod.servants;

import ar.edu.itba.pod.data.Airport;
import ar.edu.itba.pod.query.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.List;

public class QueryServant extends QueryServiceGrpc.QueryServiceImplBase {
    private final Airport airport = Airport.getInstance();

    @Override
    public void getCountersState(CountersStateRequest request, StreamObserver<CountersStateResponse> responseObserver) {
        try {
            CountersStateResponse.Builder response = CountersStateResponse.newBuilder();
            List<ar.edu.itba.pod.data.Utils.CounterState> counterStates = null;
            if (request.hasSector()){
                counterStates=airport.getCountersState(request.getSector().getSectorName());
            }else if (request.hasNoSector()){
                counterStates=airport.getAllCountersState();
            }else{
                throw new IllegalArgumentException();
            }

            assert counterStates != null;
            response.addAllCountersState(
                    counterStates.stream().
                            map(counterState -> CounterState.newBuilder()
                            .setSectorName(counterState.getSectorName())
                            .setCounterStart(counterState.getCounterStart())
                            .setCounterEnd(counterState.getCounterEnd())
                            .setAirlineName(counterState.getAirlineName())
                                    .addAllFlights(
                                            counterState.getFlights().stream().map(flight -> Flights.newBuilder()
                                                    .setFlightCode(flight.getFlightCode())
                                                    .build()
                                            ).toList()
                                    )
                            .setPeople(counterState.getPeople())
                            .build()
                    ).toList()).build();
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        }catch (IllegalArgumentException exception){
            responseObserver.onError(Status.INVALID_ARGUMENT.asRuntimeException());
        }
    }

    @Override
    public void getCheckInHistory(CheckInHistoryRequest request, StreamObserver<CheckInHistoryResponse> responseObserver) {

        try {
//            CheckInHistoryResponse.Builder response = CheckInHistoryResponse.newBuilder();
//            response.addAllCheckInsHistory(
//                    airport.getCheckInHistory(request.getSectorName(), request.getAirlineName()).stream().
//                            map(checkInHistoryInfo -> CheckInHistory.newBuilder()
//                            .setSectorName(checkInHistoryInfo.getSectorName())
//                            .setCounterCode(checkInHistoryInfo.getCounterId())
//                            .setAirlineName(checkInHistoryInfo.getAirlineName())
//                            .setFlightCode(checkInHistoryInfo.getFlightCode())
//                            .setBookingCode(checkInHistoryInfo.getBookingCode())
//                            .build()
//                    ).toList()
//            );
//            responseObserver.onNext(response.build());
//            responseObserver.onCompleted();
        }catch (IllegalArgumentException exception){
            responseObserver.onError(Status.INVALID_ARGUMENT.asRuntimeException());
        }
    }

}
