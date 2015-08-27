package org.xpen.chess.tictactoe.ai;

import org.xpen.chess.tictactoe.Board;
import org.xpen.chess.tictactoe.Seed;

/**
 * Computer move based on simple table lookup of preferences
 * The simplest computer strategy is to place the seed on the first empty cell in this order:
 *  the center, one of the four corners, one of the four sides.
 */
public class AIPlayerTableLookup extends AIPlayer {
 
   // Moves {row, col} in order of preferences. {0, 0} at top-left corner
   private int[][] preferredMoves = {
         {1, 1}, {0, 0}, {0, 2}, {2, 0}, {2, 2},
         {0, 1}, {1, 0}, {1, 2}, {2, 1}};
 
   /** constructor */
   public AIPlayerTableLookup(Board board) {
      super(board);
   }
 
   /** Search for the first empty cell, according to the preferences
    *  Assume that next move is available, i.e., not gameover
    *  @return int[2] of {row, col}
    */
   @Override
   public int[] move() {
      for (int[] move : preferredMoves) {
         if (cells[move[0]][move[1]].content == Seed.EMPTY) {
            return move;
         }
      }
      assert false : "No empty cell?!";
      return null;
   }
}