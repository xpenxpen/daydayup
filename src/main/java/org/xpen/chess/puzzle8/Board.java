package org.xpen.chess.puzzle8;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Board {
	
    // Tiles for successfully completed puzzle.
	private static byte[] goalTiles;

	private static int dimension;

	// Tiles left to right, top to bottom.
	private byte[] tiles;

	// Index of space (zero) in tiles
	private int spaceIndex;

	// Number of moves from start.
	private final int g;
	// Heuristic value (difference from goal)
	private final int h;
	// Previous state in solution chain.
	private final Board prev;

	// construct a board from an N*N array of tiles
	public Board(int dimension, byte[] tiles) {
		Board.dimension = dimension;
		this.tiles = Arrays.copyOf(tiles, tiles.length);
		this.spaceIndex = index(tiles, 0);
		this.g = 0;
		this.h = heuristic(tiles);
		this.prev = null;
		
		//set goal
		goalTiles = new byte[dimension * dimension];
        for (byte i = 0; i < goalTiles.length; i++) {
        	goalTiles[i] = (byte)(i + 1);
        }
        goalTiles[dimension * dimension - 1] = 0;
	}
	
    // Build a successor to prev by sliding tile from given index.
	public Board(Board prev, int moveIndex) {
		this.tiles = Arrays.copyOf(prev.tiles, prev.tiles.length);
		this.tiles[prev.spaceIndex] = this.tiles[moveIndex];
		this.tiles[moveIndex] = 0;
		this.spaceIndex = moveIndex;
		this.g = prev.g + 1;
		this.h = heuristic(tiles);
		this.prev = prev;
	}

	// For our A* heuristic, we just use sum of Manhatten distances of all
	// tiles.
	private int heuristic(byte[] tiles) {
		int h = 0;
		for (int i = 0; i < tiles.length; i++) {
			if (tiles[i] != 0) {
				h += manhattan(i, tiles[i] - 1);
			}
		}

		return h;
	}

	// Return the index of val in given byte array or -1 if none found.
	private int index(byte[] a, int val) {
		for (int i = 0; i < a.length; i++)
			if (a[i] == val)
				return i;
		return -1;
	}

	// return sum of Manhattan distances between blocks and goal
	public int manhattan(int a, int b) {
		return Math.abs(a / dimension - b / dimension)
				+ Math.abs(a % dimension - b % dimension);
	}
	
	public int step() {
		return g;
	}

    // A* priority function (often called F in books).
	public int priority() {
        return g + h;
    }

    // Return true iif this is the goal state.
	public boolean isGoal() {
        return Arrays.equals(tiles, goalTiles);
    }

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Board) {
			Board other = (Board) obj;
			return Arrays.equals(tiles, other.tiles);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(tiles);
	}

	// return an Iterable of all neighboring board positions
	public List<Board> neighbors() {
		List<Board> boards = new ArrayList<Board>();
		// move space up, down, left, right 这里思路反转，考虑将空白上下左右移动一格
		//up
		if ((spaceIndex - dimension) >= 0) {
			boards.add(new Board(this, spaceIndex - dimension));
		}
		//down
		if ((spaceIndex + dimension) <= (dimension * dimension - 1)) {
			boards.add(new Board(this, spaceIndex + dimension));
		}
		//left
		if (spaceIndex % dimension > 0) {
			boards.add(new Board(this, spaceIndex - 1));
		}
		//right
		if (spaceIndex % dimension < dimension - 1) {
			boards.add(new Board(this, spaceIndex + 1));
		}
		return boards;
	}
	
	public boolean isSolvable() {
		//逆序数：从左到右，前面的数字比当前的数字大的个数
		//排列的逆序数：一个排列中所有数字的逆序数之和
		//如果初始状态和目标状态的奇偶性相同，则有解，否则无解。
		return ((reversedCount(tiles) & 1) == (reversedCount(goalTiles) & 1));
		
	}
	
	private int reversedCount(byte[] tiles) {
		byte[] omitBlankTiles = new byte[tiles.length - 1];
		int copyIndex = 0;
		for (int i = 0; i < tiles.length; i++) {
			if (tiles[i] == 0) {
				continue;
			}
			omitBlankTiles[copyIndex++] = tiles[i];
		}
		
		int result = 0;
		for (int i = 0; i < omitBlankTiles.length; i++) {
			for (int j = 0; j < i; j++) {
				if (omitBlankTiles[j] > omitBlankTiles[i]) {
					result++;
				}
			}
			
		}
		return result;
	}

	// return a string representation of the board
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("p = " + priority() + " = g+h = " + g + "+" + h + "\n");
		for (int i = 0; i < dimension; i++) {
			for (int j = 0; j < dimension; j++) {
				if (tiles[i * dimension + j] == 0) {
					sb.append("   _");
				} else {
					if (tiles[i * dimension + j] < 10) {
						sb.append("   " + tiles[i * dimension + j]);
					} else {
						sb.append("  " + tiles[i * dimension + j]);
					}
				}
			}
			sb.append('\n');
		}
		return sb.toString();
	}
	
	public void printAll() {
		if (prev != null) {
			prev.printAll();
		}
        System.out.println();
        System.out.print(this);
	}

	// test client
	public static void main(String[] args) {
		int dimension = 3;
		List<Byte> initialList = new ArrayList<Byte>();
		byte[] initial = new byte[dimension * dimension];
        for (byte i = 0; i < initial.length; i++) {
        	initialList.add(i);
        }
        
        Collections.shuffle(initialList);
        
        for (int i = 0; i <  initialList.size(); i++) {
        	initial[i] = initialList.get(i);
        }
        
        Board init = new Board(dimension, initial);
        System.out.println(init);
        
        List<Board> neighbors = init.neighbors();
        System.out.println("neighbors:");
        for (Board neighbor : neighbors) {
            System.out.println(neighbor);
        }

	}
}
