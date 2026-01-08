package main

import (
	"fmt"
	"os"
	"os/exec"
	"syscall"
	"unsafe"
)

var (
	kernel32         = syscall.NewLazyDLL("kernel32.dll")
	user32           = syscall.NewLazyDLL("user32.dll")
	procVirtualAlloc = kernel32.NewProc("VirtualAlloc")
	procMessageBoxW  = user32.NewProc("MessageBoxW")
)

func main() {
	fmt.Println("[*] Test started...")

	// 测试1: 基本命令执行
	fmt.Println("[+] Test 1: Command execution")
	out, err := exec.Command("cmd", "/C", "whoami").Output()
	if err != nil {
		fmt.Printf("    FAILED: %v\n", err)
	} else {
		fmt.Printf("    SUCCESS: %s\n", string(out))
	}

	// 测试2: Windows API 调用 - VirtualAlloc
	fmt.Println("[+] Test 2: VirtualAlloc API")
	addr, _, err := procVirtualAlloc.Call(
		0,
		uintptr(4096),
		0x3000, // MEM_COMMIT | MEM_RESERVE
		0x40,   // PAGE_EXECUTE_READWRITE
	)
	if addr == 0 {
		fmt.Printf("    FAILED: %v\n", err)
	} else {
		fmt.Printf("    SUCCESS: Allocated at 0x%x\n", addr)
		// 写入测试数据
		*(*byte)(unsafe.Pointer(addr)) = 0x90
		fmt.Println("    SUCCESS: Memory write OK")
	}

	// 测试3: 注册表查询
	fmt.Println("[+] Test 3: Registry query")
	out, err = exec.Command("reg", "query", "HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run").Output()
	if err != nil {
		fmt.Printf("    FAILED: %v\n", err)
	} else {
		fmt.Printf("    SUCCESS:\n%s\n", string(out))
	}

	// 测试4: 文件写入
	fmt.Println("[+] Test 4: File write")
	tempFile := os.Getenv("TEMP") + "\\test_malware.txt"
	err = os.WriteFile(tempFile, []byte("test"), 0644)
	if err != nil {
		fmt.Printf("    FAILED: %v\n", err)
	} else {
		fmt.Printf("    SUCCESS: Written to %s\n", tempFile)
		os.Remove(tempFile)
	}

	// 测试5: 进程列表
	fmt.Println("[+] Test 5: Process list")
	out, err = exec.Command("tasklist").Output()
	if err != nil {
		fmt.Printf("    FAILED: %v\n", err)
	} else {
		fmt.Println("    SUCCESS: tasklist works")
	}

	// 测试6: PowerShell
	fmt.Println("[+] Test 6: PowerShell")
	out, err = exec.Command("powershell", "-Command", "Write-Output 'Hello'").Output()
	if err != nil {
		fmt.Printf("    FAILED: %v\n", err)
	} else {
		fmt.Printf("    SUCCESS: %s\n", string(out))
	}

	fmt.Println("\n[*] All tests completed!")
	fmt.Println("[*] Press Enter to exit...")
	fmt.Scanln()
}
