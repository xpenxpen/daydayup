package org.xpen.hello.svg;

// Parser.java
// Andrew Davison, August 2010, ad@fivedots.coe.psu.ac.th

/* A recursive descent parser which uses the Lexer class to
   read Token objects from an input file. The tokens are matched against
   a grammar and a tree of Crop Node objects is built.

   Based on "Crop Art, Part 2", Andrew Glassner's Notebook, Nov/Dec 2004
   IEEE Computer Graphics and Applications,
   http://www.glassner.com/andrew/cg/research/crop/crop.htm

   The grammar is:

     PROGRAM ::=  COMMAND+
     COMMAND  ::=  LET | DRAW | CYCLE | PRINT

     LET  ::=  let ID '=' ( EXPR | SHAPE )
     DRAW ::= draw [COLOR] ( SHAPE | ID )
     COLOR  ::=  gray | red | green | blue | yellow | orange
     CYCLE  ::=  cycle (CIRCLE|ID) EXPR(sides)  [ (EXPR(angle)|'%') ]  '{' COMMAND+ '}'
         // was Glassner's ngonloop
     PRINT ::= print STRING (EXPR | SHAPE)
         // replaces Glassner's printDict() and printStack()

     EXPR  ::=  TERM ( ('*'|'/') TERM )*
     TERM  ::=  FACTOR ( ('+'|'-') FACTOR )*
     FACTOR  ::= NUMBER | POINT | VERTEX | LOOPCOUNTER | pi | ID | '(' EXPR ')'

     POINT  ::=  point '(' EXPR(x) ',' EXPR(y) ')' |  origin |  INTERSECT  |  TURN
     INTERSECT  ::=   (intersect|intersect2)  '(' (CIRCLE|ID)  (CIRCLE|ID)  ')'
        // was Glassner's trope()
     TURN  ::=  turn  '(' (CIRCLE|ID) EXPR(point on circle)  EXPR(angle) ')'
         // was Glassner's pspin()
     VERTEX  ::=  vertex '_' (NUMBER|ID) '^'*
     LOOPCOUNTER  ::=  loopCounter '^'*

     SHAPE ::=  LINE  |  CIRCLE  |  CIRCLES
     LINE  ::=  line '(' EXPR(point)  EXPR(point) ')'
     CIRCLE  ::=  circle  '(' EXPR(point)  EXPR(radius) ')'
     CIRCLES  ::=  circles  '(' EXPR(point)  EXPR(radius)+ ')'


     The methods that interface to the Lexer object are next() and two
     versions of match()
*/


import java.util.ArrayList;

public class Parser
{
  private Lexer lexer;
  private Token token;


  public Parser(String fnm)
  {  lexer = new Lexer(fnm); }



  public ProgramNode parse()
  // PROGRAM ::=  COMMAND+
  {
    ArrayList<CommandNode> cmds = new ArrayList<CommandNode>();
    next();
    cmds.add( command() );
    while (token.getType() != Token.EOF)
      cmds.add( command() );
    return new ProgramNode(cmds);
  }  // end of parse()


  // ------------------------ command parsing --------------------------


  private CommandNode command()
  // COMMAND  ::=  LET | DRAW | CYCLE | PRINT
  {
    if (token.isString("let"))
      return let();
    else if (token.isString("draw"))
      return draw();
    else if (token.isString("cycle"))
      return cycle();
    else if (token.isString("print"))
      return print();
    else {
      error(" in command syntax");
      return null;
    }
  }  // end of command()



  private LetCmdNode let()
  // LET  ::=  let ID '=' ( EXPR | SHAPE )
  {
    match("let");
    Token t = match(Token.NAME);
    match("=");

    ShapeNode shape = null;
    ExprNode expr = null;
    if (isShape(token))
      shape = shape();
    else
      expr = expr();
    return new LetCmdNode(t.getText(), expr, shape);
  }  // end of let()



  private boolean isShape(Token token)
  // SHAPE ::=  LINE  |  CIRCLE  |  CIRCLES
  {
    if ( token.isString("line") || token.isString("circle") ||
         token.isString("circles"))
      return true;
     return false;
  }  // end of isShape()



  private DrawCmdNode draw()
  /* DRAW ::= draw [COLOR] (SHAPE|ID)
     COLOR  ::=  gray | red | green | blue | yellow | orange
  */
  {
    match("draw");
    String color = "black";   // default
    if (isColor(token)) {   // deal with optional color
      color = token.getText();
      next();
    }

    ShapeNode shape = null;
    String id = null;
    if (isShape(token))    // is a shape or an ID
      shape = shape();
    else {
      Token t = match(Token.NAME);
      id = t.getText();
    }
    return new DrawCmdNode(color, shape, id);
  }  // end of draw()



  private boolean isColor(Token token)
  // COLOR  ::=  gray | red | green | blue | yellow | orange
  {
    if ( token.isString("gray") || token.isString("red") ||
         token.isString("green") || token.isString("blue") ||
         token.isString("yellow") || token.isString("orange"))
      return true;
     return false;
  }  // end of isColor()



