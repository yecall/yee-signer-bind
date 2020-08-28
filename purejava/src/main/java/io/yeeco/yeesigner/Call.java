package io.yeeco.yeesigner;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.yeeco.yeesigner.call.BalanceTransferParams;
import org.apache.commons.codec.binary.Hex;

import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;

public class Call {

    private io.yeeco.yeesigner.call.Call call;

    private static ObjectMapper objectMapper = new ObjectMapper();

    public static Call newCall(String json) throws SignerException {

        io.yeeco.yeesigner.call.Call<Object> call = null;

        try {
            call = objectMapper.readValue(json, io.yeeco.yeesigner.call.Call.class);
        } catch (Exception e) {
            throw new SignerException(ErrorUtils.ERR_INVALID_JSON);
        }

        if (call.getModule().equals(BalanceTransferParams.MODULE) && call.getMethod().equals(BalanceTransferParams.METHOD)) {
            LinkedHashMap params = (LinkedHashMap)call.getParams();
            BalanceTransferParams dispatchedParams = null;
            try {
                dispatchedParams = objectMapper.readValue(objectMapper.writeValueAsString(params), BalanceTransferParams.class);
            }catch (Exception e){
                throw new SignerException(ErrorUtils.ERR_INVALID_JSON);
            }

            dispatchedParams.replaceAddressDestIfNeeded();

            call.setParams(dispatchedParams);

        }else{
            throw new SignerException(ErrorUtils.ERR_UNSUPPORTED_METHOD);
        }


        Call instance = new Call();
        instance.call = call;
        return instance;
    }

    public byte[] encode() throws SignerException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        out.write(call.getModule());
        out.write(call.getMethod());

        try {

            if (call.getModule().equals(BalanceTransferParams.MODULE) && call.getMethod().equals(BalanceTransferParams.METHOD)) {

                BalanceTransferParams params = (BalanceTransferParams) call.getParams();
                String dest = params.getDest();
                if (dest.startsWith("0x")){
                    dest = dest.replace("0x", "");
                }
                Long value = params.getValue();

                byte[] destEncoded = Hex.decodeHex(dest);
                byte[] valueEncoded = CodecUtils.encodeCompact(value);

                out.write(destEncoded);
                out.write(valueEncoded);
            } else {
                throw new SignerException(ErrorUtils.ERR_UNSUPPORTED_METHOD);
            }

        }catch (Exception e){
            throw new SignerException(ErrorUtils.ERR_INVALID_TX);
        }

        return out.toByteArray();

    }
}
