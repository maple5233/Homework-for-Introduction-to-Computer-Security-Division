import java.util.Scanner;

/**
 * Created by hongjiyao_2014150120 on 16-9-21.
 */
public class CaesarCipher {
    private static String M;// 明文
    private static String C;// 密文
    private static final int key = 3;// 移位密钥
    private static final int num = 26;// 字母表长度

    public static void Encryption() {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < M.length(); i++) {
            char m = M.charAt(i);
            int result = 0;
            if ('A' <= m && m <= 'Z') {
                result = (m - 'A' + key) % num + 'A';
            } else if ('a' <= m && m <= 'z') {
                result = (m - 'a' + key) % num + 'a';
            } else {
                result = (int) m;
            }
            buf.append((char) (result));
        }
        C = buf.toString();
    }

    public static void Decryption() {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < C.length(); i++) {
            char c = C.charAt(i);
            int result = 0;
            if ('A' <= c && c <= 'Z') {
                result = (c - 'A' + num - key) % num + 'A';
            } else if ('a' <= c && c <= 'z') {
                result = (c - 'a' - key + num) % num + 'a';
            } else {
                result = (int) c;
            }
            buf.append((char) (result));
        }
        M = buf.toString();
    }

    public static void display() {
        System.out.print("明文是" + M + "\t");
        System.out.println("密文是" + C);
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        M = in.nextLine();
        Encryption();
        display();
        Decryption();
        display();
    }
}
