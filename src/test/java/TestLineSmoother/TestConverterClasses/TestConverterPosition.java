package TestLineSmoother.TestConverterClasses;

import com.robotino.helperClass.ConvertPosition;
import com.robotino.helperClass.Data;
import com.robotino.logistics.Coordinate;
import com.robotino.logistics.Field;

public class TestConverterPosition {
    public static void main(String[] args) {
        /* Getestet wird ob das Coordinate- und Field-Objekt funktioniert auch werden die Funktionen
            - roboCoordinateToGamefieldCoordinate,
            - coordinateToZone
            - fieldNrtoCoordinate */
        test1();

        /* Getestet wird die Funktion
            - gamefieldCoordinateToRoboPosition */
        //test2();

        /*  Getestet weird die Funktion
        * - aStarGridIndexToGamefieldCoordinate */
        //test3();

    }

    public static void test1(){
        System.out.println("Getestet wird ob das Coordinate- und Field-Objekt funktioniert");
        System.out.println("auch werden die Funktionen \n-roboCoordinateToGamefieldCoordinate,\n-coordinateToZone,\nfieldNrtoCoordinate");
        ConvertPosition cp = new ConvertPosition();

        Coordinate roboPos = new Coordinate(1000,1000,0);
        Coordinate roboStartPos = new Coordinate(-4.5, 0.5, 0);

        Coordinate p = cp.roboCoordinateToGamefieldCoordinate(roboPos, roboStartPos);
        Field f = new Field(p);
        System.out.println("Von FeldNr M_Z51 auf M_Z62 Gamefield size min" + Data.getFIELD_COORDINATE_MIN() + " max: " + Data.getFIELD_COORDINATE_MAX());
        System.out.println("Startposition: " + new Field(roboStartPos));
        System.out.println("Endposition: " + f + "\n");


        roboPos = new Coordinate(-1000,1000,0);
        roboStartPos = new Coordinate(-4.5, 0.5, 0);

        p = cp.roboCoordinateToGamefieldCoordinate(roboPos, roboStartPos);
        f = new Field(p);
        System.out.println("Von FeldNr M_Z51 auf M_Z42" + Data.getFIELD_COORDINATE_MIN() + " max: " + Data.getFIELD_COORDINATE_MAX());
        System.out.println("Startposition: " + new Field(roboStartPos));
        System.out.println("Endposition: " + f + "\n");


        roboPos = new Coordinate(1000,1000,0);
        roboStartPos = new Coordinate(4.5, 0.5, 0);

        p = cp.roboCoordinateToGamefieldCoordinate(roboPos, roboStartPos);
        f = new Field(p);
        System.out.println("Von FeldNr C_Z51 auf C_Z42" + Data.getFIELD_COORDINATE_MIN() + " max: " + Data.getFIELD_COORDINATE_MAX());
        System.out.println("Startposition: " + new Field(roboStartPos));
        System.out.println("Endposition: " + f + "\n");


        roboPos = new Coordinate(-1000,1000,0);
        roboStartPos = new Coordinate(4.5, 0.5, 0);

        p = cp.roboCoordinateToGamefieldCoordinate(roboPos, roboStartPos);
        f = new Field(p);
        System.out.println("Von FeldNr C_Z51 auf C_Z62" + Data.getFIELD_COORDINATE_MIN() + " max: " + Data.getFIELD_COORDINATE_MAX());
        System.out.println("Startposition: " + new Field(roboStartPos));
        System.out.println("Endposition: " + f + "\n");

    }

    public static void test2(){
        System.out.println("Getestet wird die Funktion\n- gamefieldCoordinateToRoboPosition\n");
        System.out.println("Roboter soll von dem Feld M_Z51 auf das Feld C_Z13 fahren");
        System.out.println("dafür muss der roboter in x: -5000 nach rechts fahren und in y: 2000 nach vorne fahren");

        ConvertPosition cp = new ConvertPosition();

        Coordinate roboStartPos = new Coordinate(-4.5, 0.5, 0);
        Coordinate driveCoord = new Coordinate(0.5, 2.5, 0);
        Coordinate roboDriveCoord;

        roboDriveCoord = cp.gamefieldCoordinateToRoboCoordinate(driveCoord, roboStartPos);
        System.out.println(roboDriveCoord.toString());
        System.out.println(new Field((driveCoord)) + "\n\n");


        System.out.println("Roboter soll von dem Feld C_Z51 auf das Feld M_Z13 fahren " + Data.getFIELD_COORDINATE_MIN() + " max: " + Data.getFIELD_COORDINATE_MAX());
        System.out.println("dafür muss der roboter in x: 5000 nach rechts fahren und in y: 2000 nach vorne fahren");
        roboStartPos = new Coordinate(4.5, 0.5, 0);
        driveCoord = new Coordinate(-0.5, 2.5, 0);

        roboDriveCoord = cp.gamefieldCoordinateToRoboCoordinate(driveCoord, roboStartPos);
        System.out.println(roboDriveCoord.toString());
        System.out.println(new Field((driveCoord)));


    }

    public static void test3(){
        ConvertPosition convert = new ConvertPosition();

        // Grosse Feld x: -6000 y 7000
        // mittleres Feld x: -6000 y 7000
        // kleines Feld x -4000 y 4000
        int [] index = new int[]{20, 20};

        // Grosse Feld x: 1000 y 6000
        // mittleres Feld NULL
        // kleines Feld NULL
        int [] index2 = new int[]{160, 40};

        // Grosse Feld x: 0 y 0
        // mittleres Feld x: 0 y 0
        // kleines Feld NULL
        int [] index3 = new int[]{138, 159};

        System.out.println(convert.aStarGridIndexToGamefieldCoordinate(index, 0));

        System.out.println(convert.aStarGridIndexToGamefieldCoordinate(index2, 0));

        System.out.println(convert.aStarGridIndexToGamefieldCoordinate(index3, 0));


    }


}
