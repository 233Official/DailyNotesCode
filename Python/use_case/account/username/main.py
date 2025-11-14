# 随机用户名生成器
import random
from typing import List
from pathlib import Path
import toml

from summer_modules_ai.deepseek import DeepseekClient
from summer_modules_core.logger import init_and_get_logger

CURRENT_DIR = Path(__file__).resolve().parent
logger = init_and_get_logger(CURRENT_DIR, "generate_username")
CONFIG_TOML_FILEPATH = (CURRENT_DIR / "../../../config.toml").resolve()
CONFIG = toml.load(CONFIG_TOML_FILEPATH)
DEEPSEEK_APIKEY = CONFIG["deepseek_apikey"]
DEEPSEEK_CLIENT = DeepseekClient(api_key=DEEPSEEK_APIKEY)


def generate_username(style="default"):
    """生成一个更加真实的随机用户名

    参数:
        style: 用户名风格，可选值包括 "default"、"gaming"、"professional"、"social"
    """
    adjectives = [
        "cool",
        "super",
        "awesome",
        "happy",
        "clever",
        "bright",
        "swift",
        "quick",
        "brave",
        "mighty",
        "eager",
        "calm",
        "wise",
        "gentle",
        "lucky",
        "wild",
    ]

    nouns = [
        "fox",
        "tiger",
        "eagle",
        "panda",
        "wolf",
        "robot",
        "ninja",
        "hero",
        "phoenix",
        "dragon",
        "coder",
        "gamer",
        "star",
        "spark",
        "shadow",
        "runner",
        "master",
    ]

    verbs = [
        "run",
        "jump",
        "play",
        "code",
        "read",
        "build",
        "create",
        "design",
        "dream",
        "think",
    ]

    tech_terms = [
        "dev",
        "tech",
        "byte",
        "pixel",
        "data",
        "cyber",
        "code",
        "web",
        "net",
        "cloud",
    ]

    gaming_prefixes = [
        "pro",
        "epic",
        "mega",
        "ultra",
        "hyper",
        "elite",
        "legend",
        "master",
    ]

    # 风格特定的生成逻辑
    if style == "gaming":
        # 游戏风格: Elite_Dragon99, MegaGamer_2023
        pattern = random.choice(
            [
                f"{random.choice(gaming_prefixes)}{random.choice(nouns).capitalize()}{random.randint(1, 999)}",
                f"{random.choice(adjectives).capitalize()}{random.choice(nouns).capitalize()}{random.randint(1, 99)}",
                f"{random.choice(gaming_prefixes)}_{random.choice(nouns)}{random.randint(1, 99)}",
            ]
        )

    elif style == "professional":
        # 专业风格: j_smith_dev, tech_master, clever_coder
        pattern = random.choice(
            [
                f"{random.choice('abcdefghijklmnopqrstuvwxyz')}_{random.choice(nouns)}_{random.choice(tech_terms)}",
                f"{random.choice(tech_terms)}_{random.choice(nouns)}",
                f"{random.choice(adjectives)}_{random.choice(tech_terms)}",
            ]
        )

    elif style == "social":
        # 社交媒体风格: happy.fox, cool_tiger22, real.ninja.2023
        pattern = random.choice(
            [
                f"{random.choice(adjectives)}.{random.choice(nouns)}",
                f"{random.choice(adjectives)}_{random.choice(nouns)}{random.randint(10, 99)}",
                f"real.{random.choice(nouns)}.{random.randint(2020, 2025)}",
            ]
        )

    else:  # default
        # 默认风格: 多种组合
        patterns = [
            f"{random.choice(adjectives)}{random.choice(nouns)}{random.randint(1, 99)}",
            f"{random.choice(verbs)}{random.choice(nouns).capitalize()}",
            f"{random.choice(adjectives)}_{random.choice(nouns)}",
            f"{random.choice(nouns)}{random.randint(100, 999)}",
        ]
        pattern = random.choice(patterns)

    return pattern


def generate_username_with_deepseek(
    style: str = "default", count: int = 1
) -> List[str]:
    """通过 DeepSeek API 生成创意用户名

    参数:
        style: 用户名风格，可选值包括 "default"、"gaming"、"professional"、"social"、"creative"
        count: 要生成的用户名数量

    返回:
        生成的用户名列表，如果 API 调用失败则返回使用本地方法生成的用户名
    """
    # 构建适合不同风格的提示词
    style_prompts = {
        "default": "创建普通但有趣的用户名，适合一般网站使用",
        "gaming": "创建酷炫的游戏玩家用户名，可包含游戏元素、战斗词汇或英雄主题",
        "professional": "创建专业的用户名，适合职场和专业平台，保持简洁和专业性",
        "social": "创建有趣、吸引人的社交媒体用户名，能反映个性特点",
        "creative": "创建非常有创意的用户名，可使用文字游戏、双关语或独特组合",
    }

    prompt_text = style_prompts.get(style, style_prompts["default"])

    system_prompt = (
        "你是一个专业的用户名生成器。只输出用户名本身，不要有额外说明或引号。"
    )

    user_prompt = f"""请生成{count}个{style}风格的用户名。{prompt_text}。
要求:
1. 每个用户名一行
2. 长度控制在5-16个字符之间
3. 可以适当使用数字、下划线或点
4. 不要有引号或解释
5. 用户名应该看起来自然、有创意且易于记忆
"""

    try:
        response = DEEPSEEK_CLIENT.client.chat.completions.create(
            model="deepseek-chat",
            messages=[
                {"role": "system", "content": system_prompt},
                {"role": "user", "content": user_prompt},
            ],
            max_tokens=30,
            temperature=0.3,
            n=1,
            stop=None,
        )
        content = response.choices[0].message.content
        # 将返回的用户名列表拆分成单个用户名
        if content:
            usernames = content.split("\n")
            return usernames
        else:
            logger.error("DeepSeek API 返回的用户名为空")
            return []

    except Exception as e:
        logger.error(f"DeepSeek API 生成用户名时发生未预期出错: {str(e)}")
        logger.info("使用本地方法生成用户名")
        return [generate_username(style) for _ in range(count)]


def test_generate_username():
    logger.info("---本地生成的用户名, 每个风格3个---")
    for style in ["default", "gaming", "professional", "social"]:
        usernames = [generate_username(style) for _ in range(3)]
        logger.info(f"{style} 风格: {', '.join(usernames)}")


def test_generate_username_with_deepseek():
    # 测试 DeepSeek API 生成
    logger.info("\n---DeepSeek API 生成的用户名---")
    try:
        # 每个风格生成 3 个用户名
        for style in ["default", "gaming", "professional", "social"]:
            usernames = generate_username_with_deepseek(style, count=3)
            logger.info(f"{style} 风格: {', '.join(usernames)}")
    except Exception as e:
        logger.info(f"测试 DeepSeek API 时出错: {str(e)}")


if __name__ == "__main__":
    test_generate_username()
    test_generate_username_with_deepseek()
