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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author atabacar
 * 
 */
public class MarathonGUI  extends JFrame {
    private int MAP_COMPOSING_ROWS = 1;
    private int MAP_COMPOSING_COLS = 1;
    
    protected SEARCH_Map map;
    protected SEARCH_BASE searchMethod;
    protected String mapsName;
    protected String[] SEARCH_str;
    
    // mapping string to object, a dictionary
    protected Map<String, SEARCH_BASE> searchMethodsDict;
    
    protected List<JButton> searchButtonsList;
    private final JButton runAllMethods;
    private final JButton runAllRatesFMM;
    private final JButton runAllFromFile;
    private final JButton createNewMapFileRandom;
    private final JButton saveInstancesToFile;
    private final JComboBox runAllLoopSelect;
    private final JComboBox tempWallsPercent;
    private final JComboBox westHeuristicSelect;
    private final JComboBox eastHeuristicSelect;
    private final JLabel runAllCountLabel;
    private final JLabel tempWallsLabel;
    private final JLabel runLoopLabel;
    private final JLabel startFileIterationLabel;
    private final JPanel buttonPanel;
    private final JPanel buttonPanel2;
    private final JPanel innerButtonPanel;
    private final JPanel innerButtonPanel2;
    private final JPanel innerButtonPanel3;
    private final JTextField runLoopField;
    private final JTextField startFileIterationField;

    private final Double[] randWallsPercent = {0.0, 0.001, 0.005, 0.01, 0.05, 0.1, 0.15, 0.25, 0.3, 0.4, 0.5, 0.6, 0.8, 0.9};
    private final Integer[] runLoopsPerMethod = {1, 2, 4, 5, 7, 10, 15, 20, 30, 50, 80, 100, 150, 300, 500, 1000};
    private final String[] heuristicStr = {"DiscreetK3", "Euclidean" , "Manhattan"};
    
    protected double fMMp;
    private final int windowW = 1400; // window width
    private final int windowH = 600; // window height
    private final int runLoopSet = 1;
    private int runFromFileStart = 1;
    
    private int xstart, ystart, zstart, xend, yend, zend;
    protected int pathSteps;
    protected long nExpanded;
    protected long nGenerated;
    protected long nReExpanded;
    protected double runTime;
    protected double pathLength;
    
    // Nodes and List of nodes
    List<int[]> bordersCoordinates;
    protected List<NodeInterface> pathN;
    
    private final boolean providedTwoMaps;
    private final boolean is2Dmap;
    private CoordInterface startF, startB;
    
    //============================================================================
    
    public MarathonGUI(String mapName) { //throws Exception {
        
        this.mapsName = mapName;
        String[] tokens = mapName.split("[|]", 2);
        providedTwoMaps = (tokens.length > 1);
        String[] extTok = tokens[0].split("[:]")[0].split("[.]");
        String extension = extTok[extTok.length-1].trim();
        is2Dmap = ("map".equals(extension));
        System.out.println(extension +" ;  "+ Boolean.toString(is2Dmap));
        
        //resetMap();
        map = new SEARCH_Map(this.mapsName, this.providedTwoMaps, this.is2Dmap);
        map.runMap(MAP_COMPOSING_ROWS, MAP_COMPOSING_COLS);
        searchMethodsDict = new HashMap<>();
        reInstantiateSearchObjects();

        searchButtonsList = new ArrayList<>();
        for (int i=0; i<SEARCH_str.length; i++) {
            if (!"".equals(SEARCH_str[i]))
                searchButtonsList.add(new JButton(SEARCH_str[i]));    
        }

        runAllMethods = new JButton("Run All");
        runAllRatesFMM = new JButton("Run xfMM");
        runAllFromFile = new JButton("Run From File");
        createNewMapFileRandom = new JButton("Create Random Map");
        saveInstancesToFile = new JButton("Save instances to file");
        
                
        // temp wall random
        tempWallsPercent = new JComboBox(randWallsPercent);
        tempWallsPercent.setSelectedIndex(0);
        tempWallsLabel = new JLabel(" Rand-Walls %:");
        
        runAllCountLabel = new JLabel("Run-Loops:");
        runAllLoopSelect = new JComboBox(runLoopsPerMethod);
        runAllLoopSelect.setSelectedIndex(1);
        

        runLoopLabel = new JLabel("  Repeat-Loops:");
        runLoopField = new JTextField();
        runLoopField.setPreferredSize(new Dimension(50, 25));
        runLoopField.setText(Integer.toString(runLoopSet));

        startFileIterationLabel = new JLabel("  Run From File Starting at iteration:");
        startFileIterationField = new JTextField();
        startFileIterationField.setPreferredSize(new Dimension(50, 25));
        startFileIterationField.setText(Integer.toString(runFromFileStart));

        westHeuristicSelect = new JComboBox(heuristicStr);
        eastHeuristicSelect = new JComboBox(heuristicStr);
        westHeuristicSelect.setSelectedIndex(0);
        eastHeuristicSelect.setSelectedIndex(0);
        
        innerButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        innerButtonPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        innerButtonPanel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        for (int i=0; i<SEARCH_str.length; i++)
            if (!"".equals(SEARCH_str[i]))
                innerButtonPanel.add(searchButtonsList.get(i));
        
        
        innerButtonPanel2.add(runAllMethods);
        innerButtonPanel2.add(runAllRatesFMM);
        innerButtonPanel2.add(runAllFromFile);
        innerButtonPanel2.add(saveInstancesToFile);
        innerButtonPanel2.add(createNewMapFileRandom);
        innerButtonPanel2.add(runAllCountLabel);
        innerButtonPanel2.add(runAllLoopSelect);
        innerButtonPanel2.add(runLoopLabel);
        innerButtonPanel2.add(runLoopField);
        innerButtonPanel2.add(startFileIterationLabel);
        innerButtonPanel2.add(startFileIterationField);
        innerButtonPanel2.add(tempWallsLabel);
        innerButtonPanel2.add(tempWallsPercent);
                
        innerButtonPanel3.add(westHeuristicSelect);
        innerButtonPanel3.add(eastHeuristicSelect);
        
                
        buttonPanel2 = new JPanel();
        buttonPanel2.setLayout(new BorderLayout());
        buttonPanel2.add(innerButtonPanel2, BorderLayout.NORTH);
        buttonPanel2.add(innerButtonPanel3, BorderLayout.CENTER);
        
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(innerButtonPanel, BorderLayout.NORTH);
        buttonPanel.add(buttonPanel2, BorderLayout.CENTER);
        
        ActionHandler handler = new ActionHandler();
        
        for (int i=0; i<SEARCH_str.length; i++)
            if (!"".equals(SEARCH_str[i]))
                searchButtonsList.get(i).addActionListener(handler);
        
        runAllMethods.addActionListener(handler);
        runAllRatesFMM.addActionListener(handler);
        runAllFromFile.addActionListener(handler);
        saveInstancesToFile.addActionListener(handler);
        createNewMapFileRandom.addActionListener(handler);
        runAllLoopSelect.addActionListener(handler);
        tempWallsPercent.addActionListener(handler);
        runLoopField.addActionListener(handler);
        startFileIterationField.addActionListener(handler);
        westHeuristicSelect.addActionListener(handler);
        eastHeuristicSelect.addActionListener(handler);
        
        this.setTitle("Grid Map 2D/3D Search Marathon:  " + mapName);
        this.setSize(windowW, windowH); // frame size 
        this.setLayout(new BorderLayout());
        this.add(buttonPanel, BorderLayout.CENTER);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        
        pathSteps = 0;
        pathLength = 0;
        addListeners();
        
    }
    
