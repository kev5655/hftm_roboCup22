package com.robotino.drive;

import com.grips.model.teamserver.MachineClientUtils;
import com.robotino.helperClass.ConvertPosition;
import com.robotino.helperClass.Data;
import com.robotino.logistics.Coordinate;
import com.robotino.logistics.Station;

import java.util.LinkedList;
import java.util.List;


public class Obstacle {

    private final List<int []> notFree = new LinkedList<>();
    private int nameIdentifier;

    //public Obstacle(Coordinate c1, int height, int width){}
    public Obstacle(){}
    /**
     * Konstruktor dem eine Station Objekt übergeben werden kann. Diese Station wird alls hindernis hinzugefügt
     * @param station Station Objekt
     */
    public Obstacle(Station station){
        // Rundet die Koordinate auf, auf die Start Koordinate des Feldes
        int xStartField = (int) Math.floor(station.getCoordinate().getX()); // Rundet immer ab
        int yStartField = (int) Math.ceil(station.getCoordinate().getY()); // Rundet immer auf

        Coordinate startField = new Coordinate(xStartField, yStartField, 0);

        ConvertPosition convert = new ConvertPosition();
        int [] statIndex = convert.gamefieldCoordinateToAStarIndex(startField);

        blockStation(station, statIndex[0], statIndex[1]);
        if(Data.is_MIRROR_ON()){
            mirrorStation(station);
        }
    }

    /**
     * Die Entry Walls werden gesetzt
     */
    public Obstacle generateWall(){
        if(Data.isGamefieldSize5X5()){
            createEntryWallTop(0, 39); // Wall Entry Top
            createEntryWallSide(30, 40); // Wall Entry Right
            addGamefieldWall(50, 50);
        }else if(Data.isGamefieldSize7X8()){
            createEntryWallTop(0, 69); // Wall Entry Top
            createEntryWallSide(30, 70); // Wall Entry Right
            addGamefieldWall(70, 80);
        }else if(Data.isGamefieldSize14X8()){
            createEntryWallTop(0, 70); // Wall Entry Left Top
            createEntryWallSide(30, 70); // Wall Entry Left Right
            createEntryWallTop(120, 70); // Wall Entry Right Top
            createEntryWallSide(109, 70); // Wall Entry Right Right
            //addGamefieldWall(140, 80);
        }
        return this;
    }

    private void mirrorStation(Station station){
        int angle = station.getA();
        if(angle == 45 || angle == 225){
            station.setA(angle + 90);
        }else if(angle == 135 || angle == 315){
            station.setA(angle - 90);
        }
        double x = Math.floor(station.getCoordinate().getX() * -1); // Rundet immer ab
        double y = Math.ceil(station.getCoordinate().getY()); // Rundet immer auf
        Coordinate startField = new Coordinate(x, y);

        ConvertPosition converter = new ConvertPosition();
        int [] startIndex = converter.gamefieldCoordinateToAStarIndex(startField);

        blockStation(station, startIndex[0], startIndex[1]);

    }

    // ToDo Function parametrierbar machen je nach Grid grösse (50 mm, 100 mm),
    //  ich würde die Funktion neu schreiben da viel sie fiel zu lang ist.
    //  Das Zeil ist Stationen im Grid zu blockieren

