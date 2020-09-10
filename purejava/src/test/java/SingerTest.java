import io.yeeco.yeesigner.KeyPair;
import io.yeeco.yeesigner.SignerException;
import io.yeeco.yeesigner.Verifier;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SingerTest {


    @Test
    public void testKeyPairGenerate() throws Exception {

        KeyPair keyPair = KeyPair.generate();

        byte[] publicKey = keyPair.getPublicKey();
        assertEquals(publicKey.length, 32);

        byte[] secretKey = keyPair.getSecretKey();
        assertEquals(secretKey.length, 64);

    }

    @Test
    public void testKeyPairFromMiniSecretKey() throws Exception {

        byte[] miniSecretKey = Hex.decodeHex("579d7aa286b37b800b95fe41adabbf0c2a577caf2854baeca98f8fb242ff43ae");
        KeyPair keyPair = KeyPair.fromMiniSecretKey(miniSecretKey);

        byte[] publicKey = keyPair.getPublicKey();
        assertEquals(Hex.encodeHexString(publicKey), "4ef0125fab173ceb93ce4c2a97e6824396240101b9c7220e3fd63e3a2282cf20");

        byte[] secretKey = keyPair.getSecretKey();
        assertEquals(Hex.encodeHexString(secretKey), "e08d5baee7dae0f0463994503b812677c9523bce7653f724a59d28cf35581f73cd859d888ab8f09aa0ff3b1075e0c1629cd491433e00dfb07e5a154312cc7d9b");

    }
    @Test
    public void testKeyPairFromMiniSecretKeyFail() throws Exception {

        byte[] miniSecretKey = Hex.decodeHex("579d7aa286b37b800b95fe41adabbf0c2a577caf2854baeca98f8fb242ff43");

        String message = null;
        try {
            KeyPair keyPair = KeyPair.fromMiniSecretKey(miniSecretKey);
        }catch (SignerException e){
            message = e.getMessage();
        }

        assertEquals(message, "invalid mini secret key");
    }


    @Test
    public void testKeyPairFromSecretKey() throws Exception {

        byte[] secretKey = Hex.decodeHex("e08d5baee7dae0f0463994503b812677c9523bce7653f724a59d28cf35581f73cd859d888ab8f09aa0ff3b1075e0c1629cd491433e00dfb07e5a154312cc7d9b");
        KeyPair keyPair = KeyPair.fromSecretKey(secretKey);

        byte[] publicKey = keyPair.getPublicKey();
        assertEquals(Hex.encodeHexString(publicKey), "4ef0125fab173ceb93ce4c2a97e6824396240101b9c7220e3fd63e3a2282cf20");

        secretKey = keyPair.getSecretKey();
        assertEquals(Hex.encodeHexString(secretKey), "e08d5baee7dae0f0463994503b812677c9523bce7653f724a59d28cf35581f73cd859d888ab8f09aa0ff3b1075e0c1629cd491433e00dfb07e5a154312cc7d9b");
    }

    @Test
    public void testKeyPairSignVerify() throws Exception {

        byte[] secretKey = Hex.decodeHex("a8666e483fd6c26dbb6deeec5afae765561ecc94df432f02920fc5d9cd4ae206ead577e5bc11215d4735cee89218e22f2d950a2a4667745ea1b5ea8b26bba5d6");
        KeyPair keyPair = KeyPair.fromSecretKey(secretKey);

        byte[] message = new byte[]{1, 2, 3};

        byte[] signature = keyPair.sign(message);

        System.out.println(Hex.encodeHexString(signature));

        Verifier verifier = Verifier.fromPublicKey(keyPair.getPublicKey());

        verifier.verify(signature, message);

    }

    @Test
    public void testKeyPairSignVerifyFail() throws Exception {

        byte[] secretKey = Hex.decodeHex("a8666e483fd6c26dbb6deeec5afae765561ecc94df432f02920fc5d9cd4ae206ead577e5bc11215d4735cee89218e22f2d950a2a4667745ea1b5ea8b26bba5d6");
        KeyPair keyPair = KeyPair.fromSecretKey(secretKey);

        byte[] message = new byte[]{1, 2, 3};

        byte[] signature = keyPair.sign(message);

        Verifier verifier = Verifier.fromPublicKey(keyPair.getPublicKey());

        signature[0] = 0;

        boolean ok = false;
        try {
            verifier.verify(signature, message);
            ok = true;
        } catch (Exception e) {
        }

        assertEquals(ok, false);

    }

}