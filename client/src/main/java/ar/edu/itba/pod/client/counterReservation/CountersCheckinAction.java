package ar.edu.itba.pod.client.counterReservation;

import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.ServerUnavailableException;
import ar.edu.itba.pod.client.Util;
import ar.edu.itba.pod.counterReservation.*;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.List;

public class CountersCheckinAction extends Action {
    public CountersCheckinAction(List<String> argumentsForAction) {
        super(argumentsForAction);
    }

    @Override
    public void run(ManagedChannel channel) {
        counterReservationServiceGrpc.counterReservationServiceBlockingStub stub = counterReservationServiceGrpc.newBlockingStub(channel);

        try {
            CheckInCountersRequest.Builder checkInCountersRequestBuilder = CheckInCountersRequest.newBuilder()
                    .setSectorName(System.getProperty("sector"))
                    .setAirlineName(System.getProperty("airline"))
                    .setFromVal(Integer.parseInt(System.getProperty("counterFrom")));

            CheckInCountersOk response =  stub.checkInCounters(checkInCountersRequestBuilder.build());

            System.out.printf("Check-in successful of %s for flight %s at counter %d",
                    response.getBooking(), response.getFlight(),
                    response.getCounterId());

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
                    -Daction=checkinCounters
                    -Dsector=XXXXX
                    -Dairline=XXXXX 
                    -DcounterFrom=XXXXX
                """;
    }
}
