package ar.edu.itba.pod.client.admin;

import ar.edu.itba.pod.client.Action;

import java.util.List;

public enum AdminActions {
    ADD_SECTOR("addSector", new SectorAction(List.of("sector"))),
    ADD_COUNTERS("addCounters", new CountersAction(List.of("sector","counters"))),
    MANIFEST("manifest",new ManifestAction(List.of("inPath")));

    private final String actionName;
    private final Action action;

    AdminActions(String actionName, Action action) {
        this.actionName = actionName;
        this.action = action;
    }

    public static AdminActions getAction(String actionName){
        for(AdminActions action : values()){
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
