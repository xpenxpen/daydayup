package org.xpen.hello.spring.aop;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Spring HTTP Invoker 在 Spring 5.3中被标记为 deprecated
 * 随后在 Spring 6.0里被完全移除
 * 如果想要运行，请手工降级spring
 *
 */
public class ServerMain {

    public static void main(String[] args) {
        //启动JDK6自带的http服务器
        ApplicationContext context
            = new ClassPathXmlApplicationContext(new String[] {"spring/aop/server-beans.xml"});
        //AccountService accountService = context.getBean(AccountService.class);
    }

}
