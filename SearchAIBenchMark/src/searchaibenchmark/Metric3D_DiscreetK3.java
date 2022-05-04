/*
 * The MIT License
 *
 * Copyright 2022 atabacar.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package searchaibenchmark;

/**
 *
 * @author atabacar
 */
public class Metric3D_DiscreetK3 implements MetricInterface {
    private static double dx,dy,dz;
    private static double d1,d2,d3;
    private CoordInterface start, goal;
    
    Metric3D_DiscreetK3() {}
    Metric3D_DiscreetK3(CoordInterface start, CoordInterface goal) {
        this.start = start.create();
        this.goal = goal.create();
    }
    
    @Override public long getMetricCount() {return -1;}
    @Override public CoordInterface getStart() {return start;}
    @Override public CoordInterface getGoal() {return goal;}
    
    @Override public void setCoords(CoordInterface start, CoordInterface goal) {
        this.start = start.create();
        this.goal = goal.create();
    }
    
    @Override
    public MetricInterface createOppositeObj() {
        return new Metric3D_DiscreetK3(goal, start);
    }
    
    // sorting 3 numbers efficiently into 3 new vars , average actions 5.67
    private void sortDeltasXYZIntoD123() {
        if (dy<=dx) {
            if (dz<=dy) {
                d1 = dz;
                d2 = dy;
                d3 = dx;
            } else {  
                if (dz<=dx) {
                    d1 = dy;
                    d2 = dz;
                    d3 = dx;
                } else {
                    d1 = dy;
                    d2 = dx;
                    d3 = dz;
                }
            }
        } else {
            if (dz<=dx) {
                d1 = dz;
                d2 = dx;
                d3 = dy;
            } else {
                if (dz<=dy) {
                    d1 = dx;
                    d2 = dz;
                    d3 = dy;
                } else {
                    d1 = dx;
                    d2 = dy;
                    d3 = dz;
                }
            }
        }
    }
    
    @Override
    public double h(CoordInterface c, double f) {
        dx = Math.abs(c.getX()-goal.getX());
        dy = Math.abs(c.getY()-goal.getY());
        dy = Math.abs(c.getZ()-goal.getZ());
        sortDeltasXYZIntoD123();
        d3 -= d2;
        d2 -= d1;        
        return f * (d3 + d2 * SQRT2 + d1 * SQRT3);
    }
    
    @Override
    public double h(CoordInterface c1, CoordInterface c2, double f) {
        dx = Math.abs(c1.getX()-c2.getX());
        dy = Math.abs(c1.getY()-c2.getY());
        dy = Math.abs(c1.getZ()-c2.getZ());
        sortDeltasXYZIntoD123();
        d3 -= d2;
        d2 -= d1;        
        return f * (d3 + d2 * SQRT2 + d1 * SQRT3);
    }
    
//    // sorting 3 numbers efficiently into same vars (in place), average actions 5.5 
//    private void sortDeltasXYZIntoD123_rev2() {
//        if (dx<dy) {
//            if (dy<dz) {
//                // do nothing
//            } else {
//                if (dx<dz) {
//                    tmp=dz;
//                    dz=dy;
//                    dy=tmp;
//                } else {
//                    tmp=dz;
//                    dz=dy;
//                    dy=dx;
//                    dx=tmp;
//                }
//            }
//        } else {
//            if (dx<dz) {
//                tmp=dx;
//                dx=dy;
//                dy=tmp;
//            } else {
//                if (dy<dz) {
//                    tmp=dx;
//                    dx=dy;
//                    dy=dz;
//                    dz=tmp;
//                } else {
//                    tmp=dx;
//                    dx=dz;
//                    dz=tmp;
//                }
//            }
//        }
//    }    
}
