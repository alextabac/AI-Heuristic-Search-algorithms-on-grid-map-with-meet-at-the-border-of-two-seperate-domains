/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package searchaibenchmark;

import java.io.*;
import static java.util.stream.Collectors.joining;
import java.util.stream.Stream;
import java.lang.Math;
import java.util.List;


/**
 *
 * @author atabacar
 */
public class MapDemo {
    private final String mapName;
    private BufferedReader br;
    private static final String WIDTH_STR = "width";
    private static final String HEIGHT_STR = "height";
    private static final String MAP_PRIMER_STR = "map";
    private static final byte IN_EXPAND = 10;
    private static final byte IN_OPEN = 11;
    private static final byte IN_CLOSED = 12;
    private static final byte IN_PATH = 13;
    private static final char BLOCK = '@';
    private static final char TERRAIN = '.';
    private static final char WALL = 'T';
    private final int HEADER_LINES = 4;
    private int mapW;
    private int mapH;
    private byte map[][];
    private String mapChars;
    
    public MapDemo(String mapName){
        this.mapName = mapName;
        this.mapW = 0;
        this.mapH = 0;
        this.getMapSize();        
        this.map = new byte[mapW][mapH];
    }
    
    public byte[][] getMap(){
        return map;
    }
        
    public void runMap(){        
        //this.getMapSize();        
        //this.map = new byte[mapW][mapH];
        System.out.println("Map total distinct characters: " + this.getMapSymbols());
        this.readMap();
        //this.printMap();
        //System.out.println("map[0].length = " + Integer.toString(map[0].length));
        //System.out.println("map.length = " + Integer.toString(map.length));
    }
    
    public void setMapCellExpand(int x, int y){
        map[x][y] = IN_EXPAND;
    }
    
    public void setMapCellOpen(int x, int y){
        map[x][y] = IN_OPEN;
    }

    public void setMapCellPath(int x, int y){
        map[x][y] = IN_PATH;
    }
    
    public void setMapCellTerrain(int x, int y){
        map[x][y] = 0;
    }
    
    public void setMapCellClosed(int x, int y){
        map[x][y] = IN_CLOSED;
    }

    public void setMapCell(int x, int y, byte b){
        map[x][y] = b;
    }
    
    public byte getMapCell(int x, int y){
        return map[x][y];
    }
    
    public boolean isMapCellUnpassable(int x, int y){
        return (isMapCellBlocked(x, y) || isMapCellWall(x, y));
    }
    
    public boolean isMapCellTerrain(int x, int y){
        if (map[x][y] == 0) return true;
        //if (map[x][y] < mapChars.length())
        //    return (mapChars.charAt(map[x][y]) == TERRAIN);
        else return false;
    }
    
    public boolean isMapCellBlocked(int x, int y){
        if (map[x][y] == -2) return true;
        //if (map[x][y] < mapChars.length())
        //    return (mapChars.charAt(map[x][y]) == BLOCK);
        else return false;
    }

    public boolean isMapCellWall(int x, int y){
        if (map[x][y] == -1) return true;
        //if (map[x][y] < mapChars.length())
        //    return (mapChars.charAt(map[x][y]) == WALL);
        else return false;
    }

    public boolean isInOpenList(int x, int y){
        return (map[x][y] == IN_OPEN);
    }

    public boolean isPath(int x, int y){
        return (map[x][y] == IN_PATH);
    }

    public boolean isInExpandList(int x, int y){
        return (map[x][y] == IN_EXPAND);
    }
    
    public boolean isInClosedList(int x, int y){
        return (map[x][y] == IN_CLOSED);
    }

    //public char getMapCellChar(int x, int y){
    //    return mapChars.charAt(map[x][y]);
    //}
    
    public int getMapWidth(){
        return mapW;
    }
    
    public int getMapHeight(){
        return mapH;
    }

    private void printMap(){
        String line;
        
        for (int i=0; i<mapW; i=i+1){
            line = "";
            for (int j=0; j<mapH; j=j+1){
                //line = line.concat(Character.toString(mapChars.charAt(map[i][j])));                
            }
            System.out.println(line);
        }
        System.out.println("Map printing Completed.");
    }
    
    private byte getCellValueByChar(char c){
        if (c == BLOCK) return -2;
        if (c == WALL) return -1;
        return 0;
    }
    
    public void readMap(){
        String line;
        int j=0;
        
        this.moveBufferToMap();        
        while ((line = this.readLine()) != null){
            for (int i=0; i<mapW; i++){
                //map[i][j] = (byte) mapChars.indexOf(line.charAt(i));
                map[i][j] = getCellValueByChar(line.charAt(i));
            }
            j=j+1;
        }
        //System.out.println("Map read, total of " + Integer.toString(j) + " rows");
    }
    
    private String getMapSymbols(){
        String line;
        String symbols = "";

        this.moveBufferToMap();        
        while ((line = this.readLine()) != null){
            symbols = MapDemo.getDistinctCharsString(line, symbols);
        }
        this.mapChars = symbols;
        return symbols;
    }
    
    private static String getDistinctCharsString(String... strings){
        return Stream.of(strings)
                .map(String::chars)
                .flatMap(intStream -> intStream.mapToObj(charCode -> (char) charCode))
                .distinct()
                .sorted()
                .map(character -> new String(new char[]{character}))
                .collect(joining());
    }
        
    private void getMapSize(){
        String line;
        String[] headerStrings;
        
        resetMapFile();
        for (int i=0; i<HEADER_LINES; i=i+1){
            if ((line = this.readLine()) != null){
                if (line.contains(WIDTH_STR)){
                    headerStrings = line.split(" ");                    
                    this.mapW = Integer.parseInt(headerStrings[1]);
                }
                if (line.contains(HEIGHT_STR)){
                    headerStrings = line.split(" ");
                    this.mapH = Integer.parseInt(headerStrings[1]);
                }
            }
        }
        System.out.println( "Map size: width=" + Integer.toString(mapW) + 
                            ", height=" + Integer.toString(mapH));
    }
    
    private void moveBufferToMap(){
        String line;
        
        resetMapFile();
        for (int i=0; i<HEADER_LINES; i=i+1){
            if ((line = this.readLine()) != null){
                if (line.contains(MAP_PRIMER_STR))
                    break;
            }
        }
    }
    
    private String readLine(){
        String line = null;
        try {
            line = br.readLine();
            //System.out.println(line);
        }
        catch (FileNotFoundException ex){
            System.out.println("Unable to open file '" + mapName + "'");  
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + mapName + "'");                  
            // Or we could just do this: 
            // ex.printStackTrace();
        }
        return line;
    }

    private void resetMapFile(){
        br = null;
        
        try{
            br = new BufferedReader(new FileReader(mapName));
        }
        catch (FileNotFoundException ex){
            System.out.println("Unable to open file '" + mapName + "'");  
        }                   
    }
}
