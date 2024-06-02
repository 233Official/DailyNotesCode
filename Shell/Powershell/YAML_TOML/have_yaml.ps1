# 查询当前是否有 yaml 模块, 如果没有的话则安装, 安装失败则返回 false, 否则返回 true
function HaveYaml {
    # 检查当前有没有 powershell-yaml 模块, 没有的话安装
    $HaveYaml = Get-Module -Name powershell-yaml -ListAvailable
    if ($HaveYaml) {
        Write-Host "powershell-yaml 检查已通过" -ForegroundColor:Green
        return $true
    }
    else {
        # 安装 yaml 模块
        Install-Module -Name powershell-yaml -Scope CurrentUser -Force -Proxy "http://127.0.0.1:7890"
        # 导入 powershell-yaml 模块
        Import-Module powershell-yaml
        $HaveYaml = Get-Module -Name powershell-yaml -ListAvailable
        if ($HaveYaml) {
            Write-Host "已成功导入 powershell-yaml" -ForegroundColor:Green
            return $true
        }
        else {
            Write-Host "powershell-yaml 安装失败,请手工排查问题 " -ForegroundColor:Red
            return $false
        }
    }
}

# HaveYaml