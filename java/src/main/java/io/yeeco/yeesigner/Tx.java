package io.yeeco.yeesigner;

public class Tx {

    private long pointer;

    public static Tx buildTx(byte[] secretKey, long nonce, long period, long current, byte[] currentHash, Call call) throws SignerException {

        byte[] error = new byte[1];
        long pointer = JNI.buildTx(secretKey, nonce, period, current, currentHash, call.getPointer(), error);
        ErrorUtils.checkErrorCode(error[0]);

        Tx instance = new Tx();
        instance.pointer = pointer;
        return instance;
    }

    public static Tx decode(byte[] raw) throws SignerException {

        byte[] error = new byte[1];
        long pointer = JNI.txDecode(raw, error);
        ErrorUtils.checkErrorCode(error[0]);

        Tx instance = new Tx();
        instance.pointer = pointer;

        return instance;
    }

    public byte[] encode() throws SignerException {

        byte[] error = new byte[1];
        long vecPointer = JNI.txEncode(pointer, error);
        ErrorUtils.checkErrorCode(error[0]);

        byte[] encode = Utils.copyAndFreeVec(vecPointer, error);

        return encode;
    }

    public void verify(byte[] currentHash) throws SignerException {

        byte[] error = new byte[1];
        JNI.verifyTx(pointer, currentHash, error);
        ErrorUtils.checkErrorCode(error[0]);

    }

    @Override
    protected void finalize() {
        byte[] error = new byte[1];
        JNI.txFree(pointer, error);
    }

}
