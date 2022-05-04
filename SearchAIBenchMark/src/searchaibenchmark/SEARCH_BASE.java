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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *
 * @author atabacar
 * 
 */
public class SEARCH_BASE {
    protected MarathonGUI guiMarathon = null;
    protected AnimGui gui = null;
    protected DrawMatrix matPanel = null;

    protected SEARCH_Map map = null;
    protected MetricInterface metricF, metricB;
    protected HeuristicInterface hFuncF, hFuncB;
    protected NeighborsInterface neighborsF, neighborsB;
    protected List<NodeInterface> path;
    
    protected long tStart, time;
    private final long tLimit_ACCELERATE = 10; // starting point for acceleration animation.
    protected final String fname2DSingle = "single search.csv";
    protected final String fname2DMarathon = "marathon search.csv";
    protected final String fname2DMarathonRe = "2D map re-marathon search.csv";
    protected final String fname2DfMMmarathon = "2D map fMM-p marathon search.csv";
    protected final String fname3DSingle = "single search.csv";
    protected final String fname3DMarathon = "marathon search.csv";
    protected final String fname3DMarathonRe = "3D map re-marathon search.csv";
    protected final String fname3DfMMmarathon = "3D map fMM-p marathon search.csv";
    protected final String STATE_FOUND_PATH = "Found";
    protected final String STATE_TIMEOUT_PATH = "Exceeded Time";
    protected final String STATE_NOT_FOUND_PATH = "Not Found";
    protected final String STATE_INVALID_STARTS = "Invalid Map/Starts";
    protected String statsFname = fname2DSingle;
    protected String metricName; // Manhattan, Euclidean, discreet diagonal K3.
    protected String mapsName;
    protected double weightF;
    protected double weightB;
    protected double minWeight;
    protected double SumOfPaths;
    protected double Makespan;
    protected double runTime;
    protected final int countCheckTimeout = 20000; // check time lapse every 20000 expansions
    protected final int timeoutSeconds = 300;
    protected int timek = 0; // checks time elapsed every 10,000 steps, and breake the search if above 5min        
    public long nExpanded;
    public long nReExpanded;
    public long nGenerated;
    private long tCount;
    private long tLimit;
    private long tSize;
    private long tSCount;
    protected int BN=0;  // border N size
    protected int steps;
    protected CoordInterface startCoordF, startCoordB;
    protected int animDelay; // in mili-seconds
    protected boolean exit = false;
    protected boolean verbose = true;
    protected boolean isFMMmarathon = false;
    protected boolean is2D;
    public boolean isDualMap = true;
    public boolean saveStats = true;
    private int MAP_COMPOSING_ROWS;
    private int MAP_COMPOSING_COLS;
    //protected boolean startOnWest;
    
    SEARCH_BASE(boolean is2D, HeuristicInterface hFunc, String mName, SEARCH_Map aMap, 
                String metricName, double WeightF, double WeightB){
        this.mapsName = mName;
        this.map = aMap;
        this.metricName = metricName;
        this.weightF = WeightF;
        this.weightB = WeightB;
        this.path = null;
        this.minWeight = Math.min(this.weightF, this.weightB);
        this.is2D = is2D;
        this.hFuncF = hFunc;
        nExpanded = 0;
        nGenerated = 0;
        nReExpanded = 0;
        //if (this.hFuncF != null) this.hFuncF.setMap(this.map);
    }
    
    public void setAnimObj(AnimGui aGui, DrawMatrix aMat) {
        this.gui = aGui;
        this.matPanel = aMat;
    }

    public void setAnimDelay(int newDelay) {
        this.animDelay = newDelay;
    }
    public void enable() {
        exit = false;
    }
    public void stop() {
        exit = true;
    }
    
    protected void animThread() {
        tCount++;
        tSCount++;
        
        if (tCount >= tSize) {
            tCount = 0;
            if (animDelay>0) {
                matPanel.repaint();
                try {
                    Thread.sleep(animDelay);
                    //this.map.addOppNodesToMap();
                } catch (InterruptedException ex) {
                    //Logger.getLogger(SEARCH_DUAL_MAP_AStarBorderPreProcess.class.getName()).log(Level.SEVERE, null, ex);
                    System.err.println(ex);
                }
            }
        }
        if (tSCount >= tLimit) {
            tSize = (long) Math.sqrt(0.2*(double)(tLimit));
            tCount = 0;
            tSCount = 0;
            tLimit = (long)((double)(tLimit) * 1.4);
        }
    }    
    
