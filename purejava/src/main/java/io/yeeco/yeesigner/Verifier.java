package io.yeeco.yeesigner;

import com.debuggor.schnorrkel.sign.KeyPair;

public class Verifier {

    private com.debuggor.schnorrkel.sign.KeyPair keyPair;

    public static Verifier fromPublicKey(byte[] publicKey) throws SignerException {

        com.debuggor.schnorrkel.sign.KeyPair keyPair = null;

        try {
            keyPair = KeyPair.fromPublicKey(publicKey);
        } catch (Exception e) {
            throw new SignerException(ErrorUtils.ERR_INVALID_PUBLIC_KEY);
        }

        Verifier instance = new Verifier();
        instance.keyPair = keyPair;
        return instance;
    }


    public void verify(byte[] signature, byte[] message) throws SignerException {

        boolean verify = false;
        try {
            KeyPair fromPublicKey = KeyPair.fromPublicKey(keyPair.getPublicKey().toPublicKey());
            verify = fromPublicKey.verify(message, signature);
        }catch (Exception e){
            throw new SignerException(ErrorUtils.ERR_INVALID_SIGNATURE);
        }

        if (!verify) {
            throw new SignerException(ErrorUtils.ERR_INVALID_SIGNATURE);
        }
    }
}
