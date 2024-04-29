package ar.edu.itba.pod.client.checkin;

import ar.edu.itba.pod.checkIn.Booking;
import ar.edu.itba.pod.checkIn.CheckInCountersResponse;
import ar.edu.itba.pod.checkIn.CheckInServiceGrpc;
import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.ServerUnavailableException;
import ar.edu.itba.pod.client.Util;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.List;

public class CountersAction extends Action {
    public CountersAction(List<String> argumentsForAction) {
        super(argumentsForAction);
    }

    @Override
    public void run(ManagedChannel channel) {
        CheckInServiceGrpc.CheckInServiceBlockingStub stub = CheckInServiceGrpc.newBlockingStub(channel);

        try {
            CheckInCountersResponse response = stub.getCheckInCounters(
                    Booking.newBuilder()
                            .setCode(System.getProperty("booking"))
                            .build()
            );

            System.out.printf("Flight %s from %s is now checking in" +
                            " at counters (%d-%d) in Sector %s with %d people in line%n",
                    response.getFlightCode(), response.getAirline(),
                    response.getFromCounter(), response.getToCounter(), response.getSector(), response.getQueueSize());
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
    public String getUsageMessage() {
        return """
                Usage:
                    $> sh passengerClient.sh -DserverAddress=xx.xx.xx.xx:50051 
                    -Daction=fetchCounter -Dbooking=XYZ345
                """;
    }
}
