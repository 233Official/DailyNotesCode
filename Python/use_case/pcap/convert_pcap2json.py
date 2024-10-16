# 将单个PCAP文件转换为JSON文件
import pyshark
import json
import binascii
from pathlib import Path
import hashlib

def packet_to_dict(packet):
    packet_dict = {}
    for layer in packet.layers:
        if layer.layer_name == 'http':
            # 仅处理HTTP请求
            if not hasattr(layer, 'request_method'):
                continue
            
            # 提取请求行
            if hasattr(layer, 'request_method'):
                packet_dict['method'] = layer.request_method
            if hasattr(layer, 'request_uri'):
                packet_dict['uri'] = layer.request_uri
            if hasattr(layer, 'request_version'):
                packet_dict['version'] = layer.request_version
            
            # 提取头部字段
            headers = {}
            body = None
            for field_name in layer.field_names:
                header_name = field_name.replace('_', '-')
                headers[header_name] = getattr(layer, field_name, None)
                if field_name == "file_data":
                    hex_body = layer.file_data.replace(':', '')
                    body = binascii.unhexlify(hex_body).decode('utf-8', errors='replace')
            
            # 删除不需要的字段
            excluded_fields = ['', "_ws_expert", "chat", "_ws_expert_message", "_ws_expert_severity", "_ws_expert_group", "request_line", "request_full_uri", "request", "request_number", "file_data", "data_data", "content_length_header","request-method","request-uri","request-version","cookie-pair"]
            for field in excluded_fields:
                headers.pop(field.replace('_', '-'), None)
            
            packet_dict['headers'] = headers
            if body:
                packet_dict['body'] = body
            # 计算 packet_dict 的 md5 值
            packet_dict['md5'] = hashlib.md5(json.dumps(packet_dict, sort_keys=True).encode()).hexdigest()
    return packet_dict

def pcap_to_json(input_file, output_file):
    # 读取PCAP文件
    cap = pyshark.FileCapture(input_file, display_filter='http')
    
    # 解析为JSON格式
    packets = []
    for packet in cap:
        packet_dict = packet_to_dict(packet)
        if packet_dict:  # 仅添加包含HTTP请求的数据包
            packets.append(packet_dict)
    
    # 写入到输出文件
    with open(output_file, 'w') as f:
        json.dump(packets, f, indent=4)

# 使用方法
CURRENT_PATH = Path(__file__).parent
input_pcap = CURRENT_PATH / 'index.pcap'
output_pcap = CURRENT_PATH / 'index.json'
pcap_to_json(input_pcap, output_pcap)