    protected void resetAnimationCounters() {
        tCount = 0;
        tLimit = tLimit_ACCELERATE; 
        tSize = 1;
        tSCount = 0;        
    }
    
    public void updateInit(String metricName, double forwardW, double backwardW){
        this.metricName = metricName;
        this.weightF = forwardW;
        this.weightB = backwardW;
        nExpanded = 0;
        nGenerated = 0;
        nReExpanded = 0;
    }    
    
    public List<NodeInterface> findPath(CoordInterface startF, CoordInterface startB) {return null;}
    
    protected void setSearchObjects(CoordInterface startF, CoordInterface startB) {
        timek = 0; // reseting the time out counter
        resetAnimationCounters();
                
        map.createOpenList();
        startCoordF = startF;
        startCoordB = startB;
        if (this.is2D) {
            neighborsF = new Neighbors2D(weightF);
            neighborsB = new Neighbors2D(weightB);
        } else {
            neighborsF = new Neighbors3D(weightF);
            neighborsB = new Neighbors3D(weightB);
        }
        map.setStartNeighbor(neighborsF);
        map.setNextNeighbor(neighborsB);
        
        switch (metricName) {
            case MetricInterface.EUCLID -> {
                if (this.is2D) metricF = new Metric2D_Euclid(startCoordF, startCoordB);
                else           metricF = new Metric3D_Euclid(startCoordF, startCoordB);
            }
            case MetricInterface.DISCREETK3 -> {
                if (this.is2D) metricF = new Metric2D_DiscreetK3(startCoordF, startCoordB);
                else           metricF = new Metric3D_DiscreetK3(startCoordF, startCoordB);
            }
            case MetricInterface.MANHATTAN -> {
                if (this.is2D) metricF = new Metric2D_Manhattan(startCoordF, startCoordB);
                else           metricF = new Metric3D_Manhattan(startCoordF, startCoordB);
            }
            default -> {
                metricF = null;
                metricB = null;
                System.out.println("Heuristic type was not selected !");
            }
        }
        metricB = metricF.createOppositeObj();
        
        if (this.hFuncF != null){
            this.hFuncF.setHFunc(map, metricF, weightF, weightB);
            this.hFuncF.setStartGoalCoords(startF, startB);
            
            // creating a copy of the border list and saving it in the Heuristic Obj
            NodeInterface tmpB;
            List<NodeInterface> tmpBL = new ArrayList<>();
            List<NodeInterface> nList = map.getBorderNodes();
            BN = nList.size();
            for (NodeInterface b: nList) {
                tmpB = b.create();
                tmpBL.add(tmpB);
            }
            this.hFuncF.setBorder(tmpBL);
            map.setHfunc(this.hFuncF);
        }
    }
    
    
    public void swapSearchDirections() {
        setSearchObjects(startCoordB.create(), startCoordF.create());
    }
    
    protected void resetParametersEnd(String state) {
        SumOfPaths = -1; Makespan = -1; steps = -1;
        //time = System.nanoTime() - tStart;
        //runTime = time / 1e9;
        if (saveStats) printStats(state);
    }
    
    public void setMapRowsCols(int rows, int cols) {
        MAP_COMPOSING_ROWS = rows;
        MAP_COMPOSING_COLS = cols;
    }
    
    public double getPathTotalLength() {return SumOfPaths;}
    public double getPathTotalMKSP() {return Makespan;}
    
    protected boolean initialCheckBeforeSearch(CoordInterface s1, CoordInterface s2) {
        boolean valid = true;
        if (map == null) {
            System.out.println("Invalid map object initialization !");
            valid = false;
        }
        if (map.isCellBorder(s1) || map.isCellBorder(s2)) {
            System.out.println("Start/Goal locations are at the border !");
            valid = false;
        }
        if (map.isObstacle(s1) || map.isObstacle(s2)) {
            System.out.println("Start/Goal locations are not passable.");
            valid = false;
        }
        if (!valid) {
            SumOfPaths = -1; Makespan = -1; steps = -1;
            runTime = -1;
            if (saveStats) printStats(STATE_INVALID_STARTS);
        }
        return valid;
    }
    
