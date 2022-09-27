package com.robotino.eventBus.refbox;

import com.robotino.eventBus.Event;
import com.robotino.logistics.Station;

import java.util.List;

/**
 * Daten Klasse für den MachineInfoBus
 */
public class MachineInfoEvent extends Event {

    final List<Station> stations;

    public MachineInfoEvent(List<Station> stations) { //String name, String zone, String state, String type, int rotation
        super();
        this.stations = stations;
    }

    public List<Station> getStations() {
        return stations;
    }

    @Override
    public String toString() {
        return "MachineInfoEvent{" +
                "stations=" + stations +
                '}';
    }
}