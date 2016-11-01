package org.xpen.hello.spring.aop;

public class WrappedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String message = null;

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
