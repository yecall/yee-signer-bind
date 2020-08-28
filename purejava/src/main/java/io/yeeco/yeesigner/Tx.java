package io.yeeco.yeesigner;

import com.rfksystems.blake2b.Blake2b;
import com.rfksystems.blake2b.security.Blake2b256Digest;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;

public class Tx {

    private final static byte[] CTX = "substrate".getBytes();

    private Call call;
    private byte[] secretKey;
    private long nonce;
    private long period;
    private long current;
    private byte[] currentHash;

    public static Tx buildTx(byte[] secretKey, long nonce, long period, long current, byte[] currentHash, Call call) throws SignerException {

        Tx instance = new Tx();
        instance.call = call;
        instance.secretKey = secretKey;
        instance.nonce = nonce;
        instance.period = period;
        instance.current = current;
        instance.currentHash = currentHash;
        return instance;
    }

    public byte[] encode() throws SignerException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // version
        byte version = (byte) 0b0000_0001 | (byte) 0b1000_0000;
        out.write(version);

        KeyPair keyPair = KeyPair.fromSecretKey(secretKey);

        // sender
        byte[] publicKey = keyPair.getPublicKey();
        out.write(0xFF);
        out.write(publicKey, 0, publicKey.length);

        // era
        byte[] eraBytes = CodecUtils.encodeEra(period, current);

        // nonce
        byte[] nonceBytes = CodecUtils.encodeCompact(nonce);

        // call
        byte[] callBytes = call.encode();

        // payload
        byte[] payload = getPayload(nonceBytes, callBytes, eraBytes, currentHash);

        if (payload.length > 256) {
            payload = blake2b256(payload);
        }

        byte[] signature = keyPair.sign(payload, CTX);

        out.write(signature, 0, signature.length);
        out.write(nonceBytes, 0, nonceBytes.length);
        out.write(eraBytes, 0, eraBytes.length);

        out.write(callBytes, 0, callBytes.length);

        byte[] withoutLenBytes = out.toByteArray();
        long len = withoutLenBytes.length;
        byte[] lenBytes = CodecUtils.encodeCompact(len);

        ByteArrayOutputStream withLenOut = new ByteArrayOutputStream();
        withLenOut.write(lenBytes, 0, lenBytes.length);
        withLenOut.write(withoutLenBytes, 0, withoutLenBytes.length);

        return withLenOut.toByteArray();
    }

    private byte[] blake2b256(byte[] src) throws SignerException {

        try {
            Blake2b256Digest digest = (Blake2b256Digest) MessageDigest.getInstance(Blake2b.BLAKE2_B_256);
            digest.update(src);
            return digest.digest();
        } catch (Exception e) {
            throw new SignerException(ErrorUtils.ERR_INVALID_TX);
        }
    }

    private byte[] getPayload(byte[] nonce, byte[] call, byte[] era, byte[] currentHash) throws SignerException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            out.write(nonce);
            out.write(call);
            out.write(era);
            out.write(currentHash);
        } catch (Exception e) {
            throw new SignerException(ErrorUtils.ERR_INVALID_TX);
        }

        return out.toByteArray();

    }

}
