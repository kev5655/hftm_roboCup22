package com.robotino.drive;

import com.robotino.helperClass.ConvertPosition;
import com.robotino.logistics.Coordinate;


import java.util.LinkedList;
import java.util.List;

/**
 * Wandelt den AStar Path in ein normalen Path mit dem richtigen Koordinaten
 * System das im Java allgemeine gebraucht wird
 */
public class Path{

    final ConvertPosition convert = new ConvertPosition();

    // Ist nicht Smooth
    private final List<Coordinate> aStarPath = new LinkedList<>();
    private final List<Coordinate> path = new LinkedList<>();
    private final List<Coordinate> smoothedPath;

    /**
     * Mit den aStarPath entgegen mit den AStar Koordinate system im Hintergrund und
     * berechnet dan die Gamefield- / Java Koordinaten, als Liste.
     * Auch wird der Path geglättet
     * @param aStarPath Path vom AStar mit Grid Koordinaten System
     */
    public Path(List<Spot> aStarPath, double a){
        for(Spot aStar : aStarPath){
            Coordinate c1 = new Coordinate(aStar.x, aStar.y, a);
            int [] index = new int[]{aStar.x, aStar.y};
            this.aStarPath.add(c1.clone());
            c1 = convert.aStarGridIndexToGamefieldCoordinate(index, a);
            this.path.add(c1);
        }


        this.smoothedPath = LineSmoother.smooth(this.path, 0.5, 0.1, 0.000001);
    }

    public List<Coordinate> getAStarPath() { return aStarPath; }

    public List<Coordinate> getPath(){ return path; }

    public List<Coordinate> getSmoothedPath(){ return smoothedPath; }


}
