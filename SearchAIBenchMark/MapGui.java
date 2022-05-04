/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchaibenchmark;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JSplitPane;
import javax.swing.JButton;
import javax.swing.Timer;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseEvent;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.JTextField;

/**
 *
 * @author atabacar
 */
public class MapGui extends JFrame {
    
    // Grid objects (matrix)
    private DrawMatrix matrixPanel;
    private final MapDemo mapDemo;
    private byte[][] maze;
    
    // timer listeners for animation
    private Timer idaStarTimer;
    private Timer aStarTimer;   
    
    // Grapich objects
    private final JSplitPane splitPane;  // split the window in top and bottom
    private final JButton idaAStarButton;
    private final JButton idaStarAnimButton;
    private final JButton aStarAnimButton;
    private final JButton clearButton;
    private final JButton stopAnimationButton;
    private final JButton aStarButton;
    private final JButton aStarGFButton;
    private final JButton aStarHButton;
    private final JButton aStarHHButton;
    private final JButton hEuclideanButton;
    private final JButton hDiscrete3Button;
    private final JButton hMaxAxisButton;
    private final JLabel gridLabel;    
    private final JLabel timerLabel;
    private final JLabel aStarMetricTypeLabel;
    private final JLabel statusBar;
    private final JPanel buttonPanel;
    private final JPanel innerButtonPanel;
    private final JPanel innerButtonPanel2;
    private final JTextField timerTimeField;
    private final JTextField cellSizeField;
    
    // Constants
    private final String METRIC_STRING = "Type: f =";    
    private final String STATUS_LABEL = "Cursor Grid: ";
    private final int H_EUCLIDEAN = 1;
    private final int H_DISCRETE_K3 = 2;
    private final int H_MANHATAN = 3;
    private final int windowW = 1300; // window width
    private final int windowH = 1100; // window height
    private final int splitPaneSize = 140;
    private final int matrixWidth;
    private final int matrixHeight;
    private final double sqrt2 = Math.sqrt(2.0);
    
    // int, long, double, boolean
    private int timerTime = 2;
    private int gridSize = 2;
    private int searchStartX;
    private int searchStartY;
    private int searchGoalX;
    private int searchGoalY;    
    private int tempX, tempY;
    private int xstart;
    private int ystart;
    private int xend, yend;
    private int hType;
    private int pathSteps;
    private long nOpens;
    private long idaStarN;
    private double runTime;
    private double pathLength;
    private double idaStarBound;
    private double idaStarG;
    private double idaStarD;
    private double idaStarMin;
    private double idaStarT;
    private boolean pureHeuristic;
    private boolean backHfrontH;
    private boolean stopAnimBool;
    private boolean idaStarNotFound;
    private boolean idaStarFound;
    
    // Nodes and List of nodes
    private List<Node> open;
    private List<Node> closed;
    private List<Node> path;
    private List<Node> pathTemp;
    private List<Node> idaStarNodeArr;
    private List<Node> idaStarNodeArr2;
    private List<Double> idaStarGArr;
    private List<Double> idaStarBoundArr;
    private Node now;
    private Node start;
    private Node end;
    private Node tempNode;
    private Node tempNode2;
    
