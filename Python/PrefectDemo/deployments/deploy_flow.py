from prefect.deployments import Deployment
from prefect.server.schemas.schedules import CronSchedule, IntervalSchedule
import sys
import os

# 添加项目根目录到系统路径
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from flows.basic_flow import basic_data_flow
from flows.advanced_flow import advanced_data_flow


def create_deployments():
    """创建并应用多个部署"""
    
    # 1. 创建基础流程的部署 - 使用间隔调度（每小时运行一次）
    basic_deployment = Deployment.build_from_flow(
        flow=basic_data_flow,
        name="基础流程-每小时",
        version="1.0",
        description="每小时运行一次的基础数据处理流程",
        schedule=IntervalSchedule(interval=3600),  # 每3600秒（1小时）运行一次
        work_queue_name="demo-queue",
        parameters={"rows": 200, "output_path": "scheduled_basic_output.csv"},
        tags=["demo", "basic"]
    )
    
    # 2. 创建高级流程的部署 - 使用Cron调度（每天凌晨3点运行）
    advanced_deployment = Deployment.build_from_flow(
        flow=advanced_data_flow,
        name="高级流程-每日",
        version="1.0",
        description="每天凌晨运行的高级数据处理流程",
        schedule=CronSchedule(cron="0 3 * * *"),  # 每天3:00 AM
        work_queue_name="demo-queue",
        parameters={"rows": 1000, "partitions": 5},
        tags=["demo", "advanced"]
    )
    
    # 3. 创建高级流程的另一个部署 - 手动触发（无调度）
    advanced_manual_deployment = Deployment.build_from_flow(
        flow=advanced_data_flow,
        name="高级流程-手动",
        version="1.0",
        description="需要手动触发的高级数据处理流程",
        work_queue_name="demo-queue",
        parameters={"rows": 2000, "partitions": 8},
        tags=["demo", "advanced", "manual"]
    )
    
    # 应用所有部署
    basic_deployment.apply()
    advanced_deployment.apply()
    advanced_manual_deployment.apply()
    
    print("所有部署已创建并应用！")
    print("可以使用以下命令启动工作队列服务器:")
    print("prefect work-queue start \"demo-queue\"")
    print("\n要查看所有部署:")
    print("prefect deployment ls")
    print("\n要手动运行部署:")
    print("prefect deployment run 高级流程/高级流程-手动")


if __name__ == "__main__":
    create_deployments()
