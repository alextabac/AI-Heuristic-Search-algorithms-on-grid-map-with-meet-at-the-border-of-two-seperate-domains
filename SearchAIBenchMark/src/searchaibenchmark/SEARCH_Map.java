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

import java.io.BufferedReader;
import java.util.List;
import java.util.Random;

/**
 *
 * @author atabacar
 */
//public class SEARCH_Map<CoordType extends CoordInterface,MapType extends MapObjectsInterface> {
public class SEARCH_Map {
    private SEARCH_Map oppositeMap = null;
    private MapObjectsInterface mapObj;
    private Random randomNum;
    private boolean isDualMap, is2Dmap;
    private final String mapName;    
    private int lastWestMapCellX;
    private final int MAPS_GAP = 1; // must be odd #, 1, 3 for example
    private final int cutOffMap=4; // how many rows/columns of pixels to cut off the map edge before composition
    public final byte IN_OPEN = 11;
    public final byte IN_CLOSED = 12;
    public final byte IN_BORDER = 20;
    public final byte IN_BORDER_SET = 21;
    public final byte IN_WALKABLE = 0;
    public final byte IN_TEMP_WALL = -3; // such cells are cleared with map reset
    public final byte IN_BLOCK = -2;
    public final byte IN_WALL = -1;
    public final byte IN_PATH = 13;
    private final char BLOCK = '@';
    private final char BORDER = '#';
    private final char TERRAIN = '.';
    private final char WALL = 'T';
    
    private int firstMapW;
    private int firstMapH;
    private int firstMapD;
    private int secondMapW;
    private int secondMapH;
    private int secondMapD;
    private int mapW, mapH, mapD; // for 3D maps the mapD=0
    public double borderRate = 1.0;
    private byte bTmp; // temporary border map cell value, used to be restored.
    
    SEARCH_Map(String mapName, boolean isDualMap, boolean is2DMap) { //throws Exception {
        this.mapName = mapName;
        this.isDualMap = isDualMap;
        this.is2Dmap = is2DMap;
        
        if (this.is2Dmap)   mapObj = new MapObjects2D();
        else                mapObj = new MapObjects3D();
        //this.getMapSize();
        //mapObj.setSizes(mapW, mapH, mapD);
        //mapObj.createMapObjects();
        this.randomNum = new Random();
        //runMap();
    }
    
    
    public SEARCH_Map createCopyMap() {
        this.oppositeMap = new SEARCH_Map(mapName, isDualMap,this.is2Dmap);
        this.oppositeMap.oppositeMap = this;
        this.oppositeMap.firstMapW = this.firstMapW;
        this.oppositeMap.firstMapH = this.firstMapH;
        this.oppositeMap.firstMapD = this.firstMapD;
        this.oppositeMap.secondMapW = this.secondMapW;
        this.oppositeMap.secondMapH = this.secondMapH;
        this.oppositeMap.secondMapD = this.secondMapD;
        this.oppositeMap.borderRate = this.borderRate;
        this.oppositeMap.mapW = this.mapW;
        this.oppositeMap.mapH = this.mapH;
        this.oppositeMap.mapD = this.mapD;
        this.oppositeMap.lastWestMapCellX = this.lastWestMapCellX;
        this.oppositeMap.mapObj = this.mapObj.createOppCopy();
        return oppositeMap;
    }
    
    public void addOppNodesToMap() {
        // assumes its a 2D map for graphics purposes only
        if (this.is2Dmap) {
            CoordInterface c;
            for (int i=0; i<mapW; i++)
                for (int j=0; j<mapH; j++) {
                    c = new Coord2D(i,j);
                    if (this.oppositeMap.isClosed(c)) this.setClosed(c);
                    if (this.oppositeMap.isOpen(c)) setReOpen(c);
                }
        }
    }
    
    private void nullMapObj() {if (mapObj!=null) mapObj.deleteObjectsExceptMap();}
    public void deleteMapObj() {
        nullMapObj();
        if (oppositeMap != null) oppositeMap.nullMapObj();
    }
    
