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

    }

}
