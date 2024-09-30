package com.summer233;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class SummerBasicServlet implements Servlet {
   public SummerBasicServlet() {
   }

   @Override
   public void init(ServletConfig servletConfig) throws ServletException {
   }

   @Override
   public ServletConfig getServletConfig() {
      return null;
   }

   @Override
   public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
      servletResponse.getWriter().println("this is a new Servlet");
      // PS: 不可以像下面这样输出中文, 否则后面动态注册Servlet会失败而且没有日志(
      // servletResponse.getWriter().println("这是一个新增Servlet");
   }

   @Override
   public String getServletInfo() {
      return null;
   }

   @Override
   public void destroy() {
   }
}
