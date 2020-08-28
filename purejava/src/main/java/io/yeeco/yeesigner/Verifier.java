package io.yeeco.yeesigner;

import com.debuggor.schnorrkel.sign.KeyPair;
import com.debuggor.schnorrkel.sign.SigningContext;
import com.debuggor.schnorrkel.sign.SigningTranscript;

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


    public void verify(byte[] signature, byte[] message, byte[] ctx) throws SignerException {

        boolean verify = false;
        try {
            SigningContext ctxObj = SigningContext.createSigningContext(ctx);
            SigningTranscript t = ctxObj.bytes(message);
            KeyPair fromPublicKey = KeyPair.fromPublicKey(keyPair.getPublicKey().toPublicKey());
            verify = fromPublicKey.verify(t, signature);
        }catch (Exception e){
            throw new SignerException(ErrorUtils.ERR_INVALID_SIGNATURE);
        }

        if (!verify) {
            throw new SignerException(ErrorUtils.ERR_INVALID_SIGNATURE);
        }
    }
}
