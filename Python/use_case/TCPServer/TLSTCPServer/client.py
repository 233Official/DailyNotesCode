import socket
import ssl
import argparse
import logging

def create_tls_client(server_host, server_port, insecure=True):
    """
    创建一个TLS客户端连接到服务器
    """
    # 创建基础套接字
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    
    # 设置超时
    sock.settimeout(30)
    
    try:
        # 创建SSL上下文
        context = ssl.create_default_context()
        
        if insecure:
            # 如果是自签名证书，不验证证书
            context.check_hostname = False
            context.verify_mode = ssl.CERT_NONE
        
        # 连接到服务器
        logging.info(f"连接到服务器 {server_host}:{server_port}")
        sock.connect((server_host, server_port))
        
        # 包装为SSL套接字
        secure_sock = context.wrap_socket(sock, server_hostname=server_host)
        
        # 显示连接信息
        logging.info(f"已建立的TLS版本: {secure_sock.version()}")
        logging.info(f"加密套件: {secure_sock.cipher()}")
        
        return secure_sock
    except Exception as e:
        logging.error(f"连接失败: {e}")
        sock.close()
        raise

def send_and_receive(sock, message):
    """
    向服务器发送消息并接收响应
    """
    try:
        # 发送消息
        logging.info(f"发送: {message}")
        sock.sendall(message.encode())
        
        # 接收响应
        response = sock.recv(1024)
        logging.info(f"接收: {response.decode('utf-8', errors='replace')}")
        
        return response
    except Exception as e:
        logging.error(f"通信错误: {e}")
        raise

if __name__ == "__main__":
    # 设置命令行参数
    parser = argparse.ArgumentParser(description='TLS客户端示例')
    parser.add_argument('--host', default='localhost', help='服务器主机')
    parser.add_argument('--port', type=int, default=9999, help='服务器端口')
    parser.add_argument('--insecure', action='store_true', help='不验证服务器证书')
    parser.add_argument('--message', default='Hello, Server!', help='要发送的消息')
    
    args = parser.parse_args()
    
    # 设置日志
    logging.basicConfig(
        level=logging.INFO,
        format='%(asctime)s - %(levelname)s - %(message)s'
    )
    
    try:
        # 创建客户端连接
        client_socket = create_tls_client(args.host, args.port, args.insecure)
        
        # 发送消息并接收响应
        send_and_receive(client_socket, args.message)
        
        # 关闭连接
        client_socket.close()
        logging.info("连接已关闭")
    except KeyboardInterrupt:
        logging.info("用户中断")
    except Exception as e:
        logging.error(f"程序错误: {e}")