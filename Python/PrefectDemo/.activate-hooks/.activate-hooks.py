#!/usr/bin/env python
import os
import platform
import subprocess
from pathlib import Path

CURRENT_DIR = Path(__file__).parent.resolve()
# 获取项目根目录路径
PROJECT_DIR = (CURRENT_DIR / "..").resolve()


def check_virtualenv():
    """检查虚拟环境是否已创建"""
    venv_dir = PROJECT_DIR / ".venv"
    if not venv_dir.exists():
        print("\n警告: 未检测到虚拟环境 (.venv)。")
        print("请先运行 'poetry install' 创建虚拟环境，然后再执行此脚本。")
        return False
    return True


def show_success_message(system):
    """显示成功信息和验证方法"""
    prefect_home_path = PROJECT_DIR / ".prefect"
    prefect_home_str = str(prefect_home_path)
    
    print("\n" + "=" * 60)
    print(f"✅ PREFECT_HOME 已成功设置为: {prefect_home_str}")
    print("=" * 60)
    
    print("\n🔹 配置详情:")
    print(f"  • 配置位置: {prefect_home_str}")
    print("  • 当前会话已设置环境变量")
    
    if system == "windows":
        print("\n🔹 验证方法 (根据您的命令行环境):")
        print("  • CMD:        echo %PREFECT_HOME%")
        print("  • PowerShell: echo $env:PREFECT_HOME")
        print("  • Git Bash:   echo $PREFECT_HOME")
    else:
        print("\n🔹 验证方法:")
        print("  • echo $PREFECT_HOME")
    
    print("\n🔹 查看完整配置:")
    print("  • prefect config view")
    
    print("\n🔹 下次使用信息:")
    print("  • 使用 'poetry shell' 激活环境时，PREFECT_HOME 将自动设置")
    print("  • 所有 Prefect 配置和数据将保存在项目的 .prefect 目录中")
    
    if not check_virtualenv():
        print("\n⚠️  重要提示:")
        print("  当前只设置了本次会话的环境变量。请执行 'poetry install' 后")
        print("  再次运行此脚本以配置自动激活。")


def main():
    """主函数：检测系统并调用相应的脚本"""
    print("正在设置 PREFECT_HOME 环境...")

    # 确保在正确的目录中
    os.chdir(PROJECT_DIR)

    # 检查虚拟环境是否存在
    venv_exists = check_virtualenv()

    system = platform.system().lower()

    if system == "windows":
        print("Windows环境: 执行 Windows 激活脚本配置...")

        # 执行 CMD 的 bat 脚本
        bat_script = CURRENT_DIR / ".activate-hooks.bat"
        if bat_script.exists() and venv_exists:
            print("执行 CMD 激活脚本配置...")
            subprocess.run([str(bat_script)], shell=True)

        # 执行 PowerShell 的 ps1 脚本
        ps_script = CURRENT_DIR / ".activate-hooks.ps1"
        if ps_script.exists() and venv_exists:
            print("执行 PowerShell 激活脚本配置...")
            subprocess.run(
                ["powershell", "-ExecutionPolicy", "Bypass", "-File", str(ps_script)]
            )

        # 尝试执行 Git Bash 的 sh 脚本
        bash_script = CURRENT_DIR / ".activate-hooks.sh"
        if bash_script.exists() and venv_exists:
            print("尝试执行 Git Bash 激活脚本配置...")
            # 判断是否有可用的bash
            try:
                subprocess.run(
                    ["bash", "--version"],
                    stdout=subprocess.PIPE,
                    stderr=subprocess.PIPE,
                )
                subprocess.run(["bash", str(bash_script)])
                print("Git Bash 脚本执行成功")
            except FileNotFoundError:
                print("未检测到系统安装的 bash，无法自动配置 Git Bash 环境")
                print("\n注意: 如果您使用 Git Bash，请在 Git Bash 中手动运行以下命令:")
                print(f"    source \"{CURRENT_DIR / '.activate-hooks.sh'}\"")
        elif bash_script.exists():
            print("\n注意: 如果您使用 Git Bash，请在 Git Bash 中手动运行以下命令:")
            print(f"    source \"{CURRENT_DIR / '.activate-hooks.sh'}\"")

        # 设置当前进程的环境变量
        prefect_home = os.path.join(PROJECT_DIR, ".prefect")
        os.environ["PREFECT_HOME"] = prefect_home
        if not os.path.exists(prefect_home):
            os.makedirs(prefect_home)

    else:
        # Unix 系统(Linux/macOS)
        print("检测到 Unix 环境")
        bash_script = CURRENT_DIR / ".activate-hooks.sh"
        if bash_script.exists() and venv_exists:
            subprocess.run(["bash", str(bash_script)])
            print("Unix 脚本执行成功")
        elif not venv_exists:
            print("由于未检测到虚拟环境，无法修改激活脚本")

        # 设置当前进程的环境变量
        prefect_home_path = PROJECT_DIR / ".prefect"
        prefect_home_str = str(prefect_home_path)
        os.environ["PREFECT_HOME"] = prefect_home_str
        if not prefect_home_path.exists():
            prefect_home_path.mkdir(parents=True, exist_ok=True)

    # 显示成功信息和验证方法
    show_success_message(system)


if __name__ == "__main__":
    main()