from __future__ import annotations

from pathlib import Path
from typing import Iterable

from PIL import Image
import sys


def convert_to_ico(
    input_file: str | Path,
    output_file: str | Path,
    sizes: list[tuple[int, int]] | None = None,
) -> None:
    """Convert an image file to ICO format with multiple sizes.

    Args:
        input_file: Path to the input image file.
        output_file: Path to the output .ico file.
        sizes: A list of icon sizes to embed. If None, a sensible multi-size
            default will be used.
    """
    if sizes is None:
        sizes = [
            (256, 256),
            (64, 64),
            (48, 48),
            (32, 32),
            (24, 24),
            (20, 20),
            (16, 16),
        ]

    img = Image.open(input_file)
    img.save(output_file, "ICO", sizes=sizes)


def main() -> None:
    """Entry point supporting both CLI and direct-run modes.

    - CLI mode: `python all_in_one.py <input_file> <output_file>`
    - Direct-run mode: Run without args; edit the default paths below.
    """
    script_name = Path(__file__).name

    if len(sys.argv) == 3:
        input_file = sys.argv[1]
        output_file = sys.argv[2]
    else:
        # Direct-run mode. Edit the following two lines as needed.
        current_dir = Path(__file__).resolve().parent
        input_file = current_dir / "input.png"  # <- 修改为你的源图片路径
        output_file = current_dir / "output.ico"  # <- 修改为你的输出 ICO 路径
        print(
            "未检测到命令行参数，使用直跑模式默认路径。\n"
            f"- 输入: {input_file}\n- 输出: {output_file}\n"
            "如需指定路径，请使用: \n"
            f"python {script_name} <input_file> <output_file>\n"
        )

    try:
        convert_to_ico(input_file, output_file)
        print(f"已生成 ICO: {output_file}")
    except FileNotFoundError as e:
        print(f"文件不存在: {e}")
        print(f"用法: python {script_name} <input_file> <output_file>")
        sys.exit(1)
    except Exception as e:
        print(f"转换失败: {e}")
        sys.exit(1)


if __name__ == "__main__":
    main()
