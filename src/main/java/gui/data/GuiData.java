package gui.data;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Fabian Leuenberger
 * @description Holt die Gameplay Daten vom XML File und macht diese Statisch verfügbar,
 *              damit nicht in jeder Klasse das XML File eingeladen werden muss.
 */


public class GuiData {

    private static final String xmlPath = "./rsc/config/gamePlayRefboxTaric.xml";


    /*-- FIELD SIZE --*/
    private static int FIELD_WIDTH; // in mm 5000 7000 14000
    private static int FIELD_HEIGHT; // in mm 5000 8000

    /*-- AStar --*/
    private static final int FIELD_SIZE = 1000; // in mm
    private static final int GRID_SIZE = 50; // in mm default für 5mm spots = 50, für 10 mm spots = 100
    private static final int FACTOR_MM_TO_PIXEL = 10;

    private static final int MARGIN = 400; // in mm
    private static final int INDEX_TO_PIXEL = 5; //former 5 (Spot grösse, 5 oder 10)

    public static int getFIELD_WIDTH() { return FIELD_WIDTH / FACTOR_MM_TO_PIXEL; }
    public static int getFIELD_HEIGHT() { return FIELD_HEIGHT / FACTOR_MM_TO_PIXEL; }

    public static int getMARGIN() {
        return MARGIN / FACTOR_MM_TO_PIXEL;
    }
    public static int getHALF_MARGIN() {
        return (MARGIN / FACTOR_MM_TO_PIXEL) / 2;
    }
    public static int getINDEX_TO_PIXLE(){ return INDEX_TO_PIXEL; }

    /* AStar */
    public static int getFIELD_SIZE() { return FIELD_SIZE / FACTOR_MM_TO_PIXEL; }
    public static int getGRID_SIZE(){ return GRID_SIZE / FACTOR_MM_TO_PIXEL; }



    static {
        try {
            loadFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadFile() throws IOException {
        Properties prop = new Properties();
        prop.loadFromXML(new FileInputStream(xmlPath));

        FIELD_WIDTH =  Integer.parseInt(prop.getProperty("fieldWidth"));
        FIELD_HEIGHT = Integer.parseInt(prop.getProperty("fieldHeight"));


    }
}