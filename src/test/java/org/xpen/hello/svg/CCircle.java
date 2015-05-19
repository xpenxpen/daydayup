package org.xpen.hello.svg;


// CCircle.java
// Andrew Davison, August 2010, ad@fivedots.coe.psu.ac.th

/* Stores methods and details for a crop circle.
*/


import java.awt.geom.*;
import java.awt.*;
import java.text.DecimalFormat;


public class CCircle
{
  private Point2D.Double center;
  private double radius;

  private DecimalFormat df = new DecimalFormat("0.##");  // 2 dp



  public CCircle(Point2D.Double p, double r)
  {
    center = p;
    radius = r;
  }  // end of CCircle()


  public Point2D.Double getCenter()
  {  return center;  }

  public double getRadius()
  {  return radius;  }


  public String toString()
  {  return "circle( point(" + df.format(center.x) + ", " + df.format(center.y) + "), " + 
                     df.format(radius) + ")";
  } 


  public void draw(Graphics2D g2d)
  {
    Arc2D.Double circle = new Arc2D.Double();
    circle.setArcByCenter(center.x, center.y, radius, 0, 360, Arc2D.OPEN);   // use a 360-degree arc
    g2d.draw(circle);
  }  // end of draw()



  public Point2D.Double closestToPoint(Point2D.Double p)
  /* what is the point on the circle closest to p? 
     See http://stackoverflow.com/questions/300871/
                   best-way-to-find-a-point-on-a-circle-closest-to-a-given-point
  */
  {
    double vX = p.x - center.x;
    double vY = p.y - center.y;
    double magV = Math.sqrt(vX*vX + vY*vY);
 
    Point2D.Double perimP = new Point2D.Double();
    perimP.x = center.x + (vX / magV * radius);
    perimP.y = center.y + (vY / magV * radius);

    return perimP;
  }  // end of closestToPoint



  public Point2D.Double rotatePoint(Point2D.Double p, double angle)
  // rotate p counterclockwise by angle radians around circle perimter
  {
    double xRelOrig = p.x - center.x;
    double yRelOrig = p.y - center.y;

    // see http://en.wikipedia.org/wiki/Cartesian_coordinate_system
    double xRelNew = xRelOrig*Math.cos(angle) - yRelOrig*Math.sin(angle);
    double yRelNew = xRelOrig*Math.sin(angle) + yRelOrig*Math.cos(angle);

    Point2D.Double ptNew = new Point2D.Double( center.x + xRelNew, 
                                               center.y + yRelNew);
    return ptNew;
  }  // end of rotatePoint()
}  // end of CCircle class
