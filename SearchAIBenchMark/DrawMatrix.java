/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchaibenchmark;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.AlphaComposite;
import javax.swing.JPanel;

/**
 *
 * @author atabacar
 */
public class DrawMatrix extends JPanel {
    private final MapDemo map;
    private int gridSize;
    private int x1;
    private int y1;
    private int x2;
    private int y2;
    
    public DrawMatrix(MapDemo map, int gridSize){
        this.map = map;
        this.gridSize = gridSize;
        x1 = -1;
        y1 = -1;
        x2 = -1;
        y2 = -1;
    }

    public void setGridSize(int s){
        gridSize = s;
    }
    
    public void eraseLine(){
        x1 = -1;
        y1 = -1;
        x2 = -1;
        y2 = -1;        
    }
    
    public void addLine(int x1, int y1, int x2, int y2){
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }
        
    @Override
    public void paintComponent(Graphics g){
        setBackground(Color.BLACK);
        super.paintComponent(g);

        int w = map.getMapWidth();
        int h = map.getMapHeight();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, w*gridSize-1, h*gridSize-1);
        
        Graphics2D g2d = (Graphics2D)g;
        //AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f);        
        AlphaComposite alphaComposite =  AlphaComposite.SrcAtop;        
        g2d.setComposite(alphaComposite);
        
        for (int i = 0  ;  i < w  ;  i++)
            for (int j = 0  ;  j < h  ;  j++){
                //setBackground(Color.BLACK);
                if (map.isMapCellBlocked(i, j)){
                    g.setColor(Color.BLACK);
                    g.fillRect(i*gridSize, j*gridSize, gridSize, gridSize);
                }
                else if (map.isPath(i, j)){                    
                    g.setColor(new Color(0,0,250));
                    //g.fillOval(i*gridSize, j*gridSize, gridSize, gridSize);
                    g.fillRoundRect(i*gridSize, j*gridSize, gridSize, gridSize, 14, 14);
                }
                else if (map.isMapCellWall(i, j)){
                    g.setColor(new Color(0,80,0));
                    g.fillRect(i*gridSize, j*gridSize, gridSize, gridSize);                    
                }
                else if (map.isInOpenList(i,j)){
                    g.setColor(Color.GREEN);
                    //g.fillOval(i*gridSize, j*gridSize, gridSize, gridSize);
                    g.fillRoundRect(i*gridSize, j*gridSize, gridSize, gridSize, 14, 14);
                }
                else if (map.isInClosedList(i,j)){
                    g.setColor(Color.RED);
                    //g.fillOval(i*gridSize, j*gridSize, gridSize, gridSize);
                    g.fillRoundRect(i*gridSize, j*gridSize, gridSize, gridSize, 14, 14);
                }
                //else if (map.isMapCellTerrain(i, j)){
                //    g.setColor(Color.LIGHT_GRAY);
                //    g.fillRect(i*gridSize, j*gridSize, gridSize, gridSize);
                //}
                //else{
                //}
            }
        if (this.x1 != -1){
            g.setColor(new Color(250,0,250));
            g.drawLine(x1, y1, x2, y2);
        }
    }
}
