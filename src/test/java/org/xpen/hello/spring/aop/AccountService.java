package org.xpen.hello.spring.aop;

import java.util.List;

public interface AccountService {
    
    void insertAccount(Account account);

    List<Account> getAccounts(String name);
    
    List<Account> getAccountsEx(String name);
}
