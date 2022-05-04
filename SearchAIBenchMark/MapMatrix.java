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
public class MapMatrix {
    private final int WIDTH;
    private final int HEIGHT;
    private byte map[][];
    private String mapChars;
    
    public MapMatrix(int width, int height, String mapChars){
        this.HEIGHT = height;
        this.WIDTH = width;        
        this.mapChars = mapChars;
        map = new byte[WIDTH][HEIGHT];
    }
    
    public void setMapCell(int x, int y, byte c){
        map[x][y] = c;
    }
    
    public int getMapCell(int x, int y){
        return (int)map[x][y];
    }

    public char getMapCellChar(int x, int y){
        return mapChars.charAt(map[x][y]);
    }
    
    public int getMapWidth(){
        return WIDTH;
    }
    
    public int getMapHeight(){
        return HEIGHT;
    }

    
}
