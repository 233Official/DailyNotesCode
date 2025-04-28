import time
import random
import pandas as pd
import numpy as np
from prefect import task


@task(name="生成数据", description="生成随机数据集")
def generate_data(rows: int = 1000):
    """生成一个包含随机数据的DataFrame"""
    print(f"生成{rows}行随机数据...")
    time.sleep(2)  # 模拟计算延迟
    
    data = {
        "id": range(1, rows + 1),
        "value": np.random.rand(rows),
        "category": np.random.choice(["A", "B", "C"], size=rows)
    }
    
    return pd.DataFrame(data)


@task(name="处理数据", description="处理和转换数据", retries=2)
def process_data(df):
    """对数据进行简单处理"""
    print(f"处理{len(df)}行数据...")
    time.sleep(3)  # 模拟处理时间
    
    # 添加新列
    df["transformed"] = df["value"] * 100
    df["timestamp"] = pd.Timestamp.now()
    
    # 模拟随机失败以展示重试功能
    if random.random() < 0.2:
        raise ValueError("随机处理失败 - 将会重试")
        
    return df


@task(name="保存结果", description="将结果保存到文件")
def save_results(df, path: str):
    """保存处理后的数据"""
    print(f"保存{len(df)}行数据到 {path}...")
    time.sleep(1)  # 模拟IO操作
    
    # 实际保存数据
    df.to_csv(path, index=False)
    print(f"数据已保存到 {path}")
    
    return path
