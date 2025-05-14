from pathlib import Path
import toml
import httpx
import json
from typing import Dict, Any
import time
import matplotlib.pyplot as plt
from matplotlib.font_manager import FontProperties as FP
import numpy as np

from summer_modules.utils import (
    write_dict_to_json_file,
    read_json_file_to_dict,
    find_chinese_font,
)
from summer_modules.logger import init_and_get_logger

CURRENT_DIR = Path(__file__).parent.resolve()

OTX_LOGGER = init_and_get_logger(CURRENT_DIR, "otx_logger")

CONFIG_TOML_PATH = CURRENT_DIR / "../config.toml"
CONFIG_TOML = toml.load(CONFIG_TOML_PATH)
OTX_API_KEY = CONFIG_TOML["otx_api_key"]

OTX_BASE_URL = "https://otx.alienvault.com"
SEARCH_PULSE_URL = f"{OTX_BASE_URL}/api/v1/search/pulses"
HEADERS = {"X-OTX-API-KEY": OTX_API_KEY}

# 每次查询的 json 保存在 data 目录下
DATA_DIR = CURRENT_DIR / "data"
DATA_DIR.mkdir(parents=True, exist_ok=True)

OUTPUT_DIR = CURRENT_DIR / "out"
OUTPUT_DIR.mkdir(parents=True, exist_ok=True)


def otx_search_pulses(
    limit: int = 10,
    page: int = 1,
    sort: str = "created",
    q: str = "",
    timeout: int = 30,
) -> Dict[str, Any]:
    """
    使用OTX API搜索Pulses
    :param limit: 每页返回的结果数量(最大为100,超过100会自动被限制为100)
    :param page: 页码(最多查询50页)
    :param sort: 排序字段,可选项有"(-)created", "(-)modified"
    :param q: 搜索关键词
    :param timeout: 请求超时时间,单位为秒
    :return: 返回搜索结果
    """
    if sort not in ["-modified", "modified", "-created", "created"]:
        raise ValueError(
            '排序字段无效, 可选项有 "-modified", "modified", "-created", "created"'
        )
    if not isinstance(limit, int) or limit <= 0:
        raise ValueError("limit必须是正整数")
    if not isinstance(page, int) or page <= 0:
        raise ValueError("page必须是正整数")
    if page > 50:
        raise ValueError("OTX支持最多查询 50 页")
    if limit > 100:
        OTX_LOGGER.warning("limit超过100,自动限制为100")
        limit = 100
    if not isinstance(q, str):
        raise ValueError("q必须是字符串")
    # 如果q为空字符串,则不添加q参数
    if not q:
        params = {
            "limit": limit,
            "page": page,
            "sort": sort,
        }
    else:
        params = {
            "limit": limit,
            "page": page,
            "sort": sort,
            "q": q,
        }

    with httpx.Client() as client:
        # 最多重试3次
        response = client.get(
            SEARCH_PULSE_URL, headers=HEADERS, params=params, timeout=timeout
        )
        for _ in range(2):
            if response.status_code == 200:
                break
            else:
                OTX_LOGGER.error(f"请求失败, 状态码: {response.status_code}, 重试中...")
                response = client.get(
                    SEARCH_PULSE_URL, headers=HEADERS, params=params, timeout=timeout
                )
        else:
            # 如果重试3次仍然失败,则抛出异常
            raise Exception(f"请求失败, 状态码: {response.status_code}")
        response.raise_for_status()
        response_json = response.json()
        # 获取当前时间戳
        current_timestamp = int(time.time())

        write_dict_to_json_file(
            response_json,
            DATA_DIR
            / f"{current_timestamp}_otx_search_pulses_{limit}_{page}_{sort}.json",
            one_line=False,
        )
        return response.json()


def otx_search_recently_modified_5000_pulses():
    """查询最近修改的5000个Pulses
    100条 * 50 页，需要查询 50 次，每次间隔 3 秒
    """
    # 每次查询100条,查询50页
    limit = 100
    page = 1
    sort = "-modified"
    # 每次查询间隔3秒
    interval = 3
    # 查询次数
    count = 50

    otx_recently_modified_5000_pulses_dict = {}
    for i in range(count):
        OTX_LOGGER.info(f"当前正在查询第{i + 1}页，共{count}页，每页{limit}条数据")
        response_dict = otx_search_pulses(
            limit=limit, page=page, sort=sort, timeout=100
        )
        # response_dict 中的 results 是一个列表,包含了查询到的Pulses
        response_dict_results = response_dict.get("results", [])
        # 由于查询是实时的，所以可能会有重复的Pulses，所以需要去重，直接通过字典的键值对来去重，新的覆盖旧的就可以了
        for pulse in response_dict_results:
            # 这里的pulse是一个字典,包含了Pulse的所有信息
            # 通过pulse["id"]来去重
            pulse_id = pulse["id"]
            otx_recently_modified_5000_pulses_dict[pulse_id] = pulse
        page += 1
        if i < count - 1:
            time.sleep(interval)

    # 将去重后的结果按照时间戳递减排序添加到列表中
    otx_recently_modified_5000_pulses_list = sorted(
        otx_recently_modified_5000_pulses_dict.values(),
        key=lambda x: x["modified"],
        reverse=True,
    )
    OTX_LOGGER.info(f"查询到{len(otx_recently_modified_5000_pulses_list)}个Pulses")

    # 将字典写入到本地
    current_timestamp = int(time.time())
    write_dict_to_json_file(
        otx_recently_modified_5000_pulses_dict,
        DATA_DIR / f"{current_timestamp}_otx_recently_modified_5000_pulses.json",
        one_line=False,
    )

    return otx_recently_modified_5000_pulses_list


