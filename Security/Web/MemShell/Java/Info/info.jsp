<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="javax.servlet.ServletContext" %>
<%@ page import="javax.servlet.annotation.WebServlet" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Servlet Version Check</title>
</head>
<body>
    <h1>Servlet Version Information</h1>

    <%
        // 获取 ServletContext 对象
        ServletContext servletContext = request.getServletContext();

        // 获取 Servlet 版本
        int majorVersion = servletContext.getMajorVersion();
        int minorVersion = servletContext.getMinorVersion();
        boolean isServlet30Plus = majorVersion >= 3;

        // 获取 Servlet 容器的版本信息
        String serverInfo = servletContext.getServerInfo();

        // 检查是否支持注解（Servlet 3.0+ 才支持 @WebServlet 等注解）
        boolean supportsAnnotations = servletContext.getClass().isAnnotationPresent(WebServlet.class);

        // 检查是否支持异步处理
        boolean supportsAsync = servletContext.getEffectiveMajorVersion() >= 3;
        // 获取当前Web应用实际使用的 Servlet 版本
        int effectiveMajorVersion = servletContext.getEffectiveMajorVersion();
        int effectiveMinorVersion = servletContext.getEffectiveMinorVersion();
        
        // 输出结果
        out.println("<p><strong>Servlet Version(Servlet容器支持的Servlet版本号):</strong> " + majorVersion + "." + minorVersion + "</p>");
        out.println("<p><strong>Servlet Container Info:</strong> " + serverInfo + "</p>");
        out.println("<p><strong>当前 ServletContext 类本身是否有 @WebServlet 注解 - Annotations (@WebServlet, etc.):</strong> " + (supportsAnnotations ? "Yes" : "No") + "</p>");
        out.println("<p><strong>Effective Servlet Version(当前Web应用实际使用的Sevlet规范版本[基于web.xml判断]):</strong> " + effectiveMajorVersion + "." + effectiveMinorVersion + "</p>");
    %>

</body>
</html>