package org.xpen.ojc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;

public class P332Ltinerary {
    
    public List<String> findItinerary(String[][] tickets) {
        List<String> ans = new ArrayList<String>();
        if(tickets == null || tickets.length == 0) return ans;
        Map<String, PriorityQueue<String>> ticketsMap = new HashMap<>();
        for(int i = 0; i < tickets.length; i++) {
            if(!ticketsMap.containsKey(tickets[i][0])) ticketsMap.put(tickets[i][0], new PriorityQueue<String>());
            ticketsMap.get(tickets[i][0]).add(tickets[i][1]);
        }

        String curr = "JFK";
        Stack<String> drawBack = new Stack<String>();
        for(int i = 0; i < tickets.length; i++) {
            while(!ticketsMap.containsKey(curr) || ticketsMap.get(curr).isEmpty()) {
                drawBack.push(curr);
                curr = ans.remove(ans.size()-1);
            }
            ans.add(curr);
            curr = ticketsMap.get(curr).poll();
        }
        ans.add(curr);
        while(!drawBack.isEmpty()) ans.add(drawBack.pop());
        return ans;
    }

    public List<String> findItinerary2(String[][] tickets) {
        //count city
        Map<String, Integer> cityMap = new HashMap<String, Integer>();
        int current = 0;
        for (String[] ticket : tickets) {
            for (String aTicket : ticket) {
                if (!cityMap.containsKey(aTicket)) {
                    cityMap.put(aTicket, current);
                    current++;
                }
            }
        }
        
        int jfkNo = cityMap.get("JFK");
        
        String[] cities = new String[current];
        for (Map.Entry<String, Integer> entry: cityMap.entrySet()) {
            cities[entry.getValue()] = entry.getKey();
        }
        
        //System.out.println(Arrays.toString(cities));
        
        Digraph graph = new Digraph(current);
        for (String[] ticket : tickets) {
            int v = cityMap.get(ticket[0]);
            int w = cityMap.get(ticket[1]);
            graph.addEdge(v, w);
        }
        
        DirectedEulerianPath eulerianPath = new DirectedEulerianPath(graph, jfkNo, cities);
        List<String> result = new ArrayList<String>();
        for (int num : eulerianPath.path()) {
            result.add(0, cities[num]);
        }
        return result;
    }
    
    public static void main(String[] args) {
        P332Ltinerary main = new P332Ltinerary();
        String[][] tickets = {
                {"MUC", "LHR"}, {"JFK", "MUC"}, {"SFO", "SJC"}, {"LHR", "SFO"}
        };
        String[][] tickets2 = {
                {"JFK","SFO"},{"JFK","ATL"},{"SFO","ATL"},{"ATL","JFK"},{"ATL","SFO"}
        };
        
        main.doOne(tickets);
        main.doOne(tickets2);
    }
    
    public void doOne(String[][] tickets) {
        StopWatch sw = new StopWatch();
        
        sw.start();
        System.out.println(findItinerary(tickets));
        sw.stop();
        System.out.println(sw.getTime(TimeUnit.MICROSECONDS));
        
        sw.reset();
        sw.start();
        System.out.println(findItinerary2(tickets));
        sw.stop();
        System.out.println(sw.getTime(TimeUnit.MICROSECONDS));
        
    }
    
    class DirectedEulerianPath {
        private Stack<Integer> path = null;   // Eulerian path; null if no suh path

        public DirectedEulerianPath(Digraph G, int startNo, String[] cities) {

            // find vertex from which to start potential Eulerian path:
            int s = startNo;

            // create local view of adjacency lists, to iterate one vertex at a time
            List<Integer>[] adjTemp = (List<Integer>[]) new ArrayList[G.V()];
            for (int v = 0; v < G.V(); v++) {
                adjTemp[v] = G.adj(v);
                Collections.sort(adjTemp[v], new Comparator<Integer>() {

                    @Override
                    public int compare(Integer o1, Integer o2) {
                        return cities[o1].compareTo(cities[o2]);
                    }
                    
                });
            }
            Iterator<Integer>[] adj = (Iterator<Integer>[]) new Iterator[G.V()];
            for (int v = 0; v < G.V(); v++) {
                adj[v] = adjTemp[v].iterator();
            }

            // greedily add to cycle, depth-first search style
            Stack<Integer> stack = new Stack<Integer>();
            stack.push(s);
            path = new Stack<Integer>();
            while (!stack.isEmpty()) {
                int v = stack.pop();
                while (adj[v].hasNext()) {
                    stack.push(v);
                    v = adj[v].next();
                }
                // push vertex with no more available edges to path
                path.push(v);
            }
                
            // check if all edges have been used
            if (path.size() != G.E() + 1)
                path = null;

        }

        public Iterable<Integer> path() {
            return path;
        }

        public boolean hasEulerianPath() {
            return path != null;
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
        
        public List<Integer> adj(int v) {
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

}

