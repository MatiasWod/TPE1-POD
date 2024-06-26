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

public class ListCountersAction extends Action {

    public ListCountersAction(List<String> argumentsForAction) {
        super(argumentsForAction);
    }

    @Override
    public void run(ManagedChannel channel){
        counterReservationServiceGrpc.counterReservationServiceBlockingStub stub = counterReservationServiceGrpc.newBlockingStub(channel);

        try{
            ListCounterResponse response = stub.listCounters(ListCounterRequest.newBuilder()
                    .setSectorName(System.getProperty("sector"))
                    .setCounterStart(Integer.parseInt(System.getProperty("counterFrom")))
                    .setCounterEnd(Integer.parseInt(System.getProperty("counterTo")))
                    .build());
            System.out.println("Sectors\t\tCounters\t\tAirlines\t\t\tFlights\t\t\t\tPeople");
            System.out.println("############################################################################################");

            List<CountersInformation> countersInformation = response.getCountersInformationList();
            for(CountersInformation counter : countersInformation){
                System.out.printf("(%d-%d)\t\t\t%s\t\t\t",counter.getFirstCounter(), counter.getLastCounter(), counter.getAirline());
                if(!counter.getFlightsList().isEmpty()){
                    for(String flight : counter.getFlightsList()){
                        System.out.printf("%s|", flight);
                    }
                }else{
                    System.out.printf("-\t");
                }
                System.out.printf("\t\t\t\t\t\t\t\t%d\n",counter.getPeople());
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

}
