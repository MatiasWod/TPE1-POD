package ar.edu.itba.pod.servants;

import ar.edu.itba.pod.commons.Empty;

import ar.edu.itba.pod.data.Airline;
import ar.edu.itba.pod.data.Airport;
import ar.edu.itba.pod.data.Exceptions.AirlineNotFoundException;
import ar.edu.itba.pod.events.EventStatus;
import ar.edu.itba.pod.events.EventsResponse;
import ar.edu.itba.pod.events.EventsServiceGrpc;
import ar.edu.itba.pod.events.RegistrationRequest;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.BlockingQueue;

public class EventsService extends EventsServiceGrpc.EventsServiceImplBase {

    private final Airport airport = Airport.getInstance();

    @Override
    public void register(RegistrationRequest request, StreamObserver<EventsResponse> responseObserver) {
        try{
            BlockingQueue<EventsResponse> eventsQueue = airport.RegisterAirlineForEvents(request.getAirlineName());
            airport.notifyAirline(request.getAirlineName(), EventsResponse.newBuilder().setStatus(EventStatus.REGISTER_SUCCESS).build());
            while(eventsQueue!=null){
                EventsResponse eventsResponse = eventsQueue.take();
                if(eventsResponse.getStatus()!= EventStatus.DESTROYED){
                    responseObserver.onNext(eventsResponse);
                }else{
                    responseObserver.onNext(eventsResponse);
                    responseObserver.onCompleted();
                    return;
                }
            }
            responseObserver.onCompleted();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void unregister(RegistrationRequest request, StreamObserver<Empty> responseObserver) {
        try{
            airport.UnregisterAirlineForEvents(request.getAirlineName());
            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException();
        }
        catch (AirlineNotFoundException e) {
            throw new IllegalArgumentException();
        }

    }



}
