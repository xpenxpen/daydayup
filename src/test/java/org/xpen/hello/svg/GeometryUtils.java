package org.xpen.hello.svg;

// GeometryUtils.java
// Andrew Davison, August 2010, ad@fivedots.coe.psu.ac.th

/* Circles intersection code used by the intersect command
*/


import java.awt.geom.*;


public class GeometryUtils
{

  public static Point2D.Double circlesIntersect1(Point2D.Double p0, double r0,
                                                 Point2D.Double p1, double r1)
  { Point2D.Double[] pts = allCirclesIntersect(p0, r0, p1, r1);
    return (pts == null) ? null : pts[0];  // first point
  } 


  public static Point2D.Double circlesIntersect2(Point2D.Double p0, double r0,
                                                 Point2D.Double p1, double r1)
  { Point2D.Double[] pts = allCirclesIntersect(p0, r0, p1, r1);
    return (pts == null) ? null : pts[1];   // second point
  } 



  private static Point2D.Double[] allCirclesIntersect(Point2D.Double p0, double r0,
                                Point2D.Double p1, double r1)
  /*  Determine the points where 2 circles in a common plane intersect.
      See http://local.wasp.uwa.edu.au/~pbourke/geometry/2circle/
      and http://mathworld.wolfram.com/Circle-CircleIntersection.html
  */
  {
    /* dx and dy are the vertical and horizontal distances between
       the circle centers. */
    double dx = p1.x - p0.x;
    double dy = p1.y - p0.y;

    // Determine the straight-line distance between the centers
    double d = Math.sqrt((dy*dy) + (dx*dx));

    // Check for solvability
    if (d > (r0 + r1)) {
      System.out.println("circles do not intersect");
      return null;
    }

    if (d < Math.abs(r0 - r1)) {
      System.out.println("circles do not intersect: one is inside the other");
      return null; 
    }

    /* 'point2' is the point where the line through the circle
       intersection points crosses the line between the circle
       centers. */

    // Determine the distance from point0 to point2
    double a = ((r0*r0) - (r1*r1) + (d*d)) / (2.0 * d) ;

    // Determine the coordinates of point2
    Point2D.Double p2 = new Point2D.Double();
    p2.x = p0.x + (dx * a/d);
    p2.y = p0.y + (dy * a/d);

    /* Determine the distance from point2 to either of the
       intersection points. */
    double h = Math.sqrt((r0*r0) - (a*a));

    // Determine the offsets of the intersection points from point2
    double rx = -dy * (h/d);
    double ry = dx * (h/d);

    // Determine the absolute intersection points
    Point2D.Double ptIntersect1 = new Point2D.Double();
    ptIntersect1.x = p2.x + rx;
    ptIntersect1.y = p2.y + ry;

  
    Point2D.Double ptIntersect2 = new Point2D.Double();
    ptIntersect2.x = p2.x - rx;
    ptIntersect2.y = p2.y - ry;
  
    return new Point2D.Double[] { ptIntersect1, ptIntersect2} ;
  }  // end of allCirclesIntersect()


}  // end of GeometryUtils class