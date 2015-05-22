package org.xpen.hello.jmx;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 演示Spring JMX暴露jmx-beans.xml中配置的bean
 *
 */
public class JmxTest {

    //运行以后打开jconsole可以查看mbean
    //2种方式：本地进程和远程进程
    //2种方式都不需要额外的命令行-Dcom.sun.management.jmxremote参数
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("jmx/jmx-beans.xml");

        // 通过死循环保证主线程一直运行
        while (true) {

        }
    }
}
