import git
from pathlib import Path
from summerlog import logger
import os

CURRENT_DIR = Path(__file__).resolve().parent
PROJECT_BASE_DIR = (CURRENT_DIR / "..").resolve()

GIT_REPO_DICT = {
    "java-memshell-generator": {
        "git_repo_url": "https://github.com/pen4uin/java-memshell-generator.git",
        "local_repo_path": PROJECT_BASE_DIR
        / "Security/Web/MemShell/generator/java-memshell-generator/src/java-memshell-generator",
    },
    "antSword":{
        "git_repo_url": "https://github.com/AntSwordProject/antSword.git",
        "local_repo_path": PROJECT_BASE_DIR / "Security/Web/webshell/manager/AntSword/App/antSword",
    },
    "marshalsec":{
        "git_repo_url": "https://github.com/mbechler/marshalsec.git",
        "local_repo_path": PROJECT_BASE_DIR / "Security/Web/Unmarshall/marshalsec/src/marshalsec",
    }
}


def sync_git_repo(
    git_repo_name: str, git_repo_url: str, local_repo_path: Path, http_proxy: str = None
) -> None:
    """同步预定义的Git仓库到本地"""
    # 首先检查本地仓库路径是否存在, 存在则结束
    if local_repo_path.exists():
        logger.info(f"本地仓库已存在: {local_repo_path}")
        return
    if http_proxy:
        os.environ["http_proxy"] = http_proxy
        os.environ["https_proxy"] = http_proxy

    # 本地仓库不存在, 克隆仓库
    logger.info(f"开始克隆仓库: {git_repo_name} 到 {local_repo_path} ......")
    git.Repo.clone_from(url=git_repo_url, to_path=local_repo_path)
    if local_repo_path.exists():
        logger.info(f"克隆仓库成功: {git_repo_name}")
    else:
        logger.error(f"克隆仓库失败: {git_repo_name}")


def sync_git_repos(git_repo_dict: dict=GIT_REPO_DICT, http_proxy: str = None) -> None:
    """同步多个Git仓库"""
    logger.info("开始同步Git仓库 ......")
    for git_repo_name, git_repo_info in git_repo_dict.items():
        sync_git_repo(
            git_repo_name=git_repo_name,
            git_repo_url=git_repo_info["git_repo_url"],
            local_repo_path=git_repo_info["local_repo_path"],
            http_proxy=http_proxy,
        )
    logger.info("Git仓库同步完成")
