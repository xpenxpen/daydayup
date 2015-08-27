package org.xpen.chess.puzzle8.ai;

import org.xpen.chess.puzzle8.Board;


public abstract class Solver {
    protected Board initial;
    protected int lookuptimes;
    
    public int getLookuptimes() {
        return lookuptimes;
    }
   
 
   /** Abstract method to solve */
   public abstract Board solve();  // to be implemented by subclasses
}