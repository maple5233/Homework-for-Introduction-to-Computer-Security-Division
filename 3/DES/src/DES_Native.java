/**
 * 几乎原生JAVA实现的DES算法
 * Created by hongjiyao_2014150120 on 16-11-2.
 */

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;

import static javafx.scene.input.KeyCode.M;

public class DES_Native {

    private static final String DES = "DES";
    private static final int NUM_OF_BITS_OF_KEY = 64; // 主密钥长
    private static final int MAX_BIT_NUM = 256;       // 最大位长
    private static final int NUM_OF_SUB_KEY = 16;     // 子密钥有16个
    private static final int USED_LENGTH_OF_KEY = 56; // 实际使用的密钥长 也是选择置换表的长度
    private static final String ENCODE = "Encode";    // 加密模式
    private static final String DECODE = "Decode";    // 解密模式

    private static byte[] mainKey = null; // 主密钥
    private static boolean[][] subKey = new boolean[NUM_OF_SUB_KEY][USED_LENGTH_OF_KEY]; //子密钥组

    /**
     * 逐位拷贝
     *
     * @param out    目标位数组
     * @param in     源位数组
     * @param length 位的长度
     */
    private static void memcpy(boolean[] out, boolean[] in, int length) {
        // for (int i=0;i<length;i++) {
        //    out[i] = in[i];
        // }
        System.arraycopy(in, 0, out, 0, length);
    }

    /**
     * 逐位拷贝 指定起点
     *
     * @param out      目标位数组
     * @param in       源位数组
     * @param inBegin  源数组起始位置
     * @param outBegin 目标数组起始位置
     * @param length   拷贝长度
     */
    private static void memcpy(boolean[] out, boolean[] in, int outBegin, int inBegin, int length) {
        // for (int i = 0; i < length; i++) {
        //     out[outBegin + i] = in[inBegin + i];
        // }
        System.arraycopy(in, inBegin, out, outBegin, length);
    }

    /**
     * 将out前n个字节置为i
     *
     * @param out 操作的目标boolean数组
     * @param b   true或者false
     * @param n   置位长度
     */
    private static void memset(byte[] out, int b, int n) {
        for (int i = n - 1; i >= 0; i--) {
            out[i] = (byte) b;
        }
    }

    /**
     * 字节组转换成位组
     *
     * @param out  转化成的位数组
     * @param in   待转换的字节数组 (java用byte 一个元素占一个字节，也就是8个二进制位)
     * @param bits 总共多少位
     */
    private static void byteToBit(boolean[] out, byte[] in, int bits) {
        int temp;
        for (int i = 0; i < bits; i++) {
            temp = (in[i / 8] >> (i % 8)) & 1;
            out[i] = temp != 0;
        }
    }

    /**
     * 位数组转字节数组
     *
     * @param out  输出的字节数组
     * @param in   输入的位数组
     * @param bits 位数组的位数
     */
    private static void bitToByte(byte[] out, boolean[] in, int bits) {
        int temp;
        memset(out, 0, (bits + 7) / 8); // 前(bits + 7)/8位 置0
        for (int i = 0; i < bits; i++) {
            temp = in[i] ? 1 : 0;
            out[i / 8] |= temp << (i % 8);
        }
    }

    /**
     * 按位异或
     *
     * @param a   位数组1
     * @param b   位数组2
     * @param len 位数组3
     */
    private static void Xor(boolean[] a, boolean[] b, int len) {
        for (int i = 0; i < len; i++) {
            a[i] ^= b[i];
        }
    }

    /**
     * 用置换表进行置换
     *
     * @param out    置换后的密钥数组
     * @param in     置换前的密钥数组
     * @param table  置换表
     * @param length 置换表长度
     */
    private static void Transform(boolean[] out, boolean[] in, int[] table, int length) {
        boolean[] temp = new boolean[MAX_BIT_NUM];
        for (int i = 0; i < length; i++) {
            temp[i] = in[table[i] - 1];
        }
        memcpy(out, temp, length); // 回写
    }

