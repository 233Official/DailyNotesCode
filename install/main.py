from pathlib import Path
import platform
from summerlog import logger
import httpx
from download_extract_zip import download_extract_zips
from sync_git_repo import sync_git_repos


CURRENT_DIR = Path(__file__).resolve().parent

HTTP_PROXY = "http://127.0.0.1:7890"
PROXIES = {
    "http://": HTTP_PROXY,
    "https://": HTTP_PROXY,
}

def check_if_proxy_alive(proxies:dict)->bool:
    """检查代理是否存活(尝试连接google)"""
    try:
        with httpx.Client(proxies=proxies, follow_redirects=True) as client:
            resp = client.get("https://www.google.com")
            resp.raise_for_status()  # 检查请求是否成功
            return True
    except Exception as e:
        logger.error(f"代理 {proxies} 不可用, 报错信息为: {e}")
        return False


def main():
    if not check_if_proxy_alive(PROXIES):
        logger.error("代理不可用, 请检查代理设置")
        return
    else:
        logger.info("代理可用, 进入安装流程")
    os_name_lower = platform.system().lower()
    download_extract_zips(os_name_lower=os_name_lower, proxies=PROXIES)
    sync_git_repos(http_proxy=HTTP_PROXY)


if __name__ == "__main__":
    main()
