package org.xpen.hello.string;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.CharMatcher;

public class MatchTest {
    
    @Test
    public void testMatch() throws Exception {
        //大小写字母以及数字
        CharMatcher CHAR_MATCHER_ALPHANUMBER = 
                CharMatcher.inRange('0', '9')
                .or(CharMatcher.inRange('a', 'z'))
                .or(CharMatcher.inRange('A', 'Z'));
        
        Assert.assertTrue(CHAR_MATCHER_ALPHANUMBER.matchesAllOf("abcXYZ123"));
        Assert.assertFalse(CHAR_MATCHER_ALPHANUMBER.matchesAllOf("abc_XYZ123"));
    }

}
