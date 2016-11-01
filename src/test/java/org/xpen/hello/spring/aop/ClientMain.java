package org.xpen.hello.spring.aop;

import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Spring AOP 拦截 HTTP invoker测试
 *
 */
public class ClientMain {

    public static void main(String[] args) {
        //2个冲突的名字，后者会覆盖前者
        ApplicationContext context
            //= new ClassPathXmlApplicationContext(new String[] {"spring/aop/client-beans.xml", "spring/aop/client-beans2.xml"});
            = new ClassPathXmlApplicationContext(new String[] {"spring/aop/client-beans.xml"});
        AccountService accountService = context.getBean(AccountService.class);
        
        System.out.println("----------1 START------------");
        List<Account> accounts;
        try {
            accounts = accountService.getAccounts("1");
            for (Account account : accounts) {
                System.out.println(account.getName());
            }
        } catch(Exception e) {
            System.out.println(accountService.toString());
        }
        System.out.println("----------1 END------------");
        
        System.out.println("----------2 START------------");
        accounts = accountService.getAccountsEx("2");
        for (Account account : accounts) {
            System.out.println(account.getName());
        }
        System.out.println("----------2 END------------");
    }

}
