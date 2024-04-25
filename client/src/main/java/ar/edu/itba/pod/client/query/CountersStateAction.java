package ar.edu.itba.pod.client.query;

import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.ServerUnavailableException;
import ar.edu.itba.pod.query.CounterState;
import ar.edu.itba.pod.query.CountersStateRequest;
import ar.edu.itba.pod.query.CountersStateResponse;
import ar.edu.itba.pod.query.QueryServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.List;

public class CountersStateAction extends Action {

    public CountersStateAction(List<String> argumentsForAction) {
        super(argumentsForAction);
    }

    @Override
    public void run(ManagedChannel channel) {
        QueryServiceGrpc.QueryServiceBlockingStub stub = QueryServiceGrpc.newBlockingStub(channel);
        CountersStateRequest request = CountersStateRequest.newBuilder().setSectorName(System.getProperty("sector")).build();
        System.out.println("Counters state:");

        try {
            CountersStateResponse response = stub.getCountersState(request);
            //TODO write in query.txt
            for (CounterState counterState : response.getCountersStateList()) {
                System.out.println("Sector Name: " + counterState.getSectorName());
                System.out.println("Counter Start: " + counterState.getCounterStart());
                System.out.println("Counter End: " + counterState.getCounterEnd());
                System.out.println("Airline Name: " + counterState.getAirlineName());
                System.out.println("Flight Code: " + counterState.getFlightCode());
                System.out.println("People: " + counterState.getPeople());
                System.out.println();
            }
        } catch (StatusRuntimeException e) {
            if (e.getStatus() == Status.INVALID_ARGUMENT) {
                throw new IllegalArgumentException();
            } else if (e.getStatus().getCode() == Status.UNAVAILABLE.getCode()) {
                throw new ServerUnavailableException();
            }
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
                    $> ./queryClient
                        -DserverAddress=xx.xx.xx.xx:yyyy
                        -Daction=getCountersState
                        -DoutPath=query.txt
                        -DsectorName=sectorName
                """;
    }
}
