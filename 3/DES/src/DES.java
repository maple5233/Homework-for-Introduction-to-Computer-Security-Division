/**
 * 使用java的库的DES加密算法
 * Created by hongjiyao_2014150120 on 16-10-31.
 */

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class DES {
    private static final String DES = "DES";
    private static final String DEFAULT_CIPHER_ALGORITHM = "DES/ECB/PKCS5Padding";
    private static byte[] key = null;

    /**
     * 生成DES的64位密钥
     *
     * @throws NoSuchAlgorithmException 没有这种算法导致的异常
     */
    private static void generateKey() throws Exception {
        SecureRandom sr = new SecureRandom(); // 生成一个可信任的随机数据源
        KeyGenerator kg = KeyGenerator.getInstance(DES); // 由工厂方法得到一个KeyGenerator
        kg.init(sr); // 初始化这个KeyGenerator对象
        SecretKey _key = kg.generateKey(); // 生成密匙
        key = _key.getEncoded(); // Key为8个字节共64位
    }

    /**
     * 还原密钥
     *
     * @param key 二进制密钥
     * @return 还原后的密钥
     * @throws Exception
     */
    private static Key toKey(byte[] key) throws Exception {
        DESKeySpec dks = new DESKeySpec(key); // 实例化DES密钥规则
        SecretKeyFactory skf = SecretKeyFactory.getInstance(DES); // 实例化密钥工厂
        return skf.generateSecret(dks); // 生成密钥
    }

    /**
     * 加密
     *
     * @param data 待加密数据
     * @param key  密钥
     * @return byte[]   加密数据
     * @throws Exception
     */
    private static byte[] encrypt(byte[] data, Key key) throws Exception {
        return encrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
    }

    //    /**
    //     * 加密
    //     *
    //     * @param data 待加密数据
    //     * @param key  二进制密钥
    //     * @return byte[]   加密数据
    //     * @throws Exception
    //     */
    //    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
    //        return encrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
    //    }
    //    /**
    //     * 加密
    //     *
    //     * @param data            待加密数据
    //     * @param key             二进制密钥
    //     * @param cipherAlgorithm 加密算法/工作模式/填充方式
    //     * @return byte[]   加密数据
    //     * @throws Exception
    //     */
    //    private static byte[] encrypt(byte[] data, byte[] key, String cipherAlgorithm) throws Exception {
    //        Key k = toKey(key); //还原密钥
    //        return encrypt(data, k, cipherAlgorithm);
    //    }

    /**
     * 加密
     *
     * @param data            待加密数据
     * @param key             密钥
     * @param cipherAlgorithm 加密算法/工作模式/填充方式
     * @return byte[]   加密数据
     * @throws Exception
     */
    private static byte[] encrypt(byte[] data, Key key, String cipherAlgorithm) throws Exception {
        Cipher cipher = Cipher.getInstance(cipherAlgorithm); // 实例化
        cipher.init(Cipher.ENCRYPT_MODE, key); // 使用密钥初始化，设置为加密模式
        return cipher.doFinal(data); //执行操作
    }


    //    /**
    //     * 解密
    //     *
    //     * @param data 待解密数据
    //     * @param key  二进制密钥
    //     * @return byte[]   解密数据
    //     * @throws Exception
    //     */
    //    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
    //        return decrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
    //    }

    /**
     * 解密
     *
     * @param data 待解密数据
     * @param key  密钥
     * @return byte[]   解密数据
     * @throws Exception
     */
    private static byte[] decrypt(byte[] data, Key key) throws Exception {
        return decrypt(data, key, DEFAULT_CIPHER_ALGORITHM);
    }

    //    /**
    //     * 解密
    //     *
    //     * @param data            待解密数据
    //     * @param key             二进制密钥
    //     * @param cipherAlgorithm 加密算法/工作模式/填充方式
    //     * @return byte[]   解密数据
    //     * @throws Exception
    //     */
    //    private static byte[] decrypt(byte[] data, byte[] key, String cipherAlgorithm) throws Exception {
    //        //还原密钥
    //        Key k = toKey(key);
    //        return decrypt(data, k, cipherAlgorithm);
    //    }

    /**
     * 解密
     *
     * @param data            待解密数据
     * @param key             密钥
     * @param cipherAlgorithm 加密算法/工作模式/填充方式
     * @return byte[]   解密数据
     * @throws Exception
     */
    private static byte[] decrypt(byte[] data, Key key, String cipherAlgorithm) throws Exception {
        //实例化
        Cipher cipher = Cipher.getInstance(cipherAlgorithm);
        //使用密钥初始化，设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, key);
        //执行操作
        return cipher.doFinal(data);
    }

    /**
     * 转换字节数组为字符串
     *
     * @param data 要打印的字节数组
     * @return 字节数组转换的字符串
     */
    private static String showByteArray(byte[] data) {
        if (null == data) return null;
        StringBuilder sb = new StringBuilder("{");
        for (byte b : data) {
            sb.append(b).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}");
        return sb.toString();
    }

    public static void main(String[] args) {
        try {
            generateKey();
            Key _key = toKey(key);
            String P = "我是明文P：maple5233";
            System.out.println("加密前数据: " + P);
            byte[] encryptData = encrypt(P.getBytes(), _key); // 加密
            System.out.println("加密后数据: " + showByteArray(encryptData));
            System.out.println("密文: " + new String(encryptData));
            byte[] decryptData = decrypt(encryptData, _key); // 解密
            System.out.println("解密后数据: " + new String(decryptData));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
