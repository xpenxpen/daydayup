package org.xpen.graph;

import java.net.MalformedURLException;
import java.net.URL;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

/**
 * 演示jgrapht
 * 出处：官方测试代码
 *
 */
public class HelloJGraphT {

    public static void main(String[] args) {
    	Graph<String, DefaultEdge> stringGraph = createStringGraph();

        // note undirected edges are printed as: {<v1>,<v2>}
        System.out.println(stringGraph.toString());
        
        System.out.println("Edges of V1=" + stringGraph.edgesOf("v1"));

        // create a graph based on URL objects
        Graph<URL, DefaultEdge> hrefGraph = createHrefGraph();

        // note directed edges are printed as: (<v1>,<v2>)
        System.out.println(hrefGraph.toString());
    }
    
    /**
     * Creates a toy directed graph based on URL objects that represents link structure.
     *
     * @return a graph based on URL objects.
     */
    private static Graph<URL, DefaultEdge> createHrefGraph() {
    	Graph<URL, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);

        try {
            URL amazon = new URL("http://www.amazon.com");
            URL yahoo = new URL("http://www.yahoo.com");
            URL ebay = new URL("http://www.ebay.com");

            // add the vertices
            g.addVertex(amazon);
            g.addVertex(yahoo);
            g.addVertex(ebay);

            // add edges to create linking structure
            g.addEdge(yahoo, amazon);
            g.addEdge(yahoo, ebay);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return g;
    }

    /**
     * Create a toy graph based on String objects.
     *
     * @return a graph based on String objects.
     */
    private static Graph<String, DefaultEdge> createStringGraph() {
    	Graph<String, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);

        String v1 = "v1";
        String v2 = "v2";
        String v3 = "v3";
        String v4 = "v4";

        // add the vertices
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);

        // add edges to create a circuit
        g.addEdge(v1, v2);
        g.addEdge(v2, v3);
        g.addEdge(v3, v4);
        g.addEdge(v4, v1);

        return g;
    }

}
