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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author atabacar
 */
public class UtilityFunc {
    public static Random rand = new Random();

    public static void addPathToMapN(SEARCH_Map mapDemo, List<NodeInterface> p){
        for (int i=0; i<p.size(); i++){
            for (int j=0; j<p.size(); j++){
                if (i != j){
                    if (p.get(i).getCoord().getX() == p.get(j).getCoord().getX() && 
                            p.get(i).getCoord().getY() == p.get(j).getCoord().getY())
                        System.out.println("Misc. addPathToMapN: Duplicate nodes in Path List: " + 
                                Integer.toString(i) + ", " + Integer.toString(j));
                }
            }
        }
        for (NodeInterface n : p) mapDemo.setPath(n.getCoord());
    }
    
    
    public static String extractMapNames(String str) {
        String[] mapsN, sN;
        String mapN, mapForPrint;
        
        mapsN = str.split("[|]");
        mapN = mapsN[0].split("[:]")[0].trim();
        sN = mapN.split("[/]");
        mapForPrint = sN[sN.length-1].trim(); //.replace('.', '_');
        if (mapsN.length > 1) {
            mapN = mapsN[1].split("[:]")[0].trim();
            sN = mapN.split("[/]");
            mapForPrint = mapForPrint + "|" + sN[sN.length-1].trim(); //.replace('.', '_');
        }
        return mapForPrint;
    }
    
    
    public static String compactMapNames(String str) {
        String[] mapsN;
        String mapForPrint;
        
        mapsN = str.split("[|]");
        mapForPrint = "../Maps/" + mapsN[0].trim();
        if (mapsN.length > 1) {
            mapForPrint = mapForPrint + "|../Maps/" + mapsN[1].trim();
        }
        return mapForPrint;
    }
    
    public static String mapFilesToConvention(String str) {
        return extractMapNames(str).replace('|', '+').replace('.', '~') + ".csv";
    }
    public static String conventionToMapFiles(String str) {
        return str.replace('+', '|').replace('~', '.');
    }
    public static int getNumInstancesFromName(String s) {
        return Integer.valueOf(s.split("[-]",2)[1]);
    }
    
    // ========================      FILE methods     =================
    // assuming already reset to the correct line
    static public String getMapSizeFromBufferReader3D(BufferedReader br) {
        String line;
        String[] strArr;
        
        line = readLine(br);
        while (!line.toLowerCase().trim().startsWith("voxel"))
            line = readLine(br); // one header line
        
        strArr = line.trim().split("\s", 2);
        return strArr[1];
    }
    
    static public String resetFileGetMapSize3D(String mapN) {
        BufferedReader br = resetMapFile(mapN);
        String str = getMapSizeFromBufferReader3D(br);
        return str;
    }
    
    static public String resetFileGetMapSize2D(String mapN) {
        String str;
        
        BufferedReader br = resetMapFile(mapN);
        str = getMapSizeFromBufferReader2D(br);
        return str;
    }

    static public String getMapSizeFromBufferReader2D(BufferedReader br) {
        String line, str, height="", width="";
        String[] headerStrings;
        
        for (int i=0; i<4; i++){ // up to 4 rows search
            if ((line = readLine(br)) != null){
                if (line.contains("width")){
                    headerStrings = line.split("\s");
                    width = headerStrings[1];
                }
                if (line.contains("height")){
                    headerStrings = line.split("\s");
                    height = headerStrings[1];
                }
            }
        }
        
        str = width.concat(" ");
        str = str.concat(height);
        return str;
    }
    
    static public BufferedReader moveBufferToMap(String mapName, int headerLines){
        BufferedReader br;
        br = resetMapFile(mapName);
        for (int i=0; i<headerLines; i++) readLine(br);
        return br;
    }
    
    static public BufferedReader resetMapFile(String mapN){
        BufferedReader br;
        br = null;
        
        String[] mnames = mapN.split("[:]");
        String fname = mnames[0].trim();
        
        try{
            br = new BufferedReader(new FileReader(fname));
        }
        catch (FileNotFoundException ex){
            System.out.println("Unable to open file '" + fname + "': " + ex);  
        }
        return br;
    }

    static public String readLine(BufferedReader br){
        String line = null;
        try {
            line = br.readLine();
        }
        catch (FileNotFoundException ex){
            System.out.println("Unable to open map file " + ex);  
        }
        catch(IOException ex) {
            System.out.println("Error reading file  " + ex);
            // Or we could just do this: 
            // ex.printStackTrace();
        }
        return line;
    }
    
    // ================================================================================
    // ================================================================================

    
    static public String getMapsNameForStats(String mapsN) {
        String[] sp, sp2;
        String s,ss;
        
        sp = mapsN.split("[|]");
        sp2 = sp[0].split("[:]");
        sp2 = sp2[0].split("[/\\\\]");
        s = sp2[sp2.length-1].trim();
        if (sp.length > 1) {
            sp2 = sp[1].split("[:]");
            sp2 = sp2[0].split("[/\\\\]");
            ss = sp2[sp2.length-1].trim();
            return s.concat("|").concat(ss);
        } else {
            return s;
        }
    }
    
    public static int doesPathHasBorder(List<NodeInterface> nList) {
        int k = 0;
        if (nList == null) return -1;
        for (NodeInterface n: nList)
            if (n.getIsBorder()) k++;
        return k;
    }
    
    public static void warmupCPU() {
        rand = new Random();
        
        System.out.print("Warmup CPU started ....");
        double z=0;
        for (int i=0; i<500; i++)
            for (int j=0; j<1000; j++)
                for (int k=0; k<1000; k++)
                    z = rand.nextDouble() * rand.nextDouble();
        if (z==1) {}
        System.out.println("    completed.");
    }
    
