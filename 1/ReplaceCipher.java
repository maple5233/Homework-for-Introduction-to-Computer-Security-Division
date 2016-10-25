import java.util.HashMap;
import java.util.Scanner;

/**
 * Created by hongjiyao_2014150120 on 16-9-21.
 */
public class ReplaceCipher {
    private static String M;// 明文
    private static String C;// 密文
    private static HashMap<Character, Character> K;// 加密替换表
    private static HashMap<Character, Character> _K;// 解密替换表
    private static final int num = 26;// 字母表长度

    public static void initKey() {// 随机生成替换表
        K = new HashMap<Character, Character>();
        _K = new HashMap<Character, Character>();
        while (K.size() < num) {// 生成大写字母替换表
            char key = (char) ('A' + K.size());
            char value;
            do {
                value = (char) (Math.random() * num + 'A');
            } while (K.containsValue(value));
            K.put(key, value);
            _K.put(value, key);
        }
        while (K.size() < num * 2) {// 生成小写字母替换表
            char key = (char) ('a' + K.size() - num);
            char value;
            do {
                value = (char) (Math.random() * num + 'a');
            } while (K.containsValue(value));
            K.put(key, value);
            _K.put(value, key);
        }
    }

    public static void displayKey() {
        System.out.println("加密替换表:");
        for (int i = 0; i < num; i++) {
            char big = (char) ('A' + i);
            char small = (char) ('a' + i);
            System.out.println(big + "-" + K.get(big) + "\t" + small + "-" + K.get(small));
        }
    }

    public static void Encryption() {
        if (M == null) return;
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < M.length(); i++) {
            char m = M.charAt(i);
            if (K.containsKey(m)) {
                buf.append(K.get(m));
            } else {
                buf.append(m);
            }
        }
        C = buf.toString();
    }

    public static void Decryption() {
        if (C == null) return;
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < C.length(); i++) {
            char m = C.charAt(i);
            if (_K.containsKey(m)) {
                buf.append(_K.get(m));
            } else {
                buf.append(m);
            }
        }
        M = buf.toString();
    }

    public static void display() {
        System.out.print("明文是" + M + "\t");
        System.out.println("密文是" + C);
    }

    public static void main(String[] args) {
        initKey();
        displayKey();
        Scanner in = new Scanner(System.in);
        M = in.nextLine();
        Encryption();
        display();
        Decryption();
        display();
    }
}