    public MapObjectsInterface getMapObj() {return this.mapObj;}
    public void setOrderBord(List<NodeInterface> ordB) {mapObj.setOrderBord(ordB);}
    public List<NodeInterface> getOrderBorder() {return mapObj.getOrderBorder();}
    public int getMapWidth(){return mapW;}
    public int getMapHeight(){return mapH;}
    public int getMapDepth(){return mapD;}
    public void createOpenList() {mapObj.createOpenList();}
    public void setStartNode(NodeInterface s) {mapObj.setStartNode(s);}
    public NodeInterface getStartNode() {return mapObj.getStartNode();}
    public void setBorderNodesList(List<NodeInterface> aList) {mapObj.setBorderNodes(aList);}
    public void setMeeting(NodeInterface meeting) {this.mapObj.setMeeting(meeting);}
    public NodeInterface getMeeting() {return this.mapObj.getMeeting();}
    public void setStartNeighbor(NeighborsInterface neigh) {mapObj.setStartNeighbor(neigh);}
    public void setNextNeighbor(NeighborsInterface neigh) {mapObj.setNextNeighbor(neigh);}
    public NeighborsInterface getStartNeighbor() {return mapObj.getStartNeighbor();}
    public NeighborsInterface getNextNeighbor() {return mapObj.getNextNeighbor();}
    public SEARCH_Map getOppMap() {return oppositeMap;}
    public void setHfunc(HeuristicInterface aFunc) {this.mapObj.setHeuristic(aFunc);}
    public HeuristicInterface getHfunc() {return this.mapObj.getHeuristic();}
    public void setTerrain(CoordInterface c) {mapObj.setOnMap(c, IN_WALKABLE);}
    public int getBorderNodesSize() {return mapObj.getBorderSize();}
    public byte getMapCellVal(CoordInterface c) {return mapObj.getOnMap(c);}
    public void createNewMapFile(String addName) {mapObj.createNewMapFile(addName);}
    public List<NodeInterface> getBorderNodes() {return mapObj.getBorderNodes();}
    public void setMapCellPointer(CoordInterface c, NodeInterface n) {mapObj.setMapCellPointer(c, n);}
    public void setBorderOnMap(CoordInterface c) {bTmp=mapObj.getOnMap(c); mapObj.setOnMap(c, IN_BORDER);}
    public void unsetBorderOnMap(CoordInterface c) {mapObj.setOnMap(c, bTmp);}
    public void addBorderToList(NodeInterface n) {mapObj.addBorderToList(n);}
    public void setClosed(CoordInterface c) {mapObj.setOnMap(c, IN_CLOSED);}
    public void setCellMapBorderSet(CoordInterface c) {mapObj.setOnMap(c, IN_BORDER_SET);}
    public void setPath(CoordInterface c) {mapObj.setOnMap(c, IN_PATH);}
    public void setReOpen(CoordInterface c) {mapObj.setOnMap(c, IN_OPEN);}
    public void addNodeToOpenList(NodeInterface n) {mapObj.addNodeToOpenList(n);}
    public NodeInterface getNodeOnMap(CoordInterface c) {return mapObj.getNodeOnMap(c);}
    public NodeInterface removeFromOpen(int i) {return mapObj.removeFromOpen(i);}
    public void removeFromOpen(NodeInterface n) {mapObj.removeFromOpen(n);}
    public NodeInterface getFromOpen(int i) {return mapObj.getFromOpen(i);}
    public List<NodeInterface> getOpenList() {return mapObj.getOpenList();}
    public boolean isOpenListEmpty() {return mapObj.isOpenListEmpty();}
    public boolean isTerrain(CoordInterface c) {return mapObj.getOnMap(c) == IN_WALKABLE;}
    public boolean isOpen(CoordInterface c) {return mapObj.getOnMap(c) == IN_OPEN;}
    public boolean isClosed(CoordInterface c) {return mapObj.getOnMap(c) == IN_CLOSED;}
    public boolean isOpenOrClosed(CoordInterface c) {return mapObj.getOnMap(c)==IN_OPEN || mapObj.getOnMap(c)==IN_CLOSED;}
    public boolean isObstacle(CoordInterface c) {return (mapObj.getOnMap(c) < IN_WALKABLE);}
    public boolean isOccupied(CoordInterface c) {return (mapObj.getOnMap(c) > IN_WALKABLE);}
    public boolean isNonObstacle(CoordInterface c) {return (mapObj.getOnMap(c) >= IN_WALKABLE);}
    public boolean isCellBorder(CoordInterface c){return c.getX() == mapObj.getBorderColArray(c);}
    public boolean isCellMapBorder(CoordInterface c){return mapObj.getOnMap(c) == IN_BORDER;}
    public boolean isCellMapBorderSet(CoordInterface c) {return mapObj.getOnMap(c) == IN_BORDER_SET;}
    public boolean isCellNonBorder(CoordInterface c){return c.getX() != mapObj.getBorderColArray(c);}
    public boolean isCellNonBorderNonObstacle(CoordInterface c) {
        return (mapObj.getOnMap(c) >= IN_WALKABLE) && (c.getX() != mapObj.getBorderColArray(c));
    }
    public void setOpen(NodeInterface n) {
        mapObj.setOnMap(n.getCoord(), IN_OPEN);
        mapObj.setMapCellPointer(n.getCoord(), n);
    }
    //public boolean isCellWest(CoordInterface c){return c.getX() < mapObj.getBorderYZArray(c);}
    //public boolean isMapCellSameSide(CoordInterface s1, CoordInterface s2){return isCellWest(s1) == isCellWest(s2);}
    
    // assuming that 'nowsBorderParent' is a the parent of a border node, otherwise the return will always be false.
    public boolean coordNoUturnFromBorder(NodeInterface nowsBorderParent, CoordInterface c){
        if (isCellBorder(c)) return false;  // from border to border it is considered a U-Turn.
        return  (c.getX() < mapObj.getBorderColArray(c)) != 
                (nowsBorderParent.getCoord().getX() < mapObj.getBorderColArray(nowsBorderParent.getCoord()));
    }
    
    public void setOneBorder(CoordInterface c) {
        //NodeInterface n = new NodeBorder(c, 0, null);
        NodeInterface n = new Node(c, 0, null);
        n.setH(0);
        n.setF(0);
        n.setIsBorder(true);
        n.setIsCrossed(true);
        mapObj.setMapCellPointer(c, n);  // setMapCellPointer(c, n);
        mapObj.setOnMap(c, IN_BORDER);  // setBorderOnMap(c);
        mapObj.addBorderToList(n);  //  addBorderToList(n);
    }
    
    
    public void unsetBorderRandomly() {
        int x = randomNum.nextInt(getBorderNodesSize());
        NodeInterface n = mapObj.getMapBorderByIndex(x);
        CoordInterface c = n.getCoord();
        mapObj.unsetMapBorder(c);
        mapObj.setOnMap(c, IN_WALL);
        mapObj.removeBorderFromList(x);
    }
    
