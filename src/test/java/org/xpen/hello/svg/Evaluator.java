package org.xpen.hello.svg;

// Evaluator.java
// Andrew Davison, August 2010, ad@fivedots.coe.psu.ac.th

/* Evaluate the parse tree, which is constructed from
   objects defined in Nodes.java. Output a SVGGraphics2D
   object from genImage(), whih is rendered by the the 
   Apache Batik library (http://xmlgraphics.apache.org/batik/)
   in CropCircles.java
*/


import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.batik.dom.GenericDOMImplementation;
// batik and DOM classes
import org.apache.batik.svggen.SVGGraphics2D;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;



public class Evaluator
{
  // image dimensions
  private static final int WIDTH = 400;
  private static final int HEIGHT = 400;


  private HashMap<String,Object> dict;   // symbol table for IDs

  private ArrayList<RegPolygon> cycles;   // for storing cycle RegPolygons
  private int numCycles = 0;

  private SVGGraphics2D drawG2D;



  public Evaluator()
  {  
    dict = new HashMap<String,Object>();
    cycles = new ArrayList<RegPolygon>();

    initDrawing();
  }  // end of Evaluator()


  private void initDrawing()
  {
    // Get a DOMImplementation
    DOMImplementation domImpl =
            GenericDOMImplementation.getDOMImplementation();

    // Create an instance of org.w3c.dom.Document
    String svgNS = "http://www.w3.org/2000/svg";
    Document document = domImpl.createDocument(svgNS, "svg", null);

    // Create an instance of the SVG Generator
    drawG2D = new SVGGraphics2D(document);

    // request highest-quality rendering
    RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                                              RenderingHints.VALUE_ANTIALIAS_ON);
    hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    drawG2D.setRenderingHints(hints);

    // white background
    Rectangle2D.Double bgRect = new Rectangle2D.Double(0, 0, WIDTH, HEIGHT);
    drawG2D.setPaint(Color.WHITE);
    drawG2D.fill(bgRect);

