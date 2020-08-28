package io.yeeco.yeesigner;

import org.apache.commons.lang3.ArrayUtils;
import org.javatuples.Pair;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class CodecUtils {

    public static long nextPowerOfTwo(long value) {

        long i = 1;
        while (true) {

            if (i >= value) {
                return i;
            }

            long preI = i;
            i *= 2;
            if (i <= preI) {
                throw new RuntimeException("overflow");
            }
        }
    }

    public static int tailingZeros(long value) {

        int i = 0;
        while (value > 1) {
            value /= 2;
            i++;
        }
        return i;
    }

    public static byte[] encodeEra(long period, long current) {

        period = nextPowerOfTwo(period);
        if (period < 4) {
            period = 4;
        } else if (period > 1 << 16) {
            period = 1 << 16;
        }

        long phase = current % period;
        long quantizeFactor = (period >> 12);
        if (quantizeFactor < 1) {
            quantizeFactor = 1;
        }
        long quantizedPhase = phase / quantizeFactor * quantizeFactor;

        int x = (tailingZeros(period) - 1);
        if (x < 1) {
            x = 1;
        } else if (x > 15) {
            x = 15;
        }

        int y = (int) (quantizedPhase / quantizeFactor) << 4;

        int xy = x | y;

        byte[] encoded = new byte[2];

        encoded[0] = (byte) (0x000000FF & xy);
        encoded[1] = (byte) ((0x0000FF00 & xy) >> 8);

        return encoded;

    }

    public static byte[] encodeCompact(long value) {

        if (value < 0) {
            throw new RuntimeException("unsupported");
        }


        if (value < 1 << 6) {
            return new byte[]{(byte) (value << 2)};
        } else if (value < 1 << 14) {
            return toLE((value << 2) + 1, 2);
        } else if (value < 1 << 30) {
            return toLE((value << 2) + 2, 4);
        } else {
            int bytes = 0;
            for (long v = value; v > 0; v = (long) Math.floor(v / 256)) {
                ++bytes;
            }
            byte[] a = toLE(value, bytes);
            byte b = (byte) (3 + ((bytes - 4) << 2));

            byte[] encoded = new byte[bytes + 1];
            encoded[0] = b;
            System.arraycopy(a, 0, encoded, 1, bytes);
            return encoded;
        }

    }

    public static Pair<Long, Integer> decodeCompact(byte[] input) {

        Integer len = null;
        Long res = null;
        if (input[0] % 4 == 0) {
            // one byte
            res = (long)(input[0] >> 2);
            len = 1;
        } else if (input[0] % 4 == 1 || input[0] % 4 == -3) {
            res = fromLE(ArrayUtils.subarray(input, 0, 2)) >> 2;
            len = 2;
        } else if (input[0] % 4 == 2 || input[0] % 4 == -2) {
            res = fromLE(ArrayUtils.subarray(input, 0, 4)) >> 2;
            len = 4;
        } else {
            int n = (input[0] >> 2) + 4;
            res = fromLE(ArrayUtils.subarray(input, 1, n + 1));
            len = 1 + n;
        }

        return Pair.with(res, len);
    }

    private static byte[] toLE(long value, int size) {
        ByteBuffer bb = ByteBuffer.allocate(8); // 8 for long
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putLong(value);
        bb.rewind();
        byte[] dest = new byte[size];
        bb.get(dest, 0, size);
        return dest;
    }

    private static long fromLE(byte[] bytes) {

        ByteBuffer bb = ByteBuffer.allocate(8); // 8 for long
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put(bytes);
        bb.rewind();
        return bb.getLong();
    }

}