    /**
     * Wird für die Stationen verwendet, detektiert welcher Winkel die Stationen haben und blockiert
     * dement sprechen diese Felder. Ist nicht parametrierbar funktioniert nur für ein 100 mm Gird
     *
     *
     * @param station Station die blockiert wird auf
     * @param xIndexStartField index vom Feld, auf dem die Station sich, befindet der, Index ist immer von Feld der Station oben links
     * @param yIndexStartField index vom Feld, auf dem die Station sich, befindet der, Index ist immer von Feld der Station oben links
     */
    private void blockStation(Station station, int xIndexStartField, int yIndexStartField){
        int xStartCorner, xEndCorner, yStartCorner, yEndCorner;
        int start = 0, end = 0, opStart = 0, opEnd = 0;
        int nOfGridInOneField = Data.getFIELD_SIZE() / Data.getGRID_SIZE();

        switch (station.getA()) {
            case 45, 225 -> {
                start = yIndexStartField + 3;
                end = yIndexStartField + 4;
                opStart = -1;
                opEnd = 1;
            }
            case 135, 315 -> {
                start = yIndexStartField + 6;
                end = yIndexStartField + 7;
                opStart = -1;
                opEnd = 1;
            }
        }

        for (int x = xIndexStartField; x < xIndexStartField + nOfGridInOneField; x++) {
            for (int y = yIndexStartField; y < yIndexStartField + nOfGridInOneField; y++) {

                switch(station.getA()){
                    case 90, 270:
                        xStartCorner = ((Data.getFIELD_SIZE_IN_MM() - station.getWIDTH()) / 2 ) / Data.getGRID_SIZE_IN_MM();
                        xEndCorner = nOfGridInOneField - xStartCorner - 1;
                        yStartCorner = ((Data.getFIELD_SIZE_IN_MM() - station.getHEIGHT()) / 2 ) / Data.getGRID_SIZE_IN_MM();
                        yEndCorner = nOfGridInOneField - yStartCorner - 1;
                        //yEndCorner += TOTAL_OFFSET / G.getGRID_SIZE_IN_MM();
                        if((x >= xIndexStartField + xStartCorner && x <= xIndexStartField + xEndCorner && x <= xIndexStartField + xStartCorner + 0.5) ||
                                (x >= xIndexStartField + xStartCorner && x <= xIndexStartField + xEndCorner && x >= xIndexStartField + xEndCorner -0.5 ) ){
                            if(y >= yIndexStartField + yStartCorner && y <= yIndexStartField + yEndCorner){
                                notFree.add(new int[]{x,y});
                            }

                        }
                        if((y >= yIndexStartField + yStartCorner && y <= yIndexStartField + yEndCorner && y <= yIndexStartField + yStartCorner + 0.5) ||
                                (y >= yIndexStartField + yStartCorner && y <= yIndexStartField + yEndCorner && y >= yIndexStartField + yEndCorner - 0.5) ){
                            if(x >= xIndexStartField + xStartCorner && x <= xIndexStartField + xEndCorner){
                                notFree.add(new int[]{x,y});
                            }
                        }
                        break;

                    case 0, 180:
                        xStartCorner = ((Data.getFIELD_SIZE_IN_MM() - station.getHEIGHT()) / 2 ) / Data.getGRID_SIZE_IN_MM();
                        xEndCorner =  nOfGridInOneField - xStartCorner - 1;
                        yStartCorner = ((Data.getFIELD_SIZE_IN_MM() - station.getWIDTH()) / 2 ) / Data.getGRID_SIZE_IN_MM();
                        yEndCorner =  nOfGridInOneField - yStartCorner - 1;
                        if((x >= xIndexStartField + xStartCorner && x <= xIndexStartField + xEndCorner && x <= xIndexStartField + xStartCorner + 0.5) ||
                                (x >= xIndexStartField + xStartCorner && x <= xIndexStartField + xEndCorner && x >= xIndexStartField + xEndCorner -0.5 ) ){
                            if(y >= yIndexStartField + yStartCorner && y <= yIndexStartField + yEndCorner){
                                notFree.add(new int[]{x,y});
                            }

                        }
                        if((y >= yIndexStartField + yStartCorner && y <= yIndexStartField + yEndCorner && y <= yIndexStartField + yStartCorner + 0.5) ||
                                (y >= yIndexStartField + yStartCorner && y <= yIndexStartField + yEndCorner && y >= yIndexStartField + yEndCorner - 0.5) ){
                            if(x >= xIndexStartField + xStartCorner && x <= xIndexStartField + xEndCorner){
                                notFree.add(new int[]{x,y});
                            }
                        }

                        break;

                    case 45, 225: // Achtung ist nicht dynamisch
                        // Grenzt die Felder grösse ein
                        if(x >= xIndexStartField + 1 && x <= xIndexStartField + 8){
                            if(y >= yIndexStartField + 1 && y <= yIndexStartField + 8){
                                if(y >= start && y <= end) {
                                    notFree.add(new int[]{x,y});
                                }
                                //if(x == 6 + xIndexStartField) opEnd = 0;
                                if(x == 6 + xIndexStartField) opEnd = -1;

                                //if(x == 6 + xIndexStartField) opStart = 0;
                                if(x == 3 + xIndexStartField) opStart = 1;
                            }
                        }
                        break;

                    case 135, 315: // Achtung ist nicht dynamisch

                        if(x >= xIndexStartField + 1 && x <= xIndexStartField + 8) {
                            if (y >= yIndexStartField + 1 && y <= yIndexStartField + 8) {
                                if(y >= start && y <= end){
                                    notFree.add(new int[]{x,y});
                                }

                                //if(x == 6 + xIndexStartField) opEnd = 0;
                                if(x == 3 + xIndexStartField) opEnd = -1;

                                //if(x == 12 + xIndexStartField) opStart = 0;
                                if(x == 6 + xIndexStartField) opStart = 1;
                            }
                        }
                        break;
                }
            }
            if(x >= xIndexStartField + 1 && x <= xIndexStartField + 8) {
                start += opStart;
                end += opEnd;
            }

        }
    }

    /**
     * Fügt die Wall ein die Oberhalb der Einfahrzone ist
     * @param xStart immer Links
     * @param yStart immer Oben
     */
    public void createEntryWallTop(int xStart, int yStart){
        for (int x = xStart; x < xStart + 20; x++) {
            for (int y = yStart; y < yStart + 1; y++) {
                notFree.add(new int[]{x, y});
            }
        }
    }

    /**
     * Fügt die Wall die Rechte oder Links von der Einfahrzone ist je nach dem von welchem Team ausgesehen
     * @param xStart Wichtig Grid Koordinate System oben links 0,0
     * @param yStart Wichtig Grid Koordinate System oben links 0,0
     */
    private void createEntryWallSide(int xStart, int yStart) {
        for (int x = xStart; x < xStart + 1; x++) {
            for (int y = yStart; y < yStart + 10; y++) {
                notFree.add(new int[]{x, y});
            }
        }
    }