    public void solderMapsToWestNoBorder3D() {
        int x1,y1,z1,mx,my,mz;
        CoordInterface c = new Coord3D();
        
        my = Math.min(firstMapH-1, secondMapH-1);
        mz = Math.min(firstMapD-1, secondMapD-1);
        
        mx = mapW-1;
        for (z1=1; z1<mz; z1++)
            for (y1=1; y1<my; y1++){
                for (x1=firstMapW; x1>0; x1--){
                    c = new Coord3D(x1, y1, z1);
                    if (isTerrain(c)) break;
                }
                x1++;  // advances to first non-terrain cell
                if (x1>1) {
                    c = new Coord3D(x1, y1, z1);
                    mapObj.setBorderColArray(c);  // bNodesYZ[y1][z1] = x1;
                    mapObj.setOnMap(c, IN_WALL);
                    x1++; // advances to next cell, so to clear these cells into terrain                
                    c = new Coord3D(x1, y1, z1);
                    while ( x1 < mx &&  isObstacle(c) ) {
                        setTerrain(c);
                        x1++;
                        c = new Coord3D(x1, y1, z1);
                }
            }
        }
    }    
    
    
    // to be called after dual maps are defined and read
    public int solderMapsOpenToWest3D(double borderRate) {
        int x1,y1,z1,mx,my,mz, N=0;
        CoordInterface c;
        
        if (borderRate>1.0) borderRate=1.0;
        if (borderRate<0.0) borderRate=0.5;
        this.borderRate = borderRate;
                
        //for(y1=0; y1<mapH; y1++) Arrays.fill(bNodesYZ[y1], mapD); // setup virtual border for isWest calc.
        my = 1 + Math.min(firstMapH, secondMapH);
        mz = 1 + Math.min(firstMapD, secondMapD);
        
        mx = mapW-1;
        for (z1=1; z1<mz; z1++)
            for (y1=1; y1<my; y1++){
                for (x1=lastWestMapCellX; x1>0; x1--) {
                    c = new Coord3D(x1, y1, z1);
                    if (isTerrain(c)) break;
                }
                x1++;  // advances to first non-terrain cell
                if (x1>1) {
                    c = new Coord3D(x1, y1, z1);
                    mapObj.setBorderColArray(c);  // bNodesYZ[y1][z1] = x1;
                    c = new Coord3D(x1, y1, z1);
                    if (borderRate >= 1.0) {setTerrain(c); N++;}
                    else {
                        double prob = randomNum.nextDouble();
                        if (prob>borderRate) mapObj.setOnMap(c, IN_WALL); //  map[x1][y1][z1] = IN_WALL;
                        else {setTerrain(c); N++;}
                    }
                    x1++; // advances to next cell, so to clear these cells into terrain
                    c = new Coord3D(x1, y1, z1);
                    while ( x1 < mx && isObstacle(c) ) {
                        setTerrain(c);
                        x1++;
                        c = new Coord3D(x1, y1, z1);
                }
            }
        }
        return N;
    }
    
    // to be called after dual maps are defined and read
    public int solderMapsToWest3D(double borderRate) {
        int x1,y1,z1,mx,my,mz, N=0;
        CoordInterface c;
        
        if (borderRate>1.0) borderRate=1.0;
        if (borderRate<0.0) borderRate=0.5;
        this.borderRate = borderRate;
                
        //for(y1=0; y1<mapH; y1++) Arrays.fill(bNodesYZ[y1], mapD); // setup virtual border for isWest calc.
        my = 1 + Math.min(firstMapH, secondMapH);
        mz = 1 + Math.min(firstMapD, secondMapD);
        
        mx = mapW-1;
        for (z1=1; z1<mz; z1++)
            for (y1=1; y1<my; y1++){
                for (x1=lastWestMapCellX; x1>0; x1--) {
                    c = new Coord3D(x1, y1, z1);
                    if (isTerrain(c)) break;
                }
                x1++;  // advances to first non-terrain cell
                if (x1>1) {
                    c = new Coord3D(x1, y1, z1);
                    mapObj.setBorderColArray(c);  // bNodesYZ[y1][z1] = x1;
                    c = new Coord3D(x1, y1, z1);
                    if (borderRate >= 1.0) {setOneBorder(c); N++;}
                    else {
                        double prob = randomNum.nextDouble();
                        if (prob>borderRate) mapObj.setOnMap(c, IN_WALL); //  map[x1][y1][z1] = IN_WALL;
                        else {setOneBorder(c); N++;}
                    }
                    x1++; // advances to next cell, so to clear these cells into terrain
                    c = new Coord3D(x1, y1, z1);
                    while ( x1 < mx && isObstacle(c) ) {
                        setTerrain(c);
                        x1++;
                        c = new Coord3D(x1, y1, z1);
                }
            }
        }
        return N;
    }
    
