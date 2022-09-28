package org.xpen.hello.string;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.common.base.CharMatcher;

public class MatchTest {
    
    @Test
    public void testMatch() throws Exception {
        //大小写字母以及数字
        CharMatcher CHAR_MATCHER_ALPHANUMBER = 
                CharMatcher.inRange('0', '9')
                .or(CharMatcher.inRange('a', 'z'))
                .or(CharMatcher.inRange('A', 'Z'));
        
        Assertions.assertTrue(CHAR_MATCHER_ALPHANUMBER.matchesAllOf("abcXYZ123"));
        Assertions.assertFalse(CHAR_MATCHER_ALPHANUMBER.matchesAllOf("abc_XYZ123"));
    }

}
