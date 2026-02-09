"""时间戳工具模块

提供时间戳的获取和转换功能，支持秒级和毫秒级时间戳
"""

import argparse
import time
from datetime import datetime, timedelta


# ============ 时间戳获取函数 ============

def get_current_timestamp(unit: str = "milliseconds") -> int:
    """获取当前时间戳

    Args:
        unit: 时间戳单位，"seconds" 或 "milliseconds"，默认为 "milliseconds"

    Returns:
        int: 当前时间的时间戳
    """
    multiplier = 1 if unit == "seconds" else 1000
    return int(time.time() * multiplier)


def get_timestamp_from_datetime(datetime_str: str, unit: str = "milliseconds") -> int:
    """获取指定时间的时间戳

    Args:
        datetime_str: 时间字符串，格式为 "YYYY-MM-DD hh:mm:ss"
        unit: 时间戳单位，"seconds" 或 "milliseconds"，默认为 "milliseconds"

    Returns:
        int: 指定时间的时间戳
    """
    dt = datetime.strptime(datetime_str, "%Y-%m-%d %H:%M:%S")
    multiplier = 1 if unit == "seconds" else 1000
    return int(dt.timestamp() * multiplier)


def get_timestamp_from_date(date_str: str, unit: str = "milliseconds") -> int:
    """获取指定日期零点整的时间戳

    Args:
        date_str: 日期字符串，格式为 "YYYY-MM-DD"
        unit: 时间戳单位，"seconds" 或 "milliseconds"，默认为 "milliseconds"

    Returns:
        int: 指定日期零点整的时间戳
    """
    dt = datetime.strptime(date_str, "%Y-%m-%d")
    multiplier = 1 if unit == "seconds" else 1000
    return int(dt.timestamp() * multiplier)


def get_today_midnight_timestamp(unit: str = "milliseconds") -> int:
    """获取今天零点整的时间戳

    Args:
        unit: 时间戳单位，"seconds" 或 "milliseconds"，默认为 "milliseconds"

    Returns:
        int: 今天零点整的时间戳
    """
    today = datetime.now().replace(hour=0, minute=0, second=0, microsecond=0)
    multiplier = 1 if unit == "seconds" else 1000
    return int(today.timestamp() * multiplier)


def get_tomorrow_midnight_timestamp(unit: str = "milliseconds") -> int:
    """获取明天零点整的时间戳

    Args:
        unit: 时间戳单位，"seconds" 或 "milliseconds"，默认为 "milliseconds"

    Returns:
        int: 明天零点整的时间戳
    """
    tomorrow = datetime.now().replace(hour=0, minute=0, second=0, microsecond=0) + timedelta(days=1)
    multiplier = 1 if unit == "seconds" else 1000
    return int(tomorrow.timestamp() * multiplier)


# ============ 时间戳转换函数 ============