    public int solderMapsToWestNoBorder2D() { //, int mapMult) {
        boolean found;
        CoordInterface c;
        int y1,x1,miny,maxy,px,py,mmy,mx, westMapEnd, N=0;
        
        westMapEnd = lastWestMapCellX; // firstMapW;
        mmy = Math.min(firstMapH+1, secondMapH+1);
        
        found = false;
        miny = 1;
        for (y1=miny; y1<mmy && !found; y1++)
            for (x1=westMapEnd; x1>0; x1--) {
                c = new Coord2D(x1, y1);
                if (isTerrain(c)) {
                    miny = y1;
                    found = true;
                    break;
                }
            }
        found = false;
        maxy = mmy;
        for (y1=mmy; y1>0 && !found; y1--)
            for (x1=westMapEnd; x1>0; x1--) {
                c = new Coord2D(x1, y1);
                if (isTerrain(c)) {
                    maxy = y1;
                    found = true;
                    break;
                }
            }
        
        // applying the soldering
        px = 0;  // previous x
        py = 0;  // previous y
        mx = mapW-1;
        for (y1=miny; y1<=maxy; y1++){
            for (x1=westMapEnd; x1>0; x1--) {
                c = new Coord2D(x1, y1);
                if (isTerrain(c)) break;
            }
            x1++;  // advances to first non-terrain cell
            if (x1>1) {
                c = new Coord2D(x1, y1);
                mapObj.setBorderColArray(c); // bNodesX[y1] = x1;
                mapObj.setOnMap(c, IN_WALL);
                if (px > 0 && py > 0){
                    if (px != x1 && py != y1){
                        int i, k0, k1;
                        k0 = Math.min(px, x1);
                        k1 = Math.max(px, x1);
                        if (px < x1) { k0++; k1++; }
                        for (i=k0; i<k1; i++) {
                            c = new Coord2D(i, py);
                            mapObj.setOnMap(c, IN_WALL); // map[i][py] = IN_WALL;
                        }
                        i--; // stands on last WALL x cordinate that was soldered
                        for (int j=py+1; j<y1; j++) {
                            c = new Coord2D(i, j);
                            mapObj.setOnMap(c, IN_WALL); // map[i][j] = IN_WALL;
                        }
                    }
                }
                px = x1;
                py = y1;
                x1++; // advances to next cell, so to clear these cells into terrain                
                c = new Coord2D(x1, y1);
                while ( x1 < mx && isObstacle(c) ) {
                    setTerrain(c);
                    x1++;
                    c = new Coord2D(x1, y1);
                }
            }
        }
        return N;
    }                       
    
    
    public int solderOpenMapsToWest2D(double openRate) { //, int mapMult) {
        boolean found;
        CoordInterface c;
        int y1,x1,miny,maxy,px,py,mmy,mx, westMapEnd, N=0;
        
        if (openRate>1.0) openRate=1.0;
        if (openRate<0.0) openRate=0.5;
        this.borderRate = openRate;
        westMapEnd = lastWestMapCellX; // firstMapW;
        mmy = Math.min(firstMapH+1, secondMapH+1);
        
        found = false;
        miny = 1;
        for (y1=miny; y1<mmy && !found; y1++)
            for (x1=westMapEnd; x1>0; x1--) {
                c = new Coord2D(x1, y1);
                if (isTerrain(c)) {
                    miny = y1;
                    found = true;
                    break;
                }
            }
        found = false;
        maxy = mmy;
        for (y1=mmy; y1>0 && !found; y1--)
            for (x1=westMapEnd; x1>0; x1--) {
                c = new Coord2D(x1, y1);
                if (isTerrain(c)) {
                    maxy = y1;
                    found = true;
                    break;
                }
            }
        
        // applying the soldering
        px = 0;  // previous x
        py = 0;  // previous y
        mx = mapW-1;
        for (y1=miny; y1<=maxy; y1++){
            for (x1=westMapEnd; x1>0; x1--) {
                c = new Coord2D(x1, y1);
                if (isTerrain(c)) break;
            }
            x1++;  // advances to first non-terrain cell
            if (x1>1) {
                c = new Coord2D(x1, y1);
                mapObj.setBorderColArray(c); // bNodesX[y1] = x1;
                if (borderRate >= 1.0) {setTerrain(c); N++;}
                else {
                    double prob = randomNum.nextDouble();
                    if (prob>borderRate) mapObj.setOnMap(c, IN_WALL); // map[x1][y1] = IN_WALL;
                    else {setTerrain(c); N++;}
                }
                if (px > 0 && py > 0){
                    if (px != x1 && py != y1){
                        int i, k0, k1;
                        k0 = Math.min(px, x1);
                        k1 = Math.max(px, x1);
                        if (px < x1) { k0++; k1++; }
                        for (i=k0; i<k1; i++) {
                            c = new Coord2D(i, py);
                            mapObj.setOnMap(c, IN_WALL); // map[i][py] = IN_WALL;
                        }
                        i--; // stands on last WALL x cordinate that was soldered
                        // for (int j=py+1; j<y1; j++) setOneBorder(i, j);
                        for (int j=py+1; j<y1; j++) {
                            c = new Coord2D(i, j);
                            mapObj.setOnMap(c, IN_WALL); // map[i][j] = IN_WALL;
                        }
                    }
                }
                px = x1;
                py = y1;
                x1++; // advances to next cell, so to clear these cells into terrain                
                c = new Coord2D(x1, y1);
                while ( x1 < mx && isObstacle(c) ) {
                    setTerrain(c);
                    x1++;
                    c = new Coord2D(x1, y1);
                }
            }
        }
        return N;
    }            
            
