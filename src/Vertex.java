
import java.awt.Point;
import java.util.ArrayList;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mohammad
 */
public class Vertex {
    
    public Point point=new Point();

    // "IsVirtual" is "true" if verex is virtual,
    //else (vertex is actual) "IsVirtual" is false 
    //public boolean IsVirtual=false;

    // if vertex is actual, "PreObstacleVertex" is prev vertex of peresent vertex
    //else(is virtual vertex) "PreObstacleVertex" is null
    //"PreObstacleVertex" just is use in ccw detecting
   public Point PreObstacleVertex=new Point();
   //vertex does not have continues interval of degree and does not have minDegree & maxDegree 
   //public double MaxAngle=359, MinAngle=0; 
   public ArrayList<PreVertex> pre=new ArrayList<PreVertex>();// this convert to priority queue in after
}