    public void resetMap() { //throws Exception {
       map = new SEARCH_Map(this.mapsName, this.providedTwoMaps, this.is2Dmap); 
    }
    
    private void reInstantiateSearchObjects() {
        SEARCH_str = new String[30];
        for (int i=0; i<SEARCH_str.length; i++)
            SEARCH_str[i] = "";
        
        int j=0;
        
        SEARCH_str[j++] = "Dijkstra's";
        SEARCH_str[j++] = "A*+FE";
        SEARCH_str[j++] = "A*+FBE";
        SEARCH_str[j++] = "A*+EFBE";
        SEARCH_str[j++] = "MM+FE";
        SEARCH_str[j++] = "MM+FBE";
        SEARCH_str[j++] = "MM+EFBE";
        SEARCH_str[j++] = "fMM+FE";
        SEARCH_str[j++] = "fMM+FBE";
        SEARCH_str[j++] = "fMM+EFBE";
        SEARCH_str[j++] = "A*+FE_BPMX";
        SEARCH_str[j++] = "A*+FBE_BPMX";
        SEARCH_str[j++] = "A*+EFBE_BPMX";


        j=0;
        String mapsString = UtilityFunc.getMapsNameForStats(mapsName); // -->  mapsString instead of this

        searchMethodsDict.put(SEARCH_str[j++],   new SEARCH_Dijkstras(is2Dmap,              null,mapsString, map, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str[j++],       new SEARCH_AStar(is2Dmap,   new   Heuristic_FE(),mapsString, map, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str[j++],       new SEARCH_AStar(is2Dmap,   new  Heuristic_FBE(),mapsString, map, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str[j++],       new SEARCH_AStar(is2Dmap,   new Heuristic_EFBE(),mapsString, map, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str[j++],          new SEARCH_MM(is2Dmap,   new   Heuristic_FE(),mapsString, map, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str[j++],          new SEARCH_MM(is2Dmap,   new  Heuristic_FBE(),mapsString, map, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str[j++],          new SEARCH_MM(is2Dmap,   new Heuristic_EFBE(),mapsString, map, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str[j++],         new SEARCH_fMM(is2Dmap,   new   Heuristic_FE(),mapsString, map, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str[j++],         new SEARCH_fMM(is2Dmap,   new  Heuristic_FBE(),mapsString, map, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str[j++],         new SEARCH_fMM(is2Dmap,   new Heuristic_EFBE(),mapsString, map, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str[j++],  new SEARCH_AStar_BPMX(is2Dmap,   new   Heuristic_FE(),mapsString, map, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str[j++],  new SEARCH_AStar_BPMX(is2Dmap,   new  Heuristic_FBE(),mapsString, map, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str[j++],  new SEARCH_AStar_BPMX(is2Dmap,   new Heuristic_EFBE(),mapsString, map, heuristicStr[0], 1.0, 1.0));
        
        //searchMethodsDict.put(SEARCH_str[j++],   new SEARCH_AStar(is2Dmap,       new Heuristic_EFBE(),mapsString, map, heuristicStr[0], 1.0, 1.0));
        //searchMethodsDict.put(SEARCH_str[j++],   new SEARCH_AStar(is2Dmap,       new   Heuristic_FE(),mapsString, map, heuristicStr[0], 1.0, 1.0));

    }    

    private void randomizeStartGoal(double d) {
        CoordInterface coord=null;
        
        while (coord == null)
            coord = map.getRandomStartGoal("West", d);
        xstart = coord.getX();
        ystart = coord.getY();
        if (!this.is2Dmap) zstart = coord.getZ();
        coord = null;
        while (coord == null) 
            if (this.providedTwoMaps) coord = map.getRandomStartGoal("East", d);
            else coord = map.getRandomStartGoal("West", d);
        xend = coord.getX();
        yend = coord.getY();
        if (!this.is2Dmap) zend = coord.getZ();
    }
    
    private void setStarts(int[] startsArr) {
        if (this.is2Dmap) {
            startF = new Coord2D(startsArr[0], startsArr[1]);
            startB = new Coord2D(startsArr[2], startsArr[3]);
            xstart = startsArr[0];
            ystart = startsArr[1];
            xend = startsArr[2];
            yend = startsArr[3];
        } else {
            startF = new Coord3D(startsArr[0], startsArr[1], startsArr[2]);
            startB = new Coord3D(startsArr[3], startsArr[4], startsArr[5]);
            xstart = startsArr[0];
            ystart = startsArr[1];
            zstart = startsArr[2];
            xend = startsArr[3];
            yend = startsArr[4];
            zend = startsArr[5];
        }
    }

    private String getSelectedWestHeuristic(){
        if ("Euclidean".equals(this.westHeuristicSelect.getSelectedItem().toString()))
            return MetricInterface.EUCLID;
        if ("Manhattan".equals(this.westHeuristicSelect.getSelectedItem().toString()))
            return MetricInterface.MANHATTAN;
        if ("DiscreetK3".equals(this.westHeuristicSelect.getSelectedItem().toString()))
            return MetricInterface.DISCREETK3;
        
        // default
        return MetricInterface.EUCLID;        
    }
    private String getSelectedEastHeuristic(){
        if ("Euclidean".equals(this.eastHeuristicSelect.getSelectedItem().toString()))
            return MetricInterface.EUCLID;
        if ("Manhattan".equals(this.eastHeuristicSelect.getSelectedItem().toString()))
            return MetricInterface.MANHATTAN;
        if ("DiscreetK3".equals(this.eastHeuristicSelect.getSelectedItem().toString()))
            return MetricInterface.DISCREETK3;
        
        // default
        return MetricInterface.EUCLID;        
    }
    
    private Integer getSelectedRunAllLoops() {
        return Integer.parseInt(this.runAllLoopSelect.getSelectedItem().toString());
    }
    
    private Double getSelectedTempWallPercent() {
        return Double.parseDouble(this.tempWallsPercent.getSelectedItem().toString());
    }

    private void reduceBordersTo(int maxB, double lowerBr) {
        int nowB;
        int n;
        
        nowB = map.getBorderNodesSize();
        n = nowB - (int)( (double)(maxB)*lowerBr );
        if (n <= 0) System.out.println("*** Warning !!!     not reducing borders, 0 or lower max val.");
        for (int i=0; i<n; i++) {
            map.unsetBorderRandomly();
        }
    }
    
    private void parseBordersLocationsSetBorder(String bordsStr) {
        String[] parts;
        String[] coords;
        int x,y,z;
        CoordInterface c;
        //int[] num;
        
        bordersCoordinates = new ArrayList<>();
        parts = bordsStr.split("[|]");
        if (this.is2Dmap){
            for (int i=0; i< parts.length; i++) {
                coords = parts[i].split("[:]");
                x = Integer.parseInt(coords[0]);
                y = Integer.parseInt(coords[1]);
                c = new Coord2D(x,y);
                map.setOneBorder(c);
            }
        } else {
            for (int i=0; i< parts.length; i++) {
                coords = parts[i].split("[:]");
                x = Integer.parseInt(coords[0]);
                y = Integer.parseInt(coords[1]);
                z = Integer.parseInt(coords[2]);
                c = new Coord3D(x,y,z);
                map.setOneBorder(c);
            }
        }
    }
    
    protected void restoreBorderLocatoins() {
        CoordInterface c;
        int num[];
        
        if (this.is2Dmap){
            for (int i=0; i<bordersCoordinates.size(); i++) {
                num = bordersCoordinates.get(i);
                c = new Coord2D(num[0], num[1]);
                map.setOneBorder(c);
            }
        } else {
            for (int i=0; i<bordersCoordinates.size(); i++) {
                num = bordersCoordinates.get(i);
                c = new Coord3D(num[0], num[1], num[2]);
                map.setOneBorder(c);
            }
        }
    }
    
    private void saveBorderLocations() {
        List<NodeInterface> bList;
        NodeInterface n;
        int num[];

        bordersCoordinates = new ArrayList<>();
        bList = map.getBorderNodes();
        if (this.is2Dmap) {
            for (int i=0; i < bList.size(); i++){
                n = bList.get(i);
                num = new int[2];
                num[0] = n.getCoord().getX();
                num[1] = n.getCoord().getY();
                bordersCoordinates.add(num);
            }
        } else {
            for (int i=0; i < bList.size(); i++){
                n = bList.get(i);
                num = new int[3];
                num[0] = n.getCoord().getX();
                num[1] = n.getCoord().getY();
                num[2] = n.getCoord().getZ();
                bordersCoordinates.add(num);
            }
        }
    }

    private void softClearSettings(){
        nExpanded = 0;
        nGenerated = 0;
        nReExpanded = 0;
        pathSteps = 0;
        pathLength = 0;
        runTime = 0;
        map.clearMapCellsResetBorders();
    }
    
    
    private void addListeners(){                
        runLoopField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                // clears the text when in focus if it contains the inital string
                if (runLoopField.getText().equals(Integer.toString(runLoopSet)))
                    runLoopField.setText(""); 
            }
            @Override
            public void focusLost(FocusEvent e) {
            }
        });

        startFileIterationField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                // clears the text when in focus if it contains the inital string
                if (startFileIterationField.getText().equals(Integer.toString(runLoopSet)))
                    startFileIterationField.setText(""); 
            }
            @Override
            public void focusLost(FocusEvent e) {
            }
        });
    }
    
// ======================================================    
    
    
    private class ActionHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event){
            
            // DUAL MAP SEARCH classes call
            boolean swap = false;
            for (int i=0; i < searchButtonsList.size(); i++) {
                if (event.getSource() == searchButtonsList.get(i)) {
                    int runLoops;
                    double accuRunTime;
                    
                    resetMap();
                    map.runMap(MAP_COMPOSING_ROWS,MAP_COMPOSING_COLS);
                    if (is2Dmap) map.solderMapsToWest2D(1.0);
                    else map.solderMapsToWest3D(1.0);
                    saveBorderLocations();
                    
                    runLoops = Integer.parseInt(runLoopField.getText());
                    System.out.println("Loops value="+runLoops);
                    accuRunTime= 0.0;
                    
                    for (int jj=0; jj<runLoops; jj++) {
                        softClearSettings();
                        restoreBorderLocatoins();
                        map.addRandomWalls(getSelectedTempWallPercent());
                        randomizeStartGoal(0.3); // in the first 30% of the map, and the last in the second map
                        for (int k=0; k<2; k++) {
                            softClearSettings();
                            restoreBorderLocatoins();
                            searchMethodsDict = new HashMap<>();
                            reInstantiateSearchObjects();
//                            searchMethod = searchMethodsDict.get(SEARCH_str[i]);
//                            searchMethod.verbose = (jj == (runLoops-1));
//                            swap = !swap;
//                            if (swap)
//                                searchMethod.updateInit(Heuristics.DISCREETK3, getSelectedWestFactor(),
//                                                        Heuristics.DISCREETK3, getSelectedEastFactor());
//                            else
//                                searchMethod.updateInit(Heuristics.EUCLID, getSelectedWestFactor(),
//                                                        Heuristics.EUCLID, getSelectedEastFactor());
//                            System.out.println("Iteration: "+jj+"; GUI Start: "+xstart+","+ystart+","+zstart+" ; Goal: "+xend+","+yend+","+zend);
//                            long tStart = System.nanoTime();
//                            pathN = searchMethod.findPath(xstart, ystart, zstart, xend, yend, zend);
//                            long time = System.nanoTime() - tStart;
//                            runTime = time / 1e9;
//                            accuRunTime += runTime;
//                            searchMethod = null;
//                            searchMethodsDict.put(SEARCH_str[i], null);
                        }
                    } // end of run loop
                    
//                    if (pathN != null) System.out.println("need to add path"); //MiscMethods.addPathToMapN(map3Demo, pathN);
//                    else System.out.println("empty path !!!!");
//                    nExpanded = searchMethod.nExpanded;
//                    nGenerated = searchMethod.nGenerated;
//                    nReExpanded = searchMethod.nReExpanded;
//                    if (pathN != null){
//                        pathSteps = pathN.size();
//                        pathLength = searchMethod.getPathTotalLength();
//                        pathMKSP = searchMethod.getPathTotalMKSP();
//                    }
//                    else System.out.println("Path not found !");
                    restoreBorderLocatoins();
                    System.out.println("acucmulated run time:  loops="+runLoops+"; average run time="+
                            String.format("%.4f",(double)(accuRunTime/runLoops)));
                    break;
                }
            }
            
            
            if (event.getSource() == westHeuristicSelect){
                System.out.println("Heuristic Selected: " + westHeuristicSelect.getSelectedItem());
                eastHeuristicSelect.setSelectedIndex(westHeuristicSelect.getSelectedIndex());
            }

            if (event.getSource() == eastHeuristicSelect){
                System.out.println("Heuristic Selected: " + eastHeuristicSelect.getSelectedItem());
                westHeuristicSelect.setSelectedIndex(eastHeuristicSelect.getSelectedIndex());
            }

            if (event.getSource() == runAllRatesFMM){
                int runLoops;
                int repeatLoops;
                double randStartGoal = 0.9;
                double br, w;
                int[]       wL      = {1, 2, 3, 5};
                double[]    brL     = {0.01, 0.08, 0.2, 0.5, 1.0};
                double[]    fMMpL   = {0.001, 0.01, 0.05, 0.1, 0.3, 0.5, 0.7, 0.9, 0.96, 0.999};
                
                runLoops = getSelectedRunAllLoops();
                repeatLoops = Integer.parseInt(runLoopField.getText());
                System.out.println("fMM marathon:"+"; Run-All Loops="+runLoops+" ; Repeat Loops="+repeatLoops);

                resetMap();
                map.runMap(MAP_COMPOSING_ROWS,MAP_COMPOSING_COLS);
                if (is2Dmap) map.solderMapsToWest2D(1.0);
                else map.solderMapsToWest3D(1.0);
                //saveBorderLocations();
                
                for (int k=0; k < repeatLoops; k++) {
                    softClearSettings();
                    randomizeStartGoal(randStartGoal);
                    for (int i=0; i<brL.length; i++) {
                        br = brL[i];
                        resetMap();
                        map.runMap(MAP_COMPOSING_ROWS,MAP_COMPOSING_COLS);
                        if (is2Dmap) map.solderMapsToWest2D(1.0);
                        else map.solderMapsToWest3D(1.0);
                        saveBorderLocations();
                        for (int j=0; j<wL.length; j++) {
                            w = (double)wL[j];
                            System.out.println("fMM marathon "+
                                    " ;  repeat: "+(k+1)+"/"+repeatLoops+
                                    " ;  B-rate="+String.format("%.2f", br)+
                                    " ;  Weight: "+wL[j]+"/"+wL[wL.length-1]+
                                    //" ;  fMMp: "+String.format("%.2f", fMMp)+
                                    " ;  fMMp: range (0,1)"+
                                    " ;  Start: ("+xstart+","+ystart+")    ;    Goal: ("+xend+","+yend+")");
                            reInstantiateSearchObjects();
                            softClearSettings();
                            restoreBorderLocatoins();
//                            searchMethod = searchMethodsDict.get("A* FBEth H2L");
//                            searchMethod.fname3D = searchMethod.fname3DMarathon; // sets file name different from single run
//                            searchMethod.updateInit(getSelectedWestHeuristic(), w,
//                                                    getSelectedEastHeuristic(), 1.0);
//                            searchMethod.isFMMmarathon = true;
//                            pathN = searchMethod.findPath(xstart, ystart, zstart, xend, yend, zend);
//                            searchMethod.isFMMmarathon = false;
                            for (int kk=0; kk<fMMpL.length; kk++) {
                                fMMp = fMMpL[kk];
                                reInstantiateSearchObjects();
                                softClearSettings();
                                restoreBorderLocatoins();
//                                searchMethod = searchMethodsDict.get("real fMM FBEth");
//                                searchMethod.fname3D = searchMethod.fname3DMarathon; // sets file name different from single run
//                                searchMethod.updateInit(getSelectedWestHeuristic(), w,
//                                                        getSelectedEastHeuristic(), 1.0);
//                                pathN = searchMethod.findPath(xstart, ystart, zstart, xend, yend, zend);
//                                searchMethod = null;
//                                searchMethodsDict.put(SEARCH_str[i], null);
                            }
                        }
                    }
                }
                System.out.println("*** All fMM loops completed !");                        
            }            
            
            
            // run all algorithms with randomized start/goal locations (same for each)
            // and with random temporary wall locations (same for each)
            // with selected loops count.
            // with repeating same for same method inner-loop count.
            if (event.getSource() == runAllMethods){
                int repeatLoops;
                double[]    wL      = { 1.0, 2.0, 4.0, 6.0, 8.0, 10.0 };
                //double[]    wL      = { 1.0 };
                double      w;
                double[]    brL     = { 0.1, 0.4, 0.7, 1.0 };
                //double[]    brL     = { 0.7 };
                double      br;
                double randStartGoal = 0.8;
                
                repeatLoops = Integer.parseInt(runLoopField.getText());
                
                resetMap();
                map.runMap(MAP_COMPOSING_ROWS,MAP_COMPOSING_COLS);
                if (is2Dmap) map.solderMapsToWest2D(1.0);
                else map.solderMapsToWest3D(1.0);
                
                for (int k=0; k < repeatLoops; k++) {
                    //softClearSettingsClearWalls();
                    //restoreBordersStatus();
                    //map3Demo.addRandomWalls(getSelectedTempWallPercent());
                    softClearSettings();
                    randomizeStartGoal(randStartGoal);
                    //fixedStarts();
                    for (int ii=0; ii<brL.length; ii++) {
                        br = brL[ii];
                        resetMap();
                        map.runMap(MAP_COMPOSING_ROWS,MAP_COMPOSING_COLS);
                        if (is2Dmap) map.solderMapsToWest2D(1.0);
                        else map.solderMapsToWest3D(1.0);
                        saveBorderLocations();
                        for (int jj=0; jj<wL.length; jj++) {
                            w = wL[jj];
                            System.out.println( "Repeat: "+(k+1)+"/"+repeatLoops+
                                                " ;   border-rate="+br+
                                                " ;   West Weight: "+w+
                                                " ;   Start: ("+xstart+","+ystart+","+zstart+")   ;   Goal: ("+xend+","+yend+","+zend+")");
                            searchMethodsDict = new HashMap<>();
                            reInstantiateSearchObjects();
                            for (int i=0; i<SEARCH_str.length && !SEARCH_str[i].equals(""); i++) {
                                softClearSettings();
                                restoreBorderLocatoins();
//                                searchMethod = searchMethodsDict.get(SEARCH_str[i]);
//                                searchMethod.fname3D = searchMethod.fname3DMarathon;
//                                searchMethod.updateInit(getSelectedWestHeuristic(), w,
//                                                        getSelectedEastHeuristic(), 1.0);
//                                searchMethod.isFMMmarathon = false;
//                                pathN = searchMethod.findPath(xstart, ystart, zstart, xend, yend, zend);
                                searchMethod = null;
                                searchMethodsDict.put(SEARCH_str[i], null);
                            }
                        }
                    }
                }
                System.out.println("*** All loops completed !");
            }
            
                    
            if (event.getSource() == createNewMapFileRandom){
                resetMap();
                map.runMap(MAP_COMPOSING_ROWS,MAP_COMPOSING_COLS);
                map.addRandomWalls(0.10);
                map.createNewMapFile("_10percent");
            }

            
            // ===============================================================
            if (event.getSource() == saveInstancesToFile){
                //List<BDWF> factL = new ArrayList<>();
                int repeatLoops, maxB=0;
                //double[]    wL      = {4.0}; //{ 1.0, 2.0, 4.0, 6.0, 8.0, 10.0 };
                //double[]    wL      = { 1.0, 1.1, 1.5, 2.0, 4.0, 6.0, 8.0, 10.0 };
                double[]    wL      = { 1.0, 1.25, 1.5, 2.0, 4.0, 6.0, 10.0 };
                //double[]    wL      = { 4.0};
                double      w;
                //double[]    brL     = { 0.005, 0.01, 0.05, 0.1, 0.2, 0.4, 0.7, 1.0 };
                //double[]    brL     = { 1.0, 0.7, 0.4, 0.2, 0.1, 0.05, 0.01, 0.005}; 
                double[]    brL     = { 1.0, 0.4, 0.2, 0.1, 0.01}; 
                //double[]    brL     = { 0.2 }; // must be in descending order
                double      br, prevBr = -1.0;
                double randStartGoal = 0.8;
                
                FileWriter myWriter;
                String line, bArray, header, mapForPrint,mapfN;
                
                repeatLoops = Integer.parseInt(runLoopField.getText());
                
                mapForPrint = UtilityFunc.extractMapNames(mapsName);  //  "Berlin_0_1024.map|Denver_2_1024.map"
                mapfN = UtilityFunc.mapFilesToConvention(mapForPrint);//  Berlin_0_1024~map+Denver_2_1024~map.csv
                String s1 = UtilityFunc.conventionToMapFiles(mapfN);  //  "Berlin_0_1024.map|Boston_1_1024.map"
                String s2 = UtilityFunc.compactMapNames(s1);          //  "../Maps/Berlin_0_1024.map|../Maps/Boston_1_1024.map"
                
                if (is2Dmap)
                    if (providedTwoMaps)
                        header = "mapName,mapRows,mapCols,startX,startY,goalX,goalY,wegithWest,weightEast,borderRate,bordersArray\n";
                    else
                        header = "mapName,mapRows,mapCols,startX,startY,goalX,goalY,wegithWest\n";
                else
                    if (providedTwoMaps)
                        header = "mapName,mapX,mapY,startX,startY,startZ,goalX,goalY,goalZ,wegithWest,weightEast,borderRate,bordersArray\n";
                    else
                        header = "mapName,mapX,mapY,startX,startY,startZ,goalX,goalY,goalZ,wegithWest\n";
                try {
                    myWriter = new FileWriter(mapfN, false);
                    myWriter.write(header);
                    
                    // saves problem instances to file for future use if needed
                    for (int k=0; k < repeatLoops; k++) { // outer repeat loop, divided by 2
                        resetMap();
                        map.runMap(MAP_COMPOSING_ROWS,MAP_COMPOSING_COLS);
                        randomizeStartGoal(randStartGoal);
                        for (int ii=0; ii<brL.length; ii++) {
                            if (!providedTwoMaps && ii>0) continue;
                            br = brL[ii];
                            
                            // setting border, if current is derived from previous, reduce some border nodes.
                            if (ii==0) {
                                if (is2Dmap) maxB = map.solderMapsToWest2D(br);
                                else maxB = map.solderMapsToWest3D(br);                                
                                saveBorderLocations(); // to create the coordinates list object and initialize with borders
                            } else {
                                if (prevBr > br) reduceBordersTo(maxB, br);
                                else {
                                    if (is2Dmap) maxB = map.solderMapsToWest2D(br);
                                    else maxB = map.solderMapsToWest3D(br);                                
                                    saveBorderLocations(); // to create the coordinates list object and initialize with borders
                                }
                            }
                            prevBr = br;
                            List<NodeInterface> bordersList = map.getBorderNodes();
                            if (bordersList.isEmpty()) bArray = "empty";
                            else {
                                bArray = "";
                                for (int t=0; t<bordersList.size(); t++) {
                                    NodeInterface n = bordersList.get(t);
                                    if (is2Dmap) {
                                        bArray = bArray.concat(String.format("%d", n.getCoord().getX())).concat(":");
                                        bArray = bArray.concat(String.format("%d", n.getCoord().getY()));
                                    } else {
                                        bArray = bArray.concat(String.format("%d", n.getCoord().getX())).concat(":");
                                        bArray = bArray.concat(String.format("%d", n.getCoord().getY())).concat(":");
                                        bArray = bArray.concat(String.format("%d", n.getCoord().getZ()));
                                    }
                                    if (t<(bordersList.size()-1)) bArray = bArray.concat("|");
                                }
                            }
                            System.out.println("saving:  outer iteration "+(k+1)+" out of: "+ repeatLoops +
                                               " ;  border density iteration: "+(ii+1)+" out of "+brL.length + 
                                               " ;  border nodes count: " + bordersList.size());
                            for (int jj=0; jj<wL.length; jj++) {
                                if (!providedTwoMaps && jj>0) continue;
                                w = wL[jj];
                                line = mapForPrint+String.format(",%d,%d,", MAP_COMPOSING_ROWS,MAP_COMPOSING_COLS);
                                line = line.concat(String.format("%d", xstart)).concat(",");
                                line = line.concat(String.format("%d", ystart)).concat(",");
                                if (!is2Dmap) line = line.concat(String.format("%d", zstart)).concat(",");
                                line = line.concat(String.format("%d", xend)).concat(",");
                                line = line.concat(String.format("%d", yend)).concat(",");
                                if (!is2Dmap) line = line.concat(String.format("%d", zend)).concat(",");
                                if (providedTwoMaps) {
                                    line = line.concat(String.format("%.3f", w)).concat(",");
                                    line = line.concat(String.format("%.3f", 1.0)).concat(",");
                                    line = line.concat(String.format("%.4f", br)).concat(",");
                                    if (jj==0)      line = line.concat(bArray).concat("\n");
                                    else            line = line.concat("sameBorder").concat("\n");
                                } else
                                    line = line.concat(String.format("%.3f", w)).concat("\n");
                                myWriter.write(line);
                            }
                        }
                    }
                    myWriter.close();
                    System.out.println("*** All Instances saved to file !");
                } catch (IOException e) {
                    System.out.println("A file write error occurred.");
                }
            }            
            // ===============================================================
            
            
            if (event.getSource() == runAllFromFile){
                int[] coords = new int[6];
                double wWest, wEast, bordR;
                int N=0;
                String[] words;
                String line;
                BufferedReader br;
                String mapForPrint, mapfN, metricType;
                
                metricType = getSelectedWestHeuristic();
                runFromFileStart = Integer.parseInt(startFileIterationField.getText());
                
                mapForPrint = UtilityFunc.extractMapNames(mapsName);   //  "Berlin_0_1024.map|Denver_2_1024.map"
                //System.out.println("Extract Map Names: " + mapForPrint);
                mapfN = UtilityFunc.mapFilesToConvention(mapForPrint); //  "Berlin_0_1024~map+Denver_2_1024~map.csv"
                System.out.println("Convention Map Names: " + mapfN);
                String s1 = UtilityFunc.conventionToMapFiles(mapfN);   //  "Berlin_0_1024.map|Boston_1_1024.map"
                System.out.println("Un-Convention Map Names: " + s1);
                String s2 = UtilityFunc.compactMapNames(s1);           //  "../Maps/Berlin_0_1024.map|../Maps/Boston_1_1024.map"
                System.out.println("Compact Map Names: " + s2);
                bordR = 0.0;
                
                try{
                    
                    br = new BufferedReader(new FileReader(mapfN));
                    //if (is2Dmap) br = new BufferedReader(new FileReader("map2DInstances.csv"));
                    //else br = new BufferedReader(new FileReader("map3DInstances.csv"));
                    line = UtilityFunc.readLine(br); // first line, the header line
                    N = runFromFileStart - 1;
                    while (N > 0  && line != null) {
                        line = UtilityFunc.readLine(br);
                        N--;
                    }
                    N = runFromFileStart - 1;
                    //UtilityFunc.warmupCPU();          //    ---====   C P U    W A R M U P    ====---
                    while ((line = UtilityFunc.readLine(br)) != null) {
                        words = line.split(",");
                        
                        MAP_COMPOSING_ROWS = Integer.parseInt(words[1]);
                        MAP_COMPOSING_COLS = Integer.parseInt(words[2]);
                        // start locations
                        if (is2Dmap) {
                            coords[0] = Integer.parseInt(words[3]);
                            coords[1] = Integer.parseInt(words[4]);
                            coords[2] = Integer.parseInt(words[5]);
                            coords[3] = Integer.parseInt(words[6]);
                            
                        } else {
                            coords[0] = Integer.parseInt(words[3]);
                            coords[1] = Integer.parseInt(words[4]);
                            coords[2] = Integer.parseInt(words[5]);
                            coords[3] = Integer.parseInt(words[6]);
                            coords[4] = Integer.parseInt(words[7]);
                            coords[5] = Integer.parseInt(words[8]);
                        }
                        setStarts(coords);
                        
                        // weight ration
                        if (is2Dmap) {
                            wWest = Double.parseDouble(words[7]);
                            if (providedTwoMaps) {
                                wEast = Double.parseDouble(words[8]);
                                bordR = Double.parseDouble(words[9]);
                            } else wEast = wWest;
                        } else {
                            wWest = Double.parseDouble(words[9]);
                            if (providedTwoMaps) {
                                wEast = Double.parseDouble(words[10]);
                                bordR = Double.parseDouble(words[11]);
                            } else wEast = wWest;
                        }
                        
                        // start map
                        //resetMap();
                        map = new SEARCH_Map(mapsName, providedTwoMaps, is2Dmap); 
                        map.runMap(MAP_COMPOSING_ROWS,MAP_COMPOSING_COLS);
                        map.borderRate = bordR;
                        
                        String bStr;
                        if (is2Dmap) {
                            if (providedTwoMaps) {
                                map.solderMapsToWestNoBorder2D();
                                bStr = words[10];
                            } else bStr = "single";
                        }
                        else {
                            if (providedTwoMaps) {
                                map.solderMapsToWestNoBorder3D();
                                bStr = words[12];
                            } else bStr = "single";
                        }
                        if (bStr.equals("empty")) continue;  // no borders at all, continue to next iteration
                        if (!bStr.startsWith("sameBorder")) {
                            if (providedTwoMaps) {
                                parseBordersLocationsSetBorder(bStr);
                                saveBorderLocations();
                            }
                        } else {
                            if (providedTwoMaps) restoreBorderLocatoins();
                        } //System.out.println("using same borders ;  key=" + bStr);
                        
                        // start runs for each algorithm
                        N++;
                        if (is2Dmap) {
                            System.out.println( "Run iteration: " + N + " ;   West Weight: " + wWest + " ;   Border Rate: " + bordR +
                                                " ;   Start: ("+xstart+","+ystart+")   ;   Goal: ("+xend+","+yend+")");
                            
                        } else {
                            System.out.println( "Run iteration: " + N + " ;   West Weight: " + wWest + " ;   Border Rate: " + bordR +
                                                " ;   Start: ("+xstart+","+ystart+","+zstart+")   ;   Goal: ("+xend+","+yend+","+zend+")");
                        }
                        searchMethodsDict = new HashMap<>();
                        reInstantiateSearchObjects();
                        for (int i=0; i<SEARCH_str.length && !SEARCH_str[i].equals(""); i++) {
                            softClearSettings();
                            if (providedTwoMaps) restoreBorderLocatoins();
                            searchMethod = searchMethodsDict.get(SEARCH_str[i]);
                            searchMethod.statsFname = searchMethod.fname3DMarathon;
                            searchMethod.setMapRowsCols(MAP_COMPOSING_ROWS, MAP_COMPOSING_COLS);
                            searchMethod.updateInit(metricType, wWest, wEast);
                            searchMethod.isFMMmarathon = false;
                            searchMethod.setSearchObjects(startF, startB);
                            pathN = searchMethod.findPath(startF, startB);
                            searchMethod = null;
                            searchMethodsDict.put(SEARCH_str[i], null);
                            map.deleteMapObj();
                        }
                    }
                }
                catch (FileNotFoundException ex){
                    System.out.println("Unable to open run instances file " + mapfN + " : " + ex);  
                } 
                System.out.println("*** All Run from File loops completed !");
            }
            
            
            
            
            //============================================================================================
            // =================  create random map save it and create instances file ====================
//            if (event.getSource() == saveInstancesToFileWithWalls2){
//                int repeatLoops, maxB=0;
//                //double[]    wL      = {4.0}; //{ 1.0, 2.0, 4.0, 6.0, 8.0, 10.0 };
//                double[]    wL      = { 2.0, 1.0, 4.0, 6.0, 8.0, 10.0 };
//                double      w;
//                //double[]    brL     = { 0.05, 0.1, 0.2, 0.4, 0.7, 1.0 };
//                //double[]    brL     = { 1.0, 0.7, 0.4, 0.2, 0.1, 0.05}; 
//                double[]    brL     = { 0.2 }; 
//                double      br;
//                double randStartGoal = 0.85;
//                FileWriter myWriter;
//                String line, bArray, header="", mapN, wArray;
//                String[] mapsN;
//                List<Coord3D> cList;
//                String filename;
//
//                if (is2Dmap)
//                    header = header.concat("mapName,startX,startY,goalX,goalY,wegithWest,weightEast,borderRate,mapRandomWall,bordersArray\n");
//                else
//                    header = header.concat("mapName,startX,startY,startZ,goalX,goalY,goalZ,wegithWest,weightEast,borderRate,mapRandomWall,bordersArray\n");
//                mapsN = mapsName.split("[/:]");
//                mapN = mapsN[2].trim();
//                try {
//                    myWriter = new FileWriter("map3DInstances.csv", false);
//                    myWriter.write(header);
//
//                    repeatLoops = Integer.parseInt(runLoopField.getText());
//
//                    resetMap();
//                    map.runMap();
//                    map.addRandomWalls(0.10);
//                    map.createNewMapFile("_10percent");
//                    if (is2Dmap) filename = "Map2DRandomWall_10percent.dat";
//                    else filename = "Map3DRandomWall_10percent.dat";
//                    map.saveMapToFile(filename);
//
//                    // saves problem instances to file for future use if needed
//                    for (int k=0; k < repeatLoops; k++) { // outer repeat loop, divided by 2
//                        resetMap();
//                        map.runMap();
//                        map.readMapFromFile(filename);
//                        randomizeStartGoal(randStartGoal);
//                        for (int ii=0; ii<brL.length; ii++) {
//                            br = brL[ii];
//                            if (ii==0) {
//                                if (is2Dmap) maxB = map.solderMapsToWest2D(br);
//                                else maxB = map.solderMapsToWest3D(br);
//                                saveBorderLocations(); // to create the coordinates list object and initialize with borders
//                            }
//                            else                reduceBordersTo(maxB, br);
//                            List<NodeInterface> bordersList = map.getBorderNodes();
//                            bArray = "";
//                            for (int t=0; t<bordersList.size(); t++) {
//                                NodeInterface n = bordersList.get(t);
//                                if (is2Dmap) {
//                                    bArray = bArray.concat(String.format("%d", n.coord.x)).concat(":");
//                                    bArray = bArray.concat(String.format("%d", n.coord.y));
//                                } else {
//                                    bArray = bArray.concat(String.format("%d", n.coord.x)).concat(":");
//                                    bArray = bArray.concat(String.format("%d", n.coord.y)).concat(":");
//                                    bArray = bArray.concat(String.format("%d", n.coord.z));
//                                }
//                                if (t<(bordersList.size()-1)) bArray = bArray.concat("|");
//                            }
//                            System.out.println("saving:  outer iteration "+(k+1)+" out of: "+ repeatLoops +
//                                               " ;  border density iteration: "+(ii+1)+" out of "+brL.length + 
//                                               " ;  border nodes count: " + bordersList.size());
//                            for (int jj=0; jj<wL.length; jj++) {
//                                w = wL[jj];
//                                line = mapN.concat(",");
//                                line = line.concat(String.format("%d", xstart)).concat(",");
//                                line = line.concat(String.format("%d", ystart)).concat(",");
//                                if (!is2Dmap) line = line.concat(String.format("%d", zstart)).concat(",");
//                                line = line.concat(String.format("%d", xend)).concat(",");
//                                line = line.concat(String.format("%d", yend)).concat(",");
//                                if (!is2Dmap) line = line.concat(String.format("%d", zend)).concat(",");
//                                line = line.concat(String.format("%.1f", w)).concat(",");
//                                line = line.concat(String.format("%.1f", 1.0)).concat(",");
//                                line = line.concat(String.format("%.2f", br)).concat(",");
//                                if (jj==0)      line = line.concat(filename).concat(",");
//                                else            line = line.concat("sameMapWalls").concat(",");
//                                if (jj==0)      line = line.concat(bArray).concat("\n");
//                                else            line = line.concat("sameBorder").concat("\n");
//
//                                myWriter.write(line);
//                            }
//                        }
//                    }
//                    myWriter.close();
//                    System.out.println("*** All Instances saved to file !");
//                } catch (IOException e) {
//                    System.out.println("A file write error occurred.");
//                }
//            }
            // =================  create random map save it and create instances file ====================
            //============================================================================================
        }
    }
}
