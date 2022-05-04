/*
 * The MIT License
 *
 * Copyright 2022 AlexandruPaul Tabacaru.
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

/**
 * This is the main GUI.
 * Showing 3D maps followed by 2D maps, and buttons.
 * The button "Graphics 2D Single" button:
 *   Need to select a map once.
 *   Will open the 2D maps GUI with the selected map, class: AnimGui.
 * The button "Graphics 2D Dual" button:
 *   Need to select a map twice, and each time click on this button, can select same map.
 *   Will open the 2D maps GUI with the selected maps, class: AnimGui.
 * The "Marathon 2D/3D Single" button:
 *   Selecting one map to be used for running marathons.
 * The "Marathon 2D/3D Dual" button:
 *   Selecting two maps (can be same map twice), used for marathons run.
 * @author Alexandru Tabacaru
 */
import java.io.File;
import java.io.FilenameFilter;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;
import javax.swing.ScrollPaneConstants;
import java.awt.event.*;
import java.util.Arrays;
import javax.swing.JLabel;

public class MapSelection extends JFrame{
    private final JLabel statusBar;
    private final JLabel statusBar2;
    private final JPanel labelPanel;
    private final String path = "../Maps/";
    String selectedMap;
    String selectedMap2;
    MarathonGUI marathonGUI;
    AnimGui mapGui;
    //Map3Dgui map3Dgui;
    //MapSelection_Marathon2D marathon2D;
    
