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

public interface MapObjectsInterface {
    
    //  creators
    void                   createMapObjects();
    MapObjectsInterface       createOppCopy();
    void                   createMapBorders();
    void              createMapNodePointers();
    void                     createOpenList();
    void                  createMapForClone();
    void             deleteObjectsExceptMap();
    
    //  setters
    void         setOrderBord(List<NodeInterface> ordB);
    void         setStartNode(NodeInterface startN);
    void       setBorderNodes(List<NodeInterface> bL);
    void     setStartNeighbor(NeighborsInterface neigh);
    void      setNextNeighbor(NeighborsInterface neigh);
    void         setHeuristic(HeuristicInterface hObj);
    void           setMeeting(NodeInterface meeting);
    void             setOnMap(CoordInterface c, byte a);
    void             setSizes(CoordInterface sizes);
    void    setMapCellPointer(CoordInterface c, NodeInterface n);
    void       unsetMapBorder(CoordInterface c);
    void    setBorderColArray(CoordInterface c);
    void     setColArrayArr2D(int[] a);
    void     setColArrayArr3D(int[][] a);
    void            setMapArr(int i, int j, byte[] a);
    void        setMapNodeArr(int i, int j, NodeInterface[] a);

    //  getters
    List<NodeInterface>   getOrderBorder();
    NodeInterface         getStartNode();
    HeuristicInterface    getHeuristic();
    NeighborsInterface    getStartNeighbor();
    NeighborsInterface    getNextNeighbor();
    NodeInterface         getMeeting();
    NodeInterface         getNodeOnMap(CoordInterface c);
    NodeInterface         removeFromOpen(int i);
    void                  removeFromOpen(NodeInterface n);
    NodeInterface         getFromOpen(int i);
    NodeInterface         getMapBorderByIndex(int i);
    List<NodeInterface>   getBorderNodes();
    List<NodeInterface>   getOpenList();
    boolean               isOpenListEmpty();
    byte                  getOnMap(CoordInterface c);
    int                   getBorderSize();
    int                   getBorderColArray(CoordInterface c);
    
    //  operators
    void addBorderToList(NodeInterface n);
    void addNodeToOpenList(NodeInterface n);
    void insertNodeToOpenF(NodeInterface n);
    void insertNodeToOpenG(NodeInterface n);
    void insertNodeToOpenPr(NodeInterface n);
    void removeBorderFromList(int i);
    void fillMapBlocks(byte val);
    void saveMapToFile(String fname);
    void readMapFromFile(String fname);
    void createNewMapFile(String addName);
}