def analyze_recently_modified_5000_pulses(
    recently_modified_5000_pulses_dict_filepath: Path = None,
):
    """
    分析最近修改的5000个Pulses
    :param recently_modified_5000_pulses_dict_filepath: 最近修改的5000个Pulses的json文件路径,如果没有的话,则自动查询
    :return: None
    """
    if recently_modified_5000_pulses_dict_filepath is None:
        # 如果没有传入文件路径,则自动查询
        recently_modified_5000_pulses_list = otx_search_recently_modified_5000_pulses()
    else:
        # 如果传入了文件路径,则读取文件
        recently_modified_5000_pulses_dict = read_json_file_to_dict(
            recently_modified_5000_pulses_dict_filepath
        )
        recently_modified_5000_pulses_list = sorted(
            recently_modified_5000_pulses_dict.values(),
            key=lambda x: x["modified"],
            reverse=True,
        )

    # 提取订阅数数据
    subscriber_counts = [
        pulse.get("subscriber_count", 0) for pulse in recently_modified_5000_pulses_list
    ]

    # 基本统计
    stats = {
        "平均订阅数": sum(subscriber_counts) / len(subscriber_counts),
        "中位数": sorted(subscriber_counts)[len(subscriber_counts) // 2],
        "最大值": max(subscriber_counts),
        "最小值": min(subscriber_counts),
        "标准差": (
            sum(
                (x - (sum(subscriber_counts) / len(subscriber_counts))) ** 2
                for x in subscriber_counts
            )
            / len(subscriber_counts)
        )
        ** 0.5,
    }

    # 输出结果
    for key, value in stats.items():
        OTX_LOGGER.info(f"{key}: {value}")
    chinese_font = find_chinese_font()
    if chinese_font:
        font = FP(fname=chinese_font, size=12)
        plt.rcParams["font.family"] = font.get_name()
    else:
        OTX_LOGGER.warning("没有找到中文字体,可能会导致中文显示不正常")

    # 添加在analyze_recently_modified_5000_pulses函数中
    # 分段区间直方图分析
    fig, axes = plt.subplots(2, 3, figsize=(14, 10))
    fig.suptitle("Pulse订阅数分布分析", fontsize=16)

    # 1. 低订阅区间 (0-50)
    low_counts = [c for c in subscriber_counts if c <= 50]
    axes[0, 0].hist(low_counts, bins=25, alpha=0.7, color="skyblue")
    axes[0, 0].set_title(f"低订阅区间 (0-50), 共{len(low_counts)}个")
    axes[0, 0].set_xlabel("订阅数")
    axes[0, 0].set_ylabel("Pulse数量")
    axes[0, 0].grid(True, alpha=0.3)

    # 2. 中低订阅区间 (51-200)
    mid_low_counts = [c for c in subscriber_counts if 50 < c <= 200]
    axes[0, 1].hist(mid_low_counts, bins=25, alpha=0.7, color="lightgreen")
    axes[0, 1].set_title(f"中低订阅区间 (51-200), 共{len(mid_low_counts)}个")
    axes[0, 1].set_xlabel("订阅数")
    axes[0, 1].grid(True, alpha=0.3)

    # 3. 中高订阅区间 (201-1000)
    mid_high_counts = [c for c in subscriber_counts if 200 < c <= 1000]
    axes[0, 2].hist(mid_high_counts, bins=25, alpha=0.7, color="orange")
    axes[0, 2].set_title(f"中高订阅区间 (201-1000), 共{len(mid_high_counts)}个")
    axes[0, 2].set_xlabel("订阅数")
    axes[0, 2].grid(True, alpha=0.3)

    # 4. 高订阅区间 (1000-5000)
    high_counts = [c for c in subscriber_counts if 1000 < c <= 5000]
    axes[1, 0].hist(high_counts, bins=25, alpha=0.7, color="salmon")
    axes[1, 0].set_title(f"高订阅区间 (1000-5000), 共{len(high_counts)}个")
    axes[1, 0].set_xlabel("订阅数")
    axes[1, 0].grid(True, alpha=0.3)

    # 5. 超高订阅区间 (5000+) - 改进版
    super_high_counts = [c for c in subscriber_counts if c > 5000]
    if super_high_counts:
        # 使用对数刻度更好地展示广范围数据
        axes[1, 1].hist(super_high_counts, bins=20, alpha=0.7, color="purple", log=True)
        axes[1, 1].set_title(f"超高订阅区间 (5000+), 共{len(super_high_counts)}个")
        axes[1, 1].set_xlabel("订阅数")
        
        # 改进X轴刻度格式，使用千位分隔符
        import matplotlib.ticker as ticker
        def thousands_formatter(x, pos):
            return '{:,.0f}'.format(x)
        
        axes[1, 1].xaxis.set_major_formatter(ticker.FuncFormatter(thousands_formatter))
        
        # 添加文本标注，展示数值分布
        y_pos = 0.8  # 文本垂直位置
        axes[1, 1].text(0.95, y_pos, f"最大值: {max(super_high_counts):,}", 
                    transform=axes[1, 1].transAxes, ha='right')
        axes[1, 1].text(0.95, y_pos-0.1, f"最小值: {min(super_high_counts):,}", 
                    transform=axes[1, 1].transAxes, ha='right')
        axes[1, 1].text(0.95, y_pos-0.2, f"中位数: {int(np.median(super_high_counts)):,}", 
                    transform=axes[1, 1].transAxes, ha='right')
        
        # 或者考虑使用对数刻度
        # axes[1, 1].set_xscale('log')
    else:
        axes[1, 1].text(0.5, 0.5, "没有超过5000订阅的数据", 
                    ha='center', va='center', transform=axes[1, 1].transAxes)

    axes[1, 1].grid(True, alpha=0.3)

    # 展示Top 20最多订阅的Pulses
    top_n = 20
    top_pulses = sorted(
        recently_modified_5000_pulses_list,
        key=lambda x: x.get("subscriber_count", 0),
        reverse=True,
    )[:top_n]

    plt.figure(figsize=(14, 8))
    counts = [p.get("subscriber_count", 0) for p in top_pulses]
    names = [
        (
            p.get("name", "")[:40] + "..."
            if len(p.get("name", "")) > 40
            else p.get("name", "")
        )
        for p in top_pulses
    ]

    y_pos = np.arange(len(names))
    bars = plt.barh(y_pos, counts, align="center", color="skyblue")
    plt.yticks(y_pos, names)
    plt.xlabel("订阅数")
    plt.title(f"Top {top_n} 最多订阅的Pulses")

    # 为每个柱形添加数值标注
    for i, bar in enumerate(bars):
        width = bar.get_width()
        # 格式化数字，添加千位分隔符
        formatted_count = "{:,}".format(counts[i])
        plt.text(
            width + 0.01 * max(counts),
            y_pos[i],
            formatted_count,
            va="center",
            fontweight="bold",
        )

    plt.tight_layout()
    plt.savefig(OUTPUT_DIR / "top_subscribed_pulses.png")
    plt.show()


def get_top_pulses():
    """
    获取OTX中订阅者数量最多的Pulses
    """
    # 按照最近更新的 pulse 查询近
    pass


def test_otx_search_pulses():
    recently_modified_10_pulses = otx_search_pulses(limit=10, page=1, sort="-modified")
    OTX_LOGGER.info(recently_modified_10_pulses)
    least_recently_modified_10_pulses = otx_search_pulses(
        limit=10, page=1, sort="modified"
    )
    OTX_LOGGER.info(least_recently_modified_10_pulses)
    recently_created_10_pulses = otx_search_pulses(limit=10, page=1, sort="-created")
    OTX_LOGGER.info(recently_created_10_pulses)
    least_recently_created_10_pulses = otx_search_pulses(
        limit=10, page=1, sort="created"
    )
    OTX_LOGGER.info(least_recently_created_10_pulses)


def self_test():
    """
    自测函数
    """
    # 获取最近更新的第 21K 附近的 420 个Pulse
    # recently_modified_21k_10_pulses = otx_search_pulses(
    #     limit=10, page=1, sort="-modified", timeout=100
    # )
    # OTX_LOGGER.info(
    #     f"{recently_modified_21k_10_pulses},包含{len(recently_modified_21k_10_pulses['results'])}个结果"
    # )

    analyze_recently_modified_5000_pulses(
        recently_modified_5000_pulses_dict_filepath=DATA_DIR
        / "1747131230_otx_recently_modified_5000_pulses.json"
    )


def main():
    # top_pulses = get_top_pulses(limit=10, page=1, sort="subscriber_count")
    # top_pulses = get_top_pulses(limit=10, page=1, sort="created")
    # OTX_LOGGER.info(top_pulses)
    # test_otx_search_pulses()

    self_test()


if __name__ == "__main__":
    main()
