package io.yeeco.yeesigner;

import com.debuggor.schnorrkel.sign.PrivateKey;
import com.debuggor.schnorrkel.sign.Signature;

public class KeyPair {

    private com.debuggor.schnorrkel.sign.KeyPair keyPair;

    public static KeyPair generate() throws SignerException {

        com.debuggor.schnorrkel.sign.KeyPair keyPair = com.debuggor.schnorrkel.sign.KeyPair.generateKeyPair();

        KeyPair instance = new KeyPair();
        instance.keyPair = keyPair;
        return instance;
    }

    public static KeyPair fromMiniSecretKey(byte[] miniSecretKey) throws SignerException {

        if (miniSecretKey.length != 32) {
            throw new SignerException(ErrorUtils.ERR_INVALID_MINI_SECRET_KEY);
        }

        com.debuggor.schnorrkel.sign.KeyPair keyPair = com.debuggor.schnorrkel.sign.KeyPair.fromSecretSeed(miniSecretKey);

        KeyPair instance = new KeyPair();
        instance.keyPair = keyPair;
        return instance;
    }

    public static KeyPair fromSecretKey(byte[] secretKey) throws SignerException {
        com.debuggor.schnorrkel.sign.KeyPair keyPair = com.debuggor.schnorrkel.sign.KeyPair.fromPrivateKey(secretKey);

        KeyPair instance = new KeyPair();
        instance.keyPair = keyPair;
        return instance;
    }

    public byte[] getPublicKey() throws SignerException {

        return keyPair.getPublicKey().toPublicKey();
    }

    public byte[] getSecretKey() throws SignerException {

        PrivateKey privateKey = keyPair.getPrivateKey();
        if (privateKey != null) {
            return privateKey.toPrivateKey();
        }
        return null;
    }

    public byte[] sign(byte[] message) throws SignerException {

        byte[] sign = null;
        try {
            Signature signature = keyPair.sign(message);
            sign = signature.to_bytes();
        } catch (Exception e) {
            throw new SignerException(ErrorUtils.ERR_INVALID_SIGNATURE);
        }

        return sign;
    }

}
