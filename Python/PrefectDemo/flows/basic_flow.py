from prefect import flow
import sys
import os

# 添加项目根目录到系统路径
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from tasks.data_tasks import generate_data, process_data, save_results


@flow(name="基础数据处理流程", description="生成、处理并保存数据的简单流程")
def basic_data_flow(rows: int = 500, output_path: str = "output.csv"):
    """
    一个基本的Prefect流程，用于演示Task的使用
    """
    # 任务链：生成 -> 处理 -> 保存
    data = generate_data(rows)
    processed = process_data(data)
    result_path = save_results(processed, output_path)
    
    return result_path


if __name__ == "__main__":
    # 直接运行流程
    result = basic_data_flow(rows=100, output_path="data_output.csv")
    print(f"流程完成，结果保存在: {result}")
