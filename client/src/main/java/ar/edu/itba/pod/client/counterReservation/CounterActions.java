package ar.edu.itba.pod.client.counterReservation;

import ar.edu.itba.pod.client.Action;

import java.util.Collections;
import java.util.List;

public enum CounterActions {
    LIST_SECTORS("listSectors", new ListSectorsAction(Collections.emptyList())),
    LIST_COUNTERS("listCounters", new ListCountersAction(List.of("sector","counterFrom","counterTo"))),
    ASSIGN_COUNTERS("assignCounters", new AsignCountersAction(List.of("sector","flights","airline","counterCount"))),
    FREE_COUNTERS("freeCounters", new FreeCountersAction(List.of("sector", "counterFrom", "airline"))),
    /*
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
