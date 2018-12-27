package org.xpen.hello.spark;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.sql.SparkSession;

import scala.Tuple2;

/**
 * 
 * 可以直接在eclipse里运行了，无需submit到spark下运行
 * 要求：
 * 1.配置HADOOP_HOME环境变量,将hadoop.dll,winutils.exe两个文件放入HADOOP_HOME/bin
 * 2.eclipse->Run as->Environment加入HADOOP_HOME
 * 3.JVM参数 -Xms256m -Xmx1024m
 * 
 *
 */
public class WordCount2 {
    public static void main(String[] args) throws Exception {
        String inputFile = "src/test/resources/spark/word.txt";
        // Spark2.0中引入SparkSession，提供了一个统一的切入点简化使用
        SparkSession spark = SparkSession.builder().appName("JavaWordCount").master("local[2]").getOrCreate();
        
        // Load our input data.
        JavaRDD<String> input = spark.read().textFile(inputFile).javaRDD();
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
        
        List<Tuple2<String, Integer>> output = counts.collect();
        for (Tuple2<String, Integer> tuple : output) {
            System.out.println(tuple._1() + ": " + tuple._2());
        }
        spark.stop();
    }
}
