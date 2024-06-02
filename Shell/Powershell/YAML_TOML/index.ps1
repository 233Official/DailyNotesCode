# 引入 hava_yaml.ps1 中的函数# 引入 hava_yaml.ps1 中的函数# 引入 hava_yaml.ps1 中的函数
. $PSScriptRoot/have_yaml.ps1

$hava_yaml = HaveYaml
if ($hava_yaml -eq $false) {
    Write-Host "未找到且无法安装 powershell-yaml 模块，请先安装 powershell-yaml 模块"
    exit
}

Import-Module powershell-yaml
# 使用 powershell-yaml 模块解析 YAML 文件
$yaml = ConvertFrom-Yaml -Yaml (Get-Content $PSScriptRoot\config.yaml -Raw)
Write-Host "解析 YAML 文件成功" -ForegroundColor:Green
Write-Host "解析结果如下:"
$yaml

# 读取 $yaml 中的变量 v1
$v1 = $yaml.v1
Write-Host "v1 = $v1" -ForegroundColor:Green

# 读取 $yaml 中的变量 config_v2(Hashtable)
$config_v2 = $yaml.config_v2
Write-Host "config_v2 = $config_v2" -ForegroundColor:Green
# 读取 $config_v2 中的变量 v2
$v2 = $config_v2.v2
Write-Host "v2 = $v2" -ForegroundColor:Green