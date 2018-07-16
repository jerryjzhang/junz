import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.GeneralSecurityException;
import java.security.Key;

public class AESDemo {
    private static final String AES_TRANSFOMATION = "AES/ECB/PKCS5Padding";
    private static final Key DATA_KEY = toKey("XGAXicVG5GMBsx5bueOe4w==");

    public static void main(String [] args)throws Exception {
        String rawMsg = "this_is_imei";
        String encode = Base64.encodeBase64String(encryptData(rawMsg.getBytes()));
        System.out.println(encode);

        String decode = new String(decryptData(Base64.decodeBase64(encode)));
        System.out.println(decode);
    }

    public static byte[] encryptData(byte[] data) throws Exception{
        //执行操作
        return DATA_ENCRYPT_CIPHER.get().doFinal(data);
    }

    public static Key toKey(String base64Key) {
        SecretKey secretKey=new SecretKeySpec(Base64.decodeBase64(base64Key), "AES");
        return secretKey;
    }

    /**
     * 解密数据
     * @param data 待解密数据
     * @param key 密钥
     * @return byte[] 解密后的数据
     * */
    public static byte[] decryptData(byte[] data) throws Exception {
        //执行操作
        return DATA_DECRYPT_CIPHER.get().doFinal(data);
    }

    static final ThreadLocal<Cipher> DATA_ENCRYPT_CIPHER = new ThreadLocal<Cipher>() {
        protected Cipher initialValue() {
            Cipher cipher = null;
            try {
                cipher = Cipher.getInstance(AES_TRANSFOMATION);
                //初始化，设置为加密模式
                cipher.init(Cipher.ENCRYPT_MODE, DATA_KEY);
            } catch (GeneralSecurityException e) {
            }

            return cipher;
        }
    };

    static final ThreadLocal<Cipher> DATA_DECRYPT_CIPHER = new ThreadLocal<Cipher>() {
        protected Cipher initialValue() {
            Cipher cipher = null;
            try {
                cipher = Cipher.getInstance(AES_TRANSFOMATION);
                //初始化，设置为加密模式
                cipher.init(Cipher.DECRYPT_MODE, DATA_KEY);
            } catch (GeneralSecurityException e) {
            }

            return cipher;
        }
    };
}
