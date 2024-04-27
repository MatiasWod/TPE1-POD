package ar.edu.itba.pod.client.counterReservation;

import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.ServerUnavailableException;
import ar.edu.itba.pod.client.Util;
import ar.edu.itba.pod.commons.Empty;
import ar.edu.itba.pod.counterReservation.AssignCountersRequest;
import ar.edu.itba.pod.counterReservation.SectorsInformationResponse;
import ar.edu.itba.pod.counterReservation.counterReservationServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.List;

public class AsignCountersAction extends Action {
    public AsignCountersAction(List<String> argumentsForAction) {
        super(argumentsForAction);
    }

    @Override
    public void run(ManagedChannel channel) {
        counterReservationServiceGrpc.counterReservationServiceBlockingStub stub = counterReservationServiceGrpc.newBlockingStub(channel);

        try {
            AssignCountersRequest.Builder assignCountersRequestBuilder = AssignCountersRequest.newBuilder()
                    .setSector(System.getProperty("sector"))
                    .setAirline(System.getProperty("airline"))
                    .setCounterCount(Integer.parseInt(System.getProperty("counterCount")));

            String flightsArg = System.getProperty("flights");
            String[] flightsArray = flightsArg.split("\\|");
            for (String flight : flightsArray) {
                assignCountersRequestBuilder.addFlights(flight);
            }

            stub.assignCounters(assignCountersRequestBuilder.build());
            System.out.println("FINISHED");
        } catch (StatusRuntimeException exception) {
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
