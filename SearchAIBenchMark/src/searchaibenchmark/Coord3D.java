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
public class Coord3D implements CoordInterface {
    final double maxDualStep = 2.0 * Math.sqrt(3.0);
    int x,y,z;
    
    Coord3D() {}
    Coord3D(Coord3D c) { x=c.x; y=c.y; z=c.z; }
    Coord3D(int x1, int y1, int z1) { x=x1; y=y1; z=z1;}
    Coord3D(CoordInterface c) { x=c.getX(); y=c.getY(); z=c.getZ(); }
    
    @Override public double getMaxDualStep() {return maxDualStep;}
    @Override public int getX() {return x;}
    @Override public int getY() {return y;}
    @Override public int getZ() {return z;}
    @Override public CoordInterface create() {return new Coord3D(x,y,z);}
              public CoordInterface create(Coord3D c) {return new Coord3D(c);}
    @Override public CoordInterface create(CoordInterface c) {return new Coord3D(c);}
    
    @Override public void setCoord(CoordInterface c) {x=c.getX(); y=c.getY(); z=c.getZ();}
              public void setCoord(Coord3D c) {x=c.x; y=c.y; z=c.z;}
    
    @Override public void getSum(CoordInterface c1, CoordInterface c2) 
                {x=c1.getX()+c2.getX(); y=c1.getY()+c2.getY(); z=c1.getZ()+c2.getZ();}
    
    @Override public void addInPlace(CoordInterface c) {x+=c.getX(); y+=c.getY(); z+=c.getZ();}
              public void addInPlace(Coord3D c) {x+=c.x; y+=c.y; z+=c.z;}
    
    @Override public boolean equalCoord(CoordInterface c) {return (x==c.getX() && y==c.getY() && z ==c.getZ());}
              public boolean equalCoord(Coord3D c) {return (x==c.x && y==c.y && z ==c.z);}
              
    @Override public void printCoord() {System.out.println(this.toString());}
    @Override public String toString() {return String.format("(%d,%d,%d)",x,y,z);}
    @Override public void randCoords(CoordInterface maxRand) {
        x = UtilityFunc.rand.nextInt(maxRand.getX());
        y = UtilityFunc.rand.nextInt(maxRand.getY());
        z = UtilityFunc.rand.nextInt(maxRand.getZ());
    }

}