    // assuming startN is the goal node and that the current node and startN are on the same vertex
    protected void foundPathUni(NodeInterface currentN, NodeInterface startN) {
        time = System.nanoTime() - tStart;
        runTime = time / 1e9;
        SumOfPaths = currentN.getG();
        Makespan = currentN.getG();
        path = UtilityFunc.getPathToEndNodeN(currentN, startN);
        steps = path.size() - 1;
        printStats(STATE_FOUND_PATH);        
    }
    
    // assuming nodeF & nodeB are on the same vertex (the directions met)
    protected List<NodeInterface> foundPathBi(NodeInterface meetF, NodeInterface startF, NodeInterface meetB, NodeInterface startB) { 
        time = System.nanoTime() - tStart;
        runTime = time / 1e9;
        SumOfPaths = meetF.getG() + meetB.getG();
        Makespan = Math.max(meetF.getG() , meetB.getG());
        path = UtilityFunc.combineBiPaths(meetF, startF, meetB, startB);
        steps = path.size() - 1;
        printStats(STATE_FOUND_PATH);        
        return path;
    }
    
    public boolean canGenerateNeighbor(NodeInterface now, CoordInterface c, SEARCH_Map aMap) {
        if (now.getParent().getCoord().equalCoord(c)) return false; // cannot go back to the parent that just came from
        if (aMap.isObstacle(c)) return false;
        if (now.getIsBorder()) return aMap.coordNoUturnFromBorder(now.getParent(), c);
        else if (now.getIsCrossed()) return !(aMap.isCellBorder(c));
        else return true;
    }
    public boolean canGenerateNeighborNotClosed(NodeInterface now, CoordInterface c, SEARCH_Map aMap) {
        if (aMap.isClosed(c)) return false;
        if (now.getParent().getCoord().equalCoord(c)) return false; // cannot go back to the parent that just came from
        if (aMap.isObstacle(c)) return false;
        if (now.getIsBorder()) return aMap.coordNoUturnFromBorder(now.getParent(), c);
        else if (now.getIsCrossed()) return !(aMap.isCellBorder(c));
        else return true;
    }
    
    
    protected void countExpansions(NodeInterface now) {
        if (now.getIsReExpanded()) {
            nReExpanded++;
            now.setReExpanded(false);
        }
        else nExpanded++;
    }
    
    protected boolean checkTimeOut() {
        timek++;
        if (timek>countCheckTimeout) {
            timek = 0;
            time = System.nanoTime() - tStart;
            runTime = time / 1e9;
            if (runTime > timeoutSeconds) { // more than 5 minutes
                resetParametersEnd(STATE_TIMEOUT_PATH);
                return true;
            }
        }
        return false;
    }
    
    public int doesPathHasBorder() {
        return UtilityFunc.doesPathHasBorder(path);
    }
    
