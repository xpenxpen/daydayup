package org.xpen.hello.compress;

import org.junit.jupiter.api.Test;

/**
 * 可以解压乱码ZIP的工具
 */
public class GarbledZipTest {
	
    //测试乱码
    @Test
    public void testDecompress() throws Exception {
    	ZipUtils.decompressZip("src/test/resources/compress/各月龄训练方案.zip", "target/compress/各月龄训练方案");
    }

}
