package org.xpen.ojc;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class P207CourseSchedule {
    
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        Digraph digraph = new Digraph(numCourses);
        for (int[] pair: prerequisites) {
            digraph.addEdge(pair[1], pair[0]);
        }
        
        DirectedCycle directedCycle = new DirectedCycle(digraph);
        return !directedCycle.hasCycle();
    }

    public static void main(String[] args) {
        P207CourseSchedule main = new P207CourseSchedule();
        int numCourses = 1;
        int[][] prerequisites={
        };
        System.out.println(main.canFinish(numCourses, prerequisites));
    }
    
    class DirectedCycle {
        private boolean[] marked;        // marked[v] = has vertex v been marked?
        private int[] edgeTo;            // edgeTo[v] = previous vertex on path to v
        private boolean[] onStack;       // onStack[v] = is vertex on the stack?
        private Stack<Integer> cycle;    // directed cycle (or null if no such cycle)

        /**
         * Determines whether the digraph <tt>G</tt> has a directed cycle and, if so,
         * finds such a cycle.
         * @param G the digraph
         */
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
                    assert check();
                }
            }
            onStack[v] = false;
        }

        /**
         * Does the digraph have a directed cycle?
         * @return <tt>true</tt> if the digraph has a directed cycle, <tt>false</tt> otherwise
         */
        public boolean hasCycle() {
            return cycle != null;
        }

        /**
         * Returns a directed cycle if the digraph has a directed cycle, and <tt>null</tt> otherwise.
         * @return a directed cycle (as an iterable) if the digraph has a directed cycle,
         *    and <tt>null</tt> otherwise
         */
        public Iterable<Integer> cycle() {
            return cycle;
        }


        // certify that digraph has a directed cycle if it reports one
        private boolean check() {

            if (hasCycle()) {
                // verify cycle
                int first = -1, last = -1;
                for (int v : cycle()) {
                    if (first == -1) first = v;
                    last = v;
                }
                if (first != last) {
                    System.err.printf("cycle begins with %d and ends with %d\n", first, last);
                    return false;
                }
            }


            return true;
        }

    }

    class Digraph {

        private final int V;           // number of vertices in this digraph
        private int E;                 // number of edges in this digraph
        private List<Integer>[] adj;    // adj[v] = adjacency list for vertex v
        private int[] indegree;        // indegree[v] = indegree of vertex v
        
        /**
         * Initializes an empty digraph with <em>V</em> vertices.
         *
         * @param  V the number of vertices
         * @throws IllegalArgumentException if V < 0
         */
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

        /**
         * Initializes a new digraph that is a deep copy of the specified digraph.
         *
         * @param  G the digraph to copy
         */
        public Digraph(Digraph G) {
            this(G.V());
            this.E = G.E();
            for (int v = 0; v < V; v++)
                this.indegree[v] = G.indegree(v);
            for (int v = 0; v < G.V(); v++) {
                // reverse so that adjacency list is in same order as original
                Stack<Integer> reverse = new Stack<Integer>();
                for (int w : G.adj[v]) {
                    reverse.push(w);
                }
                for (int w : reverse) {
                    adj[v].add(w);
                }
            }
        }
            
        /**
         * Returns the number of vertices in this digraph.
         *
         * @return the number of vertices in this digraph
         */
        public int V() {
            return V;
        }

        /**
         * Returns the number of edges in this digraph.
         *
         * @return the number of edges in this digraph
         */
        public int E() {
            return E;
        }


        // throw an IndexOutOfBoundsException unless 0 <= v < V
        private void validateVertex(int v) {
            if (v < 0 || v >= V)
                throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V-1));
        }

        /**
         * Adds the directed edge v->w to this digraph.
         *
         * @param  v the tail vertex
         * @param  w the head vertex
         * @throws IndexOutOfBoundsException unless both 0 <= v < V and 0 <= w < V
         */
        public void addEdge(int v, int w) {
            validateVertex(v);
            validateVertex(w);
            adj[v].add(w);
            indegree[w]++;
            E++;
        }

        /**
         * Returns the vertices adjacent from vertex <tt>v</tt> in this digraph.
         *
         * @param  v the vertex
         * @return the vertices adjacent from vertex <tt>v</tt> in this digraph, as an iterable
         * @throws IndexOutOfBoundsException unless 0 <= v < V
         */
        public Iterable<Integer> adj(int v) {
            validateVertex(v);
            return adj[v];
        }

        /**
         * Returns the number of directed edges incident from vertex <tt>v</tt>.
         * This is known as the <em>outdegree</em> of vertex <tt>v</tt>.
         *
         * @param  v the vertex
         * @return the outdegree of vertex <tt>v</tt>               
         * @throws IndexOutOfBoundsException unless 0 <= v < V
         */
        public int outdegree(int v) {
            validateVertex(v);
            return adj[v].size();
        }

        /**
         * Returns the number of directed edges incident to vertex <tt>v</tt>.
         * This is known as the <em>indegree</em> of vertex <tt>v</tt>.
         *
         * @param  v the vertex
         * @return the indegree of vertex <tt>v</tt>               
         * @throws IndexOutOfBoundsException unless 0 <= v < V
         */
        public int indegree(int v) {
            validateVertex(v);
            return indegree[v];
        }

        /**
         * Returns the reverse of the digraph.
         *
         * @return the reverse of the digraph
         */
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

}

