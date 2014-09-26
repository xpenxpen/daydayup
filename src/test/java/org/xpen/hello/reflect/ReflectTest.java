package org.xpen.hello.reflect;

import java.lang.reflect.Method;
import java.util.AbstractCollection;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

public class ReflectTest {

    //测试反射中一些疑问点
    @Test
    public void testReflect() throws Exception {
        Method sizeMethod = ArrayList.class.getMethod("size", new Class<?>[]{});
        Method toStringMethod = ArrayList.class.getMethod("toString", new Class<?>[]{});
        Method waitMethod = ArrayList.class.getMethod("wait", new Class<?>[]{});
        
        //getDeclaringClass就是得到声明这个方法的最下层的子类
        Assert.assertSame(ArrayList.class, sizeMethod.getDeclaringClass());
        Assert.assertSame(AbstractCollection.class, toStringMethod.getDeclaringClass());
        Assert.assertSame(Object.class, waitMethod.getDeclaringClass());
        
        //注意返回值有大int和小int之分
        Assert.assertSame(Integer.TYPE, sizeMethod.getReturnType());
        Assert.assertSame(int.class, sizeMethod.getReturnType());
        Assert.assertNotSame(Integer.class, sizeMethod.getReturnType());
    }

}