def convert_timestamp(timestamp: int, from_unit: str, to_unit: str) -> int:
    """将时间戳从一个单位转换到另一个单位

    Args:
        timestamp: 要转换的时间戳
        from_unit: 原始单位，"seconds" 或 "milliseconds"
        to_unit: 目标单位，"seconds" 或 "milliseconds"

    Returns:
        int: 转换后的时间戳
    """
    if from_unit == to_unit:
        return timestamp
    elif from_unit == "milliseconds" and to_unit == "seconds":
        return timestamp // 1000
    elif from_unit == "seconds" and to_unit == "milliseconds":
        return timestamp * 1000
    else:
        raise ValueError(f"不支持的单位转换: {from_unit} -> {to_unit}")


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="时间戳工具 - 支持秒级和毫秒级时间戳")
    parser.add_argument("--current", action="store_true", help="获取当前时间戳")
    parser.add_argument("--datetime", type=str, help="获取指定时间的时间戳 (格式: YYYY-MM-DD hh:mm:ss)")
    parser.add_argument("--date", type=str, help="获取指定日期零点的时间戳 (格式: YYYY-MM-DD)")
    parser.add_argument("--today", action="store_true", help="获取今天零点的时间戳")
    parser.add_argument("--tomorrow", action="store_true", help="获取明天零点的时间戳")
    parser.add_argument("--unit", type=str, choices=["seconds", "milliseconds"], default="milliseconds",
                        help="时间戳单位，默认为 milliseconds")
    parser.add_argument("--convert", type=str, help="转换时间戳 (格式: 'value from_unit to_unit'，如 '1736330444000 milliseconds seconds')")

    args = parser.parse_args()

    # 处理时间戳转换
    if args.convert:
        parts = args.convert.split()
        if len(parts) != 3:
            print("错误：转换格式应为 'value from_unit to_unit'，如 '1736330444000 milliseconds seconds'")
            exit(1)
        try:
            value, from_unit, to_unit = int(parts[0]), parts[1], parts[2]
            result = convert_timestamp(value, from_unit, to_unit)
            print(f"{from_unit} 时间戳 {value} 转换为 {to_unit}: {result}")
        except ValueError as e:
            print(f"错误: {e}")
        exit(0)

    # 如果没有传递任何参数，显示所有功能演示
    if not any([args.current, args.datetime, args.date, args.today, args.tomorrow]):
        print("=== 时间戳工具演示 ===\n")
        print("--- 毫秒级时间戳 (13 位) ---")
        current_time = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        print(f"1. 当前时间: {current_time}")
        print(f"   毫秒级时间戳: {get_current_timestamp('milliseconds')}")
        print(f"2. 指定时间 (2026-01-08 12:30:45) 的毫秒级时间戳: {get_timestamp_from_datetime('2026-01-08 12:30:45', 'milliseconds')}")
        print(f"3. 指定日期 (2026-01-08) 零点的毫秒级时间戳: {get_timestamp_from_date('2026-01-08', 'milliseconds')}")
        print(f"4. 今天零点的毫秒级时间戳: {get_today_midnight_timestamp('milliseconds')}")
        print(f"5. 明天零点的毫秒级时间戳: {get_tomorrow_midnight_timestamp('milliseconds')}")
        print("\n--- 秒级时间戳 (10 位) ---")
        print(f"6. 当前时间: {current_time}")
        print(f"   秒级时间戳: {get_current_timestamp('seconds')}")
        print(f"7. 指定时间 (2026-01-08 12:30:45) 的秒级时间戳: {get_timestamp_from_datetime('2026-01-08 12:30:45', 'seconds')}")
        print(f"8. 指定日期 (2026-01-08) 零点的秒级时间戳: {get_timestamp_from_date('2026-01-08', 'seconds')}")
        print(f"9. 今天零点的秒级时间戳: {get_today_midnight_timestamp('seconds')}")
        print(f"10. 明天零点的秒级时间戳: {get_tomorrow_midnight_timestamp('seconds')}")
        print("\n--- 时间戳转换 ---")
        current_ms = get_current_timestamp('milliseconds')
        current_s = get_current_timestamp('seconds')
        print(f"11. 毫秒转秒 ({current_ms}) -> {convert_timestamp(current_ms, 'milliseconds', 'seconds')}")
        print(f"12. 秒转毫秒 ({current_s}) -> {convert_timestamp(current_s, 'seconds', 'milliseconds')}")
    else:
        if args.current:
            current_time = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
            result = get_current_timestamp(args.unit)
            print(f"当前时间: {current_time}")
            print(f"当前 {args.unit} 时间戳: {result}")
        if args.datetime:
            result = get_timestamp_from_datetime(args.datetime, args.unit)
            print(f"指定时间 ({args.datetime}) 的 {args.unit} 时间戳: {result}")
        if args.date:
            result = get_timestamp_from_date(args.date, args.unit)
            print(f"指定日期 ({args.date}) 零点的 {args.unit} 时间戳: {result}")
        if args.today:
            result = get_today_midnight_timestamp(args.unit)
            print(f"今天零点的 {args.unit} 时间戳: {result}")
        if args.tomorrow:
            result = get_tomorrow_midnight_timestamp(args.unit)
            print(f"明天零点的 {args.unit} 时间戳: {result}")
