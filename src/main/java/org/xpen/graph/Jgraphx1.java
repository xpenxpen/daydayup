package org.xpen.graph;
import javax.swing.JFrame;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
 
/**
 * 演示jgraphx
 *
 */
public class Jgraphx1 extends JFrame {
 
    private static final long serialVersionUID = 5777622946344441026L;

    public Jgraphx1() {
        super("Hello, World!");

        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();

        graph.getModel().beginUpdate();
        try {
            Object v1 = graph.insertVertex(parent, null, "Hello", 20, 20, 50, 50);
            Object v2 = graph.insertVertex(parent, null, "World!", 240, 150, 50, 50);
            graph.insertEdge(parent, null, "Edge", v1, v2);
        } finally {
            graph.getModel().endUpdate();
        }

        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        getContentPane().add(graphComponent);
    }

    public static void main(String[] args) {
        Jgraphx1 frame = new Jgraphx1();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 320);
        frame.setVisible(true);
    } 

}