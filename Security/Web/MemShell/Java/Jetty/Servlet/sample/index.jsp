<%@ page import="javax.servlet.*, javax.servlet.http.*, javax.servlet.annotation.*, java.io.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <title>Jetty JSP Example</title>
</head>
<body>
    <h1>Jetty JSP Example</h1>
    <p>访问 <a href="/jetty/servlet-sample?cmd=mycmd">/jetty/servlet-sample?cmd=mycmd</a> 查看效果</p>
</body>
</html>

<%
    // 输出调试信息
    out.println("<p>开始注册 Servlet...</p>");

    // 定义一个内部类来实现 Servlet
    class SampleServlet extends HttpServlet {
        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            String cmd = request.getParameter("cmd");
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(cmd != null ? cmd : "No cmd parameter provided");
        }
    }

    // 注册 Servlet
    ServletContext context = getServletContext();
    if (context.getServletRegistration("SampleServlet") == null) {
        try {
            out.println("<p>尝试注册 SampleServlet...</p>");
            ServletRegistration.Dynamic servlet = context.addServlet("SampleServlet", new SampleServlet());
            servlet.addMapping("/servlet-sample");
            out.println("<p>Servlet 注册成功。</p>");
        } catch (IllegalStateException e) {
            out.println("<p>Servlet 注册失败: " + e.getMessage() + "</p>");
            e.printStackTrace(new java.io.PrintWriter(out));
        } catch (Exception e) {
            out.println("<p>发生异常: " + e.getMessage() + "</p>");
            e.printStackTrace(new java.io.PrintWriter(out));
        }
    } else {
        out.println("<p>Servlet 已经注册。</p>");
    }
%>