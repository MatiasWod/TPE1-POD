package ar.edu.itba.pod.client.admin;

import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.Client;

public class AdminClient extends Client {
    public static final String USAGE_MESSAGE = """
                                                Usage:
                                                    $> ./admin-cli
                                                        -DserverAddress=xx.xx.xx.xx:yyyy
                                                        -Daction=[ addSector | addCounters | manifest ]
                                                """;

    @Override
    public Action getActionClass() {
        return null;
    }
}
