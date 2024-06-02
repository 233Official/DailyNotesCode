# 根据当前目录下的子目录名称作为包名, 子目录中的 whl 文件作为依赖文件, 安装 wheel 包
# 例如当前目录下有一个 pypykatz 目录, 里面有许多相关的 whl 文件, 则通过如下命令安装
# pip install --no-index --find-links .\pypykatz\ pypykatz
# https://stackoverflow.com/questions/11091623/how-to-install-packages-offline
# https://pip.pypa.io/en/stable/cli/pip_download/
# 

$packages = Get-ChildItem -Directory
foreach ($package in $packages) {
    $package_name = $package.Name
    $package_path = $package.FullName
    Write-Host "Install package: $package_name"
    Write-Host "Package path: $package_path"
    pip install --no-index --find-links $package_path $package_name
}

