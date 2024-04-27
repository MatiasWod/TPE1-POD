package ar.edu.itba.pod.client.counterReservation;

import ar.edu.itba.pod.client.Action;

import java.util.Collections;

public enum CounterActions {
    LIST_SECTORS("listSectors", new ListSectorsAction(Collections.emptyList())),
    LIST_COUNTERS("listCounters", new ListCountersAction(Collections.emptyList()))/*,
    ASSIGN_COUNTERS("assignCounters", ),
    FREE_COUNTERS(),
    CHECKIN_COUNTERS(),
    LIST_PENDING_ASSIGNMENTS()*/;

    private final String actionName;
    private final Action action;

    CounterActions(final String actionName, final Action action) {
        this.actionName = actionName;
        this.action = action;
    }

    public static CounterActions getAction(String actionName) {
        for (CounterActions action : CounterActions.values()) {
            if(actionName.equals(action.actionName)){
                return action;
            }
        }
        throw new IllegalArgumentException();
    }

    public Action getActionClass() {
        return action;
    }

}
