package ar.edu.itba.pod.client.query;

import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.Client;

public class QueryClient extends Client {
    public static final String USAGE_MESSAGE = """
                                                Usage:
                                                    $> ./query-cli
                                                        -DserverAddress=xx.xx.xx.xx:yyyy
                                                        --Daction=actionName -DoutPath=query.txt [ -Dsector=sectorName | -Dairline=airlineName ]
                                                                                                                         
                                                """;

    @Override
    public Action getActionClass() {
        return null;
    }
}