  private CycleCmdNode cycle()
  // CYCLE  ::=  cycle (CIRCLE|ID ) EXPR(sides) [ (EXPR(angle)|'%') ] '{' COMMAND+ '}'
  {
    match("cycle");
    CircleNode circle = null;  
    String id = null;
    if (token.isString("circle"))    // is a circle or an ID
      circle = circle();
    else {
      Token t = match(Token.NAME);
      id = t.getText();
    }
    ExprNode sides = expr();

    ExprNode angle = null;
    boolean halfRot = false;

    if (!token.isString("{")) {   // there's a % or an angle
      if (token.isString("%")) {
        match("%");
        halfRot = true;
      }
      else
        angle = expr();
    }

    // collect the cycle's commands
    ArrayList<CommandNode> cmds = new ArrayList<CommandNode>();
    match("{");
    cmds.add( command() );
    while (!token.isString("}"))
      cmds.add( command() );
    match("}");
    // System.out.println("cycle() cmds: " + cmds);

    return new CycleCmdNode(circle, id, sides, angle, halfRot, cmds);
  }  // end of cycle()




  private PrintCmdNode print()
  // PEINT  ::=  print STRING (EXPR|SHAPE)
  {
    match("print");
    String printStr = null;
    if (token.getType() == Token.STRING) {
      printStr = token.getText();
      next();
    }
    else
      error(" -- no print string found");

    ShapeNode shape = null;
    ExprNode expr = null;
    if (isShape(token))   // is a shape or an expression
      shape = shape();
    else
      expr = expr();

    return new PrintCmdNode(printStr, expr, shape);
  }  // end of print()



  // -------------------------- expresion parsing ------------------------


  private ExprNode expr()
  // EXPR  ::=  TERM ( ('*'|'/') TERM )*
  {
    TermNode term = term();
    ArrayList<ExprRestNode> terms = new ArrayList<ExprRestNode>();

    while ( token.isString("*") || token.isString("/")) {
      String op = token.getText();
      next();   // skip * or /
      TermNode t2 = term();
      terms.add( new ExprRestNode(op,t2) );
    }
    return new ExprNode(term, terms);
  }  // end of expr()



  private TermNode term()
  // TERM  ::=  FACTOR ( ('+'|'-') FACTOR )*
  {
    FactorNode f1 = factor();
    ArrayList<TermRestNode> facts = new ArrayList<TermRestNode>();

    while ( token.isString("+") || token.isString("-")) {
      String op = token.getText();
      next();   // skip + or -
      FactorNode f2 = factor();
      facts.add( new TermRestNode(op,f2) );
    }
    return new TermNode(f1, facts);
  }  // end of term()




  private FactorNode factor()
  // FACTOR  ::= NUMBER | POINT | VERTEX | LOOPCOUNTER | pi | ID | '(' EXPR ')'
  {
    boolean isNumber = false;
    double number = 0; 
    PointNode point = null; 
    VertexNode vertex = null;
    LoopCounterNode loopCounter = null; 
    String id = null; 
    ExprNode expr = null;

    if (token.getType() == Token.NUMBER) {   // a number
      number = new Double( token.getValue());
      isNumber = true;
      next();
    }
    else if (isPoint(token))  // a point
      point = point();
    else if (token.isString("vertex"))    // a vertex
      vertex = vertex();
    else if (token.isString("loopCounter"))  // a loopCounter
      loopCounter = loopCounter();
    else if (token.isString("pi")) {    // the PI constant
      match("pi");
      number = Math.PI;
      isNumber = true;
    }
    else if (token.getType() == Token.NAME) {   // an ID
      id = token.getText();
      next();
    }
    else if (token.isString("(")) {  // bracketted expression
      match("(");
      expr = expr();
      match(")");
    }
    else
      error(" in factor syntax");

    return new FactorNode(isNumber, number, point, 
                              vertex, loopCounter, id, expr);
  }  // end of factor()



  private VertexNode vertex()
  // VERTEX  ::=  vertex '_' (NUMBER|ID) '^'*
  {
    match("vertex");
    match("_");

    double number = 0;
    String id = null;
    if (token.getType() == Token.NUMBER) {   // a number
      number = new Double( token.getValue());
      next();
    }
    else if (token.getType() == Token.NAME) {   // an ID
      id = token.getText();
      next();
    }

    int ancestor = 0;
    while (token.isString("^")) {
      match("^");
      ancestor++;
    }
    return new VertexNode(number, id, ancestor);
  }  // end of vertex()



  private LoopCounterNode loopCounter()
  // LOOPCOUNTER  ::=  loopCounter '^'*
  {
    match("loopCounter");
    int ancestor = 0;
    while (token.isString("^")) {
      match("^");
      ancestor++;
    }
    return new LoopCounterNode(ancestor);
  }  // end of loopCounter()



