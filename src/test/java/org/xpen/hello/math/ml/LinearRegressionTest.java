package org.xpen.hello.math.ml;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.junit.Test;

/**
 * 演示线性回归
 *
 */
public class LinearRegressionTest {
    
    //数据来自斯坦福机器学习课程 https://www.coursera.org/learn/machine-learning
    @Test
    public void testHousePrice() throws Exception {
        SimpleRegression regression = new SimpleRegression();
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                LinearRegressionTest.class.getClassLoader().getResourceAsStream("math/ml/housePrice1.txt")));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split("\\,");
            regression.addData(Double.parseDouble(data[0]), Double.parseDouble(data[1]));
        }
        
        reader.close();

        System.out.println(regression.getIntercept());
        System.out.println(regression.getSlope());
        System.out.println(regression.getSlopeStdErr());

        System.out.println(regression.predict(15));
    }

}
