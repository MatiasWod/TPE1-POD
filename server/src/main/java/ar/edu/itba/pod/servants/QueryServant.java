package ar.edu.itba.pod.servants;

import ar.edu.itba.pod.data.Airport;
import ar.edu.itba.pod.data.Utils.CheckInHistoryInfo;
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
            CheckInHistoryResponse.Builder response = CheckInHistoryResponse.newBuilder();

            List<CheckInHistoryInfo> checkInHistoryList = null;

            if (request.hasSectorAndAirline()){
                checkInHistoryList = airport.getCheckInHistoryWithSectorAndAirline(request.getSectorAndAirline().getSectorName(),request.getSectorAndAirline().getAirlineName());
            }else if (request.hasAirline()) {
                checkInHistoryList = airport.getCheckInHistoryWithAirline(request.getAirline().getAirlineName());
            }else if (request.hasSector()){
                checkInHistoryList = airport.getCheckInHistoryWithSector(request.getSector().getSectorName());
            } else if (request.hasNoSectorNorAirline()) {
                checkInHistoryList = airport.getCheckInHistory();
            }else {
                throw new IllegalArgumentException();
            }

            assert checkInHistoryList != null;
            response.addAllCheckInsHistory(
                    checkInHistoryList.stream().
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