    /**
     * Fügt ein Wall am Rand des Spielfeldes hinzu
     */
    private void addGamefieldWall(int xMax, int yMax) {
        for (int x = 0; x < xMax; x++) {
            notFree.add(new int[]{x,0});
            notFree.add(new int[]{x,yMax - 1});
        }

        for (int y = 0; y < yMax; y++) {
            notFree.add(new int[]{0,y});
            notFree.add(new int[]{xMax - 1,y});
        }
    }

    /**
     * Generiert die Standard Mps Stationen
     * @return Liste von Obstacles
     */
    public static List<Obstacle> generateDefaultStations(){
        List<String> zones = new LinkedList<>(List.of("M_Z72",
                "M_Z77", "M_Z54", "M_Z36", "M_Z21", "M_Z28", "M_Z15",
                "C_Z72", "C_Z77", "C_Z54", "C_Z36", "C_Z21", "C_Z28", "C_Z15"));
        List<Integer> angles = new LinkedList<>(List.of(45,
                270, 270, 225, 0, 0, 90,
                135, 90, 45, 315, 0, 180, 90));

        List<Obstacle> obstacles = new LinkedList<>();
        obstacles.add(new Obstacle().generateWall());

        for (int i = 0; i < zones.size(); i++) {
            Station s = new Station();
            s.setName("CS1").
                    setType("RS").
                    setState(MachineClientUtils.MachineState.IDLE).
                    setTeamColor("CYAN").
                    setStationField(zones.get(i)).
                    setA(angles.get(i));
            Obstacle o = new Obstacle(s);
            obstacles.add(o);

        }
        return obstacles;
    }

    public List<int[]> getNotFree() {
        return notFree;
    }

    public void setNameIdentifier(String name){
        switch (name) {
            case "M-CS1" -> nameIdentifier = 1;
            case "M-CS2" -> nameIdentifier = 2;
            case "M-RS1" -> nameIdentifier = 3;
            case "M-RS2" -> nameIdentifier = 4;
            case "M-BS" -> nameIdentifier = 5;
            case "M-DS" -> nameIdentifier = 6;
            case "M-SS" -> nameIdentifier = 7;
            case "Wall" -> nameIdentifier = 8;
        }
    }
    public int getNameIdentifier() {
        return nameIdentifier;
    }
}

/*Funktion wurde verwendet für Blocking Station für 50 mm Grid aktuell wird ein Grid von 100mm verwendet
switch((int) station.getA()){
            case 45, 225:
                start = yIndexStartField + 6;
                end = yIndexStartField + 7;
                opStart = -1;
                opEnd = 1;
                break;
            case 135, 315:
                start = yIndexStartField + 12;
                end = yIndexStartField + 13;
                opStart = -1;
                opEnd = 1;
                break;
        }

case 45, 225: // Achtung ist nicht dynamisch
                        // Grenzt die Felder grösse ein
                        if(x >= xIndexStartField + 3 && x <= xIndexStartField + 16){
                            if(y >= yIndexStartField + 3 && y <= yIndexStartField + 16){
                                if(y >= start && y <= end) {
                                    notFree.add(new int[]{x,y});
                                }
                                if(x == 12 + xIndexStartField) opEnd = 0;
                                if(x == 13 + xIndexStartField) opEnd = -1;

                                if(x == 6 + xIndexStartField) opStart = 0;
                                if(x == 7 + xIndexStartField) opStart = 1;
                            }
                        }
                        break;

                    case 135, 315: // Achtung ist nicht dynamisch

                        if(x >= xIndexStartField + 3 && x <= xIndexStartField + 16) {
                            if (y >= yIndexStartField + 3 && y <= yIndexStartField + 16) {
                                if(y >= start && y <= end){
                                    notFree.add(new int[]{x,y});
                                }

                                if(x == 6 + xIndexStartField) opEnd = 0;
                                if(x == 7 + xIndexStartField) opEnd = -1;

                                if(x == 12 + xIndexStartField) opStart = 0;
                                if(x == 13 + xIndexStartField) opStart = 1;
                            }
                        }
                        break;


                        if(Data.isGamefieldSize5X5()){
            createEntryWallTop(0, 79); // Wall Entry Top
            createEntryWallSide(59, 80); // Wall Entry Right
            addGamefieldWall(100, 100);
        }else if(Data.isGamefieldSize7X8()){
            createEntryWallTop(0, 139); // Wall Entry Top
            createEntryWallSide(59, 140); // Wall Entry Right
            addGamefieldWall(140, 160);
        }else if(Data.isGamefieldSize14X8()){
            createEntryWallTop(0, 139); // Wall Entry Left Top
            createEntryWallSide(59, 140); // Wall Entry Left Right
            createEntryWallTop(240, 139); // Wall Entry Right Top
            createEntryWallSide(219, 140); // Wall Entry Right Right
            addGamefieldWall(280, 160);
        }
 */

