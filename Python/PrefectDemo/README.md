# Prefect Demo 项目

这个项目演示了 Prefect 的核心功能，包括 Flow、Task、Work Queue 和 Deployment 的使用。

## 安装

本项目使用Poetry进行依赖管理。如果你还没有安装Poetry，请先安装：

```bash
curl -sSL https://install.python-poetry.org | python3 -
```

然后安装项目依赖：

```bash
# 进入项目目录
cd PrefectDemo

# 安装依赖
poetry install
```

## 运行基础流程

```bash
poetry run python flows/basic_flow.py
```

## 创建部署

```bash
poetry run python deployments/deploy_flow.py
```

## 启动工作队列

```bash
poetry run prefect work-queue start "demo-queue"
```

## 使用主运行脚本

```bash
# 运行基础流程
poetry run python run.py basic --rows 200

# 运行高级流程
poetry run python run.py advanced --rows 1000 --partitions 5

# 创建部署
poetry run python run.py deploy

# 启动工作队列处理器
poetry run python run.py worker
```