    public void printStats(String state) {
        int i;
        FileWriter myWriter;
        boolean newfile;
        String[] names;
        String line,header,algoName, hName;
        String sNameWH;
        
        
        sNameWH = this.getClass().getName().split("[.]")[1].split("[_]", 2)[1];
        if (hFuncF != null) sNameWH = sNameWH + "+" +
                    hFuncF.getClass().getName().split("[.]")[1].split("[_]", 2)[1];
        
        if (isFMMmarathon) {
            if (this.is2D) statsFname = fname2DfMMmarathon;
            else statsFname = fname3DfMMmarathon;
        }
        
        line = "";
        header = "";
        newfile = false;
        names = this.getClass().getName().split("[.]");
        
//        names[1].indexOf("_");
//        names[1].indexOf("_", names[1].indexOf("_")+1);
        //i = names[1].indexOf("_", names[1].indexOf("_", names[1].indexOf("_")+1)+1)+1; // get after 3x '_'
        i = names[1].indexOf("_")+1;
        algoName = names[1].substring(i).replaceAll("Star", "*");
        
        if (verbose) {
            if (state.equals(this.STATE_FOUND_PATH)) {
                System.out.print(mapsName + ": "+sNameWH+": Path found ! runtime="+runTime+"; steps="+steps+
                             "; SOC="+String.format("%.3f", SumOfPaths)+
                             "; MKSP="+String.format("%.3f", Makespan));
                System.out.println("; expanded="+nExpanded+"; re-expand="+nReExpanded+"; generated="+nGenerated);
            } else if (state.equals(this.STATE_TIMEOUT_PATH)) {
                System.out.println(sNameWH + " path search has reached timeout, aborting search.");
            } else if (state.equals(this.STATE_NOT_FOUND_PATH)) {
                System.out.println(sNameWH + " could not find the goal (no more nodes to expand).");
            } else if (state.equals(this.STATE_INVALID_STARTS)) {
                System.out.println(sNameWH + " map or starts locations are invalid.");
            }
        }
        if (hFuncF != null) {
            names = this.hFuncF.getClass().getName().split("[.]");
            i = names[1].indexOf("_")+1;
            hName = names[1].substring(i);
        } else hName = "";
        
        File myObj = new File(statsFname);
        try {
            if (myObj.createNewFile()) {
                newfile = true;
                //System.out.println("File created: " + myObj.getName());
            }
        } catch (IOException e) {
            System.out.println("A file create error occurred.");
        }
        

        line = line.concat(mapsName).concat(","); // map name
        line = line.concat(Boolean.toString(this.isDualMap)).concat(","); // if dual map or 
        String comboStr = String.format("%d x %d,", MAP_COMPOSING_ROWS,MAP_COMPOSING_COLS);
        line = line + comboStr;
        if (is2D) line = line.concat("2D,");
        else line = line.concat("3D,");
        //System.out.println( new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()) );
        line = line.concat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())).concat(",");
        
        
        //algoName = algoName.concat("+").concat(hName);
        algoName = sNameWH;
        line = line.concat(algoName).concat(",");
        
        line = line.concat(state).concat(",");
        line = line.concat(String.format("%.4f", runTime)).concat(",");
        line = line.concat(String.format("%d", steps)).concat(",");
        line = line.concat(String.format("%.4f", SumOfPaths)).concat(",");
        line = line.concat(String.format("%.4f", Makespan)).concat(",");
        line = line.concat(String.format("%d", (nExpanded+nReExpanded))).concat(",");
        line = line.concat(String.format("%d", nExpanded)).concat(",");
        line = line.concat(String.format("%d", nReExpanded)).concat(",");
        line = line.concat(String.format("%d", nGenerated)).concat(",");
        
        if (is2D) line = line + String.format("(%d:%d),", startCoordF.getX(),startCoordF.getY());
        else line = line + String.format("(%d:%d:%d),", startCoordF.getX(),startCoordF.getY(),startCoordF.getZ());
        //line = line.concat(String.format("%d", startCoordF.getX())).concat(",");
        //line = line.concat(String.format("%d", startCoordF.getY())).concat(",");
        //if (!this.is2D)
        //    line = line.concat(String.format("%d", startCoordF.getZ())).concat(",");
        
        if (is2D) line = line + String.format("(%d:%d),", startCoordB.getX(),startCoordB.getY());
        else line = line + String.format("(%d:%d:%d),", startCoordB.getX(),startCoordB.getY(),startCoordB.getZ());
        //line = line.concat(String.format("%d", startCoordB.getX())).concat(",");
        //line = line.concat(String.format("%d", startCoordB.getY())).concat(",");
        //if (!this.is2D)
        //    line = line.concat(String.format("%d", startCoordB.getZ())).concat(",");
        
        line = line.concat(String.format("%d,",doesPathHasBorder()));
        line = line.concat(metricName).concat(",");
        line = line.concat(String.format("%.4f", weightF)).concat(",");
        line = line.concat(String.format("%.4f", weightB)).concat(",");
        if (isFMMmarathon) {
//            line = line.concat(String.format("%.4f", map.borderRate)).concat(",");
//            if (gui == null)
//                line = line.concat(String.format("%.4f", 9.9)).concat(",");
//            else
//                line = line.concat(String.format("%.4f", gui.fMMp)).concat(",");
        }
        else line = line.concat(String.format("%.4f", map.borderRate)).concat(",");
        line = line.concat(String.format("%d", this.BN)).concat("\n");
        
        try {
            if (newfile) {
                header = header.concat("Maps,Dual,Combo,Dim,TXN_Date,Algo,State,runTime,steps,SOC,MKSP,TotalExpanded,Expanded,ReExpanded,Generated,");
                header = header.concat("StartCoordinates,GoalCoordinates,");
                header = header.concat("PathBorders,MetricName,StartWeight,GoalWeight,");
                if (isFMMmarathon) header = header.concat("BorderRate,fMMp,BorderSize\n");
                else header = header.concat("BorderRate,BorderSize\n");
                
                myWriter = new FileWriter(statsFname, false);
                myWriter.write(header);
            } 
            else {
                myWriter = new FileWriter(statsFname, true);
            }
            
            myWriter.write(line);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("A file write error occurred.");
        }
    }    
}
