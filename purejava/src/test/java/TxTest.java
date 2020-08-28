import io.yeeco.yeesigner.*;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class TxTest {

    @Test
    public void testBuildCall() throws Exception {

        // params json
        // dest:  address: 33 bytes, 0xFF + public key
        // value: transfer value
        String json = "{\"module\":4, \"method\":0, \"params\":{\"dest\":\"0xFF927b69286c0137e2ff66c6e561f721d2e6a2e9b92402d2eed7aebdca99005c70\",\"value\":1000}}";
        Call call = Call.newCall(json);



    }

    @Test
    public void testBuildCall2() throws Exception {

        // params json
        // dest:  address: bech32 address
        // value: transfer value
        String json = "{\"module\":4, \"method\":0, \"params\":{\"dest\":\"yee1jfakj2rvqym79lmxcmjkraep6tn296deyspd9mkh467u4xgqt3cqmtaf9v\",\"value\":1000}}";
        Call call = Call.newCall(json);

    }


    @Test
    public void testBuildTx() throws Exception {

        // params json
        // dest:  address: 33 bytes, 0xFF + public key
        // value: transfer value
        String json = "{\"module\":4, \"method\":0, \"params\":{\"dest\":\"0xFF927b69286c0137e2ff66c6e561f721d2e6a2e9b92402d2eed7aebdca99005c70\",\"value\":1000}}";
        Call call = Call.newCall(json);

        // sender secret key
        byte[] secretKey = Hex.decodeHex("0b58d672927e01314d624fcb834a0f04b554f37640e0a4c342029a996ec1450bac8afb286e210d3afbfb8fd429129bd33329baaea6b919c92651c072c59d2408");

        // sender nonce
        long nonce = 0;

        // era period: use 64
        long period = 64;

        // era current: the block number of the best block
        long current = 26491;

        // era current hash: the block hash of the best block
        byte[] currentHash = Hex.decodeHex("c561eb19e88ce3728776794a9479e41f3ca4a56ffd01085ed4641bd608ecfe13");

        Tx tx = Tx.buildTx(secretKey, nonce, period, current, currentHash, call);

        // get the raw tx
        byte[] encode = tx.encode();

        System.out.println(Hex.encodeHexString(encode));

        assertEquals(encode.length, 140);

    }

    @Test
    public void testBuildTx2() throws Exception {

        // params json
        // dest:  address: bech32 address
        // value: transfer value
        String json = "{\"module\":4, \"method\":0, \"params\":{\"dest\":\"yee1jfakj2rvqym79lmxcmjkraep6tn296deyspd9mkh467u4xgqt3cqmtaf9v\",\"value\":1000}}";
        Call call = Call.newCall(json);

        // sender secret key
        byte[] secretKey = Hex.decodeHex("0b58d672927e01314d624fcb834a0f04b554f37640e0a4c342029a996ec1450bac8afb286e210d3afbfb8fd429129bd33329baaea6b919c92651c072c59d2408");

        // sender nonce
        long nonce = 0;

        // era period: use 64
        long period = 64;

        // era current: the block number of the best block
        long current = 26491;

        // era current hash: the block hash of the best block
        byte[] currentHash = Hex.decodeHex("c561eb19e88ce3728776794a9479e41f3ca4a56ffd01085ed4641bd608ecfe13");

        Tx tx = Tx.buildTx(secretKey, nonce, period, current, currentHash, call);

        // get the raw tx
        byte[] encode = tx.encode();

        System.out.println(Hex.encodeHexString(encode));

        assertEquals(encode.length, 140);

    }


}