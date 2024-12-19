package org.xpen.hello.mockito;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import junit.framework.Assert;

/**
 * Mockito 入门
 *
 */
@ExtendWith(MockitoExtension.class)
public class MockitoTest {
    
    @Mock
    List<String> mockList;
    @Mock
    StringBuilder mockStr;
    @Mock
    List<Integer> mockList2;
    
    @Test
    public void testSimple1() throws Exception {
        when(mockList.get(0)).thenReturn("first");
        mockList.add("one");
        mockList.add("two");
        mockList.add("two");
        
        verify(mockList).add("one");
        verify(mockList, times(2)).add("two");

        Assert.assertEquals(0, mockList.size());
        Assert.assertEquals("first", mockList.get(0));
        Assert.assertNull(mockList.get(999));
    }
    
    //测试ArgumentMatchers
    @Test
    public void testArgumentMatchers() throws Exception {
        when(mockStr.indexOf(anyString(), eq(1))).thenReturn(11);
        when(mockStr.indexOf(anyString(), eq(2))).thenReturn(22);
        
        Assert.assertEquals(11, mockStr.indexOf("aa", 1));
        Assert.assertEquals(11, mockStr.indexOf("bb", 1));
        Assert.assertEquals(22, mockStr.indexOf("aa", 2));
        Assert.assertEquals(22, mockStr.indexOf("bb", 2));
        
    }
    
    //测试自定义参数匹配器
    @Test
    public void testArgThat() throws Exception {
        mockList2.add(1);
        mockList2.add(3);
        mockList2.add(5);
        verify(mockList2, times(3)).add(argThat(new ArgumentMatcher<Integer>() {
            //测试接受的参数都是奇数
            @Override
            public boolean matches(Integer arg) {
                System.out.println("arg=" + arg);
                return arg % 2 == 1;
            }
        }));
        
    }

}
