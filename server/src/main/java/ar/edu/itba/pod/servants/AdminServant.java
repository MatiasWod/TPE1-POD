package ar.edu.itba.pod.servants;

import ar.edu.itba.pod.admin.AdminServiceGrpc;
import ar.edu.itba.pod.admin.CountersRequest;
import ar.edu.itba.pod.admin.PassengerRequest;
import ar.edu.itba.pod.admin.SectorRequest;
import ar.edu.itba.pod.commons.Empty;
import ar.edu.itba.pod.data.Airport;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

public class AdminServant extends AdminServiceGrpc.AdminServiceImplBase {
    private final Airport airport = Airport.getInstance();

    @Override
    public void addSector(SectorRequest request, StreamObserver<Empty> responseObserver) {
        try{
            airport.addSector(
                    request.getSectorName()
            );
            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        }
        catch (IllegalArgumentException exception){
            responseObserver.onError(Status.INVALID_ARGUMENT.asRuntimeException());
        }
    }

    @Override
    public void addCounters(CountersRequest request, StreamObserver<Empty> responseObserver) {
        try{
            airport.addCounters(
                    request.getSectorName(),
                    request.getCounters()
            );
            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        }
        catch (IllegalArgumentException exception){
            responseObserver.onError(Status.INVALID_ARGUMENT.asRuntimeException());
        }
    }

    @Override
    public void loadPassengerSet(PassengerRequest request, StreamObserver<Empty> responseObserver) {
        try{
            airport.loadPassengerSet(
                    request.getBookingCode(),
                    request.getFlightCode(),
                    request.getAirlineName()
            );
            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        }
        catch (IllegalArgumentException exception){
            responseObserver.onError(Status.INVALID_ARGUMENT.asRuntimeException());
        }
    }
}
