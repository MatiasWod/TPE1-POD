package ar.edu.itba.pod.client.query;

import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.Client;
import ar.edu.itba.pod.client.ServerUnavailableException;
import ar.edu.itba.pod.client.Util;

import java.io.IOException;

public class QueryClient extends Client {
    public static final String USAGE_MESSAGE = """
                                                Usage:
                                                    $> ./query-cli
                                                        -DserverAddress=xx.xx.xx.xx:yyyy
                                                        --Daction=actionName -DoutPath=query.txt [ -Dsector=sectorName | -Dairline=airlineName ]

                                                """;

    @Override
    public Action getActionClass() {
        return QueryActions.getAction(System.getProperty("action")).getActionClass();
    }

    public static void main(String[] args) throws IOException {
        String usageMessage = QueryClient.USAGE_MESSAGE;
        try(Client client = new QueryClient()){
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
