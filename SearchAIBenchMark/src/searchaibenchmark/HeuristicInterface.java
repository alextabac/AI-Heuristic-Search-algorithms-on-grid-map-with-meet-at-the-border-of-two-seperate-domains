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

import java.util.List;

/**
 *
 * @author atabacar
 */
public interface HeuristicInterface {
    
    //public void setOrderedBorder(List<NodeInterface> ordB);
    void setMultiVerse(List<List<NodeInterface>> aMulti);
    void setMultiByIndex(int i, List<NodeInterface> localB);
    void setStartGoalCoords(CoordInterface start, CoordInterface goal);
    void setHFunc(SEARCH_Map map, MetricInterface metObj, double weightF, double weightB);
    HeuristicInterface createTheOppositeObj();
    void additionalSetup(boolean BHS);
    long getMetricCallCount();
    double getHtoB(NodeInterface n);
    double calcFStart();
    double calcH(NodeInterface n);
    //double calcH(NodeInterface n, NodeInterface goal);
    double calcHborder(NodeInterface borderNode);
    double calcHRegOrBorder(NodeInterface borderNode);
    double calcGenericH(NodeInterface n);
    double calcGenericH(NodeInterface n, NodeInterface goal);
    double getStartW();
    double getNextW();
    NodeInterface getStartN();
    NodeInterface getGoalN();
    void setMap(SEARCH_Map aMap);
    void setB0(NodeInterface b);
    MetricInterface getMetricObj();
    List<NodeInterface> getBorder();
    void setBorder(List<NodeInterface> newB);
//    public HeuristicInterface createBackwardHFuncObj() {
//        HeuristicInterface backHFunc = this.heuristicObj.getClass().getDeclaredConstructor().newInstance();
//    }
    
    
    
}
