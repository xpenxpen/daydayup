package org.xpen.hello.math.ml;

import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.ml.regression.LinearRegression;
import org.apache.spark.ml.regression.LinearRegressionModel;
import org.apache.spark.ml.regression.LinearRegressionTrainingSummary;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class LinearRegressionSparkTest {

    public static void main(String[] args) {
        String inputFile = "src/test/resources/math/ml/housePrice2.txt";
        
        SparkSession spark = SparkSession
                .builder()
                .appName("Java Spark SQL example")
                .config("spark.sql.warehouse.dir", "D:/sparkwarehouse")
                .master("local[2]")
                .getOrCreate();
        
        // Load training data.
        Dataset<Row> training = spark.read().format("libsvm")
          .load(inputFile);
        
        training.show();

        LinearRegression lr = new LinearRegression()
          .setMaxIter(10)
          .setRegParam(0.3)
          .setElasticNetParam(0.8);

        // Fit the model.
        LinearRegressionModel lrModel = lr.fit(training);

        // Print the coefficients and intercept for linear regression.
        System.out.println("Coefficients: "
          + lrModel.coefficients() + " Intercept: " + lrModel.intercept());

        // Summarize the model over the training set and print out some metrics.
        LinearRegressionTrainingSummary trainingSummary = lrModel.summary();
        System.out.println("numIterations: " + trainingSummary.totalIterations());
        System.out.println("objectiveHistory: " + Vectors.dense(trainingSummary.objectiveHistory()));
        trainingSummary.residuals().show();
        System.out.println("RMSE: " + trainingSummary.rootMeanSquaredError());
        System.out.println("r2: " + trainingSummary.r2());
        
    }

}
