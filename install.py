import logging
import zipfile
from pathlib import Path
import httpx
import platform
import colorlog

# 配置日志记录
log_colors = {
    'DEBUG': 'cyan',
    'INFO': 'green',
    'WARNING': 'yellow',
    'ERROR': 'red',
    'CRITICAL': 'bold_red',
}

formatter = colorlog.ColoredFormatter(
    "%(log_color)s%(asctime)s - %(name)s - %(levelname)s - %(message)s",
    log_colors=log_colors
)

handler = logging.StreamHandler()
handler.setFormatter(formatter)

file_handler = logging.FileHandler("app.log")
file_handler.setFormatter(logging.Formatter(
    "%(asctime)s - %(name)s - %(levelname)s - %(message)s"
))

logging.basicConfig(
    level=logging.DEBUG,
    handlers=[file_handler, handler]
)

logger = logging.getLogger(__name__)

CURRENT_DIR = Path(__file__).resolve().parent
APPS_FOR_CHECK = {
    "AntSwordLoader": {
        "dir": CURRENT_DIR / "Security/Web/webshell/manager/AntSword/App",
        "url": {
            # windows -> Windows 64 位
            "windows": [  # type: ignore
                "https://github.com/AntSwordProject/AntSword-Loader/releases/download/4.0.3/AntSword-Loader-v4.0.3-win32-x64.zip",
            ]
        },
    }
}
HTTP_PROXY = "http://127.0.0.1:7890"
PROXIES = {
    "http": HTTP_PROXY,
    "https": HTTP_PROXY,
}


def download_zip_extract_to_dir(url: str, dir_path: Path, proxies: dict = {}) -> bool:
    """下载zip压缩包并解压到指定目录
    :param url: 下载地址
    :param dir_path: 解压目录
    :return: 是否成功
    """
    temp_zip_path = dir_path / "temp.zip"
    try:
        with httpx.Client(proxies=proxies, follow_redirects=True) as client:
            resp = client.get(url)
            resp.raise_for_status()  # 检查请求是否成功
            with open(temp_zip_path, "wb") as f:
                f.write(resp.content)
        with zipfile.ZipFile(temp_zip_path, "r") as zip_ref:
            zip_ref.extractall(dir_path)
        logger.info(f"从 {url} 下载并解压到 {dir_path} 成功, 当前代理: {proxies}")
        return True
    except Exception as e:
        logger.error(f"从 {url} 下载并解压到 {dir_path} 失败, 当前代理: {proxies}, 报错信息为: {e}, ")
        return False
    finally:
        if temp_zip_path.exists():
            temp_zip_path.unlink()
            logger.info(f"删除临时文件 {temp_zip_path}")


def check_and_install_apps(os_name: str) -> None:
    """检查并安装应用"""
    for app_name, app_info in APPS_FOR_CHECK.items():
        success = False
        for url in app_info["url"].get(os_name, []):
            success = download_zip_extract_to_dir(url, app_info["dir"])
            if success:
                break
            # 尝试使用代理下载
            success = download_zip_extract_to_dir(url, app_info["dir"], PROXIES)
            if success:
                break
        if not success:
            logger.error(f"安装 {app_name} 失败")


def main():
    os_name_lower = platform.system().lower()
    match os_name_lower:
        case "windows":
            check_and_install_apps("windows")
        case _:
            logger.error(f"不支持的操作系统: {os_name_lower}")

if __name__ == "__main__":
    main()