    public static List<NodeInterface> getPathToEndNodeN(NodeInterface endNode, NodeInterface startNode){
        List<NodeInterface> aList = new ArrayList<>();
        while (endNode != null && !endNode.equalNode(startNode)) { //  .getParent() != null) {
            aList.add(0, endNode);
            endNode = endNode.getParent();
        }
        aList.add(0, startNode);
        return aList;
    }
    
    public static boolean checkDupNode(List<NodeInterface> path) {
        for (int j=0; j<path.size(); j++)
            if (path.get(j) == null) 
                System.out.println("########### found null node in path at index "+j);
        for (int i=0; i<(path.size()-1); i++)
            for (int j=i+1; j<path.size(); j++)
                if (path.get(i).equalNode(path.get(j))) {
                    System.out.println(" ***  ***  *** DUP IN NODES at "+i+" & "+j+" : " + path.get(i).toString());
                    return true;
                }
        return false;
    }
    
    public static List<NodeInterface> combineBiPaths(NodeInterface meetF, NodeInterface startF, NodeInterface meetB, NodeInterface startB){
        List<NodeInterface> path1, path2, path, path3;
                
        path1 = getPathToEndNodeN(meetF, startF);
        path2 = getPathToEndNodeN(meetB.getParent(), startB);
        for (NodeInterface n: path2) path1.add(n);
        return path1;
    }

    public static int insertNodeInListDouble(List<Double> nodeList, double aNode){
        int i,p,q;
        int n;
        boolean found;
        
        n = nodeList.size();
        switch (n) {
            case 0 -> {return 0;}
            case 1 -> {
                if (aNode >= nodeList.get(0)) return 1;
                else return 0;
            }
            default -> {
                found = false;
                q = n-1;
                p=0;
                i = q/2;
                while ((q-1) > p) {
                    if (aNode > nodeList.get(i)) p = i;
                    else if (aNode < nodeList.get(i)) q = i;
                    else {
                        found = true;
                        break;
                    }
                    i = (p+q)/2;
                }
                if (found) return (i+1);
                else {
                    if (aNode >= nodeList.get(q)) return (q+1);
                    else if (aNode < nodeList.get(p)) return p;
                    else return q;
                }
            }
        }
    } // function end: insertNodeInListDouble    
    

    public static <T extends Comparable> void insertInListF(List<T> nodeList, T aNode){
        int i,p,q;
        int n, e;
        boolean found;
        
        n = nodeList.size();
        switch (n) {
            case 0 -> nodeList.add(aNode);
            case 1 -> {
                if (aNode.compareTo(nodeList.get(0)) >= 0) nodeList.add(1, aNode);
                else nodeList.add(0, aNode);
            }
            default -> {
                found = false;
                q = n-1;
                p=0;
                i = q/2;
                while ((q-1) > p) {
                    e = aNode.compareTo(nodeList.get(i));
                    if      (e > 0) p = i;
                    else if (e < 0) q = i;
                    else { //  e == 0l
                        found = true;
                        break;
                    }
                    i = (p+q)/2;
                }
                if (found) nodeList.add(i+1, aNode);
                else {
                    if (aNode.compareTo(nodeList.get(q)) >= 0) nodeList.add(q+1, aNode);
                    else if (aNode.compareTo(nodeList.get(p)) < 0) nodeList.add(p, aNode);
                    else nodeList.add(q, aNode);
                }
            }
        }
    } // function end: insertInListF    
    
    public static void insertNodeInListG(List<NodeInterface> nodeList, NodeInterface aNode){
        int i,p,q;
        int n, e;
        boolean found;
        
        n = nodeList.size();
        switch (n) {
            case 0 -> nodeList.add(aNode);
            case 1 -> {
                if (aNode.compareG(nodeList.get(0)) >= 0) nodeList.add(1, aNode);
                else nodeList.add(0, aNode);
            }
            default -> {
                found = false;
                q = n-1;
                p=0;
                i = q/2;
                while ((q-1) > p) {
                    e = aNode.compareG(nodeList.get(i));
                    if      (e > 0) p = i;
                    else if (e < 0) q = i;
                    else { //  e == 0l
                        found = true;
                        break;
                    }
                    i = (p+q)/2;
                }
                if (found) nodeList.add(i+1, aNode);
                else {
                    if (aNode.compareG(nodeList.get(q)) >= 0) nodeList.add(q+1, aNode);
                    else if (aNode.compareG(nodeList.get(p)) < 0) nodeList.add(p, aNode);
                    else nodeList.add(q, aNode);
                }
            }
        }
    } // function end: insertNodeInListG
    
    public static void insertNodeInListPr(List<NodeInterface> nodeList, NodeInterface aNode){
        int i,p,q;
        int n, e;
        boolean found;
        
        n = nodeList.size();
        switch (n) {
            case 0 -> nodeList.add(aNode);
            case 1 -> {
                if (aNode.comparePr(nodeList.get(0)) >= 0) nodeList.add(1, aNode);
                else nodeList.add(0, aNode);
            }
            default -> {
                found = false;
                q = n-1;
                p=0;
                i = q/2;
                while ((q-1) > p) {
                    e = aNode.comparePr(nodeList.get(i));
                    if      (e > 0) p = i;
                    else if (e < 0) q = i;
                    else { //  e == 0l
                        found = true;
                        break;
                    }
                    i = (p+q)/2;
                }
                if (found) nodeList.add(i+1, aNode);
                else {
                    if (aNode.comparePr(nodeList.get(q)) >= 0) nodeList.add(q+1, aNode);
                    else if (aNode.comparePr(nodeList.get(p)) < 0) nodeList.add(p, aNode);
                    else nodeList.add(q, aNode);
                }
            }
        }
    } // function end: insertNodeInListPr
}
