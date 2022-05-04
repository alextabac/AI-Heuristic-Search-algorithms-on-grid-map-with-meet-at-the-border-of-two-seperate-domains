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
public interface MetricInterface {
    public final double SQRT2 = Math.sqrt(2.0);
    public final double SQRT3 = Math.sqrt(3.0);
    public final String EUCLID = "Euclid";
    public final String MANHATTAN = "Manhattan";
    public final String DISCREETK3 = "DiscreetK3";
    
    public long getMetricCount();
    public CoordInterface getStart();
    public CoordInterface getGoal();
    public MetricInterface createOppositeObj();
    public void setCoords(CoordInterface start, CoordInterface goal);
    public double h(CoordInterface n1, double f);
    public double h(CoordInterface c1, CoordInterface c2, double f);
}
