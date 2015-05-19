package org.xpen.hello.svg;

import org.junit.Test;

/**
 * 演示Batik输出SVG
 *
 */
public class BatikTest {

    @Test
    public void testSimple() throws Exception {
        CropCircles.main(new String[]{"barburyCastle.txt"});
    }

}
