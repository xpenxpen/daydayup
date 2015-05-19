package org.xpen.hello.svg;

// Lexer.java
// Andrew Davison, August 2010, ad@fivedots.coe.psu.ac.th

/* Tokenize (lex) an input file using Java's StreamTokenizer.
   The tokens used here are EOF, NUMBER, WORD, with some extra work
   to separate keywords from ordinary words, and to catch strings as
   tokens.

   The Parser keeps calling scan() to get another Token object.
*/

import java.io.*;


public class Lexer
{
  private StreamTokenizer tokenizer = null;


  public Lexer(String fnm)
  {
    try {
      tokenizer = new StreamTokenizer( new FileReader(fnm));
      // tokenizer.wordChars('_', '_');     // count "_" as a word character, so a Java ID is a word
      tokenizer.ordinaryChar('/');
         /* '/' is the default comment character, which results in the parser 
             ignoring all characters after it on the same line. */
      tokenizer.slashStarComments(true);  // see http://forums.sun.com/thread.jspa?threadID=321539
      tokenizer.slashSlashComments(true);
    }
    catch(IOException e)
    {  System.out.println("Could not open " + fnm);
       System.exit(1);
    }
  }  // end of Lexer()




  public Token scan()
  // returns the next Token or null
  {
    int lineNo = -1;
    try {
      tokenizer.nextToken();
      // System.out.println("tok: " + tokenizer.ttype + "; " +
      //                       tokenizer.sval + " " +  (int)tokenizer.nval);
      lineNo = tokenizer.lineno();
    }
    catch (IOException e)
    {  System.out.println(e);
       return null;
    }

    switch (tokenizer.ttype) {

    case StreamTokenizer.TT_EOF:
      return new Token(Token.EOF, "EOF", -1, lineNo);

    case StreamTokenizer.TT_NUMBER:
      double val = tokenizer.nval;
      return new Token(Token.NUMBER, ""+val, val, lineNo);

    case StreamTokenizer.TT_WORD:
      String word = tokenizer.sval;
      int keyPosn = Token.isKeyword(word);
      if (keyPosn >= 0)
        return new Token(Token.KEYWORD, word, keyPosn, lineNo);
      else
        return new Token(Token.NAME, word, -1, lineNo);

    case '\"':
      String str = tokenizer.sval;
      // System.out.println("String: \"" + str + "\"");
      return new Token(Token.STRING, str, -1, lineNo);

    default:
      int type = tokenizer.ttype;
      if (Token.isSymbol((char)type))
        return new Token(Token.SYMBOL, ""+(char)type, type, lineNo);
      else {
        System.out.println("Unknown symbol: \'" + (char)type + "\' on line " + lineNo);
        return null;
      }
    }
  }  // end of scan()


  public int getLineNo()
  {  return tokenizer.lineno();  }

}  // end of Lexer class
