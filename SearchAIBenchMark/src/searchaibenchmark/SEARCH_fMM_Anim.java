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
public class SEARCH_fMM_Anim extends SEARCH_BASE implements Runnable {
    private NeighborsInterface currNeighbors;
    private StepNCostInterface stepcost;
    private CoordInterface currCoord;
    private NodeInterface currentNode, startFN, startBN;
    private SEARCH_Map mapF, mapB, currMap; //, oppMap;
    private int N; // amount of neighbor cells
    private boolean completed;
    private double UBL;
    private double p, oppP;
    
    SEARCH_fMM_Anim(boolean is2D, HeuristicInterface hFunc, String mName, SEARCH_Map aMap, 
                 String heuristicName, double heuristicWeightF, double heuristicWeightB) 
            {super(is2D, hFunc, mName, aMap, heuristicName, heuristicWeightF, heuristicWeightB);}
    
    
    @Override
    public void run() {
        long tStart = System.nanoTime();
        path = findPath(startCoordF, startCoordB);
        if (!exit) {
            long time = System.nanoTime() - tStart;
            runTime = time / 1e9;
            UtilityFunc.addPathToMapN(map, path);
            //System.out.println("Run time: " + runTime);
            gui.pathN = path;
            gui.runTime = runTime;
            gui.nExpanded = nExpanded;
            gui.nGenerated = nGenerated;
            gui.nReExpanded = nReExpanded;
            steps = path.size();
            gui.pathSteps = steps;
            gui.pathLength = SumOfPaths;
            gui.pathMKSP = Makespan;
            if (path == null) System.out.println("Path not found !");
            //gui.updateStatusBar(startX, startY);
            matPanel.repaint();
        }
    }
    
    
    //          Setting node priority and side selection functions
    // ====================================================================
    private void selectExpansionSide() {
        if (mapF.getOpenList().get(0).getPr() < mapB.getOpenList().get(0).getPr()) {
            currMap = mapF;
            //oppMap = mapB;
        } else {
            currMap = mapB;
            //oppMap = mapF;
        }
    }
    private void setNodePr(NodeInterface node) {
        //node.setPr(Math.max(node.getF(), 2.0 * node.getG() + minWeight));
        if (currMap == mapF)
            node.setPr(Math.max(node.getF(), node.getG()/p + minWeight));
        else
            node.setPr(Math.max(node.getF(), node.getG()/oppP + minWeight));
    }
        
    
    //                  PRE-INIT & INIT functions
    // ====================================================================
    private void preInitializeFind(CoordInterface startF) {
        stepcost = new StepCost(startF);
        currCoord = startF.create();
        path = null;
        completed = false;
        UBL = Double.MAX_VALUE;
        this.mapF = map;
        
        // this copy needs to be identitcal to mapF but with its objects new created in memory,
        // should not have reference to same location in memory. 
        // As for the border nodes: the border nodes G and H values need to be swapped.
        // this swap is needed for the opposite direction search when reaching the border.
        // As for the neighbros object, need to swap the start and the next for same reason.
        this.mapB = map.createCopyMap(); // also creates a new hFuncB for mapB.
        this.hFuncB = map.getOppMap().getHfunc();
        //System.out.println("Starting search:  " + this.getClass().getName().split("[.]")[1].split("[_]")[1] + 
        //        " + " + hFuncF.getClass().getName().split("[.]")[1].split("[_]")[1]);
        this.mapF.setMeeting(null);
        this.mapB.setMeeting(null);
        
        N = this.neighborsF.getNeighborsSize();
    }
    
    private void initializeFind() {
        this.hFuncF.additionalSetup(true); // also updates the border heuristics for hFuncB.

        startFN = hFuncF.getStartN();
        //startFN.setParent(startFN.createCopyL());
        startFN.setF(hFuncF.calcH(startFN));
        mapF.setOpen(startFN);
        
        startBN = hFuncB.getStartN();
        //startBN.setParent(startBN.createCopyL());
        startBN.setF(hFuncB.calcH(startBN));
        mapB.setOpen(startBN);

        double p1 = hFuncF.getHtoB(startFN);
        double p2 = hFuncB.getHtoB(startBN);
        p = p1 / (p1 + p2);
        oppP = 1.0 - p;
        
        setNodePr(startFN);
        mapF.addNodeToOpenList(startFN);
        setNodePr(startBN);
        mapB.addNodeToOpenList(startBN);
    }
    
    
    //                     MAIN LOOP FOR NODE SELECTION
    // ====================================================================
    @Override public List<NodeInterface> findPath(CoordInterface startF, CoordInterface startB) {
        if (!initialCheckBeforeSearch(startF, startB)) return null; // check if valid
        preInitializeFind(startF);

        tStart = System.nanoTime();   //  record start time for search run-time
        
        initializeFind();
                
        // ======  Loop to extract next node from open list
        while (true) { //(nowF != null  &&  nowB != null) {
            if (checkTimeOut()) return null;
            selectExpansionSide();
            currentNode = currMap.removeFromOpen(0);
            currMap.setClosed(currentNode.getCoord());
            expandNode(currentNode, currMap.getOpenList());
            if (completed)  break;
            if (currMap.isOpenListEmpty()) break;
        }
        
        if (this.mapF.getMeeting() != null) {
            path = foundPathBi(this.mapF.getMeeting(), startFN, 
                               this.mapB.getMeeting(), startBN);
            return path;
        } else {
            resetParametersEnd(STATE_NOT_FOUND_PATH);
            return null;
        }
    }

