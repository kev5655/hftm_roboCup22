package com.robotino.drive;

import com.robotino.logistics.Coordinate;

import java.util.LinkedList;
import java.util.List;

/**
 * Wird nicht verwendet, dass die Klasse nicht das erwünschte Ziel liefert
 * und wen der AStar Algorithms nicht mehr verwandte wird muss man evtl.
 * den Path nicht mehr glätten
 */
public class LineSmoother {

    private static List<Coordinate> smoothedPath = new LinkedList<>();

    /**
     * Smooth den Path
     * @param aStarPath Path ist eine Liste von Koordinaten
     * @param weightData Gewichtung der abweichung
     * @param weightSmooth Gewichtung der Smoothness
     * @param tolerance Toleranz einsteillesung
     * @return Smooth Path
     */
    public static List<Coordinate> smooth(List<Coordinate> aStarPath, double weightData, double weightSmooth, double tolerance) {
        for(Coordinate cord : aStarPath){
            LineSmoother.smoothedPath.add( new Coordinate(cord.getX(), cord.getY()));
        }

        double [][] path = new double[smoothedPath.size()][2];
        for (int i = 0; i < path.length; i++) {
            path[i][0] = smoothedPath.get(i).getX();
            path[i][1] = smoothedPath.get(i).getY();
        }

        double [][] copy = new double[path.length][path[0].length];

        for (int i = 0; i < path.length; i++) {
            System.arraycopy(path[i], 0, copy[i], 0, path[i].length);
        }

        double dims = path[0].length;
        double change = tolerance;

        while(change >= tolerance){
            change = 0.0;
            for (int i = 1; i < copy.length -1; i++) {
                for (int j = 0; j < dims; j++) {

                    double xI = path[i][j];
                    double yI = copy[i][j];
                    double yPrev = copy[i - 1][j];
                    double yNext = copy[i + 1][j];

                    double yISaved = yI;

                    yI += weightData * (xI - yI) + weightSmooth * (yNext + yPrev - ( 2 * yI));
                    copy[i][j] = yI;

                    change += Math.abs(yI - yISaved);
                }
            }
        }
        smoothedPath = new LinkedList<>();
        for(double [] x : copy){
            smoothedPath.add(new Coordinate(x[0], x[1]));
        }
        return smoothedPath;


    }
}
