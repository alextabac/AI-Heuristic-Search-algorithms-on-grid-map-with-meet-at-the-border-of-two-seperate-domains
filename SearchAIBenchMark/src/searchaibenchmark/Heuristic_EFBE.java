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

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author atabacar
 */
public class Heuristic_EFBE implements HeuristicInterface {
    private SEARCH_Map map;
    private MetricInterface metricObj;
    private NodeInterface startNode, goalNode;
    private double startW, nextW;
    private List<NodeInterface> border;
    private NodeInterface b, b0;
    private CoordInterface c;
    private double h,t,g;
    private int N;
    private List<String> quer;
    
    Heuristic_EFBE() {}
    
    @Override public void setStartGoalCoords(CoordInterface start, CoordInterface goal) {
        this.startNode = new Node(start);
        this.goalNode = new Node(goal);
        this.startNode.setParent(this.startNode.create());
        this.goalNode.setParent(this.goalNode.create());
        quer = new ArrayList<>();
        quer.add("g-value,scan-count\n");
    }
    
    @Override public long getMetricCallCount() {
        try { 
            FileWriter writer = new FileWriter("scanCounts_Gs.csv");
            for (int i=0; i<quer.size(); i++){
                writer.write(quer.get(i));
            }
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Heuristic_EFBE.class.getName()).log(Level.SEVERE, null, ex);
        }
        return metricObj.getMetricCount();
    }
    //@Override public void setOrderedBorder(List<NodeInterface> ordB) {}
    @Override public double getStartW() {return startW;}
    @Override public double getNextW() {return nextW;}
    @Override public NodeInterface getStartN() {return startNode;}
    @Override public NodeInterface getGoalN() {return goalNode;}
    @Override public void setMap(SEARCH_Map aMap) {this.map = aMap;}
    @Override public MetricInterface getMetricObj() {return metricObj;}
    @Override public List<NodeInterface> getBorder() {return this.border;}
    @Override public void setBorder(List<NodeInterface> newB) {this.border=newB; this.N=newB.size();}
    @Override public void setMultiByIndex(int i, List<NodeInterface> localB) {}
    @Override public void setMultiVerse(List<List<NodeInterface>> aMulti) {}
    
    @Override public void additionalSetup(boolean BHS) {
        List<NodeInterface> mapBorders = map.getBorderNodes();  // map.border are the nodes on map, this.border are new instances copy
        List<NodeInterface> mapOppBorders = map.getOppMap().getBorderNodes();
        List<NodeInterface> hOppBorders = map.getOppMap().getHfunc().getBorder();
        
        // for each border node setting f-value to be g+h, 
        // where g-value is h from startF to border,
        // and h-value is h from startB to 
        List<NodeInterface> bList = new ArrayList<>(this.N);
        List<NodeInterface> bList2 = new ArrayList<>(this.N);
        //f0 = Double.MAX_VALUE;
        for (int i=0; i<N; i++) {
            NodeInterface bo = mapBorders.get(i);
            NodeInterface bc = border.get(i);  // these are new node instances for borders (same coordinates)
            if (!bo.equalNode(bc)) System.out.println("** ** ** Border order mismatch bo:bc !!!!!!!!!!!!!!!!!!!!!!");
            bo.setG( metricObj.h(startNode.getCoord(), bo.getCoord(), startW) );
            bo.setH( metricObj.h(goalNode.getCoord(), bo.getCoord(), nextW) );
            bc.setG( bo.getG() );
            bc.setH( bo.getH() );
            bc.setF( bc.getG() + bc.getH() );
            UtilityFunc.insertInListF(bList, bc);

            NodeInterface bo2 = mapOppBorders.get(i);
            NodeInterface bc2 = hOppBorders.get(i);  // these are new node instances for borders (same coordinates)
            if (!bo2.equalNode(bc2)) System.out.println("** ** ** Border order mismatch bo2:bc2 !!!!!!!!!!!!!!!!!!!!!!");
            if (!bo.equalNode(bo2)) System.out.println("** ** ** Border order mismatch bo:bo2 !!!!!!!!!!!!!!!!!!!!!!");
            bo2.setG( bo.getH() );
            bo2.setH( bo.getG() );
            bc2.setG( bc.getH() );
            bc2.setH( bc.getG() );
            bc2.setF( bc2.getG() + bc2.getH() );
            UtilityFunc.insertInListF(bList2, bc2);
            //d = bc.getG() + bc.getH();
            //if (d < f0) {f0=d; b0=bc; map.getOppMap().getHfunc().setB0(bc2);}
        }
        b0 = bList.get(0);
        map.getOppMap().getHfunc().setB0(bList2.get(0));
        this.border = bList; // updating the copy border (of h func) with the sorted list, for faster H calc
        map.getOppMap().getHfunc().setBorder(bList2);
    }

    @Override public void setHFunc(SEARCH_Map map, MetricInterface metObj, double weightF, double weightB) {
        this.map = map;
        this.metricObj = metObj;
        this.startW = weightF;
        this.nextW = weightB;
    }
    
    @Override public HeuristicInterface createTheOppositeObj() {
        HeuristicInterface oppHeuristic = new Heuristic_EFBE();
        oppHeuristic.setHFunc(map.getOppMap(), metricObj.createOppositeObj(), nextW, startW);
        oppHeuristic.setStartGoalCoords(goalNode.getCoord(), startNode.getCoord());
        
        List<NodeInterface> bList = new ArrayList<>(this.N);
        List<NodeInterface> bN = map.getBorderNodes();
        NodeInterface tmpB;
        for (NodeInterface b: bN) {
            tmpB = b.create();
            tmpB.setIsBorder(true);
            tmpB.setIsCrossed(true);
            bList.add(tmpB);
        }
        oppHeuristic.setBorder(bList);
        
        return oppHeuristic;
    }
    
    @Override public double calcGenericH(NodeInterface n) {return calcGenericH(n, goalNode);}
    @Override public double calcGenericH(NodeInterface n, NodeInterface goalN) {
        throw new java.lang.RuntimeException("*** Calling EFBE calcGenericH() which is not ready yet, suspected invalid usage.");
    }
    
    @Override public double calcFStart() {return border.get(0).getF();}
    @Override public double getHtoB(NodeInterface n) {return b0.getG();}
    @Override public void setB0(NodeInterface b) {this.b0=b;}
    @Override public double calcHborder(NodeInterface n) {return n.getH();}
    @Override public double calcH(NodeInterface n) {
        if (n.getIsCrossed()) return metricObj.h(n.getCoord(), goalNode.getCoord(), this.nextW);
        else {
            //int qn = 1;
            //double qg;
            //qg = n.getG();
            // Here assuming that for the border nodes (copies from map nodes),
            // the g-value is the heuristic from startF to border
            // and the h-value is the heuristic from startB to border
            g = n.getG();
            c = n.getCoord();
            b = border.get(0);
            h = metricObj.h(c, b.getCoord(), startW) + b.getH();
            for (int i=1; i<N; i++) {
                b = border.get(i);
                if (b.getF() >= (g + h)) break;
                //qn++;
                t = metricObj.h(c, b.getCoord(), startW) + b.getH();
                if (t < h) h = t;
            }
            //quer.add(String.format("%.4f,%d\n",qg,qn));
            return h;
        }
    }

    @Override public double calcHRegOrBorder(NodeInterface n) {
        if (n.getIsBorder()) return n.getH();
        return calcH(n);
    }
}
