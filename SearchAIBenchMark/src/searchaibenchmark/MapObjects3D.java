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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author atabacar
 */
public class MapObjects3D implements MapObjectsInterface{
    private int mapW;
    private int mapH;
    private int mapD;
    public List<NodeInterface> mapBorders, ordeBorder;
    public int bNodesYZ[][]; // y-z plane for border passed checking
    public byte map[][][];
    public NodeInterface mapNodePointers[][][];
    private List<NodeInterface> openL;
    private HeuristicInterface hFunc;
    private NeighborsInterface startSideNeigh, nextSideNeigh;
    private NodeInterface meeting, start;
    
    MapObjects3D() {}

    @Override public void deleteObjectsExceptMap() {
        hFunc = null;
        openL = null;
        mapNodePointers = null;
        mapBorders = null;
    }
    
    @Override public void setSizes(CoordInterface sizes) {
        mapW=sizes.getX();
        mapH=sizes.getY();
        mapD=sizes.getZ();
    }
    
    @Override
    public void createMapObjects() {
        mapBorders =      new ArrayList<>();
        openL =           new ArrayList<>();
        bNodesYZ =        new int[mapH][mapD];
        map     =         new byte[mapW][mapH][mapD];
        mapNodePointers = new NodeInterface[mapW][mapH][mapD];
    }
    
    @Override public void createMapForClone() {map = new byte[mapW][mapH][];}
    
    //  creators
    @Override public void createOpenList() {openL = new ArrayList<>();}
    @Override public void createMapBorders() { mapBorders = new ArrayList<>(); }
    @Override public void createMapNodePointers() {mapNodePointers = new NodeInterface[mapW][mapH][mapD];}
    
    @Override public MapObjectsInterface createOppCopy() {
        MapObjectsInterface oppO = new MapObjects3D();
        oppO.setSizes(new Coord3D(mapW, mapH, mapD));
        oppO.createOpenList();
        
        oppO.createMapBorders();
        oppO.createMapNodePointers();
        for (NodeInterface b: mapBorders) {
            NodeInterface bb = b.create();
            bb.setIsBorder(true);
            bb.setIsCrossed(true);
            oppO.addBorderToList(bb);
            oppO.setMapCellPointer(bb.getCoord(), bb);
        }
        oppO.setStartNeighbor(nextSideNeigh);
        oppO.setNextNeighbor(startSideNeigh);
        oppO.setColArrayArr3D(this.bNodesYZ);
        oppO.createMapForClone();
        for (int i=0; i<mapW; i++)
            for (int j=0; j<mapH; j++){
                oppO.setMapArr(i, j, this.map[i][j].clone());
                oppO.setMapNodeArr(i, j, this.mapNodePointers[i][j].clone());
            }
        oppO.setHeuristic(hFunc.createTheOppositeObj());        
        return oppO;
    }
    
    //  setters
    @Override public void setOrderBord(List<NodeInterface> ordB) {this.ordeBorder = ordB;}
    @Override public void setStartNode(NodeInterface startN) {this.start = startN;}
    @Override public void setHeuristic(HeuristicInterface hObj) {this.hFunc=hObj;}
    @Override public void setStartNeighbor(NeighborsInterface neigh) {this.startSideNeigh=neigh;}
    @Override public void setNextNeighbor(NeighborsInterface neigh) {this.nextSideNeigh=neigh;}
    @Override public NodeInterface getMeeting() {return this.meeting;}
    @Override public void setMeeting(NodeInterface meeting) {this.meeting = meeting;}
    @Override public void setOnMap(CoordInterface c, byte a) {map[c.getX()][c.getY()][c.getZ()]=a;}
    
    @Override public void unsetMapBorder(CoordInterface c) {mapNodePointers[c.getX()][c.getY()][c.getZ()] = null;}
    @Override public void setMapCellPointer(CoordInterface c, NodeInterface n) {mapNodePointers[c.getX()][c.getY()][c.getZ()] = n;}
    @Override public void setBorderColArray(CoordInterface c) {bNodesYZ[c.getY()][c.getZ()]=c.getX();}
    @Override public void setBorderNodes(List<NodeInterface> bL) {mapBorders = bL;}
    @Override public void setColArrayArr3D(int[][] a) {this.bNodesYZ = a;}
    @Override public void setMapArr(int i, int j, byte[] a) { this.map[i][j] = a;}
    @Override public void setMapNodeArr(int i, int j, NodeInterface[] a) {this.mapNodePointers[i][j] = a;}
    @Override public void addNodeToOpenList(NodeInterface n) {openL.add(n);}
    @Override public void setColArrayArr2D(int[] a) {
        throw new java.lang.RuntimeException("*** Calling setColArrayArr2D() for a MapObjects3D instance, suspected invalid usage.");
    }
    
