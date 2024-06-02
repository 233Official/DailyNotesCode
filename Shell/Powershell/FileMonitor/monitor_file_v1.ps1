<#  监控目标目录下的文件变动, 并将变动的文件复制到指定目录下
    如果文件拷贝没有成功, 或是在 Powershell 中运行此脚本显示无法加载文件, 未对文件惊醒数字签名,无法在当前系统上运行该脚本
    请使用 ByPass 模式运行脚本, 具体命令大致如下:
    powershell -ExecutionPolicy Bypass -File xxx\xxx\monitor_file_v1.ps1
    
    变量前最好加上作用域, 尤其是全局变量, 否则有时运行脚本时函数中调用不到外层的变量
#>
# 监控的目录
$Global:targetDir = "C:/Users"
# log文件路径为当前目录下的log.txt
$Global:logFile = (Get-Location).Path + "\log.txt"
# 在当前目录下新建一个 monitor_cache 目录用于存放监控的文件
$Global:cacheDir = (Get-Location).Path + "\monitor_cache"
if (!(Test-Path $cacheDir)) {
    New-Item -ItemType Directory -Force -Path $cacheDir
}
Write-Host $cacheDir "created for cache files"

# 创建监控对象
$watcher = New-Object System.IO.FileSystemWatcher
$watcher.Path = $targetDir
$watcher.IncludeSubdirectories = $true
$watcher.EnableRaisingEvents = $true

# 定义一个通用的事件处理函数，根据不同的事件类型执行不同的操作
$commonAction = {
    # 获取事件参数
    $path = $Event.SourceEventArgs.FullPath
    $changeType = $Event.SourceEventArgs.ChangeType
    # 记录下时间以及变动的文件信息
    $date = Get-Date -Format "yyyy-MM-dd-HH-mm-ss"
    # 日志信息
    $log = "$date File $path was $changeType"
    # 输出到控制台
    Write-Host $log
    # 将变动的文件信息写入log文件
    Add-Content $logFile $log
    
    # 如果变动的是个文件而非目录，且不是删除事件，则将其复制到 monitor_cache 目录下，并前缀当前时间以及事件类型
    if (!(Test-Path $path -PathType Container) -and ($changeType -ne "Deleted")) {
        # 生成新的文件路径，替换掉冒号、空格和反斜杠等特殊字符，避免路径错误或冲突
        $newPath = $cacheDir + "\" + ($date + "_" + $changeType + "_" + $path.Replace($targetDir, "")).Replace(":", "-").Replace(" ", "_").Replace("\", "_")
        Copy-Item $path $newPath
        # Write-Host "Monitor File Path:" $newPath
    }
}

# 注册事件，使用同一个事件处理函数，并传递不同的事件类型参数
$eventTypes = @("Created", "Changed", "Deleted", "Renamed")
foreach ($eventType in $eventTypes) {
    Register-ObjectEvent $watcher $eventType -Action $commonAction
}

# 等待事件发生
try {
    while ($true) {
        Start-Sleep 1
    }
}
finally {
    # 清理事件注册
    $watcher.Dispose()
}
