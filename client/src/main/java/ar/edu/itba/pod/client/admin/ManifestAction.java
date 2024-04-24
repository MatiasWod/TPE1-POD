package ar.edu.itba.pod.client.admin;

import ar.edu.itba.pod.admin.AdminServiceGrpc;
import ar.edu.itba.pod.admin.PassengerRequest;
import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.Util;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class ManifestAction extends Action {
    private final AtomicInteger successfulCalls = new AtomicInteger();
    private final AtomicInteger failedCalls = new AtomicInteger();
    private static final int NUMBER_OF_FIELDS = 3;

    public ManifestAction(List<String> argumentsForAction) {
        super(argumentsForAction);
    }

    @Override
    public void run(ManagedChannel channel) {
        try(
                ExecutorService executorService = Executors.newCachedThreadPool();
                Stream<String> lines = Files.lines(Paths.get(System.getProperty("inPath"))).skip(1);
                ){
            lines.forEach(line ->{
                    try{
                        String[] fields = line.split(";");
                        if(fields.length != NUMBER_OF_FIELDS){
                            failedCalls.getAndIncrement();
                        }
                        else {
                            executorService.submit(new LoadPassengerSetRunnable(
                                    channel,
                                    successfulCalls,
                                    failedCalls,
                                    fields[0],
                                    fields[1],
                                    fields[2]
                            ));
                        }
            }
            catch (NumberFormatException exception){
                        failedCalls.getAndIncrement();
            }});
            executorService.shutdown();
            executorService.awaitTermination(Util.SYSTEM_TIMEOUT,Util.SYSTEM_TIMEOUT_UNIT);
        }
        catch (IOException | InterruptedException exception){
            System.err.println(Util.GENERIC_ERROR_MESSAGE);
            System.exit(2);
        }
        int failed = failedCalls.get();
        int successful = successfulCalls.get();
        if (failed > 0) {
            System.out.printf("Failed to book %d flights\n",failed);
        }
        if (successful > 0) {
            System.out.printf("Booked %d flights succesfully\n",successful);
        }
    }

    @Override
    public boolean hasValidArguments() {
        return super.hasValidArguments() && Files.exists(Paths.get(System.getProperty("inPath")));
    }

    @Override
    public String getUsageMessage() {
        return """
                Usage:
                    $> ./adminClient
                        -DserverAddress=xx.xx.xx.xx:yyyy
                        -Daction=manifest
                        -DinPath=filepath
                """;
    }

    private static class LoadPassengerSetRunnable implements Runnable{
        private final AdminServiceGrpc.AdminServiceBlockingStub stub;
        private final AtomicInteger successfulCalls;
        private final AtomicInteger failedCalls;
        private final String bookingCode;
        private final String flightCode;
        private final String airlineName;

        public LoadPassengerSetRunnable(ManagedChannel channel, AtomicInteger successfulCalls,
                                        AtomicInteger failedCalls, String bookingCode, String flightCode, String airlineName) {
            this.stub = AdminServiceGrpc.newBlockingStub(channel);
            this.successfulCalls = successfulCalls;
            this.failedCalls = failedCalls;
            this.bookingCode = bookingCode;
            this.flightCode = flightCode;
            this.airlineName = airlineName;
        }

        @Override
        public void run() {
            PassengerRequest passengerRequest = PassengerRequest.newBuilder()
                    .setBookingCode(bookingCode)
                    .setFlightCode(flightCode)
                    .setAirlineName(airlineName)
                    .build();

            try{
                stub.loadPassengerSet(passengerRequest);
            }
            catch (StatusRuntimeException exception){
                if (exception.getStatus() == Status.INVALID_ARGUMENT) {
                    System.out.printf("Booking %s for %s %s addition failed\n",bookingCode,airlineName,flightCode);
                    failedCalls.getAndIncrement();
                    return;
                }
                System.err.println(Util.GENERIC_ERROR_MESSAGE);
                System.exit(1);
            }
            System.out.printf("Booking %s for %s %s added succesfully\n",bookingCode,airlineName,flightCode);
            successfulCalls.getAndIncrement();
        }
    }
}
