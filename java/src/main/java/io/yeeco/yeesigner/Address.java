package io.yeeco.yeesigner;

import org.javatuples.Pair;

public class Address {

    public static String encode(byte[] publicKey, String hrp) throws SignerException {

        byte[] hrpBytes = hrp.getBytes();

        byte[] error = new byte[1];
        long vecPointer = JNI.addressEncode(publicKey, hrpBytes, error);
        ErrorUtils.checkErrorCode(error[0]);

        byte[] result = Utils.copyAndFreeVec(vecPointer, error);

        String address = null;
        try {
            address = new String(result, "UTF-8");
        } catch (Exception e) {
            throw new SignerException("charset error");
        }

        return address;
    }

    public static Pair<byte[], String> decode(String address) throws SignerException {

        byte[] addressBytes = address.getBytes();

        byte[] error = new byte[1];
        long[] publicKeyVecPointer = new long[1];
        long[] hrpVecPointer = new long[1];
        JNI.addressDecode(addressBytes, publicKeyVecPointer, hrpVecPointer, error);
        ErrorUtils.checkErrorCode(error[0]);

        byte[] publicKey = Utils.copyAndFreeVec(publicKeyVecPointer[0], error);
        byte[] hrpBytes = Utils.copyAndFreeVec(hrpVecPointer[0], error);

        String hrp = null;
        try {
            hrp = new String(hrpBytes, "UTF-8");
        } catch (Exception e) {
            throw new SignerException("charset error");
        }

        return Pair.with(publicKey, hrp);

    }

}