    // all possible neighbors - K=3:
    // {upper-left corner, up, upper-right corner, right, lower-right corner, down, lower-left corner, left}
    private final int metricNeighborN = 8;
    private final double[] metricNeighborArray = {sqrt2,1.0,sqrt2,1.0,sqrt2,1.0,sqrt2,1.0};
    private final int[] metricNeighborX = {-1,0,1,1,1,0,-1,-1};
    private final int[] metricNeighborY = {-1,-1,-1,0,1,1,1,0};
    
    
    
    
    // methods and sub-classes
    // Constructor
    public MapGui(String mapName){
        super("Map Matrix");
        stopAnimBool = false;
        pureHeuristic = false;
        backHfrontH = false;
        splitPane = new JSplitPane(); // split pane for our window
        cellSizeField = new JTextField();
        gridLabel = new JLabel("Grid Size [pixels]:");
        aStarMetricTypeLabel = new JLabel(METRIC_STRING);
        
        timerTimeField = new JTextField();
        timerLabel = new JLabel("Timer [ms]:");
        timerTimeField.setSize(80, 40);
        
        searchStartX = -1;
        searchStartY = -1;
        searchGoalX = -1;
        searchGoalY = -1;
        mapDemo = new MapDemo(mapName);
        mapDemo.runMap();
        matrixWidth = mapDemo.getMapWidth();
        matrixHeight = mapDemo.getMapHeight();
        gridSize = Math.max(Math.min((windowW - 50) / matrixWidth, (windowH - splitPaneSize - 50) / matrixHeight), gridSize);
        matrixPanel = new DrawMatrix(mapDemo, gridSize);
        idaAStarButton = new JButton("IDA*");
        aStarAnimButton = new JButton("A* Anim");
        idaStarAnimButton = new JButton("IDA* Anim");
        aStarButton = new JButton("A*");
        aStarGFButton = new JButton("g+h A*");
        aStarHButton = new JButton("Pure-h A*");
        aStarHHButton = new JButton("h+h A*");
        clearButton = new JButton("Clear");
        hEuclideanButton = new JButton("h_Euclid");
        hDiscrete3Button = new JButton("h_Discrete3");
        stopAnimationButton = new JButton("Toggle Anim");
        hMaxAxisButton = new JButton("h_Manhatan");
        statusBar = new JLabel();
        
        statusBar.setText(STATUS_LABEL);
        innerButtonPanel = new JPanel(new FlowLayout());
        innerButtonPanel2 = new JPanel(new FlowLayout());
        
        innerButtonPanel.add(aStarAnimButton);
        innerButtonPanel.add(aStarButton);
        innerButtonPanel.add(idaAStarButton);
        innerButtonPanel.add(idaStarAnimButton);
        innerButtonPanel.add(hEuclideanButton);
        innerButtonPanel.add(hDiscrete3Button);
        innerButtonPanel.add(hMaxAxisButton);
        innerButtonPanel.add(clearButton);
        
        innerButtonPanel2.add(gridLabel);
        innerButtonPanel2.add(cellSizeField);
        innerButtonPanel2.add(stopAnimationButton);
        innerButtonPanel2.add(aStarGFButton);
        innerButtonPanel2.add(aStarHButton);
        innerButtonPanel2.add(aStarHHButton);
        innerButtonPanel2.add(aStarMetricTypeLabel);
        innerButtonPanel2.add(timerLabel);
        innerButtonPanel2.add(timerTimeField);

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.add(innerButtonPanel, BorderLayout.NORTH);
        buttonPanel.add(innerButtonPanel2, BorderLayout.CENTER);
        buttonPanel.add(statusBar, BorderLayout.SOUTH);
        
        // adds action listeners to buttons
        
        ActionHandler handler = new ActionHandler();
        
        aStarAnimButton.addActionListener(handler);
        clearButton.addActionListener(handler);
        aStarButton.addActionListener(handler);
        aStarGFButton.addActionListener(handler);
        aStarHButton.addActionListener(handler);
        aStarHHButton.addActionListener(handler);
        idaAStarButton.addActionListener(handler);
        idaStarAnimButton.addActionListener(handler);
        stopAnimationButton.addActionListener(handler);
        hEuclideanButton.addActionListener(handler);
        hDiscrete3Button.addActionListener(handler);
        hMaxAxisButton.addActionListener(handler);
        cellSizeField.addActionListener(handler);
        timerTimeField.addActionListener(handler);
        
        //this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("Grid Map Search Demonstration: " + mapName);
        this.setSize(windowW, windowH); // frame size 
        this.setLayout(new GridLayout());
        this.add(splitPane);
        
        
        // split pane setup
        splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(windowH - splitPaneSize); // last 120 pixels are for the buttons
        splitPane.setTopComponent(matrixPanel); 
        splitPane.setBottomComponent(buttonPanel);
        
        this.getRootPane().setDefaultButton(hEuclideanButton);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
      
        MouseHandler mouseHandler = new MouseHandler();
        matrixPanel.addMouseListener(mouseHandler);
        matrixPanel.addMouseMotionListener(mouseHandler);
        
        this.open = new ArrayList<>();
        this.closed = new ArrayList<>();
        this.path = new ArrayList<>();
        this.xstart = 0;
        this.ystart = 0;
        this.now = null;
        this.maze = null;
        nOpens = 0;
        pathSteps = 0;
        pathLength = 0;
        hType = H_EUCLIDEAN;
        updateMetricType();        
        addListeners();
        
        hEuclideanButton.requestFocus();
        cellSizeField.setText(Integer.toString(gridSize));
        timerTimeField.setText(Integer.toString(timerTime));
    }

    
    private String getNodeArrString(List<Node> aList){
        String str;
        
        str = "{";
        for (int i=0; i<aList.size(); i++)
            str = str.concat(aList.get(i).toString() + ", ");
        
        str = str.concat("}");
        return str;
    }
    
    
    private void plotPathOnMap(List<Node> aList){
        for (int i=0; i<aList.size(); i++)
            mapDemo.setMapCellPath(aList.get(i).x, aList.get(i).y);
    }
    
    
    public List<Node> findPathToStepInto() {
        if (this.now.x != this.xend || this.now.y != this.yend) {
            if (this.open.isEmpty()) { // Nothing to examine
                return null;
            }
            
            this.now = this.open.get(0); // get first node (lowest f score)
            this.open.remove(0); // removes node from open list
            this.closed.add(this.now); // and add to the closed
            mapDemo.setMapCellClosed(now.x, now.y);
            addNeigborsToOpenList();
        }
        if (this.now.x == this.xend && this.now.y == this.yend) {
            prepareFoundPath(now);
            return this.path;
        }
        else return null;
    }    

    
    private void reversePath(){
        idaStarNodeArr2 = new ArrayList<>();
        
        idaStarNodeArr2.add(0, path.remove(0));
        while (path.size() > 0){
            path.get(0).parent = idaStarNodeArr2.get(0);
            idaStarNodeArr2.add(0, path.remove(0));
        }
        path = idaStarNodeArr2;
    }
    
    
    private void prepareFoundPath(Node endNode){
        this.path.add(0, endNode);
        while (endNode.x != this.xstart || endNode.y != this.ystart) {
            endNode = endNode.parent;
            this.path.add(0, endNode);
        }
    }
    
    
    public List<Node> findPathTo(int xend, int yend) {
        this.xend = xend;
        this.yend = yend;
        this.closed.add(this.now);
        addNeigborsToOpenList();
        while (this.now.x != this.xend || this.now.y != this.yend) {
            if (this.open.isEmpty()) return null;
            
            this.now = this.open.get(0); // get first node (lowest f score)
            this.open.remove(0); // remove it
            this.closed.add(this.now); // and add to the closed
            mapDemo.setMapCellClosed(now.x, now.y);
            addNeigborsToOpenList();
        }
        prepareFoundPath(now);
        return this.path;
    }
    
    
    private void idaStarLoop(){
        idaStarBound = hFunc(xstart, ystart);
        System.out.println("starting bound: " + Double.toString(idaStarBound));

        if (pureHeuristic && backHfrontH)
            start = new Node(null, xstart, ystart, 0, idaStarBound, hFunc2(xstart, ystart), Node.H_FRONT_PLUS_H_BACK);
        else if (pureHeuristic && !backHfrontH)
            start = new Node(null, xstart, ystart, 0, idaStarBound, 0, Node.PURE_HEURISTIC);
        else
            start = new Node(null, xstart, ystart, 0, idaStarBound, 0, Node.G_PLUS_H);

        start.idGoup = 0L;
        tempNode = start;
        tempNode2 = null;
        nOpens = 0L;
        idaStarMin = Double.MAX_VALUE;
        System.out.println("starting IDA* Search-Loop");
        idaStarLoopSearch();
    }
    
    
    private void idaStarLoopSearch() {
        double f;
        long i;
        List<Node> aList;

        while (!idaStarNotFound && !idaStarFound){
            idaStarN++;
            f = tempNode.g + getH(tempNode);
            if (f <= idaStarBound){
                path.add(0, tempNode);
                //mapDemo.setMapCellPath(tempNode.x, tempNode.y);
                if (tempNode.x == xend && tempNode.y == yend) idaStarFound = true;
                aList = addNeigborsToIdaStarList(tempNode);
                if (!aList.isEmpty()) idaStarNodeArr.addAll(0, addNeigborsToIdaStarList(tempNode));
            }
            else if (f < idaStarMin) idaStarMin = f;

            if (!idaStarFound){
                if (!idaStarNodeArr.isEmpty()) {
                    i = idaStarNodeArr.get(0).idGoup;
                    do{
                        tempNode = idaStarNodeArr.remove(0);
                        f = tempNode.g + getH(tempNode);
                        if (f > idaStarBound && f < idaStarMin) idaStarMin = f;
                        /*if (path.get(0).idGoup == tempNode.idGoup || i != tempNode.idGoup){
                            i = tempNode.idGoup;
                            tempNode2 = path.remove(0);
                            mapDemo.setMapCellTerrain(tempNode2.x, tempNode2.y);                                
                        }*/
                    } 
                    while(f > idaStarBound && !idaStarNodeArr.isEmpty());
                    
                    while (!path.isEmpty() && path.get(0).idGoup >= tempNode.idGoup){
                        tempNode2 = path.remove(0);
                        mapDemo.setMapCellTerrain(tempNode2.x, tempNode2.y);                                
                    }
                }
                else{
                    if (idaStarMin == Double.MAX_VALUE) idaStarNotFound = true;
                    else {
                        idaStarBound = idaStarMin;
                        System.out.println("Bound increased: " + String.format("%.9f", idaStarBound));
                        idaStarMin = Double.MAX_VALUE;
                        //mapDemo.readMap();
                        path = new ArrayList<>();
                        tempNode = start;
                    }
                }
            }
        }

        end = tempNode;
        if (idaStarFound) prepareFoundPath();        
        else nullUnfoundPath();
        updateStatusBar(searchStartX, searchStartY);            
        matrixPanel.repaint();      
    }
    
