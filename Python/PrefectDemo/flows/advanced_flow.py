from prefect import flow, task
import pandas as pd
import time
import sys
import os
from datetime import datetime

# 添加项目根目录到系统路径
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from tasks.data_tasks import generate_data, process_data, save_results
from flows.basic_flow import basic_data_flow


@task(name="分割数据", description="将数据分割成多个分区")
def split_data(df, num_partitions: int = 3):
    """将DataFrame分割成多个部分"""
    print(f"将数据分割为{num_partitions}个部分...")
    
    # 计算每个分区的大小
    partition_size = len(df) // num_partitions
    partitions = []
    
    # 创建分区
    for i in range(num_partitions):
        start_idx = i * partition_size
        end_idx = start_idx + partition_size if i < num_partitions - 1 else len(df)
        partitions.append(df.iloc[start_idx:end_idx])
        
    return partitions


@task(name="合并结果", description="合并处理后的数据分区")
def merge_results(dfs):
    """合并多个DataFrame"""
    print(f"合并{len(dfs)}个数据分区...")
    return pd.concat(dfs, ignore_index=True)


@flow(name="子流程", description="处理单个数据分区")
def process_partition(partition, partition_id: int):
    """子流程：处理单个数据分区"""
    print(f"处理分区 {partition_id}，大小: {len(partition)}行")
    return process_data(partition)


@flow(name="高级数据处理流程", description="包含子流程和并行执行的高级流程示例")
def advanced_data_flow(rows: int = 1000, partitions: int = 3):
    """
    一个高级Prefect流程，包含并行执行和子流程
    """
    # 生成数据
    data = generate_data(rows)
    
    # 分割数据
    data_partitions = split_data(data, partitions)
    
    # 并行处理每个分区（子流程）
    processed_partitions = []
    for i, partition in enumerate(data_partitions):
        processed = process_partition(partition, i)
        processed_partitions.append(processed)
    
    # 合并结果
    merged_data = merge_results(processed_partitions)
    
    # 生成带时间戳的输出文件名
    timestamp = datetime.now().strftime("%Y%m%d_%H%M%S")
    output_path = f"advanced_output_{timestamp}.csv"
    
    # 保存结果
    result_path = save_results(merged_data, output_path)
    
    # 额外演示：作为子流程调用基本流程
    print("执行基础流程作为子流程...")
    basic_data_flow(rows=100, output_path=f"subflow_output_{timestamp}.csv")
    
    return result_path


if __name__ == "__main__":
    # 直接运行高级流程
    result = advanced_data_flow(rows=500, partitions=4)
    print(f"高级流程完成，结果保存在: {result}")
