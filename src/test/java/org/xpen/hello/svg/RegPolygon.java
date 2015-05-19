package org.xpen.hello.svg;

// RegPolygon.java
// Andrew Davison, August 2010, ad@fivedots.coe.psu.ac.th

/* RegPolygon holds information used by the "cycle" command as it moves 
   round a circle, visiting regular polygon coordinates along its perimeter.
*/

import java.awt.geom.*;


public class RegPolygon
{
  private Point2D.Double center;     // for the circle being iterated over
  private double radius;

  private double rotAngle;   // the initial rotation of the first polygon coordinate
  private int numSides;

  private Point2D.Double coords[];  // the polygon's coordinates
  private int loopCounter = 0;



  public RegPolygon(CCircle c, double a, int n)
  {
    center = c.getCenter();
    radius = c.getRadius();  
    rotAngle = a;
    numSides = n;

    if (radius <= 0) {
      System.out.println("Radius must be > 0");
      radius = 2;
    }
    if (numSides < 2) {
      System.out.println("No. of sides must be >= 2");
      numSides = 2;
    }

    coords = new Point2D.Double[numSides];
    double cornerAngle = 2*Math.PI/numSides;   // angle of each corner

    // fill in the polygon's coordinates
    double currAngle = rotAngle;
    for (int i=0; i < numSides; i++) {
      coords[i] = new Point2D.Double( center.x + radius*Math.cos(currAngle), 
                                      center.y + radius*Math.sin(currAngle) );
      // System.out.printf("coords[%d] = (%.2f, %.2f)\n", i, coords[i].x, coords[i].y);
      currAngle += cornerAngle;
    }
  } // end of RegPolygon()



  public Point2D.Double getVertex(int idx)
  {
    if ((idx < 0) || (idx >= numSides)) {
      System.out.println("Vertex index out of range");
      idx = 0;
    }
    int coordIdx = (idx + loopCounter) % numSides;
    return coords[coordIdx];
  }  // end of getVertex()


  public void incrLoopCounter()
  {  loopCounter = (loopCounter + 1) % numSides;  }


  public int getLoopCounter()
  {  return loopCounter;  }


  public int getNumSides()
  {  return numSides;  }

}  // end of RegPolygon class

