package org.xpen.chess.tictactoe.ai;

import org.xpen.chess.tictactoe.Board;
import org.xpen.chess.tictactoe.Cell;
import org.xpen.chess.tictactoe.GameMain01;
import org.xpen.chess.tictactoe.Seed;

/**
 * Abstract superclass for all AI players with different strategies.
 * To construct an AI player:
 * 1. Construct an instance (of its subclass) with the game Board
 * 2. Call setSeed() to set the computer's seed
 * 3. Call move() which returns the next move in an int[2] array of {row, col}.
 *
 * The implementation subclasses need to override abstract method move().
 * They shall not modify Cell[][], i.e., no side effect expected.
 * Assume that next move is available, i.e., not game-over yet.
 */
public abstract class AIPlayer {
   protected int ROWS = GameMain01.ROWS;  // number of rows
   protected int COLS = GameMain01.COLS;  // number of columns
 
   protected Cell[][] cells; // the board's ROWS-by-COLS array of Cells
   protected Seed mySeed;    // computer's seed
   protected Seed oppSeed;   // opponent's seed
   
   public int nodeSeachCount;
 
   /** Constructor with reference to game board */
   public AIPlayer(Board board) {
      cells = board.cells;
   }
 
   /** Set/change the seed used by computer and opponent */
   public void setSeed(Seed seed) {
      this.mySeed = seed;
      oppSeed = (mySeed == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
   }
 
   /** Abstract method to get next move. Return int[2] of {row, col} */
   public abstract int[] move();  // to be implemented by subclasses
}