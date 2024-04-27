package ar.edu.itba.pod.client.counterReservation;

import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.Client;
import ar.edu.itba.pod.client.ServerUnavailableException;
import ar.edu.itba.pod.client.Util;
import ar.edu.itba.pod.client.admin.AdminClient;

import java.io.IOException;
import java.rmi.ServerException;


public class CounterClient extends Client {
    public static final String USAGE_MESSAGE = """
            Usage:
                $> ./counterClient.sh -DserverAddress=xx.xx.xx.xx:yyyy 
                -Daction=actionName [ -Dsector=sectorName | -DcounterFrom=fromVal | 
                -DcounterTo=toVal | -Dflights=flights | -Dairline=airlineName | 
                -DcounterCount=countVal ]
            """;

    @Override
    public Action getActionClass() {
        return CounterActions.getAction(System.getProperty("action")).getActionClass();
    }

    public static void main(String[] args) throws IOException {
        String usageMessage = CounterClient.USAGE_MESSAGE;
        try(Client client = new CounterClient()) {
            usageMessage = client.getUsageMessage();
            client.run();
        }
        catch (IllegalArgumentException exception) {
            System.out.println(Util.INVALID_ARGUMENT_MESSAGE);
            System.out.println(usageMessage);
            System.exit(2);
        }
        catch (ServerUnavailableException exception) {
            System.err.println(Util.SERVER_UNAVAILABLE_MESSAGE);
            System.exit(2);
        }
    }

}
