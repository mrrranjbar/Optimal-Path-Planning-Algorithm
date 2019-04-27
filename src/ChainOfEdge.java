import java.awt.Point;
import java.util.ArrayList;

public class ChainOfEdge {
    int id;
    double minAngle,maxAngle;
    ArrayList<Point> points=new ArrayList<Point>();
    Point start,end;
    public double Length()
    {
        double length=0;
        for(int i=0;i<points.size()-1;i++)
        {
           length+= Math.sqrt((Math.pow(points.get(i+1).x-points.get(i).x, 2))+(Math.pow(points.get(i+1).y-points.get(i).y, 2)));
        }
        return length;
    }
}

