package ar.edu.itba.pod.client.checkin;

import ar.edu.itba.pod.checkIn.*;
import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.ServerUnavailableException;
import ar.edu.itba.pod.client.Util;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.List;

public class GetInLineAction extends Action {
    public GetInLineAction(List<String> argumentsForAction) {
        super(argumentsForAction);
    }

    @Override
    public void run(ManagedChannel channel) {
        CheckInServiceGrpc.CheckInServiceBlockingStub stub = CheckInServiceGrpc.newBlockingStub(channel);

        try {
            GetInlineResponse response = stub.getInLine(
                    GetInlineRequest.newBuilder()
                            .setBooking("XYZ345")
                            .setCounterNumber(3)
                            .setSectorName("3")
                            .build()
            );

            System.out.printf("Booking %s for flight %s from %s is now waiting to check-in" +
                            "on counters %s in Sector %s with %d people in line%n",
                    response.getBooking(), response.getFlightCode(),
                    response.getAirline(),response.getCounters(), response.getSector(), response.getQueueSize());
        } catch (StatusRuntimeException e) {
            if (e.getStatus() == Status.INVALID_ARGUMENT) {
                throw new IllegalArgumentException();
            } else if (e.getStatus().getCode() == Status.UNAVAILABLE.getCode()) {
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
                    -Daction=fetchCounter -Dbooking=XYZ345 -Dsector=C -Dcounter=3
                """;
    }
}
