#!/usr/bin/env python3
"""用于将 Folo 导出的 OPML 中官方 RSSHub 链接替换为自部署地址的命令行工具。"""

from __future__ import annotations

import argparse
from pathlib import Path
from typing import Any, Sequence
from urllib.parse import parse_qsl, urlencode, urlsplit, urlunsplit
import xml.etree.ElementTree as ET

OFFICIAL_RSSHUB_PREFIX = "https://rsshub.app"


def _valid_rsshub_base(value: str) -> str:
    """确认 RSSHub 地址包含协议与主机名并移除多余的末尾斜线。"""
    parsed = urlsplit(value)
    if not parsed.scheme or not parsed.netloc:
        raise argparse.ArgumentTypeError(
            "RSSHub 地址必须包含协议和主机名, 示例: https://rsshub.app"
        )
    if parsed.query or parsed.fragment:
        raise argparse.ArgumentTypeError("RSSHub 基础地址不应包含查询参数或片段")
    normalized_path = parsed.path.rstrip("/")
    return f"{parsed.scheme}://{parsed.netloc}{normalized_path}"


def _existing_path(path_value: str) -> Path:
    """转换为 Path 并确认文件存在。"""
    path = Path(path_value).expanduser()
    if not path.exists():
        raise argparse.ArgumentTypeError(f"文件不存在: {path}")
    return path


def _writable_path(path_value: str) -> Path:
    """确认输出目录存在并返回可写路径。"""
    path = Path(path_value).expanduser()
    parent = path.parent if path.parent != Path("") else Path(".")
    if not parent.exists():
        raise argparse.ArgumentTypeError(f"输出目录不存在: {parent}")
    if path.exists() and not path.is_file():
        raise argparse.ArgumentTypeError(f"输出路径已存在且不是文件: {path}")
    return path


def parse_args(argv: Sequence[str] | None = None) -> argparse.Namespace:
    parser = argparse.ArgumentParser(
        description="将 Folo OPML 中以 https://rsshub.app 开头的订阅替换为自部署 RSSHub 并追加 ACCESS_KEY。"
    )
    parser.add_argument(
        "-url",
        "--rsshub_url",
        required=True,
        type=_valid_rsshub_base,
        help="自部署 RSSHub 基础地址 (例如 https://rsshub.example.com)",
    )
    parser.add_argument(
        "-key",
        "--access_key",
        required=True,
        help="RSSHub 服务启用的 ACCESS_KEY",
    )
    parser.add_argument(
        "-file",
        "--opml_filepath",
        required=True,
        type=_existing_path,
        help="Folo 导出的 OPML 文件路径",
    )
    parser.add_argument(
        "-out",
        "--output_filepath",
        type=_writable_path,
        help="输出 OPML 路径 (默认与输入同目录, 文件名追加 _resolved)",
    )
    return parser.parse_args(argv)


def _append_key_param(url: str, key: str) -> str:
    """在链接末尾追加或覆盖 key 查询参数。"""
    split = urlsplit(url)
    query_items = [
        (k, v) for k, v in parse_qsl(split.query, keep_blank_values=True) if k != "key"
    ]
    query_items.append(("key", key))
    new_query = urlencode(query_items)
    return urlunsplit(
        (split.scheme, split.netloc, split.path, new_query, split.fragment)
    )


def rewrite_opml(tree: ET.ElementTree[Any], base_url: str, access_key: str) -> int:
    """重写 xmlUrl 属性并返回替换数量。"""
    replacements = 0
    for element in tree.iter():
        xml_url = element.attrib.get("xmlUrl")
        if not xml_url or not xml_url.startswith(OFFICIAL_RSSHUB_PREFIX):
            continue
        suffix = xml_url[len(OFFICIAL_RSSHUB_PREFIX) :]
        rewritten = f"{base_url}{suffix}"
        element.set("xmlUrl", _append_key_param(rewritten, access_key))
        replacements += 1
    return replacements


def main(argv: Sequence[str] | None = None) -> int:
    args = parse_args(argv)
    try:
        tree = ET.parse(args.opml_filepath)
    except ET.ParseError as exc:
        raise SystemExit(f"解析 OPML 文件 {args.opml_filepath} 失败: {exc}") from exc
    replacements = rewrite_opml(tree, args.rsshub_url, args.access_key)
    if not replacements:
        print("未发现以 https://rsshub.app 开头的 xmlUrl, 文件未被修改。")
        return 0
    output_path = (
        args.output_filepath
        if args.output_filepath
        else args.opml_filepath.with_name(
            f"{args.opml_filepath.stem}_resolved{args.opml_filepath.suffix or '.opml'}"
        )
    )
    tree.write(output_path, encoding="utf-8", xml_declaration=True)
    print(f"已在 {output_path} 写入 {replacements} 条已替换的 xmlUrl, 原文件保持不变。")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
