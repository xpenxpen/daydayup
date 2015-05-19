package org.xpen.hello.svg;

// Nodes.java
// Andrew Davison, August 2010, ad@fivedots.coe.psu.ac.th

/* Parse tree node classes. 
   A parse tree is created by the parser, and interpreted by
   the evaluator. Each class here corresponds to a grammar rule, as
   defined in Parser.java.
*/

import java.util.*;


class ProgramNode {
// PROGRAM ::=  COMMAND+
   ArrayList<CommandNode> cmds;
   ProgramNode(ArrayList<CommandNode> cs) {cmds = cs;}
}

// ---------------------------------------------------------

abstract class CommandNode {}
// COMMAND  ::=  LET | DRAW | CYCLE | PRINT


class LetCmdNode extends CommandNode {
// LET  ::=  let ID '=' ( EXPR | SHAPE )
   String id; ExprNode expr; ShapeNode shape;
   LetCmdNode(String i, ExprNode e, ShapeNode s) {id=i; expr=e; shape=s;}
}

class DrawCmdNode extends CommandNode {
// DRAW ::= draw [COLOR] ( SHAPE | ID )
   String color; ShapeNode shape; String id;
   DrawCmdNode(String col, ShapeNode s,String i) {color=col; shape=s; id=i;}
}

class CycleCmdNode extends CommandNode {
// CYCLE  ::=  cycle  (CIRCLE|ID) EXPR(sides)  (EXPR(angle)|'%')  '{' COMMAND+ '}'
   CircleNode circle; String id; ExprNode sides; ExprNode angle;
   boolean halfRot; ArrayList<CommandNode> cmds;

   CycleCmdNode(CircleNode c, String i, ExprNode s, ExprNode a, 
                            boolean hr, ArrayList<CommandNode> cs)
   {circle=c; id=i; sides=s; angle=a; halfRot=hr; cmds=cs;}
}

class PrintCmdNode extends CommandNode {
// PRINT  ::=  print STRING ( EXPR | SHAPE )
   String printStr; ExprNode expr; ShapeNode shape;
   PrintCmdNode(String str, ExprNode e, ShapeNode s) 
   {printStr=str; expr=e; shape=s;}
}

// ---------------------------------------------------------

class ExprNode {
// EXPR  ::=  TERM ( ('*'|'/') TERM )*
   TermNode term; ArrayList<ExprRestNode> terms;
   ExprNode(TermNode t, ArrayList<ExprRestNode> ts) {term=t; terms=ts;}
}

class ExprRestNode {
// ( ('*'|'/') TERM )
   String op; TermNode term;
   ExprRestNode(String o, TermNode t) {op=o; term=t;}
}


class TermNode {
// TERM  ::=  FACTOR ( ('+'|'-') FACTOR )*
   FactorNode factor; ArrayList<TermRestNode> facts;
   TermNode(FactorNode f, ArrayList<TermRestNode> fs) {factor=f; facts=fs;}
}

class TermRestNode {
// ('+'|'-') FACTOR
   String op; FactorNode factor;
   TermRestNode(String o, FactorNode f) {op=o; factor=f;}
}


class FactorNode {
// FACTOR  ::= NUMBER | POINT | VERTEX | LOOPCOUNTER | pi | ID | '(' EXPR ')'
   boolean isNumber;
   double number; PointNode point; VertexNode vertex; LoopCounterNode loopCounter;
   String id; ExprNode expr;
   FactorNode(boolean isN, double n, PointNode pt, VertexNode v, 
                                  LoopCounterNode lc, String i, ExprNode e)
   {isNumber=isN; number=n; point=pt; vertex=v; loopCounter=lc; id=i; expr=e;}
}


// ---------------------------------------------------------

abstract class PointNode {}
// POINT  ::=  point '(' EXPR(x) ',' EXPR(y) ')'  | origin | INTERSECT | WALK |TURN


class APointNode extends PointNode {
//  point EXPR(x) EXPR(y)
   ExprNode x; ExprNode y;
   APointNode(ExprNode e1, ExprNode e2) {x=e1;y=e2;}
}


class OriginNode extends PointNode {   //  origin
   OriginNode() {}
}


class IntersectNode extends PointNode {
// INTERSECT  ::=   intersect|intersect2  '(' (CIRCLE|ID)  (CIRCLE|ID) ')'
   boolean useSecond; CircleNode circle1; String id1; CircleNode circle2; String id2; 
   IntersectNode(boolean b, CircleNode c1, String nm1, CircleNode c2, String nm2) 
   {useSecond=b; circle1=c1; id1=nm1; circle2=c2; id2=nm2;}
}


class TurnNode extends PointNode {
// TURN  ::=  turn '(' ( CIRCLE | ID ) EXPR (point on circle)  EXPR(angle) ')'
   CircleNode circle; String id; ExprNode point; ExprNode angle;
   TurnNode(CircleNode c, String i, ExprNode p, ExprNode a) 
   {circle=c; id=i; point=p; angle=a;}
}



// ---------------------------------------------------------

class VertexNode {
// VERTEX  ::=  vertex '-' (NUMBER|ID} '^'*
   double number; String id; int ups;
   VertexNode(double n, String s, int u) {number=n; id=s; ups=u;}
}


class LoopCounterNode {
// LOOPCOUNTER  ::=  loopCounter '^'*
   int ups;
   LoopCounterNode(int u) {ups=u;}
}

// ---------------------------------------------------------

abstract class ShapeNode {}
// SHAPE ::=  LINE  |  CIRCLE  |  CIRCLES


class LineNode extends ShapeNode {
// LINE  ::=  line '(' EXPR(point)  EXPR(point) ')'
   ExprNode point1; ExprNode point2;
   LineNode(ExprNode p1, ExprNode p2) {point1=p1; point2=p2;}
}

class CircleNode extends ShapeNode {
// CIRCLE  ::=  circle  '(' EXPR(point)  EXPR(radius) ')'
   ExprNode point; ExprNode radius;
   CircleNode(ExprNode p, ExprNode r) {point=p; radius=r;}
}

class CirclesNode extends ShapeNode {
// CIRCLES  ::=  circles  '(' EXPR(point)  EXPR(radius)+ ')'
   ExprNode point; ArrayList<ExprNode> radii;
   CirclesNode(ExprNode p, ArrayList<ExprNode> rs) {point=p; radii=rs;}
}
