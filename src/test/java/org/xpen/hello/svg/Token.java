package org.xpen.hello.svg;

// Token.java
// Andrew Davison, August 2010, ad@fivedots.coe.psu.ac.th

/* A Token object represents a token extracted from the input file.
   The type of a token can be a name, keyword, number, symbol, string or EOF.

  The keywords are listed in KEYWORDS[], the symbols in SYMBOLS[].

  Each token also stores a value (e.g. the number for a NUMBER token), and
  its line number in the input file.
*/



public class Token
{
  // token types (encoded as ints)
  public final static int NAME = 0;
  public final static int KEYWORD = 1;
  public final static int NUMBER = 2;
  public final static int SYMBOL = 3;
  public final static int STRING = 4;
  public final static int EOF = 5;

  private static final String[] TOKEN_NAMES = new String[] {
    "name", "keyword", "number", "symbol", "string", "eof"   // same order as type ints
  };

  private static final String[] KEYWORDS = new String[] {
    "point", "origin", "intersect", "intersect2", "turn", 
    "line", "circle", "circles",
    "let", "draw", "cycle", "print", "vertex", "loopCounter", "pi",
    "gray", "red", "green", "blue", "yellow", "orange"
  };
    

  private static final char[] SYMBOLS = new char[] {
    '{', '}', '=', '+', '-', '*', '/', '(', ',', ')', '_', '^', '%'
  };


  private int type;     // of the token
  private String text;
  private double value;
  private int lineNo;



  public Token(int t, String str, double val, int no)
  { type = t;
    text = str;
    value = val;
    lineNo = no;
  }

  
  public boolean isString(String s)
  {  return text.equals(s);  }
  
  public int getType()
  {  return type;  }

  public String getText()
  {  return text;  }

  public double getValue()
  {  return value;  }

  public int getLineNo()
  {  return lineNo;  }


  public String toString()
  {  
    return "< " + Token.getTypeName(type) + ", \"" + text + "\", " + 
                  ((type == Token.SYMBOL) ? (char)value : ""+value) + 
                   ", line " + lineNo + " >" ;  
  }

  // --------------------- static methods ------------------

  public static int isKeyword(String value)
  {
    for(int i=0; i < KEYWORDS.length; i++)
      if (value.equals(KEYWORDS[i]))
        return i;
    return -1;
  }  // end of isKeyword()



  public static boolean isSymbol(char value)
  {
    for(char ch : SYMBOLS)
      if (value == ch)
        return true;
    return false;
  }  // end of isSymbol()


  public static String getTypeName(int type)
  {
    if ((type >= 0) && (type < TOKEN_NAMES.length))
      return TOKEN_NAMES[type];
    return "unknown";
  }

}  // end of Token class
