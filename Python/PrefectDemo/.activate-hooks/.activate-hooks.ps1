# 设置当前工作目录的 .prefect 目录为 PREFECT_HOME
$env:PREFECT_HOME = Join-Path -Path $PWD -ChildPath ".prefect"

# 确保目录存在
if (-not (Test-Path -Path $env:PREFECT_HOME)) {
    New-Item -ItemType Directory -Path $env:PREFECT_HOME | Out-Null
}

Write-Host "已设置 PREFECT_HOME=$env:PREFECT_HOME"

# 检查激活脚本是否存在
$activateFile = Join-Path -Path ".venv" -ChildPath "Scripts" | Join-Path -ChildPath "Activate.ps1"

if (Test-Path -Path $activateFile) {
    $content = Get-Content -Path $activateFile -Raw
    if (-not ($content -like "*# 自动设置 PREFECT_HOME*")) {
        Write-Host "修改 $activateFile 以在激活环境时自动设置 PREFECT_HOME..."
        
        $appendText = @"

# 自动设置 PREFECT_HOME
`$env:PREFECT_HOME = Join-Path -Path (Split-Path -Parent `$env:VIRTUAL_ENV) -ChildPath ".prefect"
if (-not (Test-Path -Path `$env:PREFECT_HOME)) {
    New-Item -ItemType Directory -Path `$env:PREFECT_HOME | Out-Null
}
Write-Host "已自动设置 PREFECT_HOME=`$env:PREFECT_HOME"
"@
        
        Add-Content -Path $activateFile -Value $appendText
        
        Write-Host "配置已添加到激活脚本中。今后每次激活环境时将自动设置 PREFECT_HOME。"
    } else {
        Write-Host "激活脚本已包含 PREFECT_HOME 设置，无需修改。"
    }
} else {
    Write-Host "警告：找不到虚拟环境激活脚本 ($activateFile)。请确保已创建 Poetry 虚拟环境。"
}