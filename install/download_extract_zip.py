# 下载压缩包并解压到指定目录
import zipfile
import httpx
from pathlib import Path
from summerlog import logger

CURRENT_DIR = Path(__file__).resolve().parent
PROJECT_BASE_DIR = (CURRENT_DIR / "..").resolve()


APPS_FOR_CHECK = {
    "AntSwordLoader": {
        "dir": PROJECT_BASE_DIR / "Security/Web/webshell/manager/AntSword/App",
        "url": {
            # windows -> Windows 64 位
            "windows": "https://github.com/AntSwordProject/AntSword-Loader/releases/download/4.0.3/AntSword-Loader-v4.0.3-win32-x64.zip",
        },
    }
}


def download_extract_zip(
    zip_file_url: str, extract_target_path: Path, proxies: dict = {}
) -> bool:
    """下载zip压缩包并解压到指定目录
    :param url: 下载地址
    :param dir_path: 解压目录
    :return: 是否成功
    """
    temp_zip_path = extract_target_path / "temp.zip"
    try:
        with httpx.Client(proxies=proxies, follow_redirects=True) as client:
            resp = client.get(zip_file_url)
            resp.raise_for_status()  # 检查请求是否成功
            with open(temp_zip_path, "wb") as f:
                f.write(resp.content)
        with zipfile.ZipFile(temp_zip_path, "r") as zip_ref:
            zip_ref.extractall(extract_target_path)
        logger.info(
            f"从 {zip_file_url} 下载并解压到 {extract_target_path} 成功, 当前代理: {proxies}"
        )
        return True
    except Exception as e:
        logger.error(
            f"从 {zip_file_url} 下载并解压到 {extract_target_path} 失败, 当前代理: {proxies}, 报错信息为: {e}, "
        )
        return False
    finally:
        if temp_zip_path.exists():
            temp_zip_path.unlink()
            logger.info(f"删除临时文件 {temp_zip_path}")


def download_extract_zips(os_name_lower: str, proxies: dict = {}) -> None:
    """检查并安装应用"""
    logger.info("开始执行压缩包下载与解压流程......")

    os_name = os_name_lower.lower()
    if os_name == "windows":
        os_name = "windows"
    else:
        logger.error(f"不支持的操作系统: {os_name_lower} - 下载并解压zip失败")
        return

    for app_name, app_info in APPS_FOR_CHECK.items():
        url = app_info["url"].get(os_name, "")
        if not url:
            logger.error(f"{app_name} 没有找到适用于 {os_name} 的下载链接")
            continue
        # 获取压缩包文件名(url最后一部分)
        url_parts = url.split("/")
        zip_file_name = url_parts[-1].split("?")[0].split(".zip")[0]
        # 先查看本地是否已经有了这个目录,如果有了则跳过下载
        unzip_folder_path = app_info["dir"] / zip_file_name
        if unzip_folder_path.exists():
            logger.info(f"{app_name} 已经存在对应解压目录{unzip_folder_path}, 跳过下载")
            continue
        # 尝试使用代理下载，如果失败则不使用代理再试一次
        success = download_extract_zip(
            zip_file_url=url, extract_target_path=app_info["dir"], proxies=proxies
        )
        if success:
            logger.info(f"安装 {app_name} 成功")
        else:
            success = download_extract_zip(
                zip_file_url=url, extract_target_path=app_info["dir"]
            )
            logger.error(f"安装 {app_name} 失败")

    logger.info("压缩包下载与解压流程执行完毕")
