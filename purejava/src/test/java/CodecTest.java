import io.yeeco.yeesigner.CodecUtils;
import org.apache.commons.codec.binary.Hex;
import org.javatuples.Pair;
import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class CodecTest {

    @Test
    public void testNexPowerOfTwo() throws Exception {

        long a = CodecUtils.nextPowerOfTwo(10);
        assertEquals(a, 16l);

        a = CodecUtils.nextPowerOfTwo(16);
        assertEquals(a, 16l);

        a = CodecUtils.nextPowerOfTwo(Long.MAX_VALUE/2 + 1);
        assertEquals(a, 4611686018427387904l);

        boolean overflow = false;
        try {
            a = CodecUtils.nextPowerOfTwo(Long.MAX_VALUE -1);
        }catch (RuntimeException e){
            overflow = true;
        }
        assertEquals(overflow, true);

    }

    @Test
    public void testTailingZeros() throws Exception {

        int a = CodecUtils.tailingZeros(16);
        assertEquals(a, 4);

        a = CodecUtils.tailingZeros(1);
        assertEquals(a, 0);

        a = CodecUtils.tailingZeros(32);
        assertEquals(a, 5);

    }

    @Test
    public void testEncodeEra() throws Exception {

        byte[] encoded = CodecUtils.encodeEra(64, 45);

        assertEquals(encoded[0], (byte)-43);
        assertEquals(encoded[1], (byte)2);


        encoded = CodecUtils.encodeEra(64, 26491);

        assertEquals(encoded[0], (byte)-75);
        assertEquals(encoded[1], (byte)3);

    }

    @Test
    public void testEncodeCompact() throws Exception {

        byte[] encoded =  CodecUtils.encodeCompact(1000);

        assertEquals(Hex.encodeHexString(encoded), "a10f");

        encoded =  CodecUtils.encodeCompact(1000000);

        assertEquals(Hex.encodeHexString(encoded), "02093d00");

        encoded =  CodecUtils.encodeCompact(1000000000000L);

        assertEquals(Hex.encodeHexString(encoded), "070010a5d4e8");

        encoded =  CodecUtils.encodeCompact(1000000000000000L);

        assertEquals(Hex.encodeHexString(encoded), "0f0080c6a47e8d03");

    }

    @Test
    public void testDecodeCompact() throws Exception {

        Pair<Long, Integer> decoded = CodecUtils.decodeCompact(Hex.decodeHex("a10f"));

        assertEquals(decoded.getValue0(), 1000L);
        assertEquals(decoded.getValue1(), 2);

        decoded = CodecUtils.decodeCompact(Hex.decodeHex("02093d00"));

        assertEquals(decoded.getValue0(), 1000000L);
        assertEquals(decoded.getValue1(), 4);

        decoded = CodecUtils.decodeCompact(Hex.decodeHex("070010a5d4e8"));

        assertEquals(decoded.getValue0(), 1000000000000L);
        assertEquals(decoded.getValue1(), 6);

        decoded = CodecUtils.decodeCompact(Hex.decodeHex("0f0080c6a47e8d03"));

        assertEquals(decoded.getValue0(), 1000000000000000L);
        assertEquals(decoded.getValue1(), 8);



    }

}