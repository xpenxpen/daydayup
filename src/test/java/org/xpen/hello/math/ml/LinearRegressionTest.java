package org.xpen.hello.math.ml;

import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.chart.ui.UIUtils;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;

/**
 * 演示线性回归
 *
 */
public class LinearRegressionTest {
    
    protected static List<Double> areas = new ArrayList<Double>();
    protected static List<Double> prices = new ArrayList<Double>();
    protected static SimpleRegression regression = new SimpleRegression();
    
    //数据来自斯坦福机器学习课程 https://www.coursera.org/learn/machine-learning
    public static void main(String[] args) throws Exception {
        
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                LinearRegressionTest.class.getClassLoader().getResourceAsStream("math/ml/housePrice1.txt")));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split("\\,");
            regression.addData(Double.parseDouble(data[0]), Double.parseDouble(data[1]));
            areas.add(Double.parseDouble(data[0]));
            prices.add(Double.parseDouble(data[1]));
        }
        
        reader.close();

        System.out.println(regression.getIntercept());
        System.out.println(regression.getSlope());
        System.out.println(regression.getSlopeStdErr());

        System.out.println(regression.predict(15));
        
        ChartDemo1 demo = new ChartDemo1("JFreeChart");
        demo.pack();
        UIUtils.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }

}

class ChartDemo1 extends ApplicationFrame {

    private static final long serialVersionUID = 1L;

    public ChartDemo1(String title) {
        super(title);
        XYDataset dataset = createDataset();
        XYDataset dataset2 = createDataset2();
        JFreeChart chart = createChart(dataset, dataset2);
        ChartPanel chartPanel = new ChartPanel(chart, false);
        chartPanel.setFillZoomRectangle(true);
        chartPanel.setMouseWheelEnabled(true);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        setContentPane(chartPanel);
    }

    private static XYDataset createDataset() {
        DefaultXYDataset dataset = new DefaultXYDataset();
        double[][] data = new double[2][LinearRegressionTest.areas.size()];
        for (int i = 0; i < LinearRegressionTest.areas.size(); i++) {
            data[0][i] = LinearRegressionTest.areas.get(i);
            data[1][i] = LinearRegressionTest.prices.get(i);
        }
        dataset.addSeries("s1", data);
        return dataset;
    }

    private static XYDataset createDataset2() {
        DefaultXYDataset dataset = new DefaultXYDataset();
        double[][] data = new double[2][2];
        for (int i = 0; i < LinearRegressionTest.areas.size(); i++) {
            data[0][0] = 0;
            data[1][0] = LinearRegressionTest.regression.getIntercept();
            
            data[0][1] = 22;
            data[1][1] = 22 * LinearRegressionTest.regression.getSlope() +  LinearRegressionTest.regression.getIntercept();
        }
        dataset.addSeries("s2", data);
        return dataset;
    }

    private static JFreeChart createChart(XYDataset dataset, XYDataset dataset2) {
        JFreeChart chart = ChartFactory.createScatterPlot(
            "House Price", "Area" /* x-axis label*/, 
                "Price" /* y-axis label */, dataset);
        chart.setBackgroundPaint(Color.WHITE);
        chart.getLegend().setFrame(BlockBorder.NONE);
        XYPlot plot = (XYPlot) chart.getPlot();

        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        
        plot.setDataset(2, dataset2);
        renderer.setSeriesPaint(0, Color.BLUE);
        plot.setRenderer(2, renderer);
        

        return chart;
    }


}
