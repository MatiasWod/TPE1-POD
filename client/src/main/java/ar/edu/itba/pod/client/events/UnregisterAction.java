package ar.edu.itba.pod.client.events;

import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.ServerUnavailableException;
import ar.edu.itba.pod.client.Util;

import ar.edu.itba.pod.events.EventsServiceGrpc;
import ar.edu.itba.pod.events.RegistrationRequest;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.List;

public class UnregisterAction extends Action {

    public UnregisterAction(List<String> argumentsForAction) {
        super(argumentsForAction);
    }

    @Override
    public void run(ManagedChannel channel){
        EventsServiceGrpc.EventsServiceBlockingStub stub = EventsServiceGrpc.newBlockingStub(channel);

        try{
            RegistrationRequest unregisterRequest = RegistrationRequest.newBuilder().setAirlineName(System.getProperty("airline")).build();
            stub.unregister(unregisterRequest);
            System.out.println(System.getProperty("airline") + " unregistered successfully for events\n");
        }
        catch (StatusRuntimeException exception){
            if (exception.getStatus() == Status.INVALID_ARGUMENT){
                throw new IllegalArgumentException();
            } else if (exception.getStatus().getCode() == Status.UNAVAILABLE.getCode()) {
                throw new ServerUnavailableException();
            }
            System.err.println(Util.GENERIC_ERROR_MESSAGE);
            System.exit(1);
        }
    }

    @Override
    public boolean hasValidArguments() {
        return super.hasValidArguments();
    }

    @Override
    public String getUsageMessage() {
        return """
                Usage:
                    $> ./eventsClient
                    -DserverAddress=XX.XX.XX.XX:YYYY 
                    -Daction=unregister 
                    -Dairline= $airlineName
                """;
    }

}
