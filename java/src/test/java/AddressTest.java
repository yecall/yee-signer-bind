import io.yeeco.yeesigner.Address;
import io.yeeco.yeesigner.Call;
import io.yeeco.yeesigner.Tx;
import org.apache.commons.codec.binary.Hex;
import org.javatuples.Pair;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;

public class AddressTest {

    static {

        try {
            String libFileName = "jniLibs/" + (System.getProperty("os.name").toLowerCase().contains("mac") ? "libyee_signer.dylib" : "libyee_signer.so");

            File oriFile = new File(libFileName);

            InputStream is = new FileInputStream(oriFile);
            File file = File.createTempFile("lib", ".so");
            OutputStream os = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) != -1) {
                os.write(buffer, 0, length);
            }
            is.close();
            os.close();

            System.load(file.getAbsolutePath());
            file.deleteOnExit();

        }catch (Exception e){

        }
    }

    @Test
    public void testAddressEncode() throws Exception {


        byte[] publicKey = Hex.decodeHex("0001020304050607080900010203040506070809000102030405060708090001");

        String address = Address.encode(publicKey, "yee");

        assertEquals(address, "yee1qqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqqqsz6e3hh");


    }

    @Test
    public void testAddressDecode() throws Exception {


        String address = "yee1qqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqqqsz6e3hh";

        Pair<byte[], String> publicAndHrp = Address.decode(address);

        byte[] publicKey = publicAndHrp.getValue0();
        String hrp = publicAndHrp.getValue1();

        assertEquals(Hex.encodeHexString(publicKey), "0001020304050607080900010203040506070809000102030405060708090001");
        assertEquals(hrp, "yee");


    }

    @Test
    public void testAddressEncodeFailed() throws Exception {


        byte[] publicKey = Hex.decodeHex("000102030405060708090001020304050607080900010203040506070809000102");

        boolean success = false;
        try {
            String address = Address.encode(publicKey, "yee");
            success = true;
        }catch (Exception e){

        }

        assertEquals(success, false);
    }

    @Test
    public void testAddressDecodeFailed() throws Exception {


        String address = "abc1qqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqqqsyqcyq5rqwzqfqqqsdsk2fh";

        boolean success = false;
        try {
            Pair<byte[], String> publicAndHrp = Address.decode(address);
            success = true;
        }catch (Exception e){

        }

        assertEquals(success, false);

    }



}