package ar.edu.itba.pod.client.counterReservation;

import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.ServerUnavailableException;
import ar.edu.itba.pod.client.Util;
import ar.edu.itba.pod.counterReservation.AssignCountersRequest;
import ar.edu.itba.pod.counterReservation.FreeCountersRequest;
import ar.edu.itba.pod.counterReservation.counterReservationServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.List;

public class FreeCountersAction extends Action {
    public FreeCountersAction(List<String> argumentsForAction) {
        super(argumentsForAction);
    }

    @Override
    public void run(ManagedChannel channel) {
        counterReservationServiceGrpc.counterReservationServiceBlockingStub stub = counterReservationServiceGrpc.newBlockingStub(channel);

        try {
            FreeCountersRequest.Builder freeCountersRequestBuilder = FreeCountersRequest.newBuilder()
                    .setSectorName(System.getProperty("sector"))
                    .setAirlineName(System.getProperty("airline"))
                    .setCounterFrom(Integer.parseInt(System.getProperty("counterFrom")));


            stub.freeCounters(freeCountersRequestBuilder.build());
            System.out.println("FINISHED");
        } catch (StatusRuntimeException exception) {
            if (exception.getStatus().getCode() == Status.ALREADY_EXISTS.getCode() ||
                    exception.getStatus().getCode() == Status.NOT_FOUND.getCode() ||
                    exception.getStatus().getCode() == Status.FAILED_PRECONDITION.getCode()) {
                System.out.println(exception.getMessage());
                System.exit(1);
            }
            if (exception.getStatus() == Status.INVALID_ARGUMENT) {
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
                $>. ./assign
                    -DserverAddress=xx.xx.xx.xx:yyyy
                    -Daction=assignCounters 
                    -Dsector=XXXXX 
                    -Dflights=XXXXX|XXXXX|XXXXX 
                    -Dairline=XXXXX 
                    -DcounterCount=XXXXX
                """;
    }
}
