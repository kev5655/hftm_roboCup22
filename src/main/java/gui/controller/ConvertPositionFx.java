package gui.controller;


import gui.data.GuiData;
import com.robotino.helperClass.ConvertPosition;
import com.robotino.logistics.Coordinate;

public class ConvertPositionFx extends ConvertPosition {

    public Coordinate AStarGridIndexToGuiCoordinateMiddelOfField(Coordinate c){
        c = AStarGridIndexToGuiCoordinate(c);
        double x = (c.getX() - 1) + ((double) GuiData.getGRID_SIZE() / 2);
        double y = (c.getY() - 1) + ((double) GuiData.getGRID_SIZE() / 2);
        return new Coordinate(x, y);
    }

    public Coordinate AStarGridIndexToGuiCoordinate(Coordinate c){
        double x = (c.getX() * GuiData.getGRID_SIZE() + 1) + GuiData.getHALF_MARGIN();
        double y = (c.getY() * GuiData.getGRID_SIZE() + 1) + GuiData.getHALF_MARGIN();
        return new Coordinate(x, y);
    }
}
