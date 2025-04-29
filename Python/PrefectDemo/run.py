"""
Prefect Demo 主运行脚本
提供命令行界面，用于执行不同的流程和部署
"""
import sys
import os
import argparse
import subprocess

# 添加项目根目录到系统路径
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from flows.basic_flow import basic_data_flow
from flows.advanced_flow import advanced_data_flow
from deployments.flow_deploy import create_deployments


def run_basic_flow(args):
    """运行基础流程"""
    print("执行基础数据处理流程...")
    result = basic_data_flow(rows=args.rows, output_path=args.output)
    print(f"流程完成，结果保存在: {result}")


def run_advanced_flow(args):
    """运行高级流程"""
    print("执行高级数据处理流程...")
    result = advanced_data_flow(rows=args.rows, partitions=args.partitions)
    print(f"流程完成，结果保存在: {result}")


def create_deploy(args):
    """创建部署"""
    print("创建部署...")
    create_deployments()


def start_worker(args):
    """启动工作队列处理器"""
    print(f"启动工作队列: {args.queue}...")
    subprocess.run(["prefect", "work-queue", "start", args.queue])


def main():
    """主函数：解析命令行参数并执行相应操作"""
    parser = argparse.ArgumentParser(description="Prefect Demo 运行工具")
    subparsers = parser.add_subparsers(help="可用命令", dest="command")
    
    # 基础流程参数
    basic_parser = subparsers.add_parser("basic", help="运行基础数据流程")
    basic_parser.add_argument("--rows", type=int, default=100, help="要生成的数据行数")
    basic_parser.add_argument("--output", type=str, default="data_output.csv", help="输出文件路径")
    basic_parser.set_defaults(func=run_basic_flow)
    
    # 高级流程参数
    advanced_parser = subparsers.add_parser("advanced", help="运行高级数据流程")
    advanced_parser.add_argument("--rows", type=int, default=500, help="要生成的数据行数")
    advanced_parser.add_argument("--partitions", type=int, default=4, help="数据分区数")
    advanced_parser.set_defaults(func=run_advanced_flow)
    
    # 部署参数
    deploy_parser = subparsers.add_parser("deploy", help="创建流程部署")
    deploy_parser.set_defaults(func=create_deploy)
    
    # 工作队列参数
    worker_parser = subparsers.add_parser("worker", help="启动工作队列处理器")
    worker_parser.add_argument("--queue", type=str, default="demo-queue", help="工作队列名称")
    worker_parser.set_defaults(func=start_worker)
    
    # 解析参数并执行
    args = parser.parse_args()
    if hasattr(args, "func"):
        args.func(args)
    else:
        parser.print_help()


if __name__ == "__main__":
    main()
