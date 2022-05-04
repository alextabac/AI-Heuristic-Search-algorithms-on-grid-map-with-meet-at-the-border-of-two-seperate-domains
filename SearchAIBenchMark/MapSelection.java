/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchaibenchmark;

/**
 *
 * @author atabacar
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
//import javax.swing.ListSelectionModel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneLayout;
import javax.swing.ScrollPaneConstants;
import java.awt.event.*;
/**
 *
 * @author atabacar
 */
public class MapSelection extends JFrame{
    String selectedMap;
    MapGui mapGui;
    MapDemo mapDemo;
    
    // default constructor
    public MapSelection() {
        
        //setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
        File curDir = new File(".");
        
        FilenameFilter filter;
        filter = new FilenameFilter(){
            @Override
            public boolean accept(File f, String name){
                return name.endsWith(".map");
            }
        };
        
        //File[] filesList = curDir.listFiles();
        
        fileNames = curDir.list(filter);

        JList mapsList = new JList(fileNames);
        mapsList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        
        JFrame f = new JFrame("Map file selection");
        
        JButton openButton = new JButton("Open");
        JPanel buttonPanel = new JPanel();        
        buttonPanel.add(openButton);
        JScrollPane fileScroll = new JScrollPane(mapsList);
        fileScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        fileScroll.setLayout(new ScrollPaneLayout());
        add(buttonPanel,BorderLayout.NORTH);
        add(fileScroll, BorderLayout.CENTER);

	// Adds the 'add' Listener to the button
	openButton.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                try {
                    if (!mapsList.isSelectionEmpty()){                        
                        selectedMap = mapsList.getSelectedValue().toString();
                        System.out.println("Selected map: " + selectedMap);                        
                        mapGui = new MapGui(selectedMap);                        
                    }                                                                
                }
                catch (Exception e2){
                    System.out.println("Exception during selecting map file.");
                }
            }
	});
        
    }
}

