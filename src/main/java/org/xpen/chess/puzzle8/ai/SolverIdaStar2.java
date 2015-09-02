package org.xpen.chess.puzzle8.ai;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.xpen.chess.puzzle8.Board;

//IDA*搜索(不占用内存)
public class SolverIdaStar2 extends Solver {

    //A* priority queue.
    //private PriorityQueue <Board> queue;
	
    // The closed state set.
	private Set<Board> closed;
	
	//boolean solved = false;
	//Board answer;

	// find a solution to the initial board
	public SolverIdaStar2(Board initial) {
		this.initial = initial;
		
		closed = new HashSet<Board>();
	}
	
	public Board solve() {
        lookuptimes  = 0;
        Board solution = iterativeDeepening();
        return solution;
        
	}
	
	public Board iterativeDeepening() {
        //初始阶段曼哈顿距离，表示最少要移动步数
        int bound = initial.priority();
        
        Board solution = null;
        while (solution == null) {
            System.out.println("Start finding goal at maxThreadhold=" + bound);
            closed.clear();
            
            solution = depthFirstSearch(initial, bound);
            bound += 1;
        }
        
        return solution;
	}

    /**
     * 深度优先搜索
     * @param initial
     * @param bound
     * @return next cost bound
     */
	private Board depthFirstSearch(Board board, int bound) {
        lookuptimes++;
        if (board.isGoal()) {
            //solved = true;
            //answer = board;
            return board;
        } else {
            closed.add(board);
        }
        
//        int newBound = Integer.MAX_VALUE;
        List<Board> neighbors = board.neighbors();
        
        for (Board neighbor : neighbors) {
            
//            if (closed.contains(neighbor)) {
//                continue;
//            }
            
//            int b = 0;
            if (!closed.contains(neighbor)) {
                int f = neighbor.priority();
                if (f <= bound) {
                    Board result = depthFirstSearch(neighbor, bound); //search deeper
                    if (result != null) {
                        return result;
                    }
    //            } else {
    //                b = f;  //cutoff
                }
            }
            //newBound = Math.min(newBound, b); //compute next iteration's bound
        }
        
        return null;
    }

}
