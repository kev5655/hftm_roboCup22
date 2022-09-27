package com.robotino.eventBus.intern;

import com.robotino.eventBus.Event;
import com.robotino.logistics.Coordinate;

public class RingStationEvent extends Event {

    private String name;
    private String type;
    private String state;
    private String team_color;
    private Coordinate coordinate;
    private String ring_color1;
    private String ring_color2;

    public RingStationEvent(String name, String type, String state, String team_color,
                            Coordinate coordinate, String ring_color1, String ring_color2) {

        this.name = name;
        this.type = type;
        this.state = state;
        this.team_color = team_color;
        this.coordinate = coordinate;
        this.ring_color1 = ring_color1;
        this.ring_color2 = ring_color2;
    }

    /**********Getters*********/
    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getState() {
        return state;
    }

    public String getTeam_color() {
        return team_color;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public String getRing_color1() {
        return ring_color1;
    }

    public String getRing_color2() {
        return ring_color2;
    }
}
