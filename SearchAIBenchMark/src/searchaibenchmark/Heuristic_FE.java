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
public class Heuristic_FE implements HeuristicInterface {
    private SEARCH_Map map;
    private MetricInterface metricObj;
    private NodeInterface startNode, goalNode;
    private double startW, nextW, minWeight;
    
    Heuristic_FE() {}
    
    @Override public void additionalSetup(boolean BHS) {}
    @Override public void setMap(SEARCH_Map aMap) {}
    
    @Override public void setStartGoalCoords(CoordInterface start, CoordInterface goal) {
        this.startNode = new Node(start);
        this.goalNode = new Node(goal);
        this.startNode.setParent(this.startNode.create());
        this.goalNode.setParent(this.goalNode.create());
    }
    
    @Override public long getMetricCallCount() {return metricObj.getMetricCount();}
    //@Override public void setOrderedBorder(List<NodeInterface> ordB) {}
    @Override public double getStartW() {return startW;}
    @Override public double getNextW() {return nextW;}
    @Override public NodeInterface getStartN() {return startNode;}
    @Override public NodeInterface getGoalN() {return goalNode;}
    @Override public MetricInterface getMetricObj() {return metricObj;}
    @Override public void setMultiByIndex(int i, List<NodeInterface> localB) {}
    @Override public void setMultiVerse(List<List<NodeInterface>> aMulti) {}
    
    @Override public void setHFunc(SEARCH_Map map, MetricInterface metObj, double weightF, double weightB) {
        this.map = map;
        this.metricObj = metObj;
        this.startW = weightF;
        this.nextW = weightB;
        this.minWeight = Math.min(startW, nextW);
    }
    @Override public List<NodeInterface> getBorder() {
        throw new java.lang.RuntimeException("*** Calling getBorder() for FE heuristic, suspected invalid usage.");
    }
    @Override public void setBorder(List<NodeInterface> newB) {
        //throw new java.lang.RuntimeException("*** Calling setBorder() for FE heuristic, suspected invalid usage.");
    }
    
    @Override public HeuristicInterface createTheOppositeObj() {
        HeuristicInterface oppHeuristic = new Heuristic_FE();
        oppHeuristic.setHFunc(map.getOppMap(), metricObj.createOppositeObj(), nextW, startW);
        oppHeuristic.setStartGoalCoords(goalNode.getCoord(), startNode.getCoord());
        return oppHeuristic;
    }
    
    
    @Override public double getHtoB(NodeInterface n) {
        // it has no knowledge about border locations
        // assuming the border is in the middle
        return startW/(startW+nextW);
    }
    
    @Override public void setB0(NodeInterface b) {}
    @Override public double calcHborder(NodeInterface n) {return calcH(n);}
    @Override public double calcFStart() {return calcH(startNode);}
    @Override public double calcH(NodeInterface n) {
        if (n.getIsCrossed()) return metricObj.h(n.getCoord(), goalNode.getCoord(), this.nextW);
        else return metricObj.h(n.getCoord(), goalNode.getCoord(), this.minWeight);
    }
    @Override public double calcHRegOrBorder(NodeInterface n) {
        return calcH(n);
    }
    
    @Override public double calcGenericH(NodeInterface n) {return calcGenericH(n, goalNode);}
    @Override public double calcGenericH(NodeInterface n, NodeInterface goalN) {
        if (goalN.getIsBorder()){
            if (n.getIsCrossed()) return metricObj.h(n.getCoord(), goalN.getCoord(), this.nextW);
            else return metricObj.h(n.getCoord(), goalN.getCoord(), this.startW);
        } else {
            if (n.getIsCrossed()) {
                if (n.getIsCrossed()) return metricObj.h(n.getCoord(), goalN.getCoord(), this.minWeight);
                else return metricObj.h(n.getCoord(), goalN.getCoord(), this.startW);
            } else {
                if (n.getIsCrossed()) return metricObj.h(n.getCoord(), goalN.getCoord(), this.nextW);
                else return metricObj.h(n.getCoord(), goalN.getCoord(), this.minWeight);
            }
        }
    }
}
