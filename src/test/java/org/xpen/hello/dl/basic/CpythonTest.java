package org.xpen.hello.dl.basic;

import org.bytedeco.cpython.global.python;
import org.bytedeco.javacpp.Pointer;
import org.junit.jupiter.api.Test;

/**
 * 演示java调用cpython
 * 
 */
public class CpythonTest {
	
    @Test
    public void testSimple() throws Exception {
        Pointer program = python.Py_DecodeLocale(CpythonTest.class.getSimpleName(), null);
        if (program == null) {
            System.err.println("Fatal error: cannot decode class name");
            System.exit(1);
        }
        //python.Py_SetProgramName(program);  /* optional but recommended */
        python.Py_Initialize(python.cachePackages());
        python.PyRun_SimpleString("from time import time,ctime\n"
                         + "print('Today is', ctime(time()))\n"
                         + "print('2**3=', 2**3)\n"
        		);
        if (python.Py_FinalizeEx() < 0) {
            System.exit(120);
        }
        python.PyMem_RawFree(program);
    }

}