    /**
     * 左移一个数组
     *
     * @param In    要操作的数组
     * @param start 从数组的哪一位开始移动
     * @param len   移动的部分的长度 这里是28
     * @param loop  移动的位移的长度 根据移动表来
     */
    private static void RotateL(boolean[] In, int start, int len, int loop) {
        boolean[] temp = new boolean[MAX_BIT_NUM];
        // start决定了是左半部分(start=0)还是右半部分(start=28)
        memcpy(temp, In, 0, start, loop); // 先把头loop位存到tmp
        memcpy(In, In, start, start + loop, len - loop); // 把尾部len-loop位移动到头部
        memcpy(In, temp, start + len - loop, 0, loop); // 再把loop位接到尾部
    }

    /**
     * 生成DES的64位主密钥(8 bytes) 用8长度的byte数组表示
     *
     * @throws NoSuchAlgorithmException 没有这种算法导致的异常
     */
    private static void generateMainKey() throws Exception {
        SecureRandom sr = new SecureRandom(); // 生成一个可信任的随机数据源
        KeyGenerator kg = KeyGenerator.getInstance(DES); // 由工厂方法得到一个KeyGenerator
        kg.init(sr); // 初始化这个KeyGenerator对象
        SecretKey _key;
        _key = kg.generateKey(); // 生成密匙
        mainKey = _key.getEncoded(); // Key为8个字节共64位
    }

    /**
     * 密钥调度算法 生成16*48 boolean数组的密钥
     */
    private static void getKeys() {

        boolean[] K = new boolean[NUM_OF_BITS_OF_KEY]; // Key转化为的boolean数组
        byteToBit(K, mainKey, NUM_OF_BITS_OF_KEY); // 字节组转换成位组

        Transform(K, K, Const.PC_1, USED_LENGTH_OF_KEY); // 选择置换1

        for (int i = 0; i < subKey.length; i++) {
            RotateL(K, 0, USED_LENGTH_OF_KEY / 2, Const.KEY_MOVE[i]); // K的左半部分循环左移
            RotateL(K, USED_LENGTH_OF_KEY / 2, USED_LENGTH_OF_KEY / 2, Const.KEY_MOVE[i]); // K的右半部分循环左移
            Transform(subKey[i], K, Const.PC_2, Const.PC_2.length);// 选择置换2
        }
    }

