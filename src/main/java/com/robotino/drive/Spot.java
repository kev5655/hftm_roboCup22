package com.robotino.drive;

import java.util.LinkedList;
import java.util.List;

/**
 * Hills Klasse für den AStar
 */
public class Spot {

    public static Spot[][] grid = null;
    public static final List<Spot> staticWallList = new LinkedList<>();
    public static List<Spot> dynamicWallList = new LinkedList<>(); // Wir noch nicht verwendet währe für den ander Roboter zu blockieren deshalb dynamic

    public static int ySize; //Columns
    public static int xSize; //rows
    public final int y;
    public final int x;
    public double f = 0; // g + h
    public double g = 0; // Distanz to next Field
    public double h = 0; // Distanz to End
    public boolean isCross = false;
    //public boolean disableWall = false;
    private boolean wall = false;

    public final List<Spot> neighbors = new LinkedList<>();
    public Spot previous = null;

    /**
     * Erzeugt einen Spot
     * @param x Grid Koordinate
     * @param y Grid Koordinate
     */
    public Spot(int x, int y){
        this.x = x;
        this.y = y;
    }

    /**
     * Holt sich alle Spot die als Wall deklariert sind und Speicher alle
     * Spot die eine Wall sind in eine Liste um besser auf Wall zu überprüfen
     */
    public static void createStaticWallList(){
        int sX;
        for (sX = 0; sX < Spot.grid.length - 1; sX++) {
            int sY;
            for (sY = 0; sY < Spot.grid[sX].length; sY++) {
                if(Spot.grid[sX][sY].isWall()){
                    Spot.staticWallList.add((Spot.grid[sX][sY]));
                }
            }
        }
    }

    /**
     * Überprüft, ob sich eine Liste von Spot in den aktuellen Spot befinden
     * @param listToCheck Liste auf die Überprüft wird
     * @return wen sich der Spot in der Liste befindet es true zurück
     */
    public static boolean isListInWall(List<Spot> listToCheck){
        for(Spot spot : listToCheck){
            for(Spot staticWall : staticWallList){
                if(Spot.isEqual(staticWall, spot)){
                    return true;
                }
            }
            //for(Spot dynamicWall : dynamicWallList){ // Kann verwendet werden um dynamische Objekte in das Grid hineinzuladen ß
            //    if(Spot.isEqual(dynamicWall, spot)){
            //        return true;
            //    }
            //}
        }
        return false;
    }

    public static List<Spot> getStaticWallList(){
        return staticWallList;
    }

    /**
     * Sind beide Spot gleich, wen ja dan true wen nein dan false
     * @param s1 erste Spot
     * @param s2 zweite Spot
     * @return gibt zurück, ob die Spots gleich sind
     */
    public static boolean isEqual(Spot s1, Spot s2) {
        return s1.x == s2.x && s1.y == s2.y;
    }

    /**
     * Nur für zum Testen
     * @param percent übergabe der Prozent an dene ein
     *                Hindernis generiert wird Example: {0.1} für 10 %
     * @deprecated
     */
    public void addRandomObstacles(double percent){
        if(Math.random() < percent){ //&& !disableWall
            this.wall = true;
        }
    }

    /**
     * Fügt dem Spot alle Nachbarn hinzu
     * @param grid Grid in dem sich er AStar bewegt → Wege berechnet
     * */
    public void addNeighbors(Spot [][] grid){
        int y = this.y;
        int x = this.x;
        if(x < xSize - 1){
            this.neighbors.add(grid[x + 1] [y]);
        }
        if(x > 0){
            this.neighbors.add(grid[x - 1] [y]);
        }
        if(y < ySize - 1){
            this.neighbors.add(grid[x] [y + 1]);
        }
        if(y > 0){
            this.neighbors.add(grid[x] [y - 1]);
        }
        if(x > 0 && y > 0){
            this.neighbors.add(grid[x - 1][y - 1]);
            isCross = true;
        }
        if(x < xSize -1 && y > 0){
            this.neighbors.add(grid[x + 1] [y - 1]);
            isCross = true;
        }
        if(x > 0 && y < ySize - 1){
            this.neighbors.add(grid[x - 1][y + 1]);
            isCross = true;
        }
        if(x < xSize - 1 && y < ySize - 1){
            this.neighbors.add(grid[x + 1][y + 1]);
            isCross = true;
        }
    }

    public boolean isWall() {
        return wall;
    }

    /**
     * Setzt den Spot als Wall fest
     */
    public void addWall() {
        this.wall = true;
    }

    public boolean contains(Spot s1){
        return this.x == s1.x && this.y == s1.y;
    }

    @Override
    public String toString() {
        return "Spot{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

}