    public int solderMapsToWest2D(double borderRate) { //, int mapMult) {
        boolean found;
        CoordInterface c;
        int y1,x1,miny,maxy,px,py,mmy,mx, westMapEnd, N=0;
        
        if (borderRate>1.0) borderRate=1.0;
        if (borderRate<0.0) borderRate=0.5;
        this.borderRate = borderRate;
        westMapEnd = lastWestMapCellX; // firstMapW;
        mmy = Math.min(firstMapH+1, secondMapH+1);
        
        found = false;
        miny = 1;
        for (y1=miny; y1<mmy && !found; y1++)
            for (x1=westMapEnd; x1>0; x1--) {
                c = new Coord2D(x1, y1);
                if (isTerrain(c)) {
                    miny = y1;
                    found = true;
                    break;
                }
            }
        found = false;
        maxy = mmy;
        for (y1=mmy; y1>0 && !found; y1--)
            for (x1=westMapEnd; x1>0; x1--) {
                c = new Coord2D(x1, y1);
                if (isTerrain(c)) {
                    maxy = y1;
                    found = true;
                    break;
                }
            }
        
        // applying the soldering
        px = 0;  // previous x
        py = 0;  // previous y
        mx = mapW-1;
        for (y1=miny; y1<=maxy; y1++){
            for (x1=westMapEnd; x1>0; x1--) {
                c = new Coord2D(x1, y1);
                if (isTerrain(c)) break;
            }
            x1++;  // advances to first non-terrain cell
            if (x1>1) {
                c = new Coord2D(x1, y1);
                mapObj.setBorderColArray(c); // bNodesX[y1] = x1;
                if (borderRate >= 1.0) {setOneBorder(c); N++;}
                else {
                    double prob = randomNum.nextDouble();
                    if (prob>borderRate) mapObj.setOnMap(c, IN_WALL); // map[x1][y1] = IN_WALL;
                    else {setOneBorder(c); N++;}
                }
                if (px > 0 && py > 0){
                    if (px != x1 && py != y1){
                        int i, k0, k1;
                        k0 = Math.min(px, x1);
                        k1 = Math.max(px, x1);
                        if (px < x1) { k0++; k1++; }
                        for (i=k0; i<k1; i++) {
                            c = new Coord2D(i, py);
                            mapObj.setOnMap(c, IN_WALL); // map[i][py] = IN_WALL;
                        }
                        i--; // stands on last WALL x cordinate that was soldered
                        // for (int j=py+1; j<y1; j++) setOneBorder(i, j);
                        for (int j=py+1; j<y1; j++) {
                            c = new Coord2D(i, j);
                            mapObj.setOnMap(c, IN_WALL); // map[i][j] = IN_WALL;
                        }
                    }
                }
                px = x1;
                py = y1;
                x1++; // advances to next cell, so to clear these cells into terrain                
                c = new Coord2D(x1, y1);
                while ( x1 < mx && isObstacle(c) ) {
                    setTerrain(c);
                    x1++;
                    c = new Coord2D(x1, y1);
                }
            }
        }
        return N;
    }
    
    public void saveMapToFile(String fname) {
        mapObj.saveMapToFile(fname);
    }
    
    public void readMapFromFile(String fname) {
        mapObj.readMapFromFile(fname);
    }
    
