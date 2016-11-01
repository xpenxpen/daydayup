package org.xpen.hello.spring.aop;

import java.io.Serializable;

public class Account implements Serializable {
    private String name;

    public Account(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
