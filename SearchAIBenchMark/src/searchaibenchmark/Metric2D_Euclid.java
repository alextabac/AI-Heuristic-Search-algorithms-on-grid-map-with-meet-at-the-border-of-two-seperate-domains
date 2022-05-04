/*
 * The MIT License
 *
 * Copyright 2022 Alexandru Paul Tabacaru.
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
public class Metric2D_Euclid implements MetricInterface {
    private CoordInterface start, goal;
    
    Metric2D_Euclid() {}
    Metric2D_Euclid(CoordInterface start, CoordInterface goal) {
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
        return new Metric2D_Euclid(goal, start);
    }
    
    @Override
    public double h(CoordInterface c, double f) {
        return f * Math.hypot(c.getX() - goal.getX(), c.getY() - goal.getY());
    }
    @Override
    public double h(CoordInterface c1, CoordInterface c2, double f) {
        return f * Math.hypot(c1.getX() - c2.getX(), c1.getY() - c2.getY());
    }
}
