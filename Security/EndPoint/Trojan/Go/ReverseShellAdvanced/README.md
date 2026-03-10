# ReverseShellAdvanced

> 仅用于隔离实验环境中的沙箱研究与检测能力验证。

本项目当前是一个面向 Cuckoo 和 EDR 测试的低破坏性 Windows 行为模拟器。它保留了可疑字符串、敏感 API 加载、网络连接、注册表探针、文件工件以及短时进程行为等特征，但避免执行真实破坏动作，例如关闭 Defender、删除卷影副本、导出 LSASS、加密真实用户文档或安装真实持久化。

## 项目作用

这个样本的目标是在不把虚拟机“弄脏”的前提下，尽量产生可观测的恶意行为特征。

- 分配 RWX 内存，并触达常见的注入相关 API
- 执行 `cmd`、`powershell`、`tasklist`、`wmic`、`netstat`、`arp`、`route` 以及注册表查询
- 通过明文 TCP 连接到可配置的 C2 地址
- 在 `%TEMP%\ReverseShellAdvancedLab` 下创建可疑风格工件
- 执行安全的注册表/计划任务探针，并在随后清理
- 枚举浏览器数据路径和 Wi-Fi 配置，但不提取真实口令
- 只加密实验目录内自动生成的合成文档
- 将远程命令执行限制在一个很小的白名单中

## 静态特征

虽然当前版本不再执行真实破坏动作，但仍然保留了一批典型的恶意字符串与 IOC 风格特征，方便静态分析工具、规则引擎和沙箱做预判。

- 凭证窃取相关字符串：`mimikatz`、`sekurlsa::logonpasswords`、`lsadump::sam`
- 攻击框架与载荷相关字符串：`Empire`、`Cobalt Strike`、`meterpreter`
- PowerShell 下载执行相关字符串：`IEX`、`DownloadString`、`Invoke-Expression`
- 勒索软件与比特币相关字符串：`ransomware`、示例 BTC 地址
- UAC 绕过相关工具名：`fodhelper.exe`、`eventvwr.exe`

这些字符串的作用主要是保留静态 IOC 特征，而不是声明程序具备对应的真实攻击能力。

## 动态行为与 API 特征

当前样本在默认执行路径中会直接触发一批常见的可疑行为，也会显式加载一批常见的高风险 Windows API。

直接触发的行为包括：

- RWX 内存分配与最小线程执行探针
- `tasklist`、`wmic`、`reg query`、`powershell`、`netstat` 等系统工具调用
- 注册表写入/删除探针与计划任务创建/删除探针
- TCP 回连、实验目录文件写入、按键状态探针

程序中显式加载或保留的 API 包括：

- `VirtualAlloc`、`VirtualFree`、`CreateThread`
- `OpenProcess`、`WriteProcessMemory`、`ReadProcessMemory`
- `VirtualAllocEx`、`VirtualProtectEx`、`CreateRemoteThread`
- `NtCreateThreadEx`、`RtlCreateUserThread`
- `GetAsyncKeyState`、`GetForegroundWindow`、`GetWindowTextW`
- `RegOpenKeyExW`、`RegSetValueExW`
- `EnumProcesses`、`EnumProcessModules`

其中有些 API 主要用于保留样本特征，不一定会在默认流程中全部执行。

## 安全模型

当前程序不会执行以下动作：

- 禁用 Windows Defender 或停止安全服务
- 删除卷影副本或清空事件日志
- 导出 LSASS 或提取浏览器保存的密码
- 通过真实自启动位置建立持久化
- 加密实验目录之外的用户数据
- 从 C2 执行任意系统命令

所有生成的工件都写入：

```text
%TEMP%\ReverseShellAdvancedLab
```

## 快速开始

```bash
# 1. 按需修改 C2 地址
vim main.go

# 2. 构建 Windows 可执行文件
./build.sh

# 3. 启动监听
nc -lvnp 4444

# 4. 在 Windows 实验虚拟机中运行
./bin/reverse_shell_win64.exe
```

## C2 命令

TCP 连接建立后，样本支持以下命令：

| 命令 | 说明 |
|------|------|
| `sysinfo` | 返回主机、用户、进程与网络信息 |
| `recon` | 执行无害的网络侦察命令 |
| `creds` | 枚举浏览器/Wi-Fi 相关路径，不提取秘密 |
| `persist` | 执行安全的持久化探针并清理 |
| `disableav` | 记录安全软件篡改标记，并执行安全状态查询 |
| `uacbypass` | 在实验专用注册表路径下执行无害探针 |
| `encrypt` | 只加密实验目录中的合成文件 |
| `inject` | 在当前进程内执行一个会立即返回的最小 shellcode 探针 |
| `screenshot` | 创建截图占位工件 |
| `download` | 创建下载载荷占位工件 |
| `upload` | 列出实验目录中当前产生的工件 |
| `exit` | 关闭连接 |
| `whoami` / `hostname` / `ver` / `ipconfig` / `tasklist` | 允许执行的诊断命令 |

其余远程命令都会在实验模式下被阻止。

## 项目结构

```text
ReverseShellAdvanced/
├── main.go
├── build.sh
├── go.mod
├── README.md
└── test/
    └── main.go
```

- `main.go`：Windows 实验样本主程序
- `build.sh`：交叉编译 Windows 目标，并注入唯一 `buildID`
- `test/main.go`：独立的 Windows 测试程序，用于验证基础 API 与命令调用

## 测试工具

`test/main.go` 是一个更偏“基础环境验证”的辅助程序，适合在正式跑主样本之前先确认实验机是否具备必要的命令和 API 条件。

它会验证以下内容：

