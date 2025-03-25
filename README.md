# Java各种开源库积累
好好学习，天天向上。

## 编译
- 环境： JAVA 17
- mvn clean package -Dmaven.test.skip=true

## 演示内容

1)src/main/java

- org.xpen.audio 播放各种声音格式ogg,mp3,ape,flac
- org.xpen.bilibili 将bilibili缓存下来的视频转换为mp4
- org.xpen.chess.puzzle8 8数码游戏的人工智能解法（广度优先、A*、IDA*、深度优先+IDA*）
- org.xpen.chess.tictactoe tictactoe游戏的人工智能人机博弈（alpha-beta,minimax,查表法）
- org.xpen.coderead.java.util.concurrent jdk源码阅读注释
- org.xpen.concurrent CLH 自旋锁,AbstractQueuedSynchronizer类研究
- org.xpen.cv.opencv.FacemarkKazemiTest opencv人脸识别,kazemiFacemark眼睛，鼻子，嘴检测
- org.xpen.cv.opencv.FacemarkLbfTest LBFFacemark摄像头/视频五官检测
- org.xpen.cv.opencv.YOLONet YOLO v4目标检测
- org.xpen.cv.tesseract tesseract文字识别
- org.xpen.graph 演示jgraphx
- org.xpen.hello.bytecode 动态代理方案性能对比,比较了JDK, cglib, javassist proxy, javassist字节码, asm五种方式
- org.xpen.java8 java8 lamda表达式、stream api入门
- org.xpen.maze 迷宫生成算法与迷宫寻路算法(回溯法)
- org.xpen.ojc leetcode算法竞赛题
- org.xpen.perf.queue 阻塞队列性能对比

2)src/test/java

- org.xpen.hello.bean.orika spring+orika+freemarker演示,自动生成flatterner
- org.xpen.hello.bean.validator hibernate validator入门
- org.xpen.hello.cli 演示Jansi让控制台输出彩色文字
- org.xpen.hello.compress common-compress压缩测试
- org.xpen.hello.concurrent.forkjoin JDK7 fork join研究
- org.xpen.hello.crypto AES/CBC/PKCS7Padding 解密测试
- org.xpen.hello.excel 演示用poi生成一个带简单pivot table的xlsx文件
- org.xpen.hello.jmx 演示Spring JMX暴露jmx-beans.xml中配置的bean
- org.xpen.hello.math.lp 线性规划(酿酒师问题)
- org.xpen.hello.math.ml 线性回归(两种方式——commons-math和spark-ml)
- org.xpen.hello.metrics metrics演示
- org.xpen.hello.mockito mockito演示
- org.xpen.hello.network SSH连接, HTTP mock演示
- org.xpen.hello.nlp 自然语言处理(人名识别,简繁转换)
- org.xpen.hello.pdf fop,itext两种方法输出pdf
- org.xpen.hello.reflect 测试反射中一些疑问点
- org.xpen.hello.search.elasticsearch ElasticSearch索引,查询,ESQL演示
- org.xpen.hello.search.lucene lucene分词、空间搜索
- org.xpen.hello.selenium 浏览器自动化点击
- org.xpen.hello.serialize kryo入门
- org.xpen.hello.spark spark入门
- org.xpen.hello.spring.aop Spring AOP 拦截 HTTP invoker测试
- org.xpen.hello.storm storm入门
- org.xpen.hello.string 字符串转义,合法字符检查
- org.xpen.hello.svg 演示Batik输出SVG,并将SVG转为PNG
- org.xpen.hello.xml.decentxml 演示decentxml可以在保留原来xml的空格的基础上修改xml
- org.xpen.hello.xml.xslt 测试XSLT转换XML,并转为PDF
- org.xpen.hello.zookeeper 演示用Embedded ZooKeeper做单元测试

