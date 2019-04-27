import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Process {
    public ArrayList<LinkList> rings = new ArrayList<LinkList>();
    public Vertex source = new Vertex();
    public ArrayList<Vertex> AllVertexes = new ArrayList<Vertex>();
    public ArrayList<Point> ObstaclePoint = new ArrayList<Point>();
    public ArrayList<PathFromTarget> Paths = new ArrayList<PathFromTarget>();
    // Length is minimum leg length
    public double Length = 50;
    // Alpha is maximum turning angle
    public double Alpha = 30;
    public int width = 800, hight = 600;

    public Process(ArrayList<LinkList> Rings, Point src) {
        rings = Rings;
        Vertex v = new Vertex();
        v.point = src;
        PreVertex pr = new PreVertex();
        pr.dist = 0;
        pr.chain.points.add(src);
        pr.chain.points.add(src);
        pr.chain.id = 0;
        pr.chain.minAngle = 0;
        pr.chain.maxAngle = 359.99;
        pr.vt = v;
        v.pre.add(pr);
        source = v;
        AllVertexes.add(source);
        for (LinkList ring : rings) {
            Node temp = ring.head;
            while (temp.Next != ring.head) {
                Vertex vt = new Vertex();
                vt.point = temp.op;
                vt.PreObstacleVertex = temp.Prev.op;
                ObstaclePoint.add(temp.op);
                AllVertexes.add(vt);
                temp = temp.Next;
            }
            Vertex vt1 = new Vertex();
            vt1.point = temp.op;
            vt1.PreObstacleVertex = temp.Prev.op;
            ObstaclePoint.add(temp.op);
            AllVertexes.add(vt1);
        }
    }

    public void MainLoop() {
        long startTime = System.nanoTime();
        //     FindVisibleVertexesFromVertex(source,ObstaclePoint);//for test
        for (Vertex v : AllVertexes) {
            for (Vertex vt : FindVisibleVertexesFromVertex(v)) {
                //"FillVertexInfo" is fill some information in "vt" by "v" 
                FillVertexInfo(v, vt, ccw(v.point, vt.point, vt.PreObstacleVertex));
            }
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
        System.out.println("That took " + (double) duration / 1000000.0 + " miliseconds");
    }

    public ArrayList<Vertex> FindVisibleVertexesFromVertex(Vertex vt) {
        //define new concept of visibility
        ArrayList<Vertex> visibles = new ArrayList<Vertex>();
        //find visible vertexes from pt and save that into vvs
        //draw chains from pt to all vertices
        //do not intersect to obstacles edge
        for (Vertex v : AllVertexes) {
            //if vt and v is same obstacle
            if (!(v.point.equals(source.point))) {
                if (!v.point.equals(vt.point)) {
                    if (isVisible(vt, v, true) || isVisible(vt, v, false)) {
                        visibles.add(v);
                        GUI.tempdraw.add(v.point); //for test
                    }
                }
            }
        }
        return visibles;
    }

    public boolean isVisible(Vertex v1, Vertex v2, boolean IsCCW) {
        //if v2 is visible from v1 then return true else return false
        if (!IntersectionWithObstacle(v1.point, v2.point)) {
            double degree = IntersectionDegree(v1.point, v2.point);
            for (int i = 0; i < v1.pre.size(); i++) {
                if (angle_is_between_angles(degree, v1.pre.get(i).chain.minAngle, v1.pre.get(i).chain.maxAngle))//v1.pre.get(i).chain.minAngle<=degree && v1.pre.get(i).chain.maxAngle>= degree)
                {
                    return true;
                }
            }
        }
        int sign = IsCCW ? 1 : -1;
        double AB;
        double Gama = Math.toDegrees(Math.atan2(v2.point.y - v1.point.y, v2.point.x - v1.point.x));
        double Teta;
        int m = 1;
        while (true) {
            ChainOfEdge ch = new ChainOfEdge();
            Teta = 0.5 * (Alpha * m);
            double Beta = Gama - (Alpha + Teta) * sign;
            double ResultSin = 0;
            double ResultCos = 0;
            for (int i = 1; i <= m + 1; i++) {
                double degree = (i * Alpha * sign) + Beta;
                ResultCos += Math.cos(Math.toRadians(degree));
            }
            AB = (v2.point.x - v1.point.x) / ResultCos;
            if (AB < Length) {
                break;
            }
            ResultCos = 0;
            ResultSin = 0;
            boolean EdgeIntersect = false;
            ch.points.add(v1.point);
            for (int i = 1; i <= m; i++) {
                double degree = (i * Alpha * sign) + Beta;
                ResultCos += Math.cos(Math.toRadians(degree));
                ResultSin += Math.sin(Math.toRadians(degree));
                Point np = new Point();
                np.x = (int) ((AB * ResultCos) + v1.point.x);
                np.y = (int) ((AB * ResultSin) + v1.point.y);
                ch.points.add(np);
            }
            ch.points.add(v2.point);
            for (int i = 0; i < ch.points.size() - 1; i++) {
                if (IntersectionWithObstacle(ch.points.get(i), ch.points.get(i + 1))) {
                    EdgeIntersect = true;
                }
            }

            if (!EdgeIntersect) {
                GUI.tempCh.add(ch);//for test
                double degree = IntersectionDegree(ch.points.get(0), ch.points.get(1));
                for (int i = 0; i < v1.pre.size(); i++) {
                    if (angle_is_between_angles(degree, v1.pre.get(i).chain.minAngle, v1.pre.get(i).chain.maxAngle))//v1.pre.get(i).chain.minAngle<=degree && v1.pre.get(i).chain.maxAngle>= degree)
                    {
                        return true;
                    }
                }
            }
            m++;
        }
        return false;
    }

    public boolean IntersectionWithObstacle(Point p1, Point p2) {
        for (LinkList ring : rings) {
            Node temp = ring.head;
            while (temp.Next != ring.head) {
                if (p2 != temp.op && p2 != temp.Next.op) //p2 is NOT point of any edge.
                {
                    if (Intersection(p1, p2, temp.op, temp.Next.op)) {
                        return true;
                    }
                }
                temp = temp.Next;
            }
        }
        return false;
    }

    public void FillVertexInfo(Vertex p1, Vertex p2, int IsCCW) {
        int sign;
        if (IsCCW == 1) {
            sign = 1;
        } else {
            sign = -1;
        }
        double AB;
        double Gama = Math.toDegrees(Math.atan2(p2.point.y - p1.point.y, p2.point.x - p1.point.x));
        double Teta;
        double StartDegree = 0;
        int m = 0;
        int id = 0;
        boolean finished_condition = false;
        while (true) {
            Teta = 0.5 * (Alpha * m);
            double Beta = Gama - (Alpha + Teta) * sign;
            double ResultSin = 0;
            double ResultCos = 0;
            for (int i = 1; i <= m + 1; i++) {
                double degree = (i * Alpha * sign) + Beta;
                ResultCos += Math.cos(Math.toRadians(degree));
                ResultSin += Math.sin(Math.toRadians(degree));
            }
            AB = (p2.point.x - p1.point.x + p2.point.y - p1.point.y) / (ResultCos + ResultSin);
            if (AB < Length) {
                break;
            }
            ChainOfEdge chain_of_edge = new ChainOfEdge();
            boolean EdgeIntersect = false;
            chain_of_edge.id = id;
            id++;
            chain_of_edge.points.add(p1.point);
            chain_of_edge.start = p1.point;
            chain_of_edge.end = p2.point;
            ResultCos = 0;
            ResultSin = 0;
            for (int i = 1; i <= m; i++) {
                double degree = (i * Alpha * sign) + Beta;
                ResultCos += Math.cos(Math.toRadians(degree));
                ResultSin += Math.sin(Math.toRadians(degree));
                Point np = new Point();
                np.x = (int) ((AB * ResultCos) + p1.point.x);
                np.y = (int) ((AB * ResultSin) + p1.point.y);
                chain_of_edge.points.add(np);
            }
            chain_of_edge.points.add(p2.point);
            if (m == 0) {
                if (IsCCW == 1) {
                    chain_of_edge.minAngle = IntersectionDegree(p1.point, p2.point);
                    StartDegree = chain_of_edge.minAngle;
                    chain_of_edge.maxAngle = Alpha + chain_of_edge.minAngle;
                    if (chain_of_edge.maxAngle > 360) {
                        chain_of_edge.maxAngle -= 360;
                    }
                } else {// !IsCCW
                    chain_of_edge.minAngle = IntersectionDegree(p1.point, p2.point);
                    chain_of_edge.maxAngle = chain_of_edge.minAngle - Alpha;
                    if (chain_of_edge.maxAngle < 0) {
                        chain_of_edge.maxAngle += 360;
                    }
                    double temp = chain_of_edge.minAngle;
                    chain_of_edge.minAngle = chain_of_edge.maxAngle;
                    chain_of_edge.maxAngle = temp;
                    StartDegree = chain_of_edge.maxAngle;
                }
            } else {
                if (IsCCW == 1) {
                    chain_of_edge.minAngle = Teta + StartDegree + (Alpha / 2);
                    if (chain_of_edge.minAngle > 360) {
                        chain_of_edge.minAngle -= 360;
                    }
                    chain_of_edge.maxAngle = (Alpha / 2) + chain_of_edge.minAngle;
                    if (chain_of_edge.maxAngle > 360) {
                        chain_of_edge.maxAngle -= 360;
                    }
                } else { //!IsCCW
                    chain_of_edge.minAngle = StartDegree - Teta - (Alpha / 2);
                    if (chain_of_edge.minAngle < 0) {
                        chain_of_edge.minAngle += 360;
                    }
                    chain_of_edge.maxAngle = chain_of_edge.minAngle - (Alpha / 2);
                    if (chain_of_edge.maxAngle < 0) {
                        chain_of_edge.maxAngle += 360;
                    }
                    double temp = chain_of_edge.minAngle;
                    chain_of_edge.minAngle = chain_of_edge.maxAngle;
                    chain_of_edge.maxAngle = temp;
                }
                double degree = IntersectionDegree(p2.point, p2.PreObstacleVertex);
                if (angle_is_between_angles(degree, chain_of_edge.minAngle, chain_of_edge.maxAngle)) {
                    if (IsCCW == 1) {
                        chain_of_edge.maxAngle = degree;
                    } else {
                        chain_of_edge.minAngle = degree;
                    }
                    finished_condition = true;
                }
            }
            for (int i = 0; i < chain_of_edge.points.size() - 1; i++) {
                if (IntersectionWithObstacle(chain_of_edge.points.get(i), chain_of_edge.points.get(i + 1))) {
                    EdgeIntersect = true;
                }
            }
            if (!EdgeIntersect) {
                double degree = IntersectionDegree(chain_of_edge.points.get(0), chain_of_edge.points.get(1));
                for (int i = 0; i < p1.pre.size(); i++) {
                    if (chain_of_edge.minAngle != chain_of_edge.maxAngle
                            && angle_is_between_angles(degree, p1.pre.get(i).chain.minAngle, p1.pre.get(i).chain.maxAngle))//p1.pre.get(i).chain.minAngle<=degree && p1.pre.get(i).chain.maxAngle>= degree)
                    {
                        Relax(p1, p2, chain_of_edge);
                    }
                }
            }
            if (finished_condition)
                break;
            m++;
        }
        for (Vertex vt : AllVertexes) {
            if (vt.point.equals(p2.point)) {
                vt.PreObstacleVertex = p2.PreObstacleVertex;
                vt.point = p2.point;
                vt.pre = p2.pre;
                break;
            }
        }

    }

    public boolean Intersection(Point line1V1, Point line1V2, Point line2V1, Point line2V2) {
        if (((ccw(line1V1, line1V2, line2V1) * ccw(line1V1, line1V2, line2V2)) <= 0)
                && ((ccw(line2V1, line2V2, line1V1) * ccw(line2V1, line2V2, line1V2)) <= 0)) {
            return true;
        } else {
            return false;
        }
    }

    int ccw(Point p, Point q, Point r) {
        if (turn(p, q, r) > 0) {
            return 1;
        } else {
            return -1;
        }
    }

    int turn(Point p, Point q, Point r) {
        double result = (r.x - q.x) * (p.y - q.y) - (r.y - q.y) * (p.x - q.x);
        if (result < 0) {
            return -1; // P->Q->R is a right turn
        }
        if (result > 0) {
            return 1; // P->Q->R is a left turn
        }
        return 0; // P->Q->R is a straight line, i.e. P, Q, R are collinear
    }

    public double IntersectionDegree(Point p1, Point p2) {
        // Calculating degree between p1 & p2 , zero is in p1
        double degree = Math.toDegrees(Math.atan2(p2.y - p1.y, p2.x - p1.x));
        if (degree < 0) {
            degree += 360;
        }
        return degree;
    }

    double angle_1to360(double angle) {
        angle = ((int) angle % 360) + (angle - (int) (angle)); //converts angle to range -360 + 360
        if (angle > 0.0) {
            return angle;
        } else {
            return angle + 360.0;
        }
    }

    //check if angle is between angles
    boolean angle_is_between_angles(double N, double a, double b) {
        N = angle_1to360(N); //normalize angles to be 1-360 degrees
        a = angle_1to360(a);
        b = angle_1to360(b);

        if (a < b) {
            return a <= N && N <= b;
        }
        return a <= N || N <= b;
    }

    public void add_chain_to_path(ChainOfEdge chain, ChainOfEdge pre_path, ChainOfEdge path) {
        for (int i = 0; i < pre_path.points.size(); i++) {
            path.points.add(pre_path.points.get(i));
        }
        for (int i = 0; i < chain.points.size(); i++) {
            path.points.add(chain.points.get(i));
        }
    }

    public boolean isBetweenInterval(PreVertex pv, ChainOfEdge chain) {
        //max [()] | , [()|] , [(|)] , [|()]  min
        if (((pv.chain.maxAngle > pv.chain.minAngle) && (chain.maxAngle > chain.minAngle)
                && (pv.chain.maxAngle >= chain.maxAngle) && (chain.maxAngle >= pv.chain.minAngle)
                && (pv.chain.maxAngle >= chain.minAngle) && (chain.minAngle >= pv.chain.minAngle))
                || ((pv.chain.maxAngle < pv.chain.minAngle) && (chain.maxAngle > chain.minAngle)
                && (pv.chain.maxAngle >= chain.maxAngle) && (chain.maxAngle <= pv.chain.minAngle)
                && (pv.chain.maxAngle >= chain.minAngle) && (chain.minAngle <= pv.chain.minAngle))
                || ((pv.chain.maxAngle < pv.chain.minAngle) && (chain.maxAngle > chain.minAngle)
                && (pv.chain.maxAngle >= chain.maxAngle) && (chain.maxAngle <= pv.chain.minAngle)
                && (pv.chain.maxAngle <= chain.minAngle) && (chain.minAngle >= pv.chain.minAngle))
                || ((pv.chain.maxAngle < pv.chain.minAngle) && (chain.maxAngle > chain.minAngle)
                && (pv.chain.maxAngle <= chain.maxAngle) && (chain.maxAngle >= pv.chain.minAngle)
                && (pv.chain.maxAngle <= chain.minAngle) && (chain.minAngle >= pv.chain.minAngle))) {
            return true;
        } //  max ([])| , ([]|) , ([|]) , (|[])  min
        else if (((chain.maxAngle > chain.minAngle) && (pv.chain.maxAngle > pv.chain.minAngle)
                && (chain.maxAngle >= pv.chain.maxAngle) && (pv.chain.maxAngle >= chain.minAngle)
                && (chain.maxAngle >= pv.chain.minAngle) && (pv.chain.minAngle >= chain.minAngle))
                || ((chain.maxAngle < chain.minAngle) && (pv.chain.maxAngle > pv.chain.minAngle)
                && (chain.maxAngle >= pv.chain.maxAngle) && (pv.chain.maxAngle <= chain.minAngle)
                && (chain.maxAngle >= pv.chain.minAngle) && (pv.chain.minAngle <= chain.minAngle))
                || ((chain.maxAngle < chain.minAngle) && (pv.chain.maxAngle < pv.chain.minAngle)
                && (chain.maxAngle >= pv.chain.maxAngle) && (pv.chain.maxAngle <= chain.minAngle)
                && (chain.maxAngle <= pv.chain.minAngle) && (pv.chain.minAngle >= chain.minAngle))
                || ((chain.maxAngle < chain.minAngle) && (pv.chain.maxAngle > pv.chain.minAngle)
                && (chain.maxAngle <= pv.chain.maxAngle) && (pv.chain.maxAngle >= chain.minAngle)
                && (chain.maxAngle <= pv.chain.minAngle) && (pv.chain.minAngle >= chain.minAngle))) {
            return true;
        } // max [ ( ] |   ,   [ ( | ]  ,  [ | ( ] min
        else if (((pv.chain.maxAngle > pv.chain.minAngle) && (pv.chain.maxAngle >= chain.maxAngle)
                && (chain.maxAngle >= pv.chain.minAngle))
                || ((pv.chain.maxAngle < pv.chain.minAngle) && (pv.chain.maxAngle >= chain.maxAngle)
                && (chain.maxAngle <= pv.chain.minAngle))
                || ((pv.chain.maxAngle < pv.chain.minAngle) && (pv.chain.maxAngle <= chain.maxAngle)
                && (chain.maxAngle >= pv.chain.minAngle))) {
            return true;
        } // max [ ) ] |   ,   [ ) | ]  ,  [ | ) ] min
        else if (((pv.chain.maxAngle > pv.chain.minAngle) && (pv.chain.maxAngle >= chain.minAngle)
                && (chain.minAngle >= pv.chain.minAngle))
                || ((pv.chain.maxAngle < pv.chain.minAngle) && (pv.chain.maxAngle >= chain.minAngle)
                && (chain.minAngle <= pv.chain.minAngle))
                || ((pv.chain.maxAngle < pv.chain.minAngle) && (pv.chain.maxAngle <= chain.minAngle)
                && (chain.minAngle >= pv.chain.minAngle))) {
            return true;
        }
        return false;
    }

    public void Relax(Vertex v1, Vertex v2, ChainOfEdge chain) {
        double degree, distance = 0;
        boolean is_empty_again = false;
        ChainOfEdge pv_v1_path = null;
        double chain_minAngle = chain.minAngle, chain_maxAngle = chain.maxAngle;
        ArrayList<PreVertex> pre_temps = new ArrayList<PreVertex>();
        ArrayList<PreVertex> pre_temps_tmp = new ArrayList<PreVertex>();
        degree = IntersectionDegree(chain.points.get(0), chain.points.get(1));
        for (PreVertex pv_v1 : v1.pre) {
            if (angle_is_between_angles(degree, pv_v1.chain.minAngle, pv_v1.chain.maxAngle)) {//pv_v1.chain.minAngle <= degree && pv_v1.chain.maxAngle >= degree) {
                distance = pv_v1.dist;
                pv_v1_path = pv_v1.path;
                break;
            }
        }
        distance += chain.Length();
        //finding interval of v2.pre that match by minAngle & maxAngle of chain
        for (PreVertex pv : v2.pre) {
            if (isBetweenInterval(pv, chain)) {
                pre_temps.add(pv);
            }
        }
        if (pre_temps.isEmpty()) {
            PreVertex pv_temp = new PreVertex();
            pv_temp.dist = distance;
            pv_temp.chain.id = chain.id;
            pv_temp.chain.end = chain.end;
            pv_temp.chain.maxAngle = chain.maxAngle;
            pv_temp.chain.minAngle = chain.minAngle;
            pv_temp.chain.points = chain.points;
            pv_temp.chain.start = chain.start;
            pv_temp.vt = v1;
            add_chain_to_path(chain, pv_v1_path, pv_temp.path);
            if (pv_temp.chain.maxAngle != pv_temp.chain.minAngle) {
                v2.pre.add(pv_temp);
            }
        } else {
            is_empty_again = true;
            pre_temps_tmp.clear();
            for (PreVertex pv : pre_temps) {
                pre_temps_tmp.add(pv);
            }
            double ch_min_changed = -1, ch_max_changed = -1;
            for (PreVertex pv : pre_temps_tmp) {
                //max [()] | , [()|] , [(|)] , [|()]  min
                if (((pv.chain.maxAngle > pv.chain.minAngle) && (chain.maxAngle > chain.minAngle)
                        && (pv.chain.maxAngle >= chain.maxAngle) && (chain.maxAngle >= pv.chain.minAngle)
                        && (pv.chain.maxAngle >= chain.minAngle) && (chain.minAngle >= pv.chain.minAngle))
                        || ((pv.chain.maxAngle < pv.chain.minAngle) && (chain.maxAngle > chain.minAngle)
                        && (pv.chain.maxAngle >= chain.maxAngle) && (chain.maxAngle <= pv.chain.minAngle)
                        && (pv.chain.maxAngle >= chain.minAngle) && (chain.minAngle <= pv.chain.minAngle))
                        || ((pv.chain.maxAngle < pv.chain.minAngle) && (chain.maxAngle < chain.minAngle)
                        && (pv.chain.maxAngle >= chain.maxAngle) && (chain.maxAngle <= pv.chain.minAngle)
                        && (pv.chain.maxAngle <= chain.minAngle) && (chain.minAngle >= pv.chain.minAngle))
                        || ((pv.chain.maxAngle < pv.chain.minAngle) && (chain.maxAngle > chain.minAngle)
                        && (pv.chain.maxAngle <= chain.maxAngle) && (chain.maxAngle >= pv.chain.minAngle)
                        && (pv.chain.maxAngle <= chain.minAngle) && (chain.minAngle >= pv.chain.minAngle))) {
                    if (distance < pv.dist) {
                        PreVertex pv_temp1 = new PreVertex();
                        pv_temp1.dist = distance;
                        pv_temp1.chain.id = chain.id;
                        pv_temp1.chain.end = chain.end;
                        pv_temp1.chain.maxAngle = chain_maxAngle;
                        pv_temp1.chain.minAngle = chain_minAngle;
                        pv_temp1.chain.points = chain.points;
                        pv_temp1.chain.start = chain.start;
                        pv_temp1.vt = v1;
                        add_chain_to_path(chain, pv_v1_path, pv_temp1.path);
                        if (pv_temp1.chain.maxAngle != pv_temp1.chain.minAngle) {
                            v2.pre.add(pv_temp1);
                        }
                        PreVertex pv_temp2 = new PreVertex();
                        pv_temp2.dist = distance;
                        pv_temp2.chain.id = chain.id;
                        pv_temp2.chain.end = chain.end;
                        pv_temp2.chain.maxAngle = chain_maxAngle;
                        pv_temp2.chain.minAngle = chain_minAngle;
                        pv_temp2.chain.points = chain.points;
                        pv_temp2.chain.start = chain.start;
                        pv_temp2.vt = v1;
                        pv_temp2.chain.minAngle = v2.pre.get(v2.pre.indexOf(pv)).chain.minAngle;
                        pv_temp2.chain.maxAngle = chain_minAngle;
                        add_chain_to_path(chain, pv_v1_path, pv_temp2.path);
                        if (pv_temp2.chain.maxAngle != pv_temp2.chain.minAngle) {
                            v2.pre.add(pv_temp2);
                        }
                        v2.pre.get(v2.pre.indexOf(pv)).chain.minAngle = chain_maxAngle;
                        pre_temps.remove(pv);
                        ch_max_changed = ch_min_changed = chain.maxAngle;
                        if (v2.pre.get(v2.pre.indexOf(pv)).chain.minAngle == v2.pre.get(v2.pre.indexOf(pv)).chain.maxAngle) {
                            v2.pre.remove(pv);
                        }
                    } else {
                        ch_max_changed = ch_min_changed = chain.maxAngle;
                        pre_temps.remove(pv);
                    }
                } //  max ([])| , ([]|) , ([|]) , (|[])  min
                else if (((chain.maxAngle > chain.minAngle) && (pv.chain.maxAngle > pv.chain.minAngle)
                        && (chain.maxAngle >= pv.chain.maxAngle) && (pv.chain.maxAngle >= chain.minAngle)
                        && (chain.maxAngle >= pv.chain.minAngle) && (pv.chain.minAngle >= chain.minAngle))
                        || ((chain.maxAngle < chain.minAngle) && (pv.chain.maxAngle > pv.chain.minAngle)
                        && (chain.maxAngle >= pv.chain.maxAngle) && (pv.chain.maxAngle <= chain.minAngle)
                        && (chain.maxAngle >= pv.chain.minAngle) && (pv.chain.minAngle <= chain.minAngle))
                        || ((chain.maxAngle < chain.minAngle) && (pv.chain.maxAngle < pv.chain.minAngle)
                        && (chain.maxAngle >= pv.chain.maxAngle) && (pv.chain.maxAngle <= chain.minAngle)
                        && (chain.maxAngle <= pv.chain.minAngle) && (pv.chain.minAngle >= chain.minAngle))
                        || ((chain.maxAngle < chain.minAngle) && (pv.chain.maxAngle > pv.chain.minAngle)
                        && (chain.maxAngle <= pv.chain.maxAngle) && (pv.chain.maxAngle >= chain.minAngle)
                        && (chain.maxAngle <= pv.chain.minAngle) && (pv.chain.minAngle >= chain.minAngle))) {
                    if (distance < pv.dist) {
                        v2.pre.remove(pv);
                        pre_temps.remove(pv);
                    }
                } // max [ ( ] |   ,   [ ( | ]  ,  [ | ( ] min
                else if (((pv.chain.maxAngle > pv.chain.minAngle) && (pv.chain.maxAngle >= chain.maxAngle)
                        && (chain.maxAngle >= pv.chain.minAngle))
                        || ((pv.chain.maxAngle < pv.chain.minAngle) && (pv.chain.maxAngle >= chain.maxAngle)
                        && (chain.maxAngle <= pv.chain.minAngle))
                        || ((pv.chain.maxAngle < pv.chain.minAngle) && (pv.chain.maxAngle <= chain.maxAngle)
                        && (chain.maxAngle >= pv.chain.minAngle))) {
                    if (distance < pv.dist) {
                        v2.pre.get(v2.pre.indexOf(pv)).chain.minAngle = chain.maxAngle;
                        pre_temps.remove(pv);
                    } else {
                        if (ch_max_changed != chain.maxAngle)
                            ch_max_changed = pv.chain.minAngle;
                        pre_temps.remove(pv);
                    }
                } // max [ ) ] |   ,   [ ) | ]  ,  [ | ) ] min
                else if (((pv.chain.maxAngle > pv.chain.minAngle) && (pv.chain.maxAngle >= chain.minAngle)
                        && (chain.minAngle >= pv.chain.minAngle))
                        || ((pv.chain.maxAngle < pv.chain.minAngle) && (pv.chain.maxAngle >= chain.minAngle)
                        && (chain.minAngle <= pv.chain.minAngle))
                        || ((pv.chain.maxAngle < pv.chain.minAngle) && (pv.chain.maxAngle <= chain.minAngle)
                        && (chain.minAngle >= pv.chain.minAngle))) {
                    if (distance < pv.dist) {
                        v2.pre.get(v2.pre.indexOf(pv)).chain.maxAngle = chain.minAngle;
                        pre_temps.remove(pv);
                    } else {
                        if (ch_min_changed != chain.maxAngle)
                            ch_min_changed = pv.chain.maxAngle;
                        pre_temps.remove(pv);
                    }
                }
            }

            if (ch_max_changed != -1) {
                chain_maxAngle = ch_max_changed;
            }
            if (ch_min_changed != -1) {
                chain_minAngle = ch_min_changed;
            }
        }
        if (!(pre_temps.isEmpty())) {
            Collections.sort(pre_temps, new Comparator<PreVertex>() {
                @Override
                public int compare(PreVertex pre1, PreVertex pre2) {
                    if (pre1.chain.minAngle < pre2.chain.minAngle) {
                        return -1;
                    } else if (pre1.chain.minAngle > pre2.chain.minAngle) {
                        return 1;
                    }
                    return 0;
                }
            });
            for (int i = 0; i <= pre_temps.size() - 2; i++) {
                PreVertex pv_temp = new PreVertex();
                pv_temp.dist = distance;
                pv_temp.chain.id = chain.id;
                pv_temp.chain.end = chain.end;
                pv_temp.chain.maxAngle = chain_maxAngle;
                pv_temp.chain.minAngle = chain_minAngle;
                pv_temp.chain.points = chain.points;
                pv_temp.chain.start = chain.start;
                pv_temp.vt = v1;
                pv_temp.chain.minAngle = pre_temps.get(i).chain.maxAngle;
                pv_temp.chain.maxAngle = pre_temps.get(i + 1).chain.minAngle;
                add_chain_to_path(chain, pv_v1_path, pv_temp.path);
                if (pv_temp.chain.maxAngle != pv_temp.chain.minAngle) {
                    v2.pre.add(pv_temp);
                }
            }
            PreVertex pv_temp1 = new PreVertex();
            pv_temp1.dist = distance;
            pv_temp1.chain.id = chain.id;
            pv_temp1.chain.end = chain.end;
            pv_temp1.chain.maxAngle = chain_maxAngle;
            pv_temp1.chain.minAngle = chain_minAngle;
            pv_temp1.chain.points = chain.points;
            pv_temp1.chain.start = chain.start;
            pv_temp1.vt = v1;
            pv_temp1.chain.maxAngle = pre_temps.get(0).chain.minAngle;
            add_chain_to_path(chain, pv_v1_path, pv_temp1.path);
            if (pv_temp1.chain.maxAngle != pv_temp1.chain.minAngle) {
                v2.pre.add(pv_temp1);
            }
            PreVertex pv_temp2 = new PreVertex();
            pv_temp2.dist = distance;
            pv_temp2.chain.id = chain.id;
            pv_temp2.chain.end = chain.end;
            pv_temp2.chain.maxAngle = chain_maxAngle;
            pv_temp2.chain.minAngle = chain_minAngle;
            pv_temp2.chain.points = chain.points;
            pv_temp2.chain.start = chain.start;
            pv_temp2.vt = v1;
            pv_temp2.chain.minAngle = pre_temps.get(pre_temps.size() - 1).chain.maxAngle;
            pv_temp2.chain.maxAngle = chain_maxAngle;
            add_chain_to_path(chain, pv_v1_path, pv_temp2.path);
            if (pv_temp2.chain.maxAngle != pv_temp2.chain.minAngle) {
                v2.pre.add(pv_temp2);
            }
        } else if (is_empty_again && pre_temps.isEmpty() && chain_maxAngle != chain_minAngle) {
            PreVertex pv_temp = new PreVertex();
            pv_temp.dist = distance;
            pv_temp.chain = chain;
            pv_temp.chain.minAngle = chain_minAngle;
            pv_temp.chain.maxAngle = chain_maxAngle;
            pv_temp.vt = v1;
            add_chain_to_path(chain, pv_v1_path, pv_temp.path);
            v2.pre.add(pv_temp);
        }
    }
////////////////////////////////////// Target Section /////////////////////////////////////////////
    public ArrayList<PathFromTarget> GetChainsFromTargetToVertexes(Point p1) {

        int sign = 1;
        int id = 0;
        ArrayList<PathFromTarget> Info = new ArrayList<PathFromTarget>();
        for (int k = 0; k < 2; k++) {
            for (Vertex p2 : AllVertexes) {
                if (!IntersectionWithObstacle(p1, p2.point)) {
                    double degree = IntersectionDegree(p2.point, p1);
                    for (int i = 0; i < p2.pre.size(); i++) {
                        if (angle_is_between_angles(degree, p2.pre.get(i).chain.minAngle, p2.pre.get(i).chain.maxAngle)) {//p2.pre.get(i).chain.minAngle <= degree && p2.pre.get(i).chain.maxAngle >= degree) {
                            ChainOfEdge tmp_chain = new ChainOfEdge();
                            tmp_chain.points.add(p1);
                            tmp_chain.points.add(p2.point);
                            PathFromTarget tmp_p = new PathFromTarget();
                            tmp_p.chain = tmp_chain;
                            tmp_p.visible = p2.point;
                            tmp_p.preVt = p2.pre.get(i);
                            Info.add(tmp_p);
                            break;
                        }
                    }
                }
                double AB;
                double Gama = Math.toDegrees(Math.atan2(p2.point.y - p1.y, p2.point.x - p1.x));
                double Teta;
                int m = 1;
                while (true) {
                    Teta = 0.5 * (Alpha * m);
                    double Beta = Gama - (Alpha + Teta) * sign;
                    double ResultSin = 0;
                    double ResultCos = 0;
                    for (int i = 1; i <= m + 1; i++) {
                        double degree = (i * Alpha * sign) + Beta;
                        ResultCos += Math.cos(Math.toRadians(degree));
                    }
                    AB = (p2.point.x - p1.x) / ResultCos;
                    if (AB < Length) {
                        break;
                    }
                    ChainOfEdge chain_of_edge = new ChainOfEdge();
                    boolean EdgeIntersect = false;
                    chain_of_edge.id = id;
                    id++;
                    chain_of_edge.points.add(p1);
                    chain_of_edge.start = p1;
                    chain_of_edge.end = p2.point;
                    ResultCos = 0;
                    ResultSin = 0;
                    for (int i = 1; i <= m; i++) {
                        double degree = (i * Alpha * sign) + Beta;
                        ResultCos += Math.cos(Math.toRadians(degree));
                        ResultSin += Math.sin(Math.toRadians(degree));
                        Point np = new Point();
                        np.x = (int) ((AB * ResultCos) + p1.x);
                        np.y = (int) ((AB * ResultSin) + p1.y);
                        chain_of_edge.points.add(np);
                    }
                    chain_of_edge.points.add(p2.point);
                    for (int i = 0; i < chain_of_edge.points.size() - 1; i++) {
                        if (IntersectionWithObstacle(chain_of_edge.points.get(i), chain_of_edge.points.get(i + 1))) {
                            EdgeIntersect = true;
                        }
                    }
                    if (!EdgeIntersect) {
                        double degree = IntersectionDegree(chain_of_edge.points.get(chain_of_edge.points.size() - 1), chain_of_edge.points.get(chain_of_edge.points.size() - 2));
                        for (int i = 0; i < p2.pre.size(); i++) {
                            if (angle_is_between_angles(degree, p2.pre.get(i).chain.minAngle, p2.pre.get(i).chain.maxAngle)) {//p2.pre.get(i).chain.minAngle <= degree && p2.pre.get(i).chain.maxAngle >= degree) {
                                PathFromTarget tmp = new PathFromTarget();
                                tmp.chain = chain_of_edge;
                                tmp.visible = p2.point;
                                tmp.preVt = p2.pre.get(i);
                                Info.add(tmp);
                                break;
                            }
                        }
                    }
                    m++;
                }//End of While(true)
            }//End of for(allvertexes)

            sign = -1;
        }//End of for (k=0 to 2)
        return Info;
    }

    public ArrayList<Point> MainFunction(Point target) {

        long startTime = System.nanoTime();

        ArrayList<Point> shortest_path = new ArrayList<Point>();
        ArrayList<PathFromTarget> paths = GetChainsFromTargetToVertexes(target);

        if (!paths.isEmpty()) {
            PathFromTarget minPath = null;
            for (PathFromTarget path : paths) {
                if (minPath == null || (path.chain.Length() + path.preVt.dist < minPath.chain.Length() + minPath.preVt.dist)) {
                    minPath = path;
                }
            }
            for (Point pt : minPath.preVt.path.points) {
                shortest_path.add(pt);
            }
            for (int i = minPath.chain.points.size() - 1; i >= 0; i--)//Point pt:minPath.chain.points)
            {
                shortest_path.add(minPath.chain.points.get(i));
            }

        }
        long endTime = System.nanoTime();
        long duration = (endTime - startTime);  //divide by 1000000 to get milliseconds.
        System.out.println("Query Time is: " + (double) duration / 1000000.0 + " milisecond");


        double length1 = 0;
        for (int i = 0; i < shortest_path.size() - 1; i++) {
            length1 += Math.sqrt((Math.pow(shortest_path.get(i + 1).x - shortest_path.get(i).x, 2)) + (Math.pow(shortest_path.get(i + 1).y - shortest_path.get(i).y, 2)));
        }
        System.out.println("Shortest Path Length is: = " + (length1));

        return shortest_path;

    }

}
