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

public class CheckInHistoryAction extends Action {
    public enum Type {
        WITH_SECTOR{
            @Override
            public CheckInHistoryRequest getCheckInHistoryRequest(String sectorName,String airlineName) {
                return CheckInHistoryRequest.newBuilder().setSector(CheckInHistoryRequestWithSector.newBuilder().setSectorName(sectorName).build()).build();
            }
        },
        WITH_AIRLINE{
            @Override
            public CheckInHistoryRequest getCheckInHistoryRequest(String sectorName,String airlineName){
                return CheckInHistoryRequest.newBuilder().setAirline(CheckInHistoryRequestWithAirline.newBuilder().setAirlineName(airlineName).build()).build();
            }
        },
        WITHOUT_SECTOR_NOR_AIRLINE{
            @Override
            public CheckInHistoryRequest getCheckInHistoryRequest(String sectorName,String airlineName){
                return CheckInHistoryRequest.newBuilder().setNoSectorNorAirline(CheckInHistoryRequestWithOutSectorNorAirline.newBuilder()).build();
            }
        },
        WITH_SECTOR_AND_AIRLINE{
            @Override
            public CheckInHistoryRequest getCheckInHistoryRequest(String sectorName,String airlineName){
                return CheckInHistoryRequest.newBuilder().setSectorAndAirline(CheckInHistoryRequestWithSectorAndAirline.newBuilder().setSectorName(sectorName).setAirlineName(airlineName).build()).build();
            }
        }

        ;
        abstract public CheckInHistoryRequest getCheckInHistoryRequest(String sectorName,String airlineName);
    }

    private final Type type;



    public CheckInHistoryAction(List<String> argumentsForAction,Type type) {
        super(argumentsForAction);
        this.type = type;
    }

    @Override
    public void run(ManagedChannel channel) {
        QueryServiceGrpc.QueryServiceBlockingStub stub = QueryServiceGrpc.newBlockingStub(channel);
        try {
        CheckInHistoryRequest request = type.getCheckInHistoryRequest(System.getProperty("sector"),System.getProperty("airline"));
        CheckInHistoryResponse response = stub.getCheckInHistory(request);

            Path path = Paths.get(System.getProperty("outPath"));
            Files.write(
                    path,
                    String.format("Sector\tCounters\tAirline\t\t\tFlight\t\t\tBooking\n").getBytes(),
                    StandardOpenOption.WRITE,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
            StringBuilder fileContent = new StringBuilder();
            fileContent.append("###############################################################\n");
            for (CheckInHistory checkInHistory : response.getCheckInsHistoryList()) {
                StringBuilder data = new StringBuilder();
                data.append(checkInHistory.getSectorName()).append("\t\t");
                data.append(checkInHistory.getCounterCode()).append("\t\t");
                data.append(checkInHistory.getAirlineName()).append("\t\t");
                data.append(checkInHistory.getFlightCode()).append("\t\t");
                data.append(checkInHistory.getBookingCode()).append("\t\t");
                data.append("\n");
                fileContent.append(data);
            }
            Files.write(
                    path,
                    fileContent.toString().getBytes(),
                    StandardOpenOption.WRITE,
                    StandardOpenOption.APPEND
            );
            System.out.println("Out path file created succesfully");
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
                        -Daction=getCheckInHistory
                        -DoutPath=query.txt
                        -DsectorName=sectorName
                        -DairlineName=airlineName
                """;
    }
}
