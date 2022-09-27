
//package com.robotino.drive;
//
//import com.robotino.eventBus.*;
//import com.robotino.eventBus.RoboIsOnPosEvent;
//import com.robotino.eventBus.intern.DriveToNextTargetBus;
//import com.robotino.eventBus.intern.DriveToNextTargetEvent;
//import com.robotino.logistics.Coordinate;
//import com.robotino.logistics.Field;
//import com.robotino.logistics.Gamefield;
//import com.robotino.robo.Robo;
//
//import java.util.*;
//
//
//public class DrivePath implements Subscriber{
//
//    private final static boolean DRIVE_DIAGONAL = false;
//    private final static boolean ENTRY_ZONE = true;
//
//    Robo robo;
//    Gamefield gamefield;
//    private List<Coordinate> route;
//    private Queue<Coordinate> routeQueue;
//
//    /**
//     *
//     * @param r
//     * @deprecated
//     */
//    public DrivePath(Robo r){
//        gamefield = Gamefield.getInstance();
//        this.robo = r;
//    }
//
//    public DrivePath(Robo r, List<Coordinate> coordinateList){
//        gamefield = Gamefield.getInstance();
//        this.robo = r;
//        prepareAndReady(coordinateList);
//    }
//
//    /**
//     *
//     * @param coordinateList
//     * @deprecated
//     */
//    public void prepareAndReady(List<Coordinate> coordinateList){
//        this.route = new LinkedList<Coordinate>(coordinateList);
//        //calcAllPoints();
//        calcAllPoints();
//        route.remove(0);
//        this.routeQueue = new LinkedList<>(route);
//        RoboIsOnPosBus.getInstance().subscribe(this);
//    }
//
//    public void calcAllPoints(){
//        int needAnotherPoint = 0;
//        int [] failPoint = new int [3];
//        for(int i = 0; i < route.size() - 1; i++){
//            Coordinate first = route.get(i).clone();
//            Coordinate second = route.get(i + 1).clone();
//            Map<Integer, Double> distances = Collections.synchronizedMap(new LinkedHashMap<Integer, Double>());
//            List<Coordinate> allPointsAroundFirst = new LinkedList<Coordinate>(givAllPointsAround(first));
//
//            for(int n = 0; n < allPointsAroundFirst.size(); n++){
//                Double distance = calcDistance(allPointsAroundFirst.get(n), second); //allPointsAroundSecond.get(m)
//                distances.put(n, distance);
//            }
//            distances = sortByValueJava8Stream(distances);
//            //if(Arrays.equals(failPoint, first)){
//            //    needAnotherPoint = 0;
//            //}
//            Set set = distances.keySet();
//            try{
//                Integer key = (Integer)(set.toArray()[needAnotherPoint]);
//                Object obj = distances.values().toArray()[needAnotherPoint];
//                double firstDistances = (Double)obj;
//
//                if(!(0 == firstDistances)){
//                    route.add(i + 1,allPointsAroundFirst.get(key));
//                    if(route.size() > 3){
//                        // Wen die Zweit letzte Koordinate der Viert letzten Koordinate entspricht wird die Zweit letzte
//                        // und die Dritt letzte Koordinate gelöscht so wird verhindert, dass der Roboter in eine Sackgasse fährt
//
//                        if(Coordinate.compare(route.get(route.size() - 2), route.get(route.size() - 4))){ //Arrays.equals(route.get(route.size() - 2), route.get(route.size() - 4))
//                            route.remove(route.size() - 2);
//                            route.remove(route.size() - 2);
//                            i -= 2; // Zeiht dem Counter 2 ab, sonst würde die For Schleife abbrechen
//                            needAnotherPoint++;
//                            //failPoint = first;
//                        }else{
//                            needAnotherPoint = 0; // nimmt nicht mehr den zweitbesten punkt, sondern den besten Punkt
//                        }
//                    }
//
//                }
//            }catch(Exception e) {
//                System.err.println("Path Drive kann kein Weg finden please Help");
//            }
//        }
//        route.forEach(point -> {
//            System.out.println("Berrechtnete Route x: " + point.getX() + " y: " + point.getY() + " a: " + point.getA());
//        });
//    }
//
//    private LinkedHashMap<Integer, Double> sortByValueJava8Stream( Map<Integer, Double> unSortedMapReference) {
//        Map<Integer, Double> unSortedMap = new HashMap<Integer, Double>(unSortedMapReference);
//
//        //System.out.println("Unsorted Map : " + unSortedMap);
//
//        LinkedHashMap<Integer, Double> sortedMap = new LinkedHashMap<>();
//        unSortedMap.entrySet().stream().sorted(Map.Entry.comparingByValue())
//            .forEachOrdered(x -> sortedMap.put(x.getKey(), x.getValue()));
//
//        //System.out.println("Sorted Map   : " + sortedMap);
//
//        return sortedMap;
//    }
//
//    private double calcDistance(Coordinate first, Coordinate second){
//        double xDiff = Math.abs(first.getX() - second.getX());
//        double yDiff = Math.abs(first.getY() - second.getY());
//        return Math.sqrt(Math.pow(xDiff, 2) + Math.pow(yDiff, 2));
//    }
//
//    private LinkedList<Coordinate> givAllPointsAround(Coordinate roboCoordinate){
//        LinkedList<Coordinate> allPointsAround = new LinkedList<>();
//        for(double x = roboCoordinate.getX() - 1; x <= roboCoordinate.getX() + 1; x += 1){
//            for(double y = roboCoordinate.getY() - 1; y <= roboCoordinate.getY() + 1; y += 1){
//                if(x == roboCoordinate.getX() && y == roboCoordinate.getY()) continue;
//
//                if( !DRIVE_DIAGONAL && isSkipedBecauseDriveDiagonal(x, y, roboCoordinate)){
//                    continue;
//                }
//
//                Coordinate pointAround = new Coordinate(x, y, 0);
//                if( !(isOutOfGameField(pointAround))){
//                    if(gamefield.isFieldFree(pointAround)){
//                        allPointsAround.add(pointAround);
//                    }
//                }
//            }
//        }
//        if(allPointsAround.size() <= 0){
//            System.err.println("Es git keine Positionen um den Roboter, die angefahren werden können");
//        }
//        return allPointsAround;
//    }
//
//    private boolean isOutOfGameField(Coordinate coordinate){
//        for(int i = 0; i < 2; i++){
//            int out = gamefield.getWidth();
//            if(coordinate.getPointAsArray(i) < 0 || coordinate.getPointAsArray(i) >= out){
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private boolean isSkipedBecauseInEntryZone(int x, int y, Coordinate coordinate){
//        if(coordinate.getPointAsArray(0) == 2 && coordinate.getPointAsArray(1) == 0){
//            if(x == 1 && y == 0){
//                return true;
//            }
//        }
//        if(coordinate.getPointAsArray(0) == 1000 && coordinate.getPointAsArray(1) == 0){
//            if(x == 2000 && y == 0){
//                return true;
//            }
//        }
//        return false;
//    }
//
//    /**
//     * Lässt keine Diagonale Koordinaten zu
//     * @param x Koordinate
//     * @param y Koordinate
//     * @param point Akutelle Roboter Position
//     * @return true überspring die Diagonalen Koordinaten false lässt die Diagonale Koordinaten zu
//     */
//    private boolean isSkipedBecauseDriveDiagonal(double x, double y, Coordinate point){
//        if(x == point.getX() - 1 && y == point.getY() - 1) return true;
//        if(x == point.getX() - 1 && y == point.getY() + 1) return true;
//        if(x == point.getX() + 1 && y == point.getY() + 1) return true;
//        if(x == point.getX() + 1 && y == point.getY() - 1) return true;
//
//        return false;
//    }
//
//
//    /* Bekommt ein Event wen der Roboter an der Position ist vom Mqtt Subscribe */
//    @Override
//    public void onReceive(Event event) {
//        RoboIsOnPosEvent roboEvent = (RoboIsOnPosEvent) event;
//        //if (roboEvent.isRoboOnPos()) {
//            if (routeQueue.size() > 0) {
//                Coordinate coordinate = routeQueue.poll();
//                System.out.println("Drive to: x: " + coordinate.getX() + " y: " + coordinate.getY() + " a: " + coordinate.getA());
//                Field field = new Field(coordinate);
//                robo.drive(coordinate);
//
//                //routeQueue.remove();
//            } else {
//                /* End Position wurde erreicht */
//                System.out.println();
//                RoboIsOnPosBus.getInstance().unsubscribe(this);
//                Event eventPosReached = new DriveToNextTargetEvent(robo.getRoboNr(), true);
//                DriveToNextTargetBus.getInstance().publish(eventPosReached);
//            }
//        //}
//
//    }
//}
