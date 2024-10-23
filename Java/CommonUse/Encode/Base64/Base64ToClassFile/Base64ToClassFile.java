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
        String base64ClassString =  "yv66vgAAADQAMQoACAAbCgAcAB0IAB4KAB8AIAoABwAhCwAiACMHACQHACUBAAY8aW5pdD4BAAMoKVYBAARDb2RlAQAPTGluZU51bWJlclRhYmxlAQASTG9jYWxWYXJpYWJsZVRhYmxlAQAEdGhpcwEAKUxvcmcvc3UxOC9tZW1zaGVsbC90ZXN0L3RvbWNhdC9UZXN0VmFsdmU7AQAGaW52b2tlAQBSKExvcmcvYXBhY2hlL2NhdGFsaW5hL2Nvbm5lY3Rvci9SZXF1ZXN0O0xvcmcvYXBhY2hlL2NhdGFsaW5hL2Nvbm5lY3Rvci9SZXNwb25zZTspVgEAB3JlcXVlc3QBACdMb3JnL2FwYWNoZS9jYXRhbGluYS9jb25uZWN0b3IvUmVxdWVzdDsBAAhyZXNwb25zZQEAKExvcmcvYXBhY2hlL2NhdGFsaW5hL2Nvbm5lY3Rvci9SZXNwb25zZTsBAApFeGNlcHRpb25zBwAmBwAnAQAKU291cmNlRmlsZQEADlRlc3RWYWx2ZS5qYXZhDAAJAAoHACgMACkAKgEAEkkgY29tZSBoZXJlIGZpcnN0IQcAKwwALAAtDAAuAC8HADAMABAAEQEAJ29yZy9zdTE4L21lbXNoZWxsL3Rlc3QvdG9tY2F0L1Rlc3RWYWx2ZQEAJG9yZy9hcGFjaGUvY2F0YWxpbmEvdmFsdmVzL1ZhbHZlQmFzZQEAE2phdmEvaW8vSU9FeGNlcHRpb24BAB5qYXZheC9zZXJ2bGV0L1NlcnZsZXRFeGNlcHRpb24BACZvcmcvYXBhY2hlL2NhdGFsaW5hL2Nvbm5lY3Rvci9SZXNwb25zZQEACWdldFdyaXRlcgEAFygpTGphdmEvaW8vUHJpbnRXcml0ZXI7AQATamF2YS9pby9QcmludFdyaXRlcgEAB3ByaW50bG4BABUoTGphdmEvbGFuZy9TdHJpbmc7KVYBAAdnZXROZXh0AQAdKClMb3JnL2FwYWNoZS9jYXRhbGluYS9WYWx2ZTsBABlvcmcvYXBhY2hlL2NhdGFsaW5hL1ZhbHZlACEABwAIAAAAAAACAAEACQAKAAEACwAAAC8AAQABAAAABSq3AAGxAAAAAgAMAAAABgABAAAADQANAAAADAABAAAABQAOAA8AAAABABAAEQACAAsAAABbAAMAAwAAABUstgACEgO2AAQqtgAFKyy5AAYDALEAAAACAAwAAAAOAAMAAAARAAkAEwAUABQADQAAACAAAwAAABUADgAPAAAAAAAVABIAEwABAAAAFQAUABUAAgAWAAAABgACABcAGAABABkAAAACABo=";
        // String classFileName = "DecodedlassFile.class";
        String classFileName = "su18TomcatValve.class";
        DecodeBase64ToClassFile(base64ClassString, classFileName);
    }
}