    private void nullUnfoundPath(){
        System.out.println("IDA* animation NOT path found !");
        path = null;        
    }
    
    private void prepareFoundPath(){
        System.out.println("IDA* animation path found !");
        reversePath();
        mapDemo.readMap();
        pathSteps = addPathToMap(path);
    }    
    
    private double getH(Node nod){
        if (pureHeuristic && backHfrontH)
            return nod.h + nod.h2;
        return nod.h;
    }
    
    private ArrayList<Node> addNeigborsToIdaStarList(Node nod){
        ArrayList<Node> aList;
        Node nod2;
        int dx,dy;
        
        aList = new ArrayList<>();
        for (int i = 0; i < metricNeighborN ; i++){
            dx = nod.x + metricNeighborX[i];
            dy = nod.y + metricNeighborY[i];
            if (   dx >= 0 && dx < this.maze.length       // check maze boundaries
                && dy >= 0 && dy < this.maze[0].length
                && this.maze[dx][dy] >= 0    ){  // check if square is walkable
                
                if (pureHeuristic && backHfrontH)
                    nod2 = new Node(nod, dx, dy, nod.g + this.metricNeighborArray[i], 0, 0, Node.H_FRONT_PLUS_H_BACK);
                else if (pureHeuristic && !backHfrontH)
                    nod2 = new Node(nod, dx, dy, nod.g + this.metricNeighborArray[i], 0, 0, Node.PURE_HEURISTIC);
                else
                    nod2 = new Node(nod, dx, dy, nod.g + this.metricNeighborArray[i], 0, 0, Node.G_PLUS_H);
                
                
                //mapDemo.setMapCellOpen(nod.x, nod.y);
                if (    findNeighborInList(this.path, nod2) == null && 
                        findNeighborInList(idaStarNodeArr, nod2) == null &&
                        getNumberOfNeighborsInPath(path, nod2) < 2
                        ){
                    nod2.h = hFunc(dx, dy);
                    if (pureHeuristic && backHfrontH)
                        nod2.h2 = hFunc2(dx, dy);
                    nod2.idGoup = nod.idGoup + 1L;
                    aList.add(nod2);
                    nOpens = nOpens + 1L;
                    //mapDemo.setMapCellOpen(nod2.x, nod2.y);
                }
            }
        }
        //Collections.sort(aList);
        Collections.shuffle(aList);
        return aList;
    }
    