  private boolean isPoint(Token tok)
  // POINT  ::=  point '(' EXPR(x) ',' EXPR(y) ')' |origin| INTERSECT | WALK |TURN
  {
    if ( token.isString("point") || token.isString("origin") ||
         token.isString("intersect") || token.isString("intersect2") ||
         token.isString("turn"))
      return true;
     return false;
  }  // end of isPoint()



  private PointNode point()
  // POINT  ::=  point '(' EXPR(x) ',' EXPR(y) ')'  | origin | INTERSECT | TURN
  {
    PointNode pt = null;
    if (token.isString("point")) {
      next();   // skip "point"
      match("(");
      ExprNode x = expr();
      match(",");
      ExprNode y = expr();
      match(")");
      pt = new APointNode(x, y);
    }
    else if (token.isString("origin")) {
      next();
      pt = new OriginNode();
    }
    else if (token.isString("intersect"))
      pt = intersect(false);  // use first intersection point
    else if (token.isString("intersect2"))
      pt = intersect(true);    // use second point
    else if (token.isString("turn"))
      pt = turn();
    else
      error(" in point syntax");
    return pt;
  }  // end of point()



  private IntersectNode intersect(boolean useSecond)
  // INTERSECT  ::=   intersect|intersect2  '(' (CIRCLE|ID)  (CIRCLE|ID) ')'
  {
    next();    // consume "intersect" or "intersect2"
    match("(");

    CircleNode circle1 = null;
    String id1 = null;
    if (token.isString("circle"))
      circle1 = circle();
    else {   // get CCircle value from ID
      Token t = match(Token.NAME);
      id1 = t.getText();
    }

    CircleNode circle2 = null;
    String id2 = null;
    if (token.isString("circle"))
      circle2 = circle();
    else {   // get CCircle value from ID
      Token t = match(Token.NAME);
      id2 = t.getText();
    }

    match(")");
    return new IntersectNode(useSecond, circle1, id1, circle2, id2);
  }  // end of intersect()



  private TurnNode turn()
  // TURN  ::=  turn  '(' (CIRCLE|ID) EXPR (point on circle)  EXPR(angle) ')'
  {
    match("turn");
    match("(");
    CircleNode circle = null;
    String id = null;
    if (token.isString("circle"))
      circle = circle();
    else{   // get CCircle value from ID
      Token t = match(Token.NAME);
      id = t.getText();
    }
    ExprNode point = expr();
    ExprNode angle = expr();
    match(")");
    return new TurnNode(circle, id, point, angle);
  }  // end of turn()



  // --------------------------- shape parsing --------------------------

  private ShapeNode shape()
  // SHAPE ::=  LINE  |  CIRCLE  |  CIRCLES
  {
    if (token.isString("line"))
      return line();
    else if (token.isString("circle"))
      return circle();
    else if (token.isString("circles"))
      return circles();
    else {
      error(" in shape syntax");
      return null;
    }
  }  // end of shape()



  private LineNode line()
  // LINE  ::=  line '(' EXPR(point)  EXPR(point) ')'
  {
    match("line");
    match("(");
    ExprNode point1 = expr();
    ExprNode point2 = expr();
    match(")");
    return new LineNode(point1, point2);
  }  // end of line()


  private CircleNode circle()
  // CIRCLE  ::=  circle  '(' EXPR(point)  EXPR(radius) ')'
  {
    match("circle");
    match("(");
    ExprNode point = expr();
    ExprNode radius = expr();
    match(")");
    return new CircleNode(point, radius);
  }  // end of circle()



  private CirclesNode circles()
  // CIRCLES  ::=  circles  '(' EXPR(point)  EXPR(radius)+ ')'
  {
    match("circles");
    match("(");
    ExprNode point = expr();
    ArrayList<ExprNode> radii = new ArrayList<ExprNode>();
    do {
      radii.add( expr() );
    } while (!token.isString(")"));
    match(")");

    return new CirclesNode(point, radii);
  }  // end of circles()



  // --------------- token matching ----------------------


  private void next()
  {
    token = lexer.scan();
    // System.out.println("next(): " + token);  // useful for debugging lexer
    if (token == null) {
      System.out.println("Missing token on line " + lexer.getLineNo());
      System.exit(1);
    }
  }  // end of next()


  private Token match(int type)
  // match using types
  {
    Token t = null;
    if (token.getType() == type) {
      t = token;
      next();
    }
    else
      error("; expecting \"" + Token.getTypeName(type) + "\"");
    return t;
  }  // end of match()


  private Token match(String text)
  // match using text
  {
    Token t = null;
    if (token.isString(text)) {
      t = token;
      next();
    }
    else
      error("; expecting \"" + text + "\"");
    return t;
  }  // end of match()


  private void error(String msg)
  // after an error, just give up and die!
  {
    System.out.println("Incorrect token \"" + token.getText() +
                          "\" on line " + token.getLineNo() + msg);
    System.exit(1);
  }  // end of error()


}  // end of Parser class
