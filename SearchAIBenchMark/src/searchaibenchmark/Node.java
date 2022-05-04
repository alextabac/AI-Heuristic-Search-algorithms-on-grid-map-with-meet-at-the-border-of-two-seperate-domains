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

import java.util.List;


/**
 *
 * @author atabacar
 */
public class Node implements NodeInterface {
    protected NodeInterface parent;
    //private int iBorder, jBorder;
    protected double g,h,f,pr;
    protected boolean reExpanded;
    protected boolean border;
    protected boolean crossed;
    protected char direction;
    protected CoordInterface coord;
    
    Node(){}
    
    Node(CoordInterface c, double g, NodeInterface parent) {
        this.coord = c.create();
        this.parent = parent;
        this.g = g;
    }
    Node(CoordInterface c, double g, double h) {
        this.coord = c.create();
        this.g = g;
        this.h = h;
    }
    Node(CoordInterface c, double g, double h, double f) {
        this.coord = c.create();
        this.g = g;
        this.h = h;
        this.f = f;
    }
    Node(CoordInterface c, double h) {
        this.coord = c.create();
        this.h = h;
    }
    
    Node(CoordInterface c) {coord = c.create();}
    
    @Override public void close() {}
    @Override public void heritChainB() {}
    
    @Override public NodeInterface createCopyL() {
        return new Node(coord, g, parent);
    }
    public NodeInterface createCopy() {
        Node n = new Node(coord, g, h, f);
        n.setParent(parent);
        n.setIsBorder(border);
        n.setIsCrossed(crossed);
        n.setDirection(direction);
        return n;
    }
    @Override public NodeInterface createCopySwap() {return new Node(coord, g, parent);}
    @Override public NodeInterface createH() { return new Node(coord, h);}
    @Override public NodeInterface create() {return new Node(coord, g, parent);}
    @Override public NodeInterface create(CoordInterface c, double g) {
        return new Node(c.create(), g, this);
    }
    @Override public NodeInterface create(CoordInterface c, double g, NodeInterface parent) {
        return new Node(c.create(),g,parent);
    }
    
    @Override public void setIndex(int i) 
    {throw new java.lang.RuntimeException("*** Calling Node setIndex(), suspected invalid usage.");}
    @Override public int getIndex() 
    {throw new java.lang.RuntimeException("*** Calling Node getIndex(), suspected invalid usage.");}
    
    @Override public CoordInterface getCoord() {return coord;}
    @Override public NodeInterface getParent() {return parent;}
    @Override public void setParent(NodeInterface p) {parent=p;}
    @Override public void setReExpanded(boolean r) {reExpanded=r;}
    @Override public void setIsBorder(boolean r) {border=r;}
    @Override public void setIsCrossed(boolean r) {crossed=r;}
    @Override public void setF(double f1) {f=f1;}
    @Override public void setG(double g1) {g=g1;}
    @Override public void setH(double h1) {h=h1;}
    @Override public void setPr(double pr1) {pr=pr1;}
    @Override public double getG() {return g;}
    @Override public double getF() {return f;}
    @Override public double getH() {return h;}
    @Override public double getPr() {return pr;}
    @Override public boolean getIsCrossed() {return crossed;}
    @Override public boolean getIsBorder() {return border;}
    @Override public boolean getIsReExpanded() {return reExpanded;}
    @Override public char getDirection() {return direction;}
    @Override public void setDirection(char d) {this.direction=d;}
    @Override public boolean equalNode(NodeInterface n) {return coord.equalCoord(n.getCoord());}
    
    @Override public int compareTo(NodeInterface n) {
        if      (this.f > n.getF()) return 1;
        else if (this.f < n.getF()) return -1;
        else {
            if      (this.g > n.getG()) return -1;
            else if (this.g < n.getG()) return 1;
            else return 0;
        }
    }
    
    @Override public int compareG(NodeInterface n) {
        if      (this.g > n.getG()) return 1;
        else if (this.g < n.getG()) return -1;
        else return 0;
    }
    @Override public int comparePr(NodeInterface n) {
        if      (this.pr > n.getPr()) return 1;
        else if (this.pr < n.getPr()) return -1;
        else return 0;
    }
    
    @Override public String toString() {
        return coord.toString()+":"+String.format("g=%.3f;h=%.3f;f=%.3f: (",g,h,f)+
                border+","+crossed+")";
    }
}
