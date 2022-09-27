package com.robotino.eventBus.refbox;

import com.robotino.eventBus.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * Daten Klasse für den NavigationsRouteBus
 */
public class NavigationRouteEvent extends Event {

    private final List<String> zoneList;

    public NavigationRouteEvent(List<String> zoneList) {
        super();
        this.zoneList = new ArrayList<>(zoneList);
    }

    public List<String> getZoneList() {
        return zoneList;
    }

    @Override
    public String toString() {
        return "Event{" +
                "zoneList=" + zoneList +
                '}';
    }
}