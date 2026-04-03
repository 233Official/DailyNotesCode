import hashlib
import tomllib
from pathlib import Path

config_path = Path(__file__).parent / "config.toml"
with open(config_path, "rb") as f:
    config = tomllib.load(f)

ACCESS_KEY: str = config["ACCESS_KEY"]
RSSHUB_BASEURL: str = config["RSSHUB_BASEURL"].rstrip("/")

path = input("请输入路由 PATH（如 /qdaily/column/59）: ").strip()
code = hashlib.md5(f"{path}{ACCESS_KEY}".encode()).hexdigest()
print(f"{RSSHUB_BASEURL}{path}?code={code}")
