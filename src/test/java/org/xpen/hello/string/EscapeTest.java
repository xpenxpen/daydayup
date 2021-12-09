package org.xpen.hello.string;

import org.apache.commons.text.StringEscapeUtils;
import org.junit.Test;
import org.unbescape.java.JavaEscape;
import org.unbescape.java.JavaEscapeLevel;

public class EscapeTest {
    
    @Test
    public void testEscape() throws Exception {
        String s = JavaEscape.escapeJava("abc\"def\"ghi\n\rsecond line", JavaEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
        System.out.println(s);
        s = JavaEscape.unescapeJava(s);
        System.out.println(s);
        
        s = JavaEscape.escapeJava("中文", JavaEscapeLevel.LEVEL_2_ALL_NON_ASCII_PLUS_BASIC_ESCAPE_SET);
        System.out.println(s);
        s = JavaEscape.unescapeJava(s);
        System.out.println(s);
        System.out.println("-------");
        
        s = JavaEscape.escapeJava("中文");
        System.out.println(s);
        s = StringEscapeUtils.escapeJava("中文");
        System.out.println(s);
        
        s = JavaEscape.escapeJava("\\u01 F", JavaEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
        System.out.println(s);
        s = JavaEscape.unescapeJava(s);
        System.out.println(s);
        s = JavaEscape.unescapeJava("\\u01 F\\u4E2D\\u01 F");
        System.out.println(s);
        s = JavaEscape.escapeJava(s, JavaEscapeLevel.LEVEL_1_BASIC_ESCAPE_SET);
        System.out.println(s);
        s = JavaEscape.unescapeJava(s);
        System.out.println(s);
        s = JavaEscape.unescapeJava("\\/");
        System.out.println(s);
        s = StringEscapeUtils.unescapeJava("\\/");
        System.out.println(s);
        System.out.println("-------");
        s = JavaEscape.escapeJava("abc/def");
        System.out.println(s);
        s = StringEscapeUtils.escapeJava("abc/def");
        System.out.println(s);
    }

}
