import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
from pathlib import Path

SOURCE_DATA_EXCEL_PATH = Path("data/health_log.xlsx")  # 假设数据文件在 data 文件夹下

# 配置
TARGET_SLEEP_HOURS = 7.5  # 目标睡眠时长

# 读取 Excel 文件（直接从 WPS 表格读取）
def load_data(filepath):
    # 使用 pandas 的 read_excel 方法，它内部使用 openpyxl 作为引擎
    df = pd.read_excel(filepath, engine='openpyxl')
    df['昨晚入睡时间'] = pd.to_datetime(df['昨晚入睡时间'], format='%H:%M', errors='coerce')
    df['今早醒来时间'] = pd.to_datetime(df['今早醒来时间'], format='%H:%M', errors='coerce')
    df['昨日夜间睡眠时长'] = pd.to_numeric(df['昨日夜间睡眠时长'], errors='coerce')
    df['早晨洗漱后体重(斤)'] = pd.to_numeric(df['早晨洗漱后体重(斤)'], errors='coerce')
    return df

# 添加衍生字段
def enrich_data(df):
    df['睡眠债'] = TARGET_SLEEP_HOURS - df['昨日夜间睡眠时长']
    df['睡眠债'] = df['睡眠债'].apply(lambda x: x if x > 0 else 0)
    df['晚睡评分'] = df['昨晚入睡时间'].dt.hour + df['昨晚入睡时间'].dt.minute / 60
    df['晚睡评分'] = df['晚睡评分'].apply(lambda x: max(0, x - 24) if x > 24 else x)
    df['体重变化'] = df['早晨洗漱后体重(斤)'].diff()
    return df

# 生成可视化图表
def plot_trends(df):
    sns.set_theme(style="whitegrid")
    plt.figure(figsize=(12, 6))
    plt.plot(df['早晨洗漱后体重(斤)'], label='体重 (斤)', marker='o')
    plt.plot(df['昨日夜间睡眠时长'], label='睡眠时长 (h)', marker='s')
    plt.plot(df['睡眠债'], label='睡眠债 (h)', linestyle='--')
    plt.xticks(rotation=45)
    plt.title("体重 & 睡眠趋势图")
    plt.legend()
    plt.tight_layout()
    plt.savefig("trend_plot.png")
    plt.show()

# 主函数
def main():
    # 修改文件路径指向Excel文件
    df = load_data("data/health_log.xlsx")  # 或者 .xls，取决于你的文件格式
    df = enrich_data(df)
    print("\n==== 睡眠分析摘要 ====")
    print("平均睡眠时间：", round(df['昨日夜间睡眠时长'].mean(), 2), "小时")
    print("累计睡眠债：", round(df['睡眠债'].sum(), 2), "小时")
    print("最晚入睡时间：", df['昨晚入睡时间'].max().strftime('%H:%M'))
    print("体重变化区间：", round(df['早晨洗漱后体重(斤)'].min(), 1), "~", round(df['早晨洗漱后体重(斤)'].max(), 1), "斤")
    plot_trends(df)

if __name__ == '__main__':
    main()