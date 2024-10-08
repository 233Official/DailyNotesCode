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
        String base64ClassString =  "yv66vgAAADQAVwoAEQArCgAsAC0HAC4KABEALwgAHAoAMAAxCgAyADMKADIANAcANQoACQA2CgA3ADgIADkKADoAOwcAPAoADgA9BwA+BwA/BwBAAQAGPGluaXQ+AQADKClWAQAEQ29kZQEAD0xpbmVOdW1iZXJUYWJsZQEAEkxvY2FsVmFyaWFibGVUYWJsZQEABHRoaXMBACxMb3JnL3N1MTgvbWVtc2hlbGwvdGVzdC90b21jYXQvVGVzdExpc3RlbmVyOwEAEHJlcXVlc3REZXN0cm95ZWQBACYoTGphdmF4L3NlcnZsZXQvU2VydmxldFJlcXVlc3RFdmVudDspVgEAB3JlcXVlc3QBAC1Mb3JnL2FwYWNoZS9jYXRhbGluYS9jb25uZWN0b3IvUmVxdWVzdEZhY2FkZTsBAAFmAQAZTGphdmEvbGFuZy9yZWZsZWN0L0ZpZWxkOwEAA3JlcQEAJ0xvcmcvYXBhY2hlL2NhdGFsaW5hL2Nvbm5lY3Rvci9SZXF1ZXN0OwEAAWUBABVMamF2YS9sYW5nL0V4Y2VwdGlvbjsBABNzZXJ2bGV0UmVxdWVzdEV2ZW50AQAjTGphdmF4L3NlcnZsZXQvU2VydmxldFJlcXVlc3RFdmVudDsBAA1TdGFja01hcFRhYmxlBwA8AQAScmVxdWVzdEluaXRpYWxpemVkAQAKU291cmNlRmlsZQEAEVRlc3RMaXN0ZW5lci5qYXZhDAATABQHAEEMAEIAQwEAK29yZy9hcGFjaGUvY2F0YWxpbmEvY29ubmVjdG9yL1JlcXVlc3RGYWNhZGUMAEQARQcARgwARwBIBwBJDABKAEsMAEwATQEAJW9yZy9hcGFjaGUvY2F0YWxpbmEvY29ubmVjdG9yL1JlcXVlc3QMAE4ATwcAUAwAUQBSAQAPCmhhY2tlZCBieSBzdTE4BwBTDABUAFUBABNqYXZhL2xhbmcvRXhjZXB0aW9uDABWABQBACpvcmcvc3UxOC9tZW1zaGVsbC90ZXN0L3RvbWNhdC9UZXN0TGlzdGVuZXIBABBqYXZhL2xhbmcvT2JqZWN0AQAkamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdExpc3RlbmVyAQAhamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdEV2ZW50AQARZ2V0U2VydmxldFJlcXVlc3QBACAoKUxqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXF1ZXN0OwEACGdldENsYXNzAQATKClMamF2YS9sYW5nL0NsYXNzOwEAD2phdmEvbGFuZy9DbGFzcwEAEGdldERlY2xhcmVkRmllbGQBAC0oTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL2xhbmcvcmVmbGVjdC9GaWVsZDsBABdqYXZhL2xhbmcvcmVmbGVjdC9GaWVsZAEADXNldEFjY2Vzc2libGUBAAQoWilWAQADZ2V0AQAmKExqYXZhL2xhbmcvT2JqZWN0OylMamF2YS9sYW5nL09iamVjdDsBAAtnZXRSZXNwb25zZQEAKigpTG9yZy9hcGFjaGUvY2F0YWxpbmEvY29ubmVjdG9yL1Jlc3BvbnNlOwEAJm9yZy9hcGFjaGUvY2F0YWxpbmEvY29ubmVjdG9yL1Jlc3BvbnNlAQAJZ2V0V3JpdGVyAQAXKClMamF2YS9pby9QcmludFdyaXRlcjsBABNqYXZhL2lvL1ByaW50V3JpdGVyAQAHcHJpbnRsbgEAFShMamF2YS9sYW5nL1N0cmluZzspVgEAD3ByaW50U3RhY2tUcmFjZQAhABAAEQABABIAAAADAAEAEwAUAAEAFQAAAC8AAQABAAAABSq3AAGxAAAAAgAWAAAABgABAAAADQAXAAAADAABAAAABQAYABkAAAABABoAGwABABUAAADIAAIABQAAADcrtgACwAADTSy2AAQSBbYABk4tBLYABy0stgAIwAAJOgQZBLYACrYACxIMtgANpwAITSy2AA+xAAEAAAAuADEADgADABYAAAAmAAkAAAAXAAgAGAASABkAFwAaACEAHAAuACAAMQAeADIAHwA2ACMAFwAAAD4ABgAIACYAHAAdAAIAEgAcAB4AHwADACEADQAgACEABAAyAAQAIgAjAAIAAAA3ABgAGQAAAAAANwAkACUAAQAmAAAABwACcQcAJwQAAQAoABsAAQAVAAAANQAAAAIAAAABsQAAAAIAFgAAAAYAAQAAACwAFwAAABYAAgAAAAEAGAAZAAAAAAABACQAJQABAAEAKQAAAAIAKg==";
        // String classFileName = "DecodedlassFile.class";
        String classFileName = "su18Listener.class";
        DecodeBase64ToClassFile(base64ClassString, classFileName);
    }
}