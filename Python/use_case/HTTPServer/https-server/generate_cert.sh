# 生成私钥和证书
openssl req -x509 -newkey rsa:4096 -nodes -out server.crt -keyout server.key -days 365