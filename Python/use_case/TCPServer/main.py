import socketserver
import logging
import threading
import argparse

class TCPHandler(socketserver.BaseRequestHandler):
    """
    处理每个TCP连接的处理程序
    """
    def handle(self):
        # self.request是与客户端建立的连接socket
        client_address = self.client_address
        logging.info(f"接收到来自 {client_address} 的连接")
        
        try:
            # 持续处理来自这个客户端的数据
            while True:
                data = self.request.recv(1024)
                if not data:
                    # 客户端关闭了连接
                    break
                    
                logging.info(f"从 {client_address} 接收到 {len(data)} 字节的数据")
                logging.debug(f"数据内容: {data}")
                
                # 发送回应
                self.request.sendall(b"Server received your data\n")
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

def start_server(host="0.0.0.0", port=9999):
    """
    启动TCP服务器
    """
    logging.info(f"启动TCP服务器在 {host}:{port}")
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
    parser = argparse.ArgumentParser(description='启动一个接受所有TCP连接的服务器')
    parser.add_argument('--host', default='0.0.0.0', help='监听的主机地址')
    parser.add_argument('--port', type=int, default=9999, help='监听的端口')
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
    start_server(args.host, args.port)