    public MapSelection() {
        this.setTitle("Map Selection  -  Listing map files in path:  " + path);
        selectedMap = "";
        selectedMap2 = "";
        statusBar = new JLabel();
        statusBar2 = new JLabel();
        labelPanel = new JPanel();
        labelPanel.setLayout(new BorderLayout());
        statusBar.setText("Please select map or dual maps (first one map and then the other).");
        statusBar2.setText("Second Map: ");
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
                System.exit(0); //calling the method is a must
            }
      });
    }

    public void getMap() throws Exception {
        String[] fileNames;
        String[] fileNames3D;
        File[] files;
        int i;
        
        File curDir = new File(path);
        FilenameFilter filter;
        filter = new FilenameFilter(){
            @Override
            public boolean accept(File f, String name){
                return name.endsWith(".map");
            }
        };
        
        FilenameFilter filter3D;
        filter3D = new FilenameFilter(){
            @Override
            public boolean accept(File f, String name){
                return name.endsWith(".3dmap");
            }
        };
        
        fileNames = curDir.list(filter);
        files = new File(path).listFiles(filter);
        i=0;
        for (File file : files) {
            fileNames[i] = file.getName();
            String str = UtilityFunc.resetFileGetMapSize2D(path.concat(fileNames[i]));
            String[] widthHeight = str.split("\s");
            String coorText = "(".concat(widthHeight[0]).concat(" , ").concat(widthHeight[1]).concat(")");
            fileNames[i] = fileNames[i].concat("    :    ").concat(coorText);
            i++;
        }
        
        
        fileNames3D = curDir.list(filter3D);
        files = new File(path).listFiles(filter3D);
        i=0;
        for (File file : files) {
            fileNames3D[i] = file.getName();
            String str = UtilityFunc.resetFileGetMapSize3D(path.concat(fileNames3D[i]));
            String[] widthHeightDepth = str.split("\s");
            String coorText = "(".concat(widthHeightDepth[0]).concat(" , ").concat(widthHeightDepth[1]);
            coorText = coorText.concat(" , ").concat(widthHeightDepth[2]).concat(")");
            fileNames3D[i] = fileNames3D[i].concat("    :    ").concat(coorText);
            i++;
        }
        
        
        String[] fileNamesBoth = Arrays.copyOf(fileNames3D, fileNames3D.length + fileNames.length);
        System.arraycopy(fileNames, 0, fileNamesBoth, fileNames3D.length, fileNames.length);
        JList mapsList = new JList(fileNamesBoth);
        mapsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        //JFrame f = new JFrame("Map file selection");
        JButton single2D = new JButton("Graphics 2D Single");
        JButton dual2D = new JButton("Graphics 2D Dual");
        JButton singleMarathonAllButton = new JButton("Marathon 2D/3D Single");
        JButton dualMarathonAllButton = new JButton("Marathon 2D/3D Dual");
        JButton clearDualButton = new JButton("Clear Dual");
        JPanel buttonPanel = new JPanel();    
        buttonPanel.add(single2D);
        buttonPanel.add(dual2D);
        buttonPanel.add(singleMarathonAllButton);
        buttonPanel.add(dualMarathonAllButton);
        buttonPanel.add(clearDualButton);
        JScrollPane fileScroll = new JScrollPane(mapsList);
        fileScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        fileScroll.setLayout(new ScrollPaneLayout());
        add(buttonPanel,BorderLayout.NORTH);
        add(fileScroll, BorderLayout.CENTER);
        labelPanel.add(statusBar, BorderLayout.NORTH);
        labelPanel.add(statusBar2, BorderLayout.SOUTH);
        add(labelPanel,BorderLayout.SOUTH);
        

        // ==========================================================================
        // ==========================================================================
        
	clearDualButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent event){
                selectedMap = "";
                selectedMap2 = "";
                statusBar.setText("First Map:        " + selectedMap);
                statusBar2.setText("Second Map:  " + selectedMap2);
            }
	});
        
        
        single2D.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event){
                selectedMap2 = "";
                statusBar2.setText("Second Map:  " + selectedMap2);
                selectedMap = path.concat(mapsList.getSelectedValue().toString());
                statusBar.setText("First Map:        " + selectedMap);
                mapGui = new AnimGui(selectedMap);
            }
        });
                

        dual2D.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent event){
                if (!mapsList.isSelectionEmpty()){
                    if (selectedMap.isEmpty()) {
                        selectedMap = path.concat(mapsList.getSelectedValue().toString());
                        System.out.println("Selected 1st map: " + selectedMap);
                        //MapGui a = new AnimGui(selectedMap, false, 1, 1);
                    } else {
                        selectedMap2 = path.concat(mapsList.getSelectedValue().toString());
                        System.out.println("Selected 2nd map: " + selectedMap2);
                    }

                    //System.out.println("Selected map: " + selectedMap);                        
                    statusBar.setText("First Map:        " + selectedMap);
                    statusBar2.setText("Second Map:  " + selectedMap2);
                    if (!selectedMap.isEmpty() && !selectedMap2.isEmpty()) {
                        mapGui = new AnimGui(selectedMap + "|" + selectedMap2);
                        selectedMap = "";
                        selectedMap2 = "";
                        statusBar.setText("First Map:        " + selectedMap);
                        statusBar2.setText("Second Map:  " + selectedMap2);
                    }
                }
            }
	});

        singleMarathonAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event){
                selectedMap2 = "";
                statusBar2.setText("Second Map:  " + selectedMap2);
                selectedMap = path.concat(mapsList.getSelectedValue().toString());
                statusBar.setText("First Map:        " + selectedMap);
                marathonGUI = new MarathonGUI(selectedMap);
            }
        });
        
        dualMarathonAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event){
                if (!mapsList.isSelectionEmpty()){
                    if (selectedMap.isEmpty())
                        selectedMap = path.concat(mapsList.getSelectedValue().toString());
                    else
                        selectedMap2 = path.concat(mapsList.getSelectedValue().toString());
                    statusBar.setText("First Map:        " + selectedMap);
                    statusBar2.setText("Second Map:  " + selectedMap2);
                    if (!selectedMap.isEmpty() && !selectedMap2.isEmpty()) {
                        marathonGUI = new MarathonGUI(selectedMap + "|" + selectedMap2);
                        selectedMap = "";
                        selectedMap2 = "";
                        statusBar.setText("First Map:        " + selectedMap);
                        statusBar2.setText("Second Map:  " + selectedMap2);
                    }
                }
            }
        });
        
        // ==========================================================================
        // ==========================================================================
        
    }
}

