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

/**
 *
 * @author atabacar
 */
public class Coord2D implements CoordInterface {
    final double maxDualStep = 2.0 * Math.sqrt(2.0);
    int x,y;
    
    Coord2D() {}
    Coord2D(Coord2D c) { x=c.x; y=c.y; }
    Coord2D(int x1, int y1) { x=x1; y=y1; }
    Coord2D(CoordInterface c) { x=c.getX(); y=c.getY(); }
    public void setXY(int x, int y) {this.x=x; this.y=y;}
    
    @Override public double getMaxDualStep() {return maxDualStep;}
    @Override public int getX() {return x;}
    @Override public int getY() {return y;}
    @Override public CoordInterface create() { return new Coord2D(x,y); }
              public CoordInterface create(Coord2D c) { return new Coord2D(c);}
    @Override public CoordInterface create(CoordInterface c) {return new Coord2D(c);}
    
    @Override public void setCoord(CoordInterface c) {x=c.getX(); y=c.getY();}
              public void setCoord(Coord2D c) {x=c.x; y=c.y;}
    
    @Override public void getSum(CoordInterface c1, CoordInterface c2) 
                {x=c1.getX()+c2.getX(); y=c1.getY()+c2.getY();}
    
    @Override public void addInPlace(CoordInterface c) {x+=c.getX(); y+=c.getY();}
              public void addInPlace(Coord2D c) {x+=c.x; y+=c.y;}
    
    @Override public boolean equalCoord(CoordInterface c) {return (x==c.getX() && y==c.getY());}
              public boolean equalCoord(Coord2D c) {return (x==c.x && y==c.y);}
    
    @Override public void printCoord() {System.out.println(this.toString());}
    @Override public String toString() {return String.format("(%d,%d)",x,y);}
    @Override public void randCoords(CoordInterface maxRand) {
        x = UtilityFunc.rand.nextInt(maxRand.getX());
        y = UtilityFunc.rand.nextInt(maxRand.getY());
    }

    @Override public int getZ() {
        throw new java.lang.RuntimeException("*** Calling getZ() from a Coord2D object.");
        //throw new java.lang.Error("*** Calling getZ() for a Coord2D object.");
    }
}
