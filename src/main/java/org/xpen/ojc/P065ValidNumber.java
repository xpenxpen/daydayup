package org.xpen.ojc;


public class P065ValidNumber {
    
    public boolean isNumber(String s) {
        s = s.trim();
        if (s.contains("f") || s.contains("d") || s.contains("F") || s.contains("D")) {
            return false;
        }
        try {
            Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
    
    public void test(String s) {
        System.out.println(s + "-->" + isNumber(s));
    }
    
    
    public static void main(String[] args) {
        P065ValidNumber main = new P065ValidNumber();
        main.test("0");
        main.test(" 0.1 ");
        main.test("abc");
        main.test("1 a");
        main.test("2e10");
        main.test("959440.94f");
    }
}

