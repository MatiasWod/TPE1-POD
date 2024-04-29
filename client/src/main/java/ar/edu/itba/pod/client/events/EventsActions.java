package ar.edu.itba.pod.client.events;


import ar.edu.itba.pod.client.Action;

import java.util.List;

public enum EventsActions {
    REGISTER("register",new RegisterAction(List.of("airline"))),
    UNREGISTER("unregister",new UnregisterAction(List.of("airline")));

    private final String actionName;
    private final Action action;

    EventsActions(final String actionName, final Action action) {
        this.actionName = actionName;
        this.action = action;
    }

    public static EventsActions getAction(String actionName){
        for(EventsActions action : EventsActions.values()){
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
