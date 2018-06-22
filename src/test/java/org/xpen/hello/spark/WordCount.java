package org.xpen.hello.spark;

import java.util.Arrays;
import java.util.Iterator;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import scala.Tuple2;

/**
 * 可以直接在eclipse里运行了，无需submit到spark下运行
 * 要求：
 * 1.配置HADOOP_HOME环境变量,将hadoop.dll,winutils.exe两个文件放入HADOOP_HOME/bin
 * 2.eclipse->Run as->Environment加入HADOOP_HOME
 * 3.JVM参数 -Xms256m -Xmx1024m
 * 
 * 每次运行需要手工删除"target/WordCount.txt"目录
 * 运行完检查结果target/WordCount.txt/part-00000
 *
 */
public class WordCount {
    public static void main(String[] args) throws Exception {
        //String inputFile = args[0];
        //String outputFile = args[1];
        String inputFile = "src/test/resources/spark/word.txt";
        String outputFile = "target/WordCount.txt";
        // Create a Java Spark Context.
        SparkConf conf = new SparkConf().setAppName("wordCount").setMaster("local[2]");
        JavaSparkContext sc = new JavaSparkContext(conf);
        // Load our input data.
        JavaRDD<String> input = sc.textFile(inputFile);
        // Split up into words.
        JavaRDD<String> words = input.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public Iterator<String> call(String x) {
                return Arrays.asList(x.split(" ")).iterator();
            }
        });
        // Transform into word and count.
        JavaPairRDD<String, Integer> counts = words.mapToPair(new PairFunction<String, String, Integer>() {
            @Override
            public Tuple2<String, Integer> call(String x) {
                return new Tuple2<String, Integer>(x, 1);
            }
        }).reduceByKey(new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer x, Integer y) {
                return x + y;
            }
        });
        // Save the word count back out to a text file, causing evaluation.
        counts.saveAsTextFile(outputFile);
    }
}
