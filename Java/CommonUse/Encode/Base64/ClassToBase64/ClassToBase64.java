import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

public class ClassToBase64 {
    public static void main(String[] args) {
        try {
            // 读取.class文件
            File file = new File("resource/SummerCMDListener.class");
            FileInputStream fis = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            fis.read(bytes);
            fis.close();

            // 将字节数组进行Base64编码
            String encoded = Base64.getEncoder().encodeToString(bytes);

            // 输出Base64编码后的字符串
            System.out.println(encoded);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}