package com.robotino.drive;


import com.google.common.base.Stopwatch;
import com.robotino.eventBus.intern.AStarBus;
import com.robotino.eventBus.intern.AStarEvent;
import com.robotino.helperClass.ConvertPosition;
import com.robotino.helperClass.Data;
import com.robotino.helperClass.Log;
import com.robotino.logistics.Coordinate;

import java.util.LinkedList;
import java.util.List;

/**
 * Berechnet den Weg um Hindernisse zum Ziel ist jedoch nicht sehr schnell.
 * ToDo AStar Algorithms durch ein besseren Algorithms ersetzten
 */
public class AStar {
    public static final int xMaxIndex = Data.getMAX_X_INDEX(); // Gesamte Feldlänge in Y Achse in cm
    public static final int yMaxIndex = Data.getMAX_Y_INDEX(); // Gesamte Feldlänge in X Achse in cm
    private static final double costDiagonal = 1; // Hier kann die Gewichtung verändert werden
    private static final double costHorizonVerica = 1; // Hier kann die Gewichtung verändert werden
    private static final double factorHeuristic = 1; // Hier kann die Gewichtung verändert werden

    final List<Spot> listToCheck = new LinkedList<>();

    final Spot [][] grid = new Spot[xMaxIndex][yMaxIndex]; // Stellt das ganze Gird dar
    final List<Spot> openSet = new LinkedList<>();
    final List<Spot> closeSet = new LinkedList<>();
    List<Obstacle> obstacles = null;
    Spot start = null; // Start des Weges
    Spot goal = null; // Ende des Weges
    Coordinate cordGoal = null;

    /**
     * @deprecated Nur für das Testen
     */
    public AStar(){}

    /**
     * AStar aufruf ohne Hindernisse
     * @param start start Coordinate Objekt
     * @param goal ziel Coordinate Objekt
     */
    public AStar(Coordinate start, Coordinate goal){
        this.cordGoal = goal;
        init(start, goal);
    }

    /**
     * AStar aufruf mit allen Parametern
     * @param start start Coordinate Objekt
     * @param goal ziel Coordinate Objekt
     * @param obstacles Hindernis Liste mit den MPS Stationen und der Entry-wall
     */
    public AStar(Coordinate start, Coordinate goal, List<Obstacle> obstacles){
        this.obstacles = obstacles;
        this.cordGoal = goal;
        init(start, goal);
    }

    /**
     * Findet den schnellsten Path
     * @return Git ein Path Objekt zurück das direkt die umgewandelte Koordinate Liste beinhaltet
     */
    public Path findPath(){
        AStarEvent addWallEvent = new AStarEvent();
        addWallEvent.setGrid(grid);
        AStarBus.getInstance().publish(addWallEvent);
        Log.driveSystem.info("Start AStar from: " + start + "  to: " + goal + " start Time");
        Stopwatch stopwatch = Stopwatch.createStarted();

        while(openSet.size() > 0){
            //System.out.print("."); // Einkommentieren um zu sehen, was der Code am Machen ist
            int indexOfLowestElement = 0;
            for (int i = 0; i < openSet.size(); i++) {
                if(openSet.get(i).f < openSet.get(indexOfLowestElement).f){
                    indexOfLowestElement = i;
                }
            }

            Spot current = openSet.get(indexOfLowestElement);

            if(Spot.isEqual(current, goal)){ // Wenn das aktuelle Element das Ziel ist, ist der Algorithms fertig
                List<Spot> path = new LinkedList<>();
                Spot temp = current;
                path.add(temp);
                while (! (temp.previous == null)){
                    path.add(temp.previous);
                    temp = temp.previous;
                }
                AStarEvent pathEvent = new AStarEvent();
                pathEvent.setPath(path);
                AStarBus.getInstance().publish(pathEvent);
                stopwatch.stop();
                Log.driveSystem.info("A-Star done! Time: " + stopwatch);
                return new Path(path, cordGoal.getA());
            }// Noch nicht am Ziel/goal

            openSet.remove(current);
            closeSet.add(current);

            List<Spot> neighbors = new LinkedList<>(current.neighbors);

            for (Spot neighbor : neighbors) {
                getRingOfNeighbor(neighbor);
                AStarEvent aStarEvent = new AStarEvent();
                aStarEvent.setRing(listToCheck);
                AStarBus.getInstance().publish(aStarEvent);
                boolean isRingOfRoboInWall = Spot.isListInWall(listToCheck);
                if (!closeSet.contains(neighbor) && !isRingOfRoboInWall) { //! neighbor.isWall()
                    double g;
                    if (neighbor.isCross) {
                        g = current.g + costDiagonal; // Achtung bei diagonalen Nachbarn wird auch + 1 hinzugefügt was egtl. nicht richtig ist
                    } else {
                        g = current.g + costHorizonVerica; // Achtung bei diagonalen Nachbarn wird auch + 1 hinzugefügt was egtl. nicht richtig ist
                    }

                    if (openSet.contains(neighbor)) {
                        if (g < neighbor.g) {
                            neighbor.g = g;
                        }
                    } else {
                        neighbor.g = g;
                        openSet.add(neighbor);
                    }

                    neighbor.h = heuristic(neighbor, goal) * factorHeuristic; // Berechnet die Distanz zum Ziel
                    neighbor.f = neighbor.h + neighbor.g;
                    neighbor.previous = current;
                }

                if (neighbor.isCross) {
                    neighbor.g = current.g + costDiagonal; // Achtung bei diagonalen Nachbarn wird auch + 1 hinzugefügt was egtl. nicht richtig ist
                } else {
                    neighbor.g = current.g + costHorizonVerica; // Achtung bei diagonalen Nachbarn wird auch + 1 hinzugefügt was egtl. nicht richtig ist
                }
            }
            AStarEvent openSetEvent = new AStarEvent();
            openSetEvent.setOpenSet(openSet);
            AStarBus.getInstance().publish(openSetEvent);

            AStarEvent closeSetEvent = new AStarEvent();
            closeSetEvent.setCloseSet(closeSet);
            AStarBus.getInstance().publish(closeSetEvent);

        }
        Log.driveSystem.error("AStar has no Solution");
        System.out.println("Static Wall list: " + Spot.getStaticWallList());
        throw new RuntimeException("AStar kann kein weg finden von: " + start + " zu: " + goal + " " +
                "Tipp um zu debug: testen mit den localGui Projekt. Jedoch muss dies noch vorher richtig parametriert werden" +
                "wo sich die Stationen befinden und Start und Ziel position");
    }

