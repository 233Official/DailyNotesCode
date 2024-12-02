import xmindparser
import sys
from pathlib import Path

CURRENT_DIR = Path(__file__).resolve().parent
XMIND_DIR = CURRENT_DIR / "resource/source"
MARKDOWN_DIR = CURRENT_DIR / "resource/output"
XMIND_DIR.mkdir(parents=True, exist_ok=True)
MARKDOWN_DIR.mkdir(parents=True, exist_ok=True)

def parse_topic(topic, level=1):
    markdown = ""
    title = topic.get("title", "")
    if title:
        markdown += f"{'#' * level} {title}\n\n"
    topics = topic.get("topics", [])
    for sub_topic in topics:
        markdown += parse_topic(sub_topic, level + 1)
    return markdown


def xmind_to_markdown(xmind_file, markdown_file):
    data = xmindparser.xmind_to_dict(xmind_file)
    markdown = ""
    for sheet in data:
        sheet_title = sheet.get("title", "Sheet")
        markdown += f"# {sheet_title}\n\n"
        topic = sheet.get("topic", {})
        markdown += parse_topic(topic)
    with open(markdown_file, "w", encoding="utf-8") as f:
        f.write(markdown)


if __name__ == "__main__":
    # if len(sys.argv) != 3:
    #     print("用法: python xmind_to_markdown.py 输入文件.xmind 输出文件.md")
    # else:
    #     xmind_file = sys.argv[1]
    #     markdown_file = sys.argv[2]
    #     xmind_to_markdown(xmind_file, markdown_file)
    
    xmind_filepath = XMIND_DIR / "权限提升.xmind"
    markdown_filepath = MARKDOWN_DIR / "权限提升.md"

    xmind_to_markdown(xmind_filepath, markdown_filepath)

    print(f"转换完成，已保存到 {markdown_filepath}")