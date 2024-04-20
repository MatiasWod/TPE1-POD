package ar.edu.itba.pod.client.admin;

import ar.edu.itba.pod.admin.AdminServiceGrpc;
import ar.edu.itba.pod.admin.SectorRequest;
import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.ServerUnavailableException;
import ar.edu.itba.pod.client.Util;
import io.grpc.ManagedChannel;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;

import java.util.List;

public class SectorActions extends Action {

    public SectorActions(List<String> argumentsForAction) {
        super(argumentsForAction);
    }

    @Override
    public void run(ManagedChannel channel) {
        AdminServiceGrpc.AdminServiceBlockingStub stub = AdminServiceGrpc.newBlockingStub(channel);

        try {
            SectorRequest sectorRequest = SectorRequest
                    .newBuilder()
                    .setSectorName(System.getProperty("sector"))
                    .build();
            stub.addSector(sectorRequest);
        }
        catch (StatusRuntimeException exception){
            if (exception.getStatus() == Status.INVALID_ARGUMENT){
                throw new IllegalArgumentException();
            } else if (exception.getStatus().getCode() == Status.UNAVAILABLE.getCode()) {
                throw new ServerUnavailableException();
            }
        }
        System.err.println(Util.GENERIC_ERROR_MESSAGE);
        System.exit(1);
    }

    @Override
    public boolean hasValidArguments() {
        return super.hasValidArguments();
    }

    @Override
    public String getUsageMessage() {
        return """
                Usage:
                    $> ./adminClient
                        -DserverAddress=xx.xx.xx.xx:yyyy
                        -Daction=addSector
                        -Dsector=sectorName
                """;
    }
}
