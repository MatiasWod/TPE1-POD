package ar.edu.itba.pod.client.query;

import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.ServerUnavailableException;
import ar.edu.itba.pod.client.Util;
import ar.edu.itba.pod.query.*;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

public class CountersStateAction extends Action {
    public enum Type {
        NO_SECTOR{
            @Override
            public CountersStateRequest getCounterStateRequest(String sectorName) {
                return CountersStateRequest.newBuilder().setNoSector(CountersStateRequestWithOutSector.newBuilder()).build();
            }
        },
        SECTOR{
            @Override
            public CountersStateRequest getCounterStateRequest(String sectorName) {
                return CountersStateRequest.newBuilder().setSector(CountersStateRequestWithSector.newBuilder().setSectorName(sectorName).build()).build();
            }
        };

        abstract public CountersStateRequest getCounterStateRequest(String sectorName);
        }

    private final Type type;

    public CountersStateAction(List<String> argumentsForAction,Type type) {
        super(argumentsForAction);
        this.type = type;
    }

    @Override
    public void run(ManagedChannel channel) {
        QueryServiceGrpc.QueryServiceBlockingStub stub = QueryServiceGrpc.newBlockingStub(channel);
        try {
            CountersStateRequest request = type.getCounterStateRequest(System.getProperty("sector"));
            CountersStateResponse response = stub.getCountersState(request);

            Path path = Paths.get(System.getProperty("outPath"));

            Files.write(
                    path,
                    String.format("Sector\tCounters\tAirlines\t\t\tFlights\t\t\tPeople\n").getBytes(),
                    StandardOpenOption.WRITE,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

            StringBuilder fileContent = new StringBuilder();
            fileContent.append("###############################################################\n");
            for (CounterState counterState : response.getCountersStateList()) {
                StringBuilder data = new StringBuilder();
                data.append(counterState.getSectorName()).append("\t\t");
                data.append("(").append(counterState.getCounterStart()).append("-").append(counterState.getCounterEnd()).append(")").append("\t\t");
                data.append(counterState.getAirlineName()).append("\t\t");
                if (counterState.getFlightsList().isEmpty()) {
                    data.append("\t\t\t\t");
                    data.append("-");
                    data.append("\t");
                } else {
                    for (Flights flight : counterState.getFlightsList()) {
                        if (counterState.getFlightsList().indexOf(flight) != counterState.getFlightsList().size() - 1)
                            data.append(flight.getFlightCode()).append("|");
                        else
                            data.append(flight.getFlightCode());
                    }
                }

                data.append("\t\t\t");
                data.append(counterState.getPeople()).append("\n");
                fileContent.append(data);
            }

            Files.write(
                    path,
                    fileContent.toString().getBytes(),
                    StandardOpenOption.WRITE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException | InvalidPathException e) {
            System.err.println(Util.IO_ERROR_MESSAGE);
            System.exit(1);
        }
        catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.ALREADY_EXISTS.getCode() ||
                    e.getStatus().getCode() == Status.NOT_FOUND.getCode() ||
                    e.getStatus().getCode() == Status.FAILED_PRECONDITION.getCode()) {
                System.out.println(e.getMessage());
                System.exit(1);
            }
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
