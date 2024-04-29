package ar.edu.itba.pod.client.query;

import ar.edu.itba.pod.client.Action;

import java.util.Collections;
import java.util.List;

public enum QueryActions {
    COUNTERS_STATE_NO_SECTOR("queryCounters", new CountersStateAction(List.of("outPath"), CountersStateAction.Type.NO_SECTOR)),
    COUNTERS_STATE_SECTOR("queryCounters", new CountersStateAction(List.of("sector","outPath"), CountersStateAction.Type.SECTOR)),
    CHECK_IN_HISTORY_WITH_SECTOR("checkins", new CheckInHistoryAction(List.of("sector", "outPath"), CheckInHistoryAction.Type.WITH_SECTOR)),
    CHECK_IN_HISTORY_WITH_AIRLINE("checkins", new CheckInHistoryAction(List.of("airline","outPath"), CheckInHistoryAction.Type.WITH_AIRLINE)),
    CHECK_IN_HISTORY_WITHOUT_SECTOR_NOR_AIRLINE("checkins", new CheckInHistoryAction(List.of("outPath"), CheckInHistoryAction.Type.WITHOUT_SECTOR_NOR_AIRLINE)),
    CHECK_IN_HISTORY_WITH_SECTOR_AND_AIRLINE("checkins", new CheckInHistoryAction(List.of("sector", "airline","outPath"), CheckInHistoryAction.Type.WITH_SECTOR_AND_AIRLINE));


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

        if (actionName.equals("checkins")) {
            boolean hasSector = System.getProperty("sector") != null;
            boolean hasAirline = System.getProperty("airline") != null;

            if (hasSector && hasAirline) {
                return CHECK_IN_HISTORY_WITH_SECTOR_AND_AIRLINE;
            } else if (hasSector) {
                return CHECK_IN_HISTORY_WITH_SECTOR;
            } else if (hasAirline) {
                return CHECK_IN_HISTORY_WITH_AIRLINE;
            } else {
                return CHECK_IN_HISTORY_WITHOUT_SECTOR_NOR_AIRLINE;
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
