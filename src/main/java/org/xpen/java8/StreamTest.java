package org.xpen.java8;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class StreamTest {

    public static void main(String[] args) {
        List<StringBuilder> strings = new ArrayList<>();
        strings.add(new StringBuilder("bed"));
        strings.add(new StringBuilder("bee"));
        strings.add(new StringBuilder("bad"));
        strings.add(new StringBuilder("bat"));
        
        strings.stream()
            .filter(s -> s.substring(0, 2).equals("be"))
            .forEach(s -> s.append("s"));
            
        
        strings.forEach( (e) -> System.out.println( e ) );
        System.out.println("----");
        
        int sum = strings.stream()
            .filter(s -> s.substring(0, 2).equals("be"))
            .mapToInt(s -> s.length())
            .sum();
        System.out.println("sum=" + sum);
        System.out.println("----");
        
        sum = strings.parallelStream()
            .filter(s -> s.substring(0, 2).equals("be"))
            .mapToInt(s -> s.length())
            .sum();
        System.out.println("sum=" + sum);
        System.out.println("----");
        
        List<StringBuilder> collect = strings.stream()
            .filter(s -> s.substring(0, 2).equals("be"))
            .collect(Collectors.toList());
        
        collect.forEach( (e) -> System.out.println( e ) );
        System.out.println("----");
        
        Optional<StringBuilder> findFirst = strings.stream()
            .filter(s -> s.substring(0, 2).equals("ba"))
            .findFirst();
        
        System.out.println(findFirst);
        System.out.println("----");

    }

}
