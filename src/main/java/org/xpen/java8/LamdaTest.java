package org.xpen.java8;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LamdaTest {

    public static void main(String[] args) {
        Arrays.asList( "a", "b", "d" ).forEach( (e) -> System.out.println( e ) );
        System.out.println("----");
        
        List<String> strings = new ArrayList<>();
        strings.add("abc");
        strings.add("xyz");
        strings.add("sos");
        strings.add("fox");
        
        Collections.sort(strings, (String a, String b) -> -(a.compareTo(b)));
        strings.forEach( (e) -> System.out.println( e ) );
        System.out.println("----");
        
        Comparator<String> c = (a, b) -> (a.compareTo(b));
        Collections.sort(strings, c);
        strings.forEach( e -> System.out.println( e ) );
        System.out.println("----");
        
        // 使用removeIf()结合Lambda表达式实现
        ArrayList<String> list = new ArrayList<>(Arrays.asList("I", "love", "you", "too"));
        list.removeIf(str -> str.length()>3); // 删除长度大于3的元素
        System.out.println(list);
        System.out.println("----");
        
        // 使用Lambda表达式实现
        list = new ArrayList<>(Arrays.asList("I", "love", "you", "too"));
        list.replaceAll(str -> {
            if(str.length()>3)
                return str.toUpperCase();
            return str;
        });
        System.out.println(list);
        System.out.println("----");
    }

}
