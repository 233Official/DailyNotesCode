# toml 转 yaml
import toml
import yaml
from pathlib import Path

TOML_PATH = Path(__file__).parent / "config.toml"
YAML_PATH = Path(__file__).parent / "config.yaml"

with open(TOML_PATH, "r") as f:
    data = toml.load(f)

with open(YAML_PATH, "w") as f:
    yaml.dump(data, f)

print("toml 转 yaml 完成")
