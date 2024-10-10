import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.GZIPInputStream;

public class GzipBase64ToClassFile {

    public static void main(String[] args) {
        // 示例 Base64 字符串
        String base64String = "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";

        try {
            // 解码 Base64 字符串
            byte[] decodedBytes = decodeBase64(base64String);

            // 解压缩 GZIP 数据
            byte[] decompressedBytes = gzipDecompress(decodedBytes);

            // 将字节数组写入 .class 文件
            writeBytesToFile(decompressedBytes, "output/YourClass.class");

            System.out.println("Class file created successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static byte[] decodeBase64(String base64String) {
        return Base64.getDecoder().decode(base64String);
    }

    private static byte[] gzipDecompress(byte[] compressedData) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(compressedData);
        GZIPInputStream gzipInputStream = new GZIPInputStream(byteArrayInputStream);
        byte[] buffer = new byte[1024];
        int len;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        while ((len = gzipInputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, len);
        }

        return byteArrayOutputStream.toByteArray();
    }

    private static void writeBytesToFile(byte[] bytes, String filePath) throws IOException {
        File file = new File(filePath);
        file.getParentFile().mkdirs(); // 创建父目录
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(bytes);
        }
    }
}