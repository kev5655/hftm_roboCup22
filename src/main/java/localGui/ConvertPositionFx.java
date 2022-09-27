package localGui;


import com.robotino.helperClass.ConvertPosition;
import com.robotino.helperClass.Data;
import com.robotino.logistics.Coordinate;
import gui.data.GuiData;

public class ConvertPositionFx extends ConvertPosition {

    public Coordinate AStarGridIndexToGuiCoordinateMiddelOfField(Coordinate c){
        c = AStarGridIndexToGuiCoordinate(c);
        double x = (c.getX() - 1) + ((double) Data.getGRID_SIZE() / 2);
        double y = (c.getY() - 1) + ((double) Data.getGRID_SIZE() / 2);
        return new Coordinate(x, y);
    }

    public Coordinate AStarGridIndexToGuiCoordinate(Coordinate c){
        double x = (c.getX() * Data.getGRID_SIZE() + 1) + Data.getHALF_MARGIN();
        double y = (c.getY() * Data.getGRID_SIZE() + 1) + Data.getHALF_MARGIN();
        return new Coordinate(x, y);
    }
}