    //  getters
    @Override public List<NodeInterface> getOrderBorder() {return this.ordeBorder;}
    @Override public NodeInterface getStartNode() {return start;}
    @Override public HeuristicInterface getHeuristic() {return this.hFunc;}
    @Override public NeighborsInterface getStartNeighbor() {return startSideNeigh;}
    @Override public NeighborsInterface getNextNeighbor() {return nextSideNeigh;}
    @Override public int getBorderSize(){ return mapBorders.size(); }
    @Override public NodeInterface getNodeOnMap(CoordInterface c) {return mapNodePointers[c.getX()][c.getY()][c.getZ()];}
    @Override public byte getOnMap(CoordInterface c) {return map[c.getX()][c.getY()][c.getZ()];}
    @Override public List<NodeInterface> getBorderNodes() {return mapBorders;}
    @Override public NodeInterface getMapBorderByIndex(int i) {return mapBorders.get(i);}
    @Override public int getBorderColArray(CoordInterface c) { return bNodesYZ[c.getY()][c.getZ()];}
    @Override public NodeInterface removeFromOpen(int i) {return openL.remove(i);}
    @Override public void removeFromOpen(NodeInterface n) {openL.remove(n);}
    @Override public NodeInterface getFromOpen(int i) {return openL.get(i);}
    @Override public boolean isOpenListEmpty() {return openL.isEmpty();}
    
    //  operators
    @Override public void addBorderToList(NodeInterface n) { mapBorders.add(n); }
    @Override public void removeBorderFromList(int i) {mapBorders.remove(i);}
    @Override public void insertNodeToOpenF(NodeInterface n) {UtilityFunc.insertInListF(openL, n);}
    @Override public void insertNodeToOpenG(NodeInterface n) {UtilityFunc.insertNodeInListG(openL, n);}
    @Override public void insertNodeToOpenPr(NodeInterface n) {UtilityFunc.insertNodeInListPr(openL, n);}
    @Override public List<NodeInterface> getOpenList() {return openL;}

    @Override
    public void fillMapBlocks(byte val) {
        for (int i=0; i<mapW; i++)
            for (int j=0; j<mapH; j++)
            Arrays.fill(map[i][j], val);
    }
    
    @Override
    public void saveMapToFile(String fname) {
        ObjectOutputStream outputStream;
        try {
            outputStream = new ObjectOutputStream(new FileOutputStream(fname));
            outputStream.writeObject(map);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MapObjects3D.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MapObjects3D.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public void readMapFromFile(String fname) {
        ObjectInputStream inputStream;
        try {
            inputStream = new ObjectInputStream(new FileInputStream(fname));
            map = (byte[][][])inputStream.readObject();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MapObjects3D.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MapObjects3D.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(MapObjects3D.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public void createNewMapFile(String addName) {
        FileWriter myWriter;
        String fname = "../Maps/".concat("Random3D").concat(addName).concat(".3dmap");
        String line;
        
        //first line is header
        line = "voxel ".concat(String.format("%d", mapW));
        line = line.concat(String.format("%d", mapH));
        line = line.concat(String.format("%d\n", mapD));
        try {
            myWriter = new FileWriter(fname, false);
            myWriter.write(line);
            for (int i=0; i<mapW; i++)
                for (int j=0; j<mapH; j++)
                    for (int k=0; k<mapD; k++) {
                        if (map[i][j][k] < 0) {  // below walkable, meaning obstacle
                            line = String.format("%d", mapD).concat(" ");
                            line = line.concat(String.format("%d", mapH)).concat(" ");
                            line = line.concat(String.format("%d\n", mapW));
                            myWriter.write(line);
                        }
                    }
            myWriter.close();
        } catch (IOException ex) {
            Logger.getLogger(MapObjects3D.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
}
