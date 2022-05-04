/*
 * The MIT License
 *
 * Copyright 2022 Alexandru Tabacaru.
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
 * If you find a serious bug in my program please send me an email: alextabac@gmail.com
 * 
 * 
 * This program was developed during my research study at BGU university.
 * The program was developed over 1.5 years during the course of my study.
 * The primary purpose of the program is to visualize search algorithms (2D),
 * and help the student understand step-by-step, and develop new algorithms/heuristics.
 * The secondary purpose of this program is to allow student run these algorithms
 * in marathon mode for collecting statistical data of the performance (run-time/expansions).
 * 
 * 
 * This program consist of additional feature that is part of my research,
 * the feature is to allow research on the new define problem called MATB.
 * The MATB is best described in my, and my mentors: Dr. Dor Azmon, Prof. Ariel Felner.
 * The paper was published on SOCS-2022 conference.
 * Paper Title: Meeting at the border of two separate domains.
 * The additional feature is connecting two maps/graphs with border nodes connecting them.
 * And my research main purpose was to develop new heuristic that make use of the border nodes
 * to improve heuristic search algorithms on these MATB problems.
 * 
 * 
 * With this program I provide implementations for some of the known algorithms:
 * A*, MM, fMM, A*+BMPX, with the heuristic FE and added my own heuristics from my research:
 * FBE,  E-FBE.
 * My research is still ongoing to develop a improvement to these new 2 heuristics.
 * Users may test them, and develop their own.
 * Plan to add additional search algorithms:  BS*, NBS.
 * 
 * 
 * The program was built with focus on object-oriented methodology.
 * This allows to run exactly the algorithm class file on 2D and 3D.
 * Also, able to provide the heuristic object to the algorithm and so,
 * making different combinations of algorithms & heuristics.
 * 
 * 
 * -----------------------------------------------------------
 * 
 * This is main function to start the project.
 * The purpose of this package is to allow user experiment with different search 
 * algorithms that are implemented within this package.
 * The user may add his own algorithms & heuristics, and easily learn the characteristics.
 * The program also allows to run search algorithms in animation mode, or to see step by step,
 * the animation is for 2D maps only (no visualization for 3D maps).
 * 
 * There are two options for maps, either selecting single map, or selecting dual maps.
 * For dual map mode, the selected maps are concatenated along the X-axis, for both 2D/3D.
 * In dual mode, there will be a wall between the two maps, and it is needed to solder the maps
 * which means to connected the maps as one creating open passages through the wall.
 * There are two types of creating passages, either simple terrain, or border node.
 * The border node, is for experimenting with specific problem of two maps/graphs which
 * have a border between them and the user could use those border nodes to research for new
 * algorithms/heuristics.
 * 
 * 
 * @author Alexandru Tabacaru
 */
public class SearchAIBenchMark {

   public static void main(String[] args) throws Exception {
       // prevents bug for Comparator/Sorter/Merger
       System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
       System.out.println("Starting grid map benchmark.");
       MapSelection mapFiles = new MapSelection();
       try {
           mapFiles.getMap();
       }
       catch(Exception e){
           System.out.println("Exception try to get map.");
       }
       
       mapFiles.setSize(800, 700);
       mapFiles.setLocationRelativeTo(null);      
       mapFiles.setVisible(true);
   }
    
}