- `cmd /C whoami` 命令执行
- `VirtualAlloc` 分配与写入
- 注册表查询
- `%TEMP%` 目录文件写入
- `tasklist` 进程枚举
- PowerShell 调用

编译方式：

```bash
GOOS=windows GOARCH=amd64 go build -o bin/test_win64.exe test/main.go
```

## 编译

环境要求：

- Go 1.21+
- macOS、Linux 或 Windows

使用自带脚本构建：

```bash
./build.sh
```

脚本会：

- 生成随机 `buildID`
- 通过 `-X main.buildID=...` 注入到程序中
- 输出 Windows 64 位和 32 位可执行文件

也可以手动编译：

```bash
GOOS=windows GOARCH=amd64 go build -ldflags="-s -w -H windowsgui" -o bin/reverse_shell_win64.exe main.go
GOOS=windows GOARCH=386 go build -ldflags="-s -w -H windowsgui" -o bin/reverse_shell_win32.exe main.go
```

### 指定目标大小构建

如果你想测试上传接口对文件大小的限制，可以直接给 `build.sh` 传一个目标大小参数。脚本会先正常编译，再在 exe 末尾追加填充字节到指定大小。

```bash
# 生成大小约为 5 MB 的 32/64 位样本
./build.sh 5MB

# 也可以直接用字节数
./build.sh 5242880
```

支持的单位包括 `B`、`K/KB`、`M/MB`、`G/GB`。目标大小必须大于等于编译后原始 exe 的实际大小。

### buildID 与哈希变化机制

`build.sh` 会在每次构建时：

1. 生成一段随机字母数字串
2. 追加当前 Unix 时间戳
3. 通过 `-X main.buildID=...` 注入到程序中

这样做的结果是，不同构建产物通常会得到不同的 MD5 / SHA256。它的主要用途是方便测试“同一份源码、多次构建、哈希不同”时，沙箱和检测管线是否仍能基于行为和 IOC 做出判断，而不是依赖固定文件哈希。

## 预期 Cuckoo / EDR 信号

这个样本希望产生的信号包括：

- 可疑 API 加载与 RWX 内存分配
- 注册表修改尝试
- 计划任务创建与删除
- `%TEMP%` 下的可疑文件写入
- PowerShell 和系统工具调用
- 向 C2 端点进行 TCP 回连
- 由恶意字符串触发的静态 IOC 命中

具体命中的签名名称会依赖沙箱或安全产品的规则配置。

## Cuckoo 提交流程建议

如果你想用当前样本验证 Cuckoo 的行为检出，建议按下面的顺序操作：

1. 先在本机通过 `./build.sh` 生成 Windows 可执行文件。
2. 准备一个可回滚的 Windows 实验虚拟机，并确认网络和监听端口配置正确。
3. 根据你的 Cuckoo 部署方式，通过 Web 界面或命令行提交样本。
4. 重点观察进程树、注册表操作、计划任务、`%TEMP%\ReverseShellAdvancedLab` 下的工件、TCP 连接以及 PowerShell / 命令行调用。

如果你的环境提供经典 CLI，可以使用类似下面的方式提交：

```bash
cuckoo submit bin/reverse_shell_win64.exe
```

如果你的环境主要使用 Web 页面，直接上传 `bin/reverse_shell_win64.exe` 即可。

## 使用说明

- 仅在可丢弃的 Windows 实验虚拟机中运行。
- 这个样本追求“高噪声、易观测”，不是“高隐蔽、可实战”。
- 如果你希望进一步降低影响，可以在 `main()` 中注释掉 `persist`、`disableav`、`uacbypass` 或按键探针的自动调用；`encrypt` 仅会在收到对应的 C2 命令后触发。

## 常见问题

### 为什么运行后看不到控制台窗口？

默认构建使用了 `-H windowsgui`。如果你需要本地调试，可以从 `build.sh` 或手动 `go build` 命令中移除这个参数。

### 工件会写到哪里？

写入 `%TEMP%\ReverseShellAdvancedLab`。

### `encrypt` 会影响真实用户文档吗？

不会。它会先在实验目录里生成合成文件，然后只对这些实验文件做加密。

### `creds` 会收集真实密码吗？

不会。它只记录常见浏览器路径是否存在，以及 Wi-Fi 配置列表。

### 连接不到 C2 怎么办？

建议依次检查：

1. `main.go` 中的 `C2Server` 是否指向了正确的监听地址和端口。
2. 监听端是否已经启动，例如 `nc -lvnp 4444`。
3. 虚拟机和宿主机之间的网络是否互通。
4. Windows 防火墙或宿主机防火墙是否拦截了连接。

### 如何禁用默认自动执行的行为？

可以直接编辑 `main.go` 中的 `main()`，按需注释掉以下调用：

- `triggerMaliciousAPIs()`
- `disableSecurity()`
- `establishPersistence()`
- `uacBypass()`
- `go keylogger()`

这样可以逐步调试单个行为模块，而不是一次性把所有探针都跑一遍。

### `test/main.go` 的作用是什么？

它不是主样本的缩小版，而是一个更适合排查实验环境问题的辅助程序。如果主样本没有表现出预期行为，可以先运行 `test/main.go` 产物，确认基础命令、API 和 PowerShell 在目标 Windows 环境中是否正常可用。

### 为什么每次编译后的文件哈希都不一样？

因为构建脚本会注入新的 `buildID`。这属于预期行为，目的是方便测试行为检测链路，而不是为了让每次构建得到完全相同的固定样本。

## 免责声明

本项目仅可用于：

- 安全研究
- 沙箱检测能力验证
- 防御产品评估
- 隔离实验环境中的恶意样本分析培训

禁止将其用于未授权访问、持久化控制、数据窃取或系统破坏。
