import pyshark
import json
import binascii
from pathlib import Path
import time

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
            excluded_fields = ['', "_ws_expert", "chat", "_ws_expert_message", "_ws_expert_severity", "_ws_expert_group", "request_line", "request_full_uri", "request", "request_number", "file_data", "data_data", "content_length_header"]
            for field_name in layer.field_names:
                if field_name not in excluded_fields:
                    header_name = field_name.replace('_', '-')
                    headers[header_name] = getattr(layer, field_name, None)
                    if field_name == "file_data":
                        hex_body = layer.file_data.replace(':', '')
                        body = binascii.unhexlify(hex_body).decode('utf-8', errors='replace')
            packet_dict['headers'] = headers
            if body:
                packet_dict['body'] = body
    return packet_dict

def process_pcap_file(pcap_file):
    packets = []
    try:
        cap = pyshark.FileCapture(str(pcap_file), display_filter='http')
        for packet in cap:
            packet_dict = packet_to_dict(packet)
            if packet_dict:  # 仅添加包含HTTP请求的数据包
                packets.append(packet_dict)
        cap.close()
    except Exception as e:
        print(f"Error processing {pcap_file}: {e}")
    
    return packets

def pcap_to_json(input_dir):
    # 获取目录下的所有PCAP文件
    pcap_files = Path(input_dir).glob('*.pcap')
    # 将 pcap_files 转换成 list
    pcap_files = list(pcap_files)
    
    pcap_num = len(pcap_files)
    i = 0
    for pcap_file in pcap_files:
        packets = process_pcap_file(pcap_file)
        output_file = pcap_file.with_suffix('.json')
        
        # 写入到输出文件
        with open(output_file, 'w') as f:
            json.dump(packets, f, indent=4)
        
        time.sleep(2)  # 等待2秒以避免冲突
        # 进度条
        i += 1
        print(f"\rProgress: {i}/{pcap_num}", end="")

def main():
    CURRENT_PATH = Path(__file__).parent
    input_dir = CURRENT_PATH / 'pcaps'  # 替换为你的PCAP文件目录

    # 运行任务
    pcap_to_json(input_dir)

if __name__ == "__main__":
    main()