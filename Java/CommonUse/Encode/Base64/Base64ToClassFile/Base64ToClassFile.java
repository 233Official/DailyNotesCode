import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

public class Base64ToClassFile {

    public static void main(String[] args) {
        // 将步骤 1 中生成的 Base64 字符串粘贴到这里
        String base64ClassString = "yv66vgAAADQANwoABwAiCwAjACQIACUKACYAJwsAKAApBwAqBwArBwAsAQAGPGluaXQ+AQADKClWAQAEQ29kZQEAD0xpbmVOdW1iZXJUYWJsZQEAEkxvY2FsVmFyaWFibGVUYWJsZQEABHRoaXMBACpMb3JnL3N1MTgvbWVtc2hlbGwvdGVzdC90b21jYXQvVGVzdEZpbHRlcjsBAARpbml0AQAfKExqYXZheC9zZXJ2bGV0L0ZpbHRlckNvbmZpZzspVgEADGZpbHRlckNvbmZpZwEAHExqYXZheC9zZXJ2bGV0L0ZpbHRlckNvbmZpZzsBAAhkb0ZpbHRlcgEAWyhMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdDtMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVzcG9uc2U7TGphdmF4L3NlcnZsZXQvRmlsdGVyQ2hhaW47KVYBAA5zZXJ2bGV0UmVxdWVzdAEAHkxqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXF1ZXN0OwEAD3NlcnZsZXRSZXNwb25zZQEAH0xqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXNwb25zZTsBAAtmaWx0ZXJDaGFpbgEAG0xqYXZheC9zZXJ2bGV0L0ZpbHRlckNoYWluOwEACkV4Y2VwdGlvbnMHAC0HAC4BAAdkZXN0cm95AQAKU291cmNlRmlsZQEAD1Rlc3RGaWx0ZXIuamF2YQwACQAKBwAvDAAwADEBAA90aGlzIGlzIEZpbHRlciAHADIMADMANAcANQwAFAA2AQAob3JnL3N1MTgvbWVtc2hlbGwvdGVzdC90b21jYXQvVGVzdEZpbHRlcgEAEGphdmEvbGFuZy9PYmplY3QBABRqYXZheC9zZXJ2bGV0L0ZpbHRlcgEAE2phdmEvaW8vSU9FeGNlcHRpb24BAB5qYXZheC9zZXJ2bGV0L1NlcnZsZXRFeGNlcHRpb24BAB1qYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXNwb25zZQEACWdldFdyaXRlcgEAFygpTGphdmEvaW8vUHJpbnRXcml0ZXI7AQATamF2YS9pby9QcmludFdyaXRlcgEAB3ByaW50bG4BABUoTGphdmEvbGFuZy9TdHJpbmc7KVYBABlqYXZheC9zZXJ2bGV0L0ZpbHRlckNoYWluAQBAKExqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXF1ZXN0O0xqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXNwb25zZTspVgAhAAYABwABAAgAAAAEAAEACQAKAAEACwAAAC8AAQABAAAABSq3AAGxAAAAAgAMAAAABgABAAAACQANAAAADAABAAAABQAOAA8AAAABABAAEQABAAsAAAA1AAAAAgAAAAGxAAAAAgAMAAAABgABAAAAEgANAAAAFgACAAAAAQAOAA8AAAAAAAEAEgATAAEAAQAUABUAAgALAAAAZAADAAQAAAAULLkAAgEAEgO2AAQtKyy5AAUDALEAAAACAAwAAAAOAAMAAAAfAAsAIQATACIADQAAACoABAAAABQADgAPAAAAAAAUABYAFwABAAAAFAAYABkAAgAAABQAGgAbAAMAHAAAAAYAAgAdAB4AAQAfAAoAAQALAAAAKwAAAAEAAAABsQAAAAIADAAAAAYAAQAAACkADQAAAAwAAQAAAAEADgAPAAAAAQAgAAAAAgAh";

        try {
            // 解码 Base64 字符串
            byte[] classBytes = Base64.getDecoder().decode(base64ClassString);

            // 将字节数组写入 .class 文件
            try (FileOutputStream fos = new FileOutputStream("DecodedClass.class")) {
                fos.write(classBytes);
                System.out.println("Class 文件已成功生成！");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}