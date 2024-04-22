package ar.edu.itba.pod.client.admin;

import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.Client;
import ar.edu.itba.pod.client.ServerUnavailableException;
import ar.edu.itba.pod.client.Util;

import java.io.IOException;

public class AdminClient extends Client {
    public static final String USAGE_MESSAGE = """
                                                Usage:
                                                    $> ./adminClient
                                                        -DserverAddress=xx.xx.xx.xx:yyyy
                                                        -Daction=[ addSector | addCounters | manifest ]
                                                """;

    @Override
    public Action getActionClass() {
        return AdminActions.getAction(System.getProperty("action")).getActionClass();
    }

    public static void main(String[] args) throws IOException {
        String usageMessage = AdminClient.USAGE_MESSAGE;
        try(Client client = new AdminClient()){
            usageMessage = client.getUsageMessage();
            client.run();
        }
        catch (IllegalArgumentException exception){
            System.err.println(Util.INVALID_ARGUMENT_MESSAGE);
            System.err.println(usageMessage);
            System.exit(2);
        }
        catch (ServerUnavailableException exception){
            System.err.println(Util.SERVER_UNAVAILABLE_MESSAGE);
            System.exit(2);
        }
    }
}