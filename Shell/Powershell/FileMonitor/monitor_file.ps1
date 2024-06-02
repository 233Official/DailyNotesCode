# 监控指定目录下的所有文件变动，并在控制台输出文件的变动类型和路径。
# 可以根据需要对 $action 变量中的脚本块进行修改，以实现更复杂的操作，比如将变动的文件复制到其他位置、发送通知等

# 监控的目录
$targetDir = "E:\temp\testDir"
# log文件路径为当前目录下的log.txt
$logFile = ".\log.txt"
# 在当前目录下新建一个 monitor_cache 目录用于存放监控的文件
$cacheDir = ".\monitor_cache"
if (!(Test-Path $cacheDir)) {
    New-Item -ItemType Directory -Force -Path $cacheDir
}
Write-Host $cacheDir + "created for cache files"

# 创建监控对象
$watcher = New-Object System.IO.FileSystemWatcher
$watcher.Path = $targetDir
$watcher.IncludeSubdirectories = $true
$watcher.EnableRaisingEvents = $true

# 使用 ASCII 编码，防止中文乱码
$OutputEncoding = [System.Text.Encoding]::ASCII


# 文件新增事件
$createAction = {
    $path = $Event.SourceEventArgs.FullPath
    $changeType = $Event.SourceEventArgs.ChangeType
    # 记录下时间以及变动的文件信息
    $date = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    
    # 如果新增的是个文件而非目录, 则将其复制到 monitor_cache 目录下
    if (!(Test-Path $path -PathType Container)) {
        # 将新增的文件复制到 monitor_cache 目录下, 并前缀当前时间以及 _create
        $newPath = $cacheDir + "\" + $date.Replace(":", "-").Replace(" ", "_") + "_create_" + $path.Replace($targetDir, "").Replace("\", "_")
        Copy-Item $path $newPath
    }

    # 日志信息
    $log = "$date File $path was $changeType"
    # 输出到控制台
    Write-Host $log
    # 将变动的文件信息写入log文件
    Add-Content $logFile $log
}

# 文件修改事件
$changeAction = {
    $path = $Event.SourceEventArgs.FullPath
    $changeType = $Event.SourceEventArgs.ChangeType
    # 记录下时间以及变动的文件信息
    $date = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    
    if (!(Test-Path $path -PathType Container)) {
        # 将修改的文件复制到 monitor_cache 目录下, 并前缀当前时间以及 _change
        $newPath = $cacheDir + "\" + $date.Replace(":", "-").Replace(" ", "_") + "_change_" + $path.Replace($targetDir, "").Replace("\", "_")
        Copy-Item $path $newPath
    }

    # 日志信息
    $log = "$date File $path was $changeType"
    # 输出到控制台
    Write-Host $log
    # 将变动的文件信息写入log文件
    Add-Content $logFile $log
}

# 文件删除事件
$deleteAction = {
    $path = $Event.SourceEventArgs.FullPath
    $changeType = $Event.SourceEventArgs.ChangeType
    # 记录下时间以及变动的文件信息
    $date = Get-Date -Format "yyyy-MM-dd HH:mm:ss"

    # 日志信息
    $log = "$date File $path was $changeType"
    # 输出到控制台
    Write-Host $log
    # 将变动的文件信息写入log文件
    Add-Content $logFile $log
}

# 文件重命名事件
$renameAction = {
    $path = $Event.SourceEventArgs.FullPath
    $changeType = $Event.SourceEventArgs.ChangeType
    # 记录下时间以及变动的文件信息
    $date = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
    
    if (!(Test-Path $path -PathType Container)) {
        # 将重命名的文件复制到 monitor_cache 目录下, 并前缀当前时间以及 _rename
        $newPath = $cacheDir + "\" + $date.Replace(":", "-").Replace(" ", "_") + "_rename_" + $path.Replace($targetDir, "").Replace("\", "_")
        Copy-Item $path $newPath
    }

    # 日志信息
    $log = "$date File $path was $changeType"
    # 输出到控制台
    Write-Host $log
    # 将变动的文件信息写入log文件
    Add-Content $logFile $log
}


# 注册事件
Register-ObjectEvent $watcher "Created" -Action $createAction
Register-ObjectEvent $watcher "Changed" -Action $changeAction
Register-ObjectEvent $watcher "Deleted" -Action $deleteAction
Register-ObjectEvent $watcher "Renamed" -Action $renameAction


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