    private void checkAndUpdatePath(NodeInterface nowNode, NodeInterface foundNode) {
        double socAB;
        
        socAB = nowNode.getG() + foundNode.getG();
        if (socAB < UBL) { // || (socAB == SumOfPaths && Math.max(g1,g2) < Makespan)) {
            UBL = socAB;
            
            this.currMap.setMeeting(nowNode);
            this.currMap.getOppMap().setMeeting(foundNode);
        }
        
        // check if solution is optimal guarantee
        double fminW, fminE, gminW, gminE, gg;
        fminW = Double.MAX_VALUE;
        fminE = Double.MAX_VALUE;
        gminW = Double.MAX_VALUE;
        gminE = Double.MAX_VALUE;
        for (NodeInterface n: currMap.getOpenList()) {
            if (n.getF() < fminW) fminW = n.getF();
            if (n.getG() < gminW) gminW = n.getG();
        }
        for (NodeInterface n: currMap.getOppMap().getOpenList()) {
            if (n.getF() < fminE) fminE = n.getF();
            if (n.getG() < gminE) gminE = n.getG();
        }
        gg = gminW + gminE + minWeight;
        if (  UBL <= Math.max(Math.max(currentNode.getPr(), gg), Math.max(fminW, fminE))  ) {
            completed = true;
        }
    }
    
    //                     NODE EXPANSION PROCESS
    // ====================================================================
    private void expandNode(NodeInterface now, List<NodeInterface> open) {
        NodeInterface node;
        double d,h;
        
        animThread();
        
        countExpansions(now);
        
        if (now.getIsCrossed()) currNeighbors = currMap.getNextNeighbor();
        else                    currNeighbors = currMap.getStartNeighbor();
        
        currNeighbors.initCoord(now.getCoord());
        //N = currNeighbors.getNeighborsSize();
        for (int i=0; i<N; i++){
            stepcost = currNeighbors.nextNeighbor();
            currCoord = stepcost.getCoord();
            if (canGenerateNeighbor(now, currCoord, currMap)) {
                nGenerated++;
                d = now.getG() + stepcost.getCost();
                if (currMap.isTerrain(currCoord)) {            //                --===  V A C A N T  ===--
                    
                    node = now.create(currCoord, d); // now.create also creates a new Coord object copy of currCoord
                    node.setIsCrossed(now.getIsCrossed());
                    currMap.setOpen(node);
                    node.setF(d + currMap.getHfunc().calcH(node));
                    setNodePr(node);
                    UtilityFunc.insertNodeInListPr(open, node);
                    if (currMap.getOppMap().isOpen(currCoord)) 
                        checkAndUpdatePath(node, currMap.getOppMap().getNodeOnMap(currCoord));
                    
                } else {
                    
                    node = currMap.getNodeOnMap(currCoord);
                    
                    if (currMap.isOpen(currCoord)) {           //                --===  O P E N  ===--
                        
                        if (d < node.getG()) {
                            open.remove(open.indexOf(node));
                            node.setF(node.getF() - node.getG() + d);
                            node.setG(d);
                            setNodePr(node);
                            node.setParent(now);
                            UtilityFunc.insertNodeInListPr(open, node);
                            if (currMap.getOppMap().isOpen(currCoord)) 
                                checkAndUpdatePath(node, currMap.getOppMap().getNodeOnMap(currCoord));
                        }
                        
                    }  else if (currMap.isClosed(currCoord)) { //                --===  C L O S E D  ===--
                        
                        if (d < node.getG()) {
                            currMap.setReOpen(currCoord);
                            node.setReExpanded(true);
                            node.setF(node.getF() - node.getG() + d);
                            node.setG(d);
                            setNodePr(node);
                            node.setParent(now);
                            UtilityFunc.insertNodeInListPr(open, node);
                            if (currMap.getOppMap().isOpen(currCoord)) 
                                checkAndUpdatePath(node, currMap.getOppMap().getNodeOnMap(currCoord));
                        }
                        
                    } else {                                    //                --===  B O R D E R  ===--
                        
                        h = currMap.getHfunc().calcHborder(node);
                        node = now.create(currCoord, d);
                        node.setF(d + h);
                        setNodePr(node);
                        node.setIsCrossed(true);
                        node.setIsBorder(true);
                        currMap.setOpen(node);
                        UtilityFunc.insertNodeInListPr(open, node);
                        if (currMap.getOppMap().isOpen(currCoord)) 
                            checkAndUpdatePath(node, currMap.getOppMap().getNodeOnMap(currCoord));
                        
                    }
                }
            }
        }
    } //   =========   end of expandNode function
}
