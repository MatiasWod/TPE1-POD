package ar.edu.itba.pod.client.query;

import ar.edu.itba.pod.client.Action;

import java.util.List;

public enum QueryActions {
    COUNTERS_STATE("queryCounters", new CountersStateAction(List.of("sector"))),
    CHECK_IN_HISTORY("checkins", new CheckInHistoryAction(List.of("sector","airline")));


    private final String actionName;
    private final Action action;

    QueryActions(String actionName, Action action) {
        this.actionName = actionName;
        this.action = action;
    }

    public static QueryActions getAction(String actionName){
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
