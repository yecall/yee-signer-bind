package io.yeeco.yeesigner;

import com.google.common.primitives.Bytes;
import org.apache.commons.codec.binary.Hex;
import org.javatuples.Pair;

public class Address {

    private static final byte[] DECODE_TABLE = {
            //  0   1   2   3   4   5   6   7   8   9   A   B   C   D   E   F
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 00-0f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 10-1f
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, // 20-2f
            -1, -1, 26, 27, 28, 29, 30, 31, -1, -1, -1, -1, -1, -1, -1, -1, // 30-3f 2-7
            -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, // 40-4f A-O
            15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,                     // 50-5a P-Z
            -1, -1, -1, -1, -1, // 5b - 5f
            -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, // 60 - 6f a-o
            15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,                     // 70 - 7a p-z/**/
    };

    public static String encode(byte[] publicKey, String hrp) throws SignerException {

        checkHrp(hrp);
        checkPublicKeyLength(publicKey);

        Bech32Utils utils = Bech32Utils.getInstance();

        byte[] prog = null;
        try {
            prog = utils.convertBits(Bytes.asList(publicKey), 8, 5, true);
        }catch (Exception e){
            throw new SignerException(ErrorUtils.ERR_ADDRESS_DECODE_ERROR);
        }

        return utils.bech32Encode(hrp.getBytes(), prog);

    }

    public static Pair<byte[], String> decode(String address) throws SignerException {

        Bech32Utils utils = Bech32Utils.getInstance();

        Pair<byte[], byte[]> pair = null;

        try {
            pair = utils.bech32Decode(address);
        }catch (Exception e){
            throw new SignerException(ErrorUtils.ERR_ADDRESS_ENCODE_ERROR);
        }

        byte[] hrpBytes = pair.getValue0();
        byte[] prog = pair.getValue1();

        String hrp = new String(hrpBytes);
        checkHrp(hrp);

        byte[] publicKey = null;
        try {
            publicKey = utils.convertBits(Bytes.asList(prog), 5, 8, false);
        }catch (Exception e){
            throw new SignerException(ErrorUtils.ERR_ADDRESS_DECODE_ERROR);
        }
        checkPublicKeyLength(publicKey);

        return Pair.with(publicKey, hrp);

    }

    private static void checkHrp(String hrp) throws SignerException {
        if (!"yee".equals(hrp) && !"tyee".equals(hrp)) {
            throw new SignerException(ErrorUtils.ERR_INVALID_HRP);
        }
    }

    private static void checkPublicKeyLength(byte[] publicKey) throws SignerException {
        if (publicKey.length != 32) {
            throw new SignerException(ErrorUtils.ERR_INVALID_PUBLIC_KEY);
        }
    }

}
