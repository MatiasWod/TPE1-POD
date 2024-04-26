package ar.edu.itba.pod.client.checkin;

import ar.edu.itba.pod.client.Action;

import java.util.Collections;
import java.util.List;

public enum PassengerActions {
    COUNTERS("fetchCounter", new CountersAction(List.of("booking"))),
    GET_IN_LINE("passengerCheckin", new GetInLineAction(List.of("booking", "sector", "counter"))),
    PASSENGER_STATUS("passengerStatus", new GetPassengerStatusAction(List.of("booking")));
    private final String actionName;

    private final Action action;

    PassengerActions(String actionName, Action action) {
        this.actionName = actionName;
        this.action = action;
    }

    public static PassengerActions getAction(String actionName) {
        for (PassengerActions action : values()) {
            if (actionName.equals(action.actionName)) {
                return action;
            }
        }
        throw new IllegalArgumentException();
    }

    public Action getActionClass() {
        return action;
    }
}