    /**
     * S盒代替,输入6位的数，输出4位的数，将第一六位对应的十进制数作为行，第二三四五为的对应十进制数作为列
     *
     * @param in  输入 6位(48bits)
     * @param out 输出 4位(32bits)
     */
    private static void S_func(boolean[] out, boolean[] in) {
        int inIndex = 0;
        int outIndex = 0;
        byte i = 0;
        byte j, k, tmp1, tmp2, tmp3, tmp4;
        byte[] tmp = new byte[4];

        for (; i < 8; i++, inIndex += 6, outIndex += 4) {
            tmp1 = (byte) ((in[inIndex] ? 1 : 0) << 1);
            tmp2 = (byte) ((in[inIndex + 5] ? 1 : 0));
            j = (byte) (tmp1 + tmp2);

            tmp1 = (byte) ((in[inIndex + 1] ? 1 : 0) << 3);
            tmp2 = (byte) ((in[inIndex + 2] ? 1 : 0) << 2);
            tmp3 = (byte) ((in[inIndex + 3] ? 1 : 0) << 1);
            tmp4 = (byte) ((in[inIndex + 4] ? 1 : 0));
            k = (byte) (tmp1 + tmp2 + tmp3 + tmp4);

            for (int l = 0; l < 4; l++) {
                try {
                    tmp[l] = Const.S_PLATE[i * 4 * 16 + j * 16 + k];
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            byteToBit(out, tmp, tmp.length);
        }
    }

    /**
     * F函数 费斯妥函数 其每次对半块（32位）进行操作
     * S盒，P置换和E扩张各自满足了实用密码所需的必要条件:“混淆与扩散”
     *
     * @param in       输入的半块 32位
     * @param childKey 子密钥 48位
     */
    private static void F_func(boolean[] in, boolean[] childKey) {
        boolean[] mr = new boolean[Const.E.length];
        Transform(mr, in, Const.E, Const.E.length);// E置换即扩张置换
        Xor(mr, childKey, mr.length);// 按位异或
        S_func(in, mr);// S盒置换 S盒提供了DES的核心安全性
        Transform(in, in, Const.P, Const.P.length);// P置换
    }

    /**
     * 加密/解密字节数组
     * 这个字节数组一定是64位的（8字节）
     * 因此如果要加密长于8字节的数据
     * 要分段加密
     * UTF-8 一个中文字符两个字符三个字节
     *
     * @param out  结果数组
     * @param in   输入数组
     * @param type 模式
     */
    private static void Des_Run(byte[] out, byte[] in, String type) {
        boolean[] M = new boolean[NUM_OF_BITS_OF_KEY];
        boolean[] Tmp = new boolean[NUM_OF_BITS_OF_KEY / 2];
        boolean[] Li = new boolean[NUM_OF_BITS_OF_KEY / 2];
        boolean[] Ri = new boolean[NUM_OF_BITS_OF_KEY / 2];

        byteToBit(M, in, NUM_OF_BITS_OF_KEY);// 字节组转换成位组
        Transform(M, M, Const.IP, Const.IP.length);// 初始置换

        /* 将M分块 */
        memcpy(Li, M, Li.length);
        memcpy(Ri, M, 0, Li.length, Ri.length);

        if (Objects.equals(type, ENCODE)) { // 加密模式
            for (int i = 0; i < NUM_OF_SUB_KEY; i++) {
                memcpy(Tmp, Ri, NUM_OF_BITS_OF_KEY / 2);
                F_func(Ri, subKey[i]);
                Xor(Ri, Li, NUM_OF_BITS_OF_KEY / 2);
                memcpy(Li, Tmp, NUM_OF_BITS_OF_KEY / 2);
            }
        } else { // 解密模式
            for (int i = 15; i >= 0; i--) {
                memcpy(Tmp, Li, NUM_OF_BITS_OF_KEY / 2);
                F_func(Li, subKey[i]);
                Xor(Li, Ri, 32);
                memcpy(Ri, Tmp, NUM_OF_BITS_OF_KEY / 2);
            }
        }

        /* 两块合并 */
        memcpy(M, Li, Li.length);
        memcpy(M, Ri, Li.length, 0, Ri.length);

        Transform(M, M, Const.ANTI_IP, Const.ANTI_IP.length);// 最终置换
        bitToByte(out, M, NUM_OF_BITS_OF_KEY);
    }

    public static void main(String[] args) {
        /* 密钥生成和调度 */
        try {
            generateMainKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
        getKeys();
        /* 主过程 */
        try {
            String path = "data/";

            File original = new File(path + "明文.all");
            File cipher = new File(path + "密文.all");
            File _original = new File(path + "解密文结果.all");

            FileInputStream streamFromP = new FileInputStream(original);
            FileOutputStream streamToM = new FileOutputStream(cipher);
            FileInputStream streamFromM = new FileInputStream(cipher);
            FileOutputStream streamTo_P = new FileOutputStream(_original);

            byte[] buffP = new byte[8]; // 8字节一组
            byte[] buffM = new byte[8]; // 临时数组
            int length = 0;
            /**
             * 加密过程
             */
            while (streamFromP.read(buffP) != -1){
                Des_Run(buffM,buffP,ENCODE);
                streamToM.write(buffM);
            }
            /**
             * 解密过程
             */
            while ((length = streamFromM.read(buffM)) != -1){
                Des_Run(buffP,buffM,DECODE);
                streamTo_P.write(buffP,0,length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
