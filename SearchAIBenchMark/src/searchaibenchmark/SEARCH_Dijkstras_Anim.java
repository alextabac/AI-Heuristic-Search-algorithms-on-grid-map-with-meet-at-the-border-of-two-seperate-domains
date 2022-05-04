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
public class SEARCH_Dijkstras_Anim extends SEARCH_BASE implements Runnable {
    private NeighborsInterface currNeighbors;
    private NodeInterface currentNode;
    private StepNCostInterface stepcost;
    private CoordInterface currCoord;
    private NodeInterface startN;
    private int N; // amount of neighbor cells

    SEARCH_Dijkstras_Anim(boolean is2D, HeuristicInterface hFunc, String mName, SEARCH_Map aMap, 
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

    
    //                     INIT open list, start node, etc.
    // ====================================================================
    private void preInitializeFind(CoordInterface startF) {
        stepcost = new StepCost(startF);
        currCoord = startF.create();
        path = null;
        if (weightF < weightB) swapSearchDirections();
        N = this.neighborsF.getNeighborsSize();
    }
    
    private void initializeFind() {
        NodeInterface dummy = new Node(startCoordF, 0, null);
        startN = new Node(startCoordF, 0, dummy); // adding dummy for first node, to simplify
        map.addNodeToOpenList(startN);
        map.setOpen(startN);
    }

    
    
    //                     MAIN LOOP FOR NODE SELECTION
    // ====================================================================
    @Override public List<NodeInterface> findPath(CoordInterface startF, CoordInterface startB) {
        if (!initialCheckBeforeSearch(startF, startB)) return null; // check if valid
        preInitializeFind(startF);

        tStart = System.nanoTime();   //  record start time for search run-time
        
        initializeFind();
        
        // ======  Loop to extract next node from open list
        while (!map.isOpenListEmpty()) {
            if (checkTimeOut()) return null;
            currentNode = map.removeFromOpen(0);
            map.setClosed(currentNode.getCoord());
            if (currentNode.getCoord().equalCoord(this.startCoordB)) {
                foundPathUni(currentNode, startN);
                return path;
            }
            expandNode(currentNode, map.getOpenList());
        }
        resetParametersEnd(STATE_NOT_FOUND_PATH);
        return null;
    }

    
    
    //                     NODE EXPANSION PROCESS
    // ====================================================================
    private void expandNode(NodeInterface now, List<NodeInterface> open) {
        NodeInterface node;
        double d;
        
        animThread();
        
        countExpansions(now);
        
        if (now.getIsCrossed()) currNeighbors = this.neighborsB;
        else currNeighbors = this.neighborsF;
        
        currNeighbors.initCoord(now.getCoord());
        //N = currNeighbors.getNeighborsSize();
        for (int i=0; i<N; i++){
            stepcost = currNeighbors.nextNeighbor();
            currCoord = stepcost.getCoord();
            if (canGenerateNeighborNotClosed(now, currCoord, map)) {
                nGenerated++;
                d = now.getG() + stepcost.getCost();
                if (map.isTerrain(currCoord)) {                 // --===  V A C A N T  ===--
                    
                    node = now.create(currCoord, d); // now.create also creates a new Coord object copy of currCoord
                    map.setOpen(node);
                    node.setIsCrossed(now.getIsCrossed());
                    UtilityFunc.insertNodeInListG(open, node);
                    
                } else {
                    
                    node = map.getNodeOnMap(currCoord);
                    
                    if (map.isOpen(currCoord)) {               // --===  O P E N  ===--
                        
                        if (d < node.getG()) {
                            open.remove(open.indexOf(node));
                            node.setG(d);
                            node.setParent(now);
                            UtilityFunc.insertNodeInListG(open, node);
                            
                        }
                        
                    } else {                                     // --===  B O R D E R  ===--
                        
                        node = now.create(currCoord, d);
                        map.setOpen(node);
                        node.setParent(now);
                        node.setIsCrossed(true);
                        node.setIsBorder(true);
                        UtilityFunc.insertNodeInListG(open, node);
                        
                    }
                }
            }
        }
    } //  ==========   end of expandNode function
}
