package org.xpen.hello.dl.basic;

import org.junit.jupiter.api.Test;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;

/**
 * 演示djl的NDArray
 * 
 */
public class NdArrayTest {
	
    @Test
    public void testNdArray() throws Exception {
    	NDManager manager = NDManager.newBaseManager();
    	NDArray x = manager.arange(12);
    	System.out.println("x=" + x);
    	System.out.println(x.getShape());
    	
    	x = manager.create(new int[][] {
    		{1, 2, 3, 4},
    		{5, 6, 7, 8},
    		{9, 10, 11, 12},
    	});
    	System.out.println("x=" + x);
    	
    	//广播机制
    	NDArray a = manager.arange(3f).reshape(3, 1);
    	NDArray b = manager.arange(2f).reshape(1, 2);
    	System.out.println("a=" + a);
    	System.out.println("b=" + b);
    	System.out.println("a+b=" + a.add(b));
    }
    
    /**
     * 测试linspace
     */
    @Test
    public void testLinspace() throws Exception {
    	NDManager manager = NDManager.newBaseManager();
    	NDArray x = manager.linspace(1, 3, 9);
    	System.out.println("x=" + x);
    }
    
    /**
     * 向量点积
     */
    @Test
    public void testDotProduct() throws Exception {
    	NDManager manager = NDManager.newBaseManager();
    	NDArray x = manager.arange(4f);
    	NDArray y = manager.ones(new Shape(4));
    	System.out.println("x=" + x);
    	System.out.println("y=" + y);
    	System.out.println("x.dot(y)=" + x.dot(y));
    }
    
    /**
     * 矩阵乘法
     */
    @Test
    public void testMatMul() throws Exception {
    	//矩阵-向量乘法
    	NDManager manager = NDManager.newBaseManager();
    	NDArray A = manager.arange(20f).reshape(5,4);
    	NDArray x = manager.arange(4f);
    	System.out.println("A=" + A);
    	System.out.println("x=" + x);
    	System.out.println("A.matMul(x)=" + A.matMul(x));
    	
    	//矩阵-矩阵乘法
    	NDArray B = manager.ones(new Shape(4,3));
    	System.out.println("A=" + A);
    	System.out.println("B=" + B);
    	System.out.println("A.matMul(B)=" + A.matMul(B));
    }

}
