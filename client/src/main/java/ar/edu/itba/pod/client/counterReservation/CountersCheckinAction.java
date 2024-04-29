package ar.edu.itba.pod.client.counterReservation;

import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.ServerUnavailableException;
import ar.edu.itba.pod.client.Util;
import ar.edu.itba.pod.counterReservation.BookingCounterId;
import ar.edu.itba.pod.counterReservation.CheckInCountersRequest;
import ar.edu.itba.pod.counterReservation.CheckInCountersReservationResponse;
import ar.edu.itba.pod.counterReservation.counterReservationServiceGrpc;
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

            CheckInCountersReservationResponse response = stub.checkInCounters(checkInCountersRequestBuilder.build());

            for (BookingCounterId Counter : response.getBookingCounterIdList()) {
                if (Counter.getIsEmpty()) {
                    System.out.println("Counter " + Counter.getCounterId() + " is idle");
                } else {
                    System.out.println("Check-in successful of " + Counter.getBooking() + " for flight " + Counter.getFlight() + " at counter " + Counter.getCounterId());
                }
            }



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
                    -Daction=checkinCounters
                    -Dsector=XXXXX
                    -Dairline=XXXXX 
                    -DcounterFrom=XXXXX
                """;
    }
}
