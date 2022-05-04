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
public class Neighbors2D implements NeighborsInterface {
    private final int N_SIZE = 8;
    private double maxStep;
    private int[] circ;
    private int index;
    private StepCost[] stepsNcosts;
    private CoordInterface now;
    private StepCost onestep;
    
    Neighbors2D() { initArrays(1.0); }
    Neighbors2D(double w) { initArrays(w); }
    @Override public double getMaxStep() {return this.maxStep;}
    
    private void initArrays(double w) {
        this.maxStep = 2.0 * w * SQRT2;
        stepsNcosts = new StepCost[N_SIZE];
        circ = new int[N_SIZE];
        double s2 = SQRT2*w;
        double[] stepW;
        int[] stepX;
        int[] stepY;
        stepW =      new double[]{   s2,    w,    s2,    w,    s2,    w,    s2,    w};
        stepX =         new int[]{   -1,    0,     1,    1,     1,    0,    -1,   -1};
        stepY =         new int[]{   -1,   -1,    -1,    0,     1,    1,     1,    0};
        for (int i=0; i<N_SIZE; i++) {
            this.stepsNcosts[i] = new StepCost(new Coord2D(stepX[i], stepY[i]));
            stepsNcosts[i].cost = stepW[i];
        }
        onestep = new StepCost(new Coord2D());
        int nm = N_SIZE -1;
        for (int i=0; i<nm; i++) circ[i] = i+1;
        circ[nm] = 0; // closing the loop for circular index
        index = nm;
    }
    
    @Override public void initCoord(CoordInterface c) {now=c;}
    
    @Override public StepCost nextNeighbor() {
        index = circ[index];
        onestep.getSum(now, stepsNcosts[index]);
        return onestep;
    }    

    @Override public int getNeighborsSize() {return N_SIZE;}
}
