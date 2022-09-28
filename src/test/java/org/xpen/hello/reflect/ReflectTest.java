package org.xpen.hello.reflect;

import java.lang.reflect.Method;
import java.util.AbstractCollection;
import java.util.ArrayList;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ReflectTest {

    //测试反射中一些疑问点
    @Test
    public void testReflect() throws Exception {
        Method sizeMethod = ArrayList.class.getMethod("size", new Class<?>[]{});
        Method toStringMethod = ArrayList.class.getMethod("toString", new Class<?>[]{});
        Method waitMethod = ArrayList.class.getMethod("wait", new Class<?>[]{});
        
        //getDeclaringClass就是得到声明这个方法的最下层的子类
        Assertions.assertSame(ArrayList.class, sizeMethod.getDeclaringClass());
        Assertions.assertSame(AbstractCollection.class, toStringMethod.getDeclaringClass());
        Assertions.assertSame(Object.class, waitMethod.getDeclaringClass());
        
        //注意返回值有大int和小int之分
        Assertions.assertSame(Integer.TYPE, sizeMethod.getReturnType());
        Assertions.assertSame(int.class, sizeMethod.getReturnType());
        Assertions.assertNotSame(Integer.class, sizeMethod.getReturnType());
    }

}
