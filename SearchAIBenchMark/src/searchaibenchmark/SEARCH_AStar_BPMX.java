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
public class SEARCH_AStar_BPMX extends SEARCH_BASE {
    private NeighborsInterface currNeighbors;
    private NodeInterface currentNode;
    private StepNCostInterface stepcost;
    private CoordInterface currCoord;
    private NodeInterface startN;
    //private List<NodeInterface> openL;
    private int N; // amount of neighbor cells
    
    SEARCH_AStar_BPMX(boolean is2D, HeuristicInterface hFunc, String mName, SEARCH_Map aMap, 
                 String heuristicName, double heuristicWeightF, double heuristicWeightB) 
            {super(is2D, hFunc, mName, aMap, heuristicName, heuristicWeightF, heuristicWeightB);}
    
    
    
    //                  PRE-INIT & INIT functions
    // ====================================================================
    private void preInitializeFind(CoordInterface startF) {
        stepcost = new StepCost(startF);
        currCoord = startF.create();
        path = null;
        if (weightF < weightB) swapSearchDirections();
        
        map.createCopyMap();
        
        N = this.neighborsF.getNeighborsSize();
    }
    
    private void initializeFind() {
        this.hFuncF.additionalSetup(false);

        startN = hFuncF.getStartN();
        startN.setH(hFuncF.calcH(startN));
        startN.setF(startN.getH());
        map.setOpen(startN);
        map.addNodeToOpenList(startN);
        
        //System.out.println("Starting search:  " + this.getClass().getName().split("[.]")[1].split("[_]")[1] + 
        //        " + " + hFuncF.getClass().getName().split("[.]")[1].split("[_]")[1]);
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

    
    private double BPMXRule3(NodeInterface now, NeighborsInterface currNeighbors) {
        NodeInterface node;
        double bestH, h;
        
        bestH = 0;
        currNeighbors.initCoord(now.getCoord());
        for (int i=0; i<N; i++){
            stepcost = currNeighbors.nextNeighbor();
            currCoord = stepcost.getCoord();
            if (canGenerateNeighbor(now, currCoord, map)) {
                node = map.getNodeOnMap(currCoord);
                if (map.isCellMapBorder(currCoord)) {
                    //node.setG(now.getG() + stepcost.getCost());
                    node = now.create(currCoord, now.getG() + stepcost.getCost());
                    node.setIsCrossed(true);
                    node.setIsBorder(true);
                    node.setH(hFuncF.calcHborder(node));
                    node.setF(node.getG() + node.getH());
                    map.setMapCellPointer(currCoord, node);
                    //node.setParent(now);
                    map.setCellMapBorderSet(currCoord);
                } else if (node == null) {
                    node = now.create(currCoord, now.getG() + stepcost.getCost());
                    node.setIsCrossed(now.getIsCrossed());
                    node.setH(hFuncF.calcH(node));
                    node.setF(node.getG() + node.getH());
                    map.setMapCellPointer(currCoord, node);
                }
                // Pathmax Rule 3 - the parent
                h = node.getH() - stepcost.getCost();
                if (h > bestH) bestH = h;
            }
        } //  end for loop
        //bestH = Math.max(bestH, now.getH());  // its obvious that bestH started as 0 and now max.
        if (bestH > now.getH()) {
            now.setH(bestH);
            now.setF(now.getG() + bestH);
        }
        return bestH;
    }
    
    //                     NODE EXPANSION PROCESS
    // ====================================================================
    private void expandNode(NodeInterface now, List<NodeInterface> open) {
        NodeInterface node;
        double d, bestH, hci;
        boolean reInsert;
        
        countExpansions(now);
        
        if (now.getIsCrossed()) currNeighbors = this.neighborsB;
        else                    currNeighbors = this.neighborsF;
        
        bestH = BPMXRule3(now, currNeighbors); // also generates the vacant coordinates - no more vacants neighbors
        
        currNeighbors.initCoord(now.getCoord());
        //N = currNeighbors.getNeighborsSize();
        for (int i=0; i<N; i++){
            stepcost = currNeighbors.nextNeighbor();
            currCoord = stepcost.getCoord();
            if (canGenerateNeighbor(now, currCoord, map)) {
                nGenerated++;
                d = now.getG() + stepcost.getCost();
                node = map.getNodeOnMap(currCoord); // all neighbors have a defined node in pointers map already (BMPXRule3)
                hci = bestH - stepcost.getCost();
                
                if (map.isTerrain(currCoord)) {                   // --===  V A C A N T  ===--
                    
                        //int k = open.indexOf(node);
                        //if (k>=0) System.out.println(" *** UNEXPECTED VACANT NODE IN OPEN , node: "+node.toString());
                        if (hci > node.getH()) {
                            node.setH(hci);
                            node.setF(d+hci);  // at this step, d=node.getG()
                        }
                        map.setReOpen(currCoord);
                        UtilityFunc.insertInListF(open, node);

                }  else if (map.isClosed(currCoord)) {            // --===  C L O S E D  ===--
                    
                        //int k = open.indexOf(node);
                        //if (k>=0) System.out.println(" *** UNEXPECTED CLOSED NODE IN OPEN, node: "+node.toString());
                    
                        if (hci > node.getH()) {
                            node.setH(hci);
                            node.setF(d+hci);
                        }
                        if (d < node.getG()) {
                            node.setReExpanded(true);
                            node.setG(d);
                            node.setF(d + node.getH());
                            node.setParent(now);
                            map.setReOpen(currCoord);
                            UtilityFunc.insertInListF(open, node);
                        }
                        
                } else if (map.isOpen(currCoord)) {              // --===  O P E N  ===--
                    
                        //int k = open.indexOf(node);
                        //if (k<0) System.out.println(" *** UNEXPECTED OPEN NODE NOT NOT NOT IN OPEN, node: "+node.toString());
                        reInsert = false;
                        if (d < node.getG()) {
                            open.remove(open.indexOf(node));
                            node.setG(d);
                            node.setF(d + node.getH());
                            node.setParent(now);
                            reInsert = true;
                            //UtilityFunc.insertInListF(open, node);
                        }
                        
                        if (hci > node.getH()) {
                            if (!reInsert) open.remove(open.indexOf(node));
                            node.setH(hci);
                            node.setF(node.getG()+hci);
                            UtilityFunc.insertInListF(open, node);
                        } else if (reInsert) UtilityFunc.insertInListF(open, node);
                    
                } else {                                          // --===  B O R D E R  S E T ===--
                        //if (!map.isCellMapBorderSet(currCoord))
                        //    System.out.println(" ***  UNEXPECTED NON BORDER SET !  byte="+map.getMapCellVal(currCoord)+
                        //            " ;  node: "+node.toString());
                        //int k = open.indexOf(node);
                        //if (k>=0) System.out.println(" *** UNEXPECTED BORDER_SET NODE IN OPEN, node: "+node.toString());
                        
                        if (hci > node.getH()) {
                            node.setH(hci);
                            node.setF(d+hci); // at this point the d=node.getG()
                        }
                        map.setReOpen(node.getCoord());
                        UtilityFunc.insertInListF(open, node);
                        
                }
            }
        }
    } //   =========   end of expandNode function
}