    public void addRandomWalls(double percent) {
        int nx, ny, nz, dn, ds, x, y, z;
        CoordInterface c,m;
        double n;
        //dn = 4;
        
        if (this.is2Dmap) c = new Coord2D();
        else c = new Coord3D();
        
        // west map
        nx = firstMapW;
        ny = firstMapH;
        nz = firstMapD;
        if (this.is2Dmap) n = ((double)(nx*ny))*percent;
        else n = ((double)(nx*ny*nz))*percent;
        ds = 1;
        while (n>0.0) {
            n -= 1.0;
            for (int i=0; i<1000; i++){
                if (is2Dmap) {
                    c = new Coord2D(ds,1);
                    m = new Coord2D(nx,ny);
                    m.randCoords(m);
                    c.addInPlace(m);
                }
                else         {
                    c = new Coord3D(ds,1,1);
                    m = new Coord3D(nx,ny,nz);
                    m.randCoords(m);
                    c.addInPlace(m);
                }
                if (isTerrain(c)) { // map[x][y][z] == IN_WALKABLE) {
                    mapObj.setOnMap(c, IN_TEMP_WALL); // map[x][y][z] = IN_TEMP_WALL;
                    break;
                }
            }
        }
        
        // east map
        nx = secondMapW;
        ny = secondMapH;
        nz = secondMapD;
        if (this.is2Dmap) n = ((double)(nx*ny))*percent;
        else n = ((double)(nx*ny*nz))*percent;
        ds = 1 + firstMapW + 1 + MAPS_GAP + 1;
        //System.out.println("firstEastMapCellX: " + firstEastMapCellX + "; ds: " + ds);
        while (n>0.0) {
            n -= 1.0;
            for (int i=0; i<1000; i++){
                if (is2Dmap) {
                    c = new Coord2D(ds,1);
                    m = new Coord2D(nx,ny);
                    m.randCoords(m);
                    c.addInPlace(m);
                }
                else         {
                    c = new Coord3D(ds,1,1);
                    m = new Coord3D(nx,ny,nz);
                    m.randCoords(m);
                    c.addInPlace(m);
                }
                if (isTerrain(c)) { // map[x][y][z] == IN_WALKABLE) {
                    mapObj.setOnMap(c, IN_TEMP_WALL); // map[x][y][z] = IN_TEMP_WALL;
                    break;
                }
            }
        }
    }
    
    
    public CoordInterface getRandomStartGoal(String eastWest, double zonePercent) {
        CoordInterface c,m;
        boolean found=false;
        int x,y,z;
        int sw,mh,mw,md;
        
        
        c = null;
        
        if (zonePercent < 0.001 || zonePercent > 0.999) zonePercent = 0.5;
        if (!this.isDualMap || eastWest.equals("West")) {
            mw = (int)(zonePercent * (double)(firstMapW));
            sw = 1;
            mh = firstMapH;
            md = firstMapD;
        } else {
            mw = (int)(zonePercent * (double)(secondMapW));
            sw = 1 + firstMapW + MAPS_GAP + secondMapW - mw;
            mh = secondMapH;
            md = secondMapD;
//            System.out.println("==== ----   MAP EAST start-locations RAND mw = " + mw+" ;  secondMapW="+secondMapW);
//            c = new Coord2D(sw,1);
//            m = new Coord2D(mw,mh);
//            m.randCoords(m);
//            c.addInPlace(m);
//            int yy=4;
        }
        for (int i=0; i<10000; i++){
            if (is2Dmap) {
                c = new Coord2D(sw,1);
                m = new Coord2D(mw,mh);
                m.randCoords(m);
                c.addInPlace(m);
            } else {
                c = new Coord3D(sw,1,1);
                m = new Coord3D(mw,mh,md);
                m.randCoords(m);
                c.addInPlace(m);
            }
            if (isTerrain(c)) {
                //int pp=1;
                return c;
            } // if found terrain in c return it.
            else {  // else, if a neighbor corrdinate is a terrain or a null if none exists
                m = areNeighbhorsTerrain(c);
//                int xx=1;
//                int yy=1;
//                int zz=1;
                if (m != null) return m;
            }
        }
        return null; // if no terrain found in 10,000 iterations, return null
    }
    
    
    public CoordInterface areNeighbhorsTerrain(CoordInterface c) {
        StepNCostInterface stepcost;
        NeighborsInterface N;
        if (is2Dmap) N = new Neighbors2D();
        else         N = new Neighbors3D();
        int k = N.getNeighborsSize();
        N.initCoord(c);
        for (int i=0; i<k; i++){
            stepcost = N.nextNeighbor();
            if (isTerrain(stepcost.getCoord())) return stepcost.getCoord();
        }
        return null;
    }
    
    
    public void clearMapCellsResetBorders() {
        
        mapObj.createMapBorders();
        mapObj.createMapNodePointers();
        
        if (is2Dmap) {
            for (int i=0; i<mapW; i++)
                for (int j=0; j<mapH; j++) {
                    CoordInterface c = new Coord2D(i,j);
                    if (mapObj.getOnMap(c)>IN_WALKABLE) 
                        mapObj.setOnMap(c, IN_WALKABLE);
                }
        } else {
            for (int i=0; i<mapW; i++)
                for (int j=0; j<mapH; j++)
                    for (int k=0; k<mapD; k++) {
                        CoordInterface c = new Coord3D(i,j,k);
                        if (mapObj.getOnMap(c)>IN_WALKABLE) 
                            mapObj.setOnMap(c, IN_WALKABLE);
                    }
        }
        
//        if (this.oppositeMap != null) {
//            oppositeMap.mapObj.createMapBorders();
//            oppositeMap.mapObj.createMapNodePointers();
//        }
//        if (is2Dmap) {
//            if (this.oppositeMap == null) {
//                for (int i=0; i<mapW; i++)
//                    for (int j=0; j<mapH; j++) {
//                        CoordInterface c = new Coord2D(i,j);
//                        if (mapObj.getOnMap(c)>IN_WALKABLE) 
//                            mapObj.setOnMap(c, IN_WALKABLE);
//                    }
//            } else {
//                for (int i=0; i<mapW; i++)
//                    for (int j=0; j<mapH; j++) {
//                        CoordInterface c = new Coord2D(i,j);
//                        if (mapObj.getOnMap(c)>IN_WALKABLE) 
//                            mapObj.setOnMap(c, IN_WALKABLE);
//                        if (oppositeMap.mapObj.getOnMap(c)>IN_WALKABLE) 
//                            oppositeMap.mapObj.setOnMap(c, IN_WALKABLE);
//                    }
//            }
//        } else {
//            if (this.oppositeMap == null) {
//                for (int i=0; i<mapW; i++)
//                    for (int j=0; j<mapH; j++)
//                        for (int k=0; k<mapD; k++) {
//                            CoordInterface c = new Coord3D(i,j,k);
//                            if (mapObj.getOnMap(c)>IN_WALKABLE) 
//                                mapObj.setOnMap(c, IN_WALKABLE);
//                        }
//            } else {
//                for (int i=0; i<mapW; i++)
//                    for (int j=0; j<mapH; j++)
//                        for (int k=0; k<mapD; k++) {
//                            CoordInterface c = new Coord3D(i,j,k);
//                            if (mapObj.getOnMap(c)>IN_WALKABLE) 
//                                mapObj.setOnMap(c, IN_WALKABLE);
//                            if (oppositeMap.mapObj.getOnMap(c)>IN_WALKABLE) 
//                                oppositeMap.mapObj.setOnMap(c, IN_WALKABLE);
//                        }
//            }
//        }
    }

    private void readMap3D(){ // its dual map 3D
        BufferedReader br;
        
        getMapSize();
        CoordInterface c = new Coord3D(mapW,mapH,mapD);
        mapObj.setSizes(c);
        mapObj.createMapObjects(); // all arrays will have default value of 0/0.0/null
        
        if (isDualMap) {
            String[] tokens = mapName.split("[|]", 2);
            
            // read first file symbols
            // start first file map location at X=0
            br = UtilityFunc.moveBufferToMap(tokens[0], 1); // 3D maps have only one header line
            resetMapZero(1, firstMapW, firstMapH+1, firstMapD+1);
            readMapInnerLoop3D(1, br);
            mapFrameBlock(0, firstMapW+2, firstMapH+2, firstMapD+2); // set a frame around the whole map to prevent index out of bounds
            
            // read second file symbols
            // start secind file map location at X = first_file_W + gapW
            UtilityFunc.moveBufferToMap(tokens[1], 1);
            resetMapZero(2+firstMapW+MAPS_GAP+1, secondMapW, secondMapH+1, secondMapD+1);
            readMapInnerLoop3D(2+firstMapW+MAPS_GAP+1, br);
            mapFrameBlock(2+firstMapW+MAPS_GAP, secondMapW+2, secondMapH+2, secondMapD+2); // set a frame around the whole map to prevent index out of bounds
            
            
            // add the wall in the gap between the two maps
            int ww,wh,wd;
            ww = 2+firstMapW+MAPS_GAP;
            wh = mapH; //2+Math.max(firstMapH, secondMapH);
            wd = mapD; //2+Math.max(firstMapD, secondMapD);
            for (int i=(firstMapW+2); i<ww; i++)
                for (int j=0; j<wh; j++)
                    for (int k=0; k<wd; k++)
                        mapObj.setOnMap(new Coord3D(i,j,k), IN_WALL);
        }
        else {
            br = UtilityFunc.moveBufferToMap(mapName, 1);
            resetMapZero(1, firstMapW, firstMapH+1, firstMapD+1);
            readMapInnerLoop3D(1, br);
            mapFrameBlock(0, firstMapW+2, firstMapH+2, firstMapD+2);
        }
    }

