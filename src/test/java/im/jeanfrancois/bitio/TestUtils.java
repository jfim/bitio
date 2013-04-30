package im.jeanfrancois.bitio;

import im.jeanfrancois.bitio.util.BitIOUtils;
import junit.framework.TestCase;

/**
 * TODO Document me! :3
 *
 * @author jfim
 */
public class TestUtils extends TestCase {
    public void testZigZag() {
        assertEquals(0, BitIOUtils.encodeAsZigZag(0));
        assertEquals(1, BitIOUtils.encodeAsZigZag(-1));
        assertEquals(2, BitIOUtils.encodeAsZigZag(1));
        assertEquals(3, BitIOUtils.encodeAsZigZag(-2));
        assertEquals(4, BitIOUtils.encodeAsZigZag(2));

        for(int i = -100; i < 100; ++i) {
            assertEquals(i, BitIOUtils.decodeZigZag(BitIOUtils.encodeAsZigZag(i)));
        }
    }
}
