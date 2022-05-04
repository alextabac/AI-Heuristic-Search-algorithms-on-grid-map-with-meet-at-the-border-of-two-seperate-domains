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
public class SearchAIBenchMark {

   public static void main(String[] args) {
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
       
       mapFiles.setSize(250, 300);
       mapFiles.setLocationRelativeTo(null);      
       mapFiles.setVisible(true);
   }
    
}
