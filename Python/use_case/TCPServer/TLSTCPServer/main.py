# 普通TCP模式
# python main.py --port 9999
# TLS模式
# python main.py --tls --port 9999
# 使用自定义证书：
# python main.py --tls --cert your_cert.crt --key your_key.key --port 9999
import socketserver
import logging
import threading
import argparse
import ssl
import socket
from OpenSSL import crypto
import datetime

class TCPHandler(socketserver.BaseRequestHandler):
    """
    处理每个TCP连接的处理程序
    """
    def handle(self):
        # self.request是与客户端建立的连接socket
        client_address = self.client_address
        logging.info(f"接收到来自 {client_address} 的连接")
        
        connection = self.request
        is_tls = hasattr(connection, 'context')
        conn_type = "TLS" if is_tls else "普通TCP"
        logging.info(f"连接类型: {conn_type}")
        
        try:
            # 持续处理来自这个客户端的数据
            while True:
                data = connection.recv(1024)
                if not data:
                    # 客户端关闭了连接
                    break
                    
                data_str = data.decode('utf-8', errors='replace')
                logging.info(f"从 {client_address} 接收到 {len(data)} 字节的数据")
                logging.info(f"收到数据: {data_str}")
                
                # 发送回应
                response = f"Server received {len(data)} bytes of data\n".encode()
                connection.sendall(response)
                logging.info(f"发送响应: {response.decode('utf-8', errors='replace')}")
        except ssl.SSLError as e:
            logging.error(f"SSL错误处理客户端 {client_address}: {e}")
        except Exception as e:
            logging.error(f"处理客户端 {client_address} 时发生错误: {e}")
        finally:
            logging.info(f"客户端 {client_address} 连接关闭")

class ThreadedTCPServer(socketserver.ThreadingMixIn, socketserver.TCPServer):
    """
    使用线程处理每个请求的TCP服务器
    """
    # 允许端口复用，这样服务器可以快速重启
    allow_reuse_address = True
    # 优雅地关闭每个线程
    daemon_threads = True

class TLSServer(ThreadedTCPServer):
    """
    支持TLS的TCP服务器
    """
    def __init__(self, server_address, RequestHandlerClass, certfile, keyfile, 
                 bind_and_activate=True):
        ThreadedTCPServer.__init__(self, server_address, RequestHandlerClass, bind_and_activate)
        self.certfile = certfile
        self.keyfile = keyfile

    def get_request(self):
        newsocket, fromaddr = self.socket.accept()
        try:
            # 创建一个更灵活的SSL上下文
            context = ssl.create_default_context(ssl.Purpose.CLIENT_AUTH)
            # 支持多种TLS版本
            context.options &= ~ssl.OP_NO_TLSv1
            context.options &= ~ssl.OP_NO_TLSv1_1
            context.options &= ~ssl.OP_NO_TLSv1_2
            context.options &= ~ssl.OP_NO_TLSv1_3
            
            # 设置更宽松的密码套件
            context.set_ciphers('ALL:@SECLEVEL=0')
            
            # 加载证书和密钥
            context.load_cert_chain(certfile=self.certfile, keyfile=self.keyfile)
            
            # 设置更长的超时时间
            newsocket.settimeout(60)  # 60秒超时
            
            # 包装套接字
            connstream = context.wrap_socket(newsocket, server_side=True)
            return connstream, fromaddr
        except ssl.SSLError as e:
            logging.warning(f"SSL握手失败: {e}, 降级为普通TCP连接")
            return newsocket, fromaddr
        except Exception as e:
            logging.error(f"获取请求时发生错误: {e}")
            newsocket.close()
            raise

def create_self_signed_cert(cert_file, key_file):
    """创建自签名证书"""
    
    logging.info(f"生成自签名证书: {cert_file}")
    
    # 创建密钥对
    k = crypto.PKey()
    k.generate_key(crypto.TYPE_RSA, 2048)
    
    # 创建自签名证书
    cert = crypto.X509()
    cert.get_subject().C = "CN"
    cert.get_subject().ST = "State"
    cert.get_subject().L = "City"
    cert.get_subject().O = "Organization"
    cert.get_subject().OU = "Organizational Unit"
    cert.get_subject().CN = "localhost"
    cert.set_serial_number(1000)
    cert.gmtime_adj_notBefore(0)
    cert.gmtime_adj_notAfter(10*365*24*60*60)  # 10年有效期
    cert.set_issuer(cert.get_subject())
    cert.set_pubkey(k)
    cert.sign(k, 'sha256')
    
    # 保存证书和私钥
    with open(cert_file, "wb") as f:
        f.write(crypto.dump_certificate(crypto.FILETYPE_PEM, cert))
    
    with open(key_file, "wb") as f:
        f.write(crypto.dump_privatekey(crypto.FILETYPE_PEM, k))
    
    logging.info("自签名证书生成完成")

def start_server(host="0.0.0.0", port=9999, use_tls=False, cert_file=None, key_file=None):
    """
    启动TCP服务器，可选择是否启用TLS
    """
    if use_tls:
        # 如果没有提供证书，则创建自签名证书
        if not cert_file or not key_file:
            cert_file = "server.crt"
            key_file = "server.key"
            create_self_signed_cert(cert_file, key_file)
            
        logging.info(f"启动TLS服务器在 {host}:{port}")
        server = TLSServer((host, port), TCPHandler, cert_file, key_file)
    else:
        logging.info(f"启动普通TCP服务器在 {host}:{port}")
        server = ThreadedTCPServer((host, port), TCPHandler)
    
    # 在线程中启动服务器
    server_thread = threading.Thread(target=server.serve_forever)
    # 当主线程退出时，这个线程也会退出
    server_thread.daemon = True
    server_thread.start()
    
    try:
        # 保持主线程运行
        while True:
            server_thread.join(1)  # 每秒检查一次
    except KeyboardInterrupt:
        logging.info("接收到关闭信号，正在关闭服务器...")
    finally:
        server.shutdown()
        server.server_close()
        logging.info("服务器已关闭")

if __name__ == "__main__":
    # 设置命令行参数
    parser = argparse.ArgumentParser(description='启动一个接受所有TCP连接的服务器，可选择是否启用TLS')
    parser.add_argument('--host', default='0.0.0.0', help='监听的主机地址')
    parser.add_argument('--port', type=int, default=9999, help='监听的端口')
    parser.add_argument('--tls', action='store_true', help='启用TLS支持')
    parser.add_argument('--cert', help='TLS证书文件路径')
    parser.add_argument('--key', help='TLS密钥文件路径')
    parser.add_argument('--loglevel', default='INFO', 
                        choices=['DEBUG', 'INFO', 'WARNING', 'ERROR', 'CRITICAL'],
                        help='日志级别')
    
    args = parser.parse_args()
    
    # 设置日志
    logging.basicConfig(
        level=getattr(logging, args.loglevel),
        format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
    )
    
    # 启动服务器
    start_server(args.host, args.port, args.tls, args.cert, args.key)