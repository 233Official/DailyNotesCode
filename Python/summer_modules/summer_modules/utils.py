from pathlib import Path
import json

def write_dict_to_json_file(data: dict, filepath: Path, one_line=False):
    """将 dict 写入到 json 文件"""
    if one_line:
        with open(filepath, "w") as f:
            json.dump(data, f, ensure_ascii=False)
    else:
        with open(filepath, "w") as f:
            json.dump(data, f, ensure_ascii=False, indent=4)