package localGui;


import com.robotino.logistics.Coordinate;

/**
 * A class to represent a line created from two points
 * @author Derek Springer
 */
public class Line {

    public final double x1;
    public final double y1;
    public final double x2;
    public final double y2;

    public Line(Coordinate c1, Coordinate c2) {
        this.x1 = c1.getX();
        this.y1 = c1.getY();
        this.x2 = c2.getX();
        this.y2 = c2.getY();
    }


    public double slope() {
        if(x2-x1 == 0) return Double.NaN;
        return (y2-y1) / (x2-x1);
    }

    public double intercept() {
        return y1 - slope() * x1;
    }

    public static double slope(double x1, double y1, double x2, double y2) {
        return (y2-y1)/(x2-x1);
    }

    public static double slope(Coordinate point1, Coordinate point2) {
        return slope(point1.getX(), point1.getY(), point2.getX(), point2.getY());
    }

    public static double intercept(double x1, double y1, double x2, double y2) {
        return y1 - slope(x1, y1, x2, y2) * x1;
    }

    public static double intercept(Coordinate point1, Coordinate point2) {
        return intercept(point1.getX(), point1.getY(), point2.getX(), point2.getY());
    }

    @Override
    public String toString() {
        return "[(" + x1 + ", " + x2 + "), (" + y1 + ", " + y2 + ")] " +
                "m=" + slope() + ", b=" + intercept();
    }
}