    private int getNumberOfNeighborsInPath(List<Node> aList, Node nod){
        int n;
        n=0;
        for (Node aNode:aList){
            if (Math.hypot(nod.x-aNode.x, nod.y-aNode.y) <= sqrt2) n++;
        }
        return n;
    }
    
    
    private void addNeigborsToOpenList() {
        Node node;
        Node node2;
        double d;
        int x,y;
        
        for (int i = 0; i < metricNeighborN ; i++){
            x = this.now.x + metricNeighborX[i];
            y = this.now.y + metricNeighborY[i];
            if (   x >= 0 && x < this.maze.length       // check maze boundaries
                && y >= 0 && y < this.maze[0].length
                && this.maze[x][y] >= 0             ){  // check if square is walkable
                
                if (pureHeuristic && backHfrontH)
                    node = new Node(this.now, x, y, 0, 0, 0, Node.H_FRONT_PLUS_H_BACK);
                else if (pureHeuristic && !backHfrontH)
                    node = new Node(this.now, x, y, 0, 0, 0, Node.PURE_HEURISTIC);
                else
                    node = new Node(this.now, x, y, 0, 0, 0, Node.G_PLUS_H);
                
                node2 = findNeighborInList(this.open, node);
                if ( node2 != null){
                    d = this.now.g + this.metricNeighborArray[i];
                    if ( d < node2.g )  {
                        node2.g = d;
                        node2.parent = now;
                    }
                }
                else if (findNeighborInList(this.closed, node) == null) {
                        node.g = this.now.g + this.metricNeighborArray[i];
                        node.h = hFunc(node.x, node.y);
                        if (backHfrontH) node.h2 = hFunc2(node.x, node.y);
                        this.open.add(node);
                        nOpens++;
                        mapDemo.setMapCellOpen(node.x, node.y);                        
                }
            }
        }
        Collections.sort(this.open);
    }
    
    
    private static Node findNeighborInList(List<Node> array, Node node) {
        return array.stream()
                .filter(e -> (e.x == node.x && e.y == node.y))
                .findFirst()
                .orElse(null);
    }
        
    
    private double metric(int x, int y) {
        switch (hType){
            case H_EUCLIDEAN:
                return Math.hypot((double)x, (double)y);
            case H_DISCRETE_K3:
                return Math.hypot((double)x, (double)y);
            case H_MANHATAN:
                return ((double)x + (double)y);
        }
        System.out.println("heuristic switch case not found, returning x");
        return (double)x;        
    }
    
    
    private double hFunc(int x, int y) {
        int dx,dy,dm,dz;
        
        switch (hType){
            case H_EUCLIDEAN:
                return Math.hypot(x - this.xend, y - this.yend);
            case H_DISCRETE_K3:
                dx = Math.abs(x - this.xend);
                dy = Math.abs(y - this.yend);
                dz = Math.abs(dx - dy);
                dm = Math.min(dx, dy);
                return ((double)dz + (double)dm * sqrt2);
            case H_MANHATAN:
                dx = Math.abs(x - this.xend);
                dy = Math.abs(y - this.yend);
                return ((double)dx + (double)dy);
        }
        System.out.println("heuristic switch case not found, returning x");
        return (double)x;
    }

    
    private double distFunc(int x1, int y1, int x2, int y2) {
        int dx,dy,dm,dz;
        
        switch (hType){
            case H_EUCLIDEAN:
                return Math.hypot(x1 - x2, y1 - y2);
            case H_DISCRETE_K3:
                dx = Math.abs(x1 - x2);
                dy = Math.abs(y1 - y2);
                dz = Math.abs(dx - dy);
                dm = Math.min(dx, dy);
                return ((double)dz + (double)dm * sqrt2);
            case H_MANHATAN:
                dx = Math.abs(x1 - x2);
                dy = Math.abs(y1 - y2);
                return ((double)dx + (double)dy);
        }
        System.out.println("heuristic switch case not found, returning x");
        return (double)x1;
    }


