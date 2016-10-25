/**
 * RC4加密算法 从命令行参数读取文件名 加密再解密
 * Created by hongjiyao_2014150120 on 16-10-19.
 */

import java.io.*;

public class RC4 {
    private static final int LENGTH_OF_S = 255;
    private static final int LENGTH_OF_K = 256;
    private static final String KEY = "maple5233";
    private static int[] S = new int[LENGTH_OF_S];// S盒子

    /***
     * 快速交换函数
     *
     * @param a 交换的第一个数
     * @param b 交换的第二个数
     */
    private static void swap(int a, int b) {
        a = a ^ b;
        b = a ^ b;
        a = a ^ b;
    }

    /***
     * 根据密钥来生产加密盒子S
     *
     * @param K 密钥
     */
    private static void initS(String K) {
        int j = 0;
        int[] T = new int[LENGTH_OF_S];
        /* S的初始化 */
        for (int i = 0; i < LENGTH_OF_S; i++) {
            S[i] = i;
            T[i] = (int) K.charAt(i % K.length());
        }
        /* S的初始置换 */
        for (int i = 0; i < LENGTH_OF_S; i++) {
            j = (j + S[i] + T[i]) % LENGTH_OF_K;
            swap(S[i], S[j]);
        }
    }

    /***
     * 加密和解密
     *
     * @param P 密文或者明文
     * @return 明文或者密文
     */
    private static String code(String P) {
        int i = 0, j = 0, l = 0, t = 0, tmp = 0;
        char k;
        StringBuilder K = new StringBuilder();
        while (l < P.length()) {
            i = (i + 1) % LENGTH_OF_K;
            j = (j + S[i]) % LENGTH_OF_K;
            swap(S[i], S[j]);
            t = (S[i] + S[j]) % LENGTH_OF_K;
            tmp = (int) P.charAt(l) ^ S[t];
            k = (char) tmp;
            K.append(k);
            l++;
        }
        return K.toString();
    }

    /***
     * 主函数 将命令行第一个参数作为文件名，加密该文件并生成密文文件和解密文件。
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {

        if (args.length == 0) {
            System.out.println("您调用main方法时没有指定任何参数！");
            return;
        }

        initS(KEY); // 初始化S;

        File here = new File("./");
        String fileName = args[0];
        File file = null;
        String path = null;

        /* 获取当前路径和明文文件 */
        try {
            path = here.getCanonicalPath();
            file = new File(path + '/' + fileName);
            if (!file.exists()) {
                System.out.println("文件不存在!");
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        /* 获取密文文件和解密文件 */
        File _file = new File(path + '/' + fileName + "-outM");
        File __file = new File(path + '/' + fileName + "-outP");
        try {
            if (!_file.createNewFile() || !__file.createNewFile()) {
                System.out.println("检测到文件已经存在，将覆盖内容");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            assert file != null;
            BufferedReader pReader = new BufferedReader(new FileReader(file));// 明文输入字符流
            BufferedWriter mWriter = new BufferedWriter(new FileWriter(_file));// 密文输出字符流
            BufferedReader mReader = new BufferedReader(new FileReader(_file));// 密文输入字符流
            BufferedWriter ppWriter = new BufferedWriter(new FileWriter(__file)); //密文输出字节流
            StringBuilder fileBufferP = new StringBuilder();
            StringBuilder fileBufferPP = new StringBuilder();
            /* 加密过程 */
            int c;
            while ((c = pReader.read()) != -1) {
                fileBufferP.append((char) c);
            }
            String M = code(fileBufferP.toString()); // 生成密文
            mWriter.write(M); // 写入密文文件
            mWriter.flush();
            mWriter.close();
            /* 解密过程 */
            while ((c = mReader.read()) != -1) {
                fileBufferPP.append((char) c);
            }
            String P = code(fileBufferPP.toString()); // 生成明文
            ppWriter.write(P); // 写入解密文件
            ppWriter.flush();
            ppWriter.close();
            pReader.close();
            mReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