    /**
     * Berechnet die diagonale Distanz
     * @param s1 Erste Koordinate
     * @param s2 Zweite Koordinate
     * @return gibt die Distanz als double zurück von Koordinate s1 zu Koordinate s1
     */
    private double heuristic(Spot s1, Spot s2){
        double xDiff = Math.abs(s1.x - s2.x);
        double yDiff = Math.abs(s1.y - s2.y);
        return Math.sqrt( Math.pow(xDiff, 2) + Math.pow(yDiff, 2) );
    }

    /**
     * Generiert einen Ring um einen Spot der verwendet wird, um auf Hindernisse in der Distanz abzufragen
     *
     * @param neighbor Koordinate/Spot auf den ein Ring gebaut werden soll
     * @return Git die Ringliste zurück um den übergebene Spot
     */
    public List<Spot> getRingOfNeighbor(Spot neighbor){
        double radius = Math.ceil(((double)Data.getROBO_SIZE() / 2));
        radius = radius + Data.getOFFSET();
        radius = radius / Data.getGRID_SIZE();

        int xStart = (int)Math.floor(neighbor.x - radius); // Rundet ab
        int xEnd = (int)Math.ceil(neighbor.x + radius); // Rundet auf

        int yStart = (int)Math.floor(neighbor.y - radius); // Rundet ab
        int yEnd = (int)Math.ceil(neighbor.y + radius); // Rundet auf

        listToCheck.clear();

        for (int x = xStart; x <= xEnd; x++) {
            for (int y = yStart; y  <= yEnd ; y++) {
                Spot checkSpot = new Spot(x, y);
                double distance = heuristic(neighbor, checkSpot);
                if (radius >= distance && distance > radius - (1)){
                    listToCheck.add(checkSpot);
                }
            }
        }
        //Log.logger.info("List of Neighbor size: " + listToCheck.size());
        return listToCheck;
    }

    /**
     * Initialisiert das AStar Objekt
     * - Erstellt das Grid mit den Spots
     * - Setzt die Hindernisse von der Obstacle Liste hinzu in das Gird
     * - fügt die Nachbarn von jedem Spot zu dem Spot in Gird hinzu
     * @param start start Coordinate Objekt
     * @param goal ziel Coordinate Objekt
     */
    public void init(Coordinate start, Coordinate goal) {
        Spot.xSize = xMaxIndex; // So weis das Spot-Objekt wie gross das Grid ist verwendet um keine Neighbors außerhalb des Feldes zu setzten
        Spot.ySize = yMaxIndex; // So weis das Spot-Objekt wie gross das Grid ist verwendet um keine Neighbors außerhalb des Feldes zu setzten

        ConvertPosition convert = new ConvertPosition();

        int [] startIndexe = convert.gamefieldCoordinateToAStarIndex(start);
        int [] goalIndexe = convert.gamefieldCoordinateToAStarIndex(goal);

        // Create 2D Grid
        for(int x = 0; x < xMaxIndex; x++){
            for (int y = 0; y < yMaxIndex; y++) {
                grid[x][y] = new Spot(x, y);
            }
        }

        this.start = grid[startIndexe[0]][startIndexe[1]];
        this.goal = grid[goalIndexe[0]][goalIndexe[1]];

        for(int x = 0; x < xMaxIndex; x++){
            for (int y = 0; y < yMaxIndex; y++) {
                grid[x][y].addNeighbors(grid);
                if(this.start.x == x && this.start.y == y){ // So kann am Start kein Wall/Obstacles sein
                    continue;
                }else if(this.goal.x == x && this.goal.y == y) { // So kann am Ende kein Wall/Obstacles sein
                    continue;
                }else{
                    //grid[x][y].addRandomObstacles(0.1); // füge eine Wall/Obstacles dem Spot hinzu mit einer Wahrscheinlichkeit von 10%
                }
            }
        }

        for(Obstacle obstacle : obstacles){
            for(int [] coordinate : obstacle.getNotFree()){
                grid[coordinate[0]][coordinate[1]].addWall();
            }
        }

        Spot.grid = grid;
        Spot.createStaticWallList();
        openSet.add(this.start); // Fügt den Startplatz hinzu

    }
}
