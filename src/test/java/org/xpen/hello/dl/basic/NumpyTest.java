package org.xpen.hello.dl.basic;

import org.bytedeco.cpython.PyObject;
import org.bytedeco.cpython.global.python;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.SizeTPointer;
import org.bytedeco.javacpp.indexer.DoubleIndexer;
import org.bytedeco.numpy.PyArrayObject;
import org.bytedeco.numpy.global.numpy;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * 演示java调用numpy
 * 
 */
public class NumpyTest {
	
	private static Pointer program;

    @BeforeAll
    public static void setUp() throws Exception {
        /* try to use MKL when available */
        //System.setProperty("org.bytedeco.openblas.load", "mkl");
        program = python.Py_DecodeLocale(NumpyTest.class.getSimpleName(), null);
        python.Py_Initialize(numpy.cachePackages());
        if (numpy._import_array() < 0) {
            System.err.println("numpy.core.multiarray failed to import");
            python.PyErr_Print();
            System.exit(-1);
        }
    }

    @AfterAll
    public static void tearDown() throws Exception {
        if (python.Py_FinalizeEx() < 0) {
            System.exit(120);
        }
        python.PyMem_RawFree(program);
    }
	
    /**
     * 测试linspace
     */
    @Test
    public void testLinspace() throws Exception {
        python.PyRun_SimpleString("import numpy;"
        		+ "y = numpy.linspace(1, 3, 9);"
        		+ "print('linspace=', y);");
    }
	
    /**
     * 测试meshgrid
     */
    @Test
    public void testMeshgrid() throws Exception {
        python.PyRun_SimpleString("import numpy as np;"
        		+ "x = np.linspace(0, 1, 3);"
        		+ "y = np.linspace(0, 1, 2);"
        		+ "xv, yv = np.meshgrid(x, y);"
        		+ "print('x=', x);"
        		+ "print('y=', y);"
        		+ "print('xv=', xv);"
        		+ "print('yv=', yv);"
        		);
    }
    
    /**
     * 测试matmul
     */
    @Test
    public void testMatmul() throws Exception {
        PyObject globals = python.PyModule_GetDict(python.PyImport_AddModule("__main__"));

        long[] dimsx = {2, 2};
        DoublePointer datax = new DoublePointer(1, 2, 3, 4);
        PyObject x = numpy.PyArray_New(numpy.PyArray_Type(), dimsx.length, new SizeTPointer(dimsx),
        		numpy.NPY_DOUBLE, null, datax, 0, numpy.NPY_ARRAY_CARRAY, null);
        python.PyDict_SetItemString(globals, "x", x);
        System.out.println("x = " + DoubleIndexer.create(datax, dimsx));

        python.PyRun_StringFlags("import numpy; y = numpy.matmul(x, x)", python.Py_single_input, globals, globals, null);

        PyArrayObject y = new PyArrayObject(python.PyDict_GetItemString(globals, "y"));
        DoublePointer datay = new DoublePointer(numpy.PyArray_BYTES(y)).capacity(numpy.PyArray_Size(y));
        long[] dimsy = new long[numpy.PyArray_NDIM(y)];
        numpy.PyArray_DIMS(y).get(dimsy);
        System.out.println("y = " + DoubleIndexer.create(datay, dimsy));
    }

}
