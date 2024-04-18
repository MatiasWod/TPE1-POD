package ar.edu.itba.pod.client.checkin;

import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.Client;
import ar.edu.itba.pod.client.ServerUnavailableException;
import ar.edu.itba.pod.client.Util;

import java.io.IOException;

public class PassengerClient extends Client {
    public static final String USAGE_MESSAGE = """
                                                Usage:
                                                    $> ./book-cli
                                                        -DserverAddress=xx.xx.xx.xx:yyyy
                                                        -Daction=actionName [ -Dbooking=booking | -Dsector=sectorName | -Dcounter=counterNumber ]
                                                """;

    @Override
    public Action getActionClass() {
        return PassengerActions.getAction(System.getProperty("action")).getActionClass();
    }

    public static void main(String[] args) throws IOException {
        String usageMessage = PassengerClient.USAGE_MESSAGE;
        try (Client client = new PassengerClient()) {
            usageMessage = client.getUsageMessage();
            client.run();
        } catch (IllegalArgumentException e) {
            System.out.println(e);
            System.err.println(Util.INVALID_ARGUMENT_MESSAGE);
            System.err.println(usageMessage);
            System.exit(2);
        } catch (ServerUnavailableException e) {
            System.err.println(Util.SERVER_UNAVAILABLE_MESSAGE);
            System.exit(2);
        }
    }
}