    drawG2D.setPaint(Color.BLACK);
    // move the origin to the center of the image
    drawG2D.translate(WIDTH/2, HEIGHT/2);
  }  // end of initDrawing()



  public SVGGraphics2D genImage(ProgramNode pNode)
  /* ProgramNode data: 
       ArrayList<CommandNode> cmds;    */
  {
    for(CommandNode cmd: pNode.cmds)
      evalCommand(cmd);
    return drawG2D;
  }  // end of genImage()



  // -------------------------- evaluate commands ---------------------------------------

  private void evalCommand(CommandNode cmd)
  // CommandNode subclasses: LetCmdNode, DrawCmdNode, CycleCmdNode, PrintCmdNode
  {
    if (cmd instanceof LetCmdNode)
      evalLet((LetCmdNode)cmd);
    else if (cmd instanceof DrawCmdNode)
      evalDraw((DrawCmdNode)cmd);
    else if (cmd instanceof CycleCmdNode)
      evalCycle((CycleCmdNode)cmd);
    else if (cmd instanceof PrintCmdNode)
      evalPrint((PrintCmdNode)cmd);
  }  // end of evalCommand()



  private void evalLet(LetCmdNode cmd)
  /* LetCmdNode data: 
       String id; ExprNode expr; ShapeNode shape;    */
  {
    Object obj = null;   // id may be a shape or an expression
    if (cmd.shape != null)
      obj = evalShape(cmd.shape);
    else if (cmd.expr != null)
      obj = evalExpr(cmd.expr);
    else
      System.out.println("No value for let");

    // System.out.println("Storing " + cmd.id);
    dict.put(cmd.id, obj);
  }  // end of evalLet()



  private void evalDraw(DrawCmdNode cmd)
  /* DrawCmdNode data: 
       String color; ShapeNode shape; String id;    */
  {
    Object obj;   // draw a shape directly, or access one via an ID
    if (cmd.shape != null)
      obj = evalShape(cmd.shape);
    else   // must be an ID
      obj = lookupID(cmd.id);
    doDraw(obj, getColor(cmd.color) );
  }  // end of evalDraw()


  private Color getColor(String colorStr)
  // gray | red | green | blue | yellow | orange
  // black is the default
  {
    if (colorStr.equals("gray"))
      return Color.GRAY;
    else if (colorStr.equals("red"))
      return Color.RED;
    else if (colorStr.equals("green"))
      return Color.GREEN;
    else if (colorStr.equals("blue"))
      return Color.BLUE;
    else if (colorStr.equals("yellow"))
      return Color.YELLOW;
    else if (colorStr.equals("orange"))
      return Color.ORANGE;
    else
      return Color.BLACK;
  }  // end of getColor()



  private Object lookupID(String id)
  // access an ID's Object
  {
    // System.out.println("Looking up " + id);
    Object obj = null;
    obj = dict.get(id);
    if (obj == null) {
      System.out.println(id + " not defined");
      System.exit(1);
    }
    return obj;
  }  // end of lookupID()



  private void doDraw(Object obj, Color color)
  // an object can be a line, circle, or list of circles
  {
    // System.out.println("Changing color to " + color);
    drawG2D.setPaint(color);

    if (obj instanceof Line2D.Double)    // a line
      drawG2D.draw((Line2D.Double)obj);
    else if (obj instanceof CCircle)     // a single circle
      doDrawCircle((CCircle)obj);
    else if(obj instanceof ArrayList<?>) {   // an ArrayList of something
      @SuppressWarnings("unchecked")
      ArrayList<Object> objList = (ArrayList<Object>) obj;
      Object firstObj = objList.get(0);    // get first elem, and text that
      if (firstObj instanceof CCircle) {  // is a circle list
        for(Object objCircle : objList)
          doDrawCircle((CCircle)objCircle);
      }
      else
        System.out.println("draw shape is an unknown list type");
    }
    else
      System.out.println("draw shape is an unknown type");
  }  // end of doDraw()



  private void doDrawCircle(CCircle circle)
  {
    // System.out.println("Drawing circle");
    circle.draw(drawG2D);
  }  // end of doDrawCircle()



  private void evalCycle(CycleCmdNode ccmd)
  /* CycleCmdNode data: 
       CircleNode circle; String id; ExprNode sides; ExprNode angle;  (degrees)
       boolean halfRot; ArrayList<CommandNode> cmds;    */
  {
    RegPolygon regPoly = buildPoly(ccmd);  // looping info
    cycles.add(regPoly);    // store in cycles list
    numCycles++;

    int numSides = regPoly.getNumSides();
    // System.out.println("evalCycle() cmds: " + ccmd.cmds);
    for (int i=0; i < numSides; i++) {  // iterate through poly sides
      for(CommandNode cmd : ccmd.cmds)   // execute all cycle cmds for each side
        evalCommand(cmd);
      regPoly.incrLoopCounter();
    }

    numCycles--;   // finished with cycles list
    cycles.remove(numCycles);    // remove this cycle
  }  // end of evalCycle()



  private RegPolygon buildPoly(CycleCmdNode cmd)
  /* build RegPolygon from Cycle cmd to hold the iterative details
       CircleNode circle; String id; ExprNode sides; ExprNode angle;  (degrees)
       boolean halfRot; ArrayList<CommandNode> cmds;    */
  {
    CCircle ccircle = getCircleVal(cmd.circle, cmd.id);

    Double d = (Double) evalExpr(cmd.sides);
    int numSides = (int)(d.doubleValue());

    // System.out.println("buildPoly(): cmd.angle: " + cmd.angle);
    double angle = 0;     // angle is in radians
    if (cmd.halfRot)   // using "%"
      angle = Math.PI/numSides;
    else if (cmd.angle != null)   // will be null if there's no angle in the command
      angle = Math.toRadians( ((Double)evalExpr(cmd.angle)).doubleValue() );
    // System.out.println("buildPoly(): angle: " + angle);

    return new RegPolygon(ccircle, angle, numSides);   
  }  // end of buildPoly()



  private CCircle getCircleVal(CircleNode circle, String id)
  // retrive circle info, or dereference the ID
  {
    if (circle != null)
      return evalCircleNode(circle);
    else  { // use ID instead
      Object result = lookupID(id);
      if (!(result instanceof CCircle)) {
        System.out.println(id + " value is not a circle");
        System.exit(1);
      }
      return (CCircle)result;
    }
  }  // end of getCircleVal()




  private void evalPrint(PrintCmdNode cmd)
  /* PrintCmdNode data: 
       String printStr; ExprNode expr; ShapeNode shape;  */
  {
    if (cmd.shape != null) {
      Object obj = evalShape(cmd.shape);
      printShape(cmd.printStr, obj);
    }
    else if (cmd.expr != null)  
      printExprShape(cmd.printStr, cmd.expr);
    else
      System.out.printf("%s = Bad argument\n", cmd.printStr);
  }  // end of evalPrint()



  private void printShape(String printStr, Object obj)
  // print a line, circle, or circles list
  {
    if (obj instanceof Line2D.Double) {
      System.out.print(printStr + " = ");
      Line2D.Double line = (Line2D.Double)obj;
      System.out.printf("line( point(%.2f, %.2f), point(%.2f, %.2f)", 
                              line.x1, line.y1, line.x2, line.y2); 
      System.out.println("");
    }
    else if (obj instanceof CCircle)
      System.out.println(printStr + " = " + (CCircle)obj);
    else if(obj instanceof ArrayList<?>) {   // an ArrayList of something
      @SuppressWarnings("unchecked")
      ArrayList<Object> objList = (ArrayList<Object>) obj;
      Object firstObj = objList.get(0);    // get first elem, and text that
      if (firstObj instanceof CCircle) {  // is a circles list
        System.out.print(printStr + " = ( ");
        for(Object objCircle : objList)
          System.out.print((CCircle)objCircle + " ");
        System.out.println(")");
      }
    }
    else
      System.out.printf("%s = Unknown print type\n", printStr);
  }  // end of printShape()



  public void printExprShape(String printStr, ExprNode expr)
  // print a double or point, or shape (obtained from an ID)
  {
    Object obj = evalExpr(expr);
    if (obj instanceof Double) {
      double v = ((Double)obj).doubleValue();
      System.out.printf("%s %.2f\n", printStr, v);      
    }
    else if (obj instanceof Point2D.Double) {
      Point2D.Double pt = (Point2D.Double)obj;
      System.out.printf("%s point(%.2f, %.2f)\n", printStr, pt.x, pt.y);
    }
    else
      printShape(printStr, obj);
  }  // end of printExprShape()


  // ------------------------ expression evaluation ------------------------


  private Object evalExpr(ExprNode expr)
  /* ExprNode data: 
       TermNode term; ArrayList<ExprRestNode> terms;    
     and ExprRestNode data:
       String op; TermNode term;    */
  {
    Object o1 = evalTerm(expr.term);
    if (expr.terms.size() > 0)
      for(ExprRestNode er : expr.terms) {
        Object o2 = evalTerm(er.term);
        o1 = applyExpr(o1, er.op, o2);
      }
    return o1;
  }  // end of evalExpr()


  private Object applyExpr(Object o1, String op, Object o2)
  // apply * and / but only if the operand types allow it
  {
    if ((o1 instanceof Double) && (o2 instanceof Double)) {
      double v1 = ((Double)o1).doubleValue();
      double v2 = ((Double)o2).doubleValue();
      if (op.equals("*"))
        o1 = new Double(v1*v2);
      else  // must be "/"
        o1 = new Double(v1/v2);
    }
    else if ((o1 instanceof Double) && (o2 instanceof Point2D.Double)) {
      double v = ((Double)o1).doubleValue();
      Point2D.Double p = (Point2D.Double)o2;
      if (op.equals("*"))
        o1 = new Point2D.Double(v*p.x, v*p.y);
      else  // must be "/"
        o1 = new Point2D.Double(v/p.x, v/p.y);
    }
    else if ((o1 instanceof Point2D.Double) && (o2 instanceof Double)) {
      Point2D.Double p = (Point2D.Double)o1;
      double v = ((Double)o2).doubleValue();
      if (op.equals("*"))
        o1 = new Point2D.Double(p.x*v, p.y*v);
      else  // must be "/"
        o1 = new Point2D.Double(p.x/v, p.y/v);
    }
    else
      System.out.println("Operand type mismatch for " + op);

    return o1;
  }  // end of applyExpr()




  private Object evalTerm(TermNode term)
  /* TermNode data: 
       FactorNode factor; ArrayList<TermRestNode> facts;    
     and TermRestNode data:
       String op; FactorNode factor;    */
  {
    Object o1 = evalFactor(term.factor);
    if (term.facts.size() > 0)
      for(TermRestNode tr : term.facts) {
        Object o2 = evalFactor(tr.factor);
        o1 = applyTerm(o1, tr.op, o2);
      }
    return o1;
  }  // end of evalTerm()



  private Object applyTerm(Object o1, String op, Object o2)
  // apply + and - but only if the operand types allow it
  {
    // System.out.println("01 =" + o1);
    // System.out.println("02 =" + o2);
    if ((o1 instanceof Double) && (o2 instanceof Double)) {
      double v1 = ((Double)o1).doubleValue();
      double v2 = ((Double)o2).doubleValue();
      if (op.equals("+"))
        o1 = new Double(v1+v2);
      else  // must be "-"
        o1 = new Double(v1-v2);
    }
    else if ((o1 instanceof Point2D.Double) && (o2 instanceof Point2D.Double)) {
      Point2D.Double p1 = (Point2D.Double)o1;
      Point2D.Double p2 = (Point2D.Double)o2;
      if (op.equals("+"))
        o1 = new Point2D.Double(p1.x+p2.x, p1.y+p2.y);
      else  // must be "-"
        o1 = new Point2D.Double(p1.x-p2.x, p1.y-p2.y);
    }
    else
      System.out.println("Operand type mismatch for " + op);
    return o1;
  }  // end of applyTerm()



  private Object evalFactor(FactorNode f)
  /* FactorNode data: 
        boolean isNumber;
        double number; PointNode point; VertexNode vertex; 
        LoopCounterNode loopCounter; String id; ExprNode expr;   */
  {
    Object result = null;
    if (f.isNumber)
      result = new Double(f.number);
    else if (f.point != null)
      result = evalPointNode(f.point);
    else if (f.vertex != null) {
      // VertexNode data: double number; String id; int ups;
      int vertIdx = getVertexIndex(f.vertex.number, f.vertex.id);
      result = getVertex(vertIdx, f.vertex.ups);
    }
    else if (f.loopCounter != null) // LoopCounterNode data: int ups;
      result = getLoopCounter(f.loopCounter.ups);
    else if (f.id != null)
      result = lookupID(f.id);
    else if (f.expr != null)
      result = evalExpr(f.expr);

    return result;
  }  // end of evalFactor()



  private int getVertexIndex(double number, String id)
  // retrieve integer vertex index from number field, or dereference the ID
  {
    if (id == null)
      return (int)number;
    else  { // use ID instead
      Object result = lookupID(id);
      if (!(result instanceof Double)) {
        System.out.println("vertex id " + id + " is not a number; using 0");
        return 0;
      }
      else
        return (int)((Double)result).doubleValue();
    }
  }  // end of getVertexIndex()



  private Point2D.Double getVertex(int vertIdx, int ancestor)
  // access a specified vertex for this cycle, or an ancestor cycle
  {
    int cycleRef = numCycles - 1 - ancestor;
    if (cycleRef < 0) {
      System.out.println("No cycle ancestor found");
      return new Point2D.Double(0, 0);
    }
    RegPolygon poly = cycles.get(cycleRef);
    return poly.getVertex(vertIdx);
  }  // end of getVertex()




  private Double getLoopCounter(int ancestor)
  // access loop counter for this cycle, or an ancestor cycle
  {
    int cycleRef = numCycles - 1 - ancestor;
    if (cycleRef < 0) {
      System.out.println("No cycle ancestor found");
      return new Double(0);
    }
    RegPolygon poly = cycles.get(cycleRef);
    return new Double( poly.getLoopCounter());
  }  // end of getLoopCounter()




  // ----------------------------- point evaluation ---------------------------


  private Point2D.Double evalPointNode(PointNode point)
  // PointNode subclasses: APointNode, OriginNode, IntersectNode, TurnNode
  {
    Point2D.Double pt = null;
    if (point instanceof APointNode) {  // APointNode data: ExprNode x; ExprNode y;
      APointNode apt = (APointNode)point;
      pt = new Point2D.Double(evalDouble(apt.x), evalDouble(apt.y));
    }
    else if (point instanceof OriginNode)  // no data in OriginNode
      pt = new Point2D.Double(0,0);
    else if (point instanceof IntersectNode)
      pt = evalIntersect((IntersectNode)point);
    else if (point instanceof TurnNode)
      pt = evalTurn((TurnNode)point);
    return pt;
  }  // end of evalPointNode()



  private double evalDouble(ExprNode eVal)
  // evaluate the expression, returning a double
  {
    Object o = evalExpr(eVal);
    if (o instanceof Double)
      return ((Double)o).doubleValue();
    else {
      System.out.println("Expression is not a double");
      return 0;
    }
  }  // end of evalDouble()




  private Point2D.Double evalIntersect(IntersectNode in)
  /* IntersectNode data: 
       boolean useSecond; CircleNode circle1; String id1; 
       CircleNode circle2; String id2;    */
  {
    CCircle cc1 = getCircleVal(in.circle1, in.id1);
    CCircle cc2 = getCircleVal(in.circle2, in.id2);

    if (!in.useSecond)   // use the first intersect point
      return GeometryUtils.circlesIntersect1(cc1.getCenter(), cc1.getRadius(), 
                                             cc2.getCenter(), cc2.getRadius() );
    else
      return GeometryUtils.circlesIntersect2(cc1.getCenter(), cc1.getRadius(), 
                                             cc2.getCenter(), cc2.getRadius() );
  }  // end of evalIntersect()



  private Point2D.Double evalTurn(TurnNode t)
  /* TurnNode data: 
       CircleNode circle; String id; ExprNode point; ExprNode angle;   */
  {
    CCircle ccircle = getCircleVal(t.circle, t.id);

    Point2D.Double pt = evalPoint(t.point);
    pt = ccircle.closestToPoint(pt);     // move pt onto circle's perimeter

    double angle = evalDouble(t.angle);   // in degrees

    return ccircle.rotatePoint(pt, Math.toRadians(angle));
  }  // end of evalTurn()



  private Point2D.Double evalPoint(ExprNode ePoint)
  // evaluate an expression returning a Point2D
  {
    Object o = evalExpr(ePoint);
    if (o instanceof Point2D.Double)
      return (Point2D.Double)o;
    else {
      System.out.println("Expression is not a point");
      return new Point2D.Double(0,0);
    }
  }  // end of evalPoint()



  // ------------------------ shape evaluation -----------------------------------


  private Object evalShape(ShapeNode s)
  // ShapeNode subclasses: LineNode, CircleNode, CirclesNode
  {
    if (s instanceof LineNode) {
      // LineNode data: ExprNode point1; ExprNode point2;
      LineNode line = (LineNode)s;
      return new Line2D.Double( evalPoint(line.point1), 
                                evalPoint(line.point2) );
    }
    else if (s instanceof CircleNode)
      return evalCircleNode( (CircleNode)s );
    else if (s instanceof CirclesNode)
      return evalCircles( (CirclesNode)s );
    else
      System.out.println("Unknown shape type");

    return null;
  }  // end of evalShape()



  private CCircle evalCircleNode(CircleNode circle)
  /* CircleNode data: 
       ExprNode point; ExprNode radius;   */
  {
    Point2D.Double pt = evalPoint(circle.point);
    double radius = evalDouble(circle.radius);
    return new CCircle(pt, radius);
  }  // end of evalCircleNode()




  private ArrayList<CCircle> evalCircles(CirclesNode circles)
  /* CirclesNode data: 
       ExprNode point; ArrayList<ExprNode> radii;  */
  {
    ArrayList<CCircle> crcs = new ArrayList<CCircle>();
    Point2D.Double pt = evalPoint(circles.point);
    for(ExprNode radius : circles.radii)
      crcs.add( new CCircle(pt, evalDouble(radius) ) );
    return crcs;
  }  // end of evalCircles()


}  // end of Evaluator class
