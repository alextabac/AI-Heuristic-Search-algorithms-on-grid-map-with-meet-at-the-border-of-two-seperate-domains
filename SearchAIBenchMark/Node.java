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
class Node implements Comparable {
        public Node parent;
        public long idGoup;
        public int x, y;
        public double g;
        public double h;
        public double h2;
        static public final int PURE_HEURISTIC = 0;
        static public final int G_PLUS_H = 1;
        static public final int H_FRONT_PLUS_H_BACK = 2;
        public int compareType;
        
        Node(Node parent, int xpos, int ypos, double g, double hFront, double hBack, int compareType) {
            this.parent = parent;
            this.x = xpos;
            this.y = ypos;
            this.g = g;
            this.h = hFront;
            this.h2 = hBack;
            
            this.compareType = compareType;
        }
        
        @Override
        public String toString(){
            return String.format("(%d,%d).%d", x,y,idGoup);
        }
        
        @Override
        public int compareTo(Object o) {
            Node that = (Node) o;
            
            switch(compareType){
                case PURE_HEURISTIC -> {
                    if (this.h > that.h) return 1;
                    if (this.h < that.h) return -1;
                    return 0;
                }
                case G_PLUS_H -> {
                    if      ((this.g + this.h) > (that.g + that.h)) return 1;
                    else if ((this.g + this.h) < (that.g + that.h)) return -1;
                    else {
                        if      (this.g > that.g) return -1;
                        else if (this.g < that.g) return 1;
                        else return 0;
                    }
                }
                case H_FRONT_PLUS_H_BACK -> {
                    if ((this.h+this.h2) > (that.h+that.h2)) return 1;
                    if ((this.h+this.h2) < (that.h+that.h2)) return -1; 
                    if (this.h > that.h) return 1;
                    if (this.h < that.h) return -1;
                    return 0;
                }
            }
            return 0;
        }
    }

