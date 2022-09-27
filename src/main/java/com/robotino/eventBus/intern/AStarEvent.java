package com.robotino.eventBus.intern;

import com.robotino.drive.Spot;
import com.robotino.eventBus.Event;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Daten Klasse für den AStarBus
 */
public class AStarEvent extends Event {

    private Spot[][] grid = null;
    private List<Spot> path = null;
    private List<Spot> openSet = null;
    private List<Spot> closeSet = null;
    private List<Spot> ring = null;

    public void setGrid(Spot [][] grid){
        this.grid = grid.clone();
    }

    public void setOpenSet(List<Spot> openSet){
        this.openSet = new LinkedList<>(openSet);
    }

    public void setCloseSet(List<Spot> closeSet){
        this.closeSet = new LinkedList<>(closeSet);
    }

    public void setPath(List<Spot> path){
        this.path = new LinkedList<>(path);
    }

    public Spot[][] getGrid() {
        return grid;
    }

    public List<Spot> getPath() {
        return path;
    }

    public List<Spot> getOpenSet() {
        return openSet;
    }

    public List<Spot> getCloseSet() {
        return closeSet;
    }

    public List<Spot> getRing() {
        return ring;
    }

    public void setRing(List<Spot> ring) {
        this.ring = ring;
    }

    @Override
    public String toString() {
        return "AStarEvent{" +
                "grid=" + Arrays.toString(grid) +
                ", path=" + path +
                ", openSet=" + openSet +
                ", closeSet=" + closeSet +
                ", ring=" + ring +
                '}';
    }
}
