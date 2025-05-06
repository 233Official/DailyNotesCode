from flows.basic_flow import basic_data_flow
from flows.advanced_flow import advanced_data_flow
from pathlib import Path
from datetime import datetime, timedelta
from prefect.schedules import Interval, Cron
from datetime import datetime, timedelta, timezone
from prefect.docker.docker_image import DockerImage
from prefect.client.schemas.objects import (
    ConcurrencyLimitConfig,
    ConcurrencyLimitStrategy,
)

# 创建一个起止时间的调度
start_time = datetime.now()
end_time = start_time + timedelta(minutes=10)  # 只运行10分钟


CURRENT_DIR = Path(__file__).parent.resolve()
CURRENT_FILEPATH = Path(__file__).resolve()
PROHECT_DIR = CURRENT_DIR.parent.parent.resolve()


def create_deployments():
    """创建并应用多个部署"""

    # 1. 创建基础流程的部署 - 使用间隔调度（每小时运行一次）
    basic_data_flow.deploy(
        name="基础流程-每小时",
        version="1.0",
        description="每小时运行一次的基础数据处理流程",
        interval=3600,  # 每3600秒（1小时）运行一次
        work_pool_name="demo-queue-flow-deploy",
        parameters={"rows": 200, "output_path": "scheduled_basic_output.csv"},
        tags=["demo", "basic"],
    )

    # 2. 创建高级流程的部署 - 使用Cron调度（每天凌晨3点运行）
    advanced_data_flow.deploy(
        name="高级流程-每日",
        version="1.0",
        description="每天凌晨运行的高级数据处理流程",
        cron="0 3 * * *",  # 每天3:00 AM
        work_pool_name="demo-queue-flow-deploy",
        parameters={"rows": 1000, "partitions": 5},
        tags=["demo", "advanced"],
    )

    # 3. 创建高级流程的另一个部署 - 手动触发（无调度）
    advanced_data_flow.deploy(
        name="高级流程-手动",
        version="1.0",
        description="需要手动触发的高级数据处理流程",
        work_pool_name="demo-queue-flow-deploy",
        parameters={"rows": 2000, "partitions": 8},
        tags=["demo", "advanced", "manual"],
    )

    print("所有部署已创建并应用！")
    print("可以使用以下命令启动工作队列服务器:")
    print('prefect worker start --pool "demo-queue-flow-deploy"')
    print("\n要查看所有部署:")
    print("prefect deployment ls")
    print("\n要手动运行部署:")
    print("prefect deployment run 高级流程/高级流程-手动")


if __name__ == "__main__":
    # 设置只运行10分钟后结束
    # end_time = datetime.now(timezone.utc) + timedelta(minutes=3)

    # 使用Interval创建每分钟运行一次的调度
    # schedule = Interval(
    #     60,  # 每60秒运行一次
    #     active=True,
    #     # 设置结束时间作为参数
    #     # parameters={"end_time": end_time.isoformat()},
    # )

    # 使用Cron创建定时调度
    schedule = Cron(
        "15 16 * * *",  
        active=True,
        timezone="Asia/Shanghai",
    )

    # 创建 DockerImage 对象
    docker_img = DockerImage(
        name="127.0.0.1:5000/prefect-flow-deploy-docker:v0.1.5",
        dockerfile="docker/Dockerfile",  # 相对路径到你的 Dockerfile
        context=".",  # 构建上下文（通常是项目根目录）
    )

    # concurrency_limit = ConcurrencyLimitConfig(
    #     limit=3, collision_strategy=ConcurrencyLimitStrategy.CANCEL_NEW
    # )

    # create_deployments()
    deployment_id = basic_data_flow.deploy(
        name="deployment-docker-flow-deploy",
        work_pool_name="my-work-pool-docker-flow-deploy",
        schedule=schedule,
        # concurrency_limit=concurrency_limit,
        parameters={"rows": 500, "output_path": "output.csv"},
        tags=["production"],
        image=docker_img,
        build=True,
        push=True,
    )
    print(f"部署已创建: {deployment_id}")
