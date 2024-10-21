import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

public class Base64ToClassFile {

    public static void DecodeBase64ToClassFile(String base64ClassString, String classFileName) {
        try {
            // 解码 Base64 字符串
            byte[] classBytes = Base64.getDecoder().decode(base64ClassString);

            // 将字节数组写入 .class 文件
            try (FileOutputStream fos = new FileOutputStream("output/" + classFileName)) {
                fos.write(classBytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    

    public static void main(String[] args) {
        String base64ClassString =  "yv66vgAAADQALQoABgAeCwAfACAIACEKACIAIwcAJAcAJQEABjxpbml0PgEAAygpVgEABENvZGUBAA9MaW5lTnVtYmVyVGFibGUBABJMb2NhbFZhcmlhYmxlVGFibGUBAAR0aGlzAQAvTG9yZy9zdTE4L21lbXNoZWxsL3NwcmluZy9vdGhlci9UZXN0Q29udHJvbGxlcjsBAAVpbmRleAEAUihMamF2YXgvc2VydmxldC9odHRwL0h0dHBTZXJ2bGV0UmVxdWVzdDtMamF2YXgvc2VydmxldC9odHRwL0h0dHBTZXJ2bGV0UmVzcG9uc2U7KVYBAAdyZXF1ZXN0AQAnTGphdmF4L3NlcnZsZXQvaHR0cC9IdHRwU2VydmxldFJlcXVlc3Q7AQAIcmVzcG9uc2UBAChMamF2YXgvc2VydmxldC9odHRwL0h0dHBTZXJ2bGV0UmVzcG9uc2U7AQAKRXhjZXB0aW9ucwcAJgEAGVJ1bnRpbWVWaXNpYmxlQW5ub3RhdGlvbnMBADRMb3JnL3NwcmluZ2ZyYW1ld29yay93ZWIvYmluZC9hbm5vdGF0aW9uL0dldE1hcHBpbmc7AQAKU291cmNlRmlsZQEAE1Rlc3RDb250cm9sbGVyLmphdmEBACtMb3JnL3NwcmluZ2ZyYW1ld29yay9zdGVyZW90eXBlL0NvbnRyb2xsZXI7AQA4TG9yZy9zcHJpbmdmcmFtZXdvcmsvd2ViL2JpbmQvYW5ub3RhdGlvbi9SZXF1ZXN0TWFwcGluZzsBAAV2YWx1ZQEABS9zdTE4DAAHAAgHACcMACgAKQEADXN1MTggaXMgaGVyZX4HACoMACsALAEALW9yZy9zdTE4L21lbXNoZWxsL3NwcmluZy9vdGhlci9UZXN0Q29udHJvbGxlcgEAEGphdmEvbGFuZy9PYmplY3QBABNqYXZhL2xhbmcvRXhjZXB0aW9uAQAmamF2YXgvc2VydmxldC9odHRwL0h0dHBTZXJ2bGV0UmVzcG9uc2UBAAlnZXRXcml0ZXIBABcoKUxqYXZhL2lvL1ByaW50V3JpdGVyOwEAE2phdmEvaW8vUHJpbnRXcml0ZXIBAAdwcmludGxuAQAVKExqYXZhL2xhbmcvU3RyaW5nOylWACEABQAGAAAAAAACAAEABwAIAAEACQAAAC8AAQABAAAABSq3AAGxAAAAAgAKAAAABgABAAAAEQALAAAADAABAAAABQAMAA0AAAABAA4ADwADAAkAAABOAAIAAwAAAAwsuQACAQASA7YABLEAAAACAAoAAAAKAAIAAAAVAAsAFgALAAAAIAADAAAADAAMAA0AAAAAAAwAEAARAAEAAAAMABIAEwACABQAAAAEAAEAFQAWAAAABgABABcAAAACABgAAAACABkAFgAAABIAAgAaAAAAGwABABxbAAFzAB0=";
        // String classFileName = "DecodedlassFile.class";
        String classFileName = "su18SpringController.class";
        DecodeBase64ToClassFile(base64ClassString, classFileName);
    }
}