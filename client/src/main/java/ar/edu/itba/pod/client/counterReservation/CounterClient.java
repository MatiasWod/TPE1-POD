package ar.edu.itba.pod.client.counterReservation;

import ar.edu.itba.pod.client.Action;
import ar.edu.itba.pod.client.Client;


public class CounterClient extends Client {
    public static final String USAGE_MESSAGE = """
            Usage:
                $> ./counterClient.sh -DserverAddress=xx.xx.xx.xx:yyyy 
                -Daction=actionName [ -Dsector=sectorName | -DcounterFrom=fromVal | 
                -DcounterTo=toVal | -Dflights=flights | -Dairline=airlineName | 
                -DcounterCount=countVal ]
            """;

    @Override
    public Action getActionClass() { return null; }

}
