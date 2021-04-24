package sid.utils.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;

public class SecurityUtils {
    //一些支持的加密和签名算法
    public static final String DES = "DES";
    public static final String AES = "AES";
    public static final String RSA = "RSA";
    public static final String MD5_WITH_RSA = "MD5withRSA";

    public static byte[] encrypt(byte[] data,byte[] key,String cipherAlgorithm)throws Exception{
        return decryptAndEncrypt(data,key,cipherAlgorithm,Cipher.ENCRYPT_MODE);
    }

    public static byte[] decrypt(byte[] data,byte[] key,String cipherAlgorithm)throws Exception{
        return decryptAndEncrypt(data,key,cipherAlgorithm,Cipher.DECRYPT_MODE);
    }

    private static byte[] decryptAndEncrypt(byte[] data,byte[] key,String cipherAlgorithm,int mode)throws Exception{
        SecretKey secretKey = new SecretKeySpec(key, cipherAlgorithm);
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        cipher.init(mode, secretKey);
        return cipher.doFinal(data);
    }

    private static String sign(byte[] data, byte[] privateKey,String signAlgorithm) throws Exception {
//        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
//        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
//        PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
//        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
//        signature.initSign(priKey);
//        signature.update(data);
//        return Base64.encodeBase64String(signature.sign());
        return  null;
    }

    private static boolean verify(byte[] data,String publicKey,String sign,String signAlgorithm)throws Exception{
//        byte[] keyBytes = Base64.decodeBase64(publicKey);
//        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
//        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
//        PublicKey pubKey = keyFactory.generatePublic(keySpec);
//        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
//        signature.initVerify(pubKey);
//        signature.update(data);
//        return signature.verify(Base64.decodeBase64(sign));
        return  false;
    }
}
