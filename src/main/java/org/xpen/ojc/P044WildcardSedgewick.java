package org.xpen.ojc;

import java.util.ArrayList;
import java.util.List;


public class P044WildcardSedgewick {
    class NFA {

        private Digraph G;         // digraph of epsilon transitions
        private String regexp;     // regular expression
        private int M;             // number of characters in regular expression

        /**
         * Initializes the NFA from the specified regular expression.
         *
         * @param  regexp the regular expression
         */
        public NFA(String regexp) {
            this.regexp = regexp;
            M = regexp.length();
            G = new Digraph(M+1); 
            for (int i = 0; i < M; i++) { 
                int lp = i; 

                // closure operator (uses 1-character lookahead)
                if (i < M-1 && regexp.charAt(i+1) == '*') { 
                    G.addEdge(lp, i+1); 
                    G.addEdge(i+1, lp); 
                } 
                if (regexp.charAt(i) == '*') 
                    G.addEdge(i, i+1);
            }
        } 

        /**
         * Returns true if the text is matched by the regular expression.
         * 
         * @param  txt the text
         * @return <tt>true</tt> if the text is matched by the regular expression,
         *         <tt>false</tt> otherwise
         */
        public boolean recognizes(String txt) {
            DirectedDFS dfs = new DirectedDFS(G, 0);
            List<Integer> pc = new ArrayList<Integer>();
            for (int v = 0; v < G.V(); v++)
                if (dfs.marked(v)) pc.add(v);

            // Compute possible NFA states for txt[i+1]
            for (int i = 0; i < txt.length(); i++) {

                List<Integer> match = new ArrayList<Integer>();
                for (int v : pc) {
                    if (v == M) continue;
                    if ((regexp.charAt(v) == txt.charAt(i)) || regexp.charAt(v) == '.')
                        match.add(v+1); 
                }
                dfs = new DirectedDFS(G, match); 
                pc = new ArrayList<Integer>();
                for (int v = 0; v < G.V(); v++)
                    if (dfs.marked(v)) pc.add(v);

                // optimization if no states reachable
                if (pc.size() == 0) return false;
            }

            // check for accept state
            for (int v : pc)
                if (v == M) return true;
            return false;
        }

    }
    
    class Digraph {

        private final int V;           // number of vertices in this digraph
        private int E;                 // number of edges in this digraph
        private List<Integer>[] adj;    // adj[v] = adjacency list for vertex v
        private int[] indegree;        // indegree[v] = indegree of vertex v
        
        public Digraph(int V) {
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

    }
    
    class DirectedDFS {
        private boolean[] marked;  // marked[v] = true if v is reachable
                                   // from source (or sources)
        private int count;         // number of vertices reachable from s

        public DirectedDFS(Digraph G, int s) {
            marked = new boolean[G.V()];
            dfs(G, s);
        }

        public DirectedDFS(Digraph G, Iterable<Integer> sources) {
            marked = new boolean[G.V()];
            for (int v : sources) {
                if (!marked[v]) dfs(G, v);
            }
        }

        private void dfs(Digraph G, int v) { 
            count++;
            marked[v] = true;
            for (int w : G.adj(v)) {
                if (!marked[w]) dfs(G, w);
            }
        }

        public boolean marked(int v) {
            return marked[v];
        }

        public int count() {
            return count;
        }
    }

    public boolean isMatch(String s, String p) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < p.length(); i++) {
            char aChar = p.charAt(i);
            switch (aChar) {
                case '?': sb.append('.'); break;
                case '*': sb.append(".*"); break;
                default: sb.append(aChar);
            }
                        
        }
        NFA nfa = new NFA(sb.toString());
        return nfa.recognizes(s);
    }
    
    public void test(String a, String b) {
        boolean result = isMatch(a, b);
        System.out.println(a + "," + b + "->" + result);
    }
    
    
//    isMatch("aa","a") → false
//    isMatch("aa","aa") → true
//    isMatch("aaa","aa") → false
//    isMatch("aa", "*") → true
//    isMatch("aa", "a*") → true
//    isMatch("ab", "?*") → true
//    isMatch("aab", "c*a*b") → false
    public static void main(String[] args) {
        P044WildcardSedgewick main = new P044WildcardSedgewick();
        main.test("aa", "a");
        main.test("aa", "aa");
        main.test("aaa", "aa");
        main.test("aa", "*");
        main.test("aa", "a*");
        main.test("ab", "?*");
        main.test("aab", "c*a*b");
    }
    

}

