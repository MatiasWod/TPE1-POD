package ar.edu.itba.pod.client.counterReservation;

import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.ServerUnavailableException;
import ar.edu.itba.pod.client.Util;
import ar.edu.itba.pod.commons.Empty;
import ar.edu.itba.pod.counterReservation.*;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.List;


public class ListSectorsAction extends Action {

    public ListSectorsAction(List<String> argumentsForAction) {
        super(argumentsForAction);
    }

    @Override
    public void run(ManagedChannel channel){
        counterReservationServiceGrpc.counterReservationServiceBlockingStub stub = counterReservationServiceGrpc.newBlockingStub(channel);

        try{
            SectorsInformationResponse response = stub.listSectors(Empty.newBuilder().build());
            System.out.println("Sectors\t\tCounters");
            System.out.println("########################");
            List<Sector> sectorsList = response.getSectorsList();
            for(Sector sector : sectorsList){
                System.out.printf("%s\t\t\t",sector.getName());
                if(!sector.getCountersList().isEmpty()){
                    printCounters(sector.getCountersList());
                }else{
                    System.out.println("-");
                }

            }
        }
        catch(StatusRuntimeException exception){
            if (exception.getStatus().getCode() == Status.ALREADY_EXISTS.getCode() ||
                    exception.getStatus().getCode() == Status.NOT_FOUND.getCode() ||
                    exception.getStatus().getCode() == Status.FAILED_PRECONDITION.getCode()) {
                System.out.println(exception.getMessage());
                System.exit(1);
            }
            if (exception.getStatus() == Status.INVALID_ARGUMENT){
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
    public String getUsageMessage(){
        return """
                $>. ./counterClient
                    -DserverAddress=xx.xx.xx.xx:yyyy
                    -Daction=listSectors
                """;
    }

    //TODO Mover al Server
    private void printCounters(List<Counter> countersList){
        int lastCounterId = countersList.get(0).getCounterId();
        System.out.printf("(%d-",lastCounterId);
        for(Counter counter : countersList){
            if(counter.getCounterId() != lastCounterId+1 && counter.getCounterId() != lastCounterId){
                System.out.printf("%d)(%d-",lastCounterId,counter.getCounterId());
                lastCounterId = counter.getCounterId();
            }else{
                lastCounterId = counter.getCounterId();
            }
        }
        System.out.printf("%d)\n",lastCounterId);
    }
}
