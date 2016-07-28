package org.xpen.ojc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class P005LpsDp {
    
    public String longestPalindrome(String s) {
        int totalLength = s.length();
        if (s==null || totalLength==1) {return s;}
        
        boolean[][] p = new boolean[totalLength][totalLength];
        int result1 = 0;
        int result2 = 0;
        
        int step = 2;
        int finalStep = 3;
        if (totalLength == 2) {
            finalStep = 2;
        }
        for (int round = step; round <= finalStep; round++) {
            for (int i = 0, j = i + round - 1; j < totalLength; i++, j++) {
                if (s.charAt(i) == s.charAt(j)) {
                    p[i][j] = true;
                    result1 = i;
                    result2 = j;
                }
            }
        }
        
        step = 4;
        for (int round = step; round <= totalLength; round++) {
            for (int i = 0, j = i + round - 1; j < totalLength; i++, j++) {
                if (p[i+1][j-1] && s.charAt(i) == s.charAt(j)) {
                    p[i][j] = true;
                    result1 = i;
                    result2 = j;
                }
            }
        }
       
        return s.substring(result1, result2 + 1);
    }
    
    
    public void test(String s) {
        String result = s + "-->\n" + longestPalindrome(s);
        //System.out.println(result);
    }
    
    public static void main(String[] args) {
        Date startTime = new Date();
        P005LpsDp main = new P005LpsDp();
        main.test("aa");
        main.test("abacdefg");
        main.test("abcabbaabc");
        main.test("12134xy43121");
        main.test("12134abbaX43121");
        main.test("12345abbaX54321");
        for (int i=0;i<8000;i++) {
        main.test("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaabcaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        }
        Date endTime = new Date();
        System.out.println("cost:"+ (endTime.getTime()-startTime.getTime())/1000+ "s");
        
        
    }
    
}

