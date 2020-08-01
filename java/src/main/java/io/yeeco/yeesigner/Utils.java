package io.yeeco.yeesigner;

public class Utils {

    public static byte[] copyAndFreeVec(long vecPointer, byte[] error) throws SignerException {
        byte[] result = null;
        try {
            int vecLen = (int) JNI.vecLen(vecPointer, error);
            ErrorUtils.checkErrorCode(error[0]);

            result = new byte[vecLen];
            JNI.vecCopy(vecPointer, result, error);
            ErrorUtils.checkErrorCode(error[0]);

        } catch (SignerException e) {
            throw e;
        } finally {
            JNI.vecFree(vecPointer, error);
            ErrorUtils.checkErrorCode(error[0]);
        }
        return result;
    }

}