    private void readMultiMap2D(int rows, int cols){
        BufferedReader br;
        int ctoff;
        
        if (isDualMap) {
            this.getMapSize();
            if (rows == 1 && cols == 1) ctoff = 0;
            else {
                ctoff = cutOffMap;
                firstMapW = firstMapW - 2 * ctoff;
                firstMapH = firstMapH - 2 * ctoff;
                secondMapW = secondMapW - 2 * ctoff;
                secondMapH = secondMapH - 2 * ctoff;
            }
            mapW = firstMapW*cols + 2 + MAPS_GAP + secondMapW*cols + 2;
            mapH = Math.max(firstMapH, secondMapH)*rows + 2;
            mapObj.setSizes(new Coord2D(mapW, mapH));
            mapObj.createMapObjects();
            mapObj.fillMapBlocks(IN_BLOCK);
            String[] tokens = mapName.split("[|]", 2);

            // Left side of map composition, all with first map
            for (int x=0; x<cols; x++)
                for (int y=0;y<rows;y++) {
                    br = UtilityFunc.moveBufferToMap(tokens[0], 4);
                    readMapInnerLoop2D(ctoff, 1+x*firstMapW, firstMapW+ctoff, 
                            ctoff, 1+y*firstMapH, firstMapH+ctoff, br);
                }
            // Right side of map composition, all with second map
            firstMapW = cols * firstMapW;
            firstMapH = rows * firstMapH;
            mapFrameBlock(0, firstMapW+2, firstMapH+2, 0);

            int sx = 1 + firstMapW + 1 + MAPS_GAP + 1; // the last plus one is for the second map frame
            for (int x=0; x<cols; x++)
                for (int y=0;y<rows;y++) {
                    br = UtilityFunc.moveBufferToMap(tokens[1], 4);
                    readMapInnerLoop2D(ctoff, sx + x*secondMapW, secondMapW+ctoff, 
                            ctoff, 1+y*secondMapH, secondMapH+ctoff, br);
                }
            secondMapW = cols * secondMapW;
            secondMapH = rows * secondMapH;
            lastWestMapCellX = 1 + firstMapW + (int)(MAPS_GAP/2);
            mapFrameBlock(2+firstMapW+MAPS_GAP, secondMapW+2, secondMapH+2, 0);

            // add the wall in the gap between the two maps
            int wd,wh;
            wd = 2+firstMapW+MAPS_GAP;
            wh = 2+Math.min(firstMapH, secondMapH);
            for (int i=firstMapW; i<wd; i++)
                for (int j=0; j<wh; j++)
                    mapObj.setOnMap(new Coord2D(i, j), IN_WALL);

            System.out.println( "Composition maps 2D: rows:cols="+rows+":"+cols+"\nsize: width=" + Integer.toString(mapW) + 
                                ", height=" + Integer.toString(mapH));
        } else {
            this.getMapSize();
            if (rows == 1 && cols == 1) ctoff = 0;
            else {
                ctoff = cutOffMap;
                firstMapW = firstMapW - 2 * ctoff;
                firstMapH = firstMapH - 2 * ctoff;
            }
            mapW = firstMapW*cols + 2;
            mapH = firstMapH*rows + 2;
            
            mapObj.setSizes(new Coord2D(mapW, mapH));
            mapObj.createMapObjects();  // all arrays will have default value of 0/0.0/null
            br = UtilityFunc.moveBufferToMap(mapName, 4);
            readMapInnerLoop2D(0, 1, firstMapW, 0, 1, firstMapH, br);
            mapFrameBlock(0, mapW, mapH, 0);
            System.out.println( "Single map 2D of size: width=" + Integer.toString(mapW) + 
                                ", height=" + Integer.toString(mapH));
            firstMapW = mapW - 2;
            firstMapH = mapH - 2;
        }
    }
    
//    public byte[][] getMapObj2() {
//        return mapObj.getMapObj();
//    }
    public final void runMap(int rows, int cols){
        if (this.is2Dmap) readMultiMap2D(rows, cols); // for 2D maps, either single or composing maps
        else              readMap3D();
    }
    
    private byte getCellValueByChar(char c){
        if (c == BORDER) return IN_BORDER;
        if (c == BLOCK) return IN_BLOCK;
        if (c == WALL) return IN_WALL;
        return IN_WALKABLE;
    }
    
    private void readMapInnerLoop2D(int widthFileStart, int widthMapStart, int widthFileEnd, 
                                    int heightFileStart, int heightMapStart, int heightFileEnd, BufferedReader br) {
        String line;
        int j;
        
        // increments file read to desired line
        for (j=0; j<heightFileEnd && j<heightFileStart; j++) UtilityFunc.readLine(br);
        while ((line = UtilityFunc.readLine(br)) != null && j < heightFileEnd){
            for (int i=widthFileStart; i<widthFileEnd; i++){
                CoordInterface c = new Coord2D(i-widthFileStart+widthMapStart, j-heightFileStart+heightMapStart);
                mapObj.setOnMap(c, getCellValueByChar(line.charAt(i)));
            }
            j++;
        }
    }
    
