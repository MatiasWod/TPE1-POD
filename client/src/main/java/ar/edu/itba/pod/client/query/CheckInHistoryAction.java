package ar.edu.itba.pod.client.query;

import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.ServerUnavailableException;
import ar.edu.itba.pod.query.CheckInHistoryRequest;
import ar.edu.itba.pod.query.QueryServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.List;

public class CheckInHistoryAction extends Action {
    public CheckInHistoryAction(List<String> argumentsForAction) {
        super(argumentsForAction);
    }

    @Override
    public void run(ManagedChannel channel) {
        QueryServiceGrpc.QueryServiceBlockingStub stub = QueryServiceGrpc.newBlockingStub(channel);
        CheckInHistoryRequest request = CheckInHistoryRequest.newBuilder()
                .setSectorName(System.getProperty("sectorName"))
                .setAirlineName(System.getProperty("airlineName"))
                .build();

        System.out.println("Check-in history:");

        //TODO write in query.txt
        try {
            stub.getCheckInHistory(request).getCheckInsHistoryList().forEach(checkInHistory -> {
                System.out.println("Sector Name: " + checkInHistory.getSectorName());
                System.out.println("Counter: " + checkInHistory.getCounter());
                System.out.println("Airline Name: " + checkInHistory.getAirlineName());
                System.out.println("Flight Code: " + checkInHistory.getFlightCode());
                System.out.println("Booking Code: " + checkInHistory.getBookingCode());
                System.out.println();
            });
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
                        -Daction=getCheckInHistory
                        -DoutPath=query.txt
                        -DsectorName=sectorName
                        -DairlineName=airlineName
                """;
    }
}
