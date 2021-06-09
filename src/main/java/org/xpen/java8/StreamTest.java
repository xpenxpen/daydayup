package org.xpen.java8;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class StreamTest {

    public static void main(String[] args) {
        // 使用Stream.forEach()迭代
        Stream<String> stream = Stream.of("I", "love", "you", "too");
        stream.forEach(str -> System.out.println(str));
        System.out.println("----");
        
        // 找出最长的单词
        stream = Stream.of("I", "love", "you", "too");
        Optional<String> longest = stream.reduce((s1, s2) -> s1.length()>=s2.length() ? s1 : s2);
        //Optional<String> longest = stream.max((s1, s2) -> s1.length()-s2.length());
        System.out.println(longest.get());
        System.out.println("----");

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
        
        IntStream.range(1, 10)
            .peek(x -> System.out.print("\nA" + x))
            .limit(3)
            .peek(x -> System.out.print("B" + x))
            .forEach(x -> System.out.print("C" + x));
        System.out.println("----");
        
        IntStream.of(2, 4, 6, 8, 10, 12, 14, 16, 18, 20)
            .peek(x -> System.out.print("\nA" + x))
            .skip(6)
            .peek(x -> System.out.print("B" + x))
            .forEach(x -> System.out.print("C" + x));
        System.out.println("----");
    }

}
