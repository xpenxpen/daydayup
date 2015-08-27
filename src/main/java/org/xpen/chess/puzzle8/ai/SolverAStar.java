package org.xpen.chess.puzzle8.ai;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import org.xpen.chess.puzzle8.Board;

//A*搜索
public class SolverAStar extends Solver {

    //A* priority queue.
    private PriorityQueue <Board> queue;
	
    // The closed state set.
	private Set<Board> closed;

	// find a solution to the initial board
	public SolverAStar(Board initial) {
		this.initial = initial;
        this.queue = new PriorityQueue<Board>(100, new Comparator<Board>(){
            @Override
            public int compare(Board o1, Board o2) {
                return o1.priority() - o2.priority();
            }
    
        });
		
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
