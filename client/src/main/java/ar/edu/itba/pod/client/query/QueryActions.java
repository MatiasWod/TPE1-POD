package ar.edu.itba.pod.client.query;

import ar.edu.itba.pod.client.Action;

import java.util.Collections;
import java.util.List;

public enum QueryActions {
    COUNTERS_STATE_NO_SECTOR("queryCounters", new CountersStateAction(List.of("outPath"), CountersStateAction.Type.NO_SECTOR)),
    COUNTERS_STATE_SECTOR("queryCounters", new CountersStateAction(List.of("sector","outPath"), CountersStateAction.Type.SECTOR)), CHECK_IN_HISTORY("checkins", new CheckInHistoryAction(List.of("sector", "airline")));


    private final String actionName;
    private final Action action;

    QueryActions(String actionName, Action action) {
        this.actionName = actionName;
        this.action = action;
    }

    public static QueryActions getAction(String actionName){
        if (actionName.equals("queryCounters")) {
            boolean hasSector = System.getProperty("sector") != null;

            if (hasSector) {
                return COUNTERS_STATE_SECTOR;
            } else {
                return COUNTERS_STATE_NO_SECTOR;
            }
        }

        for(QueryActions action : values()){
            if (actionName.equals(action.actionName)){
                return action;
            }
        }
        throw new IllegalArgumentException();
    }

    public Action getActionClass(){
        return action;
    }
}
