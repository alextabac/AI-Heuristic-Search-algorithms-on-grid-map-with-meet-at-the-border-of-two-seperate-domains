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

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JTextField;

/**
 *
 * @author atabacar
 */
public class AnimGui extends JFrame {
    private AnimGui self;
    private int MAP_COMPOSING_ROWS = 1;
    private int MAP_COMPOSING_COLS = 1;
    private Thread searchThread;
    SEARCH_BASE searchMethod;
    protected String[] SEARCH_str;  // first row of buttons, the algorithms without animation
    protected String[] SEARCH_str1; // second row of buttons, the algorithms with animation
    protected String mapsName;
    protected Map<String, SEARCH_BASE> searchMethodsDict;
        
    // Grid objects (matrix)
    private DrawMatrix matrixPanel;
    private final SEARCH_Map mapDemo;
    
    // Grapich objects
    private final JButton clearButton;
    private final JButton stopAnimationButton;
    protected List<JButton> searchButtonsList;
    protected List<JButton> searchButtonsList1;
    private final JButton solderBordersButton;
    private final JButton solderOpenButton;
    private final JButton hEuclideanButton;
    private final JButton hDiscrete3Button;
    private final JButton hMaxAxisButton;
    private final JComboBox randBordersSelect;
    private final JComboBox westHeuristicSelect;
    private final JComboBox eastHeuristicSelect;
    private final JComboBox westFactorSelect;
    private final JComboBox eastFactorSelect;
    private final JLabel gridLabel;    
    private final JLabel timerLabel;
    private final JLabel runLoopLabel;
    private final JLabel statusBar;
    private final JPanel buttonPanel;
    private final JPanel buttonPanel1;
    private final JPanel innerButtonPanel;
    private final JPanel innerButtonPanel1;
    private final JPanel innerButtonPanel2;
    private final JScrollPane scrollMatrixPanel;
    private final JTextField timerTimeField;
    private final JTextField runLoopField;
    private final JTextField cellSizeField;
    
    // Constants
    private final String METRIC_STRING = "Type: f =";    
    private final String STATUS_LABEL = "Cursor Grid: ";
    private final String[] heuristicStr = {"DiscreetK3", "Euclidean" , "Manhattan2", "Manhattan1","f=XDP" , "f=XUP"};
    private final Double[] factosStr = {1.0, 0.5, 1.5, 2.0, 3.0, 5.0, 50.0};
    private final String[] randBordersStr = {"WEST-EAST", "10 Random Same", "20 Random Same", "50 Random Same", 
                                                          "100 Random Same", "200 Random Same", "500 Random Same",
                                                          "10 Random", "20 Random", "50 Random", 
                                                          "100 Random", "200 Random", "500 Random"};
    
    private final int H_EUCLIDEAN = 1;
    private final int H_DISCRETE_K3 = 2;
    private final int H_MANHATTAN = 3;
    private int windowW = 1600; // window width
    private int windowH = 1040; // window height
    private final int matrixWidth;
    private final int matrixHeight;
    
    // int, long, double, boolean
    private int firstMapBorderClickX;
    private int firstMapBorderClickY;
    private int timerTime = 20;
    private int runLoopSet = 1;
    private int gridSize = 1;
    
    private int searchStartX;
    private int searchStartY;
    private int searchGoalX;
    private int searchGoalY;    
    private int hType;
    protected int pathSteps;
    protected long nExpanded;
    protected long nGenerated;
    protected long nReExpanded;
    protected double runTime;
    protected double pathLength;
    protected double pathMKSP;
    protected double BD = 0.1;
    private boolean stopAnimBool;
    private boolean isDual;
    private boolean is2D;
    
    // Nodes and List of nodes
    List<int[]> bordersCoordinates;
    protected List<NodeInterface> pathN;
    
