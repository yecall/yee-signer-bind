package io.yeeco.yeesigner.call;

import io.yeeco.yeesigner.Address;
import io.yeeco.yeesigner.SignerException;
import org.apache.commons.codec.binary.Hex;
import org.javatuples.Pair;

public class BalanceTransferParams {

    public final static Integer MODULE = 4;
    public final static Integer METHOD = 0;

    private String dest;
    private Long value;

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    public boolean isAddressDest() {
        return dest.startsWith("yee") || dest.startsWith("tyee");
    }

    public void replaceAddressDestIfNeeded() throws SignerException {

        if(isAddressDest()){
            Pair<byte[], String> pair = Address.decode(dest);
            byte[] publicKey = pair.getValue0();
            byte[] account = new byte[publicKey.length+1];
            account[0] = (byte)0xFF;
            System.arraycopy(publicKey, 0, account, 1, 32);

            dest =  "0x" + Hex.encodeHexString(account);
        }

    }
}
