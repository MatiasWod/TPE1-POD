package ar.edu.itba.pod.client.events;

import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.Client;
import ar.edu.itba.pod.client.ServerUnavailableException;
import ar.edu.itba.pod.client.Util;
import ar.edu.itba.pod.client.admin.AdminClient;

import java.io.IOException;

public class EventsClient extends Client {
    public static final String USAGE_MESSAGE = """
                                                Usage:
                                                $> sh eventsClient.sh 
                                                -DserverAddress=xx.xx.xx.xx:yyyy 
                                                -Daction=actionName 
                                                -Dairline=airlineName
                                                """;

    @Override
    public Action getActionClass(){
        return EventsActions.getAction(System.getProperty("action")).getActionClass();
    }

    public static void main(String[] args) throws IOException {
        String usageMessage = EventsClient.USAGE_MESSAGE;
        try(Client client = new EventsClient()){
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
