import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

import static java.lang.Math.*;

/**
 * RSA加密算法
 * Created by hongjiyao_2014150120 on 16-11-30.
 */

class Dual {    // 二元组

    int x;      // 满足ax + by = gcd(a,b)的x
    int y;      // 满足ax + by = gcd(a,b)的y

    Dual(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

public class RSA {

    private final static int MAX_OF_E = 500;                    // e的上限
    private final static int MIN_OF_E = 300;                    // e的下限
    private final static int MAX_OF_16_BIT = 32767;             // 16位整数F范围-32768到32767
    private final static int BIG_NUM = 30000;                   // 大整数应该大于它
    private final static int LENGTH_OF_INT = 32;                // 整型的长度
    private static int N;                                       // 钥匙的N 两个质数的乘积
    private static int E;                                       // 公钥的E
    private static int D;                                       // 私钥的D
    private final static Scanner in = new Scanner(System.in);   // 输入器

    /**
     * 扩展的欧几里得算法
     * 给予二整数a、b，必存在有整数x、y使得ax + by = gcd(a,b)
     * 也就是ax ≡ gcd(a,b) (mod b)
     * by = gcd(a,b) (mod a)
     * 若gcd(a,b) = 1，即a,b互质
     * 称x是a在mod b情况下的乘法逆元 x是a关于b的模逆元
     * 称y是b在mod a情况下的乘法逆元 y是b关于a的模逆元
     *
     * @param a    数1
     * @param b    数2
     * @param dual 二元组x,y
     * @return gcd(a, b)
     */
    private static int ExtendedEuclidean(int a, int b, Dual dual) {
        if (b == 0) {
            dual.x = 1;
            dual.y = 0;
            return a;
        } else {
            int r = ExtendedEuclidean(b, a % b, dual);
            int _x = dual.x;
            // noinspection SuspiciousNameCombination
            dual.x = dual.y;
            dual.y = _x - ((a / b) * dual.y);
            return r;
        }
    }

    /**
     * 用于判断一个数是否为素数，若为素数，返回true,否则返回false
     *
     * @param num 输入的值
     * @return true、false
     */
    private static boolean isPrime(int num) {
        if (num < 2) {// 素数不小于2
            return false;
        } else {
            double squareRootOfNum = sqrt(num);
            for (int i = 2; i <= squareRootOfNum; i++) {
                if (num % i == 0) {// 若能被整除，则说明不是素数，返回false
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 生成一个随机的素数.
     *
     * @return 生成的素数
     */
    private static int getRandomPrime() {
        int rand;
        do {
            rand = (int) (random() * MAX_OF_16_BIT);
        } while (!isPrime(rand) || rand < BIG_NUM);
        return rand;
    }

    /**
     * 按照给定范围生成随机的E
     *
     * @return E
     */
    private static int getE() {
        return (int) (random() * (MAX_OF_E - MIN_OF_E)) + MIN_OF_E;
    }

    /**
     * 生成密钥
     */
    private static void initKey() {
        int p = getRandomPrime();
        int q = getRandomPrime();
        while (p == q || p * q < 0) {
            q = getRandomPrime();                   // 保证p!=q 且 p×q不溢出
        }
        N = p * q;
        int r = (p - 1) * (q - 1);                  // p*q的欧拉函数值
        Dual dual = new Dual(0, 0);
        do {
            E = getE();
        } while (E >= r || ExtendedEuclidean(E, r, dual) != 1);
        D = dual.x;                                 // D是E关于R的逆元
        if (D < 0) {
            initKey();                              // D溢出了 重置
        }
    }

    /**
     * 转换数字为二进制表示，并存到一个数组里.
     *
     * @param num    要转化为二进制的数字
     * @param binary 用于存储的数组
     * @return 转化的位数
     */
    private static int toBinaryNum(int num, int[] binary) {
        int i = 0;
        while (num != 0) {
            binary[i] = num % 2;
            num /= 2;
            i++;
        }
        return i;
    }

    /**
     * 快速幂运算 求 c ≡ num^e (mod N)
     *
     * @param num 要进行快速幂运算的数
     * @param e   乘方数
     * @return c
     */
    private static int PowerAndMod(int num, int e) {
        int[] bin = new int[LENGTH_OF_INT];
        long c = 1;
        int k = toBinaryNum(e, bin) - 1;

        for (int i = k; i >= 0; i--) {
            c = (c * c) % N;
            if (1 == bin[i]) {
                c = (c * num) % N;
            }
        }
        return (int) c;
    }

    /**
     * 字符串转换int数组
     *
     * @param string 字符串
     * @return 转换结果
     */
    private static int[] string2Unicode(String string) {
        int[] unicode = new int[string.length()];
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            unicode[i] = (int) c;
        }
        return unicode;
    }

    /**
     * int数组转字符串
     *
     * @param unicode 要转化的数组
     * @return 转化结果
     */
    private static String unicode2String(int[] unicode) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int u : unicode) {
            stringBuilder.append((char) u);
        }
        return stringBuilder.toString();
    }

    /**
     * 加密
     *
     * @return 加密结果
     */
    private static int[] encrypt(String originalText) {
        int[] unicode = string2Unicode(originalText);
        for (int i = 0; i < unicode.length; i++) {
            int u = unicode[i];
            unicode[i] = PowerAndMod(u, E);
        }
        return unicode;
    }

    /**
     * 解密
     *
     * @param cipherText 密文数组
     * @return 解密结果
     */
    private static String decrypt(int[] cipherText) {
        for (int i = 0; i < cipherText.length; i++) {
            int u = cipherText[i];
            cipherText[i] = PowerAndMod(u, D);
        }
        return unicode2String(cipherText);
    }

    /**
     * 打印结果
     */
    private static void displayKey() {
        System.out.println("公钥为：{ N: " + N + ", E: " + E + " }");
        System.out.println("私钥为：{ N: " + N + ", D: " + D + " }");
    }

    public static void main(String[] args) {
        initKey();
        displayKey();
        System.out.println("输入要加密的字符串：");
        String input = in.next();
        long begin = new Date().getTime();
        int[] c = encrypt(input);
        long encryptTime = new Date().getTime();
        String m = decrypt(c);
        long end = new Date().getTime();
        System.out.println("加密耗时" + (encryptTime - begin) + "ms");
        System.out.println("解密耗时" + (end - encryptTime) + "ms");
        System.out.println("加密结果：" + Arrays.toString(c));
        System.out.println("解密结果：" + m);
    }
}