    private void readMapInnerLoop3D(int widthStart, BufferedReader br) {
        String line;
        String[] coord;
        int i,j,k;
        
        while ((line = UtilityFunc.readLine(br)) != null){
            coord = line.split("\s");
            i = Integer.parseInt(coord[0]);
            j = Integer.parseInt(coord[1]);
            k = Integer.parseInt(coord[2]);
            mapObj.setOnMap(new Coord3D(i+widthStart, j+1, k+1), IN_WALL);
        }
    }
    
    private void resetMapZero(int widthStart, int width, int height, int depth) {
        CoordInterface c;
        width += widthStart;
        if (this.is2Dmap){
            for (int i=widthStart; i<width; i++)
                for (int j=1; j<height; j++) {
                        c = new Coord2D(i, j);
                        mapObj.setOnMap(c, IN_WALKABLE);
                        setMapCellPointer(c, null);
                    }
        } else {
            for (int i=widthStart; i<width; i++)
                for (int j=1; j<height; j++)
                    for (int k=1; k<depth; k++) {
                        c = new Coord3D(i, j, k);
                        mapObj.setOnMap(c, IN_WALKABLE);
                        setMapCellPointer(c, null);
                    }
        }
    }
    
    private void mapFrameBlock(int widthStart, int width, int height, int depth) {
        width += widthStart;
        if (this.is2Dmap) {
            for (int i=widthStart; i<width; i++) {
                mapObj.setOnMap(new Coord2D(i, 0), IN_WALL);
                mapObj.setOnMap(new Coord2D(i, height-1), IN_WALL);
            }
            for (int j=0; j<height; j++) {
                mapObj.setOnMap(new Coord2D(widthStart, j), IN_WALL);
                mapObj.setOnMap(new Coord2D(width-1, j), IN_WALL);
            }
        } else {
            //System.out.println(String.format("wstart: %d ,  w-1: %d ,  h-1: %d ,  d-1: %d", widthStart, width-1, height-1, depth-1));
            for (int i=widthStart; i<width; i++) {
                for (int j=0; j<height; j++) {
                    mapObj.setOnMap(new Coord3D(i, j, 0), IN_WALL);
                    mapObj.setOnMap(new Coord3D(i, j, depth-1), IN_WALL);
                }
            }
            for (int i=widthStart; i<width; i++) {
                for (int k=0; k<depth; k++) {
                    mapObj.setOnMap(new Coord3D(i, 0, k), IN_WALL);
                    mapObj.setOnMap(new Coord3D(i, height-1, k), IN_WALL);
                }
            }
            for (int j=0; j<height; j++) {
                for (int k=0; k<depth; k++) {
                    mapObj.setOnMap(new Coord3D(widthStart, j, k), IN_WALL);
                    mapObj.setOnMap(new Coord3D(width-1, j, k), IN_WALL);
                }
            }
        }
    }
    
    private void getMapSize(){
        BufferedReader br;
                
        if (this.isDualMap) {
            String[] tokens = this.mapName.split("[|]", 2);
            
            // first map file
            br = UtilityFunc.resetMapFile(tokens[0]);
            getMapSizeInnerLoop(br);
            firstMapW = mapW;
            firstMapH = mapH;
            firstMapD = mapD;
            
            // second map file
            br = UtilityFunc.resetMapFile(tokens[1]);
            getMapSizeInnerLoop(br);
            secondMapW = mapW;
            secondMapH = mapH;
            secondMapD = mapD;
            
            // +2 in order to apply frame
            mapW = 1 + firstMapW + 1 + MAPS_GAP + 1 + secondMapW + 1;
            mapH = 1 + Math.max(firstMapH, secondMapH) + 1;
            if (Math.max(firstMapD, secondMapD) == 0) mapD = 0;
            else mapD = 1 + Math.max(firstMapD, secondMapD) + 1;

            lastWestMapCellX = 1 + firstMapW + (int)(MAPS_GAP/2);
            System.out.println( "Dual 2D/3D Map size: width=" + Integer.toString(mapW) + 
                                ", height=" + Integer.toString(mapH)+
                                ", depth=" + Integer.toString(mapD));
        }
        else {
            br = UtilityFunc.resetMapFile(mapName);
            getMapSizeInnerLoop(br);
            lastWestMapCellX = mapW;
            firstMapW = mapW;
            firstMapH = mapH;
            firstMapD = mapD;
            mapW = 1+mapW+1; // padding for frame
            mapH = 1+mapH+1;
            mapD = 1+mapD+1;
            System.out.println( "Single 2D/3D Map size: width=" + Integer.toString(mapW) + 
                                ", height=" + Integer.toString(mapH)+
                                ", depth=" + Integer.toString(mapD));
        }
    }
    
    private void getMapSizeInnerLoop(BufferedReader br) {
        String line;
        String[] headerStrings;
        
        if (this.is2Dmap) {
            UtilityFunc.readLine(br);
            line = UtilityFunc.readLine(br);
            headerStrings = line.split("\s");
            this.mapH = Integer.parseInt(headerStrings[1]);
            line = UtilityFunc.readLine(br);
            headerStrings = line.split("\s");
            this.mapW = Integer.parseInt(headerStrings[1]);
            this.mapD = 0;
        }
        else {
            line = UtilityFunc.readLine(br);
            headerStrings = line.trim().split("\s", 2)[1].split("\s");
            this.mapW = Integer.parseInt(headerStrings[0]);
            this.mapH = Integer.parseInt(headerStrings[1]);
            this.mapD = Integer.parseInt(headerStrings[2]);
        }
    }
}