    private double hFunc2(int x, int y) {
        int dx,dy,dm,dz;
        
        switch (hType){
            case H_EUCLIDEAN:
                return Math.hypot(x - this.xstart, y - this.ystart);
            case H_DISCRETE_K3:
                dx = Math.abs(x - this.xstart);
                dy = Math.abs(y - this.ystart);
                dz = Math.abs(dx - dy);
                dm = Math.min(dx, dy);
                return ((double)dz + (double)dm * sqrt2);
            case H_MANHATAN:
                dx = Math.abs(x - this.xstart);
                dy = Math.abs(y - this.ystart);
                return ((double)dx + (double)dy);
        }
        System.out.println("heuristic switch case not found, returning x");
        return (double)x;
    }
    
    
    private double getHypotStepG(int x, int y){
        return Math.hypot(x, y); // real distance cost = either 1.0 or sqrt(2)
    }
    
    
    private double getUnitStepG(int x, int y){
        return 1.0; // Horizontal/vertical cost = 1.0
    }
    
    
    private void runMethod(Runnable toRun){
        toRun.run();
    }
        
    
    private boolean isGoalState(int x, int y){
        return (x==searchGoalX && y==searchGoalY);
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
            case H_MANHATAN: return "Manhatan";
        }
        return "Default";
    }    
    
    
    private int addPathToMap(List<Node> p){
        int x,y;
        
        for (int i=0; i<p.size(); i++){
            for (int j=0; j<p.size(); j++){
                if (i != j){
                    if (p.get(i).x == p.get(j).x && p.get(i).y == p.get(j).y)
                        System.out.println("Duplicate nodes in Path List: " + 
                                Integer.toString(i) + ", " + Integer.toString(j));
                }
            }
        }
        
        pathLength=0;
        x = p.get(0).x;
        y = p.get(0).y;
        for (Node n : p){
            mapDemo.setMapCellPath(n.x, n.y);
            pathLength = pathLength + Math.hypot((double)(x-n.x), (double)(y-n.y));
            x = n.x;
            y = n.y;
        }
        return p.size();
    }
    
    
    private void clearSettings(){
        stopAnimBool = true;
        nOpens=0;
        pathSteps=0;
        pathLength=0;
        runTime=0;
        idaStarN=0;
        open = new ArrayList<>();
        closed = new ArrayList<>();
        path = new ArrayList<>();
        idaStarNodeArr = new ArrayList<>();
        idaStarNodeArr2 = new ArrayList<>();
        idaStarGArr = new ArrayList<>();
        idaStarBoundArr = new ArrayList<>();
        idaStarFound = false;
        idaStarNotFound = false;
        end = null;
        now = null;
        maze = null;
        mapDemo.runMap();
        maze = mapDemo.getMap();        
        xstart = getGridX(searchStartX);
        ystart = getGridY(searchStartY);
        xend = getGridX(searchGoalX);
        yend = getGridY(searchGoalY);
        updateMetricType();
        idaStarTimer.stop();
        aStarTimer.stop();
    }
    
    
    private void updateMetricType(){
        if (pureHeuristic && backHfrontH)
            aStarMetricTypeLabel.setText(METRIC_STRING + " f_h + b_h; ");
        else if (pureHeuristic && !backHfrontH)
            aStarMetricTypeLabel.setText(METRIC_STRING + " pure-h; ");
        else
            aStarMetricTypeLabel.setText(METRIC_STRING + " g+h; ");
    }
            
    
    private void updateStatusBar(int x, int y){
        statusBar.setText(STATUS_LABEL + "X=" +
                Integer.toString(getGridX(x)) + ", Y="+
                Integer.toString(getGridY(y)) + ";   Line: " +
                "Start: (" + Integer.toString(getGridX(searchStartX)) + "," +
                             Integer.toString(getGridY(searchStartY)) + ") - " + 
                "Goal: (" +  Integer.toString(getGridX(searchGoalX)) + "," +
                             Integer.toString(getGridY(searchGoalY)) + ")" +
                ";   Heuristic: " + getHeuristicString() +
                ";   #expanded=" + Long.toString(nOpens) +
                ";   Path Steps=" + Integer.toString(pathSteps) +
                ";   Path Length=" + String.format("%.2f", pathLength) +
                ";   Run Time=" + String.format("%.2f",runTime) + " [sec]"
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
    
        // Timer action listener to addd delay for each IDA* process step, for animation
        idaStarTimer = new Timer(timerTime, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                double f;
                long i;
                List<Node> aList;

                if (!stopAnimBool){
                    idaStarN++;
                    f = tempNode.g + getH(tempNode);
                    //System.out.println("start iteration " + Long.toString(idaStarN) + " with bound=" + String.format("%.2f", idaStarBound) + 
                    //        "; f=" + String.format("%.2f", f));

                    if (f <= idaStarBound){
                        path.add(0, tempNode);
                        //System.out.println("path item added: " + tempNode.toString());
                        mapDemo.setMapCellPath(tempNode.x, tempNode.y);
                        if (tempNode.x == xend && tempNode.y == yend) idaStarFound = true;
                        aList = addNeigborsToIdaStarList(tempNode);
                        if (!aList.isEmpty()) idaStarNodeArr.addAll(0, addNeigborsToIdaStarList(tempNode));
                        //else
                            //mapDemo.setMapCellClosed(tempNode.x, tempNode.y);
                    }
                    else if (f < idaStarMin) idaStarMin = f;

                    //System.out.println("iteration start path="+ getNodeArrString(path) + "; NodeArr=" + getNodeArrString(idaStarNodeArr));

                    if (!idaStarFound){
                        if (!idaStarNodeArr.isEmpty()) {
                            i = idaStarNodeArr.get(0).idGoup;
                            do{
                                tempNode = idaStarNodeArr.remove(0);
                                f = tempNode.g + getH(tempNode);
                                if (f > idaStarBound && f < idaStarMin) idaStarMin = f;
                                //mapDemo.setMapCellTerrain(tempNode.x, tempNode.y);
                                //System.out.println("NodeArr item removed: " + tempNode.toString() + "; f=" + String.format("%.2f",f));
                                /*if (path.get(0).idGoup >= tempNode.idGoup || i != tempNode.idGoup){
                                    i = tempNode.idGoup;
                                    tempNode2 = path.remove(0);
                                    //System.out.println("path item removed: " + tempNode2.toString());
                                    mapDemo.setMapCellTerrain(tempNode2.x, tempNode2.y);                                
                                }*/
                                /*
                                if (i != tempNode.idGoup){
                                    i = tempNode.idGoup;
                                    tempNode2 = path.remove(0);
                                    System.out.println("path item removed: " + tempNode2.toString());
                                    mapDemo.setMapCellTerrain(tempNode2.x, tempNode2.y);
                                }
                                */
                            } while(f > idaStarBound && !idaStarNodeArr.isEmpty());
                            while (!path.isEmpty() && path.get(0).idGoup >= tempNode.idGoup){
                                //i = tempNode.idGoup;
                                tempNode2 = path.remove(0);
                                //System.out.println("path item removed: " + tempNode2.toString());
                                mapDemo.setMapCellTerrain(tempNode2.x, tempNode2.y);                                
                            }
                        }
                        else{
                            if (idaStarMin == Double.MAX_VALUE) idaStarNotFound = true;
                            else {
                                idaStarBound = idaStarMin;
                                System.out.println("Bound increased: " + String.format("%.9f", idaStarBound));
                                idaStarMin = Double.MAX_VALUE;
                                mapDemo.readMap();
                                path = new ArrayList<>();
                                tempNode = start;
                            }
                        }
                    }

                    if (idaStarNotFound || idaStarFound){
                        end = tempNode;
                        idaStarTimer.stop();
                        if (idaStarFound){
                            System.out.println("IDA* animation path found !");
                            reversePath();
                            mapDemo.readMap();
                            pathSteps = addPathToMap(path);     
                            System.out.println("iteration start path="+ getNodeArrString(path) + "; NodeArr=" + getNodeArrString(idaStarNodeArr));
                        }
                        else
                            System.out.println("IDA* animation NOT path found !");
                    }
                    updateStatusBar(searchStartX, searchStartY);            
                    matrixPanel.repaint();      
                }
            }
        });
    
        // Timer action listener to addd delay for each A* process step, for animation
        aStarTimer = new Timer(timerTime, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                pathTemp = null;
                if (!stopAnimBool)
                    pathTemp = findPathToStepInto();
                if (pathTemp != null){
                    aStarTimer.stop();
                    stopAnimBool = false;
                    path = pathTemp;
                    pathSteps = addPathToMap(path);
                }
                updateStatusBar(searchStartX, searchStartY);            
                matrixPanel.repaint();
            }
        });

    }
    
    
    private class ActionHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent event){

            if (event.getSource() == stopAnimationButton){
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
                hType = H_MANHATAN;
                updateStatusBar(searchStartX, searchStartY);
            }
            
            if (event.getSource() == timerTimeField){
                if (!timerTimeField.getText().equals(Integer.toString(timerTime))){
                    if (Integer.parseInt(timerTimeField.getText()) >= 0 &&
                        Integer.parseInt(timerTimeField.getText()) < 2000 ){
                        timerTime = Integer.parseInt(timerTimeField.getText());
                        aStarTimer.setDelay(timerTime);
                        idaStarTimer.setDelay(timerTime);
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

            // if Run buton clicked
            if (event.getSource() == clearButton){
                clearSettings();
                matrixPanel.repaint();
            }
            
            // if Run buton clicked
            if (event.getSource() == aStarAnimButton){
                clearSettings();
                if (pureHeuristic && backHfrontH)
                    now = new Node(null, xstart, ystart, 0, 0, 0, Node.H_FRONT_PLUS_H_BACK);
                else if (pureHeuristic && !backHfrontH)
                    now = new Node(null, xstart, ystart, 0, 0, 0, Node.PURE_HEURISTIC);
                else
                    now = new Node(null, xstart, ystart, 0, 0, 0, Node.G_PLUS_H);

                closed.add(now);
                stopAnimBool = false;
                addNeigborsToOpenList();
                idaStarN = 0;
                aStarTimer.start();
            }

            // if A* buton clicked
            if (event.getSource() == aStarButton){
                clearSettings();
                if (pureHeuristic && backHfrontH)
                    now = new Node(null, xstart, ystart, 0, 0, 0, Node.H_FRONT_PLUS_H_BACK);
                else if (pureHeuristic && !backHfrontH)
                    now = new Node(null, xstart, ystart, 0, 0, 0, Node.PURE_HEURISTIC);
                else
                    now = new Node(null, xstart, ystart, 0, 0, 0, Node.G_PLUS_H);
                
                long start = System.nanoTime();
                path = findPathTo(xend, yend);
                long time = System.nanoTime() - start;
                runTime = time / 1e9;
                if (path != null){
                    pathSteps = addPathToMap(path);
                    updateStatusBar(searchStartX, searchStartY);
                }
                else System.out.println("Path not found !");
                matrixPanel.repaint();
            }

            // if IDA* animation buton clicked
            if (event.getSource() == idaStarAnimButton){
                clearSettings();
                stopAnimBool = false;
                
                idaStarBound = hFunc(xstart, ystart);
                System.out.println("starting bound: " + Double.toString(idaStarBound));
                
                if (pureHeuristic && backHfrontH)
                    start = new Node(null, xstart, ystart, 0, idaStarBound, hFunc2(xstart, ystart), Node.H_FRONT_PLUS_H_BACK);
                else if (pureHeuristic && !backHfrontH)
                    start = new Node(null, xstart, ystart, 0, idaStarBound, 0, Node.PURE_HEURISTIC);
                else
                    start = new Node(null, xstart, ystart, 0, idaStarBound, 0, Node.G_PLUS_H);
                
                start.idGoup = 0L;
                tempNode = start;
                nOpens = 0L;
                idaStarMin = Double.MAX_VALUE;
                System.out.println("starting IDA* anim");
                idaStarTimer.start();
            }

            // if IDA* button clicked
            if (event.getSource() == idaAStarButton){
                clearSettings();
                long start = System.nanoTime();
                idaStarLoop();
                long time = System.nanoTime() - start;
                runTime = time / 1e9;
                if (path != null)
                    pathSteps = addPathToMap(path);
                else System.out.println("Path not found !");
                updateStatusBar(searchStartX, searchStartY);
                matrixPanel.repaint();
            }
            
            if (event.getSource() == aStarGFButton){
                pureHeuristic = false;
                backHfrontH = false;
                updateMetricType();
                matrixPanel.repaint();
            }

            if (event.getSource() == aStarHButton){
                pureHeuristic = true;
                backHfrontH = false;
                updateMetricType();
                matrixPanel.repaint();
            }

            if (event.getSource() == aStarHHButton){
                pureHeuristic = true;
                backHfrontH = true;
                updateMetricType();
                matrixPanel.repaint();
            }
        }
    }
    
    
    // Mouse handler to allow mouse clicks on the matrix to toggle cell colors
    private class MouseHandler implements MouseListener, MouseMotionListener {
        // MouseListener event handlers
        // handle event when mouse released immediately after press
        @Override
        public void mouseClicked(MouseEvent event) {
            //matrixPanel.toggleCell(event.getX(), event.getY()); // toggle cell color
            System.out.println("X=" + Integer.toString(getGridX(event.getX())) + ",   " +
                               "Y=" + Integer.toString(getGridY(event.getY())));
                    
            mapDemo.setMapCellOpen(getGridX(event.getX()), getGridY(event.getY()));
            nOpens=0;
            pathSteps=0;
            pathLength=0;
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
                nOpens=0;
                pathSteps=0;
                pathLength=0;
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
                nOpens=0;
                pathSteps=0;
                pathLength=0;
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

