# 读取系统环境变量
$envVariables = [System.Environment]::GetEnvironmentVariables([System.EnvironmentVariableTarget]::Machine)
    
foreach ($envVariable in $envVariables.GetEnumerator()) {
    # 这里需要判空, 因为 SetEnviromentVariable 函数不支持空值, 会报错并退出
    if (![string]::IsNullOrWhiteSpace($envVariable.Value)) {
        # 将这些环境变量加载到当前 powershell 进程环境变量中
        [System.Environment]::SetEnvironmentVariable($envVariable.Key, $envVariable.Value, [System.EnvironmentVariableTarget]::Process)
    }
}