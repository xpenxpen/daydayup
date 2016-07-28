package org.xpen.ojc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;


public class P210CourseSchedule2 {
    
    public int[] findOrder(int numCourses, int[][] prerequisites) {
        Digraph digraph = new Digraph(numCourses);
        for (int[] pair: prerequisites) {
            digraph.addEdge(pair[0], pair[1]);
        }
        Topological topological = new Topological(digraph);
        if (!topological.hasOrder()) {
            return new int[0];
        }
        Iterable<Integer> order = topological.order();
        int index = 0;
        int[] result = new int[numCourses];
        for (int num : order) {
            result[index] = num;
            index++;
        }
        
        return result;
    }

    public static void main(String[] args) {
        P210CourseSchedule2 main = new P210CourseSchedule2();
        int numCourses = 4;
        int[][] prerequisites={
                {1,0},{2,0},{3,1},{3,2}
        };
        System.out.println(Arrays.toString(main.findOrder(numCourses, prerequisites)));
    }
    
    class DirectedCycle {
        private boolean[] marked;        // marked[v] = has vertex v been marked?
        private int[] edgeTo;            // edgeTo[v] = previous vertex on path to v
        private boolean[] onStack;       // onStack[v] = is vertex on the stack?
        private Stack<Integer> cycle;    // directed cycle (or null if no such cycle)

        public DirectedCycle(Digraph G) {
            marked  = new boolean[G.V()];
            onStack = new boolean[G.V()];
            edgeTo  = new int[G.V()];
            for (int v = 0; v < G.V(); v++)
                if (!marked[v] && cycle == null) dfs(G, v);
        }

        // check that algorithm computes either the topological order or finds a directed cycle
        private void dfs(Digraph G, int v) {
            onStack[v] = true;
            marked[v] = true;
            for (int w : G.adj(v)) {

                // short circuit if directed cycle found
                if (cycle != null) return;

                //found new vertex, so recur
                else if (!marked[w]) {
                    edgeTo[w] = v;
                    dfs(G, w);
                }

                // trace back directed cycle
                else if (onStack[w]) {
                    cycle = new Stack<Integer>();
                    for (int x = v; x != w; x = edgeTo[x]) {
                        cycle.push(x);
                    }
                    cycle.push(w);
                    cycle.push(v);
                }
            }
            onStack[v] = false;
        }

        public boolean hasCycle() {
            return cycle != null;
        }

        public Iterable<Integer> cycle() {
            return cycle;
        }

    }

    class Digraph {

        private final int V;           // number of vertices in this digraph
        private int E;                 // number of edges in this digraph
        private List<Integer>[] adj;    // adj[v] = adjacency list for vertex v
        private int[] indegree;        // indegree[v] = indegree of vertex v
        
        public Digraph(int V) {
            if (V < 0) throw new IllegalArgumentException("Number of vertices in a Digraph must be nonnegative");
            this.V = V;
            this.E = 0;
            indegree = new int[V];
            adj = (List<Integer>[]) new List[V];
            for (int v = 0; v < V; v++) {
                adj[v] = new ArrayList<Integer>();
            }
        }
            
        public int V() {
            return V;
        }

        public int E() {
            return E;
        }


        public void addEdge(int v, int w) {
            adj[v].add(w);
            indegree[w]++;
            E++;
        }

        public Iterable<Integer> adj(int v) {
            return adj[v];
        }

       public int outdegree(int v) {
            return adj[v].size();
        }

        public int indegree(int v) {
            return indegree[v];
        }

        public Digraph reverse() {
            Digraph R = new Digraph(V);
            for (int v = 0; v < V; v++) {
                for (int w : adj(v)) {
                    R.addEdge(w, v);
                }
            }
            return R;
        }
    }
    
    class DepthFirstOrder {
        private boolean[] marked;          // marked[v] = has v been marked in dfs?
        private int[] pre;                 // pre[v]    = preorder  number of v
        private int[] post;                // post[v]   = postorder number of v
        private Queue<Integer> preorder;   // vertices in preorder
        private Queue<Integer> postorder;  // vertices in postorder
        private int preCounter;            // counter or preorder numbering
        private int postCounter;           // counter for postorder numbering

        public DepthFirstOrder(Digraph G) {
            pre    = new int[G.V()];
            post   = new int[G.V()];
            postorder = new LinkedList<Integer>();
            preorder  = new LinkedList<Integer>();
            marked    = new boolean[G.V()];
            for (int v = 0; v < G.V(); v++)
                if (!marked[v]) dfs(G, v);
        }

        // run DFS in digraph G from vertex v and compute preorder/postorder
        private void dfs(Digraph G, int v) {
            marked[v] = true;
            pre[v] = preCounter++;
            preorder.add(v);
            for (int w : G.adj(v)) {
                if (!marked[w]) {
                    dfs(G, w);
                }
            }
            postorder.add(v);
            post[v] = postCounter++;
        }

        public int pre(int v) {
            return pre[v];
        }

        public int post(int v) {
            return post[v];
        }

        public Iterable<Integer> post() {
            return postorder;
        }

        public Iterable<Integer> pre() {
            return preorder;
        }

        public Iterable<Integer> reversePost() {
            Stack<Integer> reverse = new Stack<Integer>();
            for (int v : postorder)
                reverse.push(v);
            return reverse;
        }


    }

    class Topological {
        private Iterable<Integer> order;  // topological order
        private int[] rank;               // rank[v] = position of vertex v in topological order

        public Topological(Digraph G) {
            DirectedCycle finder = new DirectedCycle(G);
            if (!finder.hasCycle()) {
                DepthFirstOrder dfs = new DepthFirstOrder(G);
                order = dfs.reversePost();
                rank = new int[G.V()];
                int i = 0;
                for (int v : order)
                    rank[v] = i++;
            }
        }

        public Iterable<Integer> order() {
            return order;
        }

        public boolean hasOrder() {
            return order != null;
        }

        public int rank(int v) {
            if (hasOrder()) return rank[v];
            else            return -1;
        }


    }
}

