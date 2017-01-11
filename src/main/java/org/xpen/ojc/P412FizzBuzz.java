package org.xpen.ojc;

import java.util.ArrayList;
import java.util.List;

public class P412FizzBuzz {
    
    public List<String> fizzBuzz(int n) {
        List<String> list = new ArrayList<String>();
        for (int i = 1; i <= n; i++) {
            list.add(Integer.toString(i));
        }
        for (int i = 3; i <= n; i+=3) {
            list.set(i-1, "Fizz");
        }
        for (int i = 5; i <= n; i+=5) {
            list.set(i-1, "Buzz");
        }
        for (int i = 15; i <= n; i+=15) {
            list.set(i-1, "FizzBuzz");
        }
        return list;
    }
    
    public static void main(String[] args) {
        P412FizzBuzz main = new P412FizzBuzz();
        
        List<String> fizzBuzz = main.fizzBuzz(15);
        System.out.println(fizzBuzz);
    }

}

