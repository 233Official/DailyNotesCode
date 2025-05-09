from pathlib import Path
import toml
import httpx
import json
from typing import Dict, Any

CURRENT_DIR = Path(__file__).parent.resolve()
CONFIG_TOML_PATH = CURRENT_DIR / "../config.toml"
CONFIG_TOML = toml.load(CONFIG_TOML_PATH)

OTX_API_KEY = CONFIG_TOML["otx_api_key"]

OTX_BASE_URL = "https://otx.alienvault.com"
SEARCH_PULSE_URL = f"{OTX_BASE_URL}/api/v1/search/pulses"

headers = {"X-OTX-API-KEY": OTX_API_KEY}


def write_dict_to_json_file(data: dict, filepath: Path, one_line=True):
    """将 dict 写入到 json 文件
    Args:
        data (dict): 要写入的 dict
        filepath (Path): 文件路径
        one_line (bool): 是否写入为一行，默认为 True
    """
    if one_line:
        with open(filepath, "w", encoding="utf-8") as f:
            json.dump(data, f, ensure_ascii=False)
    else:
        with open(filepath, "w", encoding="utf-8") as f:
            json.dump(data, f, ensure_ascii=False, indent=4)


def otx_search_pulses(
    limit: int = 10,
    page: int = 1,
    sort: str = "created",
    q: str = "",
    timeout: int = 30,
):
    """
    使用OTX API搜索Pulses
    :param limit: 每页返回的结果数量
    :param page: 页码
    :param sort: 排序字段,可选项有"(-)created", "(-)modified"
    :param q: 搜索关键词
    :param timeout: 请求超时时间,单位为秒
    :return: 返回搜索结果
    """
    if sort not in ["-modified", "modified", "-created", "created"]:
        raise ValueError(
            '排序字段无效, 可选项有"created", "modified", "subscriber_count"'
        )
    if not isinstance(limit, int) or limit <= 0:
        raise ValueError("limit必须是正整数")
    if not isinstance(page, int) or page <= 0:
        raise ValueError("page必须是正整数")
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
            SEARCH_PULSE_URL, headers=headers, params=params, timeout=timeout
        )
        for _ in range(2):
            if response.status_code == 200:
                break
            else:
                print(f"请求失败, 状态码: {response.status_code}, 重试中...")
                response = client.get(
                    SEARCH_PULSE_URL, headers=headers, params=params, timeout=timeout
                )
        else:
            # 如果重试3次仍然失败,则抛出异常
            raise Exception(f"请求失败, 状态码: {response.status_code}")
        response.raise_for_status()
        response_json = response.json()
        write_dict_to_json_file(
            response_json,
            CURRENT_DIR / f"otx_search_pulses_{limit}_{page}_{sort}.json",
            one_line=False,
        )
        return response.json()


def get_top_pulses():
    """
    获取OTX中订阅者数量最多的Pulses
    """
    # 按照最近更新的 pulse 查询近
    pass

def test_otx_search_pulses():
    recently_modified_10_pulses = otx_search_pulses(limit=10, page=1, sort="-modified")
    least_recently_modified_10_pulses = otx_search_pulses(
        limit=10, page=1, sort="modified"
    )
    recently_created_10_pulses = otx_search_pulses(limit=10, page=1, sort="-created")
    least_recently_created_10_pulses = otx_search_pulses(
        limit=10, page=1, sort="created"
    )

def main():
    # top_pulses = get_top_pulses(limit=10, page=1, sort="subscriber_count")
    # top_pulses = get_top_pulses(limit=10, page=1, sort="created")
    # print(top_pulses)
    pass

def self_test():
    """
    自测函数
    """
    # 测试搜索功能
    test_otx_search_pulses()

    # 测试获取订阅者数量最多的Pulses
    top_pulses = get_top_pulses()
    print(top_pulses)


if __name__ == "__main__":
    main()
