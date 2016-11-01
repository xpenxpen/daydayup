package org.xpen.hello.spring.aop;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServerMain {

    public static void main(String[] args) {
        //启动JDK6自带的http服务器
        ApplicationContext context
            = new ClassPathXmlApplicationContext(new String[] {"spring/aop/server-beans.xml"});
        //AccountService accountService = context.getBean(AccountService.class);
    }

}
