# 为指定文件批量生成用 RLO +  PDF,DOC,PPT,XLS 伪造的文件
from pathlib import Path

def add_fake_suffix(file_path:Path, new_extension_list:list[str])->None:
    """
    :param file_path: 原始文件路径
    :param new_extension_list: 新扩展名列表
    """

    base_dir, filename = file_path.parent, file_path.name
    filename, current_extension = filename.rsplit('.', 1)
    base_dir = base_dir / f"{filename}_fake_extension"
    base_dir.mkdir(parents=True, exist_ok=True)

    for new_extension in new_extension_list:
        # 生成新的文件名
        spoofed_file_path = base_dir / f"{filename}.{new_extension}.{current_extension}"

        # 保存文件到新的路径
        spoofed_file_path.write_bytes(file_path.read_bytes())
    

# Example usage
original_file = "msedge.exe"
original_file_path = Path(__file__).parent / original_file
new_extension_list = ["pdf", "doc","docx", "ppt", "pptx","xls", "xlsx", "txt"]
spoofed_file = add_fake_suffix(original_file_path, new_extension_list)

