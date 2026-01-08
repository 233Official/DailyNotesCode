"""时间戳工具模块

提供各种 13 位毫秒级时间戳的获取功能
"""

import argparse
import time
from datetime import datetime, timedelta


def get_current_timestamp() -> int:
    """获取当前 13 位毫秒级时间戳

    Returns:
        int: 当前时间的 13 位毫秒级时间戳
    """
    return int(time.time() * 1000)


def get_timestamp_from_datetime(datetime_str: str) -> int:
    """获取指定时间的 13 位毫秒级时间戳

    Args:
        datetime_str: 时间字符串，格式为 "YYYY-MM-DD hh:mm:ss"

    Returns:
        int: 指定时间的 13 位毫秒级时间戳
    """
    dt = datetime.strptime(datetime_str, "%Y-%m-%d %H:%M:%S")
    return int(dt.timestamp() * 1000)


def get_timestamp_from_date(date_str: str) -> int:
    """获取指定日期零点整的 13 位毫秒级时间戳

    Args:
        date_str: 日期字符串，格式为 "YYYY-MM-DD"

    Returns:
        int: 指定日期零点整的 13 位毫秒级时间戳
    """
    dt = datetime.strptime(date_str, "%Y-%m-%d")
    return int(dt.timestamp() * 1000)


def get_today_midnight_timestamp() -> int:
    """获取今天零点整的 13 位毫秒级时间戳

    Returns:
        int: 今天零点整的 13 位毫秒级时间戳
    """
    today = datetime.now().replace(hour=0, minute=0, second=0, microsecond=0)
    return int(today.timestamp() * 1000)


def get_tomorrow_midnight_timestamp() -> int:
    """获取明天零点整的 13 位毫秒级时间戳

    Returns:
        int: 明天零点整的 13 位毫秒级时间戳
    """
    tomorrow = datetime.now().replace(hour=0, minute=0, second=0, microsecond=0) + timedelta(days=1)
    return int(tomorrow.timestamp() * 1000)


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="13 位毫秒级时间戳工具")
    parser.add_argument("--current", action="store_true", help="获取当前时间戳")
    parser.add_argument("--datetime", type=str, help="获取指定时间的时间戳 (格式: YYYY-MM-DD hh:mm:ss)")
    parser.add_argument("--date", type=str, help="获取指定日期零点的时间戳 (格式: YYYY-MM-DD)")
    parser.add_argument("--today", action="store_true", help="获取今天零点的时间戳")
    parser.add_argument("--tomorrow", action="store_true", help="获取明天零点的时间戳")

    args = parser.parse_args()

    # 如果没有传递任何参数，显示所有功能演示
    if not any([args.current, args.datetime, args.date, args.today, args.tomorrow]):
        print("=== 时间戳工具演示 ===\n")
        print(f"1. 当前 13 位毫秒级时间戳: {get_current_timestamp()}")
        print(f"2. 指定时间 (2026-01-08 12:30:45) 的时间戳: {get_timestamp_from_datetime('2026-01-08 12:30:45')}")
        print(f"3. 指定日期 (2026-01-08) 零点的时间戳: {get_timestamp_from_date('2026-01-08')}")
        print(f"4. 今天零点的时间戳: {get_today_midnight_timestamp()}")
        print(f"5. 明天零点的时间戳: {get_tomorrow_midnight_timestamp()}")
    else:
        if args.current:
            print(f"当前 13 位毫秒级时间戳: {get_current_timestamp()}")
        if args.datetime:
            print(f"指定时间 ({args.datetime}) 的时间戳: {get_timestamp_from_datetime(args.datetime)}")
        if args.date:
            print(f"指定日期 ({args.date}) 零点的时间戳: {get_timestamp_from_date(args.date)}")
        if args.today:
            print(f"今天零点的时间戳: {get_today_midnight_timestamp()}")
        if args.tomorrow:
            print(f"明天零点的时间戳: {get_tomorrow_midnight_timestamp()}")
