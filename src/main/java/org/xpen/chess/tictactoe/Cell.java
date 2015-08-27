package org.xpen.chess.tictactoe;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

/**
 * The Cell class models each individual cell of the game board.
 */
public class Cell {
   // Package access
   public Seed content; // content of this cell (Seed.EMPTY, Seed.CROSS, or Seed.NOUGHT)
   int row, col; // row and column of this cell
 
   /** Constructor to initialize this cell with the specified row and col */
   public Cell(int row, int col) {
      this.row = row;
      this.col = col;
      clear(); // clear content
   }
 
   /** Clear this cell's content to EMPTY */
   public void clear() {
      content = Seed.EMPTY;
   }
 
   /** Paint itself on the graphics canvas, given the Graphics context */
   public void paint(Graphics g) {
      // Use Graphics2D which allows us to set the pen's stroke
      Graphics2D g2d = (Graphics2D)g;
      g2d.setStroke(new BasicStroke(GameMain01.SYMBOL_STROKE_WIDTH,
            BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND)); // Graphics2D only
      // Draw the Seed if it is not empty
      int x1 = col * GameMain01.CELL_SIZE + GameMain01.CELL_PADDING;
      int y1 = row * GameMain01.CELL_SIZE + GameMain01.CELL_PADDING;
      if (content == Seed.CROSS) {
         g2d.setColor(Color.RED);
         int x2 = (col + 1) * GameMain01.CELL_SIZE - GameMain01.CELL_PADDING;
         int y2 = (row + 1) * GameMain01.CELL_SIZE - GameMain01.CELL_PADDING;
         g2d.drawLine(x1, y1, x2, y2);
         g2d.drawLine(x2, y1, x1, y2);
      } else if (content == Seed.NOUGHT) {
         g2d.setColor(Color.BLUE);
         g2d.drawOval(x1, y1, GameMain01.SYMBOL_SIZE, GameMain01.SYMBOL_SIZE);
      }
   }
}