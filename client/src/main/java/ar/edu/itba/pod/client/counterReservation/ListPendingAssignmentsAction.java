package ar.edu.itba.pod.client.counterReservation;

import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.ServerUnavailableException;
import ar.edu.itba.pod.client.Util;
import ar.edu.itba.pod.counterReservation.ListPendingAssignmentsRequest;
import ar.edu.itba.pod.counterReservation.ListPendingAssignmentsResponse;
import ar.edu.itba.pod.counterReservation.PendingAssignmentsInformation;
import ar.edu.itba.pod.counterReservation.counterReservationServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.List;

public class ListPendingAssignmentsAction extends Action {
    public ListPendingAssignmentsAction(List<String> argumentsForAction) {
        super(argumentsForAction);
    }

    @Override
    public void run(ManagedChannel channel) {
        counterReservationServiceGrpc.counterReservationServiceBlockingStub stub = counterReservationServiceGrpc.newBlockingStub(channel);
        try {
            ListPendingAssignmentsResponse response = stub.listPendingAssignments(ListPendingAssignmentsRequest.newBuilder()
                            .setSectorName("sector")
                            .build());
            System.out.printf("Counters\tAirline\t\tFlights\n");
            System.out.printf("###############################################");
            List<PendingAssignmentsInformation> pendingAssignmentsInformationList = response.getPendingAssignmentsInformationList();
            int i = 0;
            for(PendingAssignmentsInformation p : pendingAssignmentsInformationList){
                System.out.printf("%d\t\t%s\t",p.getCounter(),p.getAirlineName());
                while(i < p.getFlightCodeCount()){
                    System.out.printf("%s|", p.getFlightCode(i));
                    i++;
                }
                System.out.printf("\n");
                i = 0;
            }
        }
        catch(StatusRuntimeException exception){
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
                    -Daction=listPendingAssignments
                    -Dsector=sectorName
                """;
    }
}
