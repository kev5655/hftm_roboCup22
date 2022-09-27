package localGui;

import com.robotino.drive.Path;
import com.robotino.logistics.Coordinate;

import java.util.LinkedList;
import java.util.List;

public class PathFx {

    // Wirder ferwende für das FX
    private final List<Line> smoothedFxPath = new LinkedList<>();
    //Path path;

    public PathFx(Path path){
        List<Coordinate> paht = path.getAStarPath();
        for (int i = 0; i < paht.size() - 1; i += 2) {
            smoothedFxPath.add(new Line(paht.get(i), paht.get(i + 1)));
        }
    }

    public List<Line> getSmoothedFxPath(){
        return smoothedFxPath;
    }
}
