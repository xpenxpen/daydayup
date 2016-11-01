package org.xpen.hello.spring.aop;

import java.util.Arrays;
import java.util.List;

public class AccountServiceImpl implements AccountService {
    public void insertAccount(Account acc) {
        // do something...
    }

    public List<Account> getAccounts(String name) {
        return Arrays.asList(new Account("BankA"), new Account("BankB"), new Account("BankC"));
    }
    
    public List<Account> getAccountsEx(String name) {
        throw new IllegalArgumentException("Wrong bank");
    }
}
