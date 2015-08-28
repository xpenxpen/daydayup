package org.xpen.chess.puzzle8.ai;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import org.xpen.chess.puzzle8.Board;

//IDA*搜索
public class SolverIdaStar extends Solver {

    //A* priority queue.
    private PriorityQueue <Board> queue;
	
    // The closed state set.
	private Set<Board> closed;

	// find a solution to the initial board
	public SolverIdaStar(Board initial) {
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
        
        //初始阶段曼哈顿距离，表示最少要移动步数
        int maxThreadhold = initial.priority();
        
        //迭代加深A*搜索，比如第一次迭代搜索深度最大为30，搜不到下一次迭代深度为31，直到搜到为止。越到后面越慢。
        //虽然有一些重复的搜索，比如搜31时前面30又要重搜一遍，但还是比A*要快，而且省内存。
        while (true) {
            System.out.println("Start finding goal at maxThreadhold=" + maxThreadhold);
            queue.clear();
            closed.clear();
            
            queue.add(initial);
            //System.out.println("add " + initial + " to queue");
            while (!queue.isEmpty()) {
                Board nextPoll = queue.poll();
                lookuptimes++;
                //System.out.println(lookuptimes);
                //System.out.println("next poll " + nextPoll);
                if (nextPoll.isGoal()) {
                    System.out.println("Found goal at maxThreadhold=" + maxThreadhold);
                    return nextPoll;
                }
                
                closed.add(nextPoll);
                
                List<Board> neighbors = nextPoll.neighbors();
                for (Board neighbor : neighbors) {
                    //System.out.println("find neighbor " + neighbor);
                    if (!closed.contains(neighbor) && neighbor.priority() <= maxThreadhold) {
                        queue.add(neighbor);
                        //System.out.println("add " + neighbor + " to queue");
                    }
                }
                neighbors.clear();
            }
            
            //If not found, increment maxThreadhold and find from beginning again
            maxThreadhold++;
            
        }
	}

}
