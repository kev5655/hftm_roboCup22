package TestLineSmoother;

import com.robotino.drive.LineSmoother;
import com.robotino.logistics.Coordinate;

import java.util.LinkedList;
import java.util.List;

public class TestLineSmoother {


    final List<Coordinate> aStarPath = new LinkedList<>();


    LineSmoother lineSmoother;

    public TestLineSmoother(){
        //for (int i = 0; i < 10; i++) {
        //    aStarPath.add(new Spot(i * 10, i * 10));
        //}

        aStarPath.add(new Coordinate(0, 0));
        aStarPath.add(new Coordinate(0, 1));
        aStarPath.add(new Coordinate(0, 2));
        aStarPath.add(new Coordinate(1, 2));
        aStarPath.add(new Coordinate(2, 2));
        aStarPath.add(new Coordinate(3, 2));
        aStarPath.add(new Coordinate(4, 2));
        aStarPath.add(new Coordinate(4, 3));
        aStarPath.add(new Coordinate(4, 4));


        //lineSmootherV2 = new LineSmootherV2(aStarPath);

        System.out.println(LineSmoother.smooth(aStarPath, 0.5, 0.1, 0.000001));
    }


    public static void main(String[] args) {
        new TestLineSmoother();
    }


}
