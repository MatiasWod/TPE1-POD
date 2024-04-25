package ar.edu.itba.pod.client.admin;

import ar.edu.itba.pod.admin.AdminServiceGrpc;
import ar.edu.itba.pod.admin.CountersRequest;
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
        AdminServiceGrpc.AdminServiceBlockingStub stub = AdminServiceGrpc.newBlockingStub(channel);

        try{
            CountersRequest countersRequest = CountersRequest
                    .newBuilder()
                    .setSectorName(System.getProperty("sector"))
                    .setCounters(Integer.parseInt(System.getProperty("counters")))
                    .build();
            stub.addCounters(countersRequest);
            //TODO completar bien el print
            System.out.printf("%d new counters (n-n) in Sector %s added successfully\n",Integer.parseInt(System.getProperty("counters")),System.getProperty("sector"));
        }
        catch (StatusRuntimeException exception){
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
        try{
            Integer.parseInt(System.getProperty("counters"));
            return super.hasValidArguments();
        }
        catch (NumberFormatException exception){
            return false;
        }
    }

    @Override
    public String getUsageMessage() {
        return """
                Usage:
                    $> ./adminClient
                        -DserverAddress=xx.xx.xx.xx:yyyy
                        -Daction=addCounters
                        -Dsector=sectorName
                        -Dcounters=counters
                """;
    }
}
