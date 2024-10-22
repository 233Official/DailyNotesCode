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
        String base64ClassString =  "yv66vgAAADQAKwoABgAbCwAcAB0IAB4KAB8AIAcAIQcAIgcAIwEABjxpbml0PgEAAygpVgEABENvZGUBAA9MaW5lTnVtYmVyVGFibGUBABJMb2NhbFZhcmlhYmxlVGFibGUBAAR0aGlzAQAwTG9yZy9zdTE4L21lbXNoZWxsL3NwcmluZy9vdGhlci9UZXN0SW50ZXJjZXB0b3I7AQAJcHJlSGFuZGxlAQBkKExqYXZheC9zZXJ2bGV0L2h0dHAvSHR0cFNlcnZsZXRSZXF1ZXN0O0xqYXZheC9zZXJ2bGV0L2h0dHAvSHR0cFNlcnZsZXRSZXNwb25zZTtMamF2YS9sYW5nL09iamVjdDspWgEAB3JlcXVlc3QBACdMamF2YXgvc2VydmxldC9odHRwL0h0dHBTZXJ2bGV0UmVxdWVzdDsBAAhyZXNwb25zZQEAKExqYXZheC9zZXJ2bGV0L2h0dHAvSHR0cFNlcnZsZXRSZXNwb25zZTsBAAdoYW5kbGVyAQASTGphdmEvbGFuZy9PYmplY3Q7AQAKRXhjZXB0aW9ucwcAJAEAClNvdXJjZUZpbGUBABRUZXN0SW50ZXJjZXB0b3IuamF2YQwACAAJBwAlDAAmACcBABBpJ20gaW50ZXJjZXB0b3J+BwAoDAApACoBAC5vcmcvc3UxOC9tZW1zaGVsbC9zcHJpbmcvb3RoZXIvVGVzdEludGVyY2VwdG9yAQAQamF2YS9sYW5nL09iamVjdAEAMm9yZy9zcHJpbmdmcmFtZXdvcmsvd2ViL3NlcnZsZXQvSGFuZGxlckludGVyY2VwdG9yAQATamF2YS9sYW5nL0V4Y2VwdGlvbgEAJmphdmF4L3NlcnZsZXQvaHR0cC9IdHRwU2VydmxldFJlc3BvbnNlAQAJZ2V0V3JpdGVyAQAXKClMamF2YS9pby9QcmludFdyaXRlcjsBABNqYXZhL2lvL1ByaW50V3JpdGVyAQAHcHJpbnRsbgEAFShMamF2YS9sYW5nL1N0cmluZzspVgAhAAUABgABAAcAAAACAAEACAAJAAEACgAAAC8AAQABAAAABSq3AAGxAAAAAgALAAAABgABAAAACwAMAAAADAABAAAABQANAA4AAAABAA8AEAACAAoAAABZAAIABAAAAA0suQACAQASA7YABASsAAAAAgALAAAACgACAAAADwALABAADAAAACoABAAAAA0ADQAOAAAAAAANABEAEgABAAAADQATABQAAgAAAA0AFQAWAAMAFwAAAAQAAQAYAAEAGQAAAAIAGg==";
        // String classFileName = "DecodedlassFile.class";
        String classFileName = "su18SpringInterceptor.class";
        DecodeBase64ToClassFile(base64ClassString, classFileName);
    }
}