    // methods and sub-classes
    // Constructor
    public AnimGui(String mapName){
        super("Map Matrix");
        
        this.self = this;
        this.is2D = true;
        this.mapsName = mapName;
        searchThread = null;
        
        isDual = false;
        
        // initial value is negative
        firstMapBorderClickX = -1;
        firstMapBorderClickY = -1;
        
        String[] tokens = mapName.split("[|]", 2);
        if (tokens.length == 2) { // Dual map selected
            isDual = true;
        }
        mapDemo = new SEARCH_Map(mapName, isDual, is2D);
        //mapDemo.runMap8();
        mapDemo.runMap(MAP_COMPOSING_ROWS,MAP_COMPOSING_COLS);
        matrixWidth = mapDemo.getMapWidth();
        matrixHeight = mapDemo.getMapHeight();
        gridSize = Math.max(Math.min((windowW - 50) / matrixWidth, (windowH - 200) / matrixHeight), gridSize);
        windowH = Math.min(gridSize*(matrixHeight+2) + 160, windowH);
        matrixPanel = new DrawMatrix(mapDemo, gridSize);
        
        
        searchMethodsDict = new HashMap<>();
        reInstantiateSearchObjects();
        
        
        stopAnimBool = false;
        cellSizeField = new JTextField();
        gridLabel = new JLabel("Grid Size [pixels]:");
        timerTimeField = new JTextField();
        runLoopField = new JTextField();
        timerLabel = new JLabel("Timer [ms]:");
        runLoopLabel = new JLabel("Loops:");
        timerTimeField.setSize(80, 40);
        runLoopField.setSize(80, 40);
        cellSizeField.setSize(80, 40);
        
        searchStartX = -1;
        searchStartY = -1;
        searchGoalX = -1;
        searchGoalY = -1;

        searchButtonsList = new ArrayList<>();
        for (int i=0; i<SEARCH_str.length; i++) {
            if (!"".equals(SEARCH_str[i]))
                searchButtonsList.add(new JButton(SEARCH_str[i]));    
        }

        searchButtonsList1 = new ArrayList<>();
        for (int i=0; i<SEARCH_str1.length; i++) {
            if (!"".equals(SEARCH_str1[i]))
                searchButtonsList1.add(new JButton(SEARCH_str1[i]));
        }
        
        
        solderBordersButton = new JButton("Solder Border");
        solderOpenButton = new JButton("Solder Open");
        
                
        clearButton = new JButton("Clear");
        stopAnimationButton = new JButton("Pause Anim");
        hEuclideanButton = new JButton("Euclid");
        hDiscrete3Button = new JButton("Discrete3");
        hMaxAxisButton = new JButton("Manhattan");
        
        statusBar = new JLabel();
        westHeuristicSelect = new JComboBox(heuristicStr);
        eastHeuristicSelect = new JComboBox(heuristicStr);
        westFactorSelect = new JComboBox(factosStr);
        eastFactorSelect = new JComboBox(factosStr);
        randBordersSelect = new JComboBox(randBordersStr);
        
        westHeuristicSelect.setSelectedIndex(0);
        eastHeuristicSelect.setSelectedIndex(0);
        westFactorSelect.setSelectedIndex(0);
        eastFactorSelect.setSelectedIndex(0);
        
        randBordersSelect.setSelectedIndex(0);
        
        statusBar.setText(STATUS_LABEL);
        innerButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        innerButtonPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));        
        innerButtonPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        //innerButtonPanel.add(uniDirLabel);
        
        for (int i=0; i<SEARCH_str.length; i++)
            if (!"".equals(SEARCH_str[i]))
                innerButtonPanel.add(searchButtonsList.get(i));
        
        for (int i=0; i<SEARCH_str1.length; i++)
            if (!"".equals(SEARCH_str1[i]))
                innerButtonPanel1.add(searchButtonsList1.get(i));
        
        //  LOWER BUTTONS PANEL
        innerButtonPanel2.add(gridLabel);
        innerButtonPanel2.add(cellSizeField);
        innerButtonPanel2.add(timerLabel);
        innerButtonPanel2.add(timerTimeField);
        innerButtonPanel2.add(runLoopLabel);
        innerButtonPanel2.add(runLoopField);
        innerButtonPanel2.add(stopAnimationButton);
        innerButtonPanel2.add(clearButton);
        innerButtonPanel2.add(westHeuristicSelect);
        innerButtonPanel2.add(westFactorSelect);        
        innerButtonPanel2.add(eastHeuristicSelect);
        innerButtonPanel2.add(eastFactorSelect);
        innerButtonPanel2.add(randBordersSelect);
        innerButtonPanel2.add(solderBordersButton);
        innerButtonPanel2.add(solderOpenButton);
        

        buttonPanel = new JPanel();
        buttonPanel1 = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel1.setLayout(new BorderLayout());
        buttonPanel1.add(innerButtonPanel, BorderLayout.NORTH);
        buttonPanel1.add(innerButtonPanel1, BorderLayout.CENTER);
        buttonPanel.add(buttonPanel1, BorderLayout.NORTH);
        buttonPanel.add(innerButtonPanel2, BorderLayout.CENTER);
        buttonPanel.add(statusBar, BorderLayout.SOUTH);
        
        
        /**
         * Panels:
         * scrollMatrixPanel        --> main map panel, CENTER
         * buttonPanel              --> main buttons panel , SOUTH
         * 
         *      buttonPanel:
         *      buttonPanel1        --> upper buttons , NORTH
         *      innerButtonPanel2   --> mid buttons , CENTER
         *      statusBar           --> lower status bar , SOUTH
         * 
         *      buttonPanel1:
         *      innerButtonPanel    --> upper buttons , NORTH
         *      innerButtonPanel1   --> upper second buttons , CENTER
         * 
         *  ACTUAL ORDER of PANELS:
         *          innerButtonPanel
         *          innerButtonPanel1
         *          innerButtonPanel2
         *          statusBar
        */
        
        
        
        // adds action listeners to buttons
        
        ActionHandler handler = new ActionHandler();
        
        clearButton.addActionListener(handler);
        
        for (int i=0; i<SEARCH_str.length; i++)
            if (!"".equals(SEARCH_str[i]))
                searchButtonsList.get(i).addActionListener(handler);
        
        for (int i=0; i<SEARCH_str1.length; i++)
            if (!"".equals(SEARCH_str1[i]))
                searchButtonsList1.get(i).addActionListener(handler);

        solderBordersButton.addActionListener(handler);
        solderOpenButton.addActionListener(handler);
        stopAnimationButton.addActionListener(handler);
        hEuclideanButton.addActionListener(handler);
        hDiscrete3Button.addActionListener(handler);
        hMaxAxisButton.addActionListener(handler);
        cellSizeField.addActionListener(handler);
        timerTimeField.addActionListener(handler);
        runLoopField.addActionListener(handler);
        westHeuristicSelect.addActionListener(handler);
        eastHeuristicSelect.addActionListener(handler);
        westFactorSelect.addActionListener(handler);
        eastFactorSelect.addActionListener(handler);
        randBordersSelect.addActionListener(handler);
        
        this.setTitle("Grid Map Search Demonstration: " + mapName);
        this.setSize(windowW, windowH); // frame size 
        this.setLayout(new BorderLayout());
        
        scrollMatrixPanel = new JScrollPane(matrixPanel);
        scrollMatrixPanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollMatrixPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        this.add(scrollMatrixPanel, BorderLayout.CENTER);
        this.add(buttonPanel, BorderLayout.SOUTH);
        
        
        this.getRootPane().setDefaultButton(hEuclideanButton);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
      
        MouseHandler mouseHandler = new MouseHandler();
        matrixPanel.addMouseListener(mouseHandler);
        matrixPanel.addMouseMotionListener(mouseHandler);
        
        pathSteps = 0;
        pathLength = 0;
        pathMKSP = 0;
        hType = H_EUCLIDEAN;
        addListeners();
        
        hEuclideanButton.requestFocus();
        cellSizeField.setText(Integer.toString(gridSize));
        timerTimeField.setText(Integer.toString(timerTime));
        runLoopField.setText(Integer.toString(runLoopSet));
    }

    
    private void reInstantiateSearchObjects() {
        SEARCH_str = new String[50];
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
        String mapsString = UtilityFunc.getMapsNameForStats(mapsName);
        
        searchMethodsDict.put(SEARCH_str[j++],   new SEARCH_Dijkstras(true,             null,mapsString, mapDemo, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str[j++],       new SEARCH_AStar(true,  new   Heuristic_FE(),mapsString, mapDemo, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str[j++],       new SEARCH_AStar(true,  new  Heuristic_FBE(),mapsString, mapDemo, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str[j++],       new SEARCH_AStar(true,  new Heuristic_EFBE(),mapsString, mapDemo, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str[j++],          new SEARCH_MM(true,  new   Heuristic_FE(),mapsString, mapDemo, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str[j++],          new SEARCH_MM(true,  new  Heuristic_FBE(),mapsString, mapDemo, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str[j++],          new SEARCH_MM(true,  new Heuristic_EFBE(),mapsString, mapDemo, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str[j++],         new SEARCH_fMM(true,  new   Heuristic_FE(),mapsString, mapDemo, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str[j++],         new SEARCH_fMM(true,  new  Heuristic_FBE(),mapsString, mapDemo, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str[j++],         new SEARCH_fMM(true,  new Heuristic_EFBE(),mapsString, mapDemo, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str[j++],  new SEARCH_AStar_BPMX(true,  new   Heuristic_FE(),mapsString, mapDemo, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str[j++],  new SEARCH_AStar_BPMX(true,  new  Heuristic_FBE(),mapsString, mapDemo, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str[j++],  new SEARCH_AStar_BPMX(true,  new Heuristic_EFBE(),mapsString, mapDemo, heuristicStr[0], 1.0, 1.0));
        
        

        // animation version of search algorithms
        SEARCH_str1 = new String[50];
        for (int i=0; i<SEARCH_str1.length; i++)
            SEARCH_str1[i] = "";
        j=0;
        
        SEARCH_str1[j++] = "Dijkstra's_/";
        SEARCH_str1[j++] = "A*+FE_/";
        SEARCH_str1[j++] = "A*+FBE_/";
        SEARCH_str1[j++] = "A*+EFBE_/";
        SEARCH_str1[j++] = "MM+FE_/";
        SEARCH_str1[j++] = "MM+FBE_/";
        SEARCH_str1[j++] = "MM+EFBE_/";
        SEARCH_str1[j++] = "fMM+FE_/";
        SEARCH_str1[j++] = "fMM+FBE_/";
        SEARCH_str1[j++] = "fMM+EFBE_/";
        
        j=0;
        
        searchMethodsDict.put(SEARCH_str1[j++],   new SEARCH_Dijkstras_Anim(true,             null,mapsString, mapDemo, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str1[j++],       new SEARCH_AStar_Anim(true,    new Heuristic_FE(),mapsString, mapDemo, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str1[j++],       new SEARCH_AStar_Anim(true,   new Heuristic_FBE(),mapsString, mapDemo, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str1[j++],       new SEARCH_AStar_Anim(true,  new Heuristic_EFBE(),mapsString, mapDemo, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str1[j++],          new SEARCH_MM_Anim(true,    new Heuristic_FE(),mapsString, mapDemo, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str1[j++],          new SEARCH_MM_Anim(true,   new Heuristic_FBE(),mapsString, mapDemo, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str1[j++],          new SEARCH_MM_Anim(true,  new Heuristic_EFBE(),mapsString, mapDemo, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str1[j++],         new SEARCH_fMM_Anim(true,    new Heuristic_FE(),mapsString, mapDemo, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str1[j++],         new SEARCH_fMM_Anim(true,   new Heuristic_FBE(),mapsString, mapDemo, heuristicStr[0], 1.0, 1.0));
        searchMethodsDict.put(SEARCH_str1[j++],         new SEARCH_fMM_Anim(true,  new Heuristic_EFBE(),mapsString, mapDemo, heuristicStr[0], 1.0, 1.0));
        
    }
    
    
    private boolean isOutOfGrid(int x, int y){
        if (getGridX(x)==-1) return true;
        else if (getGridY(y)==-1) return true;
        else return false;
    }
    
    
    private int getGridX(int x){
        if (x >= (gridSize * matrixWidth))
            return -1;
        else
            return (int)(x/gridSize);
    }
    
    
    private int getGridY(int y){
        if (y >= (gridSize * matrixHeight))
            return -1;
        else
            return (int)(y/gridSize);
    }
    
    
    private String getHeuristicString(){
        switch (hType){
            case H_EUCLIDEAN: return "Euclidean";
            case H_DISCRETE_K3: return "Discrete k=3";
            case H_MANHATTAN: return "Manhattan";
        }
        return "Default";
    }    
    
    private String getSelectedWestHeuristic(){
        //{"Euclidean" , "Manhattan2", "DiscreetK3", "f=XDP" , "f=XUP"}
        if ("Euclidean".equals(this.westHeuristicSelect.getSelectedItem().toString()))
            return MetricInterface.EUCLID;
        if ("Manhattan".equals(this.westHeuristicSelect.getSelectedItem().toString()))
            return MetricInterface.MANHATTAN;
        if ("DiscreetK3".equals(this.westHeuristicSelect.getSelectedItem().toString()))
            return MetricInterface.DISCREETK3;
        
        // default
        return MetricInterface.EUCLID;        
    }

    private Double getSelectedWestFactor(){
        return Double.valueOf(this.westFactorSelect.getSelectedItem().toString());
    }

    private Double getSelectedEastFactor(){
        return Double.valueOf(this.eastFactorSelect.getSelectedItem().toString());
    }
    
    private String getSelectedEastHeuristic(){
        //{"Euclidean" , "Manhattan2", "DiscreetK3", "f=XDP" , "f=XUP"}
        if ("Euclidean".equals(this.eastHeuristicSelect.getSelectedItem().toString()))
            return MetricInterface.EUCLID;
        if ("Manhattan".equals(this.eastHeuristicSelect.getSelectedItem().toString()))
            return MetricInterface.MANHATTAN;
        if ("DiscreetK3".equals(this.eastHeuristicSelect.getSelectedItem().toString()))
            return MetricInterface.DISCREETK3;
        
        // default
        return MetricInterface.EUCLID;        
    }

    
    // -1 means WEST-EST
    // 10 to 500 means random same
    // 10010 to 10500 means random not-same
    private int getSelectedBorderGenerator(){
//        {"WEST-EAST", "10 Random Same", "20 Random Same", "50 Random Same", 
//                                                          "100 Random Same", "200 Random Same", "500 Random Same",
//                                                          "10 Random", "20 Random", "50 Random", 
//                                                          "100 Random", "200 Random", "500 Random"};

        if ( randBordersSelect.getSelectedItem().toString().startsWith("WEST") )
            return -1;
        
        String s = randBordersSelect.getSelectedItem().toString().replaceAll("[^0-9]", "");
        int n = Integer.parseInt(s);
        
        if (!randBordersSelect.getSelectedItem().toString().endsWith("Same"))
            n = n + 10000;
        return n;
    }
    
    private void stopAnimThread() {
        if (searchThread!=null && searchThread.isAlive()) {
            if (searchMethod != null) {searchMethod.stop();System.out.println("stopping thread.");}
            while (searchThread!=null && searchThread.isAlive()) { /* do nothing */ }
            searchThread = null;
            pathN = null;
        }        
    }
    
    protected void restoreBordersStatus22() {
        int num[];
        CoordInterface c;
        
        for (int i=0; i<bordersCoordinates.size(); i++) {
            num = bordersCoordinates.get(i);
            if (is2D) c = new Coord2D(num[0], num[1]);
            else      c = new Coord3D(num[0], num[1],num[2]);
            mapDemo.setOneBorder(c);
        }
    }
    

    protected void restoreBorderLocatoins() {
        CoordInterface c;
        int num[];
        
        for (int i=0; i<bordersCoordinates.size(); i++) {
            num = bordersCoordinates.get(i);
            c = new Coord2D(num[0], num[1]);
            mapDemo.setOneBorder(c);
            //mapDemo.setOneBorderOnOpposite(c);
        }
    }
    
    private void saveBorderLocations() {
        List<NodeInterface> bList;
        NodeInterface n;
        int num[];

        bordersCoordinates = new ArrayList<>();
        bList = mapDemo.getBorderNodes();
        for (int i=0; i < bList.size(); i++){
            n = bList.get(i);
            num = new int[2];
            num[0] = n.getCoord().getX();
            num[1] = n.getCoord().getY();
            bordersCoordinates.add(num);
        }
    }

    
//    private void saveOneBordersStatus22() {
//        List<Node> b;
//        Node n;
//        int num[];
//
//        bordersCoordinates = new ArrayList<>();
////        b = mapDemo.getBorderNodes();
////        for (int i=0; i < b.size(); i++){
////            n = b.get(i);
////            num = new int[2];
////            num[0] = n.x;
////            num[1] = n.y;
////            bordersCoordinates.add(num);
////        }
//    }
    
    private void softClearSettings(){
//        CoordInterface c;
//        if (firstMapBorderClickX >= 0 && firstMapBorderClickY >= 0) {
//            c = new Coord2D(firstMapBorderClickX, firstMapBorderClickY);
//            mapDemo.unsetBorderOnMap(c);
//            firstMapBorderClickX = -1;
//            firstMapBorderClickY = -1;
//            matrixPanel.repaint();
//        }
        
        //borders = mapDemo.getBorderNodes();
        //borders = new ArrayList<>();
        
        nExpanded = 0;
        nGenerated = 0;
        nReExpanded = 0;
        pathSteps = 0;
        pathLength = 0;
        pathMKSP = 0;
        runTime = 0;
        //searchStartX=1967; searchStartY=111; searchGoalX=388; searchGoalY=652;
        mapDemo.clearMapCellsResetBorders();
        updateStatusBar(searchStartX, searchStartY);
        matrixPanel.repaint();
    }
    
    
//    private void updateMetricType(){
//        if (pureHeuristic && backHfrontH)
//            aStarMetricTypeLabel.setText(METRIC_STRING + " f_h + b_h; ");
//        else if (pureHeuristic && !backHfrontH)
//            aStarMetricTypeLabel.setText(METRIC_STRING + " pure-h; ");
//        else
//            aStarMetricTypeLabel.setText(METRIC_STRING + " g+h; ");
//    }
            
    
    protected void updateStatusBar(int x, int y){
        statusBar.setText(STATUS_LABEL + "X=" +
                Integer.toString(getGridX(x)) + ", Y="+
                Integer.toString(getGridY(y)) + ";  " +
                "Start: (" + Integer.toString(getGridX(searchStartX)) + "," +
                             Integer.toString(getGridY(searchStartY)) + ") - " + 
                "Goal: (" +  Integer.toString(getGridX(searchGoalX)) + "," +
                             Integer.toString(getGridY(searchGoalY)) + ")" +
                ";   Heuristic: " + getHeuristicString() +
                ";   #expanded=" + Long.toString(nExpanded) +
                ";   #re-expanded=" + Long.toString(nReExpanded) +
                ";   #generated=" + Long.toString(nGenerated) +  
                ";   Path Steps=" + Integer.toString(pathSteps) +
                ";   Path Length=" + String.format("%.3f", pathLength) +
                ";   Path MKSP=" + String.format("%.3f", pathMKSP) +
                ";   Run Time=" + String.format("%.3f",runTime) + " [sec]"
            );
    }
    
    private void addListeners(){
        cellSizeField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                // clears the text when in focus if it contains the inital string
                if (cellSizeField.getText().equals(Integer.toString(gridSize)))
                    cellSizeField.setText(""); 
            }
            @Override
            public void focusLost(FocusEvent e) {
            }
        });
        
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
        timerTimeField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                // clears the text when in focus if it contains the inital string
                if (timerTimeField.getText().equals(Integer.toString(timerTime)))
                    timerTimeField.setText(""); 
            }
            @Override
            public void focusLost(FocusEvent e) {
            }
        });        
    }
    
    
    private class ActionHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event){
            // DUAL MAP SEARCH classes call - animation style - thread
            for (int i=0; i < searchButtonsList1.size(); i++) {
                if (event.getSource() == searchButtonsList1.get(i)) {
                    stopAnimThread();
                    
                    CoordInterface startN, goalN;
                    
                    //saveOneBordersStatus();   // checks actual border nodes on map
                    if (isDual) saveBorderLocations();
                    softClearSettings();
                    if (isDual) restoreBorderLocatoins();
                    reInstantiateSearchObjects();
                    pathN = null;
                    matrixPanel.repaint();
                    
                    searchMethod = searchMethodsDict.get(SEARCH_str1[i]);
                    searchMethod.updateInit(getSelectedWestHeuristic(), getSelectedWestFactor(), getSelectedEastFactor());
                    startN = new Coord2D(getGridX(searchStartX),getGridY(searchStartY));
                    goalN = new Coord2D(getGridX(searchGoalX), getGridY(searchGoalY));
                    searchMethod.setSearchObjects(startN, goalN);
                    searchMethod.setAnimObj(self, matrixPanel);
//                    searchMethod.fname2D = searchMethod.fname2DSingle;
//                    searchMethod.updateInit(getSelectedWestHeuristic(), getSelectedWestFactor(),
//                                            getSelectedEastHeuristic(), getSelectedEastFactor());
//                    
                    searchMethod.setAnimDelay(timerTime);
                    searchMethod.enable();
//                    //System.out.println("selected thread method !");
//                    searchMethod.setStartGoal(xstart, ystart, xend, yend);
                    searchThread = new Thread((Runnable) searchMethod);
                    searchThread.start(); // this will also take care for updating status bar and painting.
                    break;
                }
            }
            
            // DUAL MAP SEARCH classes call  -  SEARCHLOOP
            for (int i=0; i < searchButtonsList.size(); i++) {
                if (event.getSource() == searchButtonsList.get(i)) {
                    int runLoops;
                    double accuRunTime;
                    CoordInterface startN, goalN;
                    

                    stopAnimThread();
                    //restoreBorderLocatoins();
                    //saveOneBordersStatus();
                    runLoops = Integer.parseInt(runLoopField.getText());
                    //System.out.println("Loops value="+runLoops);
                    accuRunTime= 0.0;
                    //mapDemo.clearMapCellsResetBordersWalls();
                    //mapDemo.addRandomWalls(0.005);
                    
                    for (int jj=0; jj<runLoops; jj++) {
                        //mapDemo = new SEARCH_Map(mapName, isDual, is2D);
                        //mapDemo.runMap8();
                        //mapDemo.runMap(MAP_COMPOSING_ROWS,MAP_COMPOSING_COLS);
                        
                        reInstantiateSearchObjects();
                        softClearSettings();
                        if (isDual) restoreBorderLocatoins();
                        startN = new Coord2D(getGridX(searchStartX),getGridY(searchStartY));
                        goalN = new Coord2D(getGridX(searchGoalX), getGridY(searchGoalY));
                        searchMethod = searchMethodsDict.get(SEARCH_str[i]);
                        searchMethod.updateInit(getSelectedWestHeuristic(), getSelectedWestFactor(), getSelectedEastFactor());
                        searchMethod.setSearchObjects(startN, goalN);
                        long tStart = System.nanoTime();
                        pathN = searchMethod.findPath(startN, goalN);
                        long time = System.nanoTime() - tStart;
                        runTime = time / 1e9;
                        accuRunTime += runTime;
//                        searchMethod.fname2D = searchMethod.fname2DSingle;
//                        searchMethod.verbose = (jj == (runLoops-1));
//                        searchMethod.updateInit(getSelectedWestHeuristic(), getSelectedWestFactor(),
//                                                getSelectedEastHeuristic(), getSelectedEastFactor());
//                        long tStart = System.nanoTime();
//                        pathN = searchMethod.findPath(xstart, ystart, xend, yend);
//                        long time = System.nanoTime() - tStart;
//                        runTime = time / 1e9;
//                        accuRunTime += runTime;
                    } // end of run loop
                    
                    if (mapDemo.getOppMap() != null) mapDemo.addOppNodesToMap();
                    if (pathN != null) UtilityFunc.addPathToMapN(mapDemo, pathN);
                    else System.out.println("empty path !!!!");
                    
                    //for (BaseNode2D n: pathN) n.printNode();
                    nExpanded = searchMethod.nExpanded;
                    nGenerated = searchMethod.nGenerated;
                    nReExpanded = searchMethod.nReExpanded;
                    if (pathN != null){
                        pathSteps = pathN.size()-1;
                        pathLength = searchMethod.getPathTotalLength();
                        pathMKSP = searchMethod.getPathTotalMKSP();
                    }
                    else System.out.println("Path not found !");
                    updateStatusBar(searchStartX, searchStartY);
                    //restoreBordersStatus();
                    matrixPanel.repaint();
                    if (runLoops > 1) System.out.println("acucmulated run time:  loops="+runLoops+"; average run time="+
                                           String.format("%.4f",(double)(accuRunTime/runLoops)));
                    
                    break;
                }
            }
            
            
            if (event.getSource() == westHeuristicSelect){
                System.out.println("Heuristic Selected: " + westHeuristicSelect.getSelectedItem());
            }

            if (event.getSource() == eastHeuristicSelect){
                System.out.println("Heuristic Selected: " + eastHeuristicSelect.getSelectedItem());
            }

            if (event.getSource() == randBordersSelect){
                System.out.println("Border Generation Selected: " + randBordersSelect.getSelectedItem());
            }

            if (event.getSource() == stopAnimationButton){
                stopAnimThread();
                stopAnimBool = !stopAnimBool;
            }

            if (event.getSource() == hEuclideanButton){
                hType = H_EUCLIDEAN;
                updateStatusBar(searchStartX, searchStartY); 
            }

            if (event.getSource() == hDiscrete3Button){
                hType = H_DISCRETE_K3;
                updateStatusBar(searchStartX, searchStartY); 
            }

            if (event.getSource() == hMaxAxisButton){
                hType = H_MANHATTAN;
                updateStatusBar(searchStartX, searchStartY);
            }
            
            if (event.getSource() == timerTimeField){
                if (!timerTimeField.getText().equals(Integer.toString(timerTime))){
                    if (Integer.parseInt(timerTimeField.getText()) >= 0 &&
                        Integer.parseInt(timerTimeField.getText()) < 2000 ){
                        timerTime = Integer.parseInt(timerTimeField.getText());
                        if (searchMethod != null && searchThread!=null && searchThread.isAlive())
                            searchMethod.setAnimDelay(timerTime);
                        //aStarTimer.setDelay(timerTime);
                        //idaStarTimer.setDelay(timerTime);
                        matrixPanel.repaint();                        
                    }
                    else
                        timerTimeField.setText(Integer.toString(timerTime));
                    updateStatusBar(searchStartX, searchStartY);
                    matrixPanel.repaint();
                }
            }            
            
            if (event.getSource() == cellSizeField){
                if (!cellSizeField.getText().equals(Integer.toString(gridSize))){
                    int oldGS;
                    
                    oldGS = gridSize;
                    gridSize = Integer.parseInt(cellSizeField.getText());
                    matrixPanel.setGridSize(gridSize);
                    matrixPanel.eraseLine();
                    
                    double temp;
                    
                    temp = (double)searchStartX;
                    temp = temp * (double)gridSize / ((double)oldGS);
                    searchStartX = (int)temp;

                    temp = (double)searchStartY;
                    temp = temp * (double)gridSize / ((double)oldGS);
                    searchStartY = (int)temp;

                    temp = (double)searchGoalX;
                    temp = temp * (double)gridSize / ((double)oldGS);
                    searchGoalX = (int)temp;

                    temp = (double)searchGoalY;
                    temp = temp * (double)gridSize / ((double)oldGS);
                    searchGoalY = (int)temp;
                    
                    matrixPanel.addLine(searchStartX, searchStartY,
                                        searchGoalX, searchGoalY);
                    updateStatusBar(searchStartX, searchStartY);
                    matrixPanel.repaint();
                }
            }

            // if Clear buton clicked
            if (event.getSource() == clearButton){
                stopAnimThread();
                if (bordersCoordinates != null) saveBorderLocations();
                softClearSettings();
                if (bordersCoordinates != null) restoreBorderLocatoins();
                //clearSettings();
                updateStatusBar(searchStartX, searchStartY);
                matrixPanel.repaint();
            }
            
            // place solder borders, or in map style pattern walls, etc.
            if (event.getSource() == solderBordersButton){
                softClearSettings();
                stopAnimThread();
                if (is2D) mapDemo.solderMapsToWest2D(BD);
                else mapDemo.solderMapsToWest3D(BD);
                matrixPanel.repaint();
                saveBorderLocations();
            }

            if (event.getSource() == solderOpenButton){
                softClearSettings();
                stopAnimThread();
                if (is2D) mapDemo.solderOpenMapsToWest2D(BD);
                else mapDemo.solderMapsOpenToWest3D(BD);
                matrixPanel.repaint();
                saveBorderLocations();
            }
            
        }
    }
    
    
    // Mouse handler to allow mouse clicks on the matrix to toggle cell colors
    private class MouseHandler implements MouseListener, MouseMotionListener {
        // MouseListener event handlers
        // handle event when mouse released immediately after press
        
        @Override
        public void mouseClicked(MouseEvent event) {
            // if (event.getButton() ==  2)  // button 2 clicked
            CoordInterface c;
            switch (event.getButton()) {
                case MouseEvent.BUTTON1 -> { // left mouse click
                    // check if need to unset border from map (from previous click)
                    c = new Coord2D(firstMapBorderClickX, firstMapBorderClickY);
                    //if (firstMapBorderClickX >= 0 && firstMapBorderClickY >= 0)
                        //mapDemo.unsetBorderOnMap(c);
                    
                    firstMapBorderClickX = getGridX(event.getX());
                    firstMapBorderClickY = getGridY(event.getY());
                    //mapDemo.setBorderOnMap(c);
                }
                case MouseEvent.BUTTON3 -> {  // right mouse click
                    // first button has been clicked already, and marked the border node for first click
                    if (firstMapBorderClickX >= 0 && firstMapBorderClickY >= 0){
                        
                        // get the locaiton of this second click
                        int secondClickX = getGridX(event.getX());
                        int secondClickY = getGridY(event.getY());
                        c = new Coord2D(secondClickX, secondClickY);
                        //mapDemo.setBorderOnMap(c);
                        
                        // purpose: when expanding such a node that is a border on the map
                        //     and also has pointer that is not null (not visited before),
                        //     the search algo will take this node's parent node and generate them,
                        //     this is instead of generating its neighbours.
                        // connect the two border nodes (supposed to be one from each map)
                        // each defined with zero g and f because at the time it is expanded
                        // the g and f will be calculated from the node leading to it
                        // and the f will be calculated from the other (jumper) node graph heuristic
                        //mapDemo.setBorderJumper(firstMapBorderClickX,firstMapBorderClickY,secondClickX,secondClickY);
                        
                        // set the first click back to intial value, negative 1
                        firstMapBorderClickX = -1;
                        firstMapBorderClickY = -1;
                    }
                }
                case MouseEvent.BUTTON2 -> { // middle mouse button
                    //if (firstMapBorderClickX >= 0 && firstMapBorderClickY >= 0)
                    //    mapDemo.unsetBorderOnMap(firstMapBorderClickX, firstMapBorderClickY);
                    
                    firstMapBorderClickX = -1;
                    firstMapBorderClickY = -1;
                }
                case MouseEvent.NOBUTTON -> {
                }
                default -> {
                }
            }
                                
            System.out.println("X=" + Integer.toString(getGridX(event.getX())) + ",   " +
                               "Y=" + Integer.toString(getGridY(event.getY())));
            matrixPanel.repaint();            
        } 

        // handle event when mouse pressed
        @Override
        public void mousePressed(MouseEvent event) {
            if (!(isOutOfGrid(event.getX(),event.getY()))){
                searchStartX = event.getX();
                searchStartY = event.getY();
                searchGoalX = searchStartX;
                searchGoalY = searchStartY;
                pathSteps=0;
                pathLength=0;
                pathMKSP=0;
            }
            else{
                searchStartX = -1;
                searchStartY = -1;
                searchGoalX = -1;
                searchGoalY = -1;
                matrixPanel.eraseLine();
                matrixPanel.repaint();
            }
        }

        // handle event when mouse released 
        @Override
        public void mouseReleased(MouseEvent event) {
            if (!(isOutOfGrid(event.getX(),event.getY()))){
                searchGoalX = event.getX();
                searchGoalY = event.getY();
                pathSteps=0;
                pathLength=0;
                pathMKSP=0;
            }
            else{
                searchStartX = -1;
                searchStartY = -1;
                searchGoalX = -1;
                searchGoalY = -1;
                matrixPanel.eraseLine();
                matrixPanel.repaint();
            }
        }

        // handle event when mouse enters area
        @Override
        public void mouseEntered(MouseEvent event) {
        }

        // handle event when mouse exits area
        @Override
        public void mouseExited(MouseEvent event) {
        }

        // MouseMotionListener event handlers
        // handle event when user drags mouse with button pressed
        @Override
        public void mouseDragged(MouseEvent event) {
            //if (mapDemo.isMapCellUnpassable(getGridX(event.getX()), getGridY(event.getY())))
            if (!(isOutOfGrid(event.getX(),event.getY()))){
                if(searchStartX != -1){
                    searchGoalX = event.getX();
                    searchGoalY = event.getY();
                    matrixPanel.addLine(searchStartX, searchStartY,
                                        searchGoalX, searchGoalY );

                    updateStatusBar(event.getX(), event.getY());
                    matrixPanel.repaint();
                }
            }            
        } 

        // handle event when user moves mouse
        @Override
        public void mouseMoved(MouseEvent event) {
            updateStatusBar(event.getX(), event.getY());
        } 
    } // end inner class MouseHandler
    
}

