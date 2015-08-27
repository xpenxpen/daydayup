package org.xpen.chess.puzzle8.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.xpen.chess.puzzle8.Board;

//广度优先搜索
public class SolverWidthFirst extends Solver {

	//广度优先队列
	private Queue <Board> queue;
	
    // The closed state set.
	private Set<Board> closed;

	// find a solution to the initial board
	public SolverWidthFirst(Board initial) {
		this.initial = initial;
		this.queue = new LinkedList<Board>();
		
		closed = new HashSet<Board>();
	}
	
	public Board solve() {
		lookuptimes  = 0;
		queue.add(initial);
		//System.out.println("add " + initial + " to queue");
		while (!queue.isEmpty()) {
			Board nextPoll = queue.poll();
			lookuptimes++;
			//System.out.println(lookuptimes);
			//System.out.println("next poll " + nextPoll);
			if (nextPoll.isGoal()) {
				return nextPoll;
			}
			
			closed.add(nextPoll);
			
			List<Board> neighbors = nextPoll.neighbors();
			for (Board neighbor : neighbors) {
				//System.out.println("find neighbor " + neighbor);
				if (!closed.contains(neighbor)) {
					queue.add(neighbor);
					//System.out.println("add " + neighbor + " to queue");
				}
			}
			neighbors.clear();
		}
		
		//fail
		return null;
